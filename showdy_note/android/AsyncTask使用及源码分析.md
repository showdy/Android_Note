## AsyncTask

### AsyncTask使用注意事项:
* `AsyncTask`只能被执行(`execute`方法)一次,多次执行将会引发异常.
* 任务的取消只能打了一个标记,并不是真正取消,需要手动去掉用;

### 构建AsyncTask抽象类的三个泛型参数;
* `AsyncTask<Params,Progress,Result>`是一个抽象类,通常用于被继承.继承AsyncTask需要指定如下三个泛型参数:
	* `Params`:启动任务时输入的参数类型.
	* `Progress`:后台任务执行中返回进度值的类型.
	* `Result`:后台任务执行完成后返回结果的类型.
### AsyncTask四个方法:
* `onPreExecute()`;
 	> 执行任务之前,一般做变量的初始化,或者ui的隐藏或者显示;该方法无参数
* `doInBackground(T...params)`;//对应泛型参数params
	> 后台执行任务,属于子线程,当调用publishProgress()方法时,会触发系统自动调用onProgressUpdate();
* `onProgressUpdate(T... values)`;//对应泛型参数progress
	> 用于更新进度
* `onPostExecute(T...result)`;//对应泛型参数result
	> 任务结束后调用,一般处理返回的结果,或者改变ui显示.
### AsyncTask历史版本问题:

在Android1.6之前,AsyncTask是串行执行任务,Android1.6时候AsyncTask开始采用线程池处理并行任务,但是从Android3.0开始,为了避免AsyncTask带来的并发错误,又采用线程池串行执行任务,尽管此处,Android3.0后,AsyncTask还是支持并发执行任务,不过需要调用`executeOnExecutor()`方法.

### 加载网络图片的实例:

```java
    public class ImageActivity extends Activity {
	    private ImageView imageView ;
	    private ProgressBar progressBar ;
	    private static String URL = "http://pic3.zhongsou.com/image/38063b6d7defc892894.jpg";
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.image);
	        imageView = (ImageView) findViewById(R.id.image);
	        progressBar = (ProgressBar) findViewById(R.id.progressBar);
	        //通过调用execute方法开始处理异步任务.相当于线程中的start方法.
	        new MyAsyncTask().execute(URL);
	    }
	
	    class MyAsyncTask extends AsyncTask<String,Void,Bitmap> {
	
	        //onPreExecute用于异步处理前的操作
	        @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	            //此处将progressBar设置为可见.
	            progressBar.setVisibility(View.VISIBLE);
	        }
	
	        //在doInBackground方法中进行异步任务的处理.
	        @Override
	        protected Bitmap doInBackground(String... params) {
	            //获取传进来的参数
	            String url = params[0];
	            Bitmap bitmap = null;
	            URLConnection connection ;
	            InputStream is ;
	            try {
	                connection = new URL(url).openConnection();
	                is = connection.getInputStream();
	                //为了更清楚的看到加载图片的等待操作,将线程休眠3秒钟.
	                Thread.sleep(3000);
	                BufferedInputStream bis = new BufferedInputStream(is);
	                //通过decodeStream方法解析输入流
	                bitmap = BitmapFactory.decodeStream(bis);
	                is.close();
	                bis.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	            return bitmap;
	        }
	
	        //onPostExecute用于UI的更新.此方法的参数为doInBackground方法返回的值.
	        @Override
	        protected void onPostExecute(Bitmap bitmap) {
	            super.onPostExecute(bitmap);
	            //隐藏progressBar
	            progressBar.setVisibility(View.GONE);
	            //更新imageView
	            imageView.setImageBitmap(bitmap);
	        }
	   	}
	}
```

  
    
### 模拟加载进度条:
    
```java
    
	public class ProgressActivity extends Activity{
	    private ProgressBar progressBar;
	    private MyAsyncTask myAsyncTask;
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.progress);
	        progressBar = (ProgressBar) findViewById(R.id.progress);
	        myAsyncTask = new MyAsyncTask();
	        //启动异步任务的处理
	        myAsyncTask.execute();
	    }
	
	    //AsyncTask是基于线程池进行实现的,当一个线程没有结束时,后面的线程是不能执行的.
	    @Override
	    protected void onPause() {
	        super.onPause();
	        if (myAsyncTask != null && myAsyncTask.getStatus() == Status.RUNNING) {
	            //cancel方法只是将对应的AsyncTask标记为cancelt状态,并不是真正的取消线程的执行.
	            myAsyncTask.cancel(true);
	        }
	    }
	
	    class MyAsyncTask extends AsyncTask<Void,Integer,Void>{
	        @Override
	        protected void onProgressUpdate(Integer... values) {
	            super.onProgressUpdate(values);
	            //通过publishProgress方法传过来的值进行进度条的更新.
	            progressBar.setProgress(values[0]);
	        }
	
	        @Override
	        protected Void doInBackground(Void... params) {
	            //使用for循环来模拟进度条的进度.
	            for (int i = 0;i < 100; i ++){
	                //如果task是cancel状态,则终止for循环,以进行下个task的执行.
	                if (isCancelled()){
	                    break;
	                }
	                //调用publishProgress方法将自动触发onProgressUpdate方法来进行进度条的更新.
	                publishProgress(i);
	                try {
	                    //通过线程休眠模拟耗时操作
	                    Thread.sleep(300);
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	            }
	            return null;
	        }
	    }
	}
```
    
