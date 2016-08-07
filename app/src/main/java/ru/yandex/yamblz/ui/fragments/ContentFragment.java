package ru.yandex.yamblz.ui.fragments;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.pavelsikun.vintagechroma.ChromaDialog;
import com.pavelsikun.vintagechroma.IndicatorMode;
import com.pavelsikun.vintagechroma.colormode.ColorMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.other.ImageInterface;
import ru.yandex.yamblz.ui.views.PaintboxView;

public class ContentFragment extends BaseFragment {

    public static ContentFragment newInstance(Fragment targetFragmnet) {
        ContentFragment fragment = new ContentFragment();
        fragment.setTargetFragment(targetFragmnet, 1);
        return fragment;
    }

    @BindView(R.id.paintbox)
    PaintboxView paintboxView;
    @BindView(R.id.load)
    Button loadFile;
    @BindView(R.id.save)
    Button saveFile;
    @BindView(R.id.color_pick)
    Button pickColor;

    DialogProperties loadDialogProperties;
    DialogProperties saveDialogProperties;

    String lastSelectedDir = DialogConfigs.DEFAULT_DIR;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        loadFile.setOnClickListener(onLoadClickListener);
        saveFile.setOnClickListener(onSaveClickListener);
        pickColor.setOnClickListener(onColorPickClickListener);

        loadDialogProperties = new DialogProperties();
        loadDialogProperties.selection_mode = DialogConfigs.SINGLE_MODE;
        loadDialogProperties.selection_type = DialogConfigs.FILE_SELECT;
        loadDialogProperties.extensions = null;

        saveDialogProperties = new DialogProperties();
        saveDialogProperties.selection_mode = DialogConfigs.SINGLE_MODE;
        saveDialogProperties.selection_type = DialogConfigs.DIR_SELECT;
        saveDialogProperties.extensions = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getTargetFragment() instanceof ImageInterface){
            ImageInterface retain = (ImageInterface) getTargetFragment();
            paintboxView.loadBitmap(retain.getImage());
        }
    }

    private Bitmap scaleBackground(Bitmap bitmap, boolean isPortrait) {

        if ( bitmap == null )
            return null;

        if (isPortrait) {
            float scalePercent = (float) paintboxView.getWidth() / (float) bitmap.getWidth();
            return Bitmap.createScaledBitmap(bitmap, paintboxView.getWidth(), (int) (bitmap.getHeight() * scalePercent), true);
        } else {
            float scalePercent = (float) paintboxView.getHeight() / (float) bitmap.getHeight();
            return Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * scalePercent), paintboxView.getHeight(), true);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (getTargetFragment() instanceof ImageInterface){
            ImageInterface retain = (ImageInterface) getTargetFragment();
            retain.setImage(paintboxView.getBitmap());
        }
        super.onSaveInstanceState(outState);
    }

    View.OnClickListener onSaveClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            saveDialogProperties.root = new File(lastSelectedDir);
            FilePickerDialog dialog = new FilePickerDialog(getActivity(), saveDialogProperties);
            dialog.setDialogSelectionListener(files -> {

                if ( files.length == 0 )
                    return;

                lastSelectedDir = files[0];
                saveBitmapToFile(lastSelectedDir, paintboxView.getBitmap());
            });
            dialog.show();
        }
    };

    private void saveBitmapToFile(String path, Bitmap image) {
        File f = new File(path ,"yaImage.png");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Couldn't create file", Toast.LENGTH_SHORT).show();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Couldn't save file", Toast.LENGTH_SHORT).show();
        }
        image.compress(Bitmap.CompressFormat.PNG, 90, out);
    }

    View.OnClickListener onLoadClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            loadDialogProperties.root = new File(lastSelectedDir);
            FilePickerDialog dialog = new FilePickerDialog(getActivity(), loadDialogProperties);
            dialog.setDialogSelectionListener(files -> {

                if ( files.length == 0 )
                    return;

                lastSelectedDir = files[0].substring(0, files[0].lastIndexOf("/"));
                paintboxView.loadBitmap(
                        scaleBackground(getBitmapFromFile(files[0]), getOrientation()));
            });
            dialog.show();
        }
    };

    View.OnClickListener onColorPickClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new ChromaDialog.Builder()
                    .initialColor(Color.BLACK)
                    .colorMode(ColorMode.ARGB)
                    .indicatorMode(IndicatorMode.HEX)
                    .onColorSelected(color -> {paintboxView.setColor(color);})
                    .create()
                    .show(getChildFragmentManager(), "ChromaDialog");
        }
    };

    private boolean getOrientation() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    private Bitmap getBitmapFromFile(String path) {
        File image = new File(path);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();

        return BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
    }
}
