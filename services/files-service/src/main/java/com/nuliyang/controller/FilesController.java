package com.nuliyang.controller;


import com.nuliyang.JwtUtil;
import com.nuliyang.result.Result;
import com.nuliyang.service.FilesService;
import com.nuliyang.vo.FilesVo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FilesController {

    private final FilesService fileService;


    @PostMapping("/upload")
    public Result<List<FilesVo>> upload(
            @RequestParam("files") MultipartFile[] files,
            HttpServletRequest request) {




        // 参数校验
        if (files == null || files.length == 0) {
            return Result.error("请选择要上传的文件");
        }

        // 验证单个文件是否为空
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                return Result.error("存在空文件，请重新选择");
            }
        }

        //从token中获取userId
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // 去掉 "Bearer "
            Long userId = Long.valueOf(JwtUtil.parseToken(token).getSubject());


            List<FilesVo> list = fileService.uploadFiles(userId,  files);

            // 验证业务逻辑执行结果
            if (list == null) {
                return Result.error("文件上传失败");
            }

            return Result.success(list);
        }
        return Result.error("token失效");
    }



    @PostMapping("/admin_nuliyang/upload/{userId}")
    public Result<List<FilesVo>> adminUpload(
            @RequestParam("files") MultipartFile[] files,
            @PathVariable Long userId) {




        // 参数校验
        if (files == null || files.length == 0) {
            return Result.error("请选择要上传的文件");
        }

        // 验证单个文件是否为空
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                return Result.error("存在空文件，请重新选择");
            }
        }


        List<FilesVo> list = fileService.uploadFiles(userId,  files);

        // 验证业务逻辑执行结果
        if (list == null) {
            return Result.error("文件上传失败");
        }

        return Result.success(list);

    }



    @GetMapping("/getAvatar")
    public Result<String> getAvatar(HttpServletRequest request) {

        //从token中获取userId
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // 去掉 "Bearer "
            Long userId = Long.valueOf(JwtUtil.parseToken(token).getSubject());

            return Result.success(fileService.getAvatarById(userId));
        }
        return Result.error("token失效");
    }


    @GetMapping("/getAvatarByUserId/{userId}")
    public Result<String> getAvatarByUserId(
            @PathVariable Long userId) {

        return Result.success(fileService.getAvatarById(userId));
    }

}
