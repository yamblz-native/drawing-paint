package ru.shmakova.painter.presentation.draw.text;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import ru.shmakova.painter.presentation.base.BasePresenter;

public class TextDialogPresenter extends BasePresenter<TextDialogView> {

    @Inject
    TextDialogPresenter() {
        // No op
    }

    @Override
    public void bindView(@NonNull TextDialogView view) {
        super.bindView(view);

        unsubscribeOnUnbindView(
                view().submitClicks()
                        .subscribe(text -> {
                            view().sendBackResult(text);
                            view().dismissDialog();
                        })
        );
    }
}
