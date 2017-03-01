### 同步工具类
* 同步工具类可以是任何一个对象,只要他根据自身的状态来协调线程的控制流.阻塞队列可以作为同步工具类,其他类型的同步工具类有: 信号量(semaphore),栅栏(Barrier),闭锁(Latch).
* 所有的同步工具类都有一些特定的结构化属性,封装了一些状态,这些状态决定执行同步工具类的线程是继续还是等待.


#### 闭锁(CountDownLatch)
#### CountDownLatch 方法解析
CountDownLatch可以使一个或者多个线程等待一组事件的发生.闭锁状态包括一个计数器,该计数器初始化为一个正数,表示需要等待的事件数量.countDown()表示递减计数器,表示有个一个事件发生,而await()等待计数器达到零,这表示所有等待的事件都已经发生.如果计数器值非0,那么await()则会阻塞直到计数器为0,或者线程中断,或者等待超时.


* `public CountDownLatch(int count)`
	 
	用一个给定的数值初始化CountDownLatch，之后计数器就从这个值开始倒计数，直到计数值达到零。

* `public void countDown()`

	这个函数用来将CountDownLatch的计数值减一

* `public void await() throws InterruptedException`
  `public boolean await(long timeout, TimeUnit unit) throws InterruptedException`

	这两个函数的作用都是让线程阻塞等待其他线程，直到CountDownLatch的计数值变为0才继续执行之后的操作。区别在于第一个函数没有等待时间限制,第二个函数给定一个等待超时时间，超过该时间就直接放弃了，并且第二个函数具有返回值，超时时间之内CountDownLatch的值达到0就返回true,等待时间结束计数值都还没达到0就返回false。这两个操作在等待过程中如果等待的线程被中断，则会抛出InterruptedException异常。

##### 测试例子

创建一定数量的线程,利用他们并发执行指定的任务,计算出总共花了多少时间.

```java
	
	public class TestHarness {
	    public long timeTasks(int nThreads, final Runnable task)
	            throws InterruptedException {
	        final CountDownLatch startGate = new CountDownLatch(1);
	        final CountDownLatch endGate = new CountDownLatch(nThreads);
	
	        for (int i = 0; i < nThreads; i++) {
	            Thread t = new Thread() {
	                public void run() {
	                    try {
	                        startGate.await(); //等待直到所有线程准备就绪,实现真正的并发执行任务
	                        try {
	                            task.run();
	                        } finally {
	                            endGate.countDown();//任务执行完毕,计数器减1;
	                        }
	                    } catch (InterruptedException ignored) {
	                    }
	                }
	            };
	            t.start();
	        }
	
	        long start = System.nanoTime();
	        startGate.countDown(); //到此处,说明所有线程准备就绪,可以开始执行任务
	        endGate.await();// 阻塞等待所有线程执行完毕.
	        long end = System.nanoTime();
	        return end - start;
	    }
	}

```

#### 栅栏(Barrier)

栅栏类似闭锁,能阻塞一组线程直到某个事件发生.栅栏与闭锁的区别关键在于:**闭锁用于等待事件,而栅栏用于等待其他线程**.

CyclicBarrier可以使得一定数量的参与反复在栅栏位置汇聚,在并行迭代算法中非常有用:将一个问题拆分成一系列相互独立的子问题.当线程达到栅栏位置时调用await()阻塞直到所有线程到达栅栏位置,如果所有线程到达,栅栏打开,线程唤醒,而栅栏被重置下次使用.如果await()调用超时,或者阻塞的线程被中断,那么栅栏就算是被打破,所有await()调用会抛出BrokenBrrierException.

CyclicBarrier还可以在构造函数中传入一个Runnable,当成功通过栅栏时会(在一个子线程中)执行他,但是阻塞线程被释放之前是不能执行的.

构造函数
##### 循环栅栏函数解析

* `public CyclicBarrier(int parties, Runnable barrierAction)`
  `public CyclicBarrier(int parties)`

	参数parties表示一共有多少线程参与这次“活动”，参数barrierAction是可选的，用来指定当所有线程都完成这些必须的“神秘任务”之后需要干的事情，所以barrierAction这里的动作在一个相互等待的循环内只会执行一次。

* `blic int getParties()`

	用来获取当前的CyclicBarrier一共有多少线程参数与.


