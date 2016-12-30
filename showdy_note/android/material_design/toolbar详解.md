
## Toolbar用法详解:

### Toolbar的组成:
Toolbar supports a more focused feature set than ActionBar. From start to end, a toolbar may contain a combination of the following optional elements:

* A navigation button. This may be an Up arrow, navigation menu toggle, close, collapse, done or another glyph of the app's choosing. This button should always be used to access other navigational destinations within the container of the Toolbar and its signified content or otherwise leave the current context signified by the Toolbar. The navigation button is vertically aligned within the Toolbar's minimum height, if set.
* A branded logo image. This may extend to the height of the bar and can be arbitrarily wide.
* A title and subtitle. The title should be a signpost for the Toolbar's current position in the navigation hierarchy and the content contained there. The subtitle, if present should indicate any extended information about the current content. If an app uses a logo image it should strongly consider omitting a title and subtitle.
* One or more custom views. The application may add arbitrary child views to the Toolbar. They will appear at this position within the layout. If a child view's Toolbar.LayoutParams indicates a Gravity value of CENTER_HORIZONTAL the view will attempt to center within the available space remaining in the Toolbar after all other elements have been measured.
* An action menu. The menu of actions will pin to the end of the Toolbar offering a few frequent, important or typical actions along with an optional overflow menu for additional actions. Action buttons are vertically aligned within the Toolbar's minimum height, if set.

上面是引用了官方文档对Toolbar的介绍,可以知道Toolbar主要包括五部分:

* 导航按钮
* 应用Logo
* 标题与副标题
* 若干自定义View
* ActionMenu

![](img/toolbar.png)

### Toolbar样式

在系统value下style文件中可以看到系统定义的Toolbar的样式如下,如果需要更改toolbar的样式只需要更改对应的样式即可;

```java

	<style name="Base.Widget.AppCompat.Toolbar" parent="android:Widget">
        <item name="titleTextAppearance">@style/TextAppearance.Widget.AppCompat.Toolbar.Title</item>
        <item name="subtitleTextAppearance">@style/TextAppearance.Widget.AppCompat.Toolbar.Subtitle</item>
        <item name="android:minHeight">?attr/actionBarSize</item>
        <item name="titleMargin">4dp</item>
        <item name="maxButtonHeight">56dp</item>
        <item name="buttonGravity">top</item>
        <item name="collapseIcon">?attr/homeAsUpIndicator</item>
        <item name="collapseContentDescription">@string/abc_toolbar_collapse_description</item>
        <item name="contentInsetStart">16dp</item>
        <item name="contentInsetStartWithNavigation">72dp</item>
        <item name="android:paddingLeft">0dp</item>
        <item name="android:paddingRight">0dp</item>
    </style>

```

系统默认HomeAsUp图标:

```java

	<vector xmlns:android="http://schemas.android.com/apk/res/android"
        android:width="24dp"
        android:height="24dp"
        android:viewportWidth="24.0"
        android:viewportHeight="24.0"
        android:autoMirrored="true"
        android:tint="?attr/colorControlNormal">
    <path
            android:pathData="M20,11L7.8,11l5.6,-5.6L12,4l-8,8l8,8l1.4,-1.4L7.8,13L20,13L20,11z"
            android:fillColor="@android:color/white"/>
</vector>

```

系统Toolbar标题Title的默认样式

```java

 	 <style name="Base.TextAppearance.AppCompat.Widget.ActionBar.Title"parent="TextAppearance.AppCompat.Title">
        <item name="android:textSize">20dp</item>
        <item name="android:textColor">?android:attr/textColorPrimary</item>
    </style>
```
系统Toolbar副标题SubTitle的默认样式
```java

 	<style name="Base.TextAppearance.AppCompat.Widget.ActionBar.Subtitle" parent="TextAppearance.AppCompat.Subhead">
        <item name="android:textSize">16dp</item>
        <item name="android:textColor">?android:attr/textColorSecondary</item>
    </style>

```

系统Toolbar溢出菜单的默认样式:

