## strings.xml的用法:
* 普通`string`:
	
		<resources>    
    		<string name="string_name">text_string</string>
		</resources>
* 数组`string`

		<resources>
    		<string-array name="string_array_name">
        		<item>text_string</item>
    		</string-array>
		</resources>
		
		String[] planets = getResource.getStringArray(R.array.planets_array);
* `Plurals`(复数)
		
		<resources>
    		<plurals name="numberOfSongsAvailable">
        		<item quantity="one">One song found.</item>
        		<item quantity="other">%d songs found.</item>
    	</plurals>
		
		String songsFound = res.getQuantityString(R.plurals.numberOfSongsAvailable, count);
</resources>
* `Formatting and Styling`格式化 占位
	* 注意撇号和引号:
	
			<string name="good_example">This\'ll work</string>
			<string name="good_example_2">"This'll also work"</string>
			<string name="bad_example">This doesn't work</string>
	  		
			<string name="good_example">This is a \"good string\".</string>
			<string name="bad_example">This is a "bad string".</string>
			<string name="bad_example_2">'This is another "bad string".'</string>
	* 格式化字符串:
		> <string name="data">整数型:%1$d，浮点型：%2$.2f，字符串:%3$s</string>
		
			* %后面是占位符的位置,从1开始,$后面是填充数据的类型
			* $d:表示整形
			* $f:表示浮点型,其中f前.2表示小数的位数
			* $s:表示字符串
		* 在strings.xml中定义:
			> `<string name="welcome_messages">Hello, %1$s! You have %2$d new messages.</string>`
		* 代码中引用:
			>` String text = String.format(getResource.getString(R.string.welcome_messages), username, mailCount);`

### SpannableString使用:
* 有时候需要对文本进行各种特别是设置,比如颜色,大小,首行缩进,或者在段落文本中加入图片,甚至书写特殊的公式,使用控件可以实现,但是会增加布局的层次,维护复杂,在html中,可以使用各种标签来实现这些的特殊的需求,android中有类似的机制,就是Spannable对象
* Spannale继承Spanned接口,Spanned接口继承CharSequence接口,TextView中setText(CharSequence text)方法中,正好是传入CharSequence对象,所以可以直接把Spannable对象给TextView显示.通常可以如下构建:
	* `SpannaleString str= new Spannable("字符串");`
	* `SpannableStringBuilder strb= new SpannableStringBuilder("字符串");`
	
