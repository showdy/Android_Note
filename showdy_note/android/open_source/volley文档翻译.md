### Volley 
特点:

* 通信更快、更简单
* GET、POST网络请求及网络图像的高效率异步处理请求
* 对请求进行优先级的排序
* 网络请求的缓存
* 多级别取消请求
* 和Activity生命周期的联动
* 不太适合进行大量网络数据的上传和下载(因volley将inputstream已经解析在内存data[]中,而大量数据则是开启一个stream通道,例如Okhttp基于socket);


### 使用默认队列Volley.newRequestQueue(this)

#### 使用GET请求:

```java

	final TextView mTextView = (TextView) findViewById(R.id.text);
	...
	
	// Instantiate the RequestQueue.
	RequestQueue queue = Volley.newRequestQueue(this);
	String url ="http://www.google.com";
	
	// Request a string response from the provided URL.
	StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
	            new Response.Listener<String>() {
	    @Override
	    public void onResponse(String response) {
	        // Display the first 500 characters of the response string.
	        mTextView.setText("Response is: "+ response.substring(0,500));
	    }
	}, new Response.ErrorListener() {
	    @Override
	    public void onErrorResponse(VolleyError error) {
	        mTextView.setText("That didn't work!");
	    }
	});
	// Add the request to the RequestQueue.
	queue.add(stringRequest);
```

#### 使用POST请求:
* 第一种方法:
```jva

		StringRequest stringRequest = new StringRequest(Method.POST, url,  listener, errorListener) {  
		    @Override  
		    protected Map<String, String> getParams() throws AuthFailureError {  
		        Map<String, String> map = new HashMap<String, String>();  
		        map.put("params1", "value1");  
		        map.put("params2", "value2");  
		        return map;  
		    }  
		};
```

* 第二种方法:

	```java
	
		   String url = "http://m.weather.com.cn/atad/101010100.html";    
		   RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());    
		   //封装请求参数    
		   Map<String, String>map = new HashMap<>();    
		   map.put("username","liming");    
		   map.put("password","123456");    
		   JSONObject jsonObject = new JSONObject(map);    
		   //参数jsonObject封装了请求参数，Volley会自动提取里面的值，作为post请求参数    
		   JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.POST,
		        url,jsonObject,            
		       new Response.Listener<JSONObject>() {                
		       @Override                
		       public void onResponse(JSONObject response) {    
		       //执行请求成功的逻辑            
		       }            
		  }, new Response.ErrorListener() {        
			 	@Override        
				 public void onErrorResponse(VolleyError error) {      
				 //执行请求失败的逻辑       
				 }    
				});    
				jsonObjectRequest.setTag("jsonObjectGET");    
				requestQueue.add(jsonObjectRequest);   

	```
	
#### 与Activity生命周期联动,取消请求

* 1.定义tag,绑定请求

	```java
	
		public static final String TAG = "MyTag";
		StringRequest stringRequest; // Assume this exists.
		RequestQueue mRequestQueue;  // Assume this exists.
		
		// Set the tag on the request.
		stringRequest.setTag(TAG);
		
		// Add the request to the RequestQueue.
		mRequestQueue.add(stringRequest);
	```
	

* 2.与activity联动,在onstop()时,取消请求:

	```java

		protected void onStop () {
		    super.onStop();
		    if (mRequestQueue != null) {
		        mRequestQueue.cancelAll(TAG);
		    }
		}

	```
	
### 自定义RequestQueue
> 使用Volley.newRequestQueue创建,可以充分利用Volley默认队列的优势.当也可以自定义一个RequestQueue,创建一个单例对象,贯穿整个app生命周期.

### 新建Network和Cache
RequsetQueue需要network传输request,cache处理caching.

在Volley.toolbox下有标准的实现类: 

* DiskBaseCache
* BaseNetwork--基于HttpClient或者HttpurlConnection
* api<9 使用HttpClient, api>9后使用HttpUrlConnection
```java

		HttpStack stack;
		...
		// If the device is running a version >= Gingerbread...
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
		    // ...use HttpURLConnection for stack.
		} else {
		    // ...use AndroidHttpClient for stack.
		}
		Network network = new BasicNetwork(stack);
	
	
		RequestQueue mRequestQueue;

```

