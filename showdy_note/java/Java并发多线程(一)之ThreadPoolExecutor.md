### 线程池的优势:

Thread pools address two different problems: they usually provide improved performance when executing large numbers of asynchronous tasks,due to reduced per-task invocation overhead,and they provide a means of bounding and managing the resources, including threads, consumed when executing a collection of tasks.


线程池主要解决了两个方面的问题:

* 在执行大量异步任务时,线程池由于减少每次任务调用开销而提高了性能.
* 在执行大量任务时,线程池提供了可限制和管理资源(比如线程的消耗)的方法.


### 线程池的创建:

可以通过ThreadPoolExecutor构造函数来创建一个线程池:

```java

	public ThreadPoolExecutor(int corePoolSize,
	                              int maximumPoolSize,
	                              long keepAliveTime,
	                              TimeUnit unit,
	                              BlockingQueue<Runnable> workQueue,
	                              ThreadFactory threadFactory,
	                              RejectedExecutionHandler handler)

```

创建一个线程池需要输入几个参数：

* `corePoolSize`(**核心线程数**):,当提交一个任务到线程池时,线程池会创建一个线程来执行任务,即使其他空闲的基本线程能够执行新任务也会创建线程,等需要执行的任务数大于线程池的基本大小时就不会创建. 如果任务数大于corePoolSize,小于maximumPoolSize时,线程也仅仅在队列满了的情况下才会创建.

* `maximumPoolSize`(**线程池最大大小**):线程池允许创建的最大线程数。如果队列满了，并且已创建的线程数小于最大线程数，则线程池会再创建新的线程执行任务。值得注意的是如果使用了**无界的任务队**列这个参数就没什么效果。

* `keepAliveTime`（**线程活动保持时间**）：线程池的工作线程空闲后，保持存活的时间。所以如果任务很多，并且每个任务执行的时间比较短，可以调大这个时间，提高线程的利用率。

* `TimeUnit`（**线程活动保持时间的单位**）：可选的单位有天（DAYS），小时（HOURS），分钟（MINUTES），毫秒(MILLISECONDS)，微秒(MICROSECONDS, 千分之一毫秒)和毫微秒(NANOSECONDS, 千分之一微秒)。

* `BlockingQueue<Runnable>`（**任务队列**）：用于保存等待执行的任务的阻塞队列。
	* ArrayBlockingQueue：是一个基于数组结构的**有界阻塞队列(bounded queue)**，此队列按 FIFO（先进先出）原则对元素进行排序。
	* LinkedBlockingQueue：一个基于链表结构的**阻塞队列(unbounded queue)**，此队列按FIFO （先进先出） 排序元素，吞吐量通常要高于ArrayBlockingQueue。静态工厂方法Executors.newFixedThreadPool()使用了这个队列。
	* SynchronousQueue：一个**不存储元素的阻塞队列(handsoff)**。每个插入操作必须等到另一个线程调用移除操作，否则插入操作一直处于阻塞状态，吞吐量通常要高于LinkedBlockingQueue，静态工厂方法Executors.newCachedThreadPool使用了这个队列。
	* PriorityBlockingQueue：一个具有优先级的无界阻塞队列。
	
* `ThreadFactory`(**线程工厂**):用于设置创建线程的工厂，可以通过线程工厂给每个创建出来的线程设置更有意义的名字.

* `RejectedExecutionHandler`（**饱和策略**）：当队列和线程池都满了，说明线程池处于饱和状态，那么必须采取一种策略处理提交的新任务。这个策略默认情况下是AbortPolicy，表示无法处理新任务时抛出异常。以下是JDK1.5提供的四种策略。
	* AbortPolicy：直接抛出异常。
	* CallerRunsPolicy：只用调用者所在线程来运行任务。
	* DiscardOldestPolicy：丢弃队列里最近的一个任务，并执行当前任务。
	* DiscardPolicy：不处理，丢弃掉。
	* 当然也可以根据应用场景需要来实现RejectedExecutionHandler接口自定义策略。如记录日志或持久化不能处理的任务。

### 向线程池提交任务:

