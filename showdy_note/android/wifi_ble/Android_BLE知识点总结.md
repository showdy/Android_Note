### BluetoothAdapter
> 蓝牙适配器表示本地蓝牙适配器,可以进行设备搜索,蓝牙配对,通过蓝牙MAC地址实例化BluetoothDevice,创建BluetoothServerSocket监听来自其他设备的连接请求,以及开启BLE设备的扫描

* BluetoothAdapter实例化(API18以上)

	final BluetoothManager bluetoothManager =(BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
	mBluetoothAdapter = bluetoothManager.getAdapter();

* LeScanCallback接口:
	> LeScanCallback 用来传递BLE扫描结果
* boolean startLeScan()
	> 开始扫描
* boolean stopLeScan();
	> 结束扫描
* BluetoothDevice getRemoteDevice(String address)
	> 通过远程device的Mac地址,获取远程BluetoothDevice;

### BluetoothDevice
> BluetoothDevice表示一个远程蓝牙设备,可以和本地设备建立连接,同时可以用来查看远程设备的Mac地址,name,以及绑定状态.

* boolean isConnected() 
  	> 用来判断是否连接
*  BluetoothGatt connectGatt(Context context, boolean autoConnect, BluetoothGattCallback callback)
	> 用来连接该设备的GATT服务, 该设备充当一个客户端的角色

### BluetoothGatt
> GATT协议的API,提供设备间的通讯.
	
* void close();
	> 	关闭GATT客户端