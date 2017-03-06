### 可见性

* 可见性: 一个线程对共享变量值的修改,能够及时地被其他线程看到.
* 共享变量: 如果一个变量在多个线程的工作内存中都存在副本,那么这个变量就是这几个线程的共享变量.

### Java 内存模型(JMM)

Java内存模型(Java Memory Model)描述了Java程序中各种变量(线程共享变量)的访问规则,以及在JVM中将变量存储到内存和从内存中读取出变量这样的底层细节.

![](https://www.processon.com/view/link/58bcccdde4b047b78a836f36)

* 所有变量都存储在主内存中

* 每个线程都有自己独立的工作内存,里面保存该线程使用到的变量副本(主内存中该变量的拷贝)


* **线程对共享变量的所有操作都必须在自己的工作内存中进行,不能直接从主内存中读写**.

* **不同线程池之间无法直接访问其他线程工作内存中的变量,线程间的变量值传递需要主内存来完成**.

### 共享变量可见性实现的原理

线程1对共享变量的修改要想被线程2及时看到,必须要经过如下2步骤:

* 把工作内存1中更新过的共享变量刷新到主内存中,
* 将主内存中最新的共享变量的值更新到工作内存2中.


### Synchronized实现可见性:

Java**语言层面**支持的可见性实现方式:

* synchronized
* volatile

JMM关于synchronized的两条规定:

* 线程解锁前,必须要共享变量的最新值刷新到主内存中,
* 线程加锁前,将清空工作内存中共享变量的值,从而使用共享变量时需要从主内存中重新读取最新的值.

两条规定能保证,线程解锁前对共享变量的修改在下次加锁时对其他线程的可见性.

线程执行互斥代码的过程:

1. 获取互斥锁
2. 清空工作内存
3. 从主内存中拷贝变量的最新副本到工作内存
4. 执行代码
5. 将更改后的共享变量值刷新到主内存中.
6. 释放互斥锁.

**指令重排序**:代码书写的顺序与实际执行的顺序不同,指令重排序是编译器或处理为了提高程序性能而做的优化,分为三种:

1. 编译器优化的重排序
2. 指令级并行重排序
3. 内存系统的重排序

**as-if-serial**:无论如何重排序,程序执行的结果都应该与代码顺序执行的结果一致(Java编译器,运行时和处理器都会保证java在单线程下遵循as-if-serial语义).

重排序不会给单线程带来内存可见性的问题,但是在多线程中程序交错执行时,重排序可能会造成内存可见性的问题.

```java

	public class NoVisibility {
	    private static boolean ready;
	    private static int number;
	
	    private static class ReaderThread extends Thread {
	        @Override
	        public void run() {
	            while (!ready) {
	                Thread.yield();
	                System.out.println(number);
	            }
	        }
	    }

	    public static void main(String[] args) {
	        new ReaderThread().start();
	        number = 42;
	        ready = true;
	    }
	}

```
上面的程序可能会一直运行下去,因为线程可能永远读取不到ready的值.也可能输出为0,因为线程可能看到写入了ready的值,但是却没有看到number之后写入的值,这种现象称为"重排序".

### Volatile实现可见性:

**volatile关键字**:

* 能够保证volatile变量的可见性
* 不能保证volatile变量复合操作的原子性

**volatile如何实现内存可见性**:

深入来说: 通过加入**内存屏障**(8条)和**禁止重排序**优化来实现.

* 对volatile变量执行写操作时,会在操作后加入一条store屏障指令(强制将变量值刷新到主内存中去)
* 对volatile变量执行读操作时,会在操作前加入一条load屏障指令(强制从主内存中读取变量的值)

线程写volatile变量的过程:

1. 改变线程工作内存中的volatile变量副本的值
2. 将改变后副本值从工作内存刷新到主内存中

线程读volatile变量的过程

1. 从主内存中读取volatile变量的最新值到线程的工作内存中
2. 从工作内存中读取volatile变量的副本


要在多线程中安全使用volatile变量,必须满足:

1. 对变量的写入操作不能依赖当前值,或者你能确保只有一个单线程更新变量的值.
2. 该变量不会与其他状态变量一起纳入不变性条件中
3. 在访问变量时不需要加锁.


```java

	public class VolatileDemo {
	    private volatile int number = 0;
	    
	    public int getNumber() {
	        return number;
	    }
	
	    public void increase() {
	        try {
	            TimeUnit.MILLISECONDS.sleep(1);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	        this.number++; //非原子操作导致结果可能不为500
	    }
	
	    public static void main(String... args) {
	        final VolatileDemo volatileDemo = new VolatileDemo();
	        for (int i = 0; i < 500; i++) {
	            new Thread(new Runnable() {
	                @Override
	                public void run() {
	                    volatileDemo.increase();
	                }
	            }).start();
	        }
	        //主线程给子线程让出资源
	        while (Thread.activeCount() > 1) {
	            Thread.yield();
	        }
	        System.out.println("number:" + volatileDemo.getNumber());
	    }
	}
```

想要保证`this.number++`的原子性操作,有三种方式:

* synchronized同步关键字
* Lock
* AotomicInteger