```java

 	<style name="Base.Widget.AppCompat.Light.PopupMenu.Overflow">
        <item name="overlapAnchor">true</item>
        <item name="android:dropDownHorizontalOffset">-4dip</item>
    </style>

	<style name="Base.Widget.AppCompat.ListPopupWindow" parent="">
        <item name="android:dropDownSelector">?attr/listChoiceBackgroundIndicator</item>
        <item name="android:popupBackground">@drawable/abc_popup_background_mtrl_mult</item>
        <item name="android:dropDownVerticalOffset">0dip</item>
        <item name="android:dropDownHorizontalOffset">0dip</item>
        <item name="android:dropDownWidth">wrap_content</item>
    </style>
```

### Toolbar常用的方法

* 设置导航图标及点击事件:
```java
	mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
	mToolbar.setNavigationOnClickListener(this);
```
* 设置标题和副标题:
```java
	mToolbar.setTitle("Toolbar");
	mToolbar.setSubtitle("demo");
```
* 设置应用Logo及溢出菜单图标:
```java
 	mToolbar.setLogo(R.drawable.ic_launcher);
	mToolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_launcher));
```
* 设置溢出菜单的布局和各个ActionMenu的点击事件,此处注意的是不能和setSupportActionBar同时使用,否则无效
```java
	mToolbar.inflateMenu(R.menu.menu_main);//不能和setSupportActionbar同时使用
	mToolbar.setOnMenuItemClickListener(this);
```


### 修改Toolbar样式

* 修改标题,导航按钮,溢出菜单默认图标的颜色:

```xml

	<!--标题和导航键的颜色-->
	<item name="android:textColorPrimary">@android:color/holo_orange_dark</item>
```

* 修改副标题的颜色

```xml

	<!--副标题的颜色-->
	<item name="android:textColorSecondary">@android:color/white</item>
```

* 修改显示在Toolbar上的ActionMenu的颜色

```xml

	<item name="actionMenuTextColor">@android:color/holo_red_dark</item>
```

* 修改溢出菜单文字及字体大小,同时也会修改自定义View字体大小,及Toolbar上ActionMenu文字的大小

```xml

    <!--自定义控件中文字颜色和溢出菜单上文字的颜色-->
	<item name="android:textColor">@android:color/holo_purple</item>
	<item name="android:textSize">25dp</item>
```

* 修改溢出菜单的背景,摆放位置,从系统Overflow样式知道其实就是修改对应属性的值即可

```xml

    <style name="AppTheme.PopupOverlay" parent="@style/Widget.AppCompat.Light.PopupMenu.Overflow">
        <item name="overlapAnchor">false</item>
        <item name="android:dropDownWidth">wrap_content</item>
        <item name="android:paddingRight">5dp</item>
        <item name="android:popupBackground">?attr/colorPrimary</item>
        <!-- 弹出层垂直方向上的偏移，即在竖直方向上距离Toolbar的距离，值为负则会盖住Toolbar -->
        <item name="android:dropDownVerticalOffset">5dip</item>
        <!-- 弹出层水平方向上的偏移，即距离屏幕左边的距离，负值会导致右边出现空隙 -->
        <item name="android:dropDownHorizontalOffset">0dip</item>
    </style>

```

### 贴出上图Toolbar的代码:

* Toolbar布局:

```xml

    <android.support.v7.widget.Toolbar
        android:background="#595af2"
        android:id="@+id/toolbar"
        android:layout_width="match_parent"
        android:layout_height="?attr/actionBarSize"
        android:minHeight="?attr/actionBarSize"
        app:popupTheme="@style/AppTheme.PopupOverlay"
        app:theme="@style/AppTheme.AppBarOverlay">
        <TextView
            android:id="@+id/tv"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:text="标题"/>
    </android.support.v7.widget.Toolbar>

```

* Toolbar配置样式,及主题:

