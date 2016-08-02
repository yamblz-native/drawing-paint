package ru.yandex.yamblz.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.thebluealliance.spectrum.SpectrumDialog;

import butterknife.BindView;
import butterknife.OnClick;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.android_views.CanvasView;
import timber.log.Timber;

public class ContentFragment extends BaseFragment implements FilterPickerDialogFragment.FilterPickerDialogListener {
    private static final int FILTER_PICKER_REQUEST_CODE = 100;

    @BindView(R.id.canvas)
    CanvasView canvasView;
    @BindView(R.id.color)
    ImageView colorIcon;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_content, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_btn:
                Timber.d("SAVE");
                break;
            case R.id.upload_btn:
                Timber.d("UPLOAD");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.color_pick_btn)
    public void onColorPickClick() {
        showColorPickerDialog();
    }

    private void showColorPickerDialog() {
        new SpectrumDialog.Builder(getContext())
                .setColors(R.array.colors)
                .setTitle(getResources().getString(R.string.color_pick))
                .setSelectedColorRes(R.color.md_black)
                .setDismissOnColorSelected(false)
                .setOutlineWidth(2)
                .setOnColorSelectedListener((positiveResult, color) -> {
                    if (positiveResult) {
                        canvasView.setColor(color);
                        changeIconColor(color);
                    } else {

                    }
                }).build().show(getFragmentManager(), "color_picker_dialog");

    }

    private void changeIconColor(int color) {
        DrawableCompat.setTint(colorIcon.getDrawable(), color);
    }

    private void showFilterPicker() {
        FragmentManager fm = getFragmentManager();
        FilterPickerDialogFragment filterPickerDialogFragment = FilterPickerDialogFragment.newInstance();
        filterPickerDialogFragment.setTargetFragment(ContentFragment.this, FILTER_PICKER_REQUEST_CODE);
        filterPickerDialogFragment.show(fm, "fragment_filter_picker");
    }

    @Override
    public void onFilterPick(String filter) {
        Toast.makeText(getContext(), "Hi, " + filter, Toast.LENGTH_SHORT).show();
    }
}
