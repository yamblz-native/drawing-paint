package ru.shmakova.painter.di.components;

import android.os.Handler;
import android.support.annotation.NonNull;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;
import ru.shmakova.painter.di.modules.ApplicationModule;
import ru.shmakova.painter.presentation.draw.DrawFragment;
import ru.shmakova.painter.presentation.draw.MainActivity;
import ru.shmakova.painter.presentation.draw.brush.BrushPickerDialogFragment;
import ru.shmakova.painter.presentation.draw.text.TextDialogFragment;

@Singleton
@Component(modules = {
        ApplicationModule.class
})
public interface ApplicationComponent {

    @NonNull
    @Named(ApplicationModule.MAIN_THREAD_HANDLER)
    Handler mainThreadHandler();

    void inject(@NonNull MainActivity mainActivity);

    void inject(@NonNull BrushPickerDialogFragment brushPickerDialogFragment);

    void inject(@NonNull TextDialogFragment textDialogFragment);

    void inject(@NonNull DrawFragment drawFragment);
}
