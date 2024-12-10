package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * @ClassName: CommonController
 * @Package:com.sky.controller.admin
 * @Description:
 * @author: Zihao
 * @date: 2024/12/10 - 14:51
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "Common Interface")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    @ApiOperation("file upload")
    public Result<String> upload(MultipartFile file){
        log.info("file upload: {}", file);
        try {

            String originalFilename = file.getOriginalFilename();
            //Extract the original file name suffix
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String objectName =  UUID.randomUUID().toString() + extension;

            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(filePath);
        } catch (IOException e) {
            log.info("File upload failed:", e);
        }

        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }
}
