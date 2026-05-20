package org.dromara.system.controller.system;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * APK文件下载控制器
 *
 * @author ruoyi
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/apk")
public class SysApkDownloadController {

    private static final String APK_DIR = "static/apk/";

    /**
     * 获取APK文件列表
     */
    @GetMapping("/list")
    public R<List<Map<String, String>>> listApkFiles() {
        List<Map<String, String>> apkList = new ArrayList<>();
        try {
            Resource resource = new ClassPathResource(APK_DIR);
            if (resource.exists()) {
                Path apkPath = Paths.get(resource.getURI());
                Files.list(apkPath)
                    .filter(path -> path.toString().toLowerCase().endsWith(".apk"))
                    .forEach(path -> {
                        Map<String, String> apkInfo = new HashMap<>();
                        String fileName = path.getFileName().toString();
                        apkInfo.put("fileName", fileName);
                        apkInfo.put("downloadUrl", "/system/apk/download/" + fileName);
                        try {
                            apkInfo.put("fileSize", String.valueOf(Files.size(path)));
                        } catch (IOException e) {
                            apkInfo.put("fileSize", "0");
                        }
                        apkList.add(apkInfo);
                    });
            }
        } catch (Exception e) {
            log.error("获取APK文件列表失败", e);
        }
        return R.ok(apkList);
    }

    /**
     * 下载APK文件
     *
     * @param fileName APK文件名
     */
    @GetMapping("/download/{fileName}")
    public void downloadApk(@PathVariable String fileName, HttpServletResponse response) {
        try {
            // 安全检查：防止路径遍历攻击
            if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("非法的文件名");
                return;
            }

            // 检查文件扩展名
            if (!fileName.toLowerCase().endsWith(".apk")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("只支持APK文件下载");
                return;
            }

            Resource resource = new ClassPathResource(APK_DIR + fileName);
            if (!resource.exists()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("文件不存在");
                return;
            }

            // 设置响应头
            response.setContentType("application/vnd.android.package-archive");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
            response.setContentLengthLong(resource.contentLength());

            // 输出文件流
            try (InputStream inputStream = resource.getInputStream()) {
                StreamUtils.copy(inputStream, response.getOutputStream());
                response.flushBuffer();
            }

            log.info("APK文件下载成功: {}", fileName);
        } catch (Exception e) {
            log.error("APK文件下载失败: {}", fileName, e);
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("文件下载失败");
            } catch (IOException ex) {
                log.error("写入错误响应失败", ex);
            }
        }
    }

    /**
     * 获取最新的APK文件信息
     */
    @GetMapping("/latest")
    public R<Map<String, String>> getLatestApk() {
        try {
            Resource resource = new ClassPathResource(APK_DIR);
            if (resource.exists()) {
                Path apkPath = Paths.get(resource.getURI());
                Path latestFile = Files.list(apkPath)
                    .filter(path -> path.toString().toLowerCase().endsWith(".apk"))
                    .max((p1, p2) -> {
                        try {
                            return Files.getLastModifiedTime(p1).compareTo(Files.getLastModifiedTime(p2));
                        } catch (IOException e) {
                            return 0;
                        }
                    })
                    .orElse(null);

                if (latestFile != null) {
                    Map<String, String> apkInfo = new HashMap<>();
                    String fileName = latestFile.getFileName().toString();
                    apkInfo.put("fileName", fileName);
                    apkInfo.put("downloadUrl", "/system/apk/download/" + fileName);
                    apkInfo.put("fileSize", String.valueOf(Files.size(latestFile)));
                    return R.ok(apkInfo);
                }
            }
        } catch (Exception e) {
            log.error("获取最新APK文件失败", e);
        }
        return R.fail("未找到APK文件");
    }
}
