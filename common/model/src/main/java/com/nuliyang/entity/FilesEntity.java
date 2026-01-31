package com.nuliyang.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@TableName("file_storage")
@Accessors(chain = true)
public class FilesEntity {

    @TableId
    @TableField("id")
    private Long id;

    @TableField("storage_path")
    private String storagePath;

    @TableField("access_url")
    private String accessUrl;


}
