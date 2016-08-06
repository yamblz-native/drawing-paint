package ru.yandex.yamblz.ui.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.views.PaintboxView;

public class ContentFragment extends BaseFragment {

    @BindView(R.id.paintbox)
    PaintboxView paintboxView;
    @BindView(R.id.load)
    Button loadFile;
    @BindView(R.id.save)
    Button saveFile;

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

        loadDialogProperties = new DialogProperties();
        loadDialogProperties.selection_mode = DialogConfigs.SINGLE_MODE;
        loadDialogProperties.selection_type = DialogConfigs.FILE_SELECT;
        loadDialogProperties.extensions = null;

        saveDialogProperties = new DialogProperties();
        saveDialogProperties.selection_mode = DialogConfigs.SINGLE_MODE;
        saveDialogProperties.selection_type = DialogConfigs.DIR_SELECT;
        saveDialogProperties.extensions = null;
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
        }
        System.out.println("file created " + f.toString());
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
                paintboxView.loadBitmap(getBitmapFromFile(files[0]));
            });
            dialog.show();
        }
    };

    private Bitmap getBitmapFromFile(String path) {
        File image = new File(path);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);

        float scalePercent = (float) paintboxView.getWidth() / (float) bitmap.getWidth();
        bitmap = Bitmap.createScaledBitmap(bitmap, paintboxView.getWidth(), (int) (bitmap.getHeight() * scalePercent), true);
        return bitmap;
    }
}
