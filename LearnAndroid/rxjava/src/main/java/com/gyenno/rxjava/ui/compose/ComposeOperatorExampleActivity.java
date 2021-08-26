package com.gyenno.rxjava.ui.compose;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.gyenno.rxjava.R;

import io.reactivex.Flowable;
import io.reactivex.Observable;

public class ComposeOperatorExampleActivity extends AppCompatActivity {

    private RxSchedulers schedulers = new RxSchedulers();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_operator_example);

        /*
            Compose for reusable code.
         */
        Observable.just(1, 2, 3, 4, 5)
                .compose(schedulers.<Integer>applyObservableAsync())
                .subscribe(/* */);

        Flowable.just(1, 2, 3, 4, 5)
                .compose(schedulers.<Integer>applyFlowableAsysnc())
                .subscribe(/* */);

    }
}
