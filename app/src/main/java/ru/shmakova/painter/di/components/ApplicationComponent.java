package ru.shmakova.painter.di.components;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import ru.shmakova.painter.App;
import ru.shmakova.painter.di.modules.ApplicationModule;

@Singleton
@Component(modules = {
        AndroidSupportInjectionModule.class,
        ApplicationModule.class
})
public interface ApplicationComponent extends AndroidInjector<App> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(App application);

        ApplicationComponent build();
    }

    void inject(App app);
}