```xml

    <style name="AppTheme" parent="Theme.AppCompat.Light.NoActionBar">
        <!-- Customize your theme here. -->
        <item name="colorPrimary">#595af2</item>
        <item name="colorAccent">#ef4045</item>
        <item name="colorPrimaryDark">#140878</item>
    </style>

    <style name="AppTheme.AppBarOverlay" parent="ThemeOverlay.AppCompat.Dark.ActionBar">
        <item name="android:textColorPrimary">@android:color/holo_orange_dark</item>
        <item name="android:textColorSecondary">@android:color/white</item>
        <item name="actionMenuTextColor">@android:color/holo_red_dark</item>
        <item name="android:textColor">@android:color/holo_purple</item>
        <item name="android:textSize">25dp</item>
    </style>

    <!--<style name="AppTheme.PopupOverlay" parent="ThemeOverlay.AppCompat.Light"/>-->

    <!--溢出菜单样式 -->
    <style name="AppTheme.PopupOverlay" parent="@style/Widget.AppCompat.Light.PopupMenu.Overflow">
        <item name="overlapAnchor">false</item>
        <item name="android:dropDownWidth">wrap_content</item>
        <item name="android:paddingRight">5dp</item>
        <item name="android:popupBackground">?attr/colorPrimary</item>
        <!-- 弹出层垂直方向上的偏移，即在竖直方向上距离Toolbar的距离，值为负则会盖住Toolbar -->
        <item name="android:dropDownVerticalOffset">5dip</item>
        <!-- 弹出层水平方向上的偏移，即距离屏幕左边的距离，负值会导致右边出现空隙 -->
        <item name="android:dropDownHorizontalOffset">0dip</item>
    </style>

```
* 配置ActionMenu布局:

```xml

	<menu xmlns:android="http://schemas.android.com/apk/res/android"
      xmlns:app="http://schemas.android.com/apk/res-auto"
      xmlns:tools="http://schemas.android.com/tools"
      tools:context=".MainActivity">
    <item
        android:id="@+id/action_text"
        android:title="文字"
        app:showAsAction="ifRoom"/>
    <item
        android:id="@+id/search_view"
        android:icon="@android:drawable/ic_menu_search"
        android:imeOptions="actionSearch"
        android:inputType="textCapWords"
        android:orderInCategory="100"
        android:title="Search"
        app:actionViewClass="android.support.v7.widget.SearchView"
        app:showAsAction="ifRoom|collapseActionView"/>
    <item
        android:id="@+id/action_settings"
        android:orderInCategory="100"
        android:title="设置"
        app:showAsAction="never"/>
    <item
        android:id="@+id/action_map"
        android:orderInCategory="100"
        android:title="地图"
        app:showAsAction="never"/>
	</menu>
```

* Activity中调用Toolbar:

```java

	public class ToolbarActivity extends AppCompatActivity implements View.OnClickListener, 
        Toolbar.OnMenuItemClickListener {
    private Toolbar mToolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toolbar);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
		//mToolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setNavigationOnClickListener(this);
        mToolbar.setTitle("Toolbar");
        mToolbar.setSubtitle("demo");
        mToolbar.setLogo(R.drawable.ic_launcher);
		//mToolbar.setOverflowIcon(getResources().getDrawable(R.drawable.ic_launcher));
		//mToolbar.inflateMenu(R.menu.menu_main);//不能和setSupportActionbar同时使用
		//mToolbar.setOnMenuItemClickListener(this);
        
        setSupportActionBar(mToolbar);
        //给左上角图标的左边加上一个返回的图标 。对应ActionBar.DISPLAY_HOME_AS_UP
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); 
        //使自定义的普通View能在title栏显示，即actionBar.setCustomView能起作用，对应ActionBar.DISPLAY_SHOW_CUSTOM
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        //这个小于4.0版本的默认值为true的。但是在4.0及其以上是false,决定左上角的图标是否可以点击。。
        getSupportActionBar().setHomeButtonEnabled(true);
        //使左上角图标是否显示，如果设成false，则没有程序图标，仅仅就个标题，否则，显示应用程序图标，
        // 对应id为android.R.id.home，对应ActionBar.DISPLAY_SHOW_HOME
        //其中setHomeButtonEnabled和setDisplayShowHomeEnabled共同起作用，
        //如果setHomeButtonEnabled设成false，即使setDisplayShowHomeEnabled设成true，图标也不能点击
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //对应ActionBar.DISPLAY_SHOW_TITLE。
        getSupportActionBar().setDisplayShowTitleEnabled(true);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.search_view:
                Snackbar.make(mToolbar, "点击搜索", Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.action_settings:
                Snackbar.make(mToolbar, "点击设置", Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.action_map:
                Snackbar.make(mToolbar, "点击地图", Snackbar.LENGTH_SHORT).show();
                break;
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case android.R.id.home:
                finish();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.search_view:
                Snackbar.make(mToolbar, "点击搜索", Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.action_settings:
                Snackbar.make(mToolbar, "点击设置", Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.action_map:
                Snackbar.make(mToolbar, "点击地图", Snackbar.LENGTH_SHORT).show();
                break;
        }

        return true;
    }
}

```

