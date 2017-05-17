### Android Handler消息机制原理概述:

Android的消息机制主要是指Handler运行机制,Handler运行机制需要底层的MessageQueue和Looper的支持,MessageQueue内部采用单链表的数据结构,提供队列的形式对外提供插入和删除的功能.MessageQueue只是一个存储单元,不能去处理消息,那么Looper便实现了这个功能.Looper会以无限循环的形式去查找是否有新消息,有就去处理,没有就一直阻塞等待.另外一点值得注意,Looper会存储在每个线程的ThreadLocal中,实现线程之间互不干扰的存储并提供数据.

经过对Android消息机制的简单概述,知道Handler消息需要:

* Handler
* MessageQueue
* Looper
* Message

简单的流程图如下(图片来自网络):

![](https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1495004303177&di=7bff27d4beb0cd1b3f23704097b1bedf&imgtype=jpg&src=http%3A%2F%2Fimg4.imgtn.bdimg.com%2Fit%2Fu%3D3731823991%2C444840863%26fm%3D214%26gp%3D0.jpg)


### 实现自己的Handler异步消息机制:

* Handler: 

	Handler对象提供两个方法一个是sendMessage();一个是handMessage();当然我们还需要dispatchMessage()也就是供Looper调度消息.

```java

	public class Handler {
	
	    private MessageQueue mMessageQueue;
	    private Looper mLooper;
	
	    public  Handler() {
	        mLooper = Looper.myLooper();
	        mMessageQueue = mLooper.mQueue;
	    }
	
	    public void sendMessage(Message msg) {
	        msg.target= this;
	        mMessageQueue.enqueueMessage(msg);
	    }
	
	
	
	    public void handleMessage(Message msg) {
	
	    }
	
	    public void dispatchMessage(Message msg) {
	        handleMessage(msg);
	    }
	}


```

* Message: 

	Message对象很简单, 提供一个int类型的消息编号,一个Object类型的消息内容obj,还有就一个与Handler绑定的target.

	```java

		public class Message {

		    public int what;
		    public Object obj;
		    Handler target;
		
		    @Override
		    public String toString() {
		        return obj.toString();
		    }
		}


	```

* Looper: 

	Looper轮询器,主要作用: 处理消息; 从MessageQueue中拿到消息并调度给Handler.值得注意的是Looper对象存储于ThreadLocal中,所以提供一个prepare()来初始化当前线程的Looper对象,另外提供一个loop()来轮询消息.

```java

	public class Looper {
	
	    public MessageQueue mQueue;
	    private static ThreadLocal<Looper> sThreadLocal = new ThreadLocal<>();
	
	    private Looper() {
	        mQueue = new MessageQueue();
	    }
	
	    public static void prepare() {
	        if (sThreadLocal.get() != null) {
	            throw new RuntimeException("Only one Looper may be created per thread");
	        }
	        sThreadLocal.set(new Looper());
	    }
	
	    public static Looper myLooper() {
	
	        return sThreadLocal.get();
	    }
	
	    public static void loop() {
	        Looper looper = myLooper();
	
	        if (looper==null){
	            throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
	        }
	
	        MessageQueue queue = looper.mQueue;
	        for (; ; ) {
	            Message msg = queue.next();
	            if (msg==null){
	                continue;
	            }
	            msg.target.dispatchMessage(msg);
	        }
	    }
	}

```

* MessageQueue: 

	MessageQueue作为消息的存储单元,必定提供对消息的插入和删除, 就是enqueueMessage()和next(); 但是要注意的是在多线程中消息的插入和删除就是一个生产者和消费者模型,这里采用ArrayBlockingQueue阻塞队列的实现方式(可重入锁Lock和Condition)来实现:

```java

	public class MessageQueue {
	
	    Message[] msgs; //采用的是ArrayBlockingQueue的实现方式.
	
	    int putIndex;
	    int takeIndex;
	    int count;
	    private Lock mLock;
	    private Condition notEmpty;
	    private Condition notFull;
	
	    public MessageQueue() {
	        msgs = new Message[50];
	        mLock = new ReentrantLock();
	        notEmpty = mLock.newCondition();
	        notFull = mLock.newCondition();
	    }
	
	    public void enqueueMessage(Message msg) {
	
	        try {
	            mLock.lock();
	            if (count == msgs.length) {
	                try {
	                    notFull.await();
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	            }
	            msgs[putIndex] = msg;
	            putIndex = (++putIndex == msgs.length) ? 0 : putIndex;
	            count++;
	            notEmpty.signal();
	        } finally {
	            mLock.unlock();
	        }
	
	    }
	
	    public Message next() {
	
	        try {
	            mLock.lock();
	            if (msgs.length == 0) {
	                try {
	                    notEmpty.await();
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	            }
	            Message msg = msgs[takeIndex];
	            msgs[takeIndex] = null;
	            takeIndex = (++takeIndex == msgs.length) ? 0 : takeIndex;
	            count--;
	            notFull.signal();
	            return msg;
	        } finally {
	            mLock.unlock();
	
	        }
	    }
	}

```

### 测试自己的Handler异步消息机制:

```java

	public class HandlerTest {
	
	    public static void main(String... args) {
	
	        for (int i = 0; i < 10; i++) {
	            new Thread(new Runnable() {
	                @Override
	                public void run() {
	                    Looper.prepare();
	
	                    final Handler handler = new Handler() {
	                        @Override
	                        public void handleMessage(Message msg) {
	                            super.handleMessage(msg);
	                            String message = (String) msg.obj;
	                            System.out.println("receive: " + Thread.currentThread().getName() + message);
	                        }
	                    };
	
	                    Message msg = new Message();
	                    msg.what = 1;
	                    msg.obj = Thread.currentThread().getName() + "--------" + System.currentTimeMillis();
	                    System.out.println("send: " + msg.obj);
	                    handler.sendMessage(msg);
	                    Looper.loop();
	                }
	            }).start();
	        }
	
	    }
	}

```

执行结果如下:

```java

	send: Thread-1--------1494995896691
	receive: Thread-1Thread-1--------1494995896691
	send: Thread-3--------1494995896708
	receive: Thread-3Thread-3--------1494995896708
	send: Thread-0--------1494995896708
	receive: Thread-0Thread-0--------1494995896708
	send: Thread-4--------1494995897043
	receive: Thread-4Thread-4--------1494995897043
	send: Thread-5--------1494995897080
	receive: Thread-5Thread-5--------1494995897080
	send: Thread-9--------1494995897118
	receive: Thread-9Thread-9--------1494995897118
	send: Thread-2--------1494995897154
	receive: Thread-2Thread-2--------1494995897154
	send: Thread-6--------1494995897186
	receive: Thread-6Thread-6--------1494995897186
	send: Thread-7--------1494995897188
	receive: Thread-7Thread-7--------1494995897188
	send: Thread-8--------1494995897234
	receive: Thread-8Thread-8--------1494995897234
	
	Process finished with exit code -1

```
