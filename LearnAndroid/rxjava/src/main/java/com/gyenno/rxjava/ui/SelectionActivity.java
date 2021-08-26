package com.gyenno.rxjava.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.gyenno.rxjava.R;
import com.gyenno.rxjava.RxApp;
import com.gyenno.rxjava.ui.cache.CacheExampleActivity;
import com.gyenno.rxjava.ui.compose.ComposeOperatorExampleActivity;
import com.gyenno.rxjava.ui.networking.NetworkingActivity;
import com.gyenno.rxjava.ui.pagination.PaginationActivity;
import com.gyenno.rxjava.ui.rxbus.RxBusActivity;
import com.gyenno.rxjava.ui.search.SearchActivity;

public class SelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
    }

    public void startOperatorsActivity(View view) {
        startActivity(new Intent(SelectionActivity.this, OperatorsActivity.class));
    }

    public void startNetworkingActivity(View view) {
        startActivity(new Intent(SelectionActivity.this, NetworkingActivity.class));
    }

    public void startCacheActivity(View view) {
        startActivity(new Intent(SelectionActivity.this, CacheExampleActivity.class));
    }

    public void startRxBusActivity(View view) {
        ((RxApp) getApplication()).sendAutoEvent();
        startActivity(new Intent(SelectionActivity.this, RxBusActivity.class));
    }

    public void startPaginationActivity(View view) {
        startActivity(new Intent(SelectionActivity.this, PaginationActivity.class));
    }

    public void startComposeOperator(View view) {
        startActivity(new Intent(SelectionActivity.this, ComposeOperatorExampleActivity.class));
    }

    public void startSearchActivity(View view) {
        startActivity(new Intent(SelectionActivity.this, SearchActivity.class));
    }

}
