package ru.shmakova.painter.presentation.draw.brush;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import ru.shmakova.painter.R;
import ru.shmakova.painter.presentation.base.BaseDialogFragment;
import rx.Observable;
import rx.subjects.PublishSubject;

public class BrushPickerDialogFragment extends BaseDialogFragment implements BrushView {
    public static final String TAG = "fragment_brush_picker";

    @NonNull
    private final PublishSubject<Integer> submitClicks = PublishSubject.create();

    private SeekBar seekBar;

    @Inject
    BrushPresenter presenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        AndroidSupportInjection.inject(this);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_brush_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        seekBar = view.findViewById(R.id.seek_bar);
        Button editBrushButton = view.findViewById(R.id.edit_brush_btn);
        editBrushButton.setOnClickListener(v -> submitClicks.onNext(seekBar.getProgress()));
        presenter.bindView(this);
    }

    @Override
    public void onDestroyView() {
        presenter.unbindView(this);
        seekBar = null;
        super.onDestroyView();
    }

    @Override
    @NonNull
    public Observable<Integer> submitClicks() {
        return submitClicks;
    }

    @Override
    public void sendBackResult(float strokeWidth) {
        BrushPickerDialogListener listener = (BrushPickerDialogListener) getTargetFragment();
        if (listener != null) {
            listener.onBrushPick(strokeWidth);
        }
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
