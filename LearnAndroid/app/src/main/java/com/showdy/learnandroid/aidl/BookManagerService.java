package com.showdy.learnandroid.aidl;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;

import com.showdy.learnandroid.Book;
import com.showdy.learnandroid.IBookManager;
import com.showdy.learnandroid.IOnNewBookArrivedListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class BookManagerService extends Service {

    private static final String TAG = "BookManagerService";


    private AtomicBoolean mIsServiceDestroyed = new AtomicBoolean(false);
    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();
    private RemoteCallbackList<IOnNewBookArrivedListener> mListenerList = new RemoteCallbackList<>();


    private Binder mBinder = new IBookManager.Stub() {

        //服务端binder池中，可以直接执行耗时操作。
        @Override
        public List<Book> getBookList() throws RemoteException {
            SystemClock.sleep(5000);
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
            boolean register = mListenerList.register(listener);
            int n = mListenerList.beginBroadcast();
            mListenerList.finishBroadcast();
            Log.e(TAG, "registerListener: current size :" + n);
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            boolean success = mListenerList.unregister(listener);
            if (success) {
                Log.e(TAG, "unregisterListener: success");
            } else {
                Log.e(TAG, "unregisterListener: failure");
            }
            int size = mListenerList.beginBroadcast();
            mListenerList.finishBroadcast();
            Log.e(TAG, "unregisterListener: current size: " + size);
        }

        /**
         *权限验证：
         * 1. permission验证
         * 2. pid和uid验证包名
         * 3. service的permission验证
         */
        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            //验证permission
            int check = checkCallingOrSelfPermission("com.showdy.learnandroid.permission.ACCESS_BOOK_SERVICE");
            if (check == PackageManager.PERMISSION_DENIED) {
                Log.e(TAG, "onTransact: permission denied");
                return false;
            }
            //验证包名，使用pid和uid来验证包名
            String packageName = null;
            String[] packages = getPackageManager().getPackagesForUid(getCallingUid());
            if (packages != null && packages.length > 0) {
                packageName = packages[0];
            }
            Log.d(TAG, "onTransact: " + packageName);
            if (!packageName.startsWith("com.showdy")) {
                return false;
            }
            return super.onTransact(code, data, reply, flags);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(1, "Android"));
        mBookList.add(new Book(2, "iOS"));
        new Thread(new ServiceWorker()).start();
    }

    /**
     * 验证权限
     *
     * @param intent
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        int check = checkCallingOrSelfPermission("com.showdy.learnandroid.permission.ACCESS_BOOK_SERVICE");
        Log.d(TAG, "onBind: " + check);
        if (check == PackageManager.PERMISSION_DENIED) {
            Log.e(TAG, "onTransact: permission denied");
            return null;
        }
        return mBinder;
    }

    @Override
    public void onDestroy() {
        mIsServiceDestroyed.set(true);
        super.onDestroy();
    }

    private class ServiceWorker implements Runnable {

        @Override
        public void run() {
            while (!mIsServiceDestroyed.get()) {
                SystemClock.sleep(1000);
                int bookId = mBookList.size() + 1;
                Book newBook = new Book(bookId, "new book #" + bookId);
                try {
                    onNewBookArrived(newBook);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void onNewBookArrived(Book newBook) throws RemoteException {
        mBookList.add(newBook);
        int N = mListenerList.beginBroadcast();
        for (int i = 0; i < N; i++) {
            IOnNewBookArrivedListener item = mListenerList.getBroadcastItem(i);
            if (item != null) {
                item.onNewBookArrived(newBook);
            }
        }
        mListenerList.finishBroadcast();
    }
}
