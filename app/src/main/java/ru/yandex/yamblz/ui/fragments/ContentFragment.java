package ru.yandex.yamblz.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.TypedArray;
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
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.yandex.yamblz.App;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.drawing.Drawer;
import ru.yandex.yamblz.ui.drawing.DrawerView;
import ru.yandex.yamblz.ui.fragments.dialogs.EditTextDialog;
import ru.yandex.yamblz.ui.fragments.dialogs.ListDialog;
import ru.yandex.yamblz.ui.other.ImageCache;

public class ContentFragment extends BaseFragment implements EditTextDialog.Callbacks, ListDialog.Callbacks {

    private static final long COLOR_SCALE_DURATION = 300;

    private static final int SAVE_FILE_DIALOG_ID = 1;

    private static final int OPEN_FILE_DIALOG_ID = 2;

    private static final int FILTER_DIALOG_ID = 3;

    private static final int STAMP_DIALOG_ID = 4;

    private static final String BITMAP_EXTRA = "bitmap";

    private static final String STAMP_EXTRA = "stamp";

    @BindView(R.id.drawer)
    DrawerView drawerView;

    @BindView(R.id.size)
    SeekBar sizeSeekBar;

    @BindView(R.id.palette)
    FlexboxLayout palette;

    @Inject
    ImageCache mImageCache;

    private BottomSheetBehavior mBottomSheetBehavior;

    private Drawer.Tool mSelectedTool = Drawer.Tool.NO;

    private Map<Integer, Drawer.Tool> mId2tool = new HashMap<>();
    private Map<Drawer.Tool, Integer> mTool2id = new HashMap<>();
    private Map<Integer, View> mColorToView = new HashMap<>();

    private int[] mColors;
    private int[] mStamps;
    private String[] mStampsNames;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((App)getActivity().getApplicationContext()).applicationComponent().inject(this);

