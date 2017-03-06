package ru.shmakova.painter.app;

import android.app.Application;
import android.support.annotation.NonNull;

import ru.shmakova.painter.app.di.components.DaggerApplicationComponent;
import ru.shmakova.painter.app.di.modules.DeveloperSettingsModule;
import ru.shmakova.painter.developer_settings.DevMetricsProxy;

public class UnitTestApp extends App {

    @NonNull
    @Override
    protected DaggerApplicationComponent.Builder prepareApplicationComponent() {
        return super.prepareApplicationComponent()
                .developerSettingsModule(new DeveloperSettingsModule() {
                    @NonNull
                    public DevMetricsProxy provideDevMetricsProxy(@NonNull Application application) {
                        return () -> {
                            //No Op
                        };
                    }
                });
    }
}
