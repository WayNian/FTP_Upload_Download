package com.example.waynian.ftp_upload.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.waynian.ftp_upload.ftp.FTP;
import com.example.waynian.ftp_upload.R;
import com.example.waynian.ftp_upload.activity.adapter.RemoteAdapter;
import com.example.waynian.ftp_upload.utils.C;
import com.example.waynian.ftp_upload.utils.MyIntent;

import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * 当前选中项.
     */
    private int position = 0;

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
    /**
     * FTP文件集合.
     */
    private List<FTPFile> remoteFile;

    /**
     * ListView.
     */
    private ListView listMain;

    private RemoteAdapter remoteadapter;

    private TextView currentPathTv;


    private String mCurrentFtpPath = "";

    private static final String TAG = "MainActivity";



    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(MainActivity.this, "上传成功", Toast.LENGTH_SHORT).show();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Intent intent = getIntent();
        hostName = intent.getStringExtra("hostName");
        userName = intent.getStringExtra("name");
        password = intent.getStringExtra("pwd");

        initView();
    }

    private void loadRemoteView(String remoutePath) {

        currentPathTv.setText(remoutePath);
        mCurrentFtpPath = remoutePath;
        listMain.setAdapter(null);

        // 初始化FTP列表
        remoteFile = new ArrayList<FTPFile>();
        // 加载FTP列表
        new Thread(){
            @Override
            public void run() {
                super.run();

                try {
                    remoteFile = new FTP(hostName, userName, password).listFiles(mCurrentFtpPath);
                    Log.d("MainActivity", "" + remoteFile.size());
                    for (FTPFile ftpFile : remoteFile) {
                        Log.d("MainActivity", "file:" + ftpFile.getName());
                        mHandler.post(runnable);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }.start();



    }

    //不能在子线程更新UI，Android更新UI要在主线程，所以利用消息机制
    Runnable runnable = new Runnable() {
        @Override
        public void run() {

            if (remoteFile != null) {
                // FTP列表适配器
                remoteadapter = new RemoteAdapter(MainActivity.this, remoteFile);
                // 加载数据到ListView
                listMain.setAdapter(remoteadapter);
            }
        }
    };

    private void initView() {
        //初始化控件
        currentPathTv = (TextView) findViewById(R.id.currentpath_text);
        listMain = (ListView) findViewById(R.id.list);

        // ListView单击
        listMain.setOnItemClickListener(listMainItemClick);
        // ListView选中项改变

        listMain.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                return false;
            }
        });


        loadRemoteView(FTP.REMOTE_PATH);

        //上传功能
        //new FTP().uploadMultiFile为多文件上传
        //new FTP().uploadSingleFile为单文件上传
        Button buttonUpload = (Button) findViewById(R.id.button_upload);
        buttonUpload.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        // 上传
                        File file = new File("/sdcard/aaa/cmd.txt");
                        try {

                            //单文件上传
                            new FTP(hostName, userName, password).uploadSingleFile(file, "/cmd", new FTP.UploadProgressListener() {

                                @Override
                                public void onUploadProgress(String currentStep, long uploadSize, File file) {
                                    // TODO Auto-generated method stub
                                    Log.d(TAG, currentStep);
                                    if (currentStep.equals(C.FTP_UPLOAD_SUCCESS)) {
                                        Log.d(TAG, "-----shanchuan--successful");
                                        mHandler.sendEmptyMessage(0);

                                    } else if (currentStep.equals(C.FTP_UPLOAD_LOADING)) {
                                        long fize = file.length();
                                        float num = (float) uploadSize / (float) fize;
                                        int result = (int) (num * 100);
                                        Log.d(TAG, "-----shangchuan---" + result + "%");
                                    }
                                }
                            });
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                }).start();

            }
        });

        //下载功能
        Button buttonDown = (Button) findViewById(R.id.button_down);
        buttonDown.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        // 下载
                        try {

                            //单文件下载
                            new FTP(hostName, userName, password).downloadSingleFile("/fff/ftpTest.docx", "/mnt/sdcard/download/", "ftpTest.docx", new FTP.DownLoadProgressListener() {

                                @Override
                                public void onDownLoadProgress(String currentStep, long downProcess, File file) {
                                    Log.d(TAG, currentStep);
                                    if (currentStep.equals(C.FTP_DOWN_SUCCESS)) {
                                        Log.d(TAG, "-----xiazai--successful");
                                    } else if (currentStep.equals(C.FTP_DOWN_LOADING)) {
                                        Log.d(TAG, "-----xiazai---" + downProcess + "%");
                                    }
                                }

                            });

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                }).start();

            }
        });

        //删除功能
        Button buttonDelete = (Button) findViewById(R.id.button_delete);
        buttonDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        // 删除
                        try {

                            new FTP(hostName, userName, password).deleteSingleFile("/fff/ftpTest.docx", new FTP.DeleteFileProgressListener() {

                                @Override
                                public void onDeleteProgress(String currentStep) {
                                    Log.d(TAG, currentStep);
                                    if (currentStep.equals(C.FTP_DELETEFILE_SUCCESS)) {
                                        Log.d(TAG, "-----shanchu--success");
                                        Toast.makeText(MainActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                                    } else if (currentStep.equals(C.FTP_DELETEFILE_FAIL)) {
                                        Log.d(TAG, "-----shanchu--fail");
                                    }
                                }

                            });

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                }).start();

            }
        });

    }

    /**
     * ListView单击事件.
     */
    private AdapterView.OnItemClickListener listMainItemClick = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int location,
                                long arg3) {
            MainActivity.this.position = location;

            if (location != 0) {
//                Toast.makeText(MainActivity.this,
//                        remoteFile.get(location - 1).getName(), Toast.LENGTH_SHORT)
//                        .show();
                if (remoteFile.get(location - 1).isDirectory()) {
                    if (!mCurrentFtpPath.endsWith("/")) {
                        mCurrentFtpPath = mCurrentFtpPath + "/" + remoteFile.get(location - 1).getName();
                    } else {
                        mCurrentFtpPath = mCurrentFtpPath + remoteFile.get(location - 1).getName();
                    }

                    loadRemoteView(mCurrentFtpPath);
                }
                MyIntent.getImageFileIntent(mCurrentFtpPath);
            } else {
                if (mCurrentFtpPath.contains("/") && !mCurrentFtpPath.equals("/")) {
                    Log.d(TAG, mCurrentFtpPath);
                    Log.d(TAG, "" + mCurrentFtpPath.lastIndexOf("/"));
                    mCurrentFtpPath = mCurrentFtpPath.substring(0, mCurrentFtpPath.lastIndexOf("/"));
                    Log.d(TAG, mCurrentFtpPath);
                    if (mCurrentFtpPath.isEmpty()) {
                        mCurrentFtpPath = "/";
                    }
                    loadRemoteView(mCurrentFtpPath);
                }
            }


        }
    };

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            //do something...
            if (mCurrentFtpPath.contains("/") && !mCurrentFtpPath.equals("/")) {
                Log.d(TAG, mCurrentFtpPath);
                Log.d(TAG, "" + mCurrentFtpPath.lastIndexOf("/"));
                mCurrentFtpPath = mCurrentFtpPath.substring(0, mCurrentFtpPath.lastIndexOf("/"));
                Log.d(TAG, mCurrentFtpPath);
                if (mCurrentFtpPath.isEmpty()) {
                    mCurrentFtpPath = "/";
                }
                loadRemoteView(mCurrentFtpPath);
            }else {
                finish();
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            new FTP().closeConnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
