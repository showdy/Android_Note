## BitmapFactory.options
BitmapFactory.Options类是BitmapFactory对图片进行解码时使用的一个配置参数类，其中定义了一系列的public成员变量，每个成员变量代表一个配置参数。

### 图片解码建议配置(inPreferredConfig)
* 参数inpreferredconfig表示图片解码时使用的颜色模式，也就是图片中每个像素颜色的表示方式

* 图片颜色:
	*  计算机表示一个颜色都需要将颜色对应到一个颜色空间中的某个颜色值,常见的颜色空间为RGB,CMYK等.
	*  JPEG格式支持RGB,CMYK,而PNG支持RGB,此外绝大数显示器只支持RGB颜色的输入,计算机显示一张图片时,如果图片本身不是RGB颜色空间编码,需将其转化为RGB颜色空间的颜色后在显示,所以非RGB显示会有失真.
	
* 颜色透明度:
	* 图片包含颜色信息和透明信息,计算机中用一个单独的透明通道表示(Alpha通道),JPEG格式图片不支持透明度,PNG/GIT格式支持透明度.
	
* Android颜色和透明度表示
	* Android通常用32位二进制表示一个像素颜色和透明度,即A,R,G,B四个通道,每个通道范围为[0,0xFF]
	
* inperferredConfig参数
	* BitmapFactory.Options类是BitmapFractory对图片进行解码时使用的配置参数类, 其中定义一系列public的成员变量(配置参数),inperferredConfig表示图片解码时使用的颜色模式:
	* inpreferredConfig参数有四个值:
		* ALPHA_8: 每个像素用占8位,存储的是图片的透明值,占1个字节
		* RGB_565:每个像素用占16位,分别为5-R,6-G,5-B通道,占2个字节
		* ARGB-4444:每个像素占16位,即每个通道用4位表示,占2个字节
		* ARGB_8888:每个像素占32位,每个通道用8位表示,占4个字节
		
* 图片解码时,默认使用ARGB_8888模式:

	*  `Bitmap.Config inPreferredConfig = Bitmap.Config.ARGB_8888;`
	*  `ARGB_4444已deprecated,在KITKAT(Android4.4)以上,会用ARGB_8888代替ARGB_4444`

* 使用inperferredConfig注意:

	* 如果inPreferredConfig不为null，解码器会尝试使用此参数指定的颜色模式来对图片进行解码，如果inPreferredConfig为null或者在解码时无法满足此参数指定的颜色模式，解码器会自动根据**原始图片的特征**以及**当前设备的屏幕位深**，选取合适的颜色模式来解码，例如，如果图片中包含透明度，那么对该图片解码时使用的配置就需要支持透明度，默认会使用ARGB_8888来解码。 
	* 根据inperferredConfig的解析,就会发现如下bm1,bm2,bm3结果会一样:
	
	    ```java
	    
			InputStream stream = getAssets().open(file);
			Options op1 = new Options();
			op1.inPreferredConfig = Config.ALPHA_8;
			Bitmap bm1 = BitmapFactory.decodeStream(stream, null, op1);
			Options op2 = new Options();
			op2.inPreferredConfig = Config.RGB_565;
			Bitmap bm2 = BitmapFactory.decodeStream(stream, null, op2);
			Options op3 = new Options();
			op3.inPreferredConfig = Config.ARGB_8888;
			Bitmap bm3 = BitmapFactory.decodeStream(stream, null, op3);
	    ```

* 疑点: 1. 当出现不满足情况时，使用的合适配置是如何选取的？ 
	* 1. 如果inPreferredConfig为null，解码时使用的颜色模式会根据图片源文件的类型进行选取，如果图片文件的颜色模式为CMYK，或RGB565，则选取RGB_565。如果是其他类型，则选取ARGB_8888。 
	* 2. 如果inPreferredConfig指定的选项在解码时无法满足，并不会再根据图片文件的类型来选取合适的选项，而是直接使用ARGB_8888选项来解码。例如，图片源文件为RGB566编码的BMP图片，使用ALPHA_8选项来解码时属于不满足的情况，这时会选取ARGB_8888选项来解码，而不是选取RGB565。和inPreferredConfig为null时选取的“合适的”选项并不相同。
	
