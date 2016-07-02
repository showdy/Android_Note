##Fragment中状态保存和重叠问题
### Fragment在实际开发应用出现的问题:
* 自从在Android 3.0引入Fragment以来，它被使用的频率也随之增多。Fragment带来的好处不言而喻，解决了不同屏幕分辨率的动态和灵活UI设计。但是在Activity管理多个Fragment中，通常会遇到这些问题：
	* Fragment状态保存问题
	* Fragment重叠问题
### 问题分析:
* 一般使用Fragment切换时都是用replace()来替换:

		public void switchContent(Fragment fragment) {
	        if(mContent != fragment) {
	            mContent = fragment;
	            mFragmentMan.beginTransaction()
	                .setCustomAnimations(android.R.anim.fade_in, R.anim.slide_out)
	                .replace(R.id.content_frame, fragment) // 替换Fragment，实现切换
	                .commit();
	        }
	    }
	> 使用replace()切换会产生一个问题: Fragment每次都会重新实例化,重新加载数据,非常消耗性能和数据流量.因为replace()常常用在上一个Fragment不再需要时才采用的简便方法.
* 如何让多个Fragment切换时不重新实例化?
	> 正确是切换方式是使用add()+hide()+show()方式.
	
		
### 重叠问题:
* 问题的原因:因为使用了Fragment的状态保存，当系统内存不足，Fragment的宿主Activity回收的时候，Fragment的实例并没有随之被回收。Activity被系统回收时，会主动调用onSaveInstance()方法来保存视图层（View Hierarchy），所以当Activity通过导航再次被重建时，之前被实例化过的Fragment依然会出现在Activity中，综上这些因素导致了Fragment重叠在一起
* 解决方法: 
	* `onSaveInstanceState()`与`onRestoreInstanceState()`在系统因为内存回收Activity时才会调用的`onSaveInstanceState()`，在转跳到其他Activity、打开多任务窗口、使用Home回到主屏幕这些操作中也被调用，然而`onRestoreInstanceState()`并没有在再次回到Activity时被调用。而且我在onResume()发现之前的Fragment只是被移除，并不是空,所以通过remove()宣告失败。
	* `onSaveInstanceState()`里面有一句`super.onRestoreInstanceState(savedInstanceState)`，Google对于这句话的解释是“Always call the superclass so it can save the view hierarchy state”，大概意思是“总是执行这句代码来调用父类去保存视图层的状态”,这也就是重叠的原因
* 优化代码(activity中):
	* oncreate()时初始化默认的Fragment
		
			setFragment(position);
	* onSaveInstancestate()保存Fragment
		
			protected void onSaveInstanceState(Bundle outState) {
	        	outState.putInt("position",position);
	    	}
	* onRestoreInstanceState()中恢复保存Fragment
	
			protected void onRestoreInstanceState(Bundle savedInstanceState) {
        		position= savedInstanceState.getInt("position");
        		setFragment(position);
        		super.onRestoreInstanceState(savedInstanceState);
    		}
### 保存UI和状态
> 反复实例化解决: 常常是先hide()所有的Fragment,再show(),具体可以使用如下代码实现:

* 隐藏所有Fragment:

		 private void hideAllFragments(FragmentTransaction transaction) {
	        if (fragment1 != null) {
	            transaction.hide(fragment1);
	        }
	        if (fragment2 != null) {
	            transaction.hide(fragment2);
	        }
   		 }
	   
* 通过add()与show()方式添加显示的Fragment:

		transaction = getSupportFragmentManager().beginTransaction();
	        hideAllFragments(transaction);
	        switch (position) {
	            case 0:
	                fragment1 = (MyHistroyFragment) getSupportFragmentManager().findFragmentByTag("my");
	                if (fragment1 == null) {
	                    fragment1 = new MyHistroyFragment();
	                    transaction.add(R.id.history_agent_container, fragment1, "my");
	                } else {
	                    transaction.show(fragment1);
	                }
	                break;
	            case 1:
	                fragment2 = (SubordinateHistoryFragment) getSupportFragmentManager().findFragmentByTag("sub");
	                if (fragment2 == null) {
	                    fragment2 = new SubordinateHistoryFragment();
	                    transaction.add(R.id.history_agent_container, fragment2, "sub");
	                } else {
	                    transaction.show(fragment2);
	                }
	                break;
	            default:
	                break;
	        }
	        transaction.commit();


	