## Wi-Fi对等网络(Wi-Fi Peer-to-Peer)
Wifi对等网络允许具有Android4.0以上系统及配备了合适的硬件的设备通过wifi直接连接而不需要中间热点的支持.使用这些API,你可以查找和连接其他支持wifi-P2P设备,并能快速连接进行通讯,且通讯距离远远超过蓝牙.这对那些多人共享数据的设备及其有用,比例多人连机游戏,照片分享等.

Wifi-P2P APIs包含如下几个方面:

* WifiP2pManager类中定义了各种方法(Methods)允许你去查找,请求和连接对等设备.
* 当调用WifiP2pManger中的方法时,每个方法都接收一个特定的监听器作为参数,而监听器(Listeners)会通知你调用WifiP2pManager中的方法是否成功.
* 通知用户由WIFI-P2P框架检测到的具体事件的意图(Intents),诸如连接断开,发现新的对等设备.

你会经常同时使用APIs中的这三个主要组件的相关功能,比如,你可以使用`discoverPeers()`中的监听器`WifiP2pManager.ActionListener`去执行回调,以便在`ActionListener.onSuccess()`和`ActionListener.onFailure()`时得到通知.如果`discoverPeers()`方法发现对等设备列表发生变化,会发出`WIFI-P2P-PEERS-CHANGED-ACTION`意图的广播.


### API总预览

WifiP2pManager类为用户和设备上的wifi硬件交互提供了各种方法,做诸如查找,连接对等设备.下面这些行为(actions)是可执行的:

表一 Wi-FiP2P方法
| 方法 | 释意 | 
| ------------- |-------------|
| initalize()|注册wifi框架,这方法必须在其他wifi-p2p方法前调用 |
| connect() | 开始让具有特定配置的设备开始点对点的连接(peer to peer) |
| cancelConncet() | 取消所有进行中的点对点的群体协商|
| requestConnectInfo()|请求设备的连接信息|
| createGroup()|使用当前设备作为组所有者创建一个点对点的组|
| removeGroup()|移除当前点对点的组群|
|requestGroupInfo()|请求点对点组群信息|
|discoverPeers()|初始化点的查找|
|requestPeers()|请求当前已查到到的点群表里|

WifiP2pManager方法能让你传递一个监听器,以便Wi-Fi框架能够通知
你activity调用者的状态,这些可用的监听器接口和相应的WifiP2pManager方法调用监听器如下表描述:

表一 Wi-FiP2P监听器

| 监听接口 | 相关行为(actions) | 
| ------------- |-------------|
|WifiP2pManager.ActionListener	|connect(), cancelConnect(), createGroup(), removeGroup(), and discoverPeers()|
|WifiP2pManager.ChannelListener	|initialize()|
|WifiP2pManager.ConnectionInfoListener|	requestConnectInfo()|
|WifiP2pManager.GroupInfoListener|	requestGroupInfo()|
|WifiP2pManager.PeerListListener|	requestPeers()|

Wi-Fi P2P APIs定义了某些Wi-FiP2P事件发生发送广播的意图(Intents),诸如当一个新的peer被发现或者一个设备的wifi状态改变时发送的广播意图.你可以在你的应用中注册广播接收器去接收这些广播意图,这些意图如下表:

表三: Wi-FiP2P意图:
| 意图| 描述 | 
| ------------- |-------------|
|WIFI_P2P_CONNECTION_CHANGED_ACTION|当设备wifi连接状态改变发送的广播|.
|WIFI_P2P_PEERS_CHANGED_ACTION|调用discoverPeers()时发送的广播.如果在应用中处理这意图,你通常会调用requsetPeers()获取peers的更新列表|
|WIFI_P2P_STATE_CHANGED_ACTION|	当WifiP2P可用或者不可用时发送的广播|
|WIFI_P2P_THIS_DEVICE_CHANGED_ACTION|当设备的详细信息改变,如设备的名称,发出的广播|

### 为Wi-Fi P2P意图创建广播接收器
广播接收器可以接收来自android系统的发出的广播意图,以便你的应用程序能够为自己比较感兴趣的事件作出响应. 创建一个广播接受者处理wifi-p2p意图最基本的步骤如下:
* 1.创建一个类继承BroadcastReceiver类.对于该类的构造函数,最好传递wifip2pManager和WifiP2pManager.Channel两个参数,而且广播接受者会注册在需要的activity中. 如果有需要,这允许广播接受者发送更新信息,访问wifi硬件以及通讯频道.
* 2. 在广播接受者中,在onReceive()方法可以检查你所有感兴趣的意图.实现任何必要的行为取决于所接受的意图.比如,如果广播接受者接受了WIFI_P2P_PEERS_CHANGED_ACTION意图,你便可以使用requsetPeers()方法获得当前已被查到的peers列表.

