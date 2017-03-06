package ru.shmakova.painter.draw.text;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import ru.shmakova.painter.R;
import ru.shmakova.painter.app.App;
import ru.shmakova.painter.screen.BaseDialogFragment;
import rx.Observable;
import rx.subjects.PublishSubject;

public class TextDialogFragment extends BaseDialogFragment implements TextDialogView {
    @NonNull
    private final PublishSubject<String> submitClicks = PublishSubject.create();

    @BindView(R.id.edit_text)
    EditText editText;

    @Inject
    TextDialogPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_text, container, false);
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies();
    }

    private void injectDependencies() {
        App.get(getContext()).applicationComponent().inject(this);
    }

    @OnClick(R.id.edit_text_btn)
    public void onEditTextButtonClick() {
        submitClicks.onNext(editText.getText().toString());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.bindView(this);
    }

    @Override
    public void onDestroyView() {
        presenter.unbindView(this);
        super.onDestroyView();
    }

    @Override
    public Observable<String> submitClicks() {
        return submitClicks;
    }

    @Override
    public void sendBackResult(String text) {
        EditTextDialogListener listener = (EditTextDialogListener) getTargetFragment();
        listener.onEditText(text);
    }

    @Override
    public void dismissDialog() {
        dismiss();
    }

    public interface EditTextDialogListener {
        void onEditText(String text);
    }
}