* 疑点: 2. 什么情况下使用什么样的配置会出现不满足的情况？ 
	* 所有情况下ARGB_8888配置都可以满足
	* 所有情况下ALPHA_8配置都不满足
	* 绝大多数情况下RGB565选项都不满足

### 优化Bitmap的内存使用(inBitmap)
* 在Android 2.2 (API level 8)以及之前，当垃圾回收发生时，应用的线程是会被暂停的，这会导致一个延迟滞后，并降低系统效率。 从Android 2.3开始，添加了并发垃圾回收的机制， 这意味着在**一个Bitmap不再被引用之后，它所占用的内存会被立即回收**。

* 在Android 2.3.3 (API level 10)以及之前, **一个Bitmap的像素级数据（pixel data）是存放在Native内存空间中的**。 这些数据与Bitmap本身所占内存是隔离的，**Bitmap本身被存放在Dalvik堆中**。我们无法预测在Native内存中的像素级数据何时会被释放，这意味着程序容易超过它的内存限制并且崩溃。 **自Android 3.0 (API Level 11)开始， 像素级数据则是与Bitmap本身一起存放在Dalvik堆中**。

* 在Android 2.3.3 (API level 10) 以及更低版本上，**推荐使用recycle()方法**。 如果在应用中显示了大量的Bitmap数据，我们很可能会遇到OutOfMemoryError的错误。 recycle()方法可以使得程序更快的释放内存。
	>Caution：只有当我们确定这个Bitmap不再需要用到的时候才应该使用recycle()。在执行recycle()方法之后，如果尝试绘制这个Bitmap， 我们将得到"Canvas: trying to use a recycled bitmap"的错误提示。
	
* 从Android 3.0 (API Level 11)开始，引进了**BitmapFactory.Options.inBitmap**字段。如果这个值被设置了，decode方法会在加载内容的时候去reuse已经存在的bitmap. 这意味着bitmap的内存是被reused的，这样可以提升性能, 并且减少了内存的allocation与de-allocation.
	* reused的bitmap必须和原数据内容大小一致, 并且是JPEG 或者 PNG 的格式 (或者是某个resource 与 stream).
	* reused的bitmap的configuration值如果有设置，则会覆盖掉inPreferredConfig值.
	* 你应该总是使用decode方法返回的bitmap, 因为你不可以假设reusing的bitmap是可用的(例如，大小不对).

	```java

		private static void addInBitmapOptions(BitmapFactory.Options options, ImageCache cache) { 
		  // inBitmap only works with mutable bitmaps, so force the decoder to 
		  // return mutable bitmaps. 
		  options.inMutable = true; 
		  if (cache != null) { 
		    // Try to find a bitmap to use for inBitmap. 
		    Bitmap inBitmap = cache.getBitmapFromReusableSet(options); 
		    if (inBitmap != null) { 
		      // If a suitable bitmap has been found, 
		      // set it as the value of inBitmap. 
		      options.inBitmap = inBitmap; 
		    } 
		  }
		}
	
		static boolean canUseForInBitmap( Bitmap candidate, BitmapFactory.Options targetOptions) { 
		  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { 
		    // From Android 4.4 (KitKat) onward we can re-use 
		    // if the byte size of the new bitmap is smaller than 
		    // the reusable bitmap candidate 
		    // allocation byte count. 
		    int width = targetOptions.outWidth / targetOptions.inSampleSize; 
		    int height = targetOptions.outHeight / targetOptions.inSampleSize; 
		    int byteCount = width * height * getBytesPerPixel(candidate.getConfig()); 
		    return byteCount <= candidate.getAllocationByteCount(); 
		  } 
		  // On earlier versions, 
		  // the dimensions must match exactly and the inSampleSize must be 1 
		  return candidate.getWidth() == targetOptions.outWidth 
		      && candidate.getHeight() == targetOptions.outHeight 
		      && targetOptions.inSampleSize == 1;
		}
	```

