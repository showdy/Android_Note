### HandlerThread源码分析

```java

	/**
	 * Handy class for starting a new thread that has a looper. The looper can then be 
	 * used to create handler classes. Note that start() must still be called.
	 * 带有Looper的线程,可以用来创建Handler,而且创建HanderThread后必须调用start().
	 */
	public class HandlerThread extends Thread {
	    int mPriority;
	    int mTid = -1;
	    Looper mLooper;
	
	    public HandlerThread(String name) {
	        super(name);
	        mPriority = Process.THREAD_PRIORITY_DEFAULT;
	    }
	    
	    /**
	     * Constructs a HandlerThread.
	     * @param name
	     * @param priority The priority to run the thread at. The value supplied must be from 
	     * {@link android.os.Process} and not from java.lang.Thread.
	     */
	    public HandlerThread(String name, int priority) {
	        super(name);
	        mPriority = priority;
	    }
	    
	    /**
	     * Call back method that can be explicitly overridden if needed to execute some
	     * setup before Looper loops.
	     */
	    protected void onLooperPrepared() {
	    }
	
	    @Override
	    public void run() {
	        mTid = Process.myTid();
	        Looper.prepare();
	        synchronized (this) {
	            mLooper = Looper.myLooper();
				//与wait()呼应,通知getLooper(),mLooper已经创建成功!
	            notifyAll();
	        }
	        Process.setThreadPriority(mPriority);
	        onLooperPrepared();
	        Looper.loop();
	        mTid = -1;
	    }
	    
	    /**
	     * This method returns the Looper associated with this thread. If this thread not been started
	     * or for any reason is isAlive() returns false, this method will return null. If this thread 
	     * has been started, this method will block until the looper has been initialized.  
	     * @return The looper.
	     */
	    public Looper getLooper() {
	        if (!isAlive()) {
	            return null;
	        }
	        
	        // If the thread has been started, wait until the looper has been created.
	        synchronized (this) {
	            while (isAlive() && mLooper == null) {
	                try {
						//由于Handler是在UI线程中创建,而Looper是在子线程中创建,所以必须要等待mLooper初始化完成,才能正确返回mLooper.
	                    wait();
	                } catch (InterruptedException e) {
	                }
	            }
	        }
	        return mLooper;
	    }
	
	    /**
	     * Quits the handler thread's looper.
	     * <p>
	     * Causes the handler thread's looper to terminate without processing any
	     * more messages in the message queue.
	     * </p><p>
	     * Any attempt to post messages to the queue after the looper is asked to quit will fail.
	     * For example, the {@link Handler#sendMessage(Message)} method will return false.
	     * </p><p class="note">
	     * Using this method may be unsafe because some messages may not be delivered
	     * before the looper terminates.  Consider using {@link #quitSafely} instead to ensure
	     * that all pending work is completed in an orderly manner.
	     * </p>
	     *
	     * @return True if the looper looper has been asked to quit or false if the
	     * thread had not yet started running.
	     *
	     * @see #quitSafely
	     */
	    public boolean quit() {
	        Looper looper = getLooper();
	        if (looper != null) {
	            looper.quit();
	            return true;
	        }
	        return false;
	    }
	
	    /**
	     * Quits the handler thread's looper safely.
	     * <p>
	     * Causes the handler thread's looper to terminate as soon as all remaining messages
	     * in the message queue that are already due to be delivered have been handled.
	     * Pending delayed messages with due times in the future will not be delivered.
	     * </p><p>
	     * Any attempt to post messages to the queue after the looper is asked to quit will fail.
	     * For example, the {@link Handler#sendMessage(Message)} method will return false.
	     * </p><p>
	     * If the thread has not been started or has finished (that is if
	     * {@link #getLooper} returns null), then false is returned.
	     * Otherwise the looper is asked to quit and true is returned.
	     * </p>
	     *
	     * @return True if the looper looper has been asked to quit or false if the
	     * thread had not yet started running.
	     */
	    public boolean quitSafely() {
	        Looper looper = getLooper();
	        if (looper != null) {
	            looper.quitSafely();
	            return true;
	        }
	        return false;
	    }
	
	    /**
	     * Returns the identifier of this thread. See Process.myTid().
	     */
	    public int getThreadId() {
	        return mTid;
	    }
	}
```

HandlerThread就是一个带有Looper的循环线程,解决了多次创建和销毁线程消耗资源的问题.HandlerThread的源码很简单,但是有几点需要注意:

* 使用notfiyAll()和wait()原因是因为: Handler创建是需要HanderThread线程中的Looper的,而Looper的创建在run()中,而我们知道当调用thread.start()后并不能保证run()立即执行了(异步调用),所以必须要等待mLooper的创建成功后,才能创建Handler,而notifyAll()与wait()正是保证了这点.
* quit()与quitSafely():一种是线程不安全,一种是线程安全的.


HandlerThread的用法在IntentService中得到体现:

```java

/**
 * IntentService is a base class for {@link Service}s that handle asynchronous
 * requests (expressed as {@link Intent}s) on demand.  Clients send requests
 * through {@link android.content.Context#startService(Intent)} calls; the
 * service is started as needed, handles each Intent in turn using a worker
 * thread, and stops itself when it runs out of work.
 *
 * <p>This "work queue processor" pattern is commonly used to offload tasks
 * from an application's main thread.  The IntentService class exists to
 * simplify this pattern and take care of the mechanics.  To use it, extend
 * IntentService and implement {@link #onHandleIntent(Intent)}.  IntentService
 * will receive the Intents, launch a worker thread, and stop the service as
 * appropriate.
 *
 * <p>All requests are handled on a single worker thread -- they may take as
 * long as necessary (and will not block the application's main loop), but
 * only one request will be processed at a time.
 *
 * <div class="special reference">
 * <h3>Developer Guides</h3>
 * <p>For a detailed discussion about how to create services, read the
 * <a href="{@docRoot}guide/topics/fundamentals/services.html">Services</a> developer guide.</p>
 * </div>
 *
 * @see android.os.AsyncTask
 */
public abstract class IntentService extends Service {
    private volatile Looper mServiceLooper;
    private volatile ServiceHandler mServiceHandler;
    private String mName;
    private boolean mRedelivery;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            onHandleIntent((Intent)msg.obj);
            stopSelf(msg.arg1);
        }
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public IntentService(String name) {
        super();
        mName = name;
    }

    /**
     * Sets intent redelivery preferences.  Usually called from the constructor
     * with your preferred semantics.
     *
     * <p>If enabled is true,
     * {@link #onStartCommand(Intent, int, int)} will return
     * {@link Service#START_REDELIVER_INTENT}, so if this process dies before
     * {@link #onHandleIntent(Intent)} returns, the process will be restarted
     * and the intent redelivered.  If multiple Intents have been sent, only
     * the most recent one is guaranteed to be redelivered.
     *
     * <p>If enabled is false (the default),
     * {@link #onStartCommand(Intent, int, int)} will return
     * {@link Service#START_NOT_STICKY}, and if the process dies, the Intent
     * dies along with it.
     */
    public void setIntentRedelivery(boolean enabled) {
        mRedelivery = enabled;
    }

    @Override
    public void onCreate() {
        // TODO: It would be nice to have an option to hold a partial wakelock
        // during processing, and to have a static startService(Context, Intent)
        // method that would launch the service & hand off a wakelock.

        super.onCreate();
        HandlerThread thread = new HandlerThread("IntentService[" + mName + "]");
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);
    }

    /**
     * You should not override this method for your IntentService. Instead,
     * override {@link #onHandleIntent}, which the system calls when the IntentService
     * receives a start request.
     * @see android.app.Service#onStartCommand
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onStart(intent, startId);
        return mRedelivery ? START_REDELIVER_INTENT : START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mServiceLooper.quit();
    }

    /**
     * Unless you provide binding for your service, you don't need to implement this
     * method, because the default implementation returns null. 
     * @see android.app.Service#onBind
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * This method is invoked on the worker thread with a request to process.
     * Only one Intent is processed at a time, but the processing happens on a
     * worker thread that runs independently from other application logic.
     * So, if this code takes a long time, it will hold up other requests to
     * the same IntentService, but it will not hold up anything else.
     * When all requests have been handled, the IntentService stops itself,
     * so you should not call {@link #stopSelf}.
     *
     * @param intent The value passed to {@link
     *               android.content.Context#startService(Intent)}.
     */
    @WorkerThread
    protected abstract void onHandleIntent(Intent intent);
}

```

IntentService是内部封装了HandlerThread和Handler,可以进行耗时操作的Service.

```java

	 @Override
	    public void onCreate() {
	        // TODO: It would be nice to have an option to hold a partial wakelock
	        // during processing, and to have a static startService(Context, Intent)
	        // method that would launch the service & hand off a wakelock.
	
	        super.onCreate();
	        HandlerThread thread = new HandlerThread("IntentService[" + mName + "]");
	        thread.start();
	
	        mServiceLooper = thread.getLooper();
	        mServiceHandler = new ServiceHandler(mServiceLooper);
	    }
```
当IntentService第一次启动时,onCreate调用,创建HandlerThread,然后使用其Looper构建一个Handler对象mServiecHandler,这样就可以使用mServiceHanlder发送消息,并最终在HandlerThread中被处理,这样IntentService中就可以耗时操作的后台任务,在onStartCommand方法中处理每个后台任务的Intent.onStartCommand()调用了onStart(),onStart()方法实现如下:

```java

 	@Override
    public void onStart(Intent intent, int startId) {
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);
    }
```

可以看出IntentService仅仅通过mServiceHandler发送了一个消息,并在HandlerThread中被处理,mServiceHandler收到消息后,将Intent对象传递给onHandlerIntent(),而这里的Intent对象与startService(intent)中对象一致,这样就可以处理具体不同的后台任务了. **onHandlerIntent()处理任务结束后,会调用stopSelf(int startId)来尝试终止服务,这里不选择stopSelf()方法主要是考虑到:消息队列可能还有消息未能处理,stopSelf(startId)会等待所有消息都处理完成后再终止服务.** ServiceHandler的源码如下:

```java

 	private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            onHandleIntent((Intent)msg.obj);
            stopSelf(msg.arg1);
        }
    }

```

由于每执行一个任务就必须要启动一次IntentService,而IntentService内部通过HandlerThread执行任务,Handler中的Looper是顺序处理消息的,这意味着IntentService也是顺序请求后台任务的.当有多个后台任务同时存在时,这些任务会按照外界发送的顺序被执行.
