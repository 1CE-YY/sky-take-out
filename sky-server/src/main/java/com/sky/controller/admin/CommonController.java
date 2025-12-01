package com.sky.controller.admin;


import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Api(tags = "公共模块")
@Slf4j
@RequiredArgsConstructor
public class CommonController {

    private final AliOssUtil aliOssUtil;

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @ApiOperation("文件上传")
    @PostMapping("/upload")
    public Result<String> uploadFile(MultipartFile file) {
        log.info("文件上传：{}", file);

        try {
            // 验证文件是否为空
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("上传文件不能为空");
            }

            String originalFilename = file.getOriginalFilename();
            // 验证文件名
            if (originalFilename == null || originalFilename.isEmpty()) {
                throw new IllegalArgumentException("文件名不能为空");
            }

            // 验证文件扩展名
            int lastDotIndex = originalFilename.lastIndexOf(".");
            if (lastDotIndex == -1 || lastDotIndex == originalFilename.length() - 1) {
                throw new IllegalArgumentException("文件必须包含有效的扩展名");
            }
            String subfix = originalFilename.substring(lastDotIndex);
            String objectName = UUID.randomUUID().toString() + subfix;

            String fileUrl = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(fileUrl);

        } catch (IllegalArgumentException e) {
            log.warn("文件参数错误：{}", e.getMessage());
            return Result.error(e.getMessage());
        } catch (Exception e) {
            log.error("文件上传失败：{}", e.getMessage());
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }

    }
}
