package com.gyenno.rxjava.ui.operators;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.gyenno.rxjava.R;
import com.gyenno.rxjava.utils.AppConstant;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class WindowExampleActivity extends AppCompatActivity {

    private static final String TAG = WindowExampleActivity.class.getSimpleName();
    Button btn;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        btn = findViewById(R.id.btn);
        textView = findViewById(R.id.textView);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSomeWork();
            }
        });
    }

    /*
     * Example using window operator -> It periodically
     * subdivide items from an Observable into
     * Observable windows and emit these windows rather than
     * emitting the items one at a time
     */
    protected void doSomeWork() {

        Observable.interval(1, TimeUnit.SECONDS).take(12)
                .window(3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getConsumer());
    }

    public Consumer<Observable<Long>> getConsumer() {
        return new Consumer<Observable<Long>>() {
            @Override
            public void accept(Observable<Long> observable) {
                Log.d(TAG, "Sub Divide begin....");
                textView.append("Sub Divide begin ....");
                textView.append(AppConstant.LINE_SEPARATOR);
                observable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long value) {
                                Log.d(TAG, "Next:" + value);
                                textView.append("Next:" + value);
                                textView.append(AppConstant.LINE_SEPARATOR);
                            }
                        });
            }
        };
    }
}
