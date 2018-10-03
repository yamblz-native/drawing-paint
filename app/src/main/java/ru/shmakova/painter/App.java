package ru.shmakova.painter;

import android.app.Application;
import android.support.v4.app.Fragment;

import com.crashlytics.android.Crashlytics;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import io.fabric.sdk.android.Fabric;
import ru.shmakova.painter.di.components.ApplicationComponent;
import ru.shmakova.painter.di.components.DaggerApplicationComponent;
import ru.shmakova.painter.utils.ErrorsReportingTree;
import timber.log.Timber;

public class App extends Application implements HasSupportFragmentInjector {
    private ApplicationComponent applicationComponent;

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = DaggerApplicationComponent
                .builder()
                .application(this)
                .build();
        applicationComponent.inject(this);
        initTrackers();
        initLogger();
    }

    protected void initLogger() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new ErrorsReportingTree());
        }
    }

    protected void initTrackers() {
        Fabric.with(this, new Crashlytics());
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }
}
