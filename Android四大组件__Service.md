## Service 服务
### 什么是服务:
> `A service is not a separate process and A service is not thread; A Service is an application component that can perform long-running opreations in the background and doesnot provider a userinterface.`

* 服务是一个无界面化的应用程序组件
* 服务一般用于后台进行耗时操作

### Service的生命周期:
![](img/service_lifecycle.png)
#### startService方式开启服务:
* `startService(Intent service)`，通过intent值来指定启动哪个Service，可以直接指定目标Service的名，也可以通过Intent的action属性来启动设置了相应action属性的Service，使用这种方式启动的Service，当启动它的Activity被销毁时，是不会影响到它的运行的，这时它仍然继续在后台运行它的工作。直至调用`StopService（Intent service）`方法时或者是当系统资源非常紧缺时，这个服务才会调用onDestory()方法停止运行。所以这种Service一般可以用做，处理一些耗时的工作。
* 四大组件默认都是和activity运行在同一个主线程中的，那就是说activity通过startservice方法启动一个服务后，被启动的服务和activity都是在同一个线程中的。所以当我主动销毁了这个activity，但是他所在的线程还是存在的，只不过是这个activity他所占用的资源被释放掉了，这个activity所在的主线程只有当android内存不足才会被杀死掉，否则一般的情况下这个activity所在的应用程序的线程始终存在，也就是这个activity所启动的服务也会一直运行下去。
#####Service
	public class LifeService extends Service {
	    private static final String TAG = "LifeService";

	    @Override
	    public void onCreate() {
	        Log.d(TAG, "服务------onCreate");
	        super.onCreate();
	
	    }
	    @Override
	    public int onStartCommand(Intent intent, int flags, int startId) {
	        Log.d(TAG, "服务------onStartCommand");
	        return super.onStartCommand(intent, flags, startId);
	    }
	    @Override
	    public void onDestroy() {
	        Log.d(TAG, "服务------onDestroy");
	        super.onDestroy();
	    }
	}
##### Activity	
	public class MainActivity extends AppCompatActivity {
	
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	    }
	
	    public void startService(View view) {
	        Intent intent = new Intent(this, LifeService.class);
	        startService(intent);
	    }
	
	    public void stopService(View view) {
	
	        Intent intent = new Intent(this, LifeService.class);
	        stopService(intent);
	    }
	}

##### 运行结果:
![](img/start_service_lifecycle.png)
> Service 可以被开启多次,但是只会创建一次.

#### bindService方式开启服务
* bindService开启的服务,可以调用到服务中的方法.
* 启动的LifeService是和MainActivity在同一个进程里的，因为在注册服务时，没有配置它的android:process = "xxxx" 属性。
##### Service
	public class LifeService extends Service {
	    private static final String TAG = "LifeService";

	    @Override
	    public void onCreate() {
	        Log.d(TAG, "服务------onCreate");
	        super.onCreate();
	    }
	
	    @Override
	    public int onStartCommand(Intent intent, int flags, int startId) {
	        Log.d(TAG, "服务------onStartCommand");
	        return super.onStartCommand(intent, flags, startId);
	    }
	
	    @Override
	    public void onDestroy() {
	        Log.d(TAG, "服务------onDestroy");
	        super.onDestroy();
	    }
	
	    @Override
	    public boolean onUnbind(Intent intent) {
	        Log.d(TAG, "服务------onUnbind");
	        return super.onUnbind(intent);
	    }
	
	    @Nullable
	    @Override
	    public IBinder onBind(Intent intent) {
	        Log.d(TAG, "服务------onBind");
	        return new Mybind();
	    }
	
	    public class Mybind extends Binder {
	
	        public void callMethodInService() {
	            methodInService();
	        }
	
	    }
	    
	    public void methodInService() {
	        Toast.makeText(this, "服务里的方法被调用了", Toast.LENGTH_SHORT).show();
	        Log.d(TAG, "服务里的方法被调用了");
	    }	
	}
##### Activity
	public class MainActivity extends AppCompatActivity {
	
	    private ServiceConnection conn;
	    private LifeService.Mybind mBinder;
	
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	    }
		
		//绑定服务
	    public void bindService(View view) {
	        Intent intent = new Intent(this, LifeService.class);
	        conn = new MyServiceConnection();
	        bindService(intent, conn, BIND_AUTO_CREATE);
	    }
	
	    private class MyServiceConnection implements ServiceConnection {
	
	        @Override
	        public void onServiceConnected(ComponentName name, IBinder service) {
	            mBinder = (LifeService.Mybind) service;
	
	        }
	
	        @Override
	        public void onServiceDisconnected(ComponentName name) {
	
	        }
	    }
	
		//解绑服务
	    public void unbindService(View view) {
	        unbindService(conn);
	    }
		//调用服务里的方法
	    public void callMethodInService(View view) {
	        mBinder.callMethodInService();
	    }
	}

