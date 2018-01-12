package com.example.unclewei.textproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by nnv on 2017/6/5.
 */

public class CustomDialog extends ProgressDialog {

    private TextView tvLoadingText;
    private String loadingText;
    private ImageView loading;
    private Context context;

    public CustomDialog(Context context, String loadingText) {
        super(context, R.style.CustomDialog);
        this.loadingText = loadingText;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_loading);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);
        tvLoadingText = findViewById(R.id.tv_loading);
        tvLoadingText.setText(loadingText);
    }

    public void setLoadingText(String loadingText) {
        tvLoadingText.setText(loadingText);
    }

}
