package com.example.foodrecommend.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@Api(value = "上传文件", tags = "上传文件")
@RequestMapping("/upload")
public class UploadController {

    @ApiOperation("上传图片")
    @PostMapping("/images")
    public String upload(@RequestParam("file") MultipartFile file, ServletRequest request) throws IOException {
        // 获取图片数据
        byte[] bytes = file.getBytes();

        // 源文件名
        String originalFilename = file.getOriginalFilename();
        // 文件后缀
        String imgSuffix = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        // 新文件名
        String newFileName = UUID.randomUUID().toString() + "." + imgSuffix;

        // 将图片数据写入本地文件
        // TODO 此处的路径先写硬编码
        FileOutputStream out = new FileOutputStream("/upload/images/" + newFileName);
        out.write(bytes);
        out.close();

        // 获取图片文件的绝对路径
        String filePath = "http://localhost:8080/upload/images/" + newFileName;

        // 返回图片文件的绝对路径
        return filePath;
    }
}