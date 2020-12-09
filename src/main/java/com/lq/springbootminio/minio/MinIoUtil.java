package com.lq.springbootminio.minio;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.List;

/**
 * description
 *
 * @author quan.luo@hand-china.com 2020/11/28 15:54
 */
@Slf4j
@Component
public class MinIoUtil {

    /**
     * minio地址+端口号
     */
    @Value("${minio.url}")
    private String url;

    /**
     * minio用户名
     */
    @Value("${minio.accessKey}")
    private String accessKey;

    /**
     * minio密码
     */
    @Value("${minio.secretKey}")
    private String secretKey;

    /**
     * 文件桶的名称
     */
    @Value("${minio.bucketName}")
    private String bucketName;

    private static MinioClient minioClient;

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        try {
            minioClient = MinioClient.builder()
                    .endpoint(url)
                    .credentials(accessKey, secretKey)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("初始化minio配置异常: ", e.fillInStackTrace());
        }
    }

    /**
     * 判断 bucket是否存在
     *
     * @param bucketName: 桶名
     * @return boolean
     */
    public static boolean bucketExists(String bucketName) {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("bucket是否存在异常: ", e.fillInStackTrace());
        }
        return false;
    }

    /**
     * 判断 bucket是否存在
     *
     * @param bucketName 桶名
     * @param region     限制
     * @return 是否
     */
    public static boolean bucketExists(String bucketName, String region) {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).region(region).build());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("bucket是否存在异常: ", e.fillInStackTrace());
        }
        return false;
    }

    /**
     * 创建 bucket
     *
     * @param bucketName: 桶名
     */
    public static void createBucket(String bucketName) {
        try {
            boolean isExist = bucketExists(bucketName);
            if (!isExist) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("创建 bucket异常: ", e.fillInStackTrace());
        }
    }

    /**
     * 获取全部bucket
     *
     * @return java.util.List<io.minio.messages.Bucket>
     */
    @SneakyThrows(Exception.class)
    public static List<Bucket> getAllBuckets() {
        return minioClient.listBuckets();
    }


    /**
     * 创建 bucket
     *
     * @param bucketName 桶名
     * @param region     限制
     */
    public static void makeBucket(String bucketName, String region) {
        try {
            boolean isExist = bucketExists(bucketName, region);
            if (!isExist) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).region(region).build());
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("创建 bucket异常: ", e.fillInStackTrace());
        }
    }

    /**
     * 删除 bucket
     *
     * @param bucketName 桶名
     */
    public static void deleteBucket(String bucketName) {
        try {
            minioClient.deleteBucketEncryption(
                    DeleteBucketEncryptionArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("删除 bucket异常: ", e.fillInStackTrace());
        }
    }

    /**
     * 删除 bucket
     *
     * @param bucketName 桶名
     * @param region     限制
     */
    public static void deleteBucket(String bucketName, String region) {
        try {
            minioClient.deleteBucketEncryption(
                    DeleteBucketEncryptionArgs.builder().bucket(bucketName).bucket(region).build());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("删除 bucket异常: ", e.fillInStackTrace());
        }
    }

    /**
     * 文件上传
     *
     * @param bucketName 桶名
     * @param objectName 文件名
     * @param filePath   地址
     */
    public static void uploadObject(String bucketName, String objectName, String filePath) {
        putObject(bucketName, null, objectName, filePath);
    }

    /**
     * 文件上传
     *
     * @param bucketName 桶名
     * @param objectName 文件名
     * @param filePath   地址
     */
    public static void uploadObject(String bucketName, String region, String objectName, String filePath) {
        putObject(bucketName, region, objectName, filePath);
    }

    /**
     * 文件上传
     *
     * @param multipartFile 文件信息
     * @param bucketName    桶名
     * @return 文件地址
     */
    public static String uploadObject(MultipartFile multipartFile, String bucketName) {
        // bucket 不存在，创建
        if (!bucketExists(bucketName)) {
            createBucket(bucketName);
        }
        return putObject(multipartFile, bucketName, null);
    }

    /**
     * 文件上传
     *
     * @param multipartFile 文件
     * @param bucketName    桶名
     * @return 文件地址
     */
    public static String uploadObject(MultipartFile multipartFile, String bucketName, String region) {
        // bucket 不存在，创建
        if (!bucketExists(bucketName, region)) {
            makeBucket(bucketName, region);
        }
        return putObject(multipartFile, bucketName, region);
    }


    /**
     * 获取minio文件的下载地址
     *
     * @param bucketName 桶名
     * @param fileName   文件名
     * @return 下载地址
     */
    @SneakyThrows(Exception.class)
    public static String getFileUrl(String bucketName, String fileName) {
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .bucket(bucketName).object(fileName).method(Method.GET).build());
    }

    /**
     * 获取文件下载地址
     *
     * @param bucketName 桶名
     * @param region     范围
     * @param fileName   文件名
     * @return
     */
    public static String getObjectUrl(String bucketName, String region, String fileName) {
        String objectUrl = null;
        try {
            objectUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .region(region)
                            .object(fileName)
                            .build());
        } catch (Exception e) {
            e.fillInStackTrace();
            log.error("获取文件下载地址异常:", e);
        }
        return objectUrl;
    }

    /**
     * 删除文件
     *
     * @param bucketName 桶名
     * @param objectName 文件名
     */
    public static void deleteObject(String bucketName, String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (Exception e) {
            e.fillInStackTrace();
            log.error("删除文件异常:", e);
        }
    }

    /**
     * 删除文件
     *
     * @param bucketName 桶名
     * @param region     限制
     * @param objectName 文件名
     */
    public static void deleteObject(String bucketName, String region, String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketName).region(region).object(objectName).build());
        } catch (Exception e) {
            e.fillInStackTrace();
            log.error("删除文件异常:", e);
        }
    }

    /**
     * 上传文件
     *
     * @param multipartFile 文件信息
     * @param bucketName    桶名
     * @return 地址
     */
    private static String putObject(MultipartFile multipartFile, String bucketName, String region) {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            // 上传文件的名称
            String fileName = multipartFile.getOriginalFilename();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .region(region)
                    .object(fileName)
                    .stream(inputStream, multipartFile.getSize(), ObjectWriteArgs.MIN_MULTIPART_SIZE)
                    .contentType(multipartFile.getContentType())
                    .build());
            // 返回访问路径
            return getObjectUrl(bucketName, region, fileName);
        } catch (Exception e) {
            e.fillInStackTrace();
            log.error("上传文件异常:", e);
        }
        return null;
    }

    /**
     * 上传文件
     *
     * @param bucketName 桶名
     * @param region     限制
     * @param objectName 文件名
     * @param filePath   文件地址
     */
    private static String putObject(String bucketName, String region, String objectName, String filePath) {
        try {
            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(bucketName)
                            .region(region)
                            .object(objectName)
                            .filename(filePath)
                            .build());
            // 返回访问路径
            return getObjectUrl(bucketName, region, objectName);
        } catch (Exception e) {
            e.fillInStackTrace();
            log.error("上传文件异常:", e);
        }
        return null;
    }
}
