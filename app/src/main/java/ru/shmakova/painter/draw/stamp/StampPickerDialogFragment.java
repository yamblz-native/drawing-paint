package ru.shmakova.painter.draw.stamp;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.OnClick;
import ru.shmakova.painter.R;
import ru.shmakova.painter.app.App;
import ru.shmakova.painter.screen.BaseDialogFragment;
import rx.Observable;
import rx.subjects.PublishSubject;

public class StampPickerDialogFragment extends BaseDialogFragment implements StampView {
    @NonNull
    private final PublishSubject<Integer> submitClicks = PublishSubject.create();

    @Inject
    StampPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stamp_picker, container, false);
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();
    }

    private void injectDependencies() {
        App.get(getContext()).applicationComponent().inject(this);
    }

    @OnClick({
            R.id.sticker_1, R.id.sticker_2, R.id.sticker_3, R.id.sticker_4, R.id.sticker_5,
            R.id.sticker_6, R.id.sticker_7, R.id.sticker_8, R.id.sticker_9, R.id.sticker_10})
    public void onStickerOneClick(View v) {
        submitClicks.onNext(v.getId());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.bindView(this);
    }

    @Override
    public void onDestroyView() {
        presenter.unbindView(this);
        super.onDestroyView();
    }

    @Override
    public Observable<Integer> submitClicks() {
        return submitClicks;
    }

    @Override
    public void sendBackResult(@IdRes int stamp) {
        StampPickerDialogListener listener = (StampPickerDialogListener) getTargetFragment();
        listener.onStampPick(stamp);
    }

    @Override
    public void dismissDialog() {
        dismiss();
    }

    public interface StampPickerDialogListener {
        void onStampPick(int stamp);
    }
}
