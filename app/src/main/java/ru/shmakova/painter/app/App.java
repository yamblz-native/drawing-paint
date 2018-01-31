package ru.shmakova.painter.app;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.yandex.metrica.YandexMetrica;

import io.fabric.sdk.android.Fabric;
import ru.shmakova.painter.BuildConfig;
import ru.shmakova.painter.app.di.components.ApplicationComponent;
import ru.shmakova.painter.app.di.components.DaggerApplicationComponent;
import ru.shmakova.painter.app.di.modules.ApplicationModule;
import ru.shmakova.painter.utils.ErrorsReportingTree;
import timber.log.Timber;

public class App extends Application {
    private static final String APPMETRICA_API_KEY = "587dddf3-74f5-4bd8-958e-80986e4fedb9";
    private ApplicationComponent applicationComponent;

    @NonNull
    public static App get(@NonNull Context context) {
        return (App) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = prepareApplicationComponent().build();
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
        YandexMetrica.activate(getApplicationContext(), APPMETRICA_API_KEY);
        YandexMetrica.enableActivityAutoTracking(this);
    }

    @NonNull
    protected DaggerApplicationComponent.Builder prepareApplicationComponent() {
        return DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this));
    }

    @NonNull
    public ApplicationComponent applicationComponent() {
        return applicationComponent;
    }
}
