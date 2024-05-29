package com.example.tp.generateText;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.tp.interfaces.AddMessage;
import com.example.tp.interfaces.ControlVisibleEditTextField;
import com.example.tp.R;
import com.example.tp.interfaces.SetHeightMessageContainer;

public class FragmentBtnLengthText extends Fragment {
    private SetHeightMessageContainer setHeightMessageContainer;
    private AddMessage addMessage;
    private ControlVisibleEditTextField controlVisibleEditTextField;
    private final Activity mActivity;

    public FragmentBtnLengthText(Activity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        addMessage = (AddMessage) context;
        controlVisibleEditTextField = (ControlVisibleEditTextField) context;
        setHeightMessageContainer = (SetHeightMessageContainer) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_btns_lenght_text, container, false);
        view.post(() -> setHeightMessageContainer.setHeightMessageContainer(view.getHeight()));

        init(view);

        return view;
    }

    private void init(View view) {
        onChooseLength(view);
        controlVisibleEditTextField.setVisibility(false);
    }

    /**
     * Handler of clicking to buttons of length
     * @param view - layout
     */
    private void onChooseLength(View view) {
        AppCompatButton lessTen = view.findViewById(R.id.lessTen);
        AppCompatButton lessTwentyFive = view.findViewById(R.id.lessTwentyFive);
        AppCompatButton lessFifty = view.findViewById(R.id.lessFifty);

        lessTwentyFive.setEnabled(false);
        lessTwentyFive.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_input_field_blocked, getActivity().getTheme()));

        lessFifty.setEnabled(false);
        lessFifty.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_input_field_blocked, getActivity().getTheme()));

        lessTen.setOnClickListener(v -> goToFragmentKeyWords(getResources().getString(R.string.less_10), 1));
        lessTwentyFive.setOnClickListener(v -> goToFragmentKeyWords(getResources().getString(R.string.less_20), 2));
        lessFifty.setOnClickListener(v -> goToFragmentKeyWords(getResources().getString(R.string.less_30), 3));
    }

    /**
     * Go to next fragment
     * @param message - message of generated text
     */
    private void goToFragmentKeyWords(String message, int length) {
        FragmentBtnKeyWords fragment = new FragmentBtnKeyWords(mActivity);

        Bundle bundle = new Bundle();
        assert this.getArguments() != null;
        bundle.putString("Article", this.getArguments().getString("Article"));
        bundle.putInt("Length", length);
        fragment.setArguments(bundle);

        addMessage.addMessage(message, fragment,
                "fragmentBtnKeyWords", true);
        addMessage.addMessage(getResources().getString(R.string.key_words_msg),
                null, "", false);
    }
}
