## 系统相机相册获取图片并裁剪Android N适配
--
### 一.基础讲解:

调用系统的拍照,相册选取图片并裁剪,一般使用系统的自带隐式意图Intent实现:

* 拍照 `MediaStore.ACTION_IMAGE_CAPTURE`

```java

	public static final java.lang.String ACTION_IMAGE_CAPTURE = "android.media.action.IMAGE_CAPTURE"
```

* 启动相册:`Intent.ACTION_GET_CONTENT`

```java

	public static final java.lang.String ACTION_GET_CONTENT = "android.intent.action.GET_CONTENT"
```

* 启动裁剪: `com.android.camera.action.CROP `
	使用裁剪的功能通过intent.putExtra("key","value")实现.

| 附加选项  | 数据类型 | 描述 |
| :------------ |:---------------:| -----:|
| crop     | String | 发送裁剪信号 |
| aspectX     | int       |X方向上的比例   |
|aspectY|int|Y方向上的比例|
|outputX|int|裁剪区的宽|
|outputY|int|裁剪区的高|
|scale|boolean|是否保留比例|
|return-data|boolean|是否将数据保留在Bitmap中返回|
|data|Parcelable|相应的Bitmap数据|
|circleCrop|String|圆形裁剪区域|
|MediaStore.EXTRA_OUTPUT|URI|将URI指向相应的file://|
|outputFormat|String|输出格式(Bitmap.CompressFormat.JPEG.toString())|
|noFaceDetection|boolean| 是否取消人脸识别|


关于return-data和MediaStore.EXTRA_OUTPUT:

* return data: 是将结果保存在data中,在onActivityResult时,直接调用intent.getdata()可得到,这里设置设置为false,即不保存在data中.

* MediaStore.EXTRA_OUTPUT:拍照生成的图片由于没有保存在data,需要有个地方保存图片,而这key-value就是指图片保存的URO地址.

> 注意: return-data如果设置为true,对应有些手机只会得到缩略图,一般设置为false,一直用URI来输出.而URI在有些手机上也会有问题.

* 权限:在Android6.0以上要检查权限是否授予:

```java

    <uses-permission android:name="android.permission.CAMERA"/>
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>

```

### 关于Android7.0 StrictMode政策

