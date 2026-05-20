package org.dromara.huahao.domain.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.huahao.domain.HAcceptanceForm;

import java.util.Date;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@AutoMapper(target = HAcceptanceForm.class, reverseConvertGenerate = false)
public class HAcceptanceFormBo {

    /**
     * 主键
     */
    private Long id;

    /**
     * 租户编号
     */
    private String tenantId;

    /**
     * 验收单号
     */
    private String accNo;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 状态（0：未开始，1：收料中，2：已完成）
     */
    private String status;

    /**
     * 创建者
     */
    private Long createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新者
     */
    private Long updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 创建部门
     */
    private Long createDept;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    private String delFlag;

    /**
     * 请求参数（用于接收日期范围等查询条件）
     */
    private Map<String, Object> params;
}
