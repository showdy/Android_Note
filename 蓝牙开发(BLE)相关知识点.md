## 低功耗蓝牙(Bluetooth Low Energy)
> android4.3(API=18)引入BLE低功耗蓝牙,并且提供了相应的API供应用程序**搜索蓝牙设备**,**查询服务**,**读写特性**.与经典蓝牙相比,BLE的显著特点是低功耗, 使得android应用可以和具有低功耗要求的蓝牙设备进行通讯,如传感器,心率检测器,健身设备等等.

#### 关键术语和概念(Key Terms and Concerpt)
* Generic Attribute Profile(GATT): 通用属性协议
	> GATT配置文件是一个通用的规范, 用于在BLE链路上发送和接收被称为"属性"的数据块.目前所有的BLE应用都是基于GATT.
	* 蓝牙SIG规定了许多低功耗设备的配置文件.配置文件就是设备如何在特定应用中运转的规格说明.注意一个设备可以实现多个配置文件,例如:一个设备可以包含心率监测器和电量检测器.

* Attribute Protocol(ATT) 属性协议
	> GATT是建立在ATT协议的基础上的.ATT对在BLE设备上运行做了优化,为此他使用了尽可能少的字节,每个属性都通过一个唯一的统一标识符(UUID)来标识,每个String类型的UUID使用是128bit标准格式,属性通过ATT被格式化为characteristics 和services
	
* Characteristic 特性
	> 一个CXharacteristic包含一个单一变量和0-n个用来描述characteristic变量的descriptor,characteristic可以被认为是一个类型, 类似于类.
* Descriptor 描述
	> Descriptor用来描述characteristic变量的属性.例如,一个descriptor可以规定一个可读的描述,或者一个characteristic变量可接受的范围,或者一个characteristic变量特定的测量单位.
	
