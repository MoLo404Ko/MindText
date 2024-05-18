package com.example.tp.translateText;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.tp.interfaces.AddMessage;
import com.example.tp.Constants;
import com.example.tp.interfaces.ControlVisibleEditTextField;
import com.example.tp.R;
import com.example.tp.interfaces.SetActionBar;
import com.example.tp.interfaces.SetHeightMessageContainer;

public class FragmentBtnTranslateContainerChooseLanguage extends Fragment {
    private SetHeightMessageContainer setHeightMessageContainer;
    private AddMessage addMessage;
    private SetActionBar setActionBar;
    private ControlVisibleEditTextField controlVisibleEditTextField;
    private final Activity mActivity;


    public FragmentBtnTranslateContainerChooseLanguage(Activity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        addMessage = (AddMessage) context;
        setHeightMessageContainer = (SetHeightMessageContainer) context;
        controlVisibleEditTextField = (ControlVisibleEditTextField) context;
        setActionBar = (SetActionBar)context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_btns_translate_container_choose_language, container, false);
        view.post(() -> setHeightMessageContainer.setHeightMessageContainer(view.getHeight()));

        init(view);
        return view;
    }

    private void init(View view) {
        onChooseLanguage(view);
        setActionBar.setActionBar(getString(R.string.transTX), true);
        controlVisibleEditTextField.setVisibility(false);
    }

    /**
     * Choouse language
     * @param view - layout
     */
    private void onChooseLanguage(View view) {
        AppCompatButton russianLanguage = view.findViewById(R.id.russian_language);
        AppCompatButton englishLanguage = view.findViewById(R.id.english_language);

        russianLanguage.setOnClickListener(view1 -> view.post(() -> {
            String language = getResources().getString(R.string.russian_language);
            createFragment(language, "1");
        }));

        englishLanguage.setOnClickListener(view1 -> {
            String language = getResources().getString(R.string.english_language);
            createFragment(language, "0");
        });
    }

    /**
     * Create fragment after choosing language
     * @param language - language into which it translates
     * @param languageKey - code language
     */
    private void createFragment(String language, String languageKey) {
        FragmentBtnTextForTranslateContainer fragment = new FragmentBtnTextForTranslateContainer(mActivity);
        Bundle bundle = new Bundle();

        bundle.putString(Constants.KEY_LANGUAGE, languageKey);
        fragment.setArguments(bundle);

        addMessage.addMessage(language, fragment,
                "fragmentBtnTextForTranslateContainer", true);
        addMessage.addMessage(getString(R.string.chosen_language), null, "",false);
    }
}
