package com.example.tp;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class FragmentMessageContainer extends Fragment {
    private View view = null;
    private LinearLayoutCompat mainMessageLayout = null;
    private ConstraintLayout rootMessageLayout = null;
    private List<String> messages = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_message_container, container, false);

        mainMessageLayout = view.findViewById(R.id.main_message_layout);
        rootMessageLayout = view.findViewById(R.id.root_message_layout);
        setHeightMessageContainer(getArguments().getInt("height"));

        addMessage(getResources().getString(R.string.start_msg), false);


        return view;
    }

    public void addMessage(String text_msg, boolean user) {
        if (mainMessageLayout.getChildCount() == 20) {
            mainMessageLayout.removeAllViews();
            messages.clear();
        }


        Context context = view.getContext();

        // Параметры сообщения
        TextView message = new TextView(view.getContext());
        message.setMaxWidth(Resources.getSystem().getDisplayMetrics().heightPixels / 3);
        message.setText(text_msg);
        message.setTextSize(13);
        message.setTextColor(getResources().getColor(R.color.black, context.getTheme()));
        message.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.shape_msg, context.getTheme()));
        message.setGravity(Gravity.CENTER_VERTICAL);
        message.setPadding(20, 20, 20, 20);

        Typeface type = Typeface.createFromAsset(context.getAssets(), "fonts/SFProText-Regular.ttf");
        message.setTypeface(type);


        // Параметры иконки
        ImageView icon = new ImageView(context);
        icon.setBackground(user ? ResourcesCompat.getDrawable(getResources(), R.drawable.ic_avatar_user, context.getTheme()) :
                ResourcesCompat.getDrawable(getResources(), R.drawable.ic_avatar_ai, context.getTheme()));


        // Параметры разделителя
        View divider = new View(context);
        divider.setLayoutParams(new ViewGroup.LayoutParams(15,15));
        divider.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.white, context.getTheme()));


        // Параметры всего блока (иконка + сообщение)
        LinearLayoutCompat messageLayout = new LinearLayoutCompat(context);
        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 0, 20, 40);
        messageLayout.setLayoutParams(params);
        messageLayout.setGravity(user ? Gravity.RIGHT : Gravity.LEFT);


        // Параметры расположения блока
        if (user) {
            messageLayout.addView(message);
            messageLayout.addView(divider);
            messageLayout.addView(icon);
            messageLayout.setGravity(Gravity.RIGHT);
        }
        else {
            messageLayout.addView(icon);
            messageLayout.addView(divider);
            messageLayout.addView(message);
        }

        messages.add(text_msg);
        mainMessageLayout.addView(messageLayout);
    }

    /**
     * Устанавливаем размер контейнера сообщений
     * @param height
     */
    public void setHeightMessageContainer(int height) {
        ScrollView scrollView = view.findViewById(R.id.scroll_view_message);
        scrollView.setMinimumHeight(height);
        rootMessageLayout.getLayoutParams().height = height;
    }
}
