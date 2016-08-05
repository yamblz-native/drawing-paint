package ru.yandex.yamblz.ui.fragments.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.yamblz.R;

public class FileNameEnterFragment extends DialogFragment {

    @BindView(R.id.save_text_view)
    EditText editText;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        OnFileNameEnteredListener onFileNameEnteredListener =
                (OnFileNameEnteredListener) getParentFragment();

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        @SuppressLint("InflateParams")
        View view = layoutInflater.inflate(R.layout.dialog_file_name_enter, null);

        ButterKnife.bind(this, view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setPositiveButton(getString(R.string.button_save), (dialog, which) -> {
            if (onFileNameEnteredListener != null) {
                onFileNameEnteredListener.onFileNameEntered(editText.getText().toString());
            }
        });
        builder.setNegativeButton(getString(R.string.button_cancel), null);

        return builder.create();
    }

    @SuppressWarnings("WeakerAccess")
    public interface OnFileNameEnteredListener {
        void onFileNameEntered(String file);
    }
}
