package com.example.foodrecommend.controller;

import com.example.foodrecommend.utils.QiniuUtils;
import com.example.foodrecommend.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@Api(value = "文件上传", tags = "文件上传")
@RequestMapping("/file")
@Slf4j
public class FileController {

    /**
     * 上传文件存储至七牛云
     *
     * @param file 文件
     * @return 文件名
     */
    @ApiOperation("上传文件")
    @PostMapping("/upload")
    public R upload(@RequestParam MultipartFile file) {
        try {
            // 获取原始文件名
            String originalFilename = file.getOriginalFilename();
            int lastIndexOf = originalFilename.lastIndexOf(".");
            // 获取文件后缀
            String suffix = originalFilename.substring(lastIndexOf - 1);
            // 使用UUID随机产生文件名称，防止同名文件覆盖
            String fileName = UUID.randomUUID().toString() + suffix;
            QiniuUtils.upload2Qiniu(file.getBytes(), fileName);
            // 上传成功
            // 如果上传文件为图片，前端需要加上前缀，前缀位置在 application.yaml 中的 qiNiuYun.link
            return R.success(fileName);
        } catch (Exception e) {
            log.error("上传图片时出现错误", e);
            return R.failure(500, "图片上传失败");
        }
    }

    /**
     * 通过文件名删除文件
     *
     * @param name 要删除的文件名
     * @return 操作结果提示
     */
    @ApiOperation("删除文件")
    @GetMapping("/delete/{name}")
    public R delete(@PathVariable String name) {
        try {
            // 删除七牛云服务器上的图片
            QiniuUtils.deleteFileFromQiniu(name);
            return R.success("删除成功");
        } catch (Exception e) {
            log.error("删除文件时出现错误", e);
            return R.failure(500, "删除失败");
        }
    }

}