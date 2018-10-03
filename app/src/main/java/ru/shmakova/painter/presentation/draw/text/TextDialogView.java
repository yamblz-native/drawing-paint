package ru.shmakova.painter.presentation.draw.text;

import android.support.annotation.NonNull;

import rx.Observable;

public interface TextDialogView {
    @NonNull
    Observable<String> submitClicks();

    void sendBackResult(@NonNull String text);

    void dismissDialog();
}
