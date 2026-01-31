package com.nuliyang.service;

import com.nuliyang.vo.FilesVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FilesService {


    /**
     * 文件上传
     * @param files
     * @return
     */
    List<FilesVo> uploadFiles(Long userId, MultipartFile[] files);


    /**
     * 根据id获取用户头像
     * @param userId
     * @return
     */
    String getAvatarById(Long userId);
}