        initArrays();
        initMaps();
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);

        ButterKnife.bind(this, view);

        mBottomSheetBehavior = BottomSheetBehavior.from(view.findViewById(R.id.paint_toolbar));
        sizeSeekBar.setOnSeekBarChangeListener(mOnSizeSeekBarChangeListener);

        initPalette();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mImageCache.put(BITMAP_EXTRA, drawerView.getBitmap());
        mImageCache.put(STAMP_EXTRA, drawerView.getStamp());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState == null) {
            //if first start then set default config for drawer
            onToolSelected(Drawer.Tool.BRUSH);
            onColorSelected(mColors[0]);
            onSizeSelected(20f);
        } else {
            //retrieve cached bitmap to continue drawing
            Bitmap bitmap = mImageCache.remove(BITMAP_EXTRA);
            Bitmap stamp = mImageCache.remove(STAMP_EXTRA);
            if(bitmap != null) {
                drawerView.setBitmap(bitmap);
            }
            //if stamp was the last tool the retrieve the stamp bitmap from cache
            if(drawerView.getTool() == Drawer.Tool.STAMP && stamp != null) {
                drawerView.selectStamp(stamp);
            }
            onToolSelected(drawerView.getTool());
            onColorSelected(drawerView.getColor());
            onSizeSelected(drawerView.getSize());

        }
    }

    /**
     * Retrieves predefined arrays from resources
     */
    private void initArrays() {
        mColors = getResources().getIntArray(R.array.palette);
        TypedArray stamps = getResources().obtainTypedArray(R.array.stamps);
        mStamps = new int[stamps.length()];
        for(int i = 0; i < mStamps.length; i++) {
            mStamps[i] = stamps.getResourceId(i, 0);
        }
        stamps.recycle();
        mStampsNames = getResources().getStringArray(R.array.stamps_names);
    }

    /**
     * Inits maps from ids 2 tools, and vice versa
     */
    private void initMaps() {
        mTool2id.put(Drawer.Tool.BRUSH, R.id.brush);
        mTool2id.put(Drawer.Tool.ERASER, R.id.eraser);
        mTool2id.put(Drawer.Tool.STAMP, R.id.stamp);

        for (Map.Entry<Drawer.Tool, Integer> entry : mTool2id.entrySet()) {
            mId2tool.put(entry.getValue(), entry.getKey());
        }
    }

    /**
     * Adds palette to ui
     */
    private void initPalette() {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        for (int color : mColors) {
            ViewGroup vg = (ViewGroup) layoutInflater.inflate(R.layout.color, palette, false);
            ImageView iv = (ImageView) vg.findViewById(R.id.color);
            iv.setImageResource(R.drawable.color);
            ((GradientDrawable) iv.getDrawable()).setColor(color);
            palette.addView(vg);
            iv.setTag(R.id.color, color);
            mColorToView.put(color, iv);
            iv.setOnClickListener(mColorClickListener);
        }
    }

    private final View.OnClickListener mColorClickListener = (view) ->
            onColorSelected((int) view.getTag(R.id.color));

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

    @OnClick({R.id.eraser, R.id.brush, R.id.clean, R.id.filter, R.id.stamp})
    void onToolClick(View view) {
        final int id = view.getId();
        if (id == R.id.clean) {
            drawerView.clean();
            return;
        }
        if (id == R.id.filter) {
            showFilterDialog();
            return;
        }

        //show dialog only if first time clicked
        if(id == R.id.stamp && mSelectedTool != Drawer.Tool.STAMP) {
            showStampsDialog();
            return;
        }

        onToolSelected(mId2tool.get(id));

    }

    @OnClick(R.id.paint_toolbar)
    void onToolbarClick() {
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void onSizeSelected(float size) {
        drawerView.setSize(size);
        sizeSeekBar.setProgress((int) drawerView.getSize());
    }

    private void onToolSelected(Drawer.Tool tool) {
        if (mSelectedTool == tool) {
            //if the tool was twice clicked then turn it off and update ui
            drawerView.disable();
            setToolIcon(tool, false);
            mSelectedTool = Drawer.Tool.NO;
        } else {
            setToolIcon(mSelectedTool, false);
            setToolIcon(tool, true);
            mSelectedTool = tool;
        }
        drawerView.selectTool(mSelectedTool);
    }

    private void disableTool() {
        disableTool(mSelectedTool);
    }

    private void disableTool(Drawer.Tool tool) {
        setToolIcon(tool, false);
        mSelectedTool = Drawer.Tool.NO;
        drawerView.disable();
    }

    /**
     * Animates color selection
     * @param color the selected color
     */
    private void onColorSelected(int color) {
        Animator curAnimator = createColorAnimation(mColorToView.get(color), true);
        if (drawerView.getColor() == color) {
            curAnimator.setDuration(COLOR_SCALE_DURATION);
            curAnimator.start();
        } else {
            Animator prevAnimator = createColorAnimation(mColorToView.get(drawerView.getColor()), false);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(prevAnimator, curAnimator);
            animatorSet.setDuration(COLOR_SCALE_DURATION);
            animatorSet.start();
        }
        drawerView.setColor(color);
    }

    /**
     * Returns color scale animation
     * @param view the view to animate
     * @param scale {@code true} if increase scale
     * @return the animator
     */
    private Animator createColorAnimation(View view, boolean scale) {
        float from, to;
        from = (scale ? 1.0f : 1.3f);
        to = (scale ? 1.3f : 1.0f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", from, to),
                ObjectAnimator.ofFloat(view, "scaleY", from, to));
        return set;
    }

    private void showFilterDialog() {
        ListDialog listDialog = ListDialog.newInstance(getString(R.string.filter), null,
                Drawer.Filter.getFilterNames(), null, null, getString(R.string.cancel), true, FILTER_DIALOG_ID);
        listDialog.show(getChildFragmentManager(), null);
    }

    private void showStampsDialog() {
        ListDialog listDialog = ListDialog.newInstance(getString(R.string.stamps), null, mStampsNames,
                null, null, getString(R.string.cancel), false, STAMP_DIALOG_ID);
        listDialog.show(getChildFragmentManager(), null);
    }

    /**
     * Sets tool appropriate icon
     * @param tool the tool
     * @param active {@code true} if tool is active
     */
    private void setToolIcon(Drawer.Tool tool, boolean active) {
        if (tool == Drawer.Tool.NO) {
            return;
        }
        int icon = -1;
        switch (tool) {
            case BRUSH:
                icon = (!active ? R.drawable.ic_brush_grey600_24dp : R.drawable.ic_brush_white_24dp);
                break;
            case ERASER:
                icon = (!active ? R.drawable.ic_eraser_grey600_24dp : R.drawable.ic_eraser_white_24dp);
                break;
            case STAMP:
                icon = (!active ? R.drawable.ic_android_grey600_24dp : R.drawable.ic_android_white_24dp);
                break;
        }
        ((ImageView) findToolView(tool)).setImageResource(icon);
    }

    private View findToolView(Drawer.Tool tool) {
        return getView().findViewById(mTool2id.get(tool));
    }

    @OnClick(R.id.open)
    void onOpenClicked() {
        ListDialog dialog = ListDialog.newInstance(getString(R.string.open),
                null, getContext().getFilesDir().list(), null, null, getString(R.string.cancel),
                true, OPEN_FILE_DIALOG_ID);
        dialog.show(getChildFragmentManager(), null);
    }

    @OnClick(R.id.save)
    void onSaveClicked() {
        EditTextDialog editTextDialog = EditTextDialog.newInstance(getString(R.string.save), null,
                getString(R.string.filename), getString(R.string.ok), null, getString(R.string.cancel),
                true, SAVE_FILE_DIALOG_ID);

        editTextDialog.show(getChildFragmentManager(), null);

    }

    @Override
    public void onPositive(String value, int id) {
        if (id == SAVE_FILE_DIALOG_ID) {
            saveImageToInternalStorage(value);
        }
    }

    /**
     * Saves image to internal storage, appending .png suffix
     * @param filename file name to save in
     */
    private void saveImageToInternalStorage(String filename) {
        if (!filename.endsWith(".png") && !filename.endsWith(".png")) {
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
        if (id == OPEN_FILE_DIALOG_ID) {
            try {
                drawerView.setBitmap(BitmapFactory.decodeStream(
                        new FileInputStream(getContext().getFilesDir() + "/" + value)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (id == FILTER_DIALOG_ID) {
            for (Drawer.Filter filter : Drawer.Filter.values()) {
                if (filter.getName().equals(value)) {
                    drawerView.filter(filter);
                    break;
                }
            }
        } else if(id == STAMP_DIALOG_ID) {
            int drawableId = getDrawableIdForStampName(value);
            Bitmap stamp = BitmapFactory.decodeResource(getResources(), drawableId);
            onToolSelected(Drawer.Tool.STAMP);
            drawerView.selectStamp(stamp);
        }
    }

    private int getDrawableIdForStampName(String name) {
        for(int i = 0; i < mStampsNames.length; i++) {
            if(mStampsNames[i].equals(name)) {
                return mStamps[i];
            }
        }
        return 0;
    }

    @Override
    public void onPositive(int id) {

    }

    @Override
    public void onNegative(int id) {

    }

    @Override
    public void onNeutral(int id) {
        if(id == STAMP_DIALOG_ID) {
            disableTool();
        }
    }

    @Override
    public void onDismiss(int id) {

    }

    @Override
    public void onCancel(int id) {

    }
}
