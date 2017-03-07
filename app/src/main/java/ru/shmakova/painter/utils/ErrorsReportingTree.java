package ru.shmakova.painter.utils;

import android.support.annotation.Nullable;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.yandex.metrica.YandexMetrica;

import timber.log.Timber;

public class ErrorsReportingTree extends Timber.Tree {
    @Override
    protected void log(int priority, @Nullable String tag, @Nullable String message, @Nullable Throwable t) {
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return;
        }

        if (priority == Log.ERROR) {
            if (t == null) {
                Throwable throwable = new RuntimeException(message);
                Crashlytics.logException(throwable);
                YandexMetrica.reportError(message, throwable);
            } else {
                Crashlytics.logException(t);
                YandexMetrica.reportError(message, t);
            }
        }
    }
}