下面的代码会向你展示如何创建一个典型的广播接受者.这个广播接受者将一个WifiP2pManager对象和一个activity对象作为参数,并当广播接受者接收一个意图时使用这两个类去实现需要的行为(carry out needed
 actions).

		/**
		 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
		 */
		public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
		
		    private WifiP2pManager mManager;
		    private Channel mChannel;
		    private MyWiFiActivity mActivity;
		
		    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel,
		            MyWifiActivity activity) {
		        super();
		        this.mManager = manager;
		        this.mChannel = channel;
		        this.mActivity = activity;
		    }
		
		    @Override
		    public void onReceive(Context context, Intent intent) {
		        String action = intent.getAction();
		
		        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
		            // Check to see if Wi-Fi is enabled and notify appropriate activity
		        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
		            // Call WifiP2pManager.requestPeers() to get a list of current peers
		        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
		            // Respond to new connection or disconnections
		        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
		            // Respond to this device's wifi state changing
		        }
		    }
		}
		
	
### 创建一个Wi-Fi P2P应用
创建一个wifip2p应用包含创建和注册一个广播接受者,	查找peers,连接peer,以及和一个peer传输数据.下面几部分将会描述如何做:

#### 初始化设置
在使用wifi-p2p APIs前,你必须要保证你的应用是可以访问wifi硬件,并且支持wifi-p2p协议.如果支持wifi-p2p,你就可以获得一个WifiP2pManager的实例,创建和注册你的广播接受者,并使用wifi-p2p APIs.

* 1.使用设备的wifi硬件的请求权限,以及在清单文件中声明应用所需正确的最小sdk版本号,如:

		<uses-sdk android:minSdkVersion="14" />
		<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
		<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
		<uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
		<uses-permission android:name="android.permission.INTERNET" />
		<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

* 2.检查是否支持wifi-P2p.当接收到WIFI_P2P_STATE_CHANGED_ACTION意图时,最好的检查地方是在广播接受者中.通知activity的wifi-p2p状态,并有相应的反应.

		@Override
		public void onReceive(Context context, Intent intent) {
		    ...
		    String action = intent.getAction();
		    if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
		        int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
		        if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
		            // Wifi P2P is enabled
		        } else {
		            // Wi-Fi P2P is not enabled
		        }
		    }
		    ...
		}

* 3.在activity的onCreate()方法中,获取一个WifiP2pManager的实例,并调用initialize()方法,使用wifi-p2p框架去注册你的应用.这个方法会返回一个WifiP2pManager.Channel,这个频道会经常让你的应用去连接wifiP2p框架.你应该使用WifiP2pManager和WifiP2pManager实例以及activity的引用去创建一个广播接受者的实例.这会允许你的广播接收者通知activity有兴趣的事件以及相应的更新.如果有必要,这也会让你操作设备的wifi状态.

		WifiP2pManager mManager;
		Channel mChannel;
		BroadcastReceiver mReceiver;
		...
		@Override
		protected void onCreate(Bundle savedInstanceState){
		    ...
		    mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		    mChannel = mManager.initialize(this, getMainLooper(), null);
		    mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
		    ...
		}

* 4.创建一个意图过滤器,并且添加一些广播接受者所检查的意图:

		IntentFilter mIntentFilter;
		...
		@Override
		protected void onCreate(Bundle savedInstanceState){
		    ...
		    mIntentFilter = new IntentFilter();
		    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
		    ...
		}

* 5.在activity的onResume()方法注册广播接受者,并在onPause()中注销广播接收者.

		IntentFilter mIntentFilter;
		...
		@Override
		protected void onCreate(Bundle savedInstanceState){
		    ...
		    mIntentFilter = new IntentFilter();
		    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		    mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
	    ...
		}
当你已经获得WifiP2pManager.Channel并且创建了一个广播接受者,你的应用便可以调用Wi-Fi P2P方法,并能够接收到WiFi P2P意图.

你现在可以调用wifiP2pManger中的方法并使用wifi p2p特征去实现的你的应用.下一个部分将介绍如何执行一些诸如查找和连接的常见行为.

### 查找对等设备(Discovering peers)

查找可连接使用的对等设备,调用discoverPeers()方法去检测有效范围内的可用对等设备.这个方法是异步的,如果你创建了WifiP2pManager.ActionListener,便在应用中便可以使用onsuccess()或者onFailure()来接收成功或者失败的信息.onSuccess()方法会通知查找成功,但是不会提供任何有关查找的设备信息,即便有的话.

	mManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
    	@Override
	    public void onSuccess() {
	        ...
	    }
	
	    @Override
	    public void onFailure(int reasonCode) {
	        ...
	    }
	});

如果查找过程是成功的并且检测了对等设备,系统会发送 WIFI_P2P_PEERS_CHANGED_ACTION意图的广播,你可以在广播接收者中监听对等设备列表.当你的应用接收到WIFI_P2P_PEERS_CHANGED_ACTION意图,你便可以调用requestPeers()方法获取已查找到的对等设备列表.创建代码如下:
	
	PeerListListener myPeerListListener;
	...
	if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
	
	    // request available peers from the wifi p2p manager. This is an
	    // asynchronous call and the calling activity is notified with a
	    // callback on PeerListListener.onPeersAvailable()
	    if (mManager != null) {
	        mManager.requestPeers(mChannel, myPeerListListener);
	    }
	}

