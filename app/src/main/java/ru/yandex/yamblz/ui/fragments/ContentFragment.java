package ru.yandex.yamblz.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.google.android.flexbox.FlexboxLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.drawing.Drawer;
import ru.yandex.yamblz.ui.drawing.DrawerView;

public class ContentFragment extends BaseFragment implements EditTextDialog.Callbacks, ListDialog.Callbacks {

    private static final long COLOR_SCALE_DURATION = 300;

    private static final int SAVE_FILE_DIALOG_ID = 1;

    private static final int OPEN_FILE_DIALOG_ID = 2;

    private static final int FILTER_DIALOG_ID = 3;

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
    private Map<Integer, View> mColorToView = new HashMap<>();

    private int[] mColors;

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

        mBottomSheetBehavior = BottomSheetBehavior.from(getView().findViewById(R.id.paint_toolbar));

        sizeSeekBar.setProgress((int)drawerView.getSize());
        sizeSeekBar.setOnSeekBarChangeListener(mOnSizeSeekBarChangeListener);

        initColors();
        initPalette();
        initMaps();
        initTool();

        drawerView.setColor(-1);
        onColorPicked(mColors[0]);
    }

    private void initColors() {
        mColors = getResources().getIntArray(R.array.palette);
    }

    private void initTool() {
        Drawer.Tool tool = drawerView.getTool();
        mSelectedTool = mTool2id.get(tool);
        setToolIcon(mSelectedTool, true);
    }

    private void initMaps() {
        mTool2id.put(Drawer.Tool.PENCIL, R.id.pencil);
        mTool2id.put(Drawer.Tool.BRUSH, R.id.brush);
        mTool2id.put(Drawer.Tool.ERASER, R.id.eraser);

        for(Map.Entry<Drawer.Tool, Integer> entry : mTool2id.entrySet()) {
            mId2tool.put(entry.getValue(), entry.getKey());
        }
    }

    private void initPalette() {
        palette.removeAllViews(); //TODO
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        for(int color : mColors) {
            ViewGroup vg = (ViewGroup) layoutInflater.inflate(R.layout.color, palette, false);
            ImageView iv = (ImageView)vg.findViewById(R.id.color);
            iv.setImageResource(R.drawable.color);
            ((GradientDrawable)iv.getDrawable()).setColor(color);
            palette.addView(vg);
            iv.setTag(R.id.color, color);
            mColorToView.put(color, iv);
            iv.setOnClickListener(mColorClickListener);
        }
    }

    private final View.OnClickListener mColorClickListener = (view) -> {
        onColorPicked((int)view.getTag(R.id.color));
    };

    private void onColorPicked(int color) {
        Animator curAnimator = animateColor(mColorToView.get(color), true);
        if(drawerView.getColor() == 0) {
            curAnimator.setDuration(COLOR_SCALE_DURATION);
            curAnimator.start();
        } else if(color != drawerView.getColor()) {
            Animator prevAnimator = animateColor(mColorToView.get(drawerView.getColor()), false);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(prevAnimator, curAnimator);
            animatorSet.setDuration(COLOR_SCALE_DURATION);
            animatorSet.start();
        }
        drawerView.setColor(color);
    }

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

    @OnClick({R.id.eraser, R.id.brush, R.id.pencil, R.id.clean, R.id.filter})
    void onToolClick(View view) {
        final int id = view.getId();
        if(id == R.id.clean) {
            drawerView.clean();
            return;
        }
        if(id == R.id.filter) {
            showFilterDialog();
            return;
        }
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

    private void showFilterDialog() {
        ListDialog listDialog = ListDialog.newInstance(getString(R.string.filter), null,
                Drawer.Filter.getFilterNames(), null, null, getString(R.string.cancel), true, FILTER_DIALOG_ID);
        listDialog.show(getChildFragmentManager(), "tag");
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
            case R.id.clean:
                icon = (!active ? R.drawable.ic_close_circle_grey600_24dp : R.drawable.ic_close_circle_white_24dp);
                break;
        }
        ((ImageView)viewById(id)).setImageResource(icon);
    }

    private View viewById(int id) {
        return getView().findViewById(id);
    }

    @OnClick(R.id.open)
    void onOpenClicked() {
        ListDialog dialog = ListDialog.newInstance(getString(R.string.open),
                null, getContext().getFilesDir().list(), null, null, getString(R.string.cancel),
                true, OPEN_FILE_DIALOG_ID);
        dialog.show(getChildFragmentManager(), "tag");
    }

    @OnClick(R.id.save)
    void onSaveClicked() {
        EditTextDialog editTextDialog = EditTextDialog.newInstance(getString(R.string.save), null,
                getString(R.string.filename), getString(R.string.ok), null, getString(R.string.cancel),
                true, SAVE_FILE_DIALOG_ID);

        editTextDialog.show(getChildFragmentManager(), "tag");

    }

    @Override
    public void onPositive(String value, int id) {
        if(id == SAVE_FILE_DIALOG_ID) {
            saveFileToInternalStorage(value);
        }
    }

    private void saveFileToInternalStorage(String filename) {
        if(!filename.endsWith(".png") && !filename.endsWith(".png")) {
            filename += ".png";
        }
        File file = new File(getContext().getFilesDir(), filename);
        try {
            FileOutputStream stream = new FileOutputStream(file);
            Bitmap bitmap = drawerView.getBitmap();
            bitmap.compress(Bitmap.CompressFormat.PNG, 75, stream);
            stream.flush();
            stream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onSelected(String value, int id) {
        if(id == OPEN_FILE_DIALOG_ID) {
            try {
                drawerView.setBitmap(BitmapFactory.decodeStream(
                        new FileInputStream(getContext().getFilesDir() + "/" + value)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if(id == FILTER_DIALOG_ID) {
            for(Drawer.Filter filter : Drawer.Filter.values()) {
                if(filter.getName().equals(value)) {
                    drawerView.filter(filter);
                    break;
                }
            }
        }
    }

    @Override
    public void onPositive(int id) {

    }

    @Override
    public void onNegative(int id) {

    }

    @Override
    public void onNeutral(int id) {

    }

    @Override
    public void onDismiss(int id) {

    }

    @Override
    public void onCancel(int id) {

    }
}