请参考: [Android7.0适配心得](http://www.devio.org/2016/09/28/Android7.0%E9%80%82%E9%85%8D%E5%BF%83%E5%BE%97/#在Android7.0上调用系统相机拍照，裁切照片)

**权限更改:**

由于随着Android版本越来越高,Android对用户隐私保护力度越来越大,从Android6.0引入**动态权限控制**(Runtime Permission)到Android7.0私有目录被限制访问,"**StrictMode API政策**".由于之前Android版本中,是可以读取到手机存储中任何一个目录及文件,这带来很多安全问题.在Android7.0中为了提高私有文件的安全性.面向Android N或者更高版本将被限制访问.


**目录限制被访问**

在Android7.0中为了提高私有文件的安全性，面向 Android N 或更高版本的应用私有目录将被限制访问。

* 私有文件的文件权限不再放权给所有的应用,使用`MODE_WORLD_READABL`E或者`MODE_WORLD_WRITEABLE`进行操作触发`SecurityException`.这使得无法通过File API访问手机存储上的数据了,基于File API的一些文件浏览器也将受到很大影响.

* 给其他应用传递file://URI类型的Uri,可能会导致接收者无法访问该路径,因为在Android7.0中传递`file://URI`会触发`FileUriExposedException`.可以通过FileProvider来解决.

* DownloadManager不再按文件名分享私人存储的文件,`COLUMN_LOCAL_FILENAME在Android7`.0中标记为deprecate,旧版应用在访问`COLUMN_LOCAL_FILENAME`可能会出现无法访问的路径.面向Android N或者更高版本中应用尝试访问`COLUMN_LOCAL_FILENAME`时会触发`SecurityException.`但可以通过`ContentResolver.openFileDescriptor()`来访问`DownloadManager`公开的文件.

### 拍照获取图片URI:

在Android7.0之前拍照,并获取图片URI如下:

```java

 	private void takePictureFromCamera() {
		//采用时间戳命名图片名称,不至于图片名称重复
        String pictureName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault()).format(new Date()) +
                "-" + System.currentTimeMillis() + ".jpg";
        mOutputImage = new File(getExternalCacheDir(), pictureName);
        imageUri = Uri.fromFile(mOutputImage);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); //图片存储的地方.
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        ComponentName componentName = intent.resolveActivity(getPackageManager());
        if (componentName != null) {
            startActivityForResult(intent, REQUEST_CAPTURE);
        }
    }

```

但是在Android7.0(API24)上会报以下错误:

```java

	android.os.FileUriExposedException: file:///storage/emulated/0/Android/data/com.showdy.androiddemo/cache/2016-12-30-10-25-55-1483064755273.jpg exposed beyond app through Intent.getData()
	at android.os.StrictMode.onFileUriExposed(StrictMode.java:1799)
	at android.net.Uri.checkFileUriExposed(Uri.java:2346)
	at android.content.Intent.prepareToLeaveProcess(Intent.java:8933)
	at android.content.Intent.prepareToLeaveProcess(Intent.java:8894)
	at android.app.Instrumentation.execStartActivity(Instrumentation.java:1517)
	at android.app.Activity.startActivityForResult(Activity.java:4223)
	...
	at android.app.Activity.startActivityForResult(Activity.java:4182)

```

导致这崩溃的原因,就是Andorid N的 **`StrictMode`** 政策,但是我可以使用`FileProvider`来解决问题:使用步骤如下，

参考：[file:// scheme is now not allowed to be attached with Intent on targetSdkVersion 24 (Android Nougat). And here is the solution.](https://inthecheesefactory.com/blog/how-to-share-access-to-file-with-fileprovider-on-android-nougat/en)　

* 清单文件中注册provider

```java

	<provider
	    android:name="android.support.v4.content.FileProvider"
	    android:authorities="${applicationId}.fileprovider"
	    android:grantUriPermissions="true"
	    android:exported="false">
	    <meta-data
	        android:name="android.support.FILE_PROVIDER_PATHS"
	        android:resource="@xml/file_paths" />
	</provider>

```
exported:要求必须为false，为true则会报安全异常。

```java

	Java.lang.RuntimeException: Unable to get provider Android.support.v4.content.FileProvider: Java.lang.SecurityException: Provider must not be exported）。
```
grantUriPermissions:true，表示授予 URI 临时访问权限。


* 指定共享目录:

为了指定共享的目录需要在res目录下创建一个xml目录,然后配置file_paths(名字随意):

```java

<?xml version="1.0" encoding="utf-8"?>
<resources>
	<paths>
	   <external-path  name="images" path="" />
	</paths>
</resources>
```

path的可选配置如下:

```java

	<files-path name="name" path="path" /> //相当 Context.getFilesDir() + path, name是分享url的一部分
	
<cache-path name="name" path="path" /> //getCacheDir()
	
external-path name="name" path="path" /> //Environment.getExternalStorageDirectory()
	
<external-files-path name="name" path="path" />//getExternalFilesDir(String) Context.getExternalFilesDir(null)
	
<external-cache-path name="name" path="path" /> //Context.getExternalCacheDir()

```

 其中path="",代表根目录,如果是path="images",表示可以向其他应用共享根目录以及其子目录的任何文件. 则表示目录名为:/storage/emulated/0/images,如果你向其他应用分享images目录范围之外的文件是不行.

* 使用FileProvider:

上面拍照代码中指定了图片存储的imageUri为:imageUri=Uri.fromFile(mOutputImage);如果是Androd N(7.0)以上,imageUri的计算应该如下:

```java
	imageUri= imageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", mOutputImage);
	//来对目标应用临时授权该Uri所代表的文件。
intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
```

通过FileProviderde得到文件的路径得到文件的路径:
```java
content://com.showdy.androiddemo.provider/name/Android/data/com.showdy.androiddemo/cache/2016-12-30-10-25-55-1483064755273.jpg
```
而我们path设置为path="",这个content类型的Uri映射的File路径就为:
```java
/storage/emulated/0/Android/data/com.showdy.androiddemo/cache/2016-12-30-10-25-55-1483064755273.jpg
```

综合前面所述Android7.0Strict Mode政策问题,拍照的功能获取图片Uri的方法(当然配置文件也是需要的)就如下:

```java
	 private void takePictureFromCamera() {

        String pictureName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault()).format(new Date()) +
                "-" + System.currentTimeMillis() + ".jpg";

        mOutputImage = new File(getExternalCacheDir(), pictureName);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", mOutputImage);

            Log.e(TAG,imageUri.getPath());
        } else {
            imageUri = Uri.fromFile(mOutputImage);
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        ComponentName componentName = intent.resolveActivity(getPackageManager());
        if (componentName != null) {
            startActivityForResult(intent, REQUEST_CAPTURE);
        }
    }

```

那么在onActivityResult()方法就能将imageUri拿到,并设置给ImageView了.

```java

 	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CAPTURE: // 拍照
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    mImageView.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
        }
    }

```

### 获取系统相册图片

打开系统相册隐式方式很简单:

```java

 	//使用隐式意图打开系统相册
    private void takePictureFromAlum() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        ComponentName componentName = intent.resolveActivity(getPackageManager());
        if (componentName != null) {
            startActivityForResult(intent, REQUEST_ALBUM);
        }
    }

```

然后在onActivityResult()方法中解析相册的物理路径:

```java
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_ALBUM: 
               mImageView.setImageBitmap(BitmapFactory.decodeFile(parsePicturePath(this, data.getData())));
                
                break;
        }
    }

```

获取图片的物理路径如下:在API19之前和API19之后实现方式不一样:

```java

	// 解析获取图片库图片Uri物理路径
    @SuppressLint("NewApi")
    private String parsePicturePath(Context context, Uri uri) {

        if (null == context || uri == null)
            return null;

        boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentUri
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageDocumentsUri
            if (isExternalStorageDocumentsUri(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] splits = docId.split(":");
                String type = splits[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + File.separator + splits[1];
                }
            }
            // DownloadsDocumentsUri
            else if (isDownloadsDocumentsUri(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaDocumentsUri
            else if (isMediaDocumentsUri(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = "_id=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isGooglePhotosContentUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;

    }

    private String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        String column = "_data";
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            try {
                if (cursor != null)
                    cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;

    }

    private boolean isExternalStorageDocumentsUri(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private boolean isDownloadsDocumentsUri(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private boolean isMediaDocumentsUri(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private boolean isGooglePhotosContentUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

```


### 图片裁剪:

在Android7.0之前我们的裁剪方法如下:

```java

	public void cropPicture(File file) {

        String cropImageName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault()).format(new Date()) +
                "-1-" + System.currentTimeMillis() + ".jpg";
        File cropFile = new File(getExternalCacheDir(), cropImageName);
		//注意到此处使用的file:// uri类型.
        cropUri = Uri.fromFile(cropFile);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(Uri.fromFile(file), "image/*"); //此处有问题
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        ComponentName componentName = intent.resolveActivity(getPackageManager());
        if (componentName != null) {
            startActivityForResult(intent, REQUEST_PICTURE_CROP);
        }

```

很显然,intent.setDataAndType()中的uri是有问题的,因为Uri的类型很多(此处主要是content和file类型),那么不能简单的用Uri.fromfile(file)这个方法得到文件的uri,应该区分何时是File uri,何时是Content uri.修正办法如下:

```java

	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sourceUri = getImageContentUri(this, file);
    } else {
            sourceUri = Uri.fromFile(file);
    }

 	intent.setDataAndType(sourceUri, "image/*"); 
	
	//获取文件的Content uri路径 
    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }	
```

最后在onActivityResult()中获取到裁剪后的图片的物理地址即可:

```java

	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_PICTURE_CROP:
                if (cropUri != null) {
                    String path = parsePicturePath(this, cropUri);
				//  String imageName = path.substring(path.lastIndexOf("/") + 1); //得到图片名称
                    mImageView.setImageBitmap(BitmapFactory.decodeFile(path));
                }

                break;
        }

```