requestPeers()方法也是异步的,当对等设备列表可用时并调用onPeersAvaiable()方法通知activity,这个方法定义在WifiP2pManager.PeerListListener接口中.onPeersAvailable()方法提供了WifiP2pDeviceList,你可以迭代遍历它去查找想要连接的那个设备.

### 连接对等设备(Connecting to peers)

当你获取了一系列对等设备并找出了你想要连接的设备,你可调用connect()方法连接这个设备.这个方法需要一个WifiP2pConfig对象,这个对象包含了你所想要连接的设备的信息. 你可调用WifiP2pManager.AcitonListener接口去监听连接成功还是失败.如下代码将告诉你如何和你期望的设备建立一个连接:
	
	//obtain a peer from the WifiP2pDeviceList
	WifiP2pDevice device;
	WifiP2pConfig config = new WifiP2pConfig();
	config.deviceAddress = device.deviceAddress;
	mManager.connect(mChannel, config, new ActionListener() {
	
	    @Override
	    public void onSuccess() {
	        //success logic
	    }
	
	    @Override
	    public void onFailure(int reason) {
	        //failure logic
	    }
	});

### 传输数据(Transferring data)
一旦连接建立,便可以通socket进行设备间的数据传输,数据传输的基本步骤如下:

1. 创建一个ServerSocket. 这个socket会在后台线程中阻塞并等待连接指定了端口号的客户端.
2. 创建一个Socket客户端,这个客户端使用IP地址和服务端Socket的端口号连接服务端设备.
3. 客户端向服务端发送数据.当客户端成功的连接上了服务端socket,便可以使用字节流向服务端发送数据.
4. 服务端socket使用accept()等待客户端的连接.这个方法阻塞直到客户端连接,所有这个方法也是在另一个线程中调用.连接成功后,服务端会接收来自客户端的数据.利用这些数据,可以实施任何行为(actions),诸如将数据存储到文件或者供用户使用.
5. 下面是一个修改后的WIFI P2P例子,将向你展会如何创建客户端和服务端socket通讯以及利用s服务(service)传输JPEG格式的图片.

		public static class FileServerAsyncTask extends AsyncTask {
		
		    private Context context;
		    private TextView statusText;
		
		    public FileServerAsyncTask(Context context, View statusText) {
		        this.context = context;
		        this.statusText = (TextView) statusText;
		    }
		
		    @Override
		    protected String doInBackground(Void... params) {
		        try {
		
		            /**
		             * Create a server socket and wait for client connections. This
		             * call blocks until a connection is accepted from a client
		             */
		            ServerSocket serverSocket = new ServerSocket(8888);
		            Socket client = serverSocket.accept();
		
		            /**
		             * If this code is reached, a client has connected and transferred data
		             * Save the input stream from the client as a JPEG file
		             */
		            final File f = new File(Environment.getExternalStorageDirectory() + "/"
		                    + context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
		                    + ".jpg");
		
		            File dirs = new File(f.getParent());
		            if (!dirs.exists())
		                dirs.mkdirs();
		            f.createNewFile();
		            InputStream inputstream = client.getInputStream();
		            copyFile(inputstream, new FileOutputStream(f));
		            serverSocket.close();
		            return f.getAbsolutePath();
		        } catch (IOException e) {
		            Log.e(WiFiDirectActivity.TAG, e.getMessage());
		            return null;
		        }
		    }
		
		    /**
		     * Start activity that can handle the JPEG image
		     */
		    @Override
		    protected void onPostExecute(String result) {
		        if (result != null) {
		            statusText.setText("File copied - " + result);
		            Intent intent = new Intent();
		            intent.setAction(android.content.Intent.ACTION_VIEW);
		            intent.setDataAndType(Uri.parse("file://" + result), "image/*");
		            context.startActivity(intent);
		        }
		    }
		}

客户端socket连接服务端socket并传输数据,这个例子将传输设备本地文件中的JPEG格式文件.

		Context context = this.getApplicationContext();
		String host;
		int port;
		int len;
		Socket socket = new Socket();
		byte buf[]  = new byte[1024];
		...
		try {
		    /**
		     * Create a client socket with the host,
		     * port, and timeout information.
		     */
		    socket.bind(null);
		    socket.connect((new InetSocketAddress(host, port)), 500);
		
		    /**
		     * Create a byte stream from a JPEG file and pipe it to the output stream
		     * of the socket. This data will be retrieved by the server device.
		     */
		    OutputStream outputStream = socket.getOutputStream();
		    ContentResolver cr = context.getContentResolver();
		    InputStream inputStream = null;
		    inputStream = cr.openInputStream(Uri.parse("path/to/picture.jpg"));
		    while ((len = inputStream.read(buf)) != -1) {
		        outputStream.write(buf, 0, len);
		    }
		    outputStream.close();
		    inputStream.close();
		} catch (FileNotFoundException e) {
		    //catch logic
		} catch (IOException e) {
		    //catch logic
		}
		
		/**
		 * Clean up any open sockets when done
		 * transferring or if an exception occurred.
		 */
		finally {
		    if (socket != null) {
		        if (socket.isConnected()) {
		            try {
		                socket.close();
		            } catch (IOException e) {
		                //catch logic
		            }
		        }
		    }
		}