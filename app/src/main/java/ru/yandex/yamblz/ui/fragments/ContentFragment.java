package ru.yandex.yamblz.ui.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.shchurov.horizontalwheelview.HorizontalWheelView;
import com.rarepebble.colorpicker.ColorPickerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.views.CanvasView;
import ru.yandex.yamblz.ui.views.PaintView;

public class ContentFragment extends BaseFragment {
    private static final String TAG = "ContentFragment";
    private ColorPickerView mColorPickerView;
    private AlertDialog mColorChooserDialog;

    @BindView(R.id.fragment_content_wheel_view)
    HorizontalWheelView mWheelView;

    @BindView(R.id.fragment_content_canvas_view)
    CanvasView mCanvasView;

    @BindView(R.id.fragment_content_paint_view)
    PaintView mPaintView;
    private RetainFragment mFragment;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mWheelView.setOnlyPositiveValues(true);
        mWheelView.setListener(new HorizontalWheelView.Listener() {
            @Override
            public void onRotationChanged(double radians) {
                setPaintSize((float) radians * 10 + 20);
            }
        });

        init();

        mColorChooserDialog = new AlertDialog.Builder(getContext())
                .setView(mColorPickerView)
                .setTitle(getString(R.string.color_chooser_title))
                .setPositiveButton(getString(R.string.color_chooser_positive_button), (dialog, which) -> setPaintColor(mColorPickerView.getColor()))
                .setNegativeButton(getString(R.string.color_chooser_negative_button), (dialog, which) -> {
                })
                .create();

        mPaintView.setOnClickListener(v -> {
            mColorPickerView.setColor(getPaintColor());
            mColorChooserDialog.show();
        });

        FragmentManager fm = getFragmentManager();
        mFragment = (RetainFragment) fm.findFragmentByTag("data_fragment");

        if (mFragment == null) {
            mFragment = new RetainFragment();
            fm.beginTransaction().add(mFragment, "data_fragment").commit();
        } else {
            mCanvasView.setPaths(mFragment.getPaths());
            mCanvasView.setBitmap(mFragment.getBitmap());
        }
    }

    private void init() {
        mColorPickerView = new ColorPickerView(getContext());
        mColorPickerView.showHex(false);

        setPaintColor(Color.CYAN);
        setPaintSize(20f);
    }

    private void setPaintSize(float size) {
        mCanvasView.setPaintSize(size);
        mPaintView.setSize(size);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_load_bitmap:
                File file = getOutputMediaFile();
                if (file != null) {
                    mCanvasView.setBitmap(getImageFromDisk(file.toString()));
                }
                return true;
            case R.id.action_save_bitmap:
                storeImage(mCanvasView.getBitmap());
                return true;
            case R.id.action_null_bitmap:
                mCanvasView.setBitmap(null);
                return true;
            default:
                return false;
        }
    }

    private void setPaintColor(int color) {
        mPaintView.setColor(color);
        mCanvasView.setPaintColor(color);
    }

    private int getPaintColor() {
        return mPaintView.getColor();
    }

    @OnClick(R.id.fragment_content_cancel_image)
    void removeLastPath() {
        mCanvasView.removeLastPath();
    }

    @Override
    public void onDestroyView() {
        if (mFragment != null) {
            mFragment.setPaths(mCanvasView.getPaths());
            mFragment.setBitmap(mCanvasView.getBitmap());
        }

        super.onDestroyView();
    }

    private Bitmap getImageFromDisk(String file) {
        return BitmapFactory.decodeFile(file);
    }

    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d(TAG, "Error creating media file, check storage permissions: ");
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }

    private File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getContext().getApplicationContext().getPackageName()
                + "/Files");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Can't create directory");
                return null;
            }
        }

        String fileName = "image.png";
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }
}