* `public int await() throws InterruptedException, BrokenBarrierException`
  `public int await(long timeout, TimeUnit unit)throws InterruptedException, BrokenBarrierException, TimeoutException`
 
	await函数用来执行等待操作，一个函数是一个无参函数，第二个函数可以指定等待的超时时间。它们的作用是：一直等待知道所有参与“活动”的线程都调用过await函数，如果当前线程不是即将调用await函数的的最后一个线程，当前线程将会被挂起，直到下列某一种情况发生：

	* 最后一个线程调用了await函数；
	* 某个线程打断了当前线程；
	* 某个线程打断了其他某个正在等待的线程；
	* 其他某个线程等待时间超过给定的超时时间；
	* 其他某个线程调用了reset函数。
	
    如果等待过程中线程被打断了，则会抛出InterruptedException异常；
	如果等待过程中出现下列情况中的某一种情况，则会抛出BrokenBarrierException异常：

	* 其他线程被打断了；
	* 当前线程等待超时了；
	* 当前CyclicBarrier被reset了；
	* 等待过程中CyclicBarrier损坏了；
	* 构造函数中指定的barrierAction在执行过程中发生了异常。
	
	如果等待时间超过给定的最大等待时间，则会抛出TimeoutException异常，并且这个时候其他已经嗲用过await函数的线程将会继续后续的动作。

	返回值：返回当前线程在调用过await函数的所以线程中的编号，编号为parties-1的表示第一个调用await函数，编号为0表示是最后一个调用await函数。


* `public boolean isBroken()`

	用来判断barrier是否已经损坏,如果因为任何原因被损坏返回true，否则返回false。

* `public void reset()`

	这个函数用来重置barrier,如果调用了该函数，则在等待的线程将会抛出BrokenBarrierException异常。

* `public int getNumberWaiting()`

	该函数用来获取当前正在等待该barrier的线程数

##### 实例展示:

```java

	public class CellularAutomata {
	    private final Board mainBoard;
	    private final CyclicBarrier barrier;
	    private final Worker[] workers;
	
	    public CellularAutomata(Board board) {
	        this.mainBoard = board;
	        int count = Runtime.getRuntime().availableProcessors();
	        this.barrier = new CyclicBarrier(count,
	                new Runnable() {
	                    public void run() {
	                        mainBoard.commitNewValues();//栅栏打开,计算值
	                    }});
	        this.workers = new Worker[count];
	        for (int i = 0; i < count; i++)
	            workers[i] = new Worker(mainBoard.getSubBoard(count, i));
	    }
	
	    private class Worker implements Runnable {
	        private final Board board;
	
	        public Worker(Board board) { this.board = board; }
	        public void run() {
	            while (!board.hasConverged()) {
	                for (int x = 0; x < board.getMaxX(); x++)
	                    for (int y = 0; y < board.getMaxY(); y++)
	                        board.setNewValue(x, y, computeValue(x, y));
	                try {
	                    barrier.await();//线程计算完毕等待其他线程
	                } catch (InterruptedException ex) {
	                    return;
	                } catch (BrokenBarrierException ex) {
	                    return;
	                }
	            }
	        }
	
	        private int computeValue(int x, int y) {
	            // Compute the new value that goes in (x,y)
	            return 0;
	        }
	    }
	
	    public void start() {
	        for (int i = 0; i < workers.length; i++)
	            new Thread(workers[i]).start();
	        mainBoard.waitForConvergence();
	    }
	
	    interface Board {
	        int getMaxX();
	        int getMaxY();
	        int getValue(int x, int y);
	        int setNewValue(int x, int y, int value);
	        void commitNewValues();
	        boolean hasConverged();
	        void waitForConvergence();
	        Board getSubBoard(int numPartitions, int index);
	    }
	}


```

#### 信号量(Semaphore)

计数信号量(Counting Semaphore)用来控制同时访问某个特定资源的操作数量,或者执行某个指定操作的数量.还可以用来实现某种连接池,或者对容器加边界.

Semaphore管理着一组虚拟的许可(permit),许可的初始数量可通过构造函数指定,在执行操作时先要获得许可,并在使用后释放许可.如果没有许可,accquire将阻塞到有许可为止(或者被中断,或者操作超时),release()将返回一个许可信号量.

Semaphore类只是一个资源数量的抽象表示,并不负责管理资源对象本身,可能有多个线程同时获取到资源使用许可,因此需要使**用同步机制避免数据竞争**.

#### Semaphore函数解析

* `public Semaphore(int permits)`
  `public Semaphore(int permits, boolean fair)`

	其中permits参数表示初始的可用资源数量，fair参数表示是否使用公平策略选择正在等候的使用者，fair为true表示公平策略，采用先来先用的算法，为false表示非公平策略，完全随机，默认使用非公平策略。


* `public void acquire() throws InterruptedException` 
  `public void acquire(int permits) throws InterruptedException`

	acquire函数用来申请资源,第一个函数用来申请一个资源，第二个函数用来申请permits个资源，当没有需要申请的数量这么多个资源时，申请线程会被阻塞，直到有可用资源或者申请线程被打断，如果申请线程被打断，则抛出InterruptedException异常。

* `public void acquireUninterruptibly()`
  `public void acquireUninterruptibly(int permits)`

	该函数用来申请可用资源，并且不会被打断，第一个函数用来申请一个资源，第二个函数用来申请permits个资源。就算线程在申请资源过程中被打断，依然会继续申请，只不过获取资源的时间可能会有所变化。


