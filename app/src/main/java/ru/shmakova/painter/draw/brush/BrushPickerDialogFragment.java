package ru.shmakova.painter.draw.brush;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import ru.shmakova.painter.R;
import ru.shmakova.painter.app.App;
import ru.shmakova.painter.screen.BaseDialogFragment;
import rx.Observable;
import rx.subjects.PublishSubject;

public class BrushPickerDialogFragment extends BaseDialogFragment implements BrushView {
    @NonNull
    private final PublishSubject<Integer> submitClicks = PublishSubject.create();

    @BindView(R.id.seek_bar)
    SeekBar seekBar;

    @Inject
    BrushPresenter presenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();
    }

    private void injectDependencies() {
        App.get(getContext()).applicationComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_brush_picker, container, false);
    }

    @OnClick(R.id.edit_brush_btn)
    public void onEditBrushButtonClick(View v) {
        submitClicks.onNext(seekBar.getProgress());
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
    public void sendBackResult(float strokeWidth) {
        BrushPickerDialogListener listener = (BrushPickerDialogListener) getTargetFragment();
        listener.onBrushPick(strokeWidth);
    }

    @Override
    public void setStrokeWidth(float savedStrokeWidth) {
        seekBar.setProgress((int) savedStrokeWidth);
    }

    @Override
    public void dismissDialog() {
        dismiss();
    }

    public interface BrushPickerDialogListener {
        void onBrushPick(float brushWidth);
    }
}