* Service 服务
	> service是characteristic的集合.例如,你可能有一个叫Heart rate minitord的service,他包含了很多characteristic,如 heart rate measurement等等.可在[bluetooth.org](https://www.bluetooth.com/specifications/adopted-specifications)找到一个目前支持基于AGTT的配置文件和服务列表

#### 角色和责任(Roles & Responsiblity)
* 一下是android设备和BLE设备交互时的角色和责任
	* 中央 vs 外设: 使用与BLE连接本身.中央设备扫描,寻找广播,外围设备发出广播
	* GATT服务端 vs GATT客户端: 决定了两个设备在建立连接后如何交互
* 为了方便理解,想象你有一个用于活动跟踪BLE设备,手机支持中央角色,后动跟踪器支持外围活动(为了建立BLE连接你需要注意两件事,只支持外围设备的两方或者只支持中央设备的两方不能互相通讯.
* 当手机和运动跟踪器建立连接后,他们开始向另一方传输GATT数据.哪一方作为服务器取决于他们传输数据的种类.例如,如果运动跟踪器想向手机报告传感器数据,运动跟踪器就是服务器.如果运动跟踪器更新来自手机的数据,手机作为服务器.

#### BLE权限(BLE Permissions)
* 为了在应用中使用Bluetooth特性,必须要声明[BLUETOOTH](http://androiddoc.qiniudn.com/reference/android/Manifest.permission.html#BLUETOOTH)权限.你需要这个权限区执行任何蓝牙通讯,例如请求连接,接收连接,传输数据.
* 如果你想要App应用初始化设备搜索或者手动操作蓝牙设置,你必须声明BLUETOOTH_ADMIN权限.注意:如何要使用BLUETOOTH权限,比先声明BLUETOOTH权限.
* 在manifest.xml文件中声明权限:

		<uses-permission android:name="android.permission.BLUETOOTH"/>
		<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>

* 如果你想要声明你的APP应用仅仅支持具有BLE功能的设备,请声明下面权限:
	
		<uses-feature android:name="android.hardware.bluetooth_le" android:required="true"/>
* 然而,如果想要你的APP对不支持BLE的设备也有效,必须引入该元素在manifest文件中,但是`required=false.`在运行时可以通过`PackagerManager.hasSystemFeature()`来判断BLE是否可用.

		// Use this check to determine whether BLE is supported on the device. Then
		// you can selectively disable BLE-related features.
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
    		Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
    		finish();
	}

#### 启动BLE(Setting up BLE)
* 在使用BLE通讯前,请确认你的APP是否支持BLE,并且要保证蓝牙已打开(enable).注意:如果`<uses-feature....>`设定为false,此处检查是必须的.
* 如果APP不支持BLE,那就不能使用BLE任何特性.如果支持BLE,但是没有打开蓝牙,你可以请求在不退出应用打开蓝牙.这样,蓝牙就是通过BluetoothAdapter分2步启动.
	
* 1.获得BluetoothAdapter:

	BluetoothAdapter在任何Bluetooth Activity中都是必备的.BluetoothAdapter代表了设备本身的适配器.整个系统有一个适配器,你的应用可以BluetoothAdapter和整个应用交互.下面代码显示了如何获取BluetoothAdapter
	> 此方法使用getSystemService()来获得一个BluetoothMangager的实例, BluetoothManager实例能够用来获取一个BluetoothAdapter的一个实例.在andriod4.3引入BluetoothManager.

		// Initializes Bluetooth adapter.
		final BluetoothManager bluetoothManager =(BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();

* 2.打开蓝牙设备: 

	下一步,你必须保证Bluetooth是开启的.调用isEnable()可以检查当前蓝牙是否开启.如果返回false,表示蓝牙为开启.下面代码用于检测蓝牙是否开启,如果没有,会显示一个错误信息让用户去开启蓝牙.
	
		private BluetoothAdapter mBluetoothAdapter;
		...
		// Ensures Bluetooth is available on the device and it is enabled. If not,
		// displays a dialog requesting user permission to enable Bluetooth.
		if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
	    	Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	    	startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}

#### 搜索BLE设备(Finding BLE Device)	
* 使用startLeScan()搜索BLE设备,此方法将`BluetoothAdapter.LeScanCallback`作为一个参数,你必须实现该回调接口,因为接口决定着扫描结果是怎么样返回的.扫描很耗电,应当遵守如下准则:
	* 找到设备立即关掉扫描功能
	* 不要反复扫描,并且设置扫描时间限制,先前可用的设备可能现在已经移出扫描范围,继续扫描可能耗干电池.
* 下面代码是如何开启和关闭扫描：
		
		//Activity for scanning and displaying available BLE devices.
		public class DeviceScanActivity extends ListActivity {
		    private BluetoothAdapter mBluetoothAdapter;
		    private boolean mScanning;
		    private Handler mHandler;
		
		    // Stops scanning after 10 seconds.
		    private static final long SCAN_PERIOD = 10000;
		    ...
		    private void scanLeDevice(final boolean enable) {
		        if (enable) {
		            // Stops scanning after a pre-defined scan period.
		            mHandler.postDelayed(new Runnable() {
		                @Override
		                public void run() {
		                    mScanning = false;
		                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
		                }
		            }, SCAN_PERIOD);
		
		            mScanning = true;
		            mBluetoothAdapter.startLeScan(mLeScanCallback);
		        } else {
		            mScanning = false;
		            mBluetoothAdapter.stopLeScan(mLeScanCallback);
		        }
		        ...
		    }
		...
		}

* 如果你想扫描特定型号的外设,可用调用`startLeScan(UUID[],BluetoothAdapter.LeScanCallback)`,需要提供你的APP支持的`GATT services`的`UUID`对象数组.下面是`BluetoothAdapter.LeScanCallback`实现实例,用来传递BLE扫描结果.

		private LeDeviceListAdapter mLeDeviceListAdapter;
		...
		// Device scan callback.
		private BluetoothAdapter.LeScanCallback mLeScanCallback =
		        new BluetoothAdapter.LeScanCallback() {
		    @Override
		    public void onLeScan(final BluetoothDevice device, int rssi,
		            byte[] scanRecord) {
		        runOnUiThread(new Runnable() {
		           @Override
		           public void run() {
		               mLeDeviceListAdapter.addDevice(device);
		               mLeDeviceListAdapter.notifyDataSetChanged();
		           }
		       });
		   }
		};
	> 注意: 扫描传统蓝牙设备和BLE蓝牙设备不能同时进行,同一时间只能扫描传统蓝牙设备或者BLE设备

#### 连接GATT服务端(`Connecting to a GATT Server`)
* 和BLE设备交互第一步就是连接BLE设备,特别的, 是连接设备上的GATT服务端.连接BLE设备上的GATT服务端,要使用connectGatt()方法.这个方法需要三个参数: Context对象,autoConnect(一旦BLE可用时是否立即连接BLE设备),以及BluetoothGattCallback接口.
	
		mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
* 连接GATT服务端时,BLE设备作为主机,并返回一个`BluetoothGatt`实例,使用这个实例可以进行GATT客户端操作. 调用者(Andorid应用)是GATT客户端. `BluetoothGattCallback`接口用来传递结果给客户端,例如连接状态,以及任何一步GATT客户端操作.
* 在这个例子中,BLE app应用提供了一个activity(`DeviceControlActivity`)去连接,展示数据,显示设备所支持的GATT服务端(`GATT services`)和特性(`characteristic`).基于用户的输入信息,这个activity和叫做`BluetoothLeService`的服务端进行通讯,这个服务端通过`android BLE API和BLE`设备交互.
	
		// A service that interacts with the BLE device via the Android BLE API.
		public class BluetoothLeService extends Service {
		    private final static String TAG = BluetoothLeService.class.getSimpleName();
		
		    private BluetoothManager mBluetoothManager;
		    private BluetoothAdapter mBluetoothAdapter;
		    private String mBluetoothDeviceAddress;
		    private BluetoothGatt mBluetoothGatt;
		    private int mConnectionState = STATE_DISCONNECTED;
		
		    private static final int STATE_DISCONNECTED = 0;
		    private static final int STATE_CONNECTING = 1;
		    private static final int STATE_CONNECTED = 2;
		
		    public final static String ACTION_GATT_CONNECTED =
		            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
		    public final static String ACTION_GATT_DISCONNECTED =
		            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
		    public final static String ACTION_GATT_SERVICES_DISCOVERED =
		            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
		    public final static String ACTION_DATA_AVAILABLE =
		            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
		    public final static String EXTRA_DATA =
		            "com.example.bluetooth.le.EXTRA_DATA";
		
		    public final static UUID UUID_HEART_RATE_MEASUREMENT =
		            UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);
		
		    // Various callback methods defined by the BLE API.
		    private final BluetoothGattCallback mGattCallback =
		            new BluetoothGattCallback() {
		        @Override
		        public void onConnectionStateChange(BluetoothGatt gatt, int status,
		                int newState) {
		            String intentAction;
		            if (newState == BluetoothProfile.STATE_CONNECTED) {
		                intentAction = ACTION_GATT_CONNECTED;
		                mConnectionState = STATE_CONNECTED;
		                broadcastUpdate(intentAction);
		                Log.i(TAG, "Connected to GATT server.");
		                Log.i(TAG, "Attempting to start service discovery:" +
		                        mBluetoothGatt.discoverServices());
		
		            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
		                intentAction = ACTION_GATT_DISCONNECTED;
		                mConnectionState = STATE_DISCONNECTED;
		                Log.i(TAG, "Disconnected from GATT server.");
		                broadcastUpdate(intentAction);
		            }
		        }
		
		        @Override
		        // New services discovered
		        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
		            if (status == BluetoothGatt.GATT_SUCCESS) {
		                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
		            } else {
		                Log.w(TAG, "onServicesDiscovered received: " + status);
		            }
		        }
		
		        @Override
		        // Result of a characteristic read operation
		        public void onCharacteristicRead(BluetoothGatt gatt,
		                BluetoothGattCharacteristic characteristic,
		                int status) {
		            if (status == BluetoothGatt.GATT_SUCCESS) {
		                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
		            }
		        }
		     ...
		    };
		...
		}
