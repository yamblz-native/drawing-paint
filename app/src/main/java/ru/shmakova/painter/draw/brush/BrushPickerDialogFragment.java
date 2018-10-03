package ru.shmakova.painter.draw.brush;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

import javax.inject.Inject;

import ru.shmakova.painter.R;
import ru.shmakova.painter.app.App;
import ru.shmakova.painter.screen.BaseDialogFragment;
import rx.Observable;
import rx.subjects.PublishSubject;

public class BrushPickerDialogFragment extends BaseDialogFragment implements BrushView {
    @NonNull
    private final PublishSubject<Integer> submitClicks = PublishSubject.create();

    private SeekBar seekBar;

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
