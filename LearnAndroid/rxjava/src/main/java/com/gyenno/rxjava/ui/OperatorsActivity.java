package com.gyenno.rxjava.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.gyenno.rxjava.R;
import com.gyenno.rxjava.ui.operators.AsyncSubjectExampleActivity;
import com.gyenno.rxjava.ui.operators.BehaviorSubjectExampleActivity;
import com.gyenno.rxjava.ui.operators.BufferExampleActivity;
import com.gyenno.rxjava.ui.operators.CompletableObserverExampleActivity;
import com.gyenno.rxjava.ui.operators.ConcatExampleActivity;
import com.gyenno.rxjava.ui.operators.DebounceExampleActivity;
import com.gyenno.rxjava.ui.operators.DeferExampleActivity;
import com.gyenno.rxjava.ui.operators.DelayExampleActivity;
import com.gyenno.rxjava.ui.operators.DisposableExampleActivity;
import com.gyenno.rxjava.ui.operators.DistinctExampleActivity;
import com.gyenno.rxjava.ui.operators.FilterExampleActivity;
import com.gyenno.rxjava.ui.operators.FlowableExampleActivity;
import com.gyenno.rxjava.ui.operators.IntervalExampleActivity;
import com.gyenno.rxjava.ui.operators.LastOperatorExampleActivity;
import com.gyenno.rxjava.ui.operators.MapExampleActivity;
import com.gyenno.rxjava.ui.operators.MergeExampleActivity;
import com.gyenno.rxjava.ui.operators.PublishSubjectExampleActivity;
import com.gyenno.rxjava.ui.operators.ReduceExampleActivity;
import com.gyenno.rxjava.ui.operators.ReplayExampleActivity;
import com.gyenno.rxjava.ui.operators.ReplaySubjectExampleActivity;
import com.gyenno.rxjava.ui.operators.ScanExampleActivity;
import com.gyenno.rxjava.ui.operators.SimpleExampleActivity;
import com.gyenno.rxjava.ui.operators.SingleObserverExampleActivity;
import com.gyenno.rxjava.ui.operators.SkipExampleActivity;
import com.gyenno.rxjava.ui.operators.SwitchMapExampleActivity;
import com.gyenno.rxjava.ui.operators.TakeExampleActivity;
import com.gyenno.rxjava.ui.operators.TakeUntilExampleActivity;
import com.gyenno.rxjava.ui.operators.TakeWhileExampleActivity;
import com.gyenno.rxjava.ui.operators.ThrottleFirstExampleActivity;
import com.gyenno.rxjava.ui.operators.ThrottleLastExampleActivity;
import com.gyenno.rxjava.ui.operators.TimerExampleActivity;
import com.gyenno.rxjava.ui.operators.WindowExampleActivity;
import com.gyenno.rxjava.ui.operators.ZipExampleActivity;

public class OperatorsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operators);
    }

    public void startSimpleActivity(View view) {
        startActivity(new Intent(OperatorsActivity.this, SimpleExampleActivity.class));
    }

    public void startMapActivity(View view) {
        startActivity(new Intent(OperatorsActivity.this, MapExampleActivity.class));
    }

    public void startZipActivity(View view) {
        startActivity(new Intent(OperatorsActivity.this, ZipExampleActivity.class));
    }

    public void startDisposableActivity(View view) {
        startActivity(new Intent(OperatorsActivity.this, DisposableExampleActivity.class));
    }

    public void startTakeActivity(View view) {
        startActivity(new Intent(OperatorsActivity.this, TakeExampleActivity.class));
    }

    public void startTimerActivity(View view) {
        startActivity(new Intent(OperatorsActivity.this, TimerExampleActivity.class));
    }

    public void startIntervalActivity(View view) {
        startActivity(new Intent(OperatorsActivity.this, IntervalExampleActivity.class));
    }

    public void startSingleObserverActivity(View view) {
        startActivity(new Intent(OperatorsActivity.this, SingleObserverExampleActivity.class));
    }

    public void startCompletableObserverActivity(View view) {
        startActivity(new Intent(OperatorsActivity.this, CompletableObserverExampleActivity.class));
    }

    public void startFlowableActivity(View view) {
        startActivity(new Intent(OperatorsActivity.this, FlowableExampleActivity.class));
    }

    public void startReduceActivity(View view) {
        startActivity(new Intent(OperatorsActivity.this, ReduceExampleActivity.class));
    }

    public void startBufferActivity(View view) {
        startActivity(new Intent(OperatorsActivity.this, BufferExampleActivity.class));
    }

    public void startFilterActivity(View view) {
        startActivity(new Intent(OperatorsActivity.this, FilterExampleActivity.class));
    }

    public void startSkipActivity(View view) {
        startActivity(new Intent(OperatorsActivity.this, SkipExampleActivity.class));
    }

    public void startScanActivity(View view) {
        startActivity(new Intent(OperatorsActivity.this, ScanExampleActivity.class));
    }

    public void startReplayActivity(View view) {
        startActivity(new Intent(OperatorsActivity.this, ReplayExampleActivity.class));
    }

    public void startConcatActivity(View view) {
        startActivity(new Intent(OperatorsActivity.this, ConcatExampleActivity.class));
    }

    public void startMergeActivity(View view) {
        startActivity(new Intent(OperatorsActivity.this, MergeExampleActivity.class));
    }

    public void startDeferActivity(View view) {
        startActivity(new Intent(OperatorsActivity.this, DeferExampleActivity.class));
    }

    public void startDistinctActivity(View view) {
        startActivity(new Intent(OperatorsActivity.this, DistinctExampleActivity.class));
    }

    public void startLastOperatorActivity(View view) {
        startActivity(new Intent(OperatorsActivity.this, LastOperatorExampleActivity.class));
    }

    public void startReplaySubjectActivity(View view) {
        startActivity(new Intent(OperatorsActivity.this, ReplaySubjectExampleActivity.class));
    }

    public void startPublishSubjectActivity(View view) {
        startActivity(new Intent(OperatorsActivity.this, PublishSubjectExampleActivity.class));
    }

    public void startBehaviorSubjectActivity(View view) {
        startActivity(new Intent(OperatorsActivity.this, BehaviorSubjectExampleActivity.class));
    }

    public void startAsyncSubjectActivity(View view) {
        startActivity(new Intent(OperatorsActivity.this, AsyncSubjectExampleActivity.class));
    }

    public void startThrottleFirstActivity(View view) {
        startActivity(new Intent(OperatorsActivity.this,ThrottleFirstExampleActivity.class));
    }

    public void startThrottleLastActivity(View view) {
        startActivity(new Intent(OperatorsActivity.this, ThrottleLastExampleActivity.class));
    }

    public void startDebounceActivity(View view) {
        startActivity(new Intent(OperatorsActivity.this, DebounceExampleActivity.class));
    }

    public void startWindowActivity(View view) {
        startActivity(new Intent(OperatorsActivity.this,WindowExampleActivity.class));
    }

    public void startDelayActivity(View view) {
        startActivity(new Intent(OperatorsActivity.this,DelayExampleActivity.class));
    }

    public void startSwitchMapActivity(View view) {
        startActivity(new Intent(OperatorsActivity.this, SwitchMapExampleActivity.class));
    }

    public void startTakeWhileActivity(View view) {
        startActivity(new Intent(OperatorsActivity.this, TakeWhileExampleActivity.class));
    }

    public void startTakeUntilActivity(View view) {
        startActivity(new Intent(OperatorsActivity.this, TakeUntilExampleActivity.class));
    }
}