* **Spannale**对象设置样式:

	* 构建Spannable对象后,可使用`spannable.setSpan(what,start,end,flags)方法来设置样式:
		* Object what: 具体样式实现的对象;
		* int start: 样式开始的位置
		* int end: 样式接受的位置
		* int flags有四种,如下:
			* `Spanned.SPAN_EXCLUSIVE_EXCLUSIVE` --- 不含两端
			* `Spanned.SPAN_EXCLUSIVE_INCLUSIVE` --- 包右不包左
			* `Spanned.SPAN_INCLUSIVE_EXCLUSIVE` --- 包左不包右
			* `Spanned.SPAN_INCLUSIVE_INCLUSIVE` --- 包两端
* **what** 样式分析:
	> 用来构建的样式都在android.text.style包下
	
	* `AbsoluteSizeSpan`绝对尺寸
		* `AbsoluteSizeSpan(int size)`：参数size， 以size的指定的像素值来设定文本大小。
		* `AbsoluteSizeSpan(int size, boolean dip)`：参数size，以size的指定像素值来设定文本大小，如果参数dip为true则以size指定的dip为值来设定文本大小。
		* `AbsoluteSizeSpan(Parcel src)`：参数src，包含有size和dip值的包装类。
		
	* `AlignmentSpan.Standard` 标准文本对其样式
		* `AlignmentSpan.Standard(Layout.Alignment align)`：
			> 参数align是Layout.Alignment类型的枚举值。包括居中、正常和相反三种情况。
		* `AlignmentSpan.Standard(Parcel src)`：
			> 参数src，包含有标准字符串的Parcel类，其值应为"ALIGN_CENTER"、"ALIGN_NORMAL"或"ALIGN_OPPOSITE"中的之一，对应Layout.Alignment枚举中的三个类型。
			
	* `BackgroundColorSpan`背景样式
		* B`ackgroundColorSpan(int color)`：参数color，颜色值。
		* `BackgroundColorSpan(Parcel src)`：参数src，包含颜色值信息的包装类，
	* `BulletSpan` 
		> 着重样式,类似于HTML中的`<li>`标签的圆点效果
		* `BulletSpan()`：仅提供一个与文本颜色一致的符号。
		* `BulletSpan(int gapWidth)`： 提供一个与文本颜色一致的符号，并指定符号与后面文字之间的空白长度。
		* `BulletSpan(int gapWidth,int color)`：提供一个指定颜色的符号，并指定符号与后面文字之间的宽度。
		* `BulletSpan(Parcel src)`：参数src，包含宽度、颜色信息的包装类，
		
	* `DrawableMarginSpan`
		> 图片+Margin样式，
		* `DrawableMarginSpan(Drawable b)`：参数b，用于显示的图片。
		* `DrawableMarginSpan(Drawable b,int pad)`：参数b，用于显示的图片，参数pad，* 图片和文字的距离。

	* `ForegroundColorSpan`
		> 字体颜色样式，用于改变字体颜色。该类有两个构造函数：
		* `ForegroundColorSpan(int color)`：参数color，字体颜色。
		* F`oregroundColorSpan(Parcel src)`：参数src，包含字体颜色信息的包装类
		
	* `IconMarginSpan`
		> 图标+Margin样式，该类与DrawableMarginSpan使用上很相似。本类有两个构造函数：
		* `IconMarginSpan(Bitmap b)`：参数b，用于显示图像的bitmap。
		* `IconMarginSpan(Bitmap b,int pad)`：参数b，用于显示图像的bitmap，参数pad，Bitmap和文本之间的间距。
	* .LeadingMarginSpan
		>LeadingMarginSpan.Standard，文本缩进的样式。有3个构造函数，分别为：
		* Standard(int arg0)：参数arg0，缩进的像素。
		* Standard(int arg0, int arg1)：参数arg0，首行缩进的像素，arg1，剩余行缩进的像素。
		* Standard(Parcel p)： 参数p，包含缩进信息的包装类。
		
	* ImageSpan
		>图片样式，主要用于在文本中插入图片。本类构造函数较多，但主要是针对Bitmap和Drawable的，也可以通过资源Id直接加载图片。如下：
		* `ImageSpan(Bitmap b)`：.参数b，用于显示的Bitmap。该方法已过时，改用Use ImageSpan(Context, Bitmap)代替。
		* `ImageSpan(Bitmap b, int verticalAlignment)`：参数b，用于显示的Bitmap，参数verticalAlignment，对齐方式，对应ImageSpan中的常量值。该方法已过时，改用ImageSpan(Context, Bitmap, int)代替。
		* `ImageSpan(Context context, Bitmap b)`：参数context，传入的上下文，参数b，用于显示的Bitmap。
		* `ImageSpan(Context context, Bitmap b, int verticalAlignment)`：参数context，传入的上下文，参数b，用于显示的Bitmap，参数verticalAlignment，对齐方式。
		* `ImageSpan(Drawable d)`：参数d，用于显示的Drawable，此Drawable须设置大小。
		* `ImageSpan(Drawable d, int verticalAlignment)`：参数d，用于显示的Drawable，参数verticalAlignment，对齐方式。
		* `ImageSpan(Drawable d, String source`)：参数d，用于显示的Drawable，参数source，资源字符串。
		* `ImageSpan(Drawable d, String source, int verticalAlignment)`：参数d，用于显示的Drawable，参数source，资源字符串，参数verticalAlignment，对齐方式。
		* `ImageSpan(Context context, Uri uri)`：参数context，传入的上下文，参数uri，图片的uri。
		* `ImageSpan(Context context, Uri uri, int verticalAlignment)`：参数context，传入的上下文，参数uri，图片的uri，参数verticalAlignment，对齐方式。
		* `ImageSpan(Context context, int resourceId)`：参数context，传入的上下文，参数resourceId，图片的资源id。
		* `ImageSpan(Context context, int resourceId, int verticalAlignment)`参数context，传入的上下文，参数resourceId，图片的资源id，参数verticalAlignment，对齐方式。