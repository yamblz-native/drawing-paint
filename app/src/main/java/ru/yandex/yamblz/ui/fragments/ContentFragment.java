package ru.yandex.yamblz.ui.fragments;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.xdty.preference.colorpicker.ColorPickerDialog;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.views.MyView;

import static android.app.Activity.RESULT_OK;

public class ContentFragment extends BaseFragment {

    @BindView(R.id.fam)
    FloatingActionsMenu fam;
    @BindView(R.id.my_view)
    MyView myView;

    private int curColor;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        ButterKnife.bind(this, view);

        FloatingActionButton fab1 = new FloatingActionButton(getActivity());
        fab1.setIcon(R.drawable.ic_clear);
        fab1.setOnClickListener(v -> {
            AlertDialog.Builder newDialog = new AlertDialog.Builder(getActivity());
            newDialog.setTitle("Clear the drawing");
            newDialog.setMessage("Are you sure you want to clear? You will lose the current drawing.");
            newDialog.setPositiveButton("Yes", (dialog, which) -> {
                myView.startNew();
                dialog.dismiss();
            });
            newDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            newDialog.show();
            fam.collapse();
        });
        fam.addButton(fab1);

        FloatingActionButton fab2 = new FloatingActionButton(getActivity());
        fab2.setIcon(R.drawable.ic_pallette);
        fab2.setOnClickListener(v -> {
            int[] mColors = {Color.parseColor("#c52424"), Color.parseColor("#f4d26c"),
                    Color.parseColor("#aeea00"), Color.parseColor("#1de9b6"),
                    Color.parseColor("#1e88e5"), Color.parseColor("#5e35b1"), Color.parseColor("#e040fb"),
                    Color.parseColor("#e91e63"), Color.parseColor("#f57f17"),
                    Color.CYAN, Color.RED, Color.GREEN, Color.YELLOW};

            ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
                    mColors,
                    Color.YELLOW,
                    5, // Number of columns
                    ColorPickerDialog.SIZE_SMALL);

            dialog.setOnColorSelectedListener((color) -> {
                curColor = color;
                myView.setColor(curColor);
            });
            dialog.show(((Activity) v.getContext()).getFragmentManager(), "color_dialog_test");
            myView.setFilter(0);
            fam.collapse();
        });
        fam.addButton(fab2);

        FloatingActionButton fab3 = new FloatingActionButton(getActivity());
        fab3.setIcon(R.drawable.ic_save);
        fab3.setOnClickListener(v -> {
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(getActivity());
            saveDialog.setTitle("Save drawing");
            saveDialog.setMessage("Save drawing to the Gallery?");
            saveDialog.setPositiveButton("Yes", (dialog, which) -> {
//                File directory = new File(Environment.getExternalStorageDirectory() + File.separator + "images");
//                directory.mkdirs();
                myView.setDrawingCacheEnabled(true);
                String imgSaved = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(),
                        myView.getDrawingCache(), "mydrawing.png", "drawing");
                if (imgSaved != null) {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                            "Successfully saved to Gallery!", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                            "Failed to save! :(", Toast.LENGTH_SHORT);
                    toast.show();
                }
                myView.destroyDrawingCache();
            });
            saveDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
            saveDialog.show();
            fam.collapse();
        });
        fam.addButton(fab3);

        FloatingActionButton fab4 = new FloatingActionButton(getActivity());
        fab4.setIcon(R.drawable.ic_load);
        fab4.setOnClickListener(v -> {
            openImageChooser();
        });
        fam.addButton(fab4);

        FloatingActionButton fab5 = new FloatingActionButton(getActivity());
        fab5.setIcon(R.drawable.ic_filter);
        fab5.setOnClickListener(v -> {
            myView.setFilter(1);
            fam.collapse();
        });
        fam.addButton(fab5);

        return view;
    }


    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                myView.setImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
