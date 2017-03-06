package ru.shmakova.painter.app.di.components;

import android.os.Handler;
import android.support.annotation.NonNull;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;
import ru.shmakova.painter.app.di.modules.ApplicationModule;
import ru.shmakova.painter.app.di.modules.DeveloperSettingsModule;
import ru.shmakova.painter.developer_settings.DevMetricsProxy;
import ru.shmakova.painter.developer_settings.DeveloperSettingsModel;
import ru.shmakova.painter.developer_settings.LeakCanaryProxy;
import ru.shmakova.painter.draw.MainActivity;
import ru.shmakova.painter.draw.brush.BrushPickerDialogFragment;
import ru.shmakova.painter.draw.filter.FilterPickerDialogFragment;
import ru.shmakova.painter.draw.stamp.StampPickerDialogFragment;
import ru.shmakova.painter.draw.text.TextDialogFragment;

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

    @NonNull
    @Named(ApplicationModule.MAIN_THREAD_HANDLER)
    Handler mainThreadHandler();

    void inject(@NonNull MainActivity mainActivity);

    void inject(@NonNull BrushPickerDialogFragment brushPickerDialogFragment);

    void inject(@NonNull FilterPickerDialogFragment filterPickerDialogFragment);

    void inject(@NonNull StampPickerDialogFragment stampPickerDialogFragment);

    void inject(@NonNull TextDialogFragment textDialogFragment);
}
