package com.example.waynian.ftp_upload.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.waynian.ftp_upload.R;

public class ImageViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        //获取Intent
        Intent receiveIntent=getIntent();
        //获取图片路径
        String picPath=receiveIntent.getStringExtra("picPath");
        ImageView iv=(ImageView)findViewById(R.id.imageView);
        //使用BitmapFactory在ImageView中显示图片
        iv.setImageBitmap(BitmapFactory.decodeFile(picPath));

    }
}
