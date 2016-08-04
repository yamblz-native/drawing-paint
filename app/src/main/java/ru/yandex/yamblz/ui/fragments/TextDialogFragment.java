package ru.yandex.yamblz.ui.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ru.yandex.yamblz.R;

/**
 * Created by shmakova on 02.08.16.
 */

public class TextDialogFragment extends AppCompatDialogFragment {
    @BindView(R.id.edit_text)
    EditText editText;

    private Unbinder unbinder;

    public static TextDialogFragment newInstance() {
        return new TextDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text, container, false);
        unbinder = ButterKnife.bind(this, view);
        getDialog().setTitle(getResources().getString(R.string.edit_text));
        return view;
    }

    @OnClick(R.id.edit_text_btn)
    public void onEditTextButtonClick() {
        sendBackResult(editText.getText().toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public void sendBackResult(String text) {
        EditTextDialogListener listener = (EditTextDialogListener) getTargetFragment();
        listener.onEditText(text);
        dismiss();
    }


    public interface EditTextDialogListener {
        void onEditText(String text);
    }

}