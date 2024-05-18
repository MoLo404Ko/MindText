package com.example.tp.tonText;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.tp.R;
import com.example.tp.interfaces.ControlVisibleEditTextField;
import com.example.tp.interfaces.SetHeightMessageContainer;

public class FragmentBtnDoneText extends Fragment {
    private ControlVisibleEditTextField controlVisibleEditTextField;
    private SetHeightMessageContainer setHeightMessageContainer;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        controlVisibleEditTextField = (ControlVisibleEditTextField) context;
        setHeightMessageContainer = (SetHeightMessageContainer) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_btns_done, container, false);
        view.post(() -> setHeightMessageContainer.setHeightMessageContainer(view.getHeight()));
        init();

        return view;
    }

    private void init() {
        controlVisibleEditTextField.setVisibility(false);
    }
}
