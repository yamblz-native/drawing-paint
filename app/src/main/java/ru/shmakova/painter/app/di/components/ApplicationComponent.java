package ru.shmakova.painter.app.di.components;

import android.os.Handler;
import android.support.annotation.NonNull;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Component;
import ru.shmakova.painter.app.di.modules.ApplicationModule;
import ru.shmakova.painter.draw.DrawFragment;
import ru.shmakova.painter.draw.MainActivity;
import ru.shmakova.painter.draw.brush.BrushPickerDialogFragment;
import ru.shmakova.painter.draw.text.TextDialogFragment;

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
