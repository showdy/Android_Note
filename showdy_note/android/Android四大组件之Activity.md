
###Activity的生命周期


![](img/activity_lifecycle.png)
* Activity的创建和销毁
	* **`onCreate()`**: 执行Activity某些基本设置的一些代码,比如声明用户界面(xml文件),定义成员变量,配置某些UI等等,oncreate一般是必须要实现的.
	
	* **`onDestroy()`**:一般不需要实现,因为本地类引用与Activity一同销毁，并且您的Activity应在 onPause() 和 onStop() 期间执行大多数清理操作。 但是，如果您的Activity包含您在 onCreate() 期间创建的后台线程或其他如若未正确关闭可能导致内存泄露的长期运行资源，应在 onDestroy() 期间终止它们.
		
* Activity开始和停止
	* **`onStart()`**: onStop() 方法应基本清理所有Activity的资源，将需要在Activity重新开始时重新实例化它们。但是，还需要在Activity初次创建时重新实例化它们（没有Activity的现有实例）。出于此原因，应经常使用 onStart() 回调方法作为 onStop() 方法的对应部分，因为系统会在它创建您的Activity以及从停止状态重新开始Activity时调用 onStart() 。
	
	* **`onStop()`**: Activity收到 onStop() 方法的调用时，它不再可见，并且应释放几乎所有用户不使用时不需要的资源。 一旦您的Activity停止，如果需要恢复系统内存，系统可能会销毁该实例。 在极端情况下，系统可能会仅终止应用进程，而不会调用Activity的最终 onDestroy() 回调，因此您使用 onStop() 释放可能泄露内存的资源非常重要。尽管 onPause() 方法在 onStop()之前调用，您应使用 onStop() 执行更大、占用更多 CPU 的关闭操作，比如向数据库写入信息。
	
* Activity运行和暂停
	* **`onResume():`** Activity获得焦点,实现onResume()初始化在 onPause() 期间释放的组件并且执行每当Activity进入“继续”状态时必须进行的任何其他初始化操作（比如开始动画和初始化只在Activity具有用户焦点时使用的组件）。
	
	* **`onPause()`**: 当系统为您的Activity调用 onPause() 时，它从技术角度看意味着您的Activity仍然处于部分可见状态，但往往说明用户即将离开Activity并且它很快就要进入“停止”状态。 您通常应使用 onPause() 回调：

		* 停止动画或其他可能消耗 CPU 的进行之中的操作。
		* 提交未保存的更改，但仅当用户离开时希望永久性保存此类更改（比如电子邮件草稿）。
		* 释放系统资源，比如广播接收器、传感器手柄（比如 GPS） 或当您的Activity暂停且用户不需要它们时仍然可能影响电池寿命的任何其他资源。
		* 但是要注意: **不得使用** onPause() 永久性存储用户更改（比如输入表格的个人信息）。 只有在您确定用户希望自动保存这些更改的情况（比如，电子邮件草稿）下，才能在 onPause()中永久性存储用户更改。但您应避免在 onPause() 期间执行 CPU 密集型工作，比如**向数据库写入信息**，因为这会拖慢向下一Activity过渡的过程（您应改为在 onStop()期间执行高负载关机操作。
	
	
* 数据存储与恢复:

	![](img/basic-lifecycle-savestate.png)

	当系统开始停止您的Activity时，它会 调用 onSaveInstanceState() (1)，因此，您可以指定您希望在 Activity 实例必须重新创建时保存的额外状态数据。如果Activity被销毁且必须重新创建相同的实例，系统将在 (1) 中定义的状态数据同时传递给 onCreate() 方法(2) 和 onRestoreInstanceState() 方法(3)。

	* **`onSaveInstanceState()`**:处于onstop()方法前, 但是与onpause()没有多少必然的联系,可能在onPause()前调用,也可能在onStop()前调用
	
	* **`onRestoreInstanceState()`**: 处于onresume前,您可以选择实现系统在 onStart() 方法之后调用的 onRestoreInstanceState()，而不是在onCreate() 期间恢复状态。 系统只在存在要恢复的已保存状态时调用 onRestoreInstanceState() ，因此您无需检查 Bundle 是否为 null：

* 还有一些的其他的生命周期方法：
	* **`onPostCreate()`**:  当activity建立后调用,即在onstart()和onRestoreInstanceState()完成后调用
	* **`onPostResume()`**: onCreate->onStart->onPostCreate->onResume->onPostResume
	
#### 和Activity生命周期有关的几个问题:

1. onSaveInstanceState方法在Activity的哪两个生命周期方法之间调用？

 	**onSaveInstanceState()的调用与onPause()的调用没有先后之分,可能在onStop()前,也可能在onPause()前,但是可以保证一定在onStop()之前.**

