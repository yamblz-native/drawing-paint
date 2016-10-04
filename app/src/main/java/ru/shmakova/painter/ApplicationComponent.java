package ru.shmakova.painter;

import android.os.Handler;
import android.support.annotation.NonNull;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;
import ru.shmakova.painter.developer_settings.DevMetricsProxy;
import ru.shmakova.painter.developer_settings.DeveloperSettingsComponent;
import ru.shmakova.painter.developer_settings.DeveloperSettingsModel;
import ru.shmakova.painter.developer_settings.DeveloperSettingsModule;
import ru.shmakova.painter.developer_settings.LeakCanaryProxy;
import ru.shmakova.painter.ui.activities.MainActivity;

@Singleton
@Component(modules = {
        ApplicationModule.class,
        DeveloperSettingsModule.class,
})
public interface ApplicationComponent {

    // Provide LeakCanary without injection to leave.
    @NonNull
    LeakCanaryProxy leakCanaryProxy();

    @NonNull
    DeveloperSettingsComponent plusDeveloperSettingsComponent();

    DeveloperSettingsModel developerSettingModel();

    DevMetricsProxy devMetricsProxy();

    @NonNull @Named(ApplicationModule.MAIN_THREAD_HANDLER)
    Handler mainThreadHandler();

    void inject(@NonNull MainActivity mainActivity);
}
