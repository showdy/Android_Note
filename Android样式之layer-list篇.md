## Android样式之layer-list篇
![](img/layer-list.jpeg)

### 问题引入:
* **如何使用layer-list制作出如上图的效果**?

* 上图Tab的背景效果，和带阴影的圆角矩形，是怎么实现的呢？大部分的人会让美工切图，用点九图做背景。但是，如果只提供一张图，会怎么样呢？比如，中间的Tab背景红色底线的像素高度为4px，那么，在mdpi设备上显示会符合预期，在hdpi设备上显示时会细了一点点，在xhdpi设备上显示时会再细一点，在xxhdpi上显示时又细了，在xxxhdpi上显示时则更细了。因为在xxxhdpi上，1dp=4px，所以，4px的图，在xxxhdpi设备上显示时，就只剩下1dp了。所以，为了适配好各种分辨率，必须提供相应的多套图片。如果去查看android的res源码资源，也会发现，像这种Tab的背景点九图，也根据不同分辨率尺寸提供了不同尺寸的点九图片。

* 但是，在这个demo里，都没有用到任何实际的图片资源，都是用shape、selector，以及本篇要讲解的layer-list完成的。使用layer-list可以将多个drawable按照顺序层叠在一起显示，像上图中的Tab，是由一个红色的层加一个白色的层叠在一起显示的结果，阴影的圆角矩形则是由一个灰色的圆角矩形叠加上一个白色的圆角矩形。先看下代码吧，以下是Tab背景的代码：

		<?xml version="1.0" encoding="utf-8"?>
		<selector xmlns:android="http://schemas.android.com/apk/res/android">
		    <!-- 第一种加载方式 -->
		    <!--<item android:drawable="@drawable/bg_tab_selected" android:state_checked="true" />-->
		    <!-- 第二种加载方式 -->
		    <item android:state_checked="true">
		        <layer-list>
		            <!-- 红色背景 -->
		            <item>
		                <color android:color="#E4007F" />
		            </item>
		            <!-- 白色背景 -->
		            <item android:bottom="4dp" android:drawable="@android:color/white" />
		        </layer-list>
		    </item>
		    <item>
		        <layer-list>
		            <!-- 红色背景 -->
		            <item>
		                <color android:color="#E4007F" />
		            </item>
		            <!-- 白色背景 -->
		            <item android:bottom="1dp" android:drawable="@android:color/white" />
		        </layer-list>
		    </item>
		</selector>
* 以下是带阴影的圆角矩形：

		<?xml version="1.0" encoding="utf-8"?>
		<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
		    <!-- 灰色阴影 -->
		    <item
		        android:left="2dp"
		        android:top="4dp">
		        <shape>
		            <solid android:color="@android:color/darker_gray" />
		            <corners android:radius="10dp" />
		        </shape>
		    </item>
		    <!-- 白色前景 -->
		    <item
		        android:bottom="4dp"
		        android:right="2dp">
		        <shape>
		            <solid android:color="#FFFFFF" />
		            <corners android:radius="10dp" />
		        </shape>
		    </item>
		</layer-list>
* 从上面的示例代码可以看到，layer-list可以作为根节点，也可以作为selector中item的子节点。layer-list可以添加多个item子节点，每个item子节点对应一个drawable资源，按照item从上到下的顺序叠加在一起，再通过设置每个item的偏移量就可以看到阴影等效果了。layer-list的item可以通过下面四个属性设置偏移量：
	* android:top 顶部的偏移量
	* android:bottom 底部的偏移量
	* android:left 左边的偏移量
	* android:right 右边的偏移量
> 这四个偏移量和控件的margin设置差不多，都是外间距的效果。如何不设置偏移量，前面的图层就完全挡住了后面的图层，从而也看不到后面的图层效果了。比如上面的例子，Tab背景中的白色背景设置了android:bottom之后才能看到一点红色背景。那么如果偏移量设为负值会怎么样呢？经过验证，偏移超出的部分会被截掉而看不到，不信可以自己试一下。有时候这很有用，比如当我想显示一个半圆的时候。

* 另外，关于item的用法，也做下总结：
	* 根节点不同时，可设置的属性是会不同的，比如selector下，可以设置一些状态属性，而在layer-list下，可以设置偏移量；
	* 就算父节点同样是selector，放在drawable目录和放在color目录下可用的属性也会不同，比如drawable目录下可用的属性为android:drawable，在color目录下可用的属性为android:color；
	* item的子节点可以为任何类型的drawable类标签，除了上面例子中的shape、color、layer-list，也可以是selector，还有其他没讲过的bitmap、clip、scale、inset、transition、rotate、animated-rotate、lever-list等等。
