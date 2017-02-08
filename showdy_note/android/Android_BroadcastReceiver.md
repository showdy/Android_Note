### Android Broadcast

#### Broadcast使用场景

Android广播分为两个方面:广播发送者和广播接受者.通常情况下,BroadcastRecevier指广播接受者,广播作为Android组件之间的通讯方式,使用场景有:

* 同一个APP内部的同一个组件类的消息通讯(单线程或者多个线程)
* 同一个APP内部不同的组件之间的消息通讯(单个进程)
* 同一个APP具有多个进程不同组件之间的消息通讯
* 不同APP之间的组件之间的通讯
* Android系统在特定情况下与APP之间的通讯.

#### Broadcast实现的基本流程为:
* 广播接受者BroadcastRecevier通过Binder机制向AMS(Activity manager Service)进行注册
* 广播发送者通过Binder机制向AMS发送广播
* AMS查找符合条件(intentFilter/permission)的BroadcastRecevier,将广播发送给ReceiverDispatcher,Dispatcher将广播发送到BroadcastReceiver(一般情况是Activity)的消息循环队列中;
* 消息循环执行此广播,回调到BoradcastReceiver中的onReceiver()方法中.


#### 广播注册方式

* 静态注册:

```xml

	<receiver 
		android:enabled=["true" | "false"]
		android:exported=["true" | "false"]
		android:icon="drawable resource"
		android:label="string resource"
		android:name="string"
		android:permission="string"
		android:process="string" >
	</receiver>

```

其中属性:

* android:exported: 此广播能否接受其他APP发出的广播,这个属性默认值由intent-filter决定,如果有intent-filter,默认值为true,否则为false(Activity/Service中同样适用).
* android:name: 广播接受者名
* android:permission: 如果设置,具有相同权限的广播发送的广播才能被此接受者接受.
* android:process: 广播接受者所处在的进程,默认为app.

```java

	<receiver android:name=".MyBroadcastReceiver" >
	    <intent-filter>
	        <action android:name="android.net.conn.CONNECTIVITY_CHANGE" />
	    </intent-filter>
	    <intent-filter>
	        <action android:name="android.intent.action.BOOT_COMPLETED" />
	    </intent-filter>
	</receiver>
```

* 动态注册:

	> 动态注册广播对使用的Context要注意,因为广播接受者的存在取决于注册的context,如果是Activity,广播在当前Activity中有效,如果是Application context则与App应用生命周期相同.

	```java
	
		 registerReceiver(BroadcastReceiver receiver, IntentFilter filter)
		
		 registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission, Handler scheduler)
	
	```

#### 广播发送及其广播类型
* 广播的类型:
	* Normal Broadcast 普通广播
	* Ordered Broadcast 有序广播
	* Sticky Broadcast 粘性广播(api21中废弃)
	* System Broadcat 系统广播
	* Local Broadcast APP内部广播
	
* 广播发送的方式:
	* `sendOrderedBroadcast(Intent, String)` 发送有序广播
	* `sendBroadcast(Intent) ` 发送普通广播
	* `LocalBroadcastManager.sendBroadcast ` 发送应用内广播

#### 不同注册方式的广播接收器回调onReceive(context, intent)中的context具体类型

* 对于静态注册的ContextReceiver，回调onReceive(context, intent)中的context具体指的是ReceiverRestrictedContext；

* 对于全局广播的动态注册的ContextReceiver，回调onReceive(context, intent)中的context具体指的是Activity Context；

* 对于通过LocalBroadcastManager动态注册的ContextReceiver，回调onReceive(context, intent)中的context具体指的是Application Context。

> 注：对于LocalBroadcastManager方式发送的应用内广播，只能通过LocalBroadcastManager动态注册的ContextReceiver才有可能接收到（静态注册或其他方式动态注册的ContextReceiver是接收不到的）。

#### 如何在广播接收者onReceiver中进行耗时操作

广播接收者有生命周期,但是很短,当onReceiver()执行完毕,他生命周期就结束了.这次BroadcastRece已经不处于active状态,被系统杀掉的几率很高.如果此时去开线程进行异步超过或者打开Dialog都还没达到相应的效果就被系统杀掉,因为这个Receiver组件在运行,但是只是一个执行完毕的空进程.这情况下可以使用下面方法,来保持Receiver处于active状态,即便系统想要快速结束receive,也可以把操作移动其他线程防止主线程卡顿.

