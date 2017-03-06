package ru.shmakova.painter.app.di.components;

import android.support.annotation.NonNull;

import dagger.Subcomponent;
import ru.shmakova.painter.developer_settings.DeveloperSettingsFragment;

@Subcomponent
public interface DeveloperSettingsComponent {
    void inject(@NonNull DeveloperSettingsFragment developerSettingsFragment);
}