2. 弹出一个Dialog时，onPause会调用吗？什么情况下会，什么情况下不会？
 
	**首先，弹出的是本Activity的Dialog，并不会有任何生命周期方法调用。Dialog是一个View，它本身就依附在Acitivty上，可以理解为是属于本Activity的，所以它的焦点也自然是本Activity的焦点，自然不会有什么生命周期方法调用了。如果其他Activity的Dialog弹出了，onPause才会调用。**

3. 横竖屏切换的时候，生命周期方法是如何调用的？如何进行配置呢？

	**横竖屏切换时，如果不做任何配置，生命周期方法的回调顺序为：
	`onPause–onSaveInstanceState–onStop–onDestory–onCreate–onStart–onResume`
	也就是说Activity被销毁并重建了。如果不想这样可以在清单文件中的Activity添加一行配置：
	`android:configChanges="keyboardHidden|orientation|screenSize"`**

4. Activity调用了onDestory方法，就会在Activity的任务栈消失吗？
	
	**如果是点击back键销毁Activity,相当于调用了Activity的finish(),将Activity从任务栈中退出,再调用onDestroy(),如果Activity是意外被销毁,直接调用onDestroy(),Activity是不会从任务栈中清除的.**

5. 永久性质的数据，应该在哪个生命周期方法中保存？

	**由于系统在紧急情况必须内存,onPause(),onStop(),onDestroy()三个方法,唯一能保证调用的只有onPause()方法,其他两个方法可能不会调用,所以在此方法中做重要数据的持久化存储,但是要注意的是,onPause()是非常轻量级的,不能做耗时操作,而由于无法保证系统会调用 onSaveInstanceState()，只应利用它来记录 Activity 的瞬态（UI 的状态）而切勿使用它来存储持久性数据**

6. 在onCreate或者onRestoreInstance方法中恢复数据时，有什么区别？
	
	区别: onRestoreInstanceState()一旦被调用,其参数Bundle saveInstance一定是有值的,我们不需要额外判断是否为null,但是onCreate()却不行,onCreate()正常启动,其参数Bundle saveInstance()为null,这个需要额外去判断.官方建议采用onResotreInstanceState()去恢复数据.

	**Activity的 onSaveInstanceState() 和 onRestoreInstanceState()并不是生命周期方法，它们不同于 onCreate()、onPause()等生命周期方法，它们并不一定会被触发。当应用遇到意外情况（如：内存不足、用户直接按Home键）由系统销毁一个Activity时，onSaveInstanceState() 会被调用。但是当用户主动去销毁一个Activity时，例如在应用中按返回键，onSaveInstanceState()就不会被调用。因为在这种情况下，用户的行为决定了不需要保存Activity的状态。通常onSaveInstanceState()只适合用于保存一些临时性的状态，而onPause()适合用于数据的持久化保存。onRestoreInstanceState()在onStart() 和 onPostCreate(Bundle)之间调用。**

7. 如果一个Activity在用户可见时才处理某个广播，不可见时注销掉，那么应该在哪两个生命周期的回调方法去注册和注销BroadcastReceiver呢？
	
	**应在onStart()中注册,而在onStop()中注销;一般情况下会选择在onStop()和onDestroy()中进行资源释放的操作, onPause() 调用期间必须保留的信息有所选择，因为该方法中的任何阻止过程都会妨碍向下一个 Activity 的转变并拖慢用户体验。.**

8. 如果有一些数据在Activity跳转时（或者离开时）要保存到数据库，那么你认为是在onPause好还是在onStop执行这个操作好呢？

	**Activity A启动 Activity B必须要经历的生命周期为: Activity A先调用onPause(),然后Activity B调用onCreate(),onStart(),onResume(),接着Activity A再调用onStop().故而,Activity A在跳转前需要先在onPause()中将数据持久化存储,以便Activity B可以调用.**
	

### Activity任务栈

* 任务栈: 指在执行特定作业时与用户交互的一系列Activity,这些Activity按照各自的打开顺序排列在堆栈(返回栈),任务栈是一种"先进后出"的栈结构.

