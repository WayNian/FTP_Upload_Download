package com.example.waynian.ftp_upload.ftp;

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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by waynian on 2016/12/22.
 */

public class FTPUtils {

    private static FTPUtils mFTPUtils = null;
    private FTPClient ftpClient = null;
    private FTPClientConfig config = null;
    private String serverIp = null;
    private int serverPort = 0;
    private String usrName = null;
    private String usrPwd = null;
    private String baseWorkDirectory = null;


    private String localCharset = "GBK";// 本地编码字符串编码
    private String serverCharset = "iso-8859-1";// FTP服务器端字符串编码


    protected FTPUtils() {
        ftpClient = new FTPClient();
    }


    public synchronized static FTPUtils getInstance() {
        if (mFTPUtils == null) {
            mFTPUtils = new FTPUtils();
        }
        return mFTPUtils;
    }


    /**
     * FTP高级设置，最好使用默认，否则获取文件等会出问题，O
     *
     * @param osType
     *            系统类型
     * @param serverLanguageCode
     *            服务器编码
     * @param defaultDateFormatStr
     *            默认日期格式
     * @param recentDateFormatStr
     *            ？？？
     * @param serverTimeZoneId
     *            时区
     */
    public boolean setFTPClientConfig(String osType, String serverLanguageCode, String defaultDateFormatStr, String recentDateFormatStr, String serverTimeZoneId) {
        try {
            if (osType != null && !osType.equals(""))
                config = new FTPClientConfig(osType);
            if (serverLanguageCode != null && !serverLanguageCode.equals(""))
                config.setServerLanguageCode(serverLanguageCode);
            if (defaultDateFormatStr != null && !defaultDateFormatStr.equals(""))
                config.setDefaultDateFormatStr(defaultDateFormatStr);
            if (recentDateFormatStr != null && !recentDateFormatStr.equals(""))
                config.setRecentDateFormatStr(recentDateFormatStr);
            if (serverTimeZoneId != null && !serverTimeZoneId.equals(""))
                config.setServerTimeZoneId(serverTimeZoneId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 使用默认配置，O
     */
    public boolean setFTPClientConfig() {
        this.config = new FTPClientConfig();
        return true;
    }


    /**
     * 设置FTP参数，O
     *
     * @param serverIp
     * @param serverPort
     * @param usrName
     * @param usrPwd
     * @param baseWorkDirectory
     * @return
     */
    public boolean setFTPClient(String serverIp, int serverPort, String usrName, String usrPwd, String baseWorkDirectory) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        this.usrName = usrName;
        this.usrPwd = usrPwd;
        this.baseWorkDirectory = baseWorkDirectory;
        System.out.println("设置参数");
        return true;
    }


    /**
     * 连接服务器，O
     *
     */
    public boolean connectServer() {
        boolean isOpen = false;
        if (serverIp != null && serverPort != 0 && usrName != null && usrPwd != null && baseWorkDirectory != null) {
            try {
                if (null != this.ftpClient) {// 没有连接
                    if (this.ftpClient.isConnected())
                        isOpen = true;
                    else
                        isOpen = Login();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return isOpen;
    }


    /**
     * 连接登录，O
     *
     */
    private boolean Login() {
        System.out.println("连接登录开始");
        boolean isLogin = false;
        try {
            if (this.config == null)
                this.config = new FTPClientConfig();
            this.ftpClient.configure(config);
            this.ftpClient.connect(serverIp, serverPort);
            int reply = this.ftpClient.getReplyCode();
            isLogin = this.ftpClient.login(usrName, usrPwd);
            if (FTPReply.isPositiveCompletion(reply)) {// 连接是否成功
                if (isLogin) {// 登录是否成功
                    this.ftpClient.setControlEncoding("GBK");
                    this.ftpClient.changeWorkingDirectory(LocaltoFTP(baseWorkDirectory));
                    this.ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
// this.ftpClient.setListHiddenFiles(true); //设置是否显示隐藏文件
                    this.ftpClient.setBufferSize(1024 * 2);
// this.ftpClient.setDataTimeout(60000); // 设置传输超时时间为60秒
                    this.ftpClient.setConnectTimeout(60000); // 连接超时为60秒
                    System.out.println("连接登录成功");
                } else
                    this.ftpClient.disconnect();
            } else
                this.ftpClient.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            if (this.ftpClient.isConnected()) {
                try {
                    this.ftpClient.disconnect();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return isLogin;
    }


    /**
     * 因为FTP有超时问题，最好每次操作前调用一次，O
     *
     */
    public boolean IsConnect() {
        boolean isConnect = false;
        if (serverIp != null && serverPort != 0 && usrName != null && usrPwd != null && baseWorkDirectory != null) {
            try {
                if (null != this.ftpClient) {// 连接成功
                    isConnect = this.ftpClient.isConnected();
                    System.out.println("连接成功");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return isConnect;
    }


    /**
     * 上传文件，O
     *
     * @param localFile
     *            本地文件
     * @param remoteFileName
     *            FTP 服务器文件名称
     * @return
     */
    public boolean uploadFile(File localFile, String remoteFileName) {
        BufferedInputStream inStream = null;
        boolean success = false;
        if (null != this.ftpClient && this.ftpClient.isConnected()) {
            try {
                inStream = new BufferedInputStream(new FileInputStream(localFile));
                this.ftpClient.enterLocalPassiveMode();
                success = this.ftpClient.storeFile(LocaltoFTP(remoteFileName), inStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inStream != null) {
                    try {
                        inStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return success;
    }


    /**
     * 上传文件，O
     *
     * @param localFilePath
     *            本地文件路径及名称
     * @param remoteFileName
     *            FTP 服务器文件名称
     * @return
     */
    public boolean uploadFile(String localFilePath, String remoteFileName) {
        BufferedInputStream inStream = null;
        boolean success = false;
        if (null != this.ftpClient && this.ftpClient.isConnected()) {
            try {
                inStream = new BufferedInputStream(new FileInputStream(localFilePath));
                this.ftpClient.enterLocalPassiveMode();
                success = this.ftpClient.storeFile(LocaltoFTP(remoteFileName), inStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inStream != null) {
                    try {
                        inStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return success;
    }


    /**
     * 上传目录及目录下的所有目录和文件，批量上传。。。，O
     *
     * @param localPath
     *            本地目录
     */
    public void uploadFiles(String localPath) {
        System.out.println("上传："+localPath);
        List<String> upFail = new ArrayList<String>();
        uploadFile(localPath, upFail);
    }


    private void uploadFile(String localPath, List<String> upFail) {
        BufferedInputStream inStream = null;
        if (null != this.ftpClient && this.ftpClient.isConnected()) {
            try {
                System.out.println("新一轮路径：" + localPath);
                String[] dir = localPath.split("/");// 拆分本地路径
                createDir(dir[dir.length - 1]);// 在FTP上创建最后一个目录的文件夹
                File[] files = new File(localPath).listFiles();// 获取本地目录文件夹句柄
                if (files != null && files.length > 0) {
                    for (File file : files) {
                        if (".".equals(file.getName()) || "..".equals(file.getName())) {


                        } else if (file.isDirectory()) {// 如果是目录
                            uploadFile(file.getAbsolutePath(), upFail);
                        } else if (file.isFile()) {// 是文件
                            System.out.println("上传：" + file.getName());
                            inStream = new BufferedInputStream(new FileInputStream(file.getAbsolutePath()));
                            this.ftpClient.enterLocalPassiveMode();
                            if (!this.ftpClient.storeFile(LocaltoFTP(file.getName()), inStream)) {
                                upFail.add(file.getAbsolutePath());// 没有上传成功！
                            }
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inStream != null) {
                    try {
                        inStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            toParentDir(1);// 后退一个目录
        }
    }


    /**
     * 下载文件,O
     *
     * @param localFilePath
     *            本地文件名及路径
     * @param remoteFileName
     *            远程文件名称
     * @return
     */
    public boolean downloadFile(String localFilePath, String remoteFileName) {
        BufferedOutputStream outStream = null;
        boolean success = false;
        if (null != this.ftpClient && this.ftpClient.isConnected()) {
            try {
                outStream = new BufferedOutputStream(new FileOutputStream(localFilePath));
                success = this.ftpClient.retrieveFile(remoteFileName, outStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (outStream != null) {
                    try {
                        outStream.flush();
                        outStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return success;
    }


    /**
     * 下载文件，O
     *
     * @param localFile
     *            本地文件
     * @param remoteFileName
     *            远程文件名称
     * @return
     */
    public boolean downloadFile(File localFile, String remoteFileName) {
        BufferedOutputStream outStream = null;
        FileOutputStream outStr = null;
        boolean success = false;
        if (null != this.ftpClient && this.ftpClient.isConnected()) {
            try {
                outStr = new FileOutputStream(localFile);
                outStream = new BufferedOutputStream(outStr);
                this.ftpClient.enterLocalPassiveMode();
                success = this.ftpClient.retrieveFile(new String(remoteFileName.getBytes(localCharset), serverCharset), outStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != outStream) {
                    try {
                        outStream.flush();
                        outStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (null != outStr) {
                    try {
                        outStr.flush();
                        outStr.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return success;
    }


    /**
     * 下载Ftp包含文件的子目录,O
     *
     * @param remoteDirName
     */
    public void downloadFiles(String localPath, String remoteDirName) {
        List<String> downFail = new ArrayList<String>();
        downloadFile(localPath, remoteDirName, downFail);
    }


    private void downloadFile(String localPath, String remoteDirName, List<String> downFail) {
        FileOutputStream fos = null;
        if (null != this.ftpClient && this.ftpClient.isConnected()) {
            try {
                localPath = localPath + "/" + remoteDirName;
                File mFile = new File(localPath);
                if (!mFile.exists())
                    mFile.mkdirs();// 创建本地文件夹
                changeDir(false, remoteDirName);// 进入FTP目录下
                System.out.println("新一轮路径：" + localPath);
                FTPFile[] files = getFiles();
                if (files != null && files.length > 0) {
                    for (FTPFile file : files) {
                        if (".".equals(file.getName()) || "..".equals(file.getName())) {


                        } else if (file.isDirectory()) {
                            downloadFile(localPath, file.getName(), downFail);
                        } else if (file.isFile()) {
                            System.out.println("下载：" + file.getName());
                            fos = new FileOutputStream(localPath + "/" + file.getName());
                            this.ftpClient.enterLocalPassiveMode();
                            if (!this.ftpClient.retrieveFile(LocaltoFTP(file.getName()), fos)) {
                                downFail.add(file.getName());
                            }
                            fos.flush();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            toParentDir(1);// 后退一个目录
        }
    }


    /**
     * 删除文件,O
     *
     * @param fileName
     *            文件名
     */
    public boolean removeFile(String fileName) {
        boolean isDeleteSuccess = false;
        if (null != this.ftpClient && this.ftpClient.isConnected()) {
            try {
                if (isFileExist(fileName)) {
                    this.ftpClient.enterLocalPassiveMode();
                    isDeleteSuccess = this.ftpClient.deleteFile(LocaltoFTP(fileName));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isDeleteSuccess;
    }


    /**
     * 删除文件夹,O
     *
     * @param DirName
     */
    public void removeAll(String DirName) {
        if (null != this.ftpClient && this.ftpClient.isConnected()) {
            try {
                createDir(DirName);// 进入子目录
                FTPFile[] files = getFiles();
                if (files != null && files.length > 0) {
                    for (FTPFile file : files) {
                        if (".".equals(file.getName()) || "..".equals(file.getName())) {


                        } else if (file.isDirectory()) {
                            removeAll(file.getName());
                        } else if (file.isFile()) {
                            System.out.println("删除：" + file.getName());
                            this.ftpClient.deleteFile(LocaltoFTP(file.getName()));
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                toParentDir(1);
                System.out.println("删除：" + DirName);
                this.ftpClient.removeDirectory(LocaltoFTP(DirName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 判断目录是否存在，O
     */
    public boolean isDirExist(String DirName) {
        boolean isName = false;
        if (null != this.ftpClient && this.ftpClient.isConnected()) {
            try {
                this.ftpClient.enterLocalPassiveMode();
                FTPFile[] dirs = this.ftpClient.listDirectories(LocaltoFTP(DirName));
                if (dirs != null && dirs.length > 2) {
                    isName = true;
                    System.out.println("目录存在！");
                } else
                    System.out.println("目录不存在！");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isName;
    }


    /**
     * 判断文件是否存在，O
     */
    public boolean isFileExist(String FileName) {
        boolean isName = false;
        if (null != this.ftpClient && this.ftpClient.isConnected()) {
            try {
                this.ftpClient.enterLocalPassiveMode();
                String[] files = this.ftpClient.listNames(LocaltoFTP(FileName));
                if (files != null && files.length > 0) {
                    isName = true;
                    System.out.println("文件存在！");
                } else
                    System.out.println("文件不存在！");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isName;
    }


    /**
     * 变更工作目录，O
     *
     * @param isBasePath
     *            是否回到FTP基路径
     * @param remoteDir
     *            --目录路径
     */
    public boolean changeDir(boolean isBasePath, String remoteDir) {
        boolean isChangeDir = false;
        if (null != this.ftpClient && this.ftpClient.isConnected()) {
            if (isBasePath) {
                toParentDir(getWorkingDirectory().split("/").length - 1);
            }
            isChangeDir = createDir(remoteDir);
            System.out.println("变更工作目录为:" + getWorkingDirectory());
        }
        return isChangeDir;
    }


    /**
     * 创建一个当前文件夹下的文件夹及其所有的子目录,如果所有的子目录文件夹存在，则返回false，O
     *
     * @param directory
     *            子目录
     * @return 成功返回true
     */
    public boolean createDir(String directory) {
        boolean isCreateSuccess = false;
        if (null != this.ftpClient && this.ftpClient.isConnected()) {
            try {
                if (null != directory && !"".equals(directory)) {
                    String[] dirs = directory.split("/");
                    this.ftpClient.enterLocalPassiveMode();
                    for (int i = 0; i < dirs.length; i++) {
                        if (!isDirExist(dirs[i]))
                            isCreateSuccess = this.ftpClient.makeDirectory(LocaltoFTP(dirs[i]));// 创建目录
                        this.ftpClient.changeWorkingDirectory(LocaltoFTP(dirs[i]));// 进入新目录
                        System.out.println("当前目录：" + getWorkingDirectory());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isCreateSuccess;
    }


    /**
     * 返回上级目录，O
     *
     * @param num
     *            返回上一级目录的次数
     */
    public boolean toParentDir(int num) {
        boolean isToParentDir = false;
        if (null != this.ftpClient) {
            try {
                int Count = getWorkingDirectory().split("/").length - 1;
                System.out.println(Count);
                if (Count < num)
                    num = Count;
                for (int i = 0; i < num; i++) {
                    this.ftpClient.enterLocalPassiveMode();
                    this.ftpClient.changeToParentDirectory();
                    System.out.println("当前目录：" + getWorkingDirectory());
                    isToParentDir = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("返回上级目录时出错！");
            }
        }
        return isToParentDir;
    }


    /**
     * 列出所有文件与目录，O
     */
    public FTPFile[] getFiles() {
        FTPFile[] files = null;
        if (null != this.ftpClient) {
            try {
                this.ftpClient.enterLocalPassiveMode();
                files = this.ftpClient.listFiles();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return files;
    }


    /**
     * 列出所有目录，O
     */
    public FTPFile[] getDirs() {
        FTPFile[] dirs = null;
        if (null != this.ftpClient) {
            try {
                this.ftpClient.enterLocalPassiveMode();
                dirs = this.ftpClient.listDirectories();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return dirs;
    }


    /**
     * 列出所有文件名，O
     */
    public String[] getNames() {
        String[] names = null;
        if (null != this.ftpClient) {
            try {
                this.ftpClient.enterLocalPassiveMode();
                names = this.ftpClient.listNames();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return names;
    }


    /**
     * 获取当前目录，O
     */
    public String getWorkingDirectory() {
        String workingDirectory = null;
        if (null != baseWorkDirectory) {
            try {
                this.ftpClient.enterLocalPassiveMode();
                workingDirectory = FTPtoLocal(this.ftpClient.printWorkingDirectory());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return workingDirectory;
    }


    /**
     * 重命名文件,O
     *
     * @param oldFileName
     *            --原文件名
     * @param newFileName
     *            --新文件名
     */
    public boolean setRenameFile(String oldFileName, String newFileName) {
        boolean rename = false;
        if (null != this.ftpClient && this.ftpClient.isConnected()) {
            try {
                this.ftpClient.enterLocalPassiveMode();
                rename = this.ftpClient.rename(LocaltoFTP(oldFileName), LocaltoFTP(newFileName));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return rename;
    }


    /**
     * 设置传输文件的类型[文本文件或者二进制文件]，O
     *
     * @param fileType
     *            --FTPClient.BINARY_FILE_TYPE,FTPClient.ASCII_FILE_TYPE
     */
    public boolean setFileType(int fileType) {
        boolean isSetFileType = false;
        if (null != this.ftpClient) {
            try {
                this.ftpClient.enterLocalPassiveMode();
                this.ftpClient.setFileType(fileType);
                isSetFileType = true;
                System.out.println("设置传输文件");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isSetFileType;
    }


    /**
     * 设置编码字符集，O
     *
     * @param localCharset
     *            本地编码
     * @param serverCharset
     *            FTP编码（一般是ISO）
     * @return
     */
    public boolean setCharset(String localCharset, String serverCharset) {
        this.localCharset = localCharset;// 本地编码字符串编码
        this.serverCharset = serverCharset;// FTP服务器端字符串编码
        return true;
    }


    /**
     * 退出并关闭FTP连接，O
     *
     */
    public boolean close() {
        boolean isLogout = false;
        if (null != this.ftpClient && this.ftpClient.isConnected()) {
            try {
                this.ftpClient.enterLocalPassiveMode();
                isLogout = this.ftpClient.logout();// 退出FTP服务器
                System.out.println("关闭");
            } catch (IOException e) {
                System.out.println("关闭异常");
                e.printStackTrace();
            } finally {
                try {
                    ftpClient.disconnect();// 关闭FTP服务器的连接
                } catch (IOException e) {
                    System.out.println("关闭连接异常");
                    e.printStackTrace();
                }
            }
        }
        return isLogout;
    }


    /**
     * 本地编码转FTP编码，O
     */
    private String LocaltoFTP(String mStr) throws IOException {
        return new String(mStr.getBytes(localCharset), serverCharset);
    }


    /**
     * FTP编码转本地编码，O
     */
    private String FTPtoLocal(String mStr) throws IOException {
        return new String(mStr.getBytes(serverCharset), localCharset);
    }


}
