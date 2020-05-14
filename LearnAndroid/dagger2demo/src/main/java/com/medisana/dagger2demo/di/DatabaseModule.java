package com.medisana.dagger2demo.di;

import com.medisana.dagger2demo.obj.DatabaseObject;
import com.medisana.dagger2demo.obj.HttpObject;

import dagger.Module;
import dagger.Provides;

/**
 * 提供对象
 */
@Module
public class DatabaseModule {
    @Provides
    public DatabaseObject provideDatabaseObject(){
        return new DatabaseObject();
    }

}
