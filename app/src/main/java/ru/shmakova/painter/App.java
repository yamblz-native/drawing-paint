package ru.shmakova.painter;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import ru.shmakova.painter.developer_settings.DevMetricsProxy;
import ru.shmakova.painter.developer_settings.DeveloperSettingsModel;
import timber.log.Timber;

public class App extends Application {
    private ApplicationComponent applicationComponent;

    // Prevent need in a singleton (global) reference to the application object.
    @NonNull
    public static App get(@NonNull Context context) {
        return (App) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = prepareApplicationComponent().build();
        Fabric.with(this, new Crashlytics());

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());

            DeveloperSettingsModel developerSettingModel = applicationComponent.developerSettingModel();
            developerSettingModel.apply();

            DevMetricsProxy devMetricsProxy = applicationComponent.devMetricsProxy();
            devMetricsProxy.apply();
        }
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