* `public boolean tryAcquire()`
  `public boolean tryAcquire(long timeout, TimeUnit unit)throws InterruptedException`
  `public boolean tryAcquire(int permits)`
  `public boolean tryAcquire(int permits, long timeout, TimeUnit unit)throws InterruptedException`

    tryAcquire函数用来获取可用资源，但是这类函数能够有时间的限制，如果超时，立即返回，

	第一个函数用来申请一个资源，如果当前有可用资源，立即返回true，否则立即返回false；
	第二个函数用来申请一个资源，指定一个超时时间，如果当前可以资源数量足够，立即返回true，否则最多等待给定的时间，如果时间到还是未能获取资源，则返回false；如果等待过程中线程被打断，抛出InterruptedException异常；
	和1一样，只是申请permits个资源；
	和2一样，只是申请permits个资源。


* `public void release()`
  `public void release(int permits)`

	第一个函数释放一个资源，第二个函数释放permits个资源。


* `public int availablePermits()`
* 
	availablePermits函数用来获取当前可用的资源数量


* `public int drainPermits()`
	drainPermits函数用来申请当前所有
可用的资源，


* `protected void reducePermits(int reduction)`

	用来禁止某些资源不可用
	reduction表示禁止的数量，比如由于厕所马桶坏了，有一个坑位不能用，此时就可以调用该函数禁止一个资源不可用。如果reduction小于零，则抛出IllegalArgumentException异常。



* `public boolean isFair()`

	函数返回true表示采用的是公平策略，返回false表示采用非公平策略。


* `public final boolean hasQueuedThreads()`

	用来判断是否有现成正在等待申请资源，返回true表示有现成正在等待申请资源，false表示没有，需要注意的是：因为申请过程是可以取消的，函数返回true并不表示肯定会申请资源，该函数设计的初衷是用来做系统监控的。


* `public final int getQueueLength()`

	返回当前正在等待申请资源的线程数。

* `protected Collection<Thread> getQueuedThreads()`

	返回当前正在等待申请资源的线程集合


##### 多线程同时操作特定资源例子

```java

	public class SemaphoreDemo {
	  
	    private final ReentrantLock lock = new ReentrantLock();
	    private final Semaphore semaphore;
	    private final LinkedList<Object> resourceList = new LinkedList<Object>();
	    private static CountDownLatch mCountDownLatch = new CountDownLatch(9);
	
	    public SemaphoreDemo(Collection<Object> resourceList) {
	        this.resourceList.addAll(resourceList);
	        //公平模式
	        this.semaphore = new Semaphore(resourceList.size(), true);
	    }
	
	   
	    public Object acquire() throws InterruptedException {
	        semaphore.acquire();
	
	        lock.lock();
	        try {
	            return resourceList.pollFirst();
	        } finally {
	            lock.unlock();
	        }
	    }
	
	    public void release(Object resource) {
	        lock.lock();
	        try {
	            resourceList.addLast(resource);
	        } finally {
	            lock.unlock();
	        }
	
	        semaphore.release();
	    }
	
	    public static void main(String[] args) {
	        //准备2个可用资源
	        List<Object> resourceList = new ArrayList<>();
	        resourceList.add("Resource1");
	        resourceList.add("Resource2");
	
	        //准备工作任务
	        final SemaphoreDemo demo = new SemaphoreDemo(resourceList);
	        Runnable worker = new Runnable() {
	            @Override
	            public void run() {
	                Object resource = null;
	                try {
	                    //获取资源
	                    resource = demo.acquire();
	                    System.out.println(Thread.currentThread().getName() + "\twork   on\t" + resource);
	                    //用resource做工作
	                    Thread.sleep(1000);
	                    System.out.println(Thread.currentThread().getName() + "\tfinish on\t" + resource);
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                } finally {
	                    //归还资源
	                    if (resource != null) {
	                        demo.release(resource);
	                        mCountDownLatch.countDown();
	
	                    }
	                }
	            }
	        };
	
	        //启动9个任务
	        ExecutorService service = Executors.newCachedThreadPool();
	        for (int i = 0; i < 9; i++) {
	            service.submit(worker);
	        }
	
	
	        try {
	            mCountDownLatch.await();
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	        service.shutdown();
	    }
	}

```


##### 将任何一种容器变成有界阻塞容器

```java

	public class BoundedHashSet <T> {
	    private final Set<T> set;
	    private final Semaphore sem;
	
	    public BoundedHashSet(int bound) {
	        this.set = Collections.synchronizedSet(new HashSet<T>());
	        sem = new Semaphore(bound);
	    }
	
	    public boolean add(T o) throws InterruptedException {
	        sem.acquire();
	        boolean wasAdded = false;
	        try {
	            wasAdded = set.add(o);
	            return wasAdded;
	        } finally {
	            if (!wasAdded)
	                sem.release();
	        }
	    }
	
	    public boolean remove(Object o) {
	        boolean wasRemoved = set.remove(o);
	        if (wasRemoved)
	            sem.release();
	        return wasRemoved;
	    }
	}

```
