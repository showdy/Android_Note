# 管理系统UI
* 淡化系统Bar
* 隐藏系统Bar
* 隐藏导航栏
* 沉浸式模式
* 响应UI可见变化

### android系统UI各种Flag
##### 相关APIs:
* Window#Flag
* View.setSystemUiVisiblity(android3.0开始)

#### 相关Flag

* WindowManager.LayoutParam.FLAG_FULLSCREEN 隐藏状态栏
	> Window flag: hide all screen decorations (such as the status bar) while this window is displayed.

* View.SYSTEM_UI_FLAG_VISIBLE API 14
	> 默认标记

* View.SYSTEM_UI_FLAG_LOW_PROFILE API 14
	> 低调模式, 会隐藏不重要的状态栏图标

* View.SYSTEM_UI_FLAG_LAYOUT_STABLE API 16
	> 保持整个View稳定, 常和控制System UI悬浮, 隐藏的Flags共用, 使View不会因为System UI的变化而重新layout

* View.SYSTEM_UI_FLAG_FULLSCREEN API 16
	> 状态栏隐藏，效果同设置WindowManager.LayoutParams.FLAG_FULLSCREEN

* View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN API 16
	> 视图延伸至状态栏区域，状态栏上浮于视图之上

* View.SYSTEM_UI_FLAG_HIDE_NAVIGATION API 14
	> 隐藏导航栏

* View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION API 16
	> 视图延伸至导航栏区域，导航栏上浮于视图之上

* View.SYSTEM_UI_FLAG_IMMERSIVE API 19
	> 沉浸模式, 隐藏状态栏和导航栏, 并且在第一次会弹泡提醒, 并且在状态栏区域滑动可以呼出状态栏（这样会系统会清楚之前设置的View.SYSTEM_UI_FLAG_FULLSCREEN或View.SYSTEM_UI_FLAG_HIDE_NAVIGATION标志）。使之生效，需要和View.SYSTEM_UI_FLAG_FULLSCREEN，View.SYSTEM_UI_FLAG_HIDE_NAVIGATION中的一个或两个同时设置。

* View.SYSTEM_UI_FLAG_IMMERSIVE_STIKY API 19
	> 与上面唯一的区别是, 呼出隐藏的状态栏后不会清除之前设置的View.SYSTEM_UI_FLAG_FULLSCREEN或View.SYSTEM_UI_FLAG_HIDE_NAVIGATION标志，在一段时间后将再次隐藏系统栏）