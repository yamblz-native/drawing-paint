package ru.shmakova.painter.developer_settings;

import android.support.annotation.NonNull;

import ru.shmakova.painter.ui.fragments.DeveloperSettingsFragment;

import dagger.Subcomponent;

@Subcomponent
public interface DeveloperSettingsComponent {
    void inject(@NonNull DeveloperSettingsFragment developerSettingsFragment);
}
