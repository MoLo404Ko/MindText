package com.example.tp.findObjectsText;

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

import com.example.tp.R;
import com.example.tp.interfaces.AddMessage;
import com.example.tp.interfaces.ControlVisibleEditTextField;
import com.example.tp.interfaces.SetActionBar;
import com.example.tp.interfaces.SetHeightMessageContainer;

public class FragmentBtnObjects extends Fragment {
    private AddMessage addMessage;
    private SetHeightMessageContainer setHeightMessageContainer;
    private ControlVisibleEditTextField controlVisibleEditTextField;
    private SetActionBar setActionBar;
    private Activity mActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        addMessage = (AddMessage) context;
        setHeightMessageContainer = (SetHeightMessageContainer) context;
        controlVisibleEditTextField = (ControlVisibleEditTextField) context;
        setActionBar = (SetActionBar) context;
    }

    public FragmentBtnObjects(Activity mActivity) {
        this.mActivity = mActivity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_btns_find_objects, container, false);
        view.post(() -> setHeightMessageContainer.setHeightMessageContainer(view.getHeight()));

        init(view);

        return view;
    }

    private void init(View view) {
        setActionBar.setActionBar(getString(R.string.namesTX), true);
        controlVisibleEditTextField.setVisibility(false);

        onBtnClick(view);
    }

    /**
     * Handler of clicking on objects buttons
     * @param view - layout
     */
    private void onBtnClick(View view) {
        AppCompatButton companiesBtn = view.findViewById(R.id.company_btn);
        AppCompatButton namesBtn = view.findViewById(R.id.names_btn);
        AppCompatButton animalNamesBtn = view.findViewById(R.id.animal_names);
        AppCompatButton locationsBtn = view.findViewById(R.id.locationsBtn);


        companiesBtn.setOnClickListener(v -> {
            goToNextFragment(getString(R.string.company));
        });

        namesBtn.setOnClickListener(v -> {
            goToNextFragment(getString(R.string.names));
        });

        animalNamesBtn.setOnClickListener(v -> {
            goToNextFragment(getString(R.string.animal_names));
        });

        locationsBtn.setOnClickListener(v -> {
            goToNextFragment(getString(R.string.locations));
        });
    }

    /**
     * Go to next fragment and save object for request
     * @param object - find object
     */
    private void goToNextFragment(String object) {
        FragmentBtnFindObjectsText fragment = new FragmentBtnFindObjectsText(mActivity);

        Bundle args = new Bundle();
        args.putString("Object", object);
        fragment.setArguments(args);

        addMessage.addMessage(object, fragment, "fragmentBtnFindObjectsText", true);
        addMessage.addMessage(getString(R.string.text_for_find_msg), null, "", false);
    }
}
