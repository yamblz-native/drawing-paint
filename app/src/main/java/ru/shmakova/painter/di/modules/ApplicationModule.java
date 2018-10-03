package ru.shmakova.painter.di.modules;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import ru.shmakova.painter.App;
import ru.shmakova.painter.presentation.draw.DrawFragment;
import ru.shmakova.painter.presentation.draw.brush.BrushPickerDialogFragment;
import ru.shmakova.painter.presentation.draw.text.TextDialogFragment;

@Module
public abstract class ApplicationModule {
    @NonNull
    @Provides
    @Singleton
    static Context provideContext(@NonNull App app) {
        return app.getApplicationContext();
    }

    @Provides
    @NonNull
    @Singleton
    static SharedPreferences provideSharedPreferences(@NonNull App application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @ContributesAndroidInjector
    abstract BrushPickerDialogFragment contributeBrushPickerDialogFragmentInjector();

    @ContributesAndroidInjector
    abstract TextDialogFragment contributeTextDialogFragmentInjector();

    @ContributesAndroidInjector
    abstract DrawFragment contributeDrawFragmentInjector();
}
