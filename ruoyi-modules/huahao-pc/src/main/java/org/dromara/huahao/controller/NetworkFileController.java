package org.dromara.huahao.controller;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.huahao.domain.RWithErrorCode;
import org.dromara.huahao.domain.bo.NetworkFileBo;
import org.dromara.huahao.domain.vo.NetworkFileVo;
import org.dromara.huahao.exception.ReceivingException;
import org.dromara.huahao.service.NetworkFileService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/huahao/network/file")
public class NetworkFileController extends BaseController {

    private final NetworkFileService networkFileService;

    /**
     * 查询网络共享文件配置列表
     */
    @GetMapping("/list")
    public TableDataInfo<NetworkFileVo> list(NetworkFileBo bo, PageQuery pageQuery) {
        return networkFileService.queryPageList(bo, pageQuery);
    }

    /**
     * 获取网络共享文件配置详细信息
     *
     * @param id
     */
    @GetMapping("/{id}")
    public R<NetworkFileVo> getInfo(@PathVariable @NotNull(message = "主键不能为空") Long id) {
        return R.ok(networkFileService.queryById(id));
    }

    /**
     * 新增网络共享文件配置
     */
    @Log(title = "网络文件", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public RWithErrorCode<Void> add(@Validated(AddGroup.class) @RequestBody NetworkFileBo bo) {
        try {
            networkFileService.insertByBo(bo);
            return RWithErrorCode.ok("操作成功", null);
        } catch (ReceivingException e) {
            return RWithErrorCode.fail(e.getMessage(), e.getErrorCode());
        }
    }

    /**
     * 修改网络共享文件配置
     */
    @Log(title = "网络文件", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody NetworkFileBo bo) {
        return toAjax(networkFileService.updateByBo(bo));
    }

    /**
     *批量删除共享文件配置
     *
     * @param ids 主键串
     */
    @Log(title = "网络文件", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空") @PathVariable Long[] ids) {
        return toAjax(networkFileService.deleteByIds(Arrays.asList(ids)));
    }

}
