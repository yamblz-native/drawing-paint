package ru.yandex.yamblz.ui.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.yandex.yamblz.R;

/**
 * Created by shmakova on 02.08.16.
 */

public class FilterPickerDialogFragment extends AppCompatDialogFragment {

    public static FilterPickerDialogFragment newInstance() {
        return new FilterPickerDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter_picker, container, false);
        getDialog().setTitle(getResources().getString(R.string.filter_pick));
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void sendBackResult() {
        FilterPickerDialogListener listener = (FilterPickerDialogListener) getTargetFragment();
        listener.onFilterPick("MU");
        dismiss();
    }


    public interface FilterPickerDialogListener {
        void onFilterPick(String filter);
    }

}