* 自定义一个RequestQueue
	```java

	
		// Instantiate the cache
		Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
		
		// Set up the network to use HttpURLConnection as the HTTP client.
		Network network = new BasicNetwork(new HurlStack());
		
		// Instantiate the RequestQueue with the cache and network.
		mRequestQueue = new RequestQueue(cache, network);
		
		// Start the queue
		mRequestQueue.start();
		
		String url ="http://www.myurl.com";
		
		// Formulate the request and handle the response.
		StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
		        new Response.Listener<String>() {
		    @Override
		    public void onResponse(String response) {
		        // Do something with the response
		    }
		},
		    new Response.ErrorListener() {
		        @Override
		        public void onErrorResponse(VolleyError error) {
		            // Handle error
		    }
		});
		
		// Add the request to the RequestQueue.
		mRequestQueue.add(stringRequest);
		...
	```	
### 使用单例模式创建RequestQueue
* 第一种方式: 使用单例模式创建RequestQueue,提供静态方法,注意要使用Application context,not Activity context,保证生命周期和app一样长.
* 第二种方式: 创建Application的之类,在onCreate()中初始化,但不推荐.
* 下面是一段单例提供RequestQueue和ImageLoader相关功能的代码:
 
 ```java
 
	public class MySingleton {
	    private static MySingleton mInstance;
	    private RequestQueue mRequestQueue;
	    private ImageLoader mImageLoader;
	    private static Context mCtx;
	
	    private MySingleton(Context context) {
	        mCtx = context;
	        mRequestQueue = getRequestQueue();
	
	        mImageLoader = new ImageLoader(mRequestQueue,
	                new ImageLoader.ImageCache() {
	            private final LruCache<String, Bitmap>
	                    cache = new LruCache<String, Bitmap>(20);
	
	            @Override
	            public Bitmap getBitmap(String url) {
	                return cache.get(url);
	            }
	
	            @Override
	            public void putBitmap(String url, Bitmap bitmap) {
	                cache.put(url, bitmap);
	            }
	        });
	    }
	
	    public static synchronized MySingleton getInstance(Context context) {
	        if (mInstance == null) {
	            mInstance = new MySingleton(context);
	        }
	        return mInstance;
	    }
	
	    public RequestQueue getRequestQueue() {
	        if (mRequestQueue == null) {
	            // getApplicationContext() is key, it keeps you from leaking the
	            // Activity or BroadcastReceiver if someone passes one in.
	            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
	        }
	        return mRequestQueue;
	    }
	
	    public <T> void addToRequestQueue(Request<T> req) {
	        getRequestQueue().add(req);
	    }
	
	    public ImageLoader getImageLoader() {
	        return mImageLoader;
	    }
	}	
	
```

* 下面是一段使用单例RequestQueue运用的代码片段:

```java

	// Get a RequestQueue
	RequestQueue queue = MySingleton.getInstance(this.getApplicationContext()).
	    getRequestQueue();
	...
	
	// Add a request (in this example, called stringRequest) to your RequestQueue.
	MySingleton.getInstance(this).addToRequestQueue(stringRequest);
```

	
### 使用Volley提供的Request类型:
* StringRequest
	> 指定url,返回String

* ImageRequest
	> 指定url,返回Iamge

* JsonRequest
	* JsonObjectRequest
	* JsonArrayRequest
	> 指定url,返回JsonObject或者JsonArray

#### 请求图片(Requset an Image)
* ImageRequset
* ImageLoader
* NetworkImageView

