package org.dromara.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_version")
public class SysVersion {

    @TableId(value = "version_id", type = IdType.AUTO)
    private Long versionId;

    private String versionNumber;

    private Date updateTime;

    private String content;

    private String url;

    private String delFlag;
}