![这里写图片描述](http://img.blog.csdn.net/20170207161237738?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvc2hvd2R5/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

#### Activity的启动模式:

* **`standard`**:标准模式.系统默认的启动模式,每次启动一个Activity都会重新创建一个新的实例,不管这个实例是否存在.谁启动了这Activity,那么这个Activity就运行在那个Activity所在的栈中.如果启动Activity时传入ApplicationContext会报错,因为非Activity类型Context并没有所谓的栈,解决这个问题需要给待启动Activity设置`FLAG_ACTIVITY_NEW_TASK`标记位,相当于以singleTask模式启动.

* **`singleTop`**: 栈顶复用模式.如果新Activity已经为栈顶,那么Activity不会被重新创建,同时onNewIntent()会被调用,但是Activity的onCreate()和onStart()不会被调用;如果新的Activity存在但是不在栈顶,那么Activity仍然会重新创建.

* **`singleTask`**: 栈内复用模式.只要Activity在一个栈中存在,多次启动Activity都不会重新创建实例,和singleTop一样,系统会调用onNewIntent().但是singleTask模式具有clearTop的效果,会导致栈内待启动Activity上面的Activity被出栈.

* **`singleInstance`**: 单例模式.singleInstance是一种加强版的singleTask模式,具有此启动模式Activity单独存在一个栈内复用.


#### 启动模式应用场景:

* 假如目前有2个任务栈,前台任务栈有12,而后台任务栈有XY,假设CD的启动模式均为singleTask,那么启动Y时,整个后台任务栈都会被切换到前台,这时后退列表就变为12XY,当点击back键时,列表中Activity会一一出栈.
	
	![这里写图片描述](http://img.blog.csdn.net/20170207161258425?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvc2hvd2R5/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)

* 但是启动的不是Y而是X,情况就不一样了.

	![这里写图片描述](http://img.blog.csdn.net/20170207161313864?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvc2hvd2R5/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)



#### 任务相关性

* **`TaskAffinity`**: 任务相关性.

	这个参数标识一个Activity所需要的任务栈的名称,默认情况为应用包名.当然,可以为每个Activity指单独的TaskAffinity属性,属性名需和包名不同,否则没有意义.TaskAffinity属性主要和singleTask启动模式以及allowTaskReparenting属性配对使用,其他情况没有意义.另外任务栈分为前台任务栈和后台任务栈,后台任务栈中所有Activity处于暂停状态.

	当TaskAffinity与singleTask启动模式使用时, 他具有该模式Activity目前任务栈的名称,待启动的Activity会运行在名字和TaskAffinity相同的任务栈.
	
	当TaskAffinity与allowTaskReparenting结合使用时,情况比较复杂,会产生特殊的效果.


#### 清理任务栈

如果用户长时间离开任务，则系统会清除所有 Activity 的任务，根 Activity 除外。 当用户再次返回到任务时，仅恢复根 Activity。系统这样做的原因是，经过很长一段时间后，用户可能已经放弃之前执行的操作，返回到任务是要开始执行新的操作。可以使用下列几个 Activity 属性修改此行为：

* **`alwaysRetainTaskState`**

	如果在任务的根 Activity 中将此属性设置为 "true"，则不会发生刚才所述的默认行为。即使在很长一段时间后，任务仍将所有 Activity 保留在其堆栈中。
* **`clearTaskOnLaunch`**

	如果在任务的根 Activity 中将此属性设置为 "true"，则每当用户离开任务然后返回时，系统都会将堆栈清除到只剩下根 Activity。 换而言之，它与 alwaysRetainTaskState 正好相反。 即使只离开任务片刻时间，用户也始终会返回到任务的初始状态。
* **`finishOnTaskLaunch`**

	此属性类似于 clearTaskOnLaunch，但它对单个 Activity 起作用，而非整个任务。 此外，它还有可能会导致任何 Activity 停止，包括根 Activity。 设置为 "true" 时，Activity 仍是任务的一部分，但是仅限于当前会话。如果用户离开然后返回任务，则任务将不复存在。


#### Activity的Flags

* **`FLAG_ACTIVITY_NEW_TASK`**:

	该标记的作用是为Activity指定"singleTask"启动模式,效果和XML中指定一样.	service中启动activity需要用到此标记.

* **`FLAG_ACTIVITY_SINGLE_TOP`**:

	该标记的作用是为Activity指定"singleTop"启动模式,效果和XML中指定一样.
	
* **`FLAG_ACTIVITY_CLEAR_TOP`**:

	具有此标记的Activity,当启动时,在同一个任务栈中所有位于他上面的Activity都要被清除出栈,此标记一般与singleTask启动模式一起使用.在这种情况下,若被启动Activity的实例已经存在,那么系统会调用onNewIntent.如果被启动Activity采用的standard模式,那么连同他之上的activity都要出栈,系统会创建新的activity实例放入栈顶.
	


### 参考

* [**ANDROID INSTANCESTATE**](http://stormzhang.com/android/2014/02/21/android-instancestate/)
* [**面试题： 怎么理解Activity的生命周期？**](http://www.jianshu.com/p/ae6e1d93cc8e)
* [**Activity的生命周期，你足够了解吗？**](http://blog.csdn.net/melodev/article/details/52075141)