##### 运行结果
![](img/bind_service_1.png)

出现一个有意思的现象: 当解绑服务后,service已经onDestroy(),但是还是能调用服务中的方法.运行图如下:
![](img/bind_service_2.png)
> 服务虽然是onDestroy了,但是MainActivity中还保留LifeService.Binder的引用,服务中的方法也保留了Service自身的引用,所以即便是Service onDestroy()了,但是还是可以调用到服务中的方法.

####混合方式开启服务
* startService开启服务: 服务能在后台长期运行,不能调用服务中方法.
* bindService开启服务: 能调用服务中的方法,但是不能在后台长期运行.
* 混合方式开启服务: 保证服务后台长期运行, 还能调用服务中的方法.
![](img/service_binding_tree_lifecycle.png)
##### Service
	public class LifeService extends Service {
	    private static final String TAG = "LifeService";
	
	
	    @Override
	    public void onCreate() {
	        Log.d(TAG, "服务------onCreate");
	        super.onCreate();
	
	    }
	
	    @Override
	    public int onStartCommand(Intent intent, int flags, int startId) {
	        Log.d(TAG, "服务------onStartCommand");
	        return super.onStartCommand(intent, flags, startId);
	    }
	
	    @Override
	    public void onDestroy() {
	        Log.d(TAG, "服务------onDestroy");
	        super.onDestroy();
	    }
	
	    @Override
	    public boolean onUnbind(Intent intent) {
	        Log.d(TAG, "服务------onUnbind");
	        return super.onUnbind(intent);
	    }
	
	    @Nullable
	    @Override
	    public IBinder onBind(Intent intent) {
	        Log.d(TAG, "服务------onBind");
	        return new Mybind();
	    }
	
	    public class Mybind extends Binder {
	
	        public void callMethodInService() {
	            methodInService();
	        }
	
	    }
	
	
	    public void methodInService() {
	        Toast.makeText(this, "服务里的方法被调用了", Toast.LENGTH_SHORT).show();
	        Log.d(TAG, "服务里的方法被调用了");
	    }
	
	}

##### Activity
	public class MainActivity extends AppCompatActivity {
	    private ServiceConnection conn;
	    private LifeService.Mybind mBinder;
	
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);
	    }
		//开启服务
	    public void startService(View view) {
	        Intent intent = new Intent(this, LifeService.class);
	        startService(intent);
	    }
		//停止服务
	    public void stopService(View view) {
	        Intent intent = new Intent(this, LifeService.class);
	        stopService(intent);
	    }
	
		//绑定服务
	    public void bindService(View view) {
	        Intent intent = new Intent(this, LifeService.class);
	        conn = new MyServiceConnection();
	        bindService(intent, conn, BIND_AUTO_CREATE);
	    }
		//解绑服务
	    public void unbindService(View view) {
	        unbindService(conn);
	    }
	
	    private class MyServiceConnection implements ServiceConnection {
	
	        @Override
	        public void onServiceConnected(ComponentName name, IBinder service) {
	            mBinder = (LifeService.Mybind) service;
	        }
	
	        @Override
	        public void onServiceDisconnected(ComponentName name) {
	
	        }
	    }
		//调用服务中的方法
	    public void callMethodInService(View view) {
	        mBinder.callMethodInService();
	    }
	}
	
##### 运行结果
![](img/mix_start_service.png)

注意几点: 

*  以startService方式开启的服务, 解绑服务,并不能使服务onDestroy
*  `IBinder: the communication channel to the service,may return null if clients not connect to services.`
*  `unlike other application components, calls on to the IBinder interface returned here may not happen on the main thread of the process`
 	
#### 接口
> 利用接口屏蔽方法内部实现的细节, 只暴露需要暴露的方法.
##### IService
	public interface IService {
	    void callMethodInService();
	}
##### Service
	private class Mybind extends Binder implements IService {
	    public void callMethodInService() {
	            methodInService();
	    }
	
	}
##### Activity
	 private class MyServiceConnection implements ServiceConnection {
	
	     @Override
	     public void onServiceConnected(ComponentName name, IBinder service) {
			//转化Iservice对象
	         mIService = (IService) service;
	     }
	      @Override
	      public void onServiceDisconnected(ComponentName name) {
	
	      }
	 }
    
### Service组件的三种通讯方式:
* `startService`
* `bindService`
* `AIDL(android interface definition language)`

### Service执行耗时操作
#### IntentService


### service服务运行在ForeGground