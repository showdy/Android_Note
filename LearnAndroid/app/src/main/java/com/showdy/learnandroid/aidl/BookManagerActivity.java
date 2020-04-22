package com.showdy.learnandroid.aidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.showdy.learnandroid.Book;
import com.showdy.learnandroid.IBookManager;
import com.showdy.learnandroid.IOnNewBookArrivedListener;
import com.showdy.learnandroid.R;

import java.util.List;

public class BookManagerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "BookManagerActivity";
    public static final int MESSAGE_NEW_BOOK_ARRIVED = 0;

    private IBookManager mBookManager;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_NEW_BOOK_ARRIVED:
                    Log.e(TAG, "received book：" + msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_manager);
        findViewById(R.id.btnBook).setOnClickListener(this);
        bindService();
    }

    private void bindService() {
        Intent intent = new Intent(this, BookManagerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        //运行在客户端的ui线程，不应该调用服务端的耗时方法
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            IBookManager bookManager = IBookManager.Stub.asInterface(iBinder);
            mBookManager = bookManager;
            try {
                //给binder设置断开的监听
                mBookManager.asBinder().linkToDeath(mDeathRecipient, 0);
                List<Book> bookList = bookManager.getBookList();
                Log.e(TAG, "onServiceConnected: query book list, list type:" + bookList.getClass().getCanonicalName());
                Log.e(TAG, "onServiceConnected: query book list：" + bookList.toString());
                Book book = new Book(3, "Swift");
                bookManager.addBook(book);
                List<Book> newList = bookManager.getBookList();
                Log.e(TAG, "onServiceConnected: book list:" + newList.toString());
                bookManager.registerListener(mArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBookManager = null;
            Log.e(TAG, "onServiceDisconnected. tname:" + Thread.currentThread().getName());
        }
    };

    //运行在客户端的binder线程池中，不能更新ui
    private IOnNewBookArrivedListener mArrivedListener = new IOnNewBookArrivedListener.Stub() {
        @Override
        public void onNewBookArrived(Book newBook) throws RemoteException {
            mHandler.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED, newBook).sendToTarget();
        }
    };

    /**
     * binder 断开的监听器，当断开时，重连；另一种方式是在onDisconnect中重连
     */
    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.e(TAG, "binderDied: tname:" + Thread.currentThread().getName());
            if (mBookManager == null) {
                return;
            }
            mBookManager.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mBookManager = null;
            //重新绑定服务
            bindService();
        }
    };

    @Override
    protected void onDestroy() {
        if (mBookManager != null && mBookManager.asBinder().isBinderAlive()) {
            try {
                mBookManager.unregisterListener(mArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mConnection);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        Log.e(TAG, "onClick: query book list");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mBookManager != null) {
                    try {
                        List<Book> bookList = mBookManager.getBookList();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