* 当一个特定的回调被触发的时候，它会调用相应的broadcastUpdate()辅助方法并且传递给它一个action。注意在该部分中的数据解析遵守蓝牙心率测量规范。

		private void broadcastUpdate(final String action) {
		    final Intent intent = new Intent(action);
		    sendBroadcast(intent);
		}
		
		private void broadcastUpdate(final String action,
		                             final BluetoothGattCharacteristic characteristic) {
		    final Intent intent = new Intent(action);
		
		    // This is special handling for the Heart Rate Measurement profile. Data
		    // parsing is carried out as per profile specifications.
		    if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
		        int flag = characteristic.getProperties();
		        int format = -1;
		        if ((flag & 0x01) != 0) {
		            format = BluetoothGattCharacteristic.FORMAT_UINT16;
		            Log.d(TAG, "Heart rate format UINT16.");
		        } else {
		            format = BluetoothGattCharacteristic.FORMAT_UINT8;
		            Log.d(TAG, "Heart rate format UINT8.");
		        }
		        final int heartRate = characteristic.getIntValue(format, 1);
		        Log.d(TAG, String.format("Received heart rate: %d", heartRate));
		        intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
		    } else {
		        // For all other profiles, writes the data formatted in HEX.
		        final byte[] data = characteristic.getValue();
		        if (data != null && data.length > 0) {
		            final StringBuilder stringBuilder = new StringBuilder(data.length);
		            for(byte byteChar : data)
		                stringBuilder.append(String.format("%02X ", byteChar));
		            intent.putExtra(EXTRA_DATA, new String(data) + "\n" +
		                    stringBuilder.toString());
		        }
		    }
		    sendBroadcast(intent);
		}
