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
import ru.shmakova.painter.developer_settings.DevMetricsProxy;
import ru.shmakova.painter.developer_settings.DeveloperSettingsModel;
import ru.shmakova.painter.utils.ErrorsReportingTree;
import timber.log.Timber;

public class App extends Application {
    private static final String APPMETRICA_API_KEY = "587dddf3-74f5-4bd8-958e-80986e4fedb9";
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
        YandexMetrica.activate(getApplicationContext(), APPMETRICA_API_KEY);
        YandexMetrica.enableActivityAutoTracking(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());

            DeveloperSettingsModel developerSettingModel = applicationComponent.developerSettingModel();
            developerSettingModel.apply();

            DevMetricsProxy devMetricsProxy = applicationComponent.devMetricsProxy();
            devMetricsProxy.apply();
        } else {
            Timber.plant(new ErrorsReportingTree());
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
