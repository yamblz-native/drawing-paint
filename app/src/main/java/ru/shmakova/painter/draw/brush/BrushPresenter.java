package ru.shmakova.painter.draw.brush;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import ru.shmakova.painter.screen.BasePresenter;
import rx.Observable;

public class BrushPresenter extends BasePresenter<BrushView> {
    public final static String STROKE_WIDTH = "STROKE_WIDTH_KEY";

    @NonNull
    private final SharedPreferences sharedPreferences;

    @Inject
    BrushPresenter(@NonNull SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public void bindView(@NonNull BrushView view) {
        super.bindView(view);

        unsubscribeOnUnbindView(
                Observable.just(1)
                        .map(o -> {
                            float savedStrokeWidth = sharedPreferences.getFloat(STROKE_WIDTH, 5f);
                            return savedStrokeWidth;
                        })
                    .subscribe(savedStrokeWidth -> view().setStrokeWidth(savedStrokeWidth)),
                view().submitClicks()
                        .doOnNext(strokeWidth -> sharedPreferences
                                .edit()
                                .putFloat(STROKE_WIDTH, strokeWidth)
                                .apply())
                        .subscribe(strokeWidth -> {
                            view().sendBackResult(strokeWidth);
                            view().dismissDialog();
                        })
        );
    }
}
