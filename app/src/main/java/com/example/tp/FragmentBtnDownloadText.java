package com.example.tp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.tp.interfaces.ControlVisibleEditTextField;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FragmentBtnDownloadText extends Fragment {
    private final Activity mActivity;
    private String prevFragment;
    private ControlVisibleEditTextField controlVisibleEditTextField;

    public FragmentBtnDownloadText(Activity mActivity, String prevFragment) {
        this.mActivity = mActivity;
        this.prevFragment = prevFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_btns_download_text, container, false);
        init(view);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        controlVisibleEditTextField = (ControlVisibleEditTextField) context;
    }

    private void init(View view) {
        onDownloadFileClick(view);
        controlVisibleEditTextField.setVisibility(false);
    }

    /**
     * Handler of clicking on download button
     * @param view - layout
     */
    private void onDownloadFileClick(View view) {
        AppCompatButton downloadBtn = view.findViewById(R.id.download_btn);

        downloadBtn.setOnClickListener(v -> {
            if (checkExternalStorage()) {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        Constants.APP_NAME);

                if (!file.exists())
                    file.mkdirs();

                new Thread(() -> {
                    String fileName = "";
                    FileOutputStream fos = null;
                    Log.d("MyLog", prevFragment);

                    switch (prevFragment) {
                        case "fragmentBtnTextForTranslateContainer":
                            fileName = Constants.TRANSLATE_TEXT_FILE;
                            break;
                        case "fragmentBtnKeyWords":
                            fileName = Constants.GENERATE_TEXT_FILE;
                            break;
                    }

                    File textFile = new File(file.getAbsolutePath() +  "/" + fileName);

                    try {
                        assert this.getArguments() != null;
                        String content = this.getArguments().getString(Constants.KEY_TEXT);

                        fos = new FileOutputStream(textFile);
                        fos.write(content.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (fos != null)
                                fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                Toast.makeText(mActivity, getString(R.string.file_downloaded), Toast.LENGTH_SHORT).show();
            }
            else Toast.makeText(mActivity, getString(R.string.external_storage_not_available),
                    Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Checking available of external storage
     * @return isAvailable
     */
    private boolean checkExternalStorage() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
