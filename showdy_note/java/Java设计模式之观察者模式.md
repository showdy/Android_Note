##java设计模式之观察者模式:
*** 本文内容来自:Android源码设计模式解析与实战**
###观察者模式的定义:
* 观察者模式(observer pattern)又称为发布-订阅模式(publish/subscribe pattern),定义对象间一对多的依赖关系,使得每当一个对象改变时,则其所有依赖的对象都会得到通知并被自动更新.既然是一对多的关系,说明唯一的是被观察者,而观察者则可以有多种.

### 观察者模式使用场景:
* 关联行为场景,关联行为是可拆分的,而不是"组合"关系
* 事件多级触发的场景
* 跨系统的消息交换场景,如消息队列,事件总线的处理机制

### 观察者模式的UML类图:
![观察者模式UML类图](http://img.blog.csdn.net/20160116152925121)

* Subject:抽象主题,也就是被观察者(Observable),抽象主题角色把所有观察者对象的引用保存在一个集合中,每个主题都可以有任意数量的观察者,抽象主题提供一个接口,可以增加和删除观察者对象.
* ConcreteSubject: 具体主题,即具体的被观察者(ConcreateObsevable),在具体主题的内部状态改变时,给所有注册过的观察者发出通知.
* Obsever: 抽象观察者,定义一个更新的接口,使得在得到主题改变的更新通知时更新自己
* ConcreteObserver: 具体观察者,实现Observer接口,在得到通知时更新自己.

### 观察者实例的实现:
* 抽象主题:

		

```java
public abstract class Observable {	    
		    //最好使用线程安全的Vector
		    private List<Observer> mObservers = new ArrayList<>();
		
		    public void attach(Observer observer) {
		        if (!mObservers.contains(observer)) {
		            mObservers.add(observer);
		        }
		    }
		    public void detach(Observer observer) {
		        if (observer != null) {
		            mObservers.remove(observer);
		        }
		    }
		    public void notifyObservers(String newStates) {
		        for (Observer observer : mObservers) {
		            observer.update(newStates);
		        }
		    }
		}
```

* 具体主题:

		

```java
public class ConcreteObservable extends Observable {
		    private String state;
		
		    public String getState() {
		        return state;
		    }
		
		    public void changed(String newState) {
		        state = newState;
		        Log.d("ConcreteObservable", "主题改变:" + state);
		        notifyObservers(state);
		    }
		}
```

* 抽象观察者:

		

```java
public interface Observer {
		    void update(String state);
		}
```

* 具体观察者：

		

```java
public class ConcreteObserver implements Observer {
		    private String observerState;
		    @Override
		    public void update(String state) {
		        observerState = state;
		        Log.d("ConcreteObserver", "状态为:" + observerState);
		    }
		}
```

* 客户端：
		
		

```java
public class Client {
		    public static void main(String[] args){
		        ConcreteObservable observable= new ConcreteObservable();
		        
		        Observer observer= new ConcreteObserver();
		        
		        observable.attach(observer);
		        
		        observer.update("new state");
		    }
		    
		}
```

### Android中观察者模式的编写:
* 抽象主题(Obsevable):

	

```java	
public abstract class Observable<T> {
		   
		    protected final ArrayList<T> mObservers = new ArrayList<T>();
		
		    /**
		     * Adds an observer to the list. The observer cannot be null and it must not already
		   	 */
		    public void registerObserver(T observer) {
		        if (observer == null) {
		            throw new IllegalArgumentException("The observer is null.");
		        }
		        synchronized(mObservers) {
		            if (mObservers.contains(observer)) {
		                throw new IllegalStateException("Observer " + observer + " is already registered.");
		            }
		            mObservers.add(observer);
		        }
		    }
		
		    /**
		     * Removes a previously registered observer. The observer must not be null and it must already have been registered.
		     */
		    public void unregisterObserver(T observer) {
		        if (observer == null) {
		            throw new IllegalArgumentException("The observer is null.");
		        }
		        synchronized(mObservers) {
		            int index = mObservers.indexOf(observer);
		            if (index == -1) {
		                throw new IllegalStateException("Observer " + observer + " was not registered.");
		            }
		            mObservers.remove(index);
		        }
		    }
		
		    /**
		     * Remove all registered observers.
		     */
		    public void unregisterAll() {
		        synchronized(mObservers) {
		            mObservers.clear();
		        }
		    }
		}
```

* 具体主题
	* ContentObservable
	* DataSetObservable
		
			

```java
public class DataSetObservable extends Observable<DataSetObserver> {
			  
			    public void notifyChanged() {
			        synchronized(mObservers) {
			      		//也可以iterator迭代器去遍历
			            for (int i = mObservers.size() - 1; i >= 0; i--) {
			                mObservers.get(i).onChanged();
			            }
			        }
			    }
			
			    public void notifyInvalidated() {
			        synchronized (mObservers) {
			            for (int i = mObservers.size() - 1; i >= 0; i--) {
			                mObservers.get(i).onInvalidated();
			            }
			        }
			    }
			}
```

* 抽象观察者(DataSetObserver):

		

```java
public abstract class DataSetObserver {
		    /**
		     * This method is called when the entire data set has changed,
		     * most likely through a call to {@link Cursor#requery()} on a {@link Cursor}.
		     */
		    public void onChanged() {
		        // Do nothing
		    }
		
		    /**
		     * This method is called when the entire data becomes invalid,
		     * most likely through a call to {@link Cursor#deactivate()} or {@link Cursor#close()} on a
		     * {@link Cursor}.
		     */
		    public void onInvalidated() {
		        // Do nothing
		    }
		}
```

* 具体观察者:PagerObserver使用在ViewPager中,是具体实现类:
```java			
     private class PagerObserver extends DataSetObserver {
		        @Override
		        public void onChanged() {
		            dataSetChanged();
		        }
		        @Override
		        public void onInvalidated() {
		            dataSetChanged();
		        }
		    }
```
	

*　AdapterDataSetObserver定义在AbListView中的观察者　
	
```java			
        class AdapterDataSetObserver extends AdapterView<ListAdapter>.AdapterDataSetObserver {
		        @Override
		        public void onChanged() {
		            super.onChanged();
		            if (mFastScroll != null) {
		                mFastScroll.onSectionsChanged();
		            }
		        }
		
		        @Override
		        public void onInvalidated() {
		            super.onInvalidated();
		            if (mFastScroll != null) {
		                mFastScroll.onSectionsChanged();
		            }
		        }
		    }
```

### 简单介绍观察者模式在ListView中的使用:
* ListView是android中最重要的控件之一,而ListView的一个重要功能就是Adapter(适配器模式构建),通常在加载数据后都会notifyDatasetChanged()来刷新数据的展示,而这过程会是怎么样的?
* 首先看下notfiyDatasetChanged()这个方法所在的类:BaseAdapter,其中代码具体为:

		

```java
public abstract class BaseAdapter implements ListAdapter, SpinnerAdapter {
		    private final DataSetObservable mDataSetObservable = new DataSetObservable();
		
		 	//...代码省略
		    
		    public void registerDataSetObserver(DataSetObserver observer) {
		        mDataSetObservable.registerObserver(observer);
		    }
		
		    public void unregisterDataSetObserver(DataSetObserver observer) {
		        mDataSetObservable.unregisterObserver(observer);
		    }
		    
		    //通知所有观察者数据已经变化
		    public void notifyDataSetChanged() {
		        mDataSetObservable.notifyChanged();
		    }
			//.....
		}
```

* 在mDataSetObservable.notifyChanged()函数中看到:

		

```java
public class DataSetObservable extends Observable<DataSetObserver> {  
				/**调用每个观察者的onchanged()方法,来通知他们被观察者发生变化*/
			    public void notifyChanged() {
			        synchronized(mObservers) {
			      		//也可以iterator迭代器去遍历
			            for (int i = mObservers.size() - 1; i >= 0; i--) {
			                mObservers.get(i).onChanged();
			            }
			        }
			    }
			
			    public void notifyInvalidated() {
			        synchronized (mObservers) {
			            for (int i = mObservers.size() - 1; i >= 0; i--) {
			                mObservers.get(i).onInvalidated();
			            }
			        }
			    }
			}
```

* 上述代码中的mObservers是怎么回事呢,其实是在ListView通过setAdapter()设置Adapter产生的

		

```java
 public void setAdapter(ListAdapter adapter) {
	        if (mAdapter != null && mDataSetObserver != null) {
	            mAdapter.unregisterDataSetObserver(mDataSetObserver);
	        }
			//...代码省略
	        super.setAdapter(adapter);
	
	        if (mAdapter != null) {
	            mAreAllItemsSelectable = mAdapter.areAllItemsEnabled();
	            mOldItemCount = mItemCount;
				//获取数据的数量
	            mItemCount = mAdapter.getCount();
	            checkFocus();
				//创建一个新的数据集观察者
	            mDataSetObserver = new AdapterDataSetObserver();
	            mAdapter.registerDataSetObserver(mDataSetObserver);
				//...代码省略
	        }else{
				//...代码省略
	        }
	
	        requestLayout();
	    }
```

*　往下走，此时发现AdapterDataSetObsever应该是一个具体观察者,其定义在ListView的父类AbsListView中,代码为:
	
	
```java
    class AdapterDataSetObserver extends AdapterView<ListAdapter>.AdapterDataSetObserver {
		        @Override
		        public void onChanged() {
		            super.onChanged();
		            if (mFastScroll != null) {
		                mFastScroll.onSectionsChanged();
		            }
		        }
		
		        @Override
		        public void onInvalidated() {
		            super.onInvalidated();
		            if (mFastScroll != null) {
		                mFastScroll.onSectionsChanged();
		            }
		        }
		    }
```

* 此时发现其继承AdapterView.AdapterDataSetObserver,看看其代码:
  ```java
      class AdapterDataSetObserver extends DataSetObserver {
		        private Parcelable mInstanceState = null;
		        @Override
		        public void onChanged() {
		            mDataChanged = true;
		            mOldItemCount = mItemCount;
					//获取Adapter中数据的数量
		            mItemCount = getAdapter().getCount();
		            if (AdapterView.this.getAdapter().hasStableIds() && mInstanceState != null&& mOldItemCount == 0 && mItemCount > 0) {
		                AdapterView.this.onRestoreInstanceState(mInstanceState);
		                mInstanceState = null;
		            } else {
		                rememberSyncState();
		            }
		            checkFocus();
					//重新布局
		            requestLayout();
		        }
		
		        @Override
		        public void onInvalidated() {
		           //...代码省略
		        }
		
		        public void clearSavedState() {
		            mInstanceState = null;
		        }
		    }
  ```

* 此时会发现,ListView数据变化时调用Adapter的notifysetDataChanged(),此方法会调用DatasetObservable的notifyChanged(),而这个方法会调用所有观察者(AdapterDatasetObservable)的onchanged(),在onChanged()中又会调用ListView重新布局的函数来属性界面.

* 总结一下:**AdapterView中有一个内部类AdapterDataSetObserver,在调用setAdapter()时会构建一个AdapterDatasetObserver,并且注册到Adapter中,这就是观察者,而Adapter中包含一个数据集被观察者DatasetObservable,在数据发送变化时,手动调用notifyDataSetChanged(),就会遍历所有观察者的onChanged()函数,在AdapterDataSetObserver的onChanged()会获取Adapter数据集的新数据,然后调用ListView的重新布局requsetLayout()进行重新布局,更新界面**.
