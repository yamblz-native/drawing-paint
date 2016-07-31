package ru.yandex.yamblz.ui.fragments;

import android.graphics.Color;
import android.graphics.Path;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.drawing.DrawerView;

public class ContentFragment extends BaseFragment {

    @BindView(R.id.drawer)
    DrawerView drawerView;

    @BindView(R.id.size)
    SeekBar sizeSeekBar;

    private BottomSheetBehavior mBottomSheetBehavior;

    private int mSelectedTool = -1;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);

        ButterKnife.bind(this, view);

        drawerView.setSize(20);
        drawerView.setColor(Color.RED);

        mBottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.paint_toolbar));

        sizeSeekBar.setOnSeekBarChangeListener(mOnSizeSeekBarChangeListener);

        return view;
    }

    private final SeekBar.OnSeekBarChangeListener mOnSizeSeekBarChangeListener =
            new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            drawerView.setSize(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    @OnClick({R.id.eraser, R.id.brush, R.id.pencil})
    void onToolClick(View view) {
        int id = view.getId();
        if(mSelectedTool == id) {
            drawerView.disable();
            setToolIcon(mSelectedTool, false);
            mSelectedTool = -1;
        } else {
            setToolIcon(mSelectedTool, false);
            setToolIcon(id, true);
            selectTool(id);
            mSelectedTool = id;
        }
    }

    @OnClick(R.id.paint_toolbar)
    void onToolbarClick() {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void selectTool(int id) {
        switch (id) {
            case R.id.pencil:
                drawerView.pencil();
                break;
            case R.id.brush:
                drawerView.brush();
                break;
            case R.id.eraser:
                drawerView.eraser();
                break;
        }
    }

    private void setToolIcon(int id, boolean active) {
        if(id == -1) {
            return;
        }
        int icon = -1;
        switch (id) {
            case R.id.pencil:
                icon = (active ? R.drawable.ic_pencil_grey600_24dp : R.drawable.ic_pencil_white_24dp);
                break;
            case R.id.brush:
                icon = (active ? R.drawable.ic_brush_grey600_24dp : R.drawable.ic_brush_white_24dp);
                break;
            case R.id.eraser:
                icon = (active ? R.drawable.ic_eraser_grey600_24dp : R.drawable.ic_eraser_white_24dp);
                break;
        }
        ((ImageView)viewById(id)).setImageResource(icon);
    }

    private View viewById(int id) {
        return getView().findViewById(id);
    }
}
