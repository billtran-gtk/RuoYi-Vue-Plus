package org.dromara.huahao.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.huahao.domain.NetworkFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@AutoMapper(target = NetworkFile.class, reverseConvertGenerate = false)
public class NetworkFileBo {
    /**
     * id
     */
    private Long id;
    /**
     * 文件路径
     */
    @NotNull(message = "文件路径不能为空")
    private String filePath;

    /**
     * 文件类型
     */
    @NotNull(message = "文件类型不能为空")
    private String type;
    /**
     * 是否启用
     */
    private String isEnable;
}