### 高效加载大图片(inJustDecodeBounds / inSmapleSize)
* 如果设置为true,将不返回bitmap, 但是Bitmap的outWidth,outHeight等属性将会赋值,允许调用查询Bitmap,而不需要为Bitmap分配内存.
* 例如加载一张很大的位图, 如果直接解码会造成OOM,做法是:
	* 1.先拿到位图的尺寸后,进行放缩后再加载位图
	
		```java

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeResource(getResources(), R.id.myimage, options);
			int imageHeight = options.outHeight;
			int imageWidth = options.outWidth;
			String imageType = options.outMimeType;
		```
	* 2.计算inSampleSize
	
		```java

			public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
			    // Raw height and width of image
			    final int height = options.outHeight;
			    final int width = options.outWidth;
			    int inSampleSize = 1;
			
			    if (height > reqHeight || width > reqWidth) {
			
			        final int halfHeight = height / 2;
			        final int halfWidth = width / 2;
			
			        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
			        // height and width larger than the requested height and width.
			        while ((halfHeight / inSampleSize) > reqHeight
			                && (halfWidth / inSampleSize) > reqWidth) {
			            inSampleSize *= 2;
			        }
			    }
			
			    return inSampleSize;
			}
		```
		>**设置inSampleSize为2的幂是因为解码器最终还是会对非2的幂的数进行向下处理，获取到最靠近2的幂的数。详情参考inSampleSize的文档**
	* 3.放缩后再加载小位图:
		
		```java

			public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
		        int reqWidth, int reqHeight) {
		
			    // First decode with inJustDecodeBounds=true to check dimensions
			    final BitmapFactory.Options options = new BitmapFactory.Options();
			    options.inJustDecodeBounds = true;
			    BitmapFactory.decodeResource(res, resId, options);
			
			    // Calculate inSampleSize
			    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
			
			    // Decode bitmap with inSampleSize set
			    options.inJustDecodeBounds = false;
			    return BitmapFactory.decodeResource(res, resId, options);
			}
	    ```
### inPremultiplied
* 如果设置了true(默认是true)，那么返回的图片RGB都会预乘透明通道A后的颜色
* 系统View或者Canvas绘制图片,不建议设置为fase,否则会抛出异常,这是因为系统会假定所有图像都预乘A通道的已简化绘制时间.
* 设置inPremultiplied的同时,设置inScale会导致绘制的颜色不正确.

### inDither
设置是否抖动处理图片.

### inMutable
 如果设置为true,将返回一个mutable的bitmap,可用于修改BitmapFactory加载而来的bitmap.

* `BitmapFactory.decodeResource(Resources res, int id)`获取到的bitmap是mutable的，而`BitmapFactory.decodeFile(String path)`获取到的是immutable的
* 可以使用`Bitmap copy(Config config, boolean isMutable)`获取mutable位图用于修改位图pixels.


### inDesity

* 设置位图的像素密度，即每英寸有多少个像素
* 如果inScale设置了，同时inDensity的值和inTargetDensity不同时，这个时候图片将缩放位inTartgetDensity指定的值.
* 如果设置为0，则`BitmapFactory.decodeResource(Resources,int)`和`BitmapFactory.decodeResource(Resources, int,BitmapFactory.Options)`，`BitmapFactory.decodeResourceStream()` 将`inTargetDensity`用`DisplayMetrics.densityDpi`来设置，其它函数则不会对bitmap进行任何缩放。

###inTargetDensity:
* 设置绘制位图的屏幕密度,与inScale和inDesity一起使用,来对位图进行放缩.
* 如果设置为0， `BitmapFactory.decodeResource(Resources,int)`, `BitmapFactory.decodeResource(Resources, int, BitmapFactory.Options)`,`BitmapFactory.decodeResourceStream()`将按照DisplayMetrics的density处理.

### inScreenDensity
* 表示正在使用的实际屏幕的像素密度.纯粹用于运行在兼容性代码中的应用程序,其中inTargetDensity实际上是看到的应用程序的密度,而非真正的屏幕密度.
* inDesity, inTargetDensity,inScreenDensity这三个参数主是确定是否需要对bitmap进行缩放处理，如果缩放，缩放后的W和H应该是多少，缩放比例主要是通过：InTargetDenisity/inDensity作为缩放比例。

