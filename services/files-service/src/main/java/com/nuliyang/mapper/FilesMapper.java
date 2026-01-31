package com.nuliyang.mapper;


import com.nuliyang.entity.FilesEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface FilesMapper {


    /**
     * 添加文件
     * @param filesEntity
     */
    @Insert("insert into file_storage (id, storage_path, access_url) " +
            "VALUES " +
    "(#{id},#{storagePath},#{accessUrl})")
    void insertFiles(FilesEntity filesEntity);


    /**
     * 根据用户id获取访问URL
     * @param userId
     * @return
     */
    @Select("select access_url from file_storage where id = #{userId}")
    String getAccessUrlById(Long userId);


    /**
     * 更新文件
     * @param filesEntity
     */
    @Update("update file_storage set storage_path = #{storagePath}, access_url = #{accessUrl} where id = #{id}")
    void updateFiles(FilesEntity filesEntity);
}