* 返回`DeviceControlActivity`,这些事件被`BroadcastReceiver`处理.
	
		// Handles various events fired by the Service.
		// ACTION_GATT_CONNECTED: connected to a GATT server.
		// ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
		// ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
		// ACTION_DATA_AVAILABLE: received data from the device. This can be a
		// result of read or notification operations.
		private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
		    @Override
		    public void onReceive(Context context, Intent intent) {
		        final String action = intent.getAction();
		        if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
		            mConnected = true;
		            updateConnectionState(R.string.connected);
		            invalidateOptionsMenu();
		        } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
		            mConnected = false;
		            updateConnectionState(R.string.disconnected);
		            invalidateOptionsMenu();
		            clearUI();
		        } else if (BluetoothLeService.
		                ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
		            // Show all the supported services and characteristics on the
		            // user interface.
		            displayGattServices(mBluetoothLeService.getSupportedGattServices());
		        } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
		            displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
		        }
		    }
		};

#### 读取BLE属性(Reading BLE Attributes)
* 一旦你的android app连接上GATT服务端,并且发现服务(services),就可以读取和写入属性(attributes).例如,这段代码迭代服务端的services和characteristics并将他们展示在UI上.

		public class DeviceControlActivity extends Activity {
		    ...
		    // Demonstrates how to iterate through the supported GATT
		    // Services/Characteristics.
		    // In this sample, we populate the data structure that is bound to the
		    // ExpandableListView on the UI.
		    private void displayGattServices(List<BluetoothGattService> gattServices) {
		        if (gattServices == null) return;
		        String uuid = null;
		        String unknownServiceString = getResources().
		                getString(R.string.unknown_service);
		        String unknownCharaString = getResources().
		                getString(R.string.unknown_characteristic);
		        ArrayList<HashMap<String, String>> gattServiceData =
		                new ArrayList<HashMap<String, String>>();
		        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
		                = new ArrayList<ArrayList<HashMap<String, String>>>();
		        mGattCharacteristics =
		                new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
		
		        // Loops through available GATT Services.
		        for (BluetoothGattService gattService : gattServices) {
		            HashMap<String, String> currentServiceData =
		                    new HashMap<String, String>();
		            uuid = gattService.getUuid().toString();
		            currentServiceData.put(
		                    LIST_NAME, SampleGattAttributes.
		                            lookup(uuid, unknownServiceString));
		            currentServiceData.put(LIST_UUID, uuid);
		            gattServiceData.add(currentServiceData);
		
		            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
		                    new ArrayList<HashMap<String, String>>();
		            List<BluetoothGattCharacteristic> gattCharacteristics =
		                    gattService.getCharacteristics();
		            ArrayList<BluetoothGattCharacteristic> charas =
		                    new ArrayList<BluetoothGattCharacteristic>();
		           // Loops through available Characteristics.
		            for (BluetoothGattCharacteristic gattCharacteristic :
		                    gattCharacteristics) {
		                charas.add(gattCharacteristic);
		                HashMap<String, String> currentCharaData =
		                        new HashMap<String, String>();
		                uuid = gattCharacteristic.getUuid().toString();
		                currentCharaData.put(
		                        LIST_NAME, SampleGattAttributes.lookup(uuid,
		                                unknownCharaString));
		                currentCharaData.put(LIST_UUID, uuid);
		                gattCharacteristicGroupData.add(currentCharaData);
		            }
		            mGattCharacteristics.add(charas);
		            gattCharacteristicData.add(gattCharacteristicGroupData);
		         }
		    ...
		    }
		...
		}

#### 接收GATT通知(Receving GATT Notifications)
* 当设备上的特性改变时会通知BLE应用程序。这段代码显示了如何使用`setCharacteristicNotification( )`给一个特性设置通知。

		private BluetoothGatt mBluetoothGatt;
		BluetoothGattCharacteristic characteristic;
		boolean enabled;
		...
		mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
		...
		BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
		        UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
		descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
		mBluetoothGatt.writeDescriptor(descriptor);
* 如果对一个特性启用通知,当远程蓝牙设备特性发送变化，回调函数onCharacteristicChanged( ))被触发。

		@Override
		// Characteristic notification
		public void onCharacteristicChanged(BluetoothGatt gatt,
		        BluetoothGattCharacteristic characteristic) {
		    broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
		}

#### 关闭客户端App(closing the client App)
* 一旦你的app已使用完BLE设备,要调用close()方法,这样系统适当的释放资源.

		public void close() {
		    if (mBluetoothGatt == null) {
		        return;
		    }
		    mBluetoothGatt.close();
		    mBluetoothGatt = null;
		}