------------------------------------------------------------------------

### AsyncTask的源码解析:

AsyncTask的使用分两步:

```java

	myAsyncTask = new MyAsyncTask();
	        //启动异步任务的处理
	myAsyncTask.execute();

```

从执行起步任务的起始点开始, 进入execute()方法:

```java

	public final AsyncTask<Params, Progress, Result> execute(Params... params) {
        return executeOnExecutor(sDefaultExecutor, params);
    }

    @MainThread
    public final AsyncTask<Params, Progress, Result> executeOnExecutor(Executor exec,
            Params... params) {
        if (mStatus != Status.PENDING) {
            switch (mStatus) {
                case RUNNING:
                    throw new IllegalStateException("Cannot execute task:"
                            + " the task is already running.");
                case FINISHED:
                    throw new IllegalStateException("Cannot execute task:"
                            + " the task has already been executed "
                            + "(a task can be executed only once)");
            }
        }

        mStatus = Status.RUNNING; //状态改变执行中
        onPreExecute(); //执行前准备工作
        mWorker.mParams = params;//赋值
        exec.execute(mFuture); //执行异步任务
        return this;
    }

```
一个异步进入,先判断该任务是否在执行,或者执行完毕,如果是,则抛出异常,说明一个任务只能被执行一次.否则,将任务状态改变为RUNNING,并将传进来的params传给mWorker, 那么mWorker是什么?,继续看源码:


```java

	public AsyncTask() {
        mWorker = new WorkerRunnable<Params, Result>() {
            public Result call() throws Exception {
                mTaskInvoked.set(true);
                Result result = null;
                try {
                    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    //noinspection unchecked
                    result = doInBackground(mParams);
                    Binder.flushPendingCommands();
                } catch (Throwable tr) {
                    mCancelled.set(true);
                    throw tr;
                } finally {
                    postResult(result);
                }
                return result;
            }
        };

        mFuture = new FutureTask<Result>(mWorker) {
            @Override
            protected void done() {
                try {
                    postResultIfNotInvoked(get());
                } catch (InterruptedException e) {
                    android.util.Log.w(LOG_TAG, e);
                } catch (ExecutionException e) {
                    throw new RuntimeException("An error occurred while executing doInBackground()",
                            e.getCause());
                } catch (CancellationException e) {
                    postResultIfNotInvoked(null);
                }
            }
        };
    }

	private static abstract class WorkerRunnable<Params, Result> implements Callable<Result> {
        Params[] mParams;
    }
```
可以看到mWorker在构造方法中完成了初始化工作,因为是个抽象类,就new了一个具体子类,实现call方法,并将原子类变量mTaskInvoked=true,最后调用doInbackGround(mParams),并将返回的result作为参数给postResult(),在postResult()方法中:

```java

	private Result postResult(Result result) {
        @SuppressWarnings("unchecked")
        Message message = getHandler().obtainMessage(MESSAGE_POST_RESULT,
                new AsyncTaskResult<Result>(this, result));
        message.sendToTarget();
        return result;
    }

```
在postReuslt()中可以看到,采用异步消息机制,发送一个message.what为MESSAGE_POST_RESULT的消息,将异步执行的结果切换到主线,完成线程的切换工作.

```java

	private static class InternalHandler extends Handler {
        public InternalHandler() {
            super(Looper.getMainLooper());
        }

        @SuppressWarnings({"unchecked", "RawUseOfParameterizedType"})
        @Override
        public void handleMessage(Message msg) {
            AsyncTaskResult<?> result = (AsyncTaskResult<?>) msg.obj;
            switch (msg.what) {
                case MESSAGE_POST_RESULT:
                    // There is only one result
                    result.mTask.finish(result.mData[0]);
                    break;
                case MESSAGE_POST_PROGRESS:
                    result.mTask.onProgressUpdate(result.mData);
                    break;
            }
        }
    }

```

可以看到Handler使用的是Looper.getMainLooper,说明Handler接收消息发生主线程,收到MESSAGE_POST_RESULT执行finish():

```java

    private void finish(Result result) {
        if (isCancelled()) {
            onCancelled(result);
        } else {
            onPostExecute(result);
        }
        mStatus = Status.FINISHED;
    }

```

