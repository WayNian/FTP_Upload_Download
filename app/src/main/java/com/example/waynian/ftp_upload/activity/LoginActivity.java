package com.example.waynian.ftp_upload.activity;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.waynian.ftp_upload.R;
import com.example.waynian.ftp_upload.utils.AssetsCopyTOSDcard;
import com.example.waynian.ftp_upload.utils.MyIntent;

public class LoginActivity extends AppCompatActivity {


    private EditText mFtpUrl,mName,mPwd;
    private Button mFtp,mTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initUI();

//        String path= "aaa/cmd.txt";
//        AssetsCopyTOSDcard assetsCopyTOSDcard=new AssetsCopyTOSDcard(getApplicationContext());
//        assetsCopyTOSDcard.AssetToSD(path, Environment.getExternalStorageDirectory().toString()+"/"+path);

    }

    private void initUI() {

        //获取控件对象
        mFtp = (Button) findViewById(R.id.bt_Ftp);
        mTxt = (Button) findViewById(R.id.bt_txt);
        mFtpUrl = (EditText) findViewById(R.id.et_FtpUrl);
        mName  = (EditText) findViewById(R.id.et_Name);
        mPwd = (EditText) findViewById(R.id.et_Pwd);


        //设置控件对应相应函数
        mFtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                intent.putExtra("hostName",mFtpUrl.getText().toString());
                intent.putExtra("name",mName.getText().toString());
                intent.putExtra("pwd",mPwd.getText().toString());
                startActivity(intent);
            }
        });

        mTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent it = MyIntent.getTextFileIntent(TXTPATH,false);
//                startActivity(it);

                Intent intent = new Intent(LoginActivity.this,TXTActivity.class);
                intent.putExtra("hostName",mFtpUrl.getText().toString());
                intent.putExtra("name",mName.getText().toString());
                intent.putExtra("pwd",mPwd.getText().toString());
                startActivity(intent);
            }
        });

    }
}
