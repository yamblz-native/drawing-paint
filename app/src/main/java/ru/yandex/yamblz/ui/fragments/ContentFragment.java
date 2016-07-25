package ru.yandex.yamblz.ui.fragments;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.xdty.preference.colorpicker.ColorPickerDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;
import ru.yandex.yamblz.ui.views.MyView;

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
        fab1.setIcon(R.drawable.ic_brush);
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
        });
        fam.addButton(fab2);

        FloatingActionButton fab3 = new FloatingActionButton(getActivity());
        fab3.setIcon(R.drawable.ic_save);
        fam.addButton(fab3);

        FloatingActionButton fab4 = new FloatingActionButton(getActivity());
        fab4.setIcon(R.drawable.ic_load);
        fam.addButton(fab4);
        return view;
    }
}
