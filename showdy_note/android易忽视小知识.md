### Android开发中易忽视的小知识点:
#### 1.开启隐式意图要判断:
	static final int REQUEST_IMAGE_CAPTURE = 1;
	private void dispatchTakePictureIntent() {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
	    }
	}

#### 2.string.xml提示错误
* 编辑strings.xml的时候,在行`<string name="myurl">http://code.dd.com/rr?q=%rr.55</string>`提示下面的错误
    `Multiple annotations found at this line:`
    - `error: Multiple substitutions specified in non-positional format; did you mean to add`
    the formatted="false" attribute?
    - `error: Unexpected end tag string`
    出现这个错误的原因主要是因为strings字串中包含百分号(%),
 
* 有几种方式解决
    - 1.用两个百分号表示一个百分号即
    `<string name="myurl">http://code.dd.com/rr?q=%%rr.55</string>`
    - 2.用转义符表示
    `<string name="myurl">http://code.dd.com/rr?q=\%rr.55</string>`
    - 3.根据错误提示可知,如果字符串无需格式化，可在`<string 标签上增加属性:formatted="false"`,即
    `<string name="myurl" formatted="false">http://code.dd.com/rr?q=%rr.55</string>`

#### 3.proguard删除log信息
[http://stackoverflow.com/questions/12390466/android-proguard-not-removing-all-log-messages](http://stackoverflow.com/questions/12390466/android-proguard-not-removing-all-log-messages "参考stackoverflow")

	# 删除所有Log
	-assumenosideeffects class android.util.Log { *; }
	# 删除log.d和log.e
	-assumenosideeffects class android.util.Log {
	    public static *** d(...);
	    public static *** e(...);
	}

#### 4. Button默认点击效果
* 5.0+(水波纹效果))5.0-(阴影效果)

		android:background="?android:attr/selectableItemBackground"
* 继承样式:

		<!--按钮-->
	    <style name="PersonInfoButton" parent="@android:style/ButtonBar">
	        <item name="android:layout_width">80dp</item>
	        <item name="android:layout_height">32dp</item>
	        <item name="android:textSize">14sp</item>
	    </style>
		<!--系统样式-->
		<style name="ButtonBar">
	        <item name="paddingTop">5dip</item>
	        <item name="paddingStart">4dip</item>
	        <item name="paddingEnd">4dip</item>
	        <item name="paddingBottom">1dip</item>
	        <item name="background">@drawable/bottom_bar</item>
	    </style>

#### TextView的标准字体

* 样式:

		style="@style/TextAppearance.AppCompat.Display4"
		style="@style/TextAppearance.AppCompat.Display3"
		style="@style/TextAppearance.AppCompat.Display2"
		style="@style/TextAppearance.AppCompat.Display1"
		style="@style/TextAppearance.AppCompat.Headline"
		style="@style/TextAppearance.AppCompat.Title"
		style="@style/TextAppearance.AppCompat.Subhead"
		style="@style/TextAppearance.AppCompat.Body2"
		style="@style/TextAppearance.AppCompat.Body1"
		style="@style/TextAppearance.AppCompat.Caption"
		style="@style/TextAppearance.AppCompat.Button"

* 对应字体:
![](img/android_default_textsize.png)