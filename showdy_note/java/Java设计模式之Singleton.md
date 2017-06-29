### Singleton单例模式简介

在应用单例模式时,单例对象的类必须保证只有一个实例存在.许多时候整个系统只需要一个全局对象, 这样有利于协调系统整体行为,如在一个应用中,应该只有ImageLoader实例,这个ImageLoader中又含有线程池,缓存系统,网络请求等, 很消耗资源,所以不能多次构建实例.

构建单例模式要保证几点:

* 构造方法不对外开放,一般为private
* 通过一个静态方法或者枚举返回单例类对象
* 确保单例对象有且只有一个,尤其在多线程的环境下.
* 确保单例对象在反序列化时不会重新构建对象.


### 构建单例模式的方式:

* 懒汉式: 指全局的单例实例在第一次被使用时构建
* 饿汉式: 指全局的单例实例在类转载时构建.


#### 懒汉式单例

单线程的懒汉式单例:

```java

	public class Singleton {
	
	    private static Singleton sInstance;
	
	    private Singleton() {
	    }
	
	    public Singleton getInstance() {
	        if (sInstance == null) {
	              sInstance = new Singleton();
	         } 
	        return sInstance;
	    }
	}

```
这种最简单的懒汉式单例只有在单线程中才有作用, 如果在多线程中由于多线程执行的问题的会因为线程并发的问题产生多个实例.所以我们需要同步即:

```java

	public class Singleton {
	
	    private static  Singleton sInstance;
	
	    private Singleton() {
	    }
	
	    public Singleton getInstance() {
	        if (sInstance == null) {
	            synchronized (Singleton.class) {      
	                    sInstance = new Singleton();
	            }
	        }
	        return sInstance;
	    }
	}

```
但这种线程同步还是不能做到线程安全的问题, 还是会产生多个实例对象, 所以我们再一次进行判断nul:

```java

	public class Singleton {
	
	    private static  Singleton sInstance;
	
	    private Singleton() {
	    }
	
	    public Singleton getInstance() {
	        if (sInstance == null) {
	            synchronized (Singleton.class) {
	                if (sInstance == null) {
	                    sInstance = new Singleton();
	                }
	            }
	        }
	        return sInstance;
	    }
	}

```

这种双重判断DCL单例在JDK小于1.5时还是会为因为构造对象出现指令重排序的问题, 故而给单例对象添加volatile关键字修饰, 禁止指令重排序; 所以最终版为:

```java

	public class Singleton {
	
	    private static volatile Singleton sInstance;
	
	    private Singleton() {
	    }
	
	    public Singleton getInstance() {
	        if (sInstance == null) {
	            synchronized (Singleton.class) {
	                if (sInstance == null) {
	                    sInstance = new Singleton();
	                }
	            }
	        }
	        return sInstance;
	    }
	}

```

从上面代码中可以看到:

* 构造方法私有化;
* 单例对象使用volatile修饰;
* 使用静态方法getInstance()返回单例对象;
* 在构建单例对象使用Double-CheckLock(DCL);

使用DLC双重检查,第一次判断sInstance=null,是为了避免不必要的同步问题,第二次判断sInstance=null是为了避免多次创建实例对象.其实在构建实例sInstance=new Singleton()时, 这句代码并不是一个原子操作,可以分为三步:

* 给Singleton实例分配内存
* 调用Singleton()构造函数,初始化成员变量字段
* 将sInstance对象指向分配的内存空间.

但是由于**指令重排序**的原因,可能不保证上述三点不按照顺序执行,可能是1-2-3,也可能是2-1-3或者1-3-2,如果是第三点先执行,第二点还未执行,就切换到其他线程,sInstance已经非空,就不会构建实例,使用时就会崩溃.

知道是指令重排序造成的问题后,只要禁止指令重排序既可, 就可以使用**volatile关键字**修饰sInstance,保证单例对象内存可见性.

#### 饿汉式单例

```java

	public class Singleton{
	    
	    private static final Singleton sInstance= new Singleton();
	    
	    private Singleton(){};
	    
	    public static Singleton getInstance(){
	        return sInstance;
	    }
	    
	}

```

饿汉式单例存在的特点也很明显: 由于sInstance实例在类加载时进行的,而类的加载是由ClassLoader来进行,故而实例的初始化时机比较难把握,可能由于初始化时机太早,造成资源浪费,如果初始化本身依赖一些其他数据,那么很难保证其他数据在这之前已经准备就绪.

那么什么时候会类加载? 不太严格的说,类的加载一般会出现在一下几个时机:

* new一个对象是;
* 使用反射创建实例时;
* 子类被加载时,如果父类还未加载,就先加载父类
* JVM启动执行的主类会首先被加载.

### 静态内部类单例模式

DCL双重检查单例模式虽然在一定程度上解决了资源消耗,多余的同步,线程安全问题,但是他还是会出现失效问题, 这种问题被称为双重检查锁定(DCL)失效,建议使用如下代码:

```java

	public class Singleton {
	
	    private Singleton(){};
	    
	    public static Singleton getInstance(){
	        return  SingletonHolder.sInstance;
	    }
	    
	    private static class SingletonHolder{
	        private static final Singleton sInstance= new Singleton();
	    }
	}

```
当第一次加载Singleton类并不会初始化sInstance,只有在第一次调用Singleton的getInstance()才会初始化sInstance.


### 枚举单例

```java
	
	public enum SingletonEnum{
		INSTANCE;
		public void doSomething(){
			//...
		}
	}

```

枚举单例的最大优势在于,无偿提供了序列化机制,绝对防止对象实例化,即使在面对复杂的序列化或者反射攻击的时候.