* goAsync()
* JobService()

```java

	public class MyBroadcastReceiver extends BroadcastReceiver {
	    private static final String TAG = "MyBroadcastReceiver";
	
	    @Override
	    public void onReceive(final Context context, final Intent intent) {
	        final PendingResult pendingResult = goAsync();
	        AsyncTask<String, Integer, String> asyncTask = new AsyncTask<String, Integer, String>() {
	            @Override
	            protected String doInBackground(String... params) {
	                StringBuilder sb = new StringBuilder();
	                sb.append("Action: " + intent.getAction() + "\n");
	                sb.append("URI: " + intent.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n");
	                Log.d(TAG, log);
	                // Must call finish() so the BroadcastReceiver can be recycled.
	                pendingResult.finish();
	                return data;
	            }
	        };
	        asyncTask.execute();
	    }
	}


```


#### 广播的安全隐患以及相应的措施

Android中的广播可以跨进程甚至跨App直接通信，且注册是exported对于有intent-filter的情况下默认值是true，由此将可能出现安全隐患如下：

* 1.其他App可能会针对性的发出与当前App intent-filter相匹配的广播，由此导致当前App不断接收到广播并处理；

* 2.其他App可以注册与当前App一致的intent-filter用于接收广播，获取广播具体信息。

无论哪种情形，这些安全隐患都确实是存在的。由此，最常见的增加安全性的方案是：

* 1.对于同一App内部发送和接收广播，将exported属性人为设置成false，使得非本App内部发出的此广播不被接收；

* 2.在广播发送和接收时，都增加上相应的permission，用于权限验证；

* 3.发送广播时，指定特定广播接收器所在的包名，具体是通过intent.setPackage(packageName)指定在，这样此广播将只会发送到此包中的App内与之相匹配的有效广播接收器中。

App应用内广播可以理解成一种局部广播的形式，广播的发送者和接收者都同属于一个App。实际的业务需求中，App应用内广播确实可能需要用到。同时，之所以使用应用内广播时，而不是使用全局广播的形式，更多的考虑到的是Android广播机制中的安全性问题。

相比于全局广播，App应用内广播优势体现在：1.安全性更高；2.更加高效。

为此，Android v4兼容包中给出了封装好的LocalBroadcastManager类，用于统一处理App应用内的广播问题，使用方式上与通常的全局广播几乎相同，只是注册/取消注册广播接收器和发送广播时将主调context变成了LocalBroadcastManager的单一实例。

```java

	//registerReceiver(mBroadcastReceiver, intentFilter);
	//注册应用内广播接收器
	localBroadcastManager = LocalBroadcastManager.getInstance(this);
	localBroadcastManager.registerReceiver(mBroadcastReceiver, intentFilter);
	        
	//unregisterReceiver(mBroadcastReceiver);
	//取消注册应用内广播接收器
	localBroadcastManager.unregisterReceiver(mBroadcastReceiver);
	
	Intent intent = new Intent();
	intent.setAction(BROADCAST_ACTION);
	intent.putExtra("name", "qqyumidi");
	//sendBroadcast(intent);
	//发送应用内广播
	localBroadcastManager.sendBroadcast(intent);

```

### 面试题:

1. 广播中如何进行耗时操作?(Service/Notification)
	* goAsync()
	* JobService
	
2. 广播是否可以开启Activity?

	广播启动activity很可能影响用户体验,何况有时接受者还不止一个,可以考虑使用Notification.

3. 广播来更新界面是否合适?

	如果不是频繁更新刷新,可以是广播来达到效果.对于频繁地刷新动作,不要使用广播,广播发送和接收使用具有一定的代价,他的传输是通过Binder机制实现,那么系统会为广播做进程之间通讯做准备很好性能,另外,广播的接收具有一定的延时性,可能导致卡顿(Binder传输).
	
4. 有时候基于数据安全考虑，我们想发送广播只有自己（本进程）能接收到，那么该如何去做呢？如果不使用LocalBroadcastManger,该怎么实现?

	可能使用Handler，往主线程的消息池（Message Queue）发送消息，只有主线程的Handler可以分发处理它，广播发送的内容是一个Intent对象，我们可以直接用Message封装一下，留一个和sendBroadcast一样的接口。在handleMessage时把Intent对象传递给已注册的Receiver。
