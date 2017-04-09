package ru.shmakova.painter.draw.text;

import rx.Observable;

public interface TextDialogView {
    Observable<String> submitClicks();

    void sendBackResult(String text);

    void dismissDialog();
}