可以看到在finish()对异步任务是否取消做了判断,如果异步任务已经取消,则调用onCancel()方法,否则调用onPostResult(),这里也可以看到当异步任务被取消后, onPostExecute()是不会执行.最后将状态置为FINISHED.构造函数仅仅只是完成了mWorker的初始化工作,并采用FutureTask将mWorker包装了下,并未真正执行,当然在任务执行结束后,会调用postResultNotInvoked(get()),来查看任务是否已经执行:

```java

    private void postResultIfNotInvoked(Result result) {
        final boolean wasTaskInvoked = mTaskInvoked.get();
        if (!wasTaskInvoked) {
            postResult(result);
        }
    }

```
如果mTaskInvoked不为true,则postResult(),但是mWorker初始化的时,就已经将mTaskResult置为true,所以这个方法不会调用.

下面看看  `exec.execute(mFuture); `到底做了什么:

```java

	public final AsyncTask<Params, Progress, Result> execute(Params... params) {
        return executeOnExecutor(sDefaultExecutor, params);
    }

```

知道exec实际上是`sDefaultExecutor`:

```java

	public static final Executor SERIAL_EXECUTOR = new SerialExecutor();

    private static volatile Executor sDefaultExecutor = SERIAL_EXECUTOR;
  
    private static class SerialExecutor implements Executor {
        final ArrayDeque<Runnable> mTasks = new ArrayDeque<Runnable>();
        Runnable mActive;

        public synchronized void execute(final Runnable r) {
            mTasks.offer(new Runnable() {
                public void run() {
                    try {
                        r.run();
                    } finally {
                        scheduleNext();
                    }
                }
            });
            if (mActive == null) {
                scheduleNext();
            }
        }

        protected synchronized void scheduleNext() {
            if ((mActive = mTasks.poll()) != null) {
                THREAD_POOL_EXECUTOR.execute(mActive);
            }
        }
    }

```

sDefaultExecutor实际上SerialExecutor的一个实例,其内部维护一个双端队列(数组实现),执行execute(),会调用offer()将任务放入队列尾部, 然后判断mActive否为空,如果为null,则调用scheduleNext()从队列尾部取出一个任务,调用THREAD_POOL_EXECUTOR真正执行任务:

```java

	private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE_SECONDS = 30;

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
        }
    };

    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(128);

    public static final Executor THREAD_POOL_EXECUTOR; //可以并行执行的线程池

    static {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
                sPoolWorkQueue, sThreadFactory);
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        THREAD_POOL_EXECUTOR = threadPoolExecutor;
    }

```

线程池最大支持`CUP_COUNT*2+1`的线程并发,加上长度为128的阻塞队列,如果任务的数量为`CUP_COUNT*2+1+128`是否会因为任务数过多而抛出异常,实际上不可能,再来先看下SerialExecutor这个类:

```java
	private static class SerialExecutor implements Executor {
        final ArrayDeque<Runnable> mTasks = new ArrayDeque<Runnable>();
        Runnable mActive;

        public synchronized void execute(final Runnable r) {
            mTasks.offer(new Runnable() {
                public void run() {
                    try {
                        r.run();
                    } finally {
                        scheduleNext();
                    }
                }
            });
            if (mActive == null) {
                scheduleNext();
            }
        }

        protected synchronized void scheduleNext() {
            if ((mActive = mTasks.poll()) != null) {
                THREAD_POOL_EXECUTOR.execute(mActive);
            }
        }
    }


```
如果此时有10个任务同时调用execute()方法,第个任务加入队列,mActive=null,从队列尾部取出一个任务,然后交给线程池去执行,然后第二任务入队,但是此时mActive不为null,不会执行scheduleNext()方法,只能等待第一个任务执行完毕,再调用scheduleNext().内部虽然是个线程池,但是确实个串行的线程池.

那么AysncTask是否就不能并发执行任务呢? 其实AsyncTask是可以并发执行任务的,不过要调用

```java

	public final AsyncTask<Params, Progress, Result> executeOnExecutor(Executor exec, Params... params) {
		....
	}

```
然后自己传入一个可以并发执行任务的线程池,比如AsyncTask提供的THREAD_POOL_EXECUTOR.在AsyncTaskCompat中:

```java
	
	public static <Params, Progress, Result> AsyncTask<Params, Progress, Result> executeParallel(
            AsyncTask<Params, Progress, Result> task,
            Params... params) {
        if (task == null) {
            throw new IllegalArgumentException("task can not be null");
        }

        if (Build.VERSION.SDK_INT >= 11) {
            // From API 11 onwards, we need to manually select the THREAD_POOL_EXECUTOR
            AsyncTaskCompatHoneycomb.executeParallel(task, params);
        } else {
            // Before API 11, all tasks were run in parallel
            task.execute(params);
        }

        return task;
    }

	class AsyncTaskCompatHoneycomb {

	    static <Params, Progress, Result> void executeParallel(
	            AsyncTask<Params, Progress, Result> task,
	            Params... params) {
	        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
	    }

	}

```

可以看出在API 11之前调用`execute()`就表示并发执行任务,在API需要调用`executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params)`来执行并发任务. 
