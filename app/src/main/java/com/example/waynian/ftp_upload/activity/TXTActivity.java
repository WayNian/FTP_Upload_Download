package com.example.waynian.ftp_upload.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.waynian.ftp_upload.R;
import com.example.waynian.ftp_upload.ftp.FTP;
import com.example.waynian.ftp_upload.utils.C;
import com.example.waynian.ftp_upload.utils.StringToTxt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TXTActivity extends AppCompatActivity {
    private static final String TAG = "TXTActivity";
    private static final int READTXT_SUCESS = 10;
    private static final int SAVETXT_SUCESS = 11;
    private static final int DWONLOADNEWPIC_SUCESS = 12;
    private static final int DWONLOADOLDPIC_SUCESS = 13;


    private EditText et1, et2, et3, et4, et5, et6, et7, et8, et9, et10, et11, et12, et14, et16;
    private CheckBox cb13, cb15;

    private String cb13Txt = "0";
    private Boolean cb15Txt = false;


    private ScrollView mainView;
    private String mTXT;
    private String text;
    private Button mSave;
    private ImageView mNewImage, mOldImage;
    private TextView mNewPic, mOldPic, mStop;
    private Boolean setSucess = false;
    private int num;

    //终止线程
    public volatile boolean exit = false;

    /**
     * 服务器名.
     */
    private String hostName;

    /**
     * 用户名.
     */
    private String userName;

    /**
     * 密码.
     */
    private String password;


    final resource rs = new resource();

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case READTXT_SUCESS:
                    break;

                case SAVETXT_SUCESS:

                    break;

                case DWONLOADOLDPIC_SUCESS:

                    break;

                case DWONLOADNEWPIC_SUCESS:
                    System.gc();
                    mainView.setVisibility(View.INVISIBLE);
                    mNewPic.setVisibility(View.VISIBLE);
                    mOldPic.setVisibility(View.VISIBLE);
                    mSave.setClickable(true);
//                    mStop.setVisibility(View.INVISIBLE);
                    Bitmap bitmap1 = getLoacalBitmap("/sdcard/aaa/old.jpg");

                    mOldImage.setImageBitmap(centerSquareScaleBitmap(bitmap1, 512));

                    Bitmap bitmap = getLoacalBitmap("/sdcard/aaa/new.jpg"); //从本地取图片

                    mNewImage.setImageBitmap(bitmap);
                    setSucess = true;
                    System.gc();
                    break;
                case 1:
                    Toast.makeText(TXTActivity.this, "连接FTP失败,请检查网络！", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setContentView(R.layout.activity_txt);


        Intent intent = getIntent();
        hostName = intent.getStringExtra("hostName");
        userName = intent.getStringExtra("name");
        password = intent.getStringExtra("pwd");

        System.gc();


        intUI();


    }


    private void intUI() {


        mSave = (Button) findViewById(R.id.bt_save);
        mNewImage = (ImageView) findViewById(R.id.iv_newImage);
        mOldImage = (ImageView) findViewById(R.id.iv_oldImage);
        mainView = (ScrollView) findViewById(R.id.main);
        mNewPic = (TextView) findViewById(R.id.tv_new);
        mOldPic = (TextView) findViewById(R.id.tv_old);
        mStop = (TextView) findViewById(R.id.stop);


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
        cb13 = (CheckBox) findViewById(R.id.cb13);
        et14 = (EditText) findViewById(R.id.et14);
        cb15 = (CheckBox) findViewById(R.id.cb15);
        et16 = (EditText) findViewById(R.id.et16);

        cb13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cb13.isChecked()) {
                    cb13Txt = "1";
                } else {
                    cb13Txt = "0";
                }
                Log.d(TAG, "cb13Txt: " + cb13Txt);
            }
        });




        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mSave.setClickable(false);
