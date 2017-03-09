### 线程的生命周期(状态)

![](http://www.uml-diagrams.org/examples/state-machine-example-java-6-thread-states.png)

Java线程运行的生命周期处于图中的6中不同的状态,一个时刻,只能是一种状态:

* NEW: 初始状态,线程被构建,但是还没有调用start().

* RUNNABLE: 运行状态,Java线程将被操作系统中的就绪和运行状态统称"运行中".

* BLOCKED: 阻塞状态,表示线程阻塞于锁,一般由调用以下方法造成:
	* object.wait().

* WAITING: 等待状态,表示线程进入等待状态,进入该状态表示当前线程需要等待其他线程做出一些特定动作(通知或者中断).可能由以下方法造成:
	* Object.wait()
	* Thread.join()
	* LockSupport.park()
	
* TIME_WAITING: 超时等待状态,该状态不同于WAITING,他可在指定的时间自行返回的.可能由调用一下方法造成:
	* Thread.sleep(sleeptime)
	* Object.wait(timeout)
	* Thread.join(timeout)
	* LockSupport.parkNanos(timeout)
	* LockSupport.parkUntil(timeout)
* TERMINATED: 终止状态,表示当前线程已经执行完毕.


### 线程让步

 `public static native void yield();`


### 线程守护

`public final void setDaemon(boolean on)`


### 线程Jion

`public final void join(long millis) throws InterruptedException`


