package org.dromara.huahao.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.huahao.domain.NetworkFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = NetworkFile.class)
public class NetworkFileVo extends BaseEntity {
    /**
     * id
     */
    private Long id;
    /**
     * 文件路径
     */
    private String filePath;
    /**
     * 文件类型
     */
    private String type;
    /**
     * 是否启用
     */
    private String isEnable;
}
