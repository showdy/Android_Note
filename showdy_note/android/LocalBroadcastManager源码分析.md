### LocalBroadcastManager优势:

 Helper to register for and send broadcasts of Intents to local objects within your process.  This has a number of advantages over sending global broadcasts with {@link android.content.Context#sendBroadcast}:

 *  You know that the data you are broadcasting won't leave your app, so don't need to worry about leaking private data.
 *  It is not possible for other applications to send these broadcasts to your app, so you don't need to worry about having security holes they can exploit.
 *  It is more efficient than sending a global broadcast through the system.

相对于BroadcastReceiver来说,LocalBroadcastManager有如下优势:

 * 发送的广播只会在当前APP中传播,不会泄露给其他APP,确保数据传输的安全性.
 * 其他APP的广播无法发送到本地APP中,不用担心安全漏洞被其他APP利用.
 * 比系统全局广播更加高效.


### LocalBraodcastManager源码如下:

```java

	/**
	 * Helper to register for and send broadcasts of Intents to local objects
	 * within your process.  This has a number of advantages over sending
	 * global broadcasts with {@link android.content.Context#sendBroadcast}:
	 * <ul>
	 * <li> You know that the data you are broadcasting won't leave your app, so
	 * don't need to worry about leaking private data.
	 * <li> It is not possible for other applications to send these broadcasts to
	 * your app, so you don't need to worry about having security holes they can
	 * exploit.
	 * <li> It is more efficient than sending a global broadcast through the
	 * system.
	 * </ul>
	 */
	public final class LocalBroadcastManager {
	    private static class ReceiverRecord {
	        final IntentFilter filter;
	        final BroadcastReceiver receiver;
	        boolean broadcasting;
	
	        ReceiverRecord(IntentFilter _filter, BroadcastReceiver _receiver) {
	            filter = _filter;
	            receiver = _receiver;
	        }
	
	        @Override
	        public String toString() {
	            StringBuilder builder = new StringBuilder(128);
	            builder.append("Receiver{");
	            builder.append(receiver);
	            builder.append(" filter=");
	            builder.append(filter);
	            builder.append("}");
	            return builder.toString();
	        }
	    }
	
	    private static class BroadcastRecord {
	        final Intent intent;
	        final ArrayList<ReceiverRecord> receivers;
	
	        BroadcastRecord(Intent _intent, ArrayList<ReceiverRecord> _receivers) {
	            intent = _intent;
	            receivers = _receivers;
	        }
	    }
	
	    private static final String TAG = "LocalBroadcastManager";
	    private static final boolean DEBUG = false;
	
	    private final Context mAppContext;
	
	    private final HashMap<BroadcastReceiver, ArrayList<IntentFilter>> mReceivers
	            = new HashMap<BroadcastReceiver, ArrayList<IntentFilter>>();
	    private final HashMap<String, ArrayList<ReceiverRecord>> mActions
	            = new HashMap<String, ArrayList<ReceiverRecord>>();
	
	    private final ArrayList<BroadcastRecord> mPendingBroadcasts
	            = new ArrayList<BroadcastRecord>();
	
	    static final int MSG_EXEC_PENDING_BROADCASTS = 1;
	
	    private final Handler mHandler;
	
	    private static final Object mLock = new Object();
	    private static LocalBroadcastManager mInstance;
	
	    public static LocalBroadcastManager getInstance(Context context) {
	        synchronized (mLock) {
	            if (mInstance == null) {
	                mInstance = new LocalBroadcastManager(context.getApplicationContext());
	            }
	            return mInstance;
	        }
	    }
	
	    private LocalBroadcastManager(Context context) {
	        mAppContext = context;
	        mHandler = new Handler(context.getMainLooper()) {
	
	            @Override
	            public void handleMessage(Message msg) {
	                switch (msg.what) {
	                    case MSG_EXEC_PENDING_BROADCASTS:
	                        executePendingBroadcasts();
	                        break;
	                    default:
	                        super.handleMessage(msg);
	                }
	            }
	        };
	    }
	
	    /**
	     * Register a receive for any local broadcasts that match the given IntentFilter.
	     *
	     * @param receiver The BroadcastReceiver to handle the broadcast.
	     * @param filter Selects the Intent broadcasts to be received.
	     *
	     * @see #unregisterReceiver
	     */
	    public void registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
	        synchronized (mReceivers) {
	            ReceiverRecord entry = new ReceiverRecord(filter, receiver);
	            ArrayList<IntentFilter> filters = mReceivers.get(receiver);
	            if (filters == null) {
	                filters = new ArrayList<IntentFilter>(1);
	                mReceivers.put(receiver, filters);
	            }
	            filters.add(filter);
	            for (int i=0; i<filter.countActions(); i++) {
	                String action = filter.getAction(i);
	                ArrayList<ReceiverRecord> entries = mActions.get(action);
	                if (entries == null) {
	                    entries = new ArrayList<ReceiverRecord>(1);
	                    mActions.put(action, entries);
	                }
	                entries.add(entry);
	            }
	        }
	    }
	
	    /**
	     * Unregister a previously registered BroadcastReceiver.  <em>All</em>
	     * filters that have been registered for this BroadcastReceiver will be
	     * removed.
	     *
	     * @param receiver The BroadcastReceiver to unregister.
	     *
	     * @see #registerReceiver
	     */
	    public void unregisterReceiver(BroadcastReceiver receiver) {
	        synchronized (mReceivers) {
	            ArrayList<IntentFilter> filters = mReceivers.remove(receiver);
	            if (filters == null) {
	                return;
	            }
	            for (int i=0; i<filters.size(); i++) {
	                IntentFilter filter = filters.get(i);
	                for (int j=0; j<filter.countActions(); j++) {
	                    String action = filter.getAction(j);
	                    ArrayList<ReceiverRecord> receivers = mActions.get(action);
	                    if (receivers != null) {
	                        for (int k=0; k<receivers.size(); k++) {
	                            if (receivers.get(k).receiver == receiver) {
	                                receivers.remove(k);
	                                k--;
	                            }
	                        }
	                        if (receivers.size() <= 0) {
	                            mActions.remove(action);
	                        }
	                    }
	                }
	            }
	        }
	    }
	
	    /**
	     * Broadcast the given intent to all interested BroadcastReceivers.  This
	     * call is asynchronous; it returns immediately, and you will continue
	     * executing while the receivers are run.
	     *
	     * @param intent The Intent to broadcast; all receivers matching this
	     *     Intent will receive the broadcast.
	     *
	     * @see #registerReceiver
	     */
	    public boolean sendBroadcast(Intent intent) {
	        synchronized (mReceivers) {
	            final String action = intent.getAction();
	            final String type = intent.resolveTypeIfNeeded(
	                    mAppContext.getContentResolver());
	            final Uri data = intent.getData();
	            final String scheme = intent.getScheme();
	            final Set<String> categories = intent.getCategories();
	
	            final boolean debug = DEBUG ||
	                    ((intent.getFlags() & Intent.FLAG_DEBUG_LOG_RESOLUTION) != 0);
	            if (debug) Log.v(
	                    TAG, "Resolving type " + type + " scheme " + scheme
	                    + " of intent " + intent);
	
	            ArrayList<ReceiverRecord> entries = mActions.get(intent.getAction());
	            if (entries != null) {
	                if (debug) Log.v(TAG, "Action list: " + entries);
	
	                ArrayList<ReceiverRecord> receivers = null;
	                for (int i=0; i<entries.size(); i++) {
	                    ReceiverRecord receiver = entries.get(i);
	                    if (debug) Log.v(TAG, "Matching against filter " + receiver.filter);
	
	                    if (receiver.broadcasting) {
	                        if (debug) {
	                            Log.v(TAG, "  Filter's target already added");
	                        }
	                        continue;
	                    }
	
	                    int match = receiver.filter.match(action, type, scheme, data,
	                            categories, "LocalBroadcastManager");
	                    if (match >= 0) {
	                        if (debug) Log.v(TAG, "  Filter matched!  match=0x" +
	                                Integer.toHexString(match));
	                        if (receivers == null) {
	                            receivers = new ArrayList<ReceiverRecord>();
	                        }
	                        receivers.add(receiver);
	                        receiver.broadcasting = true;
	                    } else {
	                        if (debug) {
	                            String reason;
	                            switch (match) {
	                                case IntentFilter.NO_MATCH_ACTION: reason = "action"; break;
	                                case IntentFilter.NO_MATCH_CATEGORY: reason = "category"; break;
	                                case IntentFilter.NO_MATCH_DATA: reason = "data"; break;
	                                case IntentFilter.NO_MATCH_TYPE: reason = "type"; break;
	                                default: reason = "unknown reason"; break;
	                            }
	                            Log.v(TAG, "  Filter did not match: " + reason);
	                        }
	                    }
	                }
	
	                if (receivers != null) {
	                    for (int i=0; i<receivers.size(); i++) {
	                        receivers.get(i).broadcasting = false;
	                    }
	                    mPendingBroadcasts.add(new BroadcastRecord(intent, receivers));
	                    if (!mHandler.hasMessages(MSG_EXEC_PENDING_BROADCASTS)) {
	                        mHandler.sendEmptyMessage(MSG_EXEC_PENDING_BROADCASTS);
	                    }
	                    return true;
	                }
	            }
	        }
	        return false;
	    }
	
	    /**
	     * Like {@link #sendBroadcast(Intent)}, but if there are any receivers for
	     * the Intent this function will block and immediately dispatch them before
	     * returning.
	     */
	    public void sendBroadcastSync(Intent intent) {
	        if (sendBroadcast(intent)) {
	            executePendingBroadcasts();
	        }
	    }
	
	    private void executePendingBroadcasts() {
	        while (true) {
	            BroadcastRecord[] brs = null;
	            synchronized (mReceivers) {
	                final int N = mPendingBroadcasts.size();
	                if (N <= 0) {
	                    return;
	                }
	                brs = new BroadcastRecord[N];
	                mPendingBroadcasts.toArray(brs);
	                mPendingBroadcasts.clear();
	            }
	            for (int i=0; i<brs.length; i++) {
	                BroadcastRecord br = brs[i];
	                for (int j=0; j<br.receivers.size(); j++) {
	                    br.receivers.get(j).receiver.onReceive(mAppContext, br.intent);
	                }
	            }
	        }
	    }
	}
	
	
```

* `LocalBroadcastManager`中有三个关键字段分别为:

	```java
	
		 private final HashMap<BroadcastReceiver, ArrayList<IntentFilter>> mReceivers = new HashMap();	
		 private final HashMap<String, ArrayList<ReceiverRecord>> mActions = new HashMap();
		 private final ArrayList<BroadcastRecord> mPendingBroadcasts = new ArrayList();
	
	```
	
	`HashMap`对象的`mReceivers`存储广播和过滤信息集合,通过以`BroadcastReceiver`为Key,`ArrayList<IntentFilter>`为Value,这样做是为了方便注销.
	
	`HashMap`对象的`mActions`存储`Action`和`ReceiverRecord`,以`Action`为key,`ArrayList<ReceiverRecord>`为value,`mActions`的主要作用是方便在广播发送后快速得到可以接收他的`BroadcastReceiver`.
	
	`mPendingBroadcasts`就是发送广播的集合.

* `LocalBroadcastManager`的构造函数:

	```java

	    private LocalBroadcastManager(Context context) {
	        mAppContext = context;
	        mHandler = new Handler(context.getMainLooper()) {
	
	            @Override
	            public void handleMessage(Message msg) {
	                switch (msg.what) {
	                    case MSG_EXEC_PENDING_BROADCASTS:
	                        executePendingBroadcasts();
	                        break;
	                    default:
	                        super.handleMessage(msg);
	                }
	            }
	        };
	    }

	```

	`LocalBroadcastManager`的构造函数很简单,就做了一件事,创建一个基于主线程Looper的Handler,并接收`msg.what`为`MSG_EXEC_PENDING_BROADCASTS`的消息,并调用 `executePendingBroadcasts()`发送广播.


* `LocalBroadcastManager注册`

	```java

		public void registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
	        synchronized (mReceivers) {
	            ReceiverRecord entry = new ReceiverRecord(filter, receiver);
	            ArrayList<IntentFilter> filters = mReceivers.get(receiver);
	            if (filters == null) {
	                filters = new ArrayList<IntentFilter>(1);
	                mReceivers.put(receiver, filters);
	            }
	            filters.add(filter);
	            for (int i=0; i<filter.countActions(); i++) {
	                String action = filter.getAction(i);
	                ArrayList<ReceiverRecord> entries = mActions.get(action);
	                if (entries == null) {
	                    entries = new ArrayList<ReceiverRecord>(1);
	                    mActions.put(action, entries);
	                }
	                entries.add(entry);
	            }
	        }
	    }

	```
	基于要注册的BroadReceiver和IntentFilter,在mReceivers中查找,如果为null,便把传过来的receiver和filter参数存储到mReceivers中,同时将IntentFilter中的Action存储到mActions中.方便后面注销广播,对元素的移除.

* `LocalBroadcastManager`的注销

	```java
	
		public void unregisterReceiver(BroadcastReceiver receiver) {
	        synchronized (mReceivers) {
	            ArrayList<IntentFilter> filters = mReceivers.remove(receiver);
	            if (filters == null) {
	                return;
	            }
	            for (int i=0; i<filters.size(); i++) {
	                IntentFilter filter = filters.get(i);
	                for (int j=0; j<filter.countActions(); j++) {
	                    String action = filter.getAction(j);
	                    ArrayList<ReceiverRecord> receivers = mActions.get(action);
	                    if (receivers != null) {
	                        for (int k=0; k<receivers.size(); k++) {
	                            if (receivers.get(k).receiver == receiver) {
	                                receivers.remove(k);
	                                k--;
	                            }
	                        }
	                        if (receivers.size() <= 0) {
	                            mActions.remove(action);
	                        }
	                    }
	                }
	            }
	        }
	    }

	````

	`LocalBroadcastManager`注销就是从mReceivers和mActions中移除相应的元素,比如BroadcastReceiver和IntentFilter以及Action.

* `LocalBroadcastManager`发送广播:

	```java
	
		public boolean sendBroadcast(Intent intent) {
	        synchronized (mReceivers) {
	            final String action = intent.getAction();
	            final String type = intent.resolveTypeIfNeeded(
	                    mAppContext.getContentResolver());
	            final Uri data = intent.getData();
	            final String scheme = intent.getScheme();
	            final Set<String> categories = intent.getCategories();
	
	            final boolean debug = DEBUG ||
	                    ((intent.getFlags() & Intent.FLAG_DEBUG_LOG_RESOLUTION) != 0);
	            if (debug) Log.v(
	                    TAG, "Resolving type " + type + " scheme " + scheme
	                    + " of intent " + intent);
	
	            ArrayList<ReceiverRecord> entries = mActions.get(intent.getAction());
	            if (entries != null) {
	                if (debug) Log.v(TAG, "Action list: " + entries);
	
	                ArrayList<ReceiverRecord> receivers = null;
	                for (int i=0; i<entries.size(); i++) {
	                    ReceiverRecord receiver = entries.get(i);
	                    if (debug) Log.v(TAG, "Matching against filter " + receiver.filter);
	
	                    if (receiver.broadcasting) {
	                        if (debug) {
	                            Log.v(TAG, "  Filter's target already added");
	                        }
	                        continue;
	                    }
	
	                    int match = receiver.filter.match(action, type, scheme, data,
	                            categories, "LocalBroadcastManager");
	                    if (match >= 0) {
	                        if (debug) Log.v(TAG, "  Filter matched!  match=0x" +
	                                Integer.toHexString(match));
	                        if (receivers == null) {
	                            receivers = new ArrayList<ReceiverRecord>();
	                        }
	                        receivers.add(receiver);
	                        receiver.broadcasting = true;
	                    } else {
	                        if (debug) {
	                            String reason;
	                            switch (match) {
	                                case IntentFilter.NO_MATCH_ACTION: reason = "action"; break;
	                                case IntentFilter.NO_MATCH_CATEGORY: reason = "category"; break;
	                                case IntentFilter.NO_MATCH_DATA: reason = "data"; break;
	                                case IntentFilter.NO_MATCH_TYPE: reason = "type"; break;
	                                default: reason = "unknown reason"; break;
	                            }
	                            Log.v(TAG, "  Filter did not match: " + reason);
	                        }
	                    }
	                }
	
	                if (receivers != null) {
	                    for (int i=0; i<receivers.size(); i++) {
	                        receivers.get(i).broadcasting = false;
	                    }
	                    mPendingBroadcasts.add(new BroadcastRecord(intent, receivers));
	                    if (!mHandler.hasMessages(MSG_EXEC_PENDING_BROADCASTS)) {
	                        mHandler.sendEmptyMessage(MSG_EXEC_PENDING_BROADCASTS);
	                    }
	                    return true;
	                }
	            }
	        }
	        return false;
	    }

	```
	
	先根据Action从mActions中取出ReceiverRecord列表,遍历ReceiverRecord判断filter和intent中的action,type,scheme,data,category是否匹配,是的话保存到receivers列表中,发送msg.what为MSG_EXEC_PENDING_BROADCASTS的消息,通过主线程的Handler去处理.
	
* `LocalBroadcastManager`处理消息

	```java

	    private void executePendingBroadcasts() {
	        while (true) {
	            BroadcastRecord[] brs = null;
	            synchronized (mReceivers) {
	                final int N = mPendingBroadcasts.size();
	                if (N <= 0) {
	                    return;
	                }
	                brs = new BroadcastRecord[N];
	                mPendingBroadcasts.toArray(brs);
	                mPendingBroadcasts.clear();
	            }
	            for (int i=0; i<brs.length; i++) {
	                BroadcastRecord br = brs[i];
	                for (int j=0; j<br.receivers.size(); j++) {
	                    br.receivers.get(j).receiver.onReceive(mAppContext, br.intent);
	                }
	            }
	        }
	    }

	```

	mPendingBroadcasts转换为数组`BroadcastRecord`,循环遍历每个receiver,调用其onReceive函数,完成消息的传递.



### LocalBroadcastManager分析总结:

* `LocalBroadcastManager`的核心思想还是Handler,只是利用了IntentFilter的match功能,至于BroadcastReceiver换成其他接口也无所谓,只是顺便利用了现成的类和概念而已.
* 因为是Handler通讯,所以才有实现了应用内通讯,自然安全性高,效率高.




	
