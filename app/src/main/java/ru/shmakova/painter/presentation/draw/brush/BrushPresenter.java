package ru.shmakova.painter.presentation.draw.brush;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import ru.shmakova.painter.presentation.base.BasePresenter;

public class BrushPresenter extends BasePresenter<BrushView> {
    public static final String STROKE_WIDTH = "STROKE_WIDTH_KEY";

    @NonNull
    private final SharedPreferences sharedPreferences;

    @Inject
    BrushPresenter(@NonNull SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public void bindView(@NonNull BrushView view) {
        super.bindView(view);
        float savedStrokeWidth = sharedPreferences.getFloat(STROKE_WIDTH, 5f);
        view().setStrokeWidth(savedStrokeWidth);

        unsubscribeOnUnbindView(
                view().submitClicks()
                        .doOnNext(strokeWidth -> sharedPreferences.edit()
                                .putFloat(STROKE_WIDTH, strokeWidth)
                                .apply())
                        .subscribe(strokeWidth -> {
                            view().sendBackResult(strokeWidth);
                            view().dismissDialog();
                        })
        );
    }
}
