### 队列

队列是先进先出（FIFO）的线性表。在具体应用中通常用链表或者数组来实现。队列只允许在后端（称为rear）进行插入操作，在前端（称为front）进行删除操作。队列的操作方式和堆栈类似，唯一的区别在于队列只允许新数据在后端进行添加。

|操作|抛出异常|有返回值|
| ------------- |:-------------:| -----:|
|Insert|	add(e)|	offer(e)|
|Remove|	remove()|	poll()|
|Examine|	element()|	peek()|

![](http://www.uml-diagrams.org/examples/java-7-concurrent-collections-uml-class-diagram-example.png)


### 阻塞队列

阻塞队列(BlockingQueue)是一个支持两个附加操作的队列,这两个附加操作支持阻塞的插入和移除方法.

* 支持阻塞的插入方法: 当队列满时,队列会阻塞插入元素的线程,直到队列不满为止.
* 支持阻塞的移除方法: 当队列为空时,获取元素的线程阻塞等待线程非空.

阻塞队列通常用于生产者和消费者的场景,生产者就是向队列里添加元素,而消费者就是从队列里取出元素. 阻塞队列就是生产者存储元素而消费者用来获取元素的容器.

|操作方式|抛出异常|返回特殊值|一直阻塞| 超时退出|
| ------- |:-------:| -----:|-----:|-----:|
|插入|	add(e)|	offer(e)|put(e)|offer(e,time,unit)|
|移除|	remove()|	poll()|take()|poll(time,unit)|
|检查|	element()|	peek()|不可用|不可用|

注意: 如果是无界阻塞队列,队列永远都不会出现满的情况,所以使用put或者take方法永远都不会被阻塞,而且使用put方法时,该方法永远返回为true.


### JDK提供的阻塞队列

从上面的UML图可以看到,JKD7提供了7个阻塞队列:

* `ArrayBlockingQueue`: 由数组结构组成的有界阻塞队列
* `LinkedBlockingQueue`: 由链表结构组成的有界阻塞队列
* `PriorityBlockingQueue`: 支持优先级排序的无界阻塞队列
* `DelayQueue`:　使用优先级队列队列实现的无界阻塞队列
* `SynchronousQueue`: 不存储元素的阻塞队列
* `LinkedTransferQueue`: 由链表结构组成的无界阻塞队列
* `LinkedBlockingDeque`: 由链表结构组成的双向阻塞队列

#### `ArrayBlockingQueue`

`ArrayBlockingQueue`是一个用数组实现的有界队列,此队列按照先进先出的原则对元素进行排序.

默认情况下不保证线程公平的访问队列,所谓公平访问队列是指阻塞的线程,可以按照阻塞的先后顺序访问队列,即先阻塞线程先访问队列.非公平性对先等待的线程是非公平的,当队列可用时,阻塞的线程都可以争夺访问队列的资格,有可能先阻塞的线程最后才访问队列.

为了保证公平性,通常会降低吞吐量,可以使用以下代码创建一个公平的阻塞队列.

```java

	ArrayBlockingQueue fairQueue= new ArrayBlockingQueue(1000,true);

```

访问者的公平性是使用可重入锁实现的,代码如下:

```java

	public ArrayBlockingQueue(int capacity, boolean fair) {
        if (capacity <= 0)
            throw new IllegalArgumentException();
        this.items = new Object[capacity];
        lock = new ReentrantLock(fair);
        notEmpty = lock.newCondition();
        notFull =  lock.newCondition();
    }

```


#### `LinkedBlockingQueue`

`LinkedBlockingQueue`是一个用链表实现的有界阻塞队列,此队列默认最大长度为Integer.MAX_VALUE,按照先进先出(FIFO)的原则对元素进行排序


#### `PriorityBlockingQueue`

`PriorityBlockingQueue`是一个支持优先级的无界阻塞队列,默认情况下元素采用自然排序升序排列,也可以自定义类实现compareTo()方法来指定元素排序规则,或者初始化`PriorityBlockingQueue`时,指定构造参数Comparator来对元素进行排序,需要注意的是不能保证同优先级的元素排序.

#### `DelayQueue`

`DelayQueue`是一个支持延时获取元素的无界阻塞队列,队列使用PriorityQueue来实现. 队列中元素必须实现Delayed接口,在创建元素时可以指定多久才能从队列中获取当前元素.只有延迟期满时才能从队列中提出元素.

`DelayQueue`非常有用,可以将`DelayQueue`运用在一下场景:

* 缓存系统的设计: 可以送`DelayQueue`保存缓存元素的有效期,使用一个线程循环查询`DelayQueue`,一旦从`DelayQueue`获取元素,就表示缓存到期了.
* 定时任务调度:使用`DelayQueue`保存当前将会执行的任务和执行时间,一旦从`DelayQueue`中获取到任务就开始执行,比如TimeQueue就是使用`DelayQueue`实现的.

#### `SynchronousQueue`
`SynchronousQueue`是一个不存储元素的阻塞队列,每个put操作必须等待一个take操作,否则不能继续添加元素.

支持公平访问队列,默认情况下线程采用非公平性策略,使用带boolean参数的构造方法可以实现等待线程采用先进先出(FIFO)的顺序访问队列.

```java

	public SynchronousQueue(boolean fair) {
        transferer = fair ? new TransferQueue<E>() : new TransferStack<E>();
    }

```
#### `LinkedTransferQueue`

`LinkedTransferQueue`是一个由链表结构组成的无界阻塞TransferQueue队列,相当于其他阻塞队列,LinkedTransferQueue多了一tryTransfer和transfer方法.

* transfer方法
	
	如果当前有消费者正在等待接收元素(消费者使用take()方法或者带时间限制的poll方式时)transfer()方法可以吧生产者传入的元素立即transfer(传输)给消费者,如果没有消费者在等待接收元素,transfer方法将元素存放在队列的tail节点,并等待该元素被消费者消费了才返回.

* tryTransfer方法

	tryTransfer方法用来试探生产者传入元素是否能够直接传递给消费者,如果没有消费者等待接收元素.则返回false, 和transfer方法的区别是tryTransfer方法无论消费者是否接收,方法立即返回,而transfer需要等待消费者消费了才返回.

#### `LinkedBlockingDeque`

`LinkedBlockingDeque`是一由链表结构组成的双向阻塞队列,所谓双向队列指的是可以从队列两端插入和移除元素,双端队列因为多了一个操作队列的入口,在多线程同时入队时,也就减少了一般竞争.相比其他阻塞队列,`LinkedBlockingDeque`多了addFirst, addLast,offerFirst,offerLast,peekFirst,peekLast等方法.

在初始化`LinkedBlockingDeque`时可以设置容量防止其过渡膨胀, 另外,双向阻塞队列可以运行在"工作窃取"模式中.


### 阻塞队列实现的原理

**通知模式实现**: 所谓通知模式,就是当生产者从满的队列里添加元素时会阻塞生产者,而当消费者消费了一个队列中的元素后,就会通知生产者当前队列可用. **ArrayBlockingQueue使用ReentrantLock和Condition实现**.

```java

 	/** Main lock guarding all access */
    final ReentrantLock lock;
    /** Condition for waiting takes */
    private final Condition notEmpty;
    /** Condition for waiting puts */
    private final Condition notFull;

	 public ArrayBlockingQueue(int capacity, boolean fair) {
        if (capacity <= 0)
            throw new IllegalArgumentException();
        this.items = new Object[capacity];
        lock = new ReentrantLock(fair);
        notEmpty = lock.newCondition();
        notFull =  lock.newCondition();
    }

	 public void put(E e) throws InterruptedException {
        Objects.requireNonNull(e);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (count == items.length)
                notFull.await();
            enqueue(e);
        } finally {
            lock.unlock();
        }
    }
	
	public E take() throws InterruptedException {
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try {
            while (count == 0)
                notEmpty.await();
            return dequeue();
        } finally {
            lock.unlock();
        }
    }

	private void enqueue(E x) {
        // assert lock.getHoldCount() == 1;
        // assert items[putIndex] == null;
        final Object[] items = this.items;
        items[putIndex] = x;
        if (++putIndex == items.length) putIndex = 0;
        count++;
        notEmpty.signal();
    }

    /**
     * Extracts element at current take position, advances, and signals.
     * Call only when holding lock.
     */
    private E dequeue() {
        // assert lock.getHoldCount() == 1;
        // assert items[takeIndex] != null;
        final Object[] items = this.items;
        @SuppressWarnings("unchecked")
        E x = (E) items[takeIndex];
        items[takeIndex] = null;
        if (++takeIndex == items.length) takeIndex = 0;
        count--;
        if (itrs != null)
            itrs.elementDequeued();
        notFull.signal();
        return x;
    }
```

当往队列里插入一个元素时,如果队列不可用,那么阻塞生产者主要通过LockSupport.part(this)实现:

```java

	public final void await() throws InterruptedException {
            if (Thread.interrupted())
                throw new InterruptedException();
            Node node = addConditionWaiter();
            int savedState = fullyRelease(node);
            int interruptMode = 0;
            while (!isOnSyncQueue(node)) {
                LockSupport.park(this);
                if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
                    break;
            }
            if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
                interruptMode = REINTERRUPT;
            if (node.nextWaiter != null) // clean up if cancelled
                unlinkCancelledWaiters();
            if (interruptMode != 0)
                reportInterruptAfterWait(interruptMode);
        }

```

然后看看LockSupport的源码:发现调研setBlocker先保存一下将要阻塞的线程,然后代用unsafe.park阻塞当前线程:

```java

	public static void park(Object blocker) {
        Thread t = Thread.currentThread();
        setBlocker(t, blocker);
        U.park(false, 0L);
        setBlocker(t, null);
    }
```

park是个native方法,会阻塞当前线程,只有以下四种情况中一种发生时,该返回才会返回.

* 与park相对的unpark执行或者已经执行. "已经执行"是指执行unpark,再执行park的情况
* 线程被中断时
* 等待完time参数指定的毫秒数时
* 异常现象发生时,这个异常现象没有任何原因