//                mStop.setVisibility(View.VISIBLE);
                //保存
                StringToTxt.stringTxt(txtString());

                mNewImage.setVisibility(View.VISIBLE);
                mOldImage.setVisibility(View.VISIBLE);

                System.gc();

                num = new Integer(et14.getText().toString());

                cb15.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (cb15.isChecked()) {
                            num = 50;
                        } else {
                            num = new Integer(et14.getText().toString());
                        }
                        Log.d(TAG, "cb15Txt: " + cb15Txt);
                    }
                });



                ftp.start();


            }

        });

        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                exit = true;
                num = 0;
            }
        });


    }


    Thread ftp = new Thread() {


        @Override
        public void run() {
            super.run();
            try {
                Log.d(TAG, "登录状态" + rs.isLoginSuccess());
            } catch (IOException e) {
                e.printStackTrace();
                mHandler.sendEmptyMessage(1);
            }

            for (int m = 1; m <= num; m++) {
                Log.d(TAG, "onClick: 循环了" + m + "次");
                if (!exit) {
                    while (rs.checkOneExist()) {
                        rs.delete();
                        break;
                    }
                    try {
                        if (rs.upload()) {
                            for (int i = 2; i > 0; i++) {

                                if (rs.checkExist()) {

                                    if (rs.downloadold() && rs.downloadnew()) {
                                        Log.d(TAG, "run: 下载照片成功");

                                        Message msg = new Message();
                                        msg.what = DWONLOADNEWPIC_SUCESS;
                                        mHandler.sendMessage(msg);
                                        break;
                                    }
                                    break;
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            System.gc();
                            rs.delete();
                            deletepic("/sdcard/aaa/new.jpg");
                            deletepic("/sdcard/aaa/old.jpg");
                            System.gc();
                        }
                    }.start();

                }
            }

        }
    };


    private String txtString() {
        //将页面内容转换为string

        String s1 = "wname_buf = " + et1.getText().toString();
        String s2 = "dtcwt_n = " + et2.getText().toString();
        String s3 = "dtcwt_level = " + et3.getText().toString();
        String s4 = "a_weight = " + et4.getText().toString();
        String s5 = "l_weight = " + et5.getText().toString();
        String s6 = "h_weight = " + et6.getText().toString();
        String s7 = "dtcwt_weight[0] = " + et7.getText().toString();
        String s8 = "dtcwt_weight[1] = " + et8.getText().toString();
        String s9 = "dtcwt_weight[2] = " + et9.getText().toString();
        String s10 = "dtcwt_weight[3] = " + et10.getText().toString();
        String s11 = "dtcwt_weight[4] = " + et11.getText().toString();
        String s12 = "dtcwt_weight[5] = " + et12.getText().toString();
        String s13 = "dtcwt_flag = " + cb13Txt;
        String s14 = "captureNum = " + "1";
        String s15 = "infiniteLoop = " + "0";
        String s16 = "periodTime = " + et16.getText().toString();

        mTXT = s1 + "\n" + s2 + "\n" + s3 + "\n" + s4 + "\n" + s5 + "\n"
                + s6 + "\n" + s7 + "\n" + s8 + "\n" + s9 + "\n" + s10 + "\n" + s11 + "\n"
                + s12 + "\n" + s13 + "\n" + s14 + "\n" + s15 + "\n" + s16 + "\n";

        return mTXT;
    }


    //从本地获取图片
    public static Bitmap getLoacalBitmap(String url) {

        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }


    }

    public static Bitmap centerSquareScaleBitmap(Bitmap bitmap, int edgeLength) {
        if (null == bitmap || edgeLength <= 0) {
            return null;
        }

        Bitmap result = bitmap;
        int widthOrg = bitmap.getWidth();
        int heightOrg = bitmap.getHeight();

        if (widthOrg > edgeLength && heightOrg > edgeLength) {
            //压缩到一个最小长度是edgeLength的bitmap
            int longerEdge = (int) (edgeLength * Math.max(widthOrg, heightOrg) / Math.min(widthOrg, heightOrg));
            int scaledWidth = widthOrg > heightOrg ? longerEdge : edgeLength;
            int scaledHeight = widthOrg > heightOrg ? edgeLength : longerEdge;
            Bitmap scaledBitmap;

            try {
                scaledBitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
            } catch (Exception e) {
                return null;
            }

            //从图中截取正中间的正方形部分。
            int xTopLeft = (scaledWidth - edgeLength) / 2;
            int yTopLeft = (scaledHeight - edgeLength) / 2;

            try {
                result = Bitmap.createBitmap(scaledBitmap, xTopLeft, yTopLeft, edgeLength, edgeLength);
                scaledBitmap.recycle();
            } catch (Exception e) {
                return null;
            }
        }

        return result;
    }


    //删除本地文件
    private void deletepic(String path) {

        File file = new File(path);
        Boolean isExist = file.exists();
        if (isExist) {
            file.delete();
            Log.d(TAG, "deletepic: 本地文件已删除");
        }

    }

    //p判断本地文件和ftp文件大小
    private Boolean localLen(String localpath, String ftpPath) throws IOException {
        File file = new File(localpath);
        Boolean len;

        int localLen = (int) file.length();
        int ftpLen = new FTP(hostName, userName, password).ftpFileLength(ftpPath);
        if (localLen == ftpLen) {
            Log.d(TAG, "大小相等,大小为" + localLen + "----" + ftpLen);
            len = true;
        } else {
            Log.d(TAG, "大小不相等,大小为" + localLen + "----" + ftpLen);
            len = false;
        }

        return len;
    }

    private Boolean localftpLen() throws IOException {

        Boolean oldJpg = localLen("/sdcard/aaa/old.jpg", "/pic/new/old.jpg");
        Boolean newJpg = localLen("/sdcard/aaa/new.jpg", "/pic/new/new.jpg");
        Boolean len = oldJpg && newJpg;

        return len;
    }

    public class resource {
        //是否登录成功

        public Boolean isLoginSuccess() throws IOException {
            Boolean isSucess = new FTP(hostName, userName, password).openConnect();

            return isSucess;
        }


        public Boolean upload() throws IOException {


            File file = new File("/sdcard/aaa/cmd.txt");

            //单文件上传
            Boolean sucess = new FTP(hostName, userName, password).uploadSingleFile(file, "/cmd", new FTP.UploadProgressListener() {

                @Override
                public void onUploadProgress(String currentStep, long uploadSize, File file) {
                    // TODO Auto-generated method stub
                    Log.d(TAG, currentStep);
                    if (currentStep.equals(C.FTP_UPLOAD_SUCCESS)) {
                        Log.d(TAG, "-----shanchuan--successful");
                        Message msg = new Message();
                        msg.what = SAVETXT_SUCESS;
                        mHandler.sendMessage(msg);
                    } else if (currentStep.equals(C.FTP_UPLOAD_LOADING)) {
                        long fize = file.length();
                        float num = (float) uploadSize / (float) fize;
                        int result = (int) (num * 100);
                        Log.d(TAG, "-----shangchuan---" + result + "%");
                    }
                }
            });


            return sucess;


        }

        public Boolean checkExist() {

            Boolean isNewExist = new FTP(hostName, userName, password).isExist("/pic/new/new.jpg");
            Boolean isOldExist = new FTP(hostName, userName, password).isExist("/pic/old/old.jpg");


            Boolean isExist = isNewExist && isOldExist;
            if (isExist) {
                Log.d(TAG, "checkExist: 文件存在");
            } else {
                Log.d(TAG, "checkExist: 文件不存在");
            }
            return isExist;
        }

        public Boolean checkOneExist() {

            Boolean isNewExist = new FTP(hostName, userName, password).isExist("/pic/new/new.jpg");
            Boolean isOldExist = new FTP(hostName, userName, password).isExist("/pic/old/old.jpg");


            Boolean isExist = isNewExist || isOldExist;
            if (isExist) {
                Log.d(TAG, "checkExist: 文件存在");
            } else {
                Log.d(TAG, "checkExist: 文件不存在");
            }
            return isExist;
        }


        public Boolean downloadold() throws Exception {

            //下载老的图片
            //单文件下载
            Boolean success = new FTP(hostName, userName, password).downloadSingleFile("/pic/old/old.jpg", "/sdcard/aaa/", "old.jpg", new FTP.DownLoadProgressListener() {

                @Override
                public void onDownLoadProgress(String currentStep, long downProcess, File file) {
                    Log.d(TAG, currentStep);
                    if (currentStep.equals(C.FTP_DOWN_SUCCESS)) {
                        Log.d(TAG, "-----下载老图片--成功");


                    } else if (currentStep.equals(C.FTP_DOWN_LOADING)) {
                        Log.d(TAG, "-----正在下载老图片---" + downProcess + "%");
                    }
                }

            });


            return success;
        }


        public Boolean downloadnew() throws Exception {
            //下载新的图片

            //单文件下载
            Boolean success = new FTP(hostName, userName, password).downloadSingleFile("/pic/new/new.jpg", "/sdcard/aaa/", "new.jpg", new FTP.DownLoadProgressListener() {

                @Override
                public void onDownLoadProgress(String currentStep, long downProcess, File file) {
                    Log.d(TAG, currentStep);
                    if (currentStep.equals(C.FTP_DOWN_SUCCESS)) {
                        Log.d(TAG, "-----下载新图片--成功-----");
                        Message msg = new Message();
                        msg.what = DWONLOADNEWPIC_SUCESS;
                        mHandler.sendMessage(msg);
                    } else if (currentStep.equals(C.FTP_DOWN_LOADING)) {
                        Log.d(TAG, "-----正在下载新图片---" + downProcess + "%");
                    }
                }

            });


            return success;
        }

        public void delete() {
            //下载新的图片
            synchronized (this) {

                // 删除
                try {

                    new FTP(hostName, userName, password).deleteSingleFile("/pic/old/old.jpg", new FTP.DeleteFileProgressListener() {

                        @Override
                        public void onDeleteProgress(String currentStep) {
                            Log.d(TAG, currentStep);
                            if (currentStep.equals(C.FTP_DELETEFILE_SUCCESS)) {
                                Log.d(TAG, "-----删除老照片成功------");
                            } else if (currentStep.equals(C.FTP_DELETEFILE_FAIL)) {
                                Log.d(TAG, "-----删除老照片失败------");
                            }
                        }

                    });

                    new FTP(hostName, userName, password).deleteSingleFile("/pic/new/new.jpg", new FTP.DeleteFileProgressListener() {

                        @Override
                        public void onDeleteProgress(String currentStep) {
                            Log.d(TAG, currentStep);
                            if (currentStep.equals(C.FTP_DELETEFILE_SUCCESS)) {
                                Log.d(TAG, "-----删除新照片成功------");
                            } else if (currentStep.equals(C.FTP_DELETEFILE_FAIL)) {
                                Log.d(TAG, "-----删除新照片失败------l");
                            }
                        }

                    });

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }

    }


}
