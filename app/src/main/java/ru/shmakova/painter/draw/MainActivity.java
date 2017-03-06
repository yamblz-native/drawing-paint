package ru.shmakova.painter.draw;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;

import javax.inject.Inject;
import javax.inject.Named;

import ru.shmakova.painter.BuildConfig;
import ru.shmakova.painter.R;
import ru.shmakova.painter.app.App;
import ru.shmakova.painter.app.di.modules.DeveloperSettingsModule;
import ru.shmakova.painter.developer_settings.ViewModifier;
import ru.shmakova.painter.screen.BaseActivity;

public class MainActivity extends BaseActivity {

    @Inject @Named(DeveloperSettingsModule.MAIN_ACTIVITY_VIEW_MODIFIER)
    ViewModifier viewModifier;

    @SuppressLint("InflateParams") // It's okay in our case.
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.get(this).applicationComponent().inject(this);

        if (BuildConfig.DEBUG) {
            setContentView(viewModifier.modify(getLayoutInflater().inflate(R.layout.activity_main, null)));
        } else {
            setContentView(R.layout.activity_main);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_frame_layout, new ContentFragment())
                    .commit();
        }
    }
}
