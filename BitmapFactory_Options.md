### inPreferredConfig(建议的配置参数)
**[位图解码](http://blog.csdn.net/ccpat/article/details/46834089)**

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
		* ALPHA_8: 每个像素用8个byte存储,存储的是图片的透明值
		* RGB_565:每个像素用16个byte存储,分别为5-R,6-G,5-B通道.
		* ARGB-4444:每个像素用16个byte存储,即每个通道用4位表示
		* ARGB_8888:每个像素用32个byte存储,每个通道用8位表示.
		
* 图片解码时,默认使用ARGB_8888模式:

	*  `Bitmap.Config inPreferredConfig = Bitmap.Config.ARGB_8888;`
	*  `ARGB_4444已deprecated,在KITKAT(Android4.4)以上,会用ARGB_8888代替ARGB_4444`

* 使用inperferredConfig注意:

	* 如果inPreferredConfig不为null，解码器会尝试使用此参数指定的颜色模式来对图片进行解码，如果inPreferredConfig为null或者在解码时无法满足此参数指定的颜色模式，解码器会自动根据原始图片的特征以及当前设备的屏幕位深，选取合适的颜色模式来解码，例如，如果图片中包含透明度，那么对该图片解码时使用的配置就需要支持透明度，默认会使用ARGB_8888来解码。 
	* 根据inperferredConfig的解析,就会发现如下bm1,bm2,bm3结果会一样:
	
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
* 疑点: 1. 当出现不满足情况时，使用的合适配置是如何选取的？ 
	* 1. 如果inPreferredConfig为null，解码时使用的颜色模式会根据图片源文件的类型进行选取，如果图片文件的颜色模式为CMYK，或RGB565，则选取RGB_565。如果是其他类型，则选取ARGB_8888。 
	* 2. 如果inPreferredConfig指定的选项在解码时无法满足，并不会再根据图片文件的类型来选取合适的选项，而是直接使用ARGB_8888选项来解码。例如，图片源文件为RGB566编码的BMP图片，使用ALPHA_8选项来解码时属于不满足的情况，这时会选取ARGB_8888选项来解码，而不是选取RGB565。和inPreferredConfig为null时选取的“合适的”选项并不相同。
	
* 疑点: 2. 什么情况下使用什么样的配置会出现不满足的情况？ 
	* 所有情况下ARGB_8888配置都可以满足
	* 所有情况下ALPHA_8配置都不满足
	* 绝大多数情况下RGB565选项都不满足

### inPremultiplied
### inBitmap
[**Bitmap的内存管理**](http://hukai.me/android-training-course-in-chinese/graphics/displaying-bitmaps/manage-memory.html)

* 在Android 2.2 (API level 8)以及之前，当垃圾回收发生时，应用的线程是会被暂停的，这会导致一个延迟滞后，并降低系统效率。 从Android 2.3开始，添加了并发垃圾回收的机制， 这意味着在**一个Bitmap不再被引用之后，它所占用的内存会被立即回收**。

* 在Android 2.3.3 (API level 10)以及之前, **一个Bitmap的像素级数据（pixel data）是存放在Native内存空间中的**。 这些数据与Bitmap本身所占内存是隔离的，**Bitmap本身被存放在Dalvik堆中**。我们无法预测在Native内存中的像素级数据何时会被释放，这意味着程序容易超过它的内存限制并且崩溃。 **自Android 3.0 (API Level 11)开始， 像素级数据则是与Bitmap本身一起存放在Dalvik堆中**。

* 在Android 2.3.3 (API level 10) 以及更低版本上，**推荐使用recycle()方法**。 如果在应用中显示了大量的Bitmap数据，我们很可能会遇到OutOfMemoryError的错误。 recycle()方法可以使得程序更快的释放内存。
	>Caution：只有当我们确定这个Bitmap不再需要用到的时候才应该使用recycle()。在执行recycle()方法之后，如果尝试绘制这个Bitmap， 我们将得到"Canvas: trying to use a recycled bitmap"的错误提示。
	
* 从Android 3.0 (API Level 11)开始，引进了**BitmapFactory.Options.inBitmap**字段。 如果使用了这个设置字段，decode方法会在加载Bitmap数据的时候去重用已经存在的Bitmap。这意味着Bitmap的内存是被重新利用的，这样可以提升性能，并且减少了内存的分配与回收。
	* You should still always use the returned Bitmap of the decode method and not assume that reusing the bitmap worked, due to the constraints outlined above and failure situations that can occur.
	
		* 总是使用解码方法，因为不能保证重用的bitmap会起作用,(例如,位图大小不匹配就无法重用)　
		
	* As of {@link android.os.Build.VERSION_CODES#KITKAT}, any mutable bitmap can be reused by {@link BitmapFactory} to decode any　other bitmaps as long as the resulting {@link Bitmap#getByteCount()　byte count} of the decoded bitmap is less than or equal to the {@link Bitmap#getAllocationByteCount() allocated byte count} of the reused bitmap．
		* 版本4.4后,任何mutable的图片都可以被BitmapFactory重用成任何其他的位图,只要源位图的大小(Bitmap.getByteCount())比重用位图(Bitmap.getAllocateByteCount)小或者相等
		* 版本KITKAT前,重用的位图大小必须和源位图大小相同,而且位图格式必须是JPEG或者PNG(无论是流形式还是资源形式图片)
		* 如果重用位图设置了Bitmap.Config.configuration将会覆盖inperferredConfig的设置,如果有的话.

### inJustDecodeBounds / inSmapleSize
[**高效加载大图片**](http://hukai.me/android-training-course-in-chinese/graphics/displaying-bitmaps/load-bitmap.html )

* 如果设置为true,将不返回bitmap, 但是Bitmap的outWidth,outHeight等属性将会赋值,允许调用查询Bitmap,而不需要为Bitmap分配内存.
* 例如加载一张很大的位图, 如果直接解码会造成OOM,做法是:
	* 1.先拿到位图的尺寸后,进行放缩后再加载位图

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeResource(getResources(), R.id.myimage, options);
			int imageHeight = options.outHeight;
			int imageWidth = options.outWidth;
			String imageType = options.outMimeType;
	* 2.计算inSampleSize
	
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
		>**设置inSampleSize为2的幂是因为解码器最终还是会对非2的幂的数进行向下处理，获取到最靠近2的幂的数。详情参考inSampleSize的文档**
	* 3.放缩后再加载小位图:
	
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
		
### inMutable
> 如果设置为true,将返回一个mutable的bitmap,可用于修改BitmapFactory加载而来的bitmap's effects.

* BitmapFactory.decodeResource(Resources res, int id)获取到的bitmap是mutable的，而BitmapFactory.decodeFile(String path)获取到的是immutable的
* 可以使用Bitmap copy(Config config, boolean isMutable)获取mutable位图用于修改位图pixels.


### inDesity, inTargetDensity,inScreenDensity,inScale
	