* `public void execute(Runnable command)`: 执行一个Runnable任务,没有返回值,无法判断线程池执行是否成功,主要分三步:
	* 活动线程小于corePoolSize的时候就创建新线程池
	* 活动线程大于corePoolSize时就想加入到任务队列中
	* 任务队列满了再去启动新线程,如果线程数达到最大值就拒绝任务.

	```java
	
		public void execute(Runnable command) {
		    if (command == null)
		        throw new NullPointerException();
		
		    int c = ctl.get();
		    // 活动线程数 < corePoolSize
		    if (workerCountOf(c) < corePoolSize) {
		        // 直接启动新的线程。第二个参数true:addWorker中会重新检查workerCount是否小于corePoolSize
		        if (addWorker(command, true))
		            // 添加成功返回
		            return;
		        c = ctl.get();
		    }
		    // 活动线程数 >= corePoolSize
		    // runState为RUNNING && 队列未满
		    if (isRunning(c) && workQueue.offer(command)) {
		        int recheck = ctl.get();
		        // double check
		        // 非RUNNING状态 则从workQueue中移除任务并拒绝
		        if (!isRunning(recheck) && remove(command))
		            reject(command);// 采用线程池指定的策略拒绝任务
		        // 线程池处于RUNNING状态 || 线程池处于非RUNNING状态但是任务移除失败
		        else if (workerCountOf(recheck) == 0)
		            // 这行代码是为了SHUTDOWN状态下没有活动线程了，但是队列里还有任务没执行这种特殊情况。
		            // 添加一个null任务是因为SHUTDOWN状态下，线程池不再接受新任务
		            addWorker(null, false);
		
		        // 两种情况：
		        // 1.非RUNNING状态拒绝新的任务
		        // 2.队列满了启动新的线程失败（workCount > maximumPoolSize）
		    } else if (!addWorker(command, false))
		        reject(command);
		}
	
	```

