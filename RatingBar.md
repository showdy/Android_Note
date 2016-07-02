###相关属性：
* android:isIndicator：是否用作指示，用户无法更改，默认false
* android:numStars：显示多少个星星，必须为整数
* android:rating：默认评分值，必须为浮点数
* android:stepSize： 评分每次增加的值，必须为浮点数

### 系统样式:
* style="?android:attr/ratingBarStyleSmall"
* style="?android:attr/ratingBarStyleIndicator"
### 监听事件
*  Ratingbar.onRatingChanged (RatingBar ratingBar, float rating, boolean fromUser) 

| 参数| 意义| 翻译|
| ------------- |:-------------| :-----|
| ratingBar	 | The RatingBar whose rating has changed. | 监听对象ratingbar |
| rating | The current rating. This will be in the range 0..numStars. |当前评分(0-numStarts) |
|fromUser | True if the rating change was initiated by a user's touch gesture or arrow key/horizontal trackbell movement.| 如果是人为触摸或方向键水平滑动导致返回为true |

### 自定义RatingBar样式
* ratingbar_list.xml

		<?xml version="1.0" encoding="utf-8"?>
			<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
			<item android:id="@android:id/background"
			android:drawable="@mipmap/ic_rating_off1" />
			<item android:id="@android:id/secondaryProgress"
			android:drawable="@mipmap/ic_rating_off1" />
			<item android:id="@android:id/progress"
			android:drawable="@mipmap/ic_rating_on1" />
		</layer-list> 

* styles.xml
	
		<style name="roomRatingBar" parent="@android:style/Widget.RatingBar">
	        <item name="android:progressDrawable">@drawable/ratingbar_full</item>
	        <item name="android:minHeight">24dip</item>
	        <item name="android:maxHeight">24dip</item>
	    </style>
* 布局中引用:

		<RatingBar
	        android:id="@+id/rb_normal"
	        style="@style/roomRatingBar"
	        android:layout_width="wrap_content"
	        android:layout_height="wrap_content" />