#### 使用ImageRequest
* 使用单例模式下RequsetQueue的一段ImageRequset代码:
```java

		ImageView mImageView;
		String url = "http://i.imgur.com/7spzG.png";
		mImageView = (ImageView) findViewById(R.id.myImage);
		...
		
		// Retrieves an image specified by the URL, displays it in the UI.
		ImageRequest request = new ImageRequest(url,
		    new Response.Listener<Bitmap>() {
		        @Override
		        public void onResponse(Bitmap bitmap) {
		            mImageView.setImageBitmap(bitmap);
		        }
		    }, 0, 0, null,
		    new Response.ErrorListener() {
		        public void onErrorResponse(VolleyError error) {
		            mImageView.setImageResource(R.drawable.image_load_error);
		        }
		    });
		// Access the RequestQueue through your singleton class.
		MySingleton.getInstance(this).addToRequestQueue(request);
```

#### 使用ImageLoader和NetworkImageView
* 联合使用ImageLoader和NetworkImageView可以有效管理多图片显示,在xml可以类似ImageView布局:
```mxl

	<com.android.volley.toolbox.NetworkImageView
		android:id="@+id/networkImageView"
		android:layout_width="150dp"
		android:layout_height="170dp"
		android:layout_centerHorizontal="true" />
```


* 也可以使用ImageLoader加载图片:
	```java
	
		ImageLoader mImageLoader;
		ImageView mImageView;
		// The URL for the image that is being loaded.
		private static final String IMAGE_URL =
		    "http://developer.android.com/images/training/system-ui.png";
		...
		mImageView = (ImageView) findViewById(R.id.regularImageView);
		
		// Get the ImageLoader through your singleton class.
		mImageLoader = MySingleton.getInstance(this).getImageLoader();
		mImageLoader.get(IMAGE_URL, ImageLoader.getImageListener(mImageView,
		         R.drawable.def_image, R.drawable.err_image));
	```
	
* 也可以使用NetworkImageView构建ImageView视图
	```java
	
		ImageLoader mImageLoader;
		NetworkImageView mNetworkImageView;
		private static final String IMAGE_URL =
		    "http://developer.android.com/images/training/system-ui.png";
		...
		
		// Get the NetworkImageView that will display the image.
		mNetworkImageView = (NetworkImageView) findViewById(R.id.networkImageView);
		
		// Get the ImageLoader through your singleton class.
		mImageLoader = MySingleton.getInstance(this).getImageLoader();
		
		// Set the URL of the image that should be loaded into this view, and
		// specify the ImageLoader that will be used to make the request.
		mNetworkImageView.setImageUrl(IMAGE_URL, mImageLoader);
	```
	
#### 使用LRU cache
* Volley实现了标准缓存类DiskBasedCache,该类直接将文件写入到硬盘中
* 使用ImageLoader,需要实现ImageLoader.ImageCache接口,构建自定义的LRU bitmap cache
* 下面是LruBitmapCache继承自LruCache,实现了ImageLoader.ImageCache接口.
	```java
	
		public class LruBitmapCache extends LruCache<String, Bitmap>implements ImageCache {
		
		    public LruBitmapCache(int maxSize) {
		        super(maxSize);
		    }
		
		    public LruBitmapCache(Context ctx) {
		        this(getCacheSize(ctx));
		    }
		
		    @Override
		    protected int sizeOf(String key, Bitmap value) {
		        return value.getRowBytes() * value.getHeight();
		    }
		
		    @Override
		    public Bitmap getBitmap(String url) {
		        return get(url);
		    }
		
		    @Override
		    public void putBitmap(String url, Bitmap bitmap) {
		        put(url, bitmap);
		    }
		
		    // Returns a cache size equal to approximately three screens worth of images.
		    public static int getCacheSize(Context ctx) {
		        final DisplayMetrics displayMetrics = ctx.getResources().
		                getDisplayMetrics();
		        final int screenWidth = displayMetrics.widthPixels;
		        final int screenHeight = displayMetrics.heightPixels;
		        // 4 bytes per pixel
		        final int screenBytes = screenWidth * screenHeight * 4;
		
		        return screenBytes * 3;
		    }
		}
	```
	
* 下面展示使用LruBitmapCache实例化ImageLoader
	```java
	
		RequestQueue mRequestQueue; // assume this exists.
		ImageLoader mImageLoader = new ImageLoader(mRequestQueue, new LruBitmapCache(
		            LruBitmapCache.getCacheSize()));
			
	```
#### Requset Json
* Volley提供了两个类做Json Request,此二者都是继承JsonRequest:
	* JsonArrayRequest
	* JsonObjectRequest

* 下面一段如何使用的例子:
```java

		TextView mTxtDisplay;
		ImageView mImageView;
		mTxtDisplay = (TextView) findViewById(R.id.txtDisplay);
		String url = "http://my-json-feed";
		
		JsonObjectRequest jsObjRequest = new JsonObjectRequest
		        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
		
		    @Override
		    public void onResponse(JSONObject response) {
		        mTxtDisplay.setText("Response: " + response.toString());
		    }
		}, new Response.ErrorListener() {
		
		    @Override
		    public void onErrorResponse(VolleyError error) {
		        // TODO Auto-generated method stub
		
		    }
		});
		
		// Access the RequestQueue through your singleton class.
		MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
		For an example of implementing a custom JSON request based on Gson, see the next lesson, Implementing a Custom Request.
```
### 实现自定义请求(Custom Requset)

* reponse如果不是json,String,image,则需要自定义Requset:
	* 继承 Request<T>,利用Response是String,则自定义Requset<String> extend Request<T>.
	* 实现抽象方法parseNetworkResponse()和deliverResponse();
	
#### parseNetworkResponse
*　属于Response对象,但是封装了resposne的解析,下面是一个parseNetWorkResponse();
```java

		@Override
		protected Response<T> parseNetworkResponse(
		        NetworkResponse response) {
		    try {
		        String json = new String(response.data,
		        HttpHeaderParser.parseCharset(response.headers));
		    return Response.success(gson.fromJson(json, clazz),
		    HttpHeaderParser.parseCacheHeaders(response));
		    }
		    // handle errors
		...
		}
```

* parseNetworkResponse()其中参数为NetworkResponse,包含 a byte[], HTTP status code, and response headers
* 返回值必须为Response<T>,其中包含response对象,cache metadata,or an error.
* 如果协议没有标准的cache semantics,必须要构建Cache.Entry,但多数请求如下定义:
	
		return Response.success(myDecodedObject,
	        HttpHeaderParser.parseCacheHeaders(response));
* Volley在work thread中调用parseNetworkResponse(),保证在在大量数据解析时不会阻塞主线程.

#### deliverResponse
* 在主线程会回调返回调用parseNetworkResponse()后的结果,多数回调接口如下:
	```java
		protected void deliverResponse(T response) {
        	listener.onResponse(response);
	```
	
#### GsonRequest例子
```java
		public class GsonRequest<T> extends Request<T> {
		    private final Gson gson = new Gson();
		    private final Class<T> clazz;
		    private final Map<String, String> headers;
		    private final Listener<T> listener;
		
		    /**
		     * Make a GET request and return a parsed object from JSON.
		     *
		     * @param url URL of the request to make
		     * @param clazz Relevant class object, for Gson's reflection
		     * @param headers Map of request headers
		     */
		    public GsonRequest(String url, Class<T> clazz, Map<String, String> headers,
		            Listener<T> listener, ErrorListener errorListener) {
		        super(Method.GET, url, errorListener);
		        this.clazz = clazz;
		        this.headers = headers;
		        this.listener = listener;
		    }
		
		    @Override
		    public Map<String, String> getHeaders() throws AuthFailureError {
		        return headers != null ? headers : super.getHeaders();
		    }
		
		    @Override
		    protected void deliverResponse(T response) {
		        listener.onResponse(response);
		    }
		
		    @Override
		    protected Response<T> parseNetworkResponse(NetworkResponse response) {
		        try {
		            String json = new String(
		                    response.data,
		                    HttpHeaderParser.parseCharset(response.headers));
		            return Response.success(
		                    gson.fromJson(json, clazz),
		                    HttpHeaderParser.parseCacheHeaders(response));
		        } catch (UnsupportedEncodingException e) {
		            return Response.error(new ParseError(e));
		        } catch (JsonSyntaxException e) {
		            return Response.error(new ParseError(e));
		        }
		    }
		}
```
