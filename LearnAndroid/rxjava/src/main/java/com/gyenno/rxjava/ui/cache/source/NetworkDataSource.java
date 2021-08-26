package com.gyenno.rxjava.ui.cache.source;

import com.gyenno.rxjava.ui.cache.model.Data;

import io.reactivex.Observable;


/**
 * Class to simulate Network DataSource
 */
public class NetworkDataSource {

    public Observable<Data> getData() {
        return Observable.create(emitter -> {
            Data data = new Data();
            data.source = "network";
            emitter.onNext(data);
            emitter.onComplete();
        });
    }

}
