package ru.yandex.yamblz.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
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

import com.google.android.flexbox.FlexboxLayout;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.drawing.Drawer;
import ru.yandex.yamblz.ui.drawing.DrawerView;

public class ContentFragment extends BaseFragment {

    private static final long COLOR_SCALE_DURATION = 300;

    @BindView(R.id.drawer)
    DrawerView drawerView;

    @BindView(R.id.size)
    SeekBar sizeSeekBar;

    @BindView(R.id.palette)
    FlexboxLayout palette;

    private BottomSheetBehavior mBottomSheetBehavior;

    private int mSelectedTool = -1;

    private Map<Integer, Drawer.Tool> mId2tool = new HashMap<>();
    private Map<Drawer.Tool, Integer> mTool2id = new HashMap<>();
    private View prevSelectedIV;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        drawerView.setSize(20);
        drawerView.setColor(Color.RED);

        mBottomSheetBehavior = BottomSheetBehavior.from(getView().findViewById(R.id.paint_toolbar));

        sizeSeekBar.setProgress((int)drawerView.getSize());
        sizeSeekBar.setOnSeekBarChangeListener(mOnSizeSeekBarChangeListener);

        initPalette();
        initMaps();
        initTool();
    }

    private void initTool() {
        Drawer.Tool tool = drawerView.getTool();
        mSelectedTool = mTool2id.get(tool);
        setToolIcon(mSelectedTool, true);
    }

    private void initMaps() {
        mTool2id.put(Drawer.Tool.Pencil, R.id.pencil);
        mTool2id.put(Drawer.Tool.Brush, R.id.brush);
        mTool2id.put(Drawer.Tool.Eraser, R.id.eraser);

        for(Map.Entry<Drawer.Tool, Integer> entry : mTool2id.entrySet()) {
            mId2tool.put(entry.getValue(), entry.getKey());
        }
    }

    private void initPalette() {
        palette.removeAllViews(); //TODO
        int[] colors = getResources().getIntArray(R.array.palette);
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        for(int color : colors) {
            ViewGroup vg = (ViewGroup) layoutInflater.inflate(R.layout.color, palette, false);
            ImageView iv = (ImageView)vg.findViewById(R.id.color);
            iv.setImageResource(R.drawable.color);
            ((GradientDrawable)iv.getDrawable()).setColor(color);
            palette.addView(vg);
            iv.setTag(R.id.color, color);
            iv.setOnClickListener(mColorClickListener);
        }
    }

    private final View.OnClickListener mColorClickListener = (view) -> {
        int selectedColor = (int)view.getTag(R.id.color);
        if(selectedColor != drawerView.getColor()) {
            Animator prevAnimator = animateColor(prevSelectedIV, false);
            Animator curAnimator = animateColor(view, true);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(prevAnimator, curAnimator);
            animatorSet.setDuration(COLOR_SCALE_DURATION);
            animatorSet.start();
            drawerView.setColor(selectedColor);
            prevSelectedIV = view;
        }
    };

    private Animator animateColor(View view, boolean scale) {
        float from, to;
        from = (scale ? 1.0f : 1.3f);
        to = (scale ? 1.3f : 1.0f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", from, to),
                ObjectAnimator.ofFloat(view, "scaleY", from, to));
        return set;
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
        drawerView.selectTool(mId2tool.get(id));
    }

    private void setToolIcon(int id, boolean active) {
        if(id == -1) {
            return;
        }
        int icon = -1;
        switch (id) {
            case R.id.pencil:
                icon = (!active ? R.drawable.ic_pencil_grey600_24dp : R.drawable.ic_pencil_white_24dp);
                break;
            case R.id.brush:
                icon = (!active ? R.drawable.ic_brush_grey600_24dp : R.drawable.ic_brush_white_24dp);
                break;
            case R.id.eraser:
                icon = (!active ? R.drawable.ic_eraser_grey600_24dp : R.drawable.ic_eraser_white_24dp);
                break;
        }
        ((ImageView)viewById(id)).setImageResource(icon);
    }

    private View viewById(int id) {
        return getView().findViewById(id);
    }
}
