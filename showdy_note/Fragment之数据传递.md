## Fragment之数据传递有如下几种情况:
* Fragment与宿主Activity之间的数据传递
	* Fragment与宿主Activity之间的数据传递
		* Fragment传递数据给Activity
		* 宿主Activity传递数据给Fragment
	* Fragment与非宿主Activity之间数据传递
* Fragment与Fragment之间的数据传递
	* 相同宿主Fragment之间的数据传递
	* 不同宿主Fragment之间的数据传递
	* Fragment之间互为寄宿关系的数据传递

### 宿主Activity传递参数给Fragment
* 建议的传值方式是通过Bundle来传递，而不是直接作为fragment的构造参数传递。在activity中创建bundle数据包，并调用fragment的`setArguments(Bundle bundle)`方法，即可将Bundle数据包传给fragment。在Fragment中用getArguments方法得到传递过来的值。
* Activity代码编写:

		public class MainActivity extends Activity {
		    private FragmentManager manager;
		    private FragmentTransaction transaction;
		 
		    @Override
		    protected void onCreate(Bundle savedInstanceState) {
		        super.onCreate(savedInstanceState);
		        setContentView(R.layout.activity_main);
		 
		        manager = getFragmentManager();
		        transaction = manager.beginTransaction();
		 
		        MyFragment1 fragment1 = new MyFragment1();
		        Bundle bundle1 = new Bundle();
		        bundle1.putString("id", "Activity发送给MyFragment1的数据");
		        fragment1.setArguments(bundle1);
		        transaction.replace(R.id.left, fragment1, "left");
		        transaction.commit();
		    }
		 
		}
* Fragment代码编写:
	
		public class MyFragment1 extends Fragment {   
		    @Override
		    public View onCreateView(LayoutInflater inflater, ViewGroup container,
		            Bundle savedInstanceState) {
		        View view = inflater.inflate(R.layout.f1, null); 
		        TextView textView = (TextView) view.findViewById(R.id.textView);
		        Bundle bundle1 = getArguments();
		        textView.setText(bundle1.getString("id"));
		        return view;
		    }
		}
### Fragment将数据传递给宿主Activity
* 在fragment中定义一个内部回调接口，再让包含该fragment的activity实现该回调接口，这样fragment即可调用该回调方法将数据传给activity。其实接口回调的原理都一样，接口回调是java不同对象之间数据交互的通用方法
* Fragment编写代码:
	
		public class MyDealerFragment extends Fragment {
		    @Nullable
		    @Override
		    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		      //....代码省略
		    }
		
		    @Override
		    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		        super.onActivityCreated(savedInstanceState);
		        //TODO: 网络获取数据后,传给activity
		        mListener.setOrders("2000");
		    }
		
			//此处用于判断宿主是否实现了接口	
		    @Override
		    public void onAttach(Activity activity) {
		        super.onAttach(activity);
		        if (activity instanceof DataQueryDealerActivity || (activity instanceof DateQueryAgentActivity)) {
		            mListener = (MyFragmentListener) activity;
		        } else {
		            throw new IllegalArgumentException("activity must implents MyFragmentListener");
		        }
		    }
		
		    @Override
		    public void onDetach() {
		        super.onDetach();
		        mListener = null;
		    }
		
		    public interface MyFragmentListener {
		        void setOrders(String order);
		    }
		
		    private MyFragmentListener mListener;	
		}
* Activity的编写非常简单,实现接口,重写方法即可:
	
		public class DateQueryAgentActivity extends AppCompatActivity implements MyDealerFragment.MyFragmentListener{
			 @Override
		    public void setOrders(String order) {
		        this.orders=order;
		    }

### 不同宿主Fragments之间的数据传递
