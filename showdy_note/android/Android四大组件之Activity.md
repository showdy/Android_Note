### Activity

#### 和Activity生命周期有关的几个问题:
1. onSaveInstanceState方法在Activity的哪两个生命周期方法之间调用？

2. 弹出一个Dialog时，onPause会调用吗？什么情况下会，什么情况下不会？

3. 横竖屏切换的时候，生命周期方法是如何调用的？如何进行配置呢？

4. Activity调用了onDestory方法，就会在Activity的任务栈消失吗？

5. 永久性质的数据，应该在哪个生命周期方法中保存？

6. 在onCreate或者onRestoreInstance方法中恢复数据时，有什么区别？

#### Activity的生命周期
![](img/activity_lifecycle.png)

* Activity的创建和销毁
	* onCreate();
		> 执行Activity某些基本设置的一些代码,比如声明用户界面(xml文件),定义成员变量,配置某些UI等等,oncreate一般是必须要实现的.
	* onDestroy();
		> 一般不需要实现,因为本地类引用与Activity一同销毁，并且您的Activity应在 onPause() 和 onStop() 期间执行大多数清理操作。 但是，如果您的Activity包含您在 onCreate() 期间创建的后台线程或其他如若未正确关闭可能导致内存泄露的长期运行资源，应在 onDestroy() 期间终止它们.
		
* Activity开始和停止
	* onStart();
		> onStop() 方法应基本清理所有Activity的资源，将需要在Activity重新开始时重新实例化它们。但是，还需要在Activity初次创建时重新实例化它们（没有Activity的现有实例）。出于此原因，应经常使用 onStart() 回调方法作为 onStop() 方法的对应部分，因为系统会在它创建您的Activity以及从停止状态重新开始Activity时调用 onStart() 。
	* onStop();
		> Activity收到 onStop() 方法的调用时，它不再可见，并且应释放几乎所有用户不使用时不需要的资源。 一旦您的Activity停止，如果需要恢复系统内存，系统可能会销毁该实例。 在极端情况下，系统可能会仅终止应用进程，而不会调用Activity的最终 onDestroy() 回调，因此您使用 onStop() 释放可能泄露内存的资源非常重要。尽管 onPause() 方法在 onStop()之前调用，您应使用 onStop() 执行更大、占用更多 CPU 的关闭操作，比如向数据库写入信息。
* Activity运行和暂停
	* onResume();
		> Activity获得焦点,实现onResume()初始化在 onPause() 期间释放的组件并且执行每当Activity进入“继续”状态时必须进行的任何其他初始化操作（比如开始动画和初始化只在Activity具有用户焦点时使用的组件）。
	* onPause();
		> 当系统为您的Activity调用 onPause() 时，它从技术角度看意味着您的Activity仍然处于部分可见状态，但往往说明用户即将离开Activity并且它很快就要进入“停止”状态。 您通常应使用 onPause() 回调：

		* 停止动画或其他可能消耗 CPU 的进行之中的操作。
		* 提交未保存的更改，但仅当用户离开时希望永久性保存此类更改（比如电子邮件草稿）。
		* 释放系统资源，比如广播接收器、传感器手柄（比如 GPS） 或当您的Activity暂停且用户不需要它们时仍然可能影响电池寿命的任何其他资源。
		* 但是要注意: **不得使用** onPause() 永久性存储用户更改（比如输入表格的个人信息）。 只有在您确定用户希望自动保存这些更改的情况（比如，电子邮件草稿）下，才能在 onPause()中永久性存储用户更改。但您应避免在 onPause() 期间执行 CPU 密集型工作，比如**向数据库写入信息**，因为这会拖慢向下一Activity过渡的过程（您应改为在 onStop()期间执行高负载关机操作。
		
* 数据存储与恢复:
![](img/basic-lifecycle-savestate.png)

	当系统开始停止您的Activity时，它会 调用 onSaveInstanceState() (1)，因此，您可以指定您希望在 Activity 实例必须重新创建时保存的额外状态数据。如果Activity被销毁且必须重新创建相同的实例，系统将在 (1) 中定义的状态数据同时传递给 onCreate() 方法(2) 和 onRestoreInstanceState() 方法(3)。

	* onSaveInstanceState()
		* 处于onstop()方法前, 但是与onpause()没有多少必然的联系
	* onRestoreInstanceState()
		* 处于onresume前,
		* 您可以选择实现系统在 onStart() 方法之后调用的 onRestoreInstanceState()，而不是在onCreate() 期间恢复状态。 系统只在存在要恢复的已保存状态时调用 onRestoreInstanceState() ，因此您无需检查 Bundle 是否为 null：

* 还有一些的其他的生命周期方法：
	* onPostCreate()
		> 当activity建立后调用,即在onstart()和onRestoreInstanceState()完成后调用
	* onPostResume()
		> onCreate->onStart->onPostCreate->onResume->onPostResume
	


### Activity任务栈