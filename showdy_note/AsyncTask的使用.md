## AsyncTask异步任务的使用
### AsyncTask实现的原理:
*　线程池＋handler机制

### AsyncTask使用注意事项:
* `AsyncTas`k只能被执行(`execute`方法)一次,多次执行将会引发异常.
* 任务的取消只能打了一个标记,并不是真正取消,需要手动去掉用;

### 构建AsyncTask抽象类的三个泛型参数;
* `AsyncTask<Params,Progress,Result>`是一个抽象类,通常用于被继承.继承AsyncTask需要指定如下三个泛型参数:
	* `Params`:启动任务时输入的参数类型.
	* `Progress`:后台任务执行中返回进度值的类型.
	* `Result`:后台任务执行完成后返回结果的类型.
### AsyncTask四个方法:
* `onPreExecute()`;
 	> 执行任务之前,一般做变量的初始化,或者ui的隐藏或者显示;该方法无参数
* `doInBackground(T...params)`;//对应泛型参数params
	> 后台执行任务,属于子线程,当调用publishProgress()方法时,会触发系统自动调用onProgressUpdate();
* `onProgressUpdate(T... values)`;//对应泛型参数progress
	> 用于更新进度
* `onPostExecute(T...result)`;//对应泛型参数result
	> 任务结束后调用,一般处理返回的结果,或者改变ui显示.

### 加载网络图片的实例:
	
		
	public class ImageActivity extends Activity {
	    private ImageView imageView ;
	    private ProgressBar progressBar ;
	    private static String URL = "http://pic3.zhongsou.com/image/38063b6d7defc892894.jpg";
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.image);
	        imageView = (ImageView) findViewById(R.id.image);
	        progressBar = (ProgressBar) findViewById(R.id.progressBar);
	        //通过调用execute方法开始处理异步任务.相当于线程中的start方法.
	        new MyAsyncTask().execute(URL);
	    }
	
	    class MyAsyncTask extends AsyncTask<String,Void,Bitmap> {
	
	        //onPreExecute用于异步处理前的操作
	        @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	            //此处将progressBar设置为可见.
	            progressBar.setVisibility(View.VISIBLE);
	        }
	
	        //在doInBackground方法中进行异步任务的处理.
	        @Override
	        protected Bitmap doInBackground(String... params) {
	            //获取传进来的参数
	            String url = params[0];
	            Bitmap bitmap = null;
	            URLConnection connection ;
	            InputStream is ;
	            try {
	                connection = new URL(url).openConnection();
	                is = connection.getInputStream();
	                //为了更清楚的看到加载图片的等待操作,将线程休眠3秒钟.
	                Thread.sleep(3000);
	                BufferedInputStream bis = new BufferedInputStream(is);
	                //通过decodeStream方法解析输入流
	                bitmap = BitmapFactory.decodeStream(bis);
	                is.close();
	                bis.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	            return bitmap;
	        }
	
	        //onPostExecute用于UI的更新.此方法的参数为doInBackground方法返回的值.
	        @Override
	        protected void onPostExecute(Bitmap bitmap) {
	            super.onPostExecute(bitmap);
	            //隐藏progressBar
	            progressBar.setVisibility(View.GONE);
	            //更新imageView
	            imageView.setImageBitmap(bitmap);
	        }
	   	}
	}

### 模拟加载进度条:

	public class ProgressActivity extends Activity{
	    private ProgressBar progressBar;
	    private MyAsyncTask myAsyncTask;
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.progress);
	        progressBar = (ProgressBar) findViewById(R.id.progress);
	        myAsyncTask = new MyAsyncTask();
	        //启动异步任务的处理
	        myAsyncTask.execute();
	    }
	
	    //AsyncTask是基于线程池进行实现的,当一个线程没有结束时,后面的线程是不能执行的.
	    @Override
	    protected void onPause() {
	        super.onPause();
	        if (myAsyncTask != null && myAsyncTask.getStatus() == Status.RUNNING) {
	            //cancel方法只是将对应的AsyncTask标记为cancelt状态,并不是真正的取消线程的执行.
	            myAsyncTask.cancel(true);
	        }
	    }
	
	    class MyAsyncTask extends AsyncTask<Void,Integer,Void>{
	        @Override
	        protected void onProgressUpdate(Integer... values) {
	            super.onProgressUpdate(values);
	            //通过publishProgress方法传过来的值进行进度条的更新.
	            progressBar.setProgress(values[0]);
	        }
	
	        @Override
	        protected Void doInBackground(Void... params) {
	            //使用for循环来模拟进度条的进度.
	            for (int i = 0;i < 100; i ++){
	                //如果task是cancel状态,则终止for循环,以进行下个task的执行.
	                if (isCancelled()){
	                    break;
	                }
	                //调用publishProgress方法将自动触发onProgressUpdate方法来进行进度条的更新.
	                publishProgress(i);
	                try {
	                    //通过线程休眠模拟耗时操作
	                    Thread.sleep(300);
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
	            }
	            return null;
	        }
	    }
	}

------------------------------------------------------------------------

### AsyncTask的进阶学习:
