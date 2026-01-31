package com.nuliyang.service.serviceimpl;

import com.nuliyang.entity.FilesEntity;
import com.nuliyang.mapper.FilesMapper;
import com.nuliyang.service.FilesService;
import com.nuliyang.vo.FilesVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FilesServiceImpl implements FilesService {


    private final FilesMapper filesMapper;


    // 本地存储路径
    private static final String STORAGE_PATH = "D:/manabu/cloud/realtimecall_assets/";
    // 访问URL前缀
    private static final String ACCESS_URL_PREFIX = "http://localhost:7896/files/";



    /**
     * 上传文件
     * @param files
     * @return
     */
    @Override
    public List<FilesVo> uploadFiles(Long userId, MultipartFile[] files) {
        if (files == null || files.length == 0) {
            return new ArrayList<>();
        }

        List<FilesVo> result = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            try {
                // 生成唯一文件名（避免重名）
                String originalFilename = file.getOriginalFilename();
                String fileExtension = getFileExtension(originalFilename);
                String uniqueFileName = UUID.randomUUID().toString().replace("-", "") +
                        (fileExtension.isEmpty() ? "" : "." + fileExtension);

                // 构建本地存储路径
                String localFilePath = STORAGE_PATH + uniqueFileName;
                File storageFile = new File(localFilePath);

                // 确保目录存在
                storageFile.getParentFile().mkdirs();

                // 保存文件到本地
                file.transferTo(storageFile);

                // 构建访问URL
                String accessUrl = ACCESS_URL_PREFIX + uniqueFileName;

                // 创建 FilesEntity
                FilesEntity filesEntity = new FilesEntity()
                        .setId(userId)
                        .setStoragePath(localFilePath)
                        .setAccessUrl(accessUrl);

                //检查该userId是否已存有数据
                //若没有数据则插入
                if (filesMapper.getAccessUrlById(userId) == null) {

                    // 保存到数据库
                    filesMapper.insertFiles(filesEntity);
                }
                //若有数据则更新
                filesMapper.updateFiles(filesEntity);


                // 转换为 FilesVo 返回
                FilesVo filesVo = new FilesVo();
                filesVo.setAccessUrl(accessUrl);

                result.add(filesVo);

            } catch (IOException e) {
                throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
            }
        }

        return result;
    }


    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }






    /**
     * 根据用户ID获取头像URL
     * @param userId
     * @return
     */
    @Override
    public String getAvatarById(Long userId) {
        return filesMapper.getAccessUrlById(userId);
    }
}

