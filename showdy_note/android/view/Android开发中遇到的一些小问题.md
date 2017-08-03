### Android 开发中的一个小问题:

#### RadioGroup#checked(id)调用会多次触发onCheckedChanged()监听.

查看RadioGroup#check(id)源码:

```java

 	public void check(@IdRes int id) {
        // don't even bother
        if (id != -1 && (id == mCheckedId)) {
            return;
        }

        if (mCheckedId != -1) {
            setCheckedStateForView(mCheckedId, false);
        }

        if (id != -1) {
            setCheckedStateForView(id, true);
        }

        setCheckedId(id);
    }

```

从源码可以看到onCheckedChanged()监听会触发三次:

* 当前RadioButton状态置为unChecked;
* 已选RadioButton状态置为checked;
* RadioGroup保存哪个RadioButton已经checked.

解决此问题,可以选择另一个方法:

```java

	private void setCheckedId(@IdRes int id) {
        mCheckedId = id;
        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(this, mCheckedId);
        }
    }
```

### TextView设置setClickable(false)时,TextView还是能响应点击事件:

查看View的源码:

```java

	public void setOnClickListener(@Nullable OnClickListener l) {
        if (!isClickable()) {
            setClickable(true);
        }
        getListenerInfo().mOnClickListener = l;
    }
```

发现在setOnClickListener中会将view设置可点击,这也就是设置setclickable(false),然后调用调用监听无效的原因.

解决办法很简单: 将setclickable(false)位置放在setOnClickListener()后面.
