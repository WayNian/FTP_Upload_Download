package com.example.waynian.ftp_upload.ftp;

import android.util.Log;

import com.example.waynian.ftp_upload.utils.C;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FTP {

    public static final String REMOTE_PATH = "/";

    private static final String TAG = "FTP";
    /**
     * 服务器名.
     */
    private String hostName;

    /**
     * 端口号
     */
    private int serverPort;

    /**
     * 用户名.
     */
    private String userName;

    /**
     * 密码.
     */
    private String password;

    /**
     * FTP连接.
     */
    private FTPClient ftpClient;

    /**
     * FTP列表.
     */
    private List<FTPFile> list;


    public FTP() {
    }

    public FTP(String hostName, String userName, String password) {
        this.hostName = hostName;
        this.serverPort = 21;
        this.userName = userName;
        this.password = password;
        this.list = new ArrayList<FTPFile>();
        this.ftpClient = new FTPClient();
    }

    // -------------------------------------------------------文件上传方法------------------------------------------------

    /**
     * 上传单个文件.
     *
     * @param remotePath FTP目录
     * @param listener   监听器
     * @throws IOException
     */
    public Boolean uploadSingleFile(File singleFile, String remotePath,
                                    UploadProgressListener listener) throws IOException {


        // 上传之前初始化
        this.uploadBeforeOperate(remotePath, listener);

        boolean flag;
        flag = uploadingSingle(singleFile, listener);
        if (flag) {
            listener.onUploadProgress(C.FTP_UPLOAD_SUCCESS, 0,
                    singleFile);
        } else {
            listener.onUploadProgress(C.FTP_UPLOAD_FAIL, 0,
                    singleFile);
        }

        // 上传完成之后关闭连接
        this.uploadAfterOperate(listener);


        return flag;
    }

    /**
     * 上传多个文件.
     *
     * @param remotePath FTP目录
     * @param listener   监听器
     * @throws IOException
     */
    public void uploadMultiFile(LinkedList<File> fileList, String remotePath,
                                UploadProgressListener listener) throws IOException {

        // 上传之前初始化
        this.uploadBeforeOperate(remotePath, listener);

        boolean flag;

        for (File singleFile : fileList) {
            flag = uploadingSingle(singleFile, listener);
            if (flag) {
                listener.onUploadProgress(C.FTP_UPLOAD_SUCCESS, 0,
                        singleFile);
            } else {
                listener.onUploadProgress(C.FTP_UPLOAD_FAIL, 0,
                        singleFile);
            }
        }

        // 上传完成之后关闭连接
        this.uploadAfterOperate(listener);
    }

    /**
     * 上传单个文件.
     *
     * @param localFile 本地文件
     * @return true上传成功, false上传失败
     * @throws IOException
     */
    private boolean uploadingSingle(File localFile,
                                    UploadProgressListener listener) throws IOException {
        boolean flag = true;
        // 不带进度的方式
        // // 创建输入流
        // InputStream inputStream = new FileInputStream(localFile);
        // // 上传单个文件
        // flag = ftpClient.storeFile(localFile.getName(), inputStream);
        // // 关闭文件流
        // inputStream.close();

        // 带有进度的方式
        BufferedInputStream buffIn = new BufferedInputStream(
                new FileInputStream(localFile));
        ProgressInputStream progressInput = new ProgressInputStream(buffIn,
                listener, localFile);
        flag = ftpClient.storeFile(localFile.getName(), progressInput);
        buffIn.close();

        return flag;
    }

    /**
     * 上传文件之前初始化相关参数
     *
     * @param remotePath FTP目录
     * @param listener   监听器
     * @throws IOException
     */
    private void uploadBeforeOperate(String remotePath,
                                     UploadProgressListener listener) throws IOException {

        // 打开FTP服务
        try {
            this.openConnect();
            listener.onUploadProgress(C.FTP_CONNECT_SUCCESSS, 0,
                    null);
        } catch (IOException e1) {
            e1.printStackTrace();
            listener.onUploadProgress(C.FTP_CONNECT_FAIL, 0, null);
            return;
        }

        // 设置模式
        ftpClient.setFileTransferMode(org.apache.commons.net.ftp.FTP.STREAM_TRANSFER_MODE);
        // FTP下创建文件夹
        ftpClient.makeDirectory(remotePath);
        // 改变FTP目录
        ftpClient.changeWorkingDirectory(remotePath);
        // 上传单个文件

    }

    /**
     * 上传完成之后关闭连接
     *
     * @param listener
     * @throws IOException
     */
    private void uploadAfterOperate(UploadProgressListener listener)
            throws IOException {
        this.closeConnect();
        listener.onUploadProgress(C.FTP_DISCONNECT_SUCCESS, 0, null);
    }

    // -------------------------------------------------------文件下载方法------------------------------------------------

    /**
     * 下载单个文件，可实现断点下载.
     *
     * @param serverPath Ftp目录及文件路径
     * @param localPath  本地目录
     * @param fileName   下载之后的文件名称
     * @param listener   监听器
     * @throws IOException
     */
    public Boolean downloadSingleFile(String serverPath, String localPath, String fileName, DownLoadProgressListener listener)
            throws Exception {


        // 打开FTP服务
        try {
            this.openConnect();
            listener.onDownLoadProgress(C.FTP_CONNECT_SUCCESSS, 0, null);
        } catch (IOException e1) {
            e1.printStackTrace();
            listener.onDownLoadProgress(C.FTP_CONNECT_FAIL, 0, null);
        }

        BufferedOutputStream outStream = null;
        boolean success = false;
        if (null != this.ftpClient && this.ftpClient.isConnected()) {
            outStream = new BufferedOutputStream(new FileOutputStream(localPath + fileName));
            success = this.ftpClient.retrieveFile(serverPath, outStream);

            if (outStream != null) {
                try {
                    outStream.flush();
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return success;


    }


    public Boolean isExist(String serverPath) {

        // 打开FTP服务
        try {
            this.openConnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 先判断服务器文件是否存在
        FTPFile[] files = new FTPFile[0];
        try {
            files = ftpClient.listFiles(serverPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (files.length == 0) {
            return false;
        } else {
            return true;
        }
    }

    public int ftpFileLength(String serverPath) throws IOException {

        // 打开FTP服务
        try {
            this.openConnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 先判断服务器文件是否存在
        FTPFile[] files = new FTPFile[0];

        files = ftpClient.listFiles(serverPath);
        int len = files.length;

        return len;
    }

    // -------------------------------------------------------文件删除方法------------------------------------------------

    /**
     * 删除Ftp下的文件.
     *
     * @param serverPath Ftp目录及文件路径
     * @param listener   监听器
     * @throws IOException
     */
    public Boolean deleteSingleFile(String serverPath, DeleteFileProgressListener listener)
            throws Exception {

        Boolean isExistFile = false;

        // 打开FTP服务
        try {
            this.openConnect();
            listener.onDeleteProgress(C.FTP_CONNECT_SUCCESSS);
        } catch (IOException e1) {
            e1.printStackTrace();
            listener.onDeleteProgress(C.FTP_CONNECT_FAIL);
        }

        // 先判断服务器文件是否存在
//        FTPFile[] files = ftpClient.listFiles(serverPath);
//        if (files.length == 0) {
//            listener.onDeleteProgress(C.FTP_FILE_NOTEXISTS);
//            return isExistFile;
//        }

        //进行删除操作
        boolean flag = true;
        this.ftpClient.enterLocalPassiveMode();
        flag = ftpClient.deleteFile(serverPath);
        if (flag) {
            listener.onDeleteProgress(C.FTP_DELETEFILE_SUCCESS);
        } else {
            listener.onDeleteProgress(C.FTP_DELETEFILE_FAIL);
        }

        // 删除完成之后关闭连接
        this.closeConnect();
        listener.onDeleteProgress(C.FTP_DISCONNECT_SUCCESS);
        return flag;
    }

    // -------------------------------------------------------打开关闭连接------------------------------------------------

    /**
     * 打开FTP服务.
     *
     * @throws IOException
     */
    public Boolean openConnect() throws IOException {
        Boolean isSuccessLogin;
        // 中文转码
        ftpClient.setControlEncoding("UTF-8");
        int reply; // 服务器响应值
        // 连接至服务器
        ftpClient.connect(hostName, serverPort);
        // 获取响应值
        reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            // 断开连接
            ftpClient.disconnect();
            throw new IOException("connect fail: " + reply);
        }
        // 登录到服务器
        isSuccessLogin = ftpClient.login(userName, password);
        // 获取响应值
        reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            // 断开连接
            ftpClient.disconnect();
            throw new IOException("connect fail: " + reply);
        } else {
            // 获取登录信息
            FTPClientConfig config = new FTPClientConfig(ftpClient
                    .getSystemType().split(" ")[0]);
            config.setServerLanguageCode("zh");
            ftpClient.configure(config);
            // 使用主动模式设为默认
            ftpClient.enterLocalActiveMode();
            // 二进制文件支持
            ftpClient
                    .setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
        }

        return isSuccessLogin;
    }

    /**
     * 关闭FTP服务.
     *
     * @throws IOException
     */
    public void closeConnect() throws IOException {
        if (ftpClient != null) {
            // 退出FTP
            ftpClient.logout();
            // 断开连接
            ftpClient.disconnect();
        }
        Log.d(TAG, "logout.....  closeConnect....");
    }


    /**
     * 列出FTP下所有文件.
     *
     * @param remotePath 服务器目录
     * @return FTPFile集合
     * @throws IOException
     */
    public List<FTPFile> listFiles(String remotePath) throws IOException {
        this.openConnect();
        // 获取文件
        FTPFile[] files = ftpClient.listFiles(remotePath);
        // 遍历并且添加到集合
        for (FTPFile file : files) {
            list.add(file);
        }

        return list;
    }


    // ---------------------------------------------------上传、下载、删除监听---------------------------------------------

    /*
     * 上传进度监听
     */
    public interface UploadProgressListener {
        public void onUploadProgress(String currentStep, long uploadSize, File file);
    }

    /*
     * 下载进度监听
     */
    public interface DownLoadProgressListener {
        public void onDownLoadProgress(String currentStep, long downProcess, File file);
    }

    /*
     * 文件删除监听
     */
    public interface DeleteFileProgressListener {
        public void onDeleteProgress(String currentStep);
    }

    //    /**
//     * 本地编码转FTP编码，O
//     */
//    private String LocaltoFTP(String mStr) throws IOException {
//        return new String(mStr.getBytes(localCharset), serverCharset);
//    }


}
