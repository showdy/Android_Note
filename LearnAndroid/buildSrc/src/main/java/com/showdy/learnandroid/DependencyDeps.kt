package com.showdy.learnandroid

/**
 * Created by <b>Showdy</b> on 2020/10/29 19:14
 *
 */
object Version {
    val retrofit = "2.9.0"
    val okhttpLogging = "4.8.0"
    val appcompat = "1.2.0"
    val coreKtx = "1.3.1"
    val constraintlayout = "2.0.4"
    val paging = "3.0.0-alpha02"
    val timber = "4.7.1"
    val kotlin = "1.4.10"
    val kotlinCoroutinesCore = "1.4.0"
    val kotlinCoroutinesAndrid = "1.4.0"
    val koin = "2.1.5"
    val work = "2.2.0"
    val room = "2.3.0-alpha01"
    val cardview = "1.0.0"
    val recyclerview = "1.0.0"
    val fragment = "1.3.0-alpha06"
    val anko = "0.10.8"
    val swiperefreshlayout = "1.1.0"
    val junit = "4.13"
    val junitExt = "1.1.1"
    val espressoCore = "3.2.0"
    val jDatabinding = "1.0.1"
    val progressview = "1.0.0"
    val runtime = "0.11.0"
    val hit = "2.28-alpha"
    val hitViewModule = "1.0.0-alpha01"
    val appStartup = "1.0.0-alpha01"
    val material = "1.2.0-alpha06"
    val activity = "1.1.0"
    val annotation = "1.1.0"
    val lifecycle = "2.2.0"
    val navigation = "2.3.0-alpha01"
    val rx_android = "2.0.1"
    val rxjava2 = "2.1.3"
    val truth = "1.0.1"
}

object AndroidX {
    val appcompat = "androidx.appcompat:appcompat:${Version.appcompat}"
    val coreKtx = "androidx.core:core-ktx:${Version.coreKtx}"
    val constraintlayout =
        "androidx.constraintlayout:constraintlayout:${Version.constraintlayout}"
    val pagingRuntime = "androidx.paging:paging-runtime:${Version.paging}"

    val workRuntime = "androidx.work:work-runtime:${Version.work}"
    val workTesting = "androidx.work:work-testing:${Version.work}"
    val cardview = "androidx.cardview:cardview:${Version.cardview}"
    val recyclerview = "androidx.recyclerview:recyclerview:${Version.recyclerview}"
    val swiperefreshlayout =
        "androidx.swiperefreshlayout:swiperefreshlayout:${Version.swiperefreshlayout}"

    val appStartup = "androidx.startup:startup-runtime:${Version.appStartup}"

    val annotation = "androidx.annotation:annotation:${Version.annotation}"

    // To use the Java-compatible @Experimental API annotation
    val annotationExperimentail =
        "androidx.annotation:annotation-experimental:${Version.annotation}"
}

object Activity {
    val activity = "androidx.activity:activity:${Version.activity}"
    val activityktx = "androidx.activity:activity-ktx:${Version.activity}"
}

object Google {
    val material = "com.google.android.material:material:${Version.material}"
    val truth = "com.google.truth:truth:${Version.truth}"
}

object Hilt {
    val daggerRuntime = "com.google.dagger:hilt-android:${Version.hit}"
    val daggerCompiler = "com.google.dagger:hilt-android-compiler:${Version.hit}"
    val viewModule = "androidx.hilt:hilt-lifecycle-viewmodel:${Version.hitViewModule}"
    val compiler = "androidx.hilt:hilt-compiler:${Version.hitViewModule}"
}

object Coil {
    val runtime = "io.coil-kt:coil:${Version.runtime}"
}

object Future {
    val concurrent_future = "androidx.concurrent:concurrent-futures:1.1.0"
    // Kotlin
    val concurrent_futures_ktx = "androidx.concurrent:concurrent-futures-ktx:1.1.0"
}

object DataStore {
    // Preferences DataStore
    val ds_prefrences = "androidx.datastore:datastore-preferences:1.0.0-alpha02"

    // Proto DataStore
    val ds_core = "androidx.datastore:datastore-core:1.0.0-alpha02"
}

object Lifecycel {
    val viewmodel_ktx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Version.lifecycle}"
    val viewmodel = "androidx.lifecycle:lifecycle-viewmodel:${Version.lifecycle}"

    // LiveData
    val livedata_ktx = "androidx.lifecycle:lifecycle-livedata-ktx:${Version.lifecycle}"
    val livedata = "androidx.lifecycle:lifecycle-livedata:${Version.lifecycle}"

    // Lifecycles only (without ViewModel or LiveData)
    val lifecylce_runtime = "androidx.lifecycle:lifecycle-runtime:${Version.lifecycle}"
    val lifecylce_runtime_ktx = "androidx.lifecycle:lifecycle-runtime-ktx:${Version.lifecycle}"

    // Saved state module for ViewModel
    val savestate = "androidx.lifecycle:lifecycle-viewmodel-savedstate:${Version.lifecycle}"

    // Annotation processor
    val lifeycel_compiler = "androidx.lifecycle:lifecycle-compiler:${Version.lifecycle}"

    // alternately - if using Java8, use the following instead of lifecycle-compiler
    val lifecycle_common_java8 = "androidx.lifecycle:lifecycle-common-java8:${Version.lifecycle}"

    // optional - helpers for implementing LifecycleOwner in a Service
    val lifecycle_service = "androidx.lifecycle:lifecycle-service:${Version.lifecycle}"

    // optional - ProcessLifecycleOwner provides a lifecycle for the whole application process
    val lifecycle_process = "androidx.lifecycle:lifecycle-process:${Version.lifecycle}"

    // optional - ReactiveStreams support for LiveData
    val reactiveStream = "androidx.lifecycle:lifecycle-reactivestreams:${Version.lifecycle}"
    val reactive_stream_ktx =
        "androidx.lifecycle:lifecycle-reactivestreams-ktx:${Version.lifecycle}"
}

object Room {
    val runtime = "androidx.room:room-runtime:${Version.room}"
    val compiler = "androidx.room:room-compiler:${Version.room}"
    val ktx = "androidx.room:room-ktx:${Version.room}"
    val rxjava2 = "androidx.room:room-rxjava2:${Version.room}"
    val testing = "androidx.room:room-testing:${Version.room}"
}

object Navigation {
    val navigation_runtime = "androidx.navigation:navigation-runtime:${Version.navigation}"
    val navigation_runtime_ktx = "androidx.navigation:navigation-runtime-ktx:${Version.navigation}"
    val navigation_fragment = "androidx.navigation:navigation-fragment:${Version.navigation}"
    val navigation_fragment_ktx =
        "androidx.navigation:navigation-fragment-ktx:${Version.navigation}"
    val navigation_testing = "androidx.navigation:navigation-testing:${Version.navigation}"
    val navigation_ui = "androidx.navigation:navigation-ui:${Version.navigation}"
    val navigation_ui_ktx = "androidx.navigation:navigation-ui-ktx:${Version.navigation}"
    val navigation_safe_args_plugin =
        "androidx.navigation:navigation-safe-args-gradle-plugin:${Version.navigation}"
}


object Fragment {
    val runtime = "androidx.fragment:fragment:${Version.fragment}"
    val runtimeKtx = "androidx.fragment:fragment-ktx:${Version.fragment}"
    val testing = "androidx.fragment:fragment-testing:${Version.fragment}"
}

object Kotlin {
    val stdlibJdk7 = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Version.kotlin}"
    val stdlibJdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Version.kotlin}"
    val coroutinesCore =
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Version.kotlinCoroutinesCore}"
    val coroutinesAndroid =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Version.kotlinCoroutinesAndrid}"
    val test = "org.jetbrains.kotlin:kotlin-test-junit:${Version.kotlin}"
    val plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Version.kotlin}"
    val allopen = "org.jetbrains.kotlin:kotlin-allopen:${Version.kotlin}"
    val reflect = "org.jetbrains.kotlin:kotlin-reflect:${Version.kotlin}"
}

object Koin {
    val core = "org.koin:koin-core:${Version.koin}"
    val androidCore = "org.koin:koin-android:${Version.koin}"
    val viewmodel = "org.koin:koin-androidx-viewmodel:${Version.koin}"
    val androidScope = "org.koin:koin-android-scope:$${Version.koin}"
}

object Anko {
    val common = "org.jetbrains.anko:anko-common:${Version.anko}"
    val sqlite = "org.jetbrains.anko:anko-sqlite:${Version.anko}"
    val coroutines = "org.jetbrains.anko:anko-coroutines:${Version.anko}"
    val design = "org.jetbrains.anko:anko-design:${Version.anko}" // For SnackBars
}

object Retrofit {
    val runtime = "com.squareup.retrofit2:retrofit:${Version.retrofit}"
    val gson = "com.squareup.retrofit2:converter-gson:${Version.retrofit}"
    val mock = "com.squareup.retrofit2:retrofit-mock:${Version.retrofit}"
    val logging = "com.squareup.okhttp3:logging-interceptor:${Version.okhttpLogging}"
}

object RxJava {
    val rx_android = "io.reactivex.rxjava2:rxandroid:${Version.rx_android}"
    val rxjava2 = "io.reactivex.rxjava2:rxjava:${Version.rxjava2}"
}


object Depend {

    val junit = "junit:junit:${Version.junit}"
    val androidTestJunit = "androidx.test.ext:junit:${Version.junitExt}"
    val espressoCore = "androidx.test.espresso:espresso-core:${Version.espressoCore}"
    val jDatabinding = "com.hi-dhl:jdatabinding:${Version.jDatabinding}"
    val progressview = "com.hi-dhl:progressview:${Version.progressview}"
    val timber = "com.jakewharton.timber:timber:${Version.timber}"
}

object Third{
    val logger = "com.orhanobut:logger:2.2.0"
    val perrmissionx = "com.permissionx.guolindev:permissionx:1.4.0"
    val zxing= "me.dm7.barcodescanner:zxing:1.9.8"
}