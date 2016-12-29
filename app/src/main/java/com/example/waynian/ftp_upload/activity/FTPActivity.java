package com.example.waynian.ftp_upload.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.waynian.ftp_upload.R;
import com.example.waynian.ftp_upload.utils.StringToTxt;

public class FTPActivity extends AppCompatActivity {

    private static final String TAG = "TXTActivity";
    private static final int READTXT_SUCESS = 10;
    private static final int SAVETXT_SUCESS = 11;
    private static final int DWONLOADNEWPIC_SUCESS = 12;
    private static final int DWONLOADOLDPIC_SUCESS = 13;

    private Button mSave;


    private TextView tv1,tv2,tv3,tv4,tv5,tv6,tv7,tv8,tv9,tv10,tv11,tv12,tv13,tv14,tv15,tv16;
    private EditText et1,et2,et3,et4,et5,et6,et7,et8,et9,et10,et11,et12,et13,et14,et15,et16;



    private String mTXT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_ftp);

        intUI();


    }

    private void intUI() {


        mSave = (Button) findViewById(R.id.bt_save);

        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);
        tv4 = (TextView) findViewById(R.id.tv4);
        tv5 = (TextView) findViewById(R.id.tv5);
        tv6 = (TextView) findViewById(R.id.tv6);
        tv7 = (TextView) findViewById(R.id.tv7);
        tv8 = (TextView) findViewById(R.id.tv8);
        tv9 = (TextView) findViewById(R.id.tv9);
        tv10 = (TextView) findViewById(R.id.tv10);
        tv11 = (TextView) findViewById(R.id.tv11);
        tv12 = (TextView) findViewById(R.id.tv12);
        tv13 = (TextView) findViewById(R.id.tv13);
        tv14 = (TextView) findViewById(R.id.tv14);
        tv15 = (TextView) findViewById(R.id.tv15);
        tv16 = (TextView) findViewById(R.id.tv16);

        et1 = (EditText) findViewById(R.id.et1);
        et2 = (EditText) findViewById(R.id.et2);
        et3 = (EditText) findViewById(R.id.et3);
        et4 = (EditText) findViewById(R.id.et4);
        et5 = (EditText) findViewById(R.id.et5);
        et6 = (EditText) findViewById(R.id.et6);
        et7 = (EditText) findViewById(R.id.et7);
        et8 = (EditText) findViewById(R.id.et8);
        et9 = (EditText) findViewById(R.id.et9);
        et10 = (EditText) findViewById(R.id.et10);
        et11 = (EditText) findViewById(R.id.et11);
        et12 = (EditText) findViewById(R.id.et12);
        et13 = (EditText) findViewById(R.id.et13);
        et14 = (EditText) findViewById(R.id.et14);
        et15 = (EditText) findViewById(R.id.et15);
        et16 = (EditText) findViewById(R.id.et16);



        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //保存
                StringToTxt.stringTxt(txtString());

            }

        });
    }


    private String txtString(){
        //将页面内容转换为string

        String s1 = tv1.getText().toString() + et1.getText().toString();
        String s2 = tv2.getText().toString() + et2.getText().toString();
        String s3 = tv3.getText().toString() + et3.getText().toString();
        String s4 = tv4.getText().toString() + et4.getText().toString();
        String s5 = tv5.getText().toString() + et5.getText().toString();
        String s6 = tv6.getText().toString() + et6.getText().toString();
        String s7 = tv7.getText().toString() + et7.getText().toString();
        String s8 = tv8.getText().toString() + et8.getText().toString();
        String s9 = tv9.getText().toString() + et9.getText().toString();
        String s10 = tv10.getText().toString() + et10.getText().toString();
        String s11 = tv11.getText().toString() + et11.getText().toString();
        String s12 = tv12.getText().toString() + et12.getText().toString();
        String s13 = tv13.getText().toString() + et13.getText().toString();
        String s14 = tv14.getText().toString() + et14.getText().toString();
        String s15 = tv15.getText().toString() + et15.getText().toString();
        String s16 = tv16.getText().toString() + et16.getText().toString();

        mTXT = s1 + "\n" + s2 + "\n" + s3 + "\n" + s4 + "\n" + s5 + "\n"
                + s6 + "\n" + s7 + "\n" + s8 + "\n" + s9 + "\n" + s10 + "\n" + s11 + "\n"
                + s12 + "\n" + s13 + "\n" + s14 + "\n" + s15 + "\n" + s16 + "\n";

        return mTXT;
    }


}
