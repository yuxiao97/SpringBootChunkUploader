package com.yuxiao.springboot.chunkuploader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @Auther: Yuxiao
 * @Date: 2018/6/25 21:53
 * @Description: 文件分片上传处理
 */
public class MultipartFileDealUtil {


    @Value("${file.upload.root-path}")
    private static String rootPath;

    @Value("${file.upload.chunk-size}")
    private static long CHUNK_SIZE;


    /**
     *
     * @param param
     * @param savePath
     * @return
     * @throws IOException
     */
    public static boolean uploadFileRandomAccessFile(MultipartFileParam param, String savePath) throws IOException {
        String path = StringUtils.isEmpty(savePath) ? rootPath : savePath;
        String fileName = param.getName();
        File tmpDir = new File(path);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        String finalFile = tmpDir+File.separator + fileName;
        File tmpFile = new File(finalFile);
        RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
        long offset = param.getChunkSize() * param.getChunk();
        //定位到该分片的偏移量
        accessTmpFile.seek(offset);
        //写入该分片数据
        accessTmpFile.write(param.getFile().getBytes());
        // 释放
        accessTmpFile.close();
        return checkAndSetUploadProgress(param, path);
    }


    /**
     *
     * @param param
     * @param uploadDirPath
     * @return
     * @throws IOException
     */
    private static boolean checkAndSetUploadProgress(MultipartFileParam param, String uploadDirPath) throws IOException {
        String fileName = param.getName();
        File confFile = new File(uploadDirPath, fileName + ".conf");
        RandomAccessFile accessConfFile = new RandomAccessFile(confFile, "rw");
        //把该分段标记为 true 表示完成
        accessConfFile.setLength(param.getChunks());
        accessConfFile.seek(param.getChunk());
        accessConfFile.write(Byte.MAX_VALUE);
        //completeList 检查是否全部完成,如果数组里是否全部都是(全部分片都成功上传)
        byte[] completeList = FileCopyUtils.copyToByteArray(confFile);
        byte isComplete = Byte.MAX_VALUE;
        for (int i = 0; i < completeList.length && isComplete == Byte.MAX_VALUE; i++) {
            //与运算, 如果有部分没有完成则 isComplete 不是 Byte.MAX_VALUE
            isComplete = (byte) (isComplete & completeList[i]);
        }
        accessConfFile.close();
        return isComplete == Byte.MAX_VALUE;
    }


    /**
     *
     * @param param
     */
    public static void cleanTempFile(MultipartFileParam param, String savePath){
        if(param != null){
            String path = StringUtils.isEmpty(savePath) ? rootPath : savePath;
            File file = new File(path+File.separator+param.getName());
            file.delete();
            file = new File(path+File.separator+param.getName()+".conf");
            file.delete();
        }
    }

}
