### DialogFragment的优势:

    创建对话框的样式和结构,应该使用DialogFragment 用作对话框的容器,DialogFragment 类提供您创建对话框和管理其外观所需的所有
 控件，而不是调用 Dialog 对象上的方法。使用 DialogFragment 管理对话框可确保它能正确处理生命周期事件，如用户按“返回”按钮或旋
 转屏幕时。此外，DialogFragment 类还允许您将对话框的 UI 作为嵌入式组件在较大 UI 中重复使用，就像传统 Fragment 一样（例如，
 当您想让对话框 UI 在大屏幕和小屏幕上具有不同外观时）。
 
 ### DialogFragment与AlertDialog结合创建对话框
 
 完成各种对话框设计—包括自定义布局以及对话框设计指南中描述的布局—通过扩展 DialogFragment 并在 onCreateDialog() 回调方法中创建 AlertDialog。
 
 ```java
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setMessage("创建一个AlertDialog!")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //user clicked Ok button;
                    }
                })
                .setNegativeButton("Cancel", null)
                .setOnCancelListener(null)
                .setOnDismissListener(null)
                .show();
    }
 
 ```
 
 ### 创建自定义布局的对话框:
 
 您想让一部分 UI 在某些情况下显示为对话框，但在其他情况下全屏显示或显示为嵌入式片段（也许取决于设备使用大屏幕还是小屏幕）。
 DialogFragment 类便具有这种灵活性，因为它仍然可以充当嵌入式 Fragment。但在这种情况下，您不能使用 AlertDialog.Builder
 或其他 Dialog 对象来构建对话框。如果您想让 DialogFragment 具有嵌入能力，则必须在布局中定义对话框的 UI，
 然后在 onCreateView() 回调中加载布局。
 
 ```java
  @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_guide, container, false);
        ImageView ivDismiss = (ImageView) view.findViewById(R.id.iv_dismiss);
        ivDismiss.setOnClickListener(this);
        setupDialog(getDialog(), dialog);
        setWindowAttributes();
        return view;
    }

    private void setWindowAttributes() {
        //设置window全屏
        Window window = getDialog().getWindow();
        WindowManager manager = window.getWindowManager();
        Display display = manager.getDefaultDisplay();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = display.getWidth();
        window.setAttributes(params);
    }
 ```
 ### DialogFragment将事件传递给对话框的宿主
 
 在Dialog中定义接口,使用接口回调的方式,传递事件
 
 ```java
 
 @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (PositiveListener) activity;
        } catch (ClassCastException exception) {
            throw new ClassCastException(activity.toString()
                    + " must implement PositiveListener");
        }
    }
    
     @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onPositive(this);
        }
    }

    public PositiveListener listener;

    public interface PositiveListener {
        void onPositive(DialogFragment dialog);
    }
 ```
 然后, 宿主activity实现自定义的回到接口:
 
 ```java
 public class DailogActivity extends AppCompatActivity implements 
        GuideDialogFragment.PositiveListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        final GuideDialogFragment dialog = new GuideDialogFragment();
        dialog.show(getSupportFragmentManager(), "GuideDialog");
    }

    @Override
    public void onPositive(DialogFragment dialog) {
        dialog.dismissAllowingStateLoss();
        Toast.makeText(this, "dialog消失", Toast.LENGTH_SHORT).show();
    }
}
 ```
 ### 定义Dialog的样式style
 
 ```java
 
  setupDialog(getDialog(), R.style.dialog);
 
 ```
 
 
```xml
    <style name="dialog" parent="@android:style/Theme.Dialog">
        <!--无边框-->
        <item name="android:windowFrame">@null</item>
        <!--对话框悬浮-->
        <item name="android:windowIsFloating">true</item>
        <!--window半透明透明-->
        <item name="android:windowIsTranslucent">true</item>
        <!--无标题-->
        <item name="android:windowNoTitle">true</item>
        <!--dialog背景透明-->
        <item name="android:windowBackground">@android:color/transparent</item>
        <!--背景模糊-->
        <item name="android:backgroundDimEnabled">true</item>
        <!--外部点击不消失-->
        <item name="android:windowCloseOnTouchOutside">false</item>
    </style>
```

### 设置dialog的宽高

```java
  private void setWindowAttributes() {
        //设置window全屏
        Window window = getDialog().getWindow();
        WindowManager manager = window.getWindowManager();
        Display display = manager.getDefaultDisplay();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = display.getWidth();
        window.setAttributes(params);
    }

```
 
 