* ` <T> Future<T> submit(Callable<T> task)`:

  	****执行一个Runnable或者一个Callable任务,返回一个Future来判断任务否执行成功,通过{@Link Funtrure#get()}获取执行的结果,get()方法会阻塞直到任务完成或者失败.

	```java
	
		Future<Object> future = executor.submit(harReturnValuetask);
		try {
		     Object s = future.get();
		} catch (InterruptedException e) {
		    // 处理中断异常
		} catch (ExecutionException e) {
		    // 处理无法执行任务异常
		} finally {
		    // 关闭线程池
		    executor.shutdown();
		}
	
	```



### 线程池的关闭

* `shutdown()` 会将runState设置为SHUTDOWN,会**终止所有的空闲线程**.

```java

 	public void shutdown() {
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            checkShutdownAccess();
			//将线程池状态设置为SHUTDOWN
            advanceRunState(SHUTDOWN);
			//注意这里是中断所有空闲的线程：runWorker中等待的线程被中断 → 进入processWorkerExit →
            // tryTerminate方法中会保证队列中剩余的任务得到执行。
            interruptIdleWorkers();
            onShutdown(); // hook for ScheduledThreadPoolExecutor
        } finally {
            mainLock.unlock();
        }
        tryTerminate();
    }

```

* `shutdownNow()`将runState设置为STOP,和shutdown()区别是**这个方法终止所有线程**.

```java

	public List<Runnable> shutdownNow() {
        List<Runnable> tasks;
        final ReentrantLock mainLock = this.mainLock;
        mainLock.lock();
        try {
            checkShutdownAccess();
			// STOP状态：不再接受新任务且不再执行队列中的任务。
            advanceRunState(STOP);
			// 中断所有线程
            interruptWorkers();
			// 返回队列中还没有被执行的任务。
            tasks = drainQueue();
        } finally {
            mainLock.unlock();
        }
        tryTerminate();
        return tasks;
    }

```

* `boolean isShutdwon()` 

```java

	public boolean isShutdown() {
		//说明只要调用了shutdown()或者shutdwonNow()之一,此方法就会返回ture.
        return ! isRunning(ctl.get());
    }
	
	 private static boolean isRunning(int c) {
        return c < SHUTDOWN;
    }

```

* `boolean isTerminated()` 当所有线程都终止时此方法才返回true.

```java

    public boolean isTerminated() {
        return runStateAtLeast(ctl.get(), TERMINATED);
    }
		
	private static boolean runStateAtLeast(int c, int s) {
        return c >= s;
    }

```

我们可以通过调用线程池的shutdown或shutdownNow方法来关闭线程池，它们的原理是遍历线程池中的工作线程，然后逐个调用线程的interrupt方法来中断线程，所以无法响应中断的任务可能永远无法终止。但是它们存在一定的区别，shutdownNow首先将线程池的状态设置成STOP，然后尝试停止所有的正在执行或暂停任务的线程，并返回等待执行任务的列表，而shutdown只是将线程池的状态设置成SHUTDOWN状态，然后中断所有没有正在执行任务的线程。

只要调用了这两个关闭方法的其中一个，isShutdown方法就会返回true。当所有的任务都已关闭后,才表示线程池关闭成功，这时调用isTerminaed方法会返回true。至于我们应该调用哪一种方法来关闭线程池，应该由提交到线程池的任务特性决定，通常调用shutdown来关闭线程池，如果任务不一定要执行完，则可以调用shutdownNow。


### AtomicInteger ctl

`private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));`

AtomicInteger保证了对这个变量的操作是原子的，通过巧妙的操作，ThreadPoolExecutor用这一个变量保存了两个内容：

* 所有有效线程的数量
* 各个线程的状态（runState）

低29位存线程数，高3位存runState,这样runState有5个值：

```java

	private static final int COUNT_BITS = Integer.SIZE - 3;
    private static final int CAPACITY   = (1 << COUNT_BITS) - 1;

 	private static final int RUNNING    = -1 << COUNT_BITS;
    private static final int SHUTDOWN   =  0 << COUNT_BITS;
    private static final int STOP       =  1 << COUNT_BITS;
    private static final int TIDYING    =  2 << COUNT_BITS;
    private static final int TERMINATED =  3 << COUNT_BITS;
```	
	
线程池中各个状态间的转换比较复杂：

* RUNNING状态：线程池正常运行，可以接受新的任务并处理队列中的任务；
* SHUTDOWN状态：不再接受新的任务，但是会执行队列中的任务；
* STOP状态：不再接受新任务，不处理队列中的任务

围绕ctl变量操作如下:

```java
	
	/*
	 * 该方法用于取出runstate的值,因为CAPACTIY值为:00011111111111111111111111111111
	 * ~为按位取反操作，则~CAPACITY值为：11100000000000000000000000000000
 	 * 再同参数做&操作，就将低29位置0了，而高3位还是保持原先的值，也就是runState的值
 	 * /
    private static int runStateOf(int c) { 
		return c & ~CAPACITY; 
	}
	
	/**
	 * 这个方法用于取出workerCount的值
	 * 因为CAPACITY值为：00011111111111111111111111111111，所以&操作将参数的高3位置0了
	 * 保留参数的低29位，也就是workerCount的值
	 * 
	 * @param c ctl, 存储runState和workerCount的int值
	 * @return workerCount的值
	 */
    private static int workerCountOf(int c)  {
		 return c & CAPACITY;
	 }
	
	/**
	 * 将runState和workerCount存到同一个int中
	 * “|”运算的意思是，假设rs的值是101000，wc的值是000111，则他们位或运算的值为101111
	 * 
	 * @param rs runState移位过后的值，负责填充返回值的高3位
	 * @param wc workerCount移位过后的值，负责填充返回值的低29位
	 * @return 两者或运算过后的值
	 */
    private static int ctlOf(int rs, int wc) {
		 return rs | wc; 
	}

	// 只有RUNNING状态会小于0
	private static boolean isRunning(int c) {
	    return c < SHUTDOWN;
	}

```


### 线程池配置策略:

要想合理的配置线程池，就必须首先分析任务特性，可以从以下几个角度来进行分析：

1. 任务的性质：CPU密集型任务，IO密集型任务和混合型任务。
2. 任务的优先级：高，中和低。
3. 任务的执行时间：长，中和短。
4. 任务的依赖性：是否依赖其他系统资源，如数据库连接。

任务性质不同的任务可以用不同规模的线程池分开处理。CPU密集型任务配置尽可能小的线程，如配置Ncpu+1个线程的线程池。IO密集型任务则由于线程并不是一直在执行任务，则配置尽可能多的线程，如2*Ncpu。混合型的任务，如果可以拆分，则将其拆分成一个CPU密集型任务和一个IO密集型任务，只要这两个任务执行的时间相差不是太大，那么分解后执行的吞吐率要高于串行执行的吞吐率，如果这两个任务执行时间相差太大，则没必要进行分解。我们可以通过Runtime.getRuntime().availableProcessors()方法获得当前设备的CPU个数。

优先级不同的任务可以使用优先级队列PriorityBlockingQueue来处理。它可以让优先级高的任务先得到执行，需要注意的是如果一直有优先级高的任务提交到队列里，那么优先级低的任务可能永远不能执行。

执行时间不同的任务可以交给不同规模的线程池来处理，或者也可以使用优先级队列，让执行时间短的任务先执行。

依赖数据库连接池的任务，因为线程提交SQL后需要等待数据库返回结果，如果等待的时间越长CPU空闲时间就越长，那么线程数应该设置越大，这样才能更好的利用CPU。

**建议使用有界队列**，有界队列能增加系统的稳定性和预警能力，可以根据需要设大一点，比如几千。有一次我们组使用的后台任务线程池的队列和线程池全满了，不断的抛出抛弃任务的异常，通过排查发现是数据库出现了问题，导致执行SQL变得非常缓慢，因为后台任务线程池里的任务全是需要向数据库查询和插入数据的，所以导致线程池里的工作线程全部阻塞住，任务积压在线程池里。如果当时我们设置成无界队列，线程池的队列就会越来越多，有可能会撑满内存，导致整个系统不可用，而不只是后台任务出现问题。当然我们的系统所有的任务是用的单独的服务器部署的，而我们使用不同规模的线程池跑不同类型的任务，但是出现这样问题时也会影响到其他任务


