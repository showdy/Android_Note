##android样式之shape篇
* 一个有用应该保持一套统一的样式,包括Button,EditText,ProgressBar,Toast,CheckBox等各个控件的样式,还包括控件间隔,文字大小和颜色,阴影等等.web的样式用css来定义,而android 的样式主要通过shape,selector,layer-list,level-list,style,theme等组合实现.
### shape的四种形状
> 一般使用shape定义的xml文件放在drawable目录下,使用shape主要自定义形状,通过android:shape属性指定:

* rectangle: 矩形,默认的形状,可画直角矩形,圆角矩形,弧形
* oval: 椭圆,用得比较多的是画圆
* line: 线性,用来画实现或者虚线
* ring: 环形,可以画环形进度条

### rectange矩形
* rectangle 是默认的形状,也是用的最多的形状,一些文字背景,按钮背景,控件或者布局背景等.
* rectangle的特性:
	* solid: 设置形状填充的颜色,只有一个android:color属性
	* padding:设置内容与形状边界的内间距,可分别设置上下左右距离:
		* android:left
		* android:right
		* android:top
		* android:bottom
	* gradient: 设置形状的渐变颜色,可以线性渐变,辐射渐变,扫描渐变
		* android:type 渐变的类型
			* linear 线性渐变
			* radial(android:gradientRadius必须设置) 辐射渐变
			* sweep扫描渐变
		* android:startColor渐变开始的颜色
		* android:endColor 渐变结束的颜色
		* android:centerColor 中将的颜色
		* android:angle 渐变的角度,线性渐变才有效,必须是45的倍数,0为左到右,90为上到下
		* android:centerX 渐变中心的相对x坐标,放射渐变才有效,在0-1.0之间,默认为0.5
		* android:centerY 渐变中心相对Y坐标,放射渐变才有效,在0-1.0之间,默认为0.5
		* android:gradientRadius渐变的半径,放射渐变才有效
		* android:useLevel如果为true,则可在LevelListDrawable中使用
	* corners: 设角度圆角,只适合rectangle类型,可以分别设置四个角不同半径的圆角,当角度半径很大时,就为圆弧
		* radius 圆弧半径
		* topLeftRadius 左上角的半径
		* topRightRadius 右上角的半径
		* bottomLeftRadius 左下角半径
		* bottomRightRadius 右下角半径 
	* stroke: 设置描边,可绘虚线和实线
		* android: color 线的颜色
		* android:width 描边的宽度
		* anroid:dashWidth 设置虚线时横线长度
		* android:dashGap 设置虚线时横线间距

### Oval 椭圆
* 实际开发中使用ova一般绘制圆形,只需要设置size的height和width一致即可
* oval设置的属性:
	* size: 设置形状默认的大小
		* android:width
		* android:height
	* solid: 设置圆填充的颜色
	* padding:内容与边界的距离
	* stroke: 描边
	* gradient:渐变,设置radial渐变时,必须设置android:gradientRadius属性

### line 线性
* 主要用来绘制分割线,通过stroke和size组合实现
* 有几点需要注意:
	* 只能画水平线,画不了竖线
	* 线的高度是通过stroke的width设置
	* size的height必须大于stroke的width,否则无法显示
	* 线在整个形状区域中是居中显示的
	* 线左右两边的会留有空白间距,线越粗,空白越大
	* 引用虚线的view需要设置属性anroid:layerType="software",否则显示不了虚线
	
### ring 圆环
* shape根元素有些只有ring才能使用如下:
	* android:innerRadius内环的半径
	* android:innerRaiusRatio 浮点型,以环的宽度比率来表示内环的半径,默认为3,表示内环半径为环的宽度除以3,该值会被android:innerRandius覆盖
	* anroid:thickness 环的厚度
	* android:thicknessRadtio 浮点型,以环的宽度比率来表示环的厚度,默认为9,表示环的厚度为环宽度除以9,该值会被android:thickness覆盖
	* android:useLevel一般为false,否则环形可能无法显示,只有作为LevelListDrawable使用时才为true
* 如进度条:

		<rotate xmlns:...
			android:fromDegress="0"
			android:pivoX="50%"
			android:pivoY="50%"
			android:toDegress="1080.0">
			<shape 
				innerRadiusRatio="3"
				android:thicknessRatio="8"
				android:useLevel="false">
				<gradient
					android:startColor="#ffffff"
					android:endColor="#2f90BD"
					anroid:type="sweep"/>
			</shape>
		</rotate>