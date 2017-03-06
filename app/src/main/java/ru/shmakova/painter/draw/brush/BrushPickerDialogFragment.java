package ru.shmakova.painter.draw.brush;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ru.shmakova.painter.R;
import ru.shmakova.painter.screen.BaseDialogFragment;

public class BrushPickerDialogFragment extends BaseDialogFragment {
    @BindView(R.id.seek_bar)
    SeekBar seekBar;
    private Unbinder unbinder;

    public static BrushPickerDialogFragment newInstance() {
        return new BrushPickerDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_brush_picker, container, false);
        unbinder = ButterKnife.bind(this, view);
        getDialog().setTitle(R.string.set_brush_width);
        return view;
    }

    @OnClick(R.id.edit_brush_btn)
    public void onEditBrushButtonClick(View v) {
        sendBackResult();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public void sendBackResult() {
        BrushPickerDialogListener listener = (BrushPickerDialogListener) getTargetFragment();
        listener.onBrushPick(seekBar.getProgress());
        dismiss();
    }

    public interface BrushPickerDialogListener {
        void onBrushPick(int brushWidth);
    }
}