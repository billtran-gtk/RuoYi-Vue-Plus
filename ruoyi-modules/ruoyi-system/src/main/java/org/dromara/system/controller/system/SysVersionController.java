package org.dromara.system.controller.system;

import cn.hutool.core.io.FileUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.system.domain.bo.SysVersionBo;
import org.dromara.system.domain.vo.SysVersionVo;
import org.dromara.system.service.ISysVersionService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * app安装包版本管理
 *
 * @author Lion Li
 * @date 2026-03-13
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/version")
public class SysVersionController extends BaseController {

    private final ISysVersionService sysVersionService;
    /**
     * 上传文件存储路径
     */
    @Value("${huahao.upload-apk-path}")
    private String profile;

    /**
     * 查询app安装包版本管理列表
     */
    @GetMapping("/list")
    public TableDataInfo<SysVersionVo> list(SysVersionBo bo, PageQuery pageQuery) {
        return sysVersionService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出app安装包版本管理列表
     */
    @Log(title = "app安装包版本管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SysVersionBo bo, HttpServletResponse response) {
        List<SysVersionVo> list = sysVersionService.queryList(bo);
        ExcelUtil.exportExcel(list, "app安装包版本管理", SysVersionVo.class, response);
    }

    /**
     * 获取app安装包版本管理详细信息
     *
     * @param versionId 主键
     */
    @GetMapping("/{versionId}")
    public R<SysVersionVo> getInfo(@NotNull(message = "主键不能为空")
                                   @PathVariable Long versionId) {
        return R.ok(sysVersionService.queryById(versionId));
    }

    /**
     * 新增app安装包版本管理
     */
    @Log(title = "app安装包版本管理", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysVersionBo bo) {
        return toAjax(sysVersionService.insertByBo(bo));
    }

    /**
     * 修改app安装包版本管理
     */
    @Log(title = "app安装包版本管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysVersionBo bo) {
        return toAjax(sysVersionService.updateByBo(bo));
    }

    /**
     * 删除app安装包版本管理
     *
     * @param versionIds 主键串
     */
    @Log(title = "app安装包版本管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{versionIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] versionIds) {
        return toAjax(sysVersionService.deleteWithValidByIds(List.of(versionIds), true));
    }

    /**
     * APK文件上传
     */
    @PostMapping("/upload")
    @ResponseBody
    public R<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("versionNumber") String versionNumber, HttpServletRequest request) throws IOException {
        try {
            //校验文件是否为空
            if (file.isEmpty()) {
                return R.fail("上传文件不能为空");
            }
            //校验文件类型（APK）
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.endsWith(".apk")) {
                return R.fail("仅支持APK格式文件上传");
            }
            //校验文件大小（100MB）
            long fileSize = file.getSize();
            long maxSize = 100 * 1024 * 1024; // 100MB
            if (fileSize > maxSize) {
                return R.fail("文件大小不能超过100MB");
            }
            //构建文件存储路径
            // 日期目录
            String datePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
            // 生成唯一文件名（避免重复）
            String fileName = versionNumber + ".apk";
            // 完整存储路径
            String filePath = profile;
            File destFile = new File(filePath + fileName);
            // 创建目录（不存在则创建）
            FileUtil.mkdir(destFile.getParentFile());
            //保存文件
            file.transferTo(destFile);
            //构建访问URL（根据实际部署域名/端口调整）
            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            String fileUrl = "/profile/apk/" + fileName;
            //返回结果
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            return R.ok("上传成功", result);
        } catch (Exception e) {
            e.printStackTrace();
            return R.fail("上传失败：" + e.getMessage());
        }
    }
}
