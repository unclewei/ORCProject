package com.example.unclewei.textproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.nanchen.compresshelper.CompressHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //训练数据路径，必须包含tesseract文件夹
    @SuppressLint("SdCardPath")
    static final String TESSBASE_PATH = "/storage/emulated/0/0/";
    static final String DEFAULT_LANGUAGE = "eng";
    //识别语言简体中文
    static final String CHINESE_LANGUAGE = "chi_sim";
    private android.widget.ImageView simplechinese;
    private android.widget.TextView simplechinesetext;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.simplechinesetext = (TextView) findViewById(R.id.simple_chinese_text);
        this.simplechinese = (ImageView) findViewById(R.id.simple_chinese);
        initImagePicker();
    }

    public void ocr(View view) {
        //简体中文识别
        SimpleChineseOCR();
    }

    public void SimpleChineseOCR() {
        //设置图片可以缓存
        simplechinese.setDrawingCacheEnabled(true);
        //获取缓存的bitmap
        final Bitmap bmp = BitmapFactory.decodeFile(path);

        final TessBaseAPI baseApi = new TessBaseAPI();
        //初始化OCR的训练数据路径与语言
        baseApi.init(TESSBASE_PATH, CHINESE_LANGUAGE);
//        baseApi.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "X0123456789"); // 识别白名单
//        baseApi.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "ABCDEFGHIJKLMNOPQRXTUVWabcdefghijklmnopqrxtuvwxyzYZ!@#$%^&*()_+=-[]}{;:'\"\\|~`,./<>?"); // 识别黑名单
        baseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO_OSD);//设置识别模式
        //设置识别模式
        baseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO_OSD);

        //设置要识别的图片
        baseApi.setImage(bmp);
        final CustomDialog customDialog = new CustomDialog(this, "loading");
        customDialog.show();
        new Thread() {
            @Override
            public void run() {
                super.run();
                final String text = baseApi.getUTF8Text();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        simplechinesetext.setText(text);
                        baseApi.clear();
                        baseApi.end();
                        customDialog.dismiss();
                    }
                });
            }
        }.start();
    }

    public void choose(View view) {

        //不允许裁剪
        ImagePicker.getInstance().setCrop(false);
        //设置是否多选
        ImagePicker.getInstance().setMultiMode(true);
        //选中数量限制
        ImagePicker.getInstance().setSelectLimit(1);
        //不显示拍照按钮
        ImagePicker.getInstance().setShowCamera(false);
        ImagePicker.getInstance().setOutPutX(800);//保存文件的宽度。单位像素
        ImagePicker.getInstance().setOutPutY(800);//保存文件的高度。单位像素
        Intent intent = new Intent(this, ImageGridActivity.class);
        startActivityForResult(intent, 100);
    }

    /**
     * 初始化ImagePicker
     */
    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        //设置图片加载器
        imagePicker.setImageLoader(new GlideImageLoader());
        //是否按矩形区域保存
        imagePicker.setSaveRectangle(true);
        //裁剪框的形状
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);
        //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusWidth(800);
        //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == 100) {
                List<ImageItem> imageItemList = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (imageItemList != null) {
                    path = imageItemList.get(0).path;
                    File oldfile = new File(path);
                    File newFile = new CompressHelper.Builder(this)
                            .setMaxWidth(720)  // 默认最大宽度为720
                            .setMaxHeight(960) // 默认最大高度为960
                            .setQuality(100)    // 默认压缩质量为80
                            .setCompressFormat(Bitmap.CompressFormat.JPEG) // 设置默认压缩为jpg格式
                            .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES).getAbsolutePath())
                            .build()
                            .compressToFile(oldfile);
                    path = newFile.getAbsolutePath();
                    Log.e("William:path :", path);
                    Glide.with(MainActivity.this).load(path).into(simplechinese);
                }
            }
        }
    }
}
