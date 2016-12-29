package com.example.waynian.ftp_upload.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;

/**
 * Created by waynian on 2016/12/20.
 */

public class StringToTxt {

    public static void saveFile(String str) {
        String filePath = null;
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) { // SD卡根目录的hello.text
            filePath = Environment.getExternalStorageDirectory().toString() + File.separator + "cmd.txt";
        } else  // 系统下载缓存根目录的hello.text
            filePath = Environment.getDownloadCacheDirectory().toString() + File.separator + "cmd.txt";

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }
            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(str.getBytes());
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void stringTxt(String str){
        try {
            FileWriter fw = new FileWriter("/sdcard/aaa" + "/cmd.txt");
            fw.flush();
            fw.write(str);
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}