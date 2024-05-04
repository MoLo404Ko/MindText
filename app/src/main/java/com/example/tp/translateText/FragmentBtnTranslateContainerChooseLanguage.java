package com.example.tp.translateText;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.tp.AddMessage;
import com.example.tp.Constants;
import com.example.tp.ControlVisibleEditTextField;
import com.example.tp.R;
import com.example.tp.SetHeightMessageContainer;

public class FragmentBtnTranslateContainerChooseLanguage extends Fragment {
    private SetHeightMessageContainer setHeightMessageContainer;
    private AddMessage addMessage;
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
        setToolBar();
        controlVisibleEditTextField.setVisibility(false);
    }

    /**
     * Set title and show back button
     */
    private void setToolBar() {
        TextView titlePage = mActivity.findViewById(R.id.titlePage);
        mActivity.findViewById(R.id.backArrowBtn).setVisibility(View.VISIBLE);

        titlePage.setText(getResources().getString(R.string.transTX));

        titlePage.setTextSize(16);
        titlePage.setTextColor(getResources().getColor(R.color.black, mActivity.getTheme()));
    }

    /**
     * Choose language buttons
     */
    private void onChooseLanguage(View view) {
        AppCompatButton russianLanguage = view.findViewById(R.id.russian_language);
        AppCompatButton englishLanguage = view.findViewById(R.id.english_language);

        String text2 = getResources().getString(R.string.chosen_language);
        Bundle bundle = new Bundle();

        russianLanguage.setOnClickListener(view1 -> view.post(() -> {
            String text1 = getResources().getString(R.string.animal_article);

            FragmentBtnTextForTranslateContainer fragment = new FragmentBtnTextForTranslateContainer(mActivity);
            bundle.putString(Constants.KEY_LANGUAGE, "1");
            fragment.setArguments(bundle);

            addMessage.setMessageToContainer(text1, fragment,
                    "fragmentBtnTextForTranslateContainer", true);
            addMessage.setMessageToContainer(text2, null, "",false);
        }));


        englishLanguage.setOnClickListener(view1 -> {
            String text1 = getResources().getString(R.string.english_language);

            FragmentBtnTextForTranslateContainer fragment = new FragmentBtnTextForTranslateContainer(mActivity);
            bundle.putString(Constants.KEY_LANGUAGE, "0");
            fragment.setArguments(bundle);

            addMessage.setMessageToContainer(text1, fragment,
                    "fragmentBtnTextForTranslateContainer", true);
            addMessage.setMessageToContainer(text2, null, "",false);
        });
    }
}
