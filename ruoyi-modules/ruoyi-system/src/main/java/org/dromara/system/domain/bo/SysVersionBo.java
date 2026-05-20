package org.dromara.system.domain.bo;

import org.dromara.system.domain.SysVersion;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

import java.util.Date;

/**
 * app安装包版本管理业务对象 sys_version
 *
 * @author Lion Li
 * @date 2026-03-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysVersion.class, reverseConvertGenerate = false)
public class SysVersionBo extends BaseEntity {

    /**
     * 主键
     */
    @NotNull(message = "主键不能为空", groups = {EditGroup.class})
    private Long versionId;

    /**
     * 版本号
     */
    private String versionNumber;

    /**
     * 内容
     */
    private String content;

    /**
     * 路径
     */
    private String url;

    /**
     * 更新时间
     */
    private Date updateTime;
}