### inScale
* 当inScale设置为true时,且inDenstiy和inTargetDensity也不为0时,位图将在加载时(解码)时放缩去匹配inTargetDensity,在绘制到canvas时不会依赖图像系统放缩.
* BitmapRegionDecoder会忽略这个标记.
* 此标记默认为true,如果需要非放缩的位图,可以设置为false,9-patch图片会忽略这标记而自动放缩适配.
* 如果inPremultipled设置为false,并且图片有A通道,设置这个标记为true,会导致位图出现不正确的颜色.



### inTargetDensity,inScale,inDesity之间的关系:
说三者之间的关系前,先谈下系统位图放缩规则,做个试验(使用小米3作为测试机):将一张144*144的ic_lanucher.png(系统默认在xxhdpi包下)分别放置在hdpi,xhdpi,xxhdpi三个文件夹,打印出位图的大小.

 ```java

	Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_launcher);
	Log.d(TAG, "size:" + bitmap.getByteCount());
 ```

 ```java

	hdpi包size:331776
	xhdpi包size:186624
	xxhdpi包size:82944

 ```

我们知道一张`144*144`的 ic_lanucher.png所占的实际内存为 `144*144*4=82944`字节,那么为什么同一张图片放在不同包下表现不一样的大小?

屏幕密度与Drawable目录有着如下的关系:

|目录|	屏幕密度|
| :------------ |:---------------|
|drawable-ldpi|	120dpi|
|drawable-mdpi	|160dpi|
|drawable-hdpi	|240dpi|
|drawable-xhdpi|320dpi|
|drawable-xxhdpi|480dpi|

当使用decodeResuore()解码drawable目录下的图片时, 会根据手机的屏幕密度,到对应的文件夹中查找图片,如果图片存在于其他目录,则会对该图片进行放缩处理在显示,放缩处理的规则: 

**`scale= 设备屏幕密度/drawable目录设定的屏幕密度`**

**`图片内存=int(图片长度*scale+0.5f)* int(图片宽度*scale)*单位像素占字节数`**

由于实验使用的小米3,屏幕密度为480,则当图片放入在hdpi时:`scale= 480/240;`
图片放入xhdpi:`scale=480/320`;
图片放入xxhdpi时:`scale= 480/480`;

说完系统加载位图使用的放缩规则后,再来说说这三个标记之间的关系:

inDesity: 位图使用的像素密度
inTargetDesity: 设备的屏幕密度
inScale: 是否需要放缩位图

清楚这三者的含义,就可以在加载图片时,根据图片在不同设备上的使用,可以放缩来加载位图: 

**`放缩规则 scale= inTargetDensity/inDesity;`**

```java

 	BitmapFactory.Options options = new BitmapFactory.Options();
    options.inScaled = true;
    options.inDensity = getBitmapDensity();
    options.inTargetDensity =Resources.getSystem().getDisplayMetrics().densityDpi ;
    Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher, options);

```

### 开发中遇到的问题:

在手机上加载大图时根据屏幕的密度对图片进行缩放，因此我们使用最大的图片资源，这样的话对于任何的手机屏幕，都会对图像进行压缩，不会造成视觉上的问题.但Android系统升级到4.4之后，发现之前开发的App运行起来非常的卡，严重影响了用户体验。后来发现跟Bitmap.decodeByteArray的底层实现有关。而android4.4以前的BitmapFactory.cpp中nativeDecodeByteArray调用doDecode函数时不会根据density进行缩放处理.4.4后由于一张图片缩放加载后,在内存放大很多,导致内存占有量过大,造成卡顿.


###参考资料
* [**Android inpreferredconfig参数分析**](http://blog.csdn.net/ccpat/article/details/46834089)
* [**Bitmap的内存管理**](http://hukai.me/android-training-course-in-chinese/graphics/displaying-bitmaps/manage-memory.html)
* [**高效加载大图片**](http://hukai.me/android-training-course-in-chinese/graphics/displaying-bitmaps/load-bitmap.html )
* [**Android坑档案：你的Bitmap究竟占多大内存？**](https://zhuanlan.zhihu.com/p/20732309?refer=bennyhuo)
