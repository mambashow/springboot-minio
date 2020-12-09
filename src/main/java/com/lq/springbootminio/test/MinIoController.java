package com.lq.springbootminio.test;

import com.lq.springbootminio.minio.MinIoUtil;
import io.minio.messages.Bucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * description
 *
 * @author quan.luo@hand-china.com 2020/12/09 14:03
 */
@RestController
@RequestMapping("/minio")
public class MinIoController {

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


    @PostMapping(value = "/upload")
    public String upload(@RequestParam("file") MultipartFile file) throws Exception {
        String fileUrl = MinIoUtil.uploadObject(file, bucketName);
        return "文件下载地址：" + fileUrl;
    }

    /**
     * 获取全部桶
     *
     * @return
     */
    @GetMapping(value = "/list")
    public List<Bucket> list() {
        return MinIoUtil.getAllBuckets();
    }

    @GetMapping(value = "/download")
    public String download(@RequestParam("fileName") String fileName, HttpServletResponse response) {
        String fileUrl = MinIoUtil.getFileUrl(bucketName, fileName);
        return "文件下载地址：" + fileUrl;
    }

    @GetMapping(value = "/delete")
    public String delete(@RequestParam("fileName") String fileName) {
        MinIoUtil.deleteObject(bucketName, fileName);
        return "删除成功";
    }
}
