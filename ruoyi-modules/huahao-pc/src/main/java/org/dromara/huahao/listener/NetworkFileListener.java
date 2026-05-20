package org.dromara.huahao.listener;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.excel.core.ExcelResult;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.huahao.domain.HAcceptanceDetail;
import org.dromara.huahao.domain.vo.HAcceptanceDetailVo;
import org.dromara.huahao.domain.vo.NetworkFileVo;
import org.dromara.huahao.service.IAcceptanceDetailService;
import org.dromara.huahao.service.IAcceptanceFormService;
import org.dromara.huahao.service.NetworkFileService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class NetworkFileListener {
    @PostConstruct
    public void init() {
        try {
            initializeWatcher();
            startWatcherThread();
        } catch (Exception e) {
            System.err.println("文件监听器初始化失败: " + e.getMessage());
        }
    }

    /**
     * 启动监听线程
     */
    private void startWatcherThread() {
        new Thread(() -> {
            try {
                startWatching();
            } catch (Exception e) {
                log.error("启动监听失败: {}", e.getMessage(), e);
            }
        }, "NetworkFileWatcher").start();
    }

    /**
     * 重新初始化监听器（当数据库配置改变时调用）
     */
    public synchronized void reinitializeWatcher() {
        try {
            log.info("重新初始化文件监听器...");

            // 停止当前监听
            stopCurrentWatcher();

            // 重新初始化
            initializeWatcher();

            // 重启监听线程
            startWatcherThread();

            log.info("文件监听器重新初始化完成");
        } catch (Exception e) {
            log.error("重新初始化监听器失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 停止当前监听器
     */
    private void stopCurrentWatcher() {
        running = false;

        if (watchService != null) {
            try {
                watchService.close();
            } catch (IOException e) {
                log.error("关闭WatchService时出错: {}", e.getMessage(), e);
            }
        }

        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        // 重置状态
        running = true;
        executor = Executors.newSingleThreadExecutor();
    }

    private void initializeWatcher() throws IOException {
        // 在@PostConstruct中进行初始化，此时@Value已经注入
//        if (networkFolderPath == null || networkFolderPath.trim().isEmpty()) {
//            // 设置默认测试路径
//            networkFolderPath = "D:\\test\\watch_folder";
//            log.info("警告: 未配置网络文件夹路径，使用默认路径: {}", networkFolderPath);
//        }
        NetworkFileVo networkFileVo = networkFileService.getAcceptanceFilePath();
        String networkFolderPath = networkFileVo.getFilePath();
        // 检查路径是否发生变化
        if (currentListeningPath != null && currentListeningPath.equals(networkFolderPath)) {
            log.info("监听路径未发生变化: {}", networkFolderPath);
            return;
        }

        this.folderPath = Paths.get(networkFolderPath);
        this.currentListeningPath = networkFolderPath;

        // 检查文件夹是否存在
        if (!Files.exists(folderPath)) {
            // 尝试创建文件夹
            try {
                Files.createDirectories(folderPath);
                log.info("创建文件夹: {}", networkFolderPath);
            } catch (IOException e) {
                throw new IOException("无法创建文件夹: " + networkFolderPath, e);
            }
        }

        if (!Files.isDirectory(folderPath)) {
            throw new IOException("路径不是文件夹: " + networkFolderPath);
        }
        // 重新创建 WatchService
        if (watchService != null) {
            try {
                watchService.close();
            } catch (IOException e) {
                log.warn("关闭旧的WatchService时出错: {}", e.getMessage());
            }
        }
        this.watchService = FileSystems.getDefault().newWatchService();
        this.executor = Executors.newSingleThreadExecutor();

        // 注册监听事件
        folderPath.register(watchService,
            StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_MODIFY,
            StandardWatchEventKinds.ENTRY_DELETE);

        log.info("文件监听器初始化完成，监听路径: {}", folderPath);
    }

    private WatchService watchService;
    private Path folderPath;
    private ExecutorService executor;
    private volatile boolean running = true;
    // 缓存当前监听的路径，用于比较是否需要重新初始化
    private volatile String currentListeningPath;
//    @Value("${huahao.network-folder-acceptance-path}")
//    private String networkFolderPath;

    private final IAcceptanceDetailService acceptanceDetailService;

    private final IAcceptanceFormService acceptanceFormService;
    private final NetworkFileService networkFileService;

    public NetworkFileListener(IAcceptanceDetailService acceptanceDetailService, IAcceptanceFormService acceptanceFormService, NetworkFileService networkFileService) {
        this.acceptanceDetailService = acceptanceDetailService;
        this.acceptanceFormService = acceptanceFormService;
        this.networkFileService = networkFileService;
        // 构造函数中不进行文件系统操作，避免@Value未注入的问题
        this.folderPath = null; // 初始化为null，在@PostConstruct中设置
    }

    public void startWatching() {
        log.info("开始监听文件夹: {}", folderPath);

        executor.submit(() -> {
            try {
                while (running && !Thread.currentThread().isInterrupted()) {
                    WatchKey key = watchService.poll(1, TimeUnit.SECONDS);
                    if (key == null) {
                        continue;
                    }

                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();

                        if (kind == StandardWatchEventKinds.OVERFLOW) {
                            log.info("事件溢出");
                            continue;
                        }

                        // 获取文件名
                        @SuppressWarnings("unchecked")
                        WatchEvent<Path> ev = (WatchEvent<Path>) event;
                        Path filename = ev.context();
                        Path fullPath = folderPath.resolve(filename);

                        log.info("检测到文件变化: {} -> {}", kind.name(), fullPath);

                        // 处理文件创建事件
                        if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                            handleFileCreated(fullPath);
                        }
                    }
                    key.reset();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("监听被中断");
            } catch (Exception e) {
                log.error("监听过程中出错: {}", e.getMessage(), e);
            }
        });
    }

    private void handleFileCreated(Path filePath) {
        try {
            log.info("新文件创建: {}", filePath);
            log.info("文件大小: {} 字节", Files.size(filePath));

            // 获取文件基本信息
            String fileName = filePath.getFileName().toString();
            String fileExtension = getFileExtension(fileName);
            long fileSize = Files.size(filePath);

            log.info("文件名: {}", fileName);
            log.info("文件扩展名: {}", fileExtension);
            log.info("文件大小: {} 字节", fileSize);

            //根文件类型进行不同处理
            switch (fileExtension.toLowerCase()) {
                case ".txt", ".jpg", ".jpeg", ".png", ".pdf", ".xml", ".doc", ".docx":
                    break;
                case ".xlsx", ".xls":
                    handleExcelFile(filePath);
                    break;
                default:
                    handleGenericFile(filePath);
                    break;
            }

        } catch (IOException e) {
            log.error("处理文件时出错: {}", e.getMessage(), e);
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex) : "";
    }

    /**
     * 将文件转换为InputStream
     */
    private InputStream convertToInputStream(Path filePath) {
        int maxRetries = 3;
        int retryDelay = 1000; // 1秒

        for (int i = 0; i < maxRetries; i++) {
            try {
                return Files.newInputStream(filePath);
            } catch (IOException e) {
                if (e.getMessage().contains("另一个程序正在使用此文件")) {
                    log.warn("文件被占用，第{}次重试: {}", i + 1, filePath);
                    if (i < maxRetries - 1) {
                        try {
                            Thread.sleep(retryDelay);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                } else {
                    log.error("文件转换为InputStream失败: {}", e.getMessage(), e);
                    break;
                }
            }
        }
        return null;
    }

    /**
     * 处理Excel文件
     */
    private void handleExcelFile(Path filePath) throws IOException {
        // 可以在这里添加Excel解析逻辑
        // 例如使用Apache POI解析Excel文件
        try (InputStream inputStream = convertToInputStream(filePath)) {
            if (inputStream == null) {
                log.warn("无法获取文件输入流: {}", filePath);
                return;
            }

            // Excel处理逻辑
            log.info("Excel文件流已获取");

            try {
                ExcelResult<HAcceptanceDetailVo> excelResult = ExcelUtil.importExcel(inputStream, HAcceptanceDetailVo.class, true);

                if (excelResult != null && excelResult.getList() != null) {
                    List<HAcceptanceDetail> list = MapstructUtils.convert(excelResult.getList(), HAcceptanceDetail.class);
                    if (!CollectionUtils.isEmpty(list)) {
                        acceptanceDetailService.saveBatch(list);
                        // 一个验收单号文件里只会有一个验收单号，所以直接取第一个
                        acceptanceFormService.saveDetailList(list.get(0), "");
                    }
                }
            } catch (Exception e) {
                log.error("Excel解析失败: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * 处理其他类型文件
     */
    private void handleGenericFile(Path filePath) throws IOException {
        log.info("处理通用文件: {}", filePath.getFileName());
        log.info("文件类型: {}", Files.probeContentType(filePath));

        //可以移动文件到指定目录
        // Path targetPath = Paths.get("D:\\processed\\" + filePath.getFileName());
        // Files.move(filePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
    }

    public void stopWatching() {
        running = false;
        try {
            watchService.close();
        } catch (IOException e) {
            log.error("关闭WatchService时出错: {}", e.getMessage(), e);
        }
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("监听已停止");
    }

    /**
     * 定期检查数据库配置是否发生变化
     */
//    @Scheduled(fixedDelay = 30000) // 每30秒检查一次
    public void checkConfigurationChange() {
        try {
            NetworkFileVo currentConfig = networkFileService.getAcceptanceFilePath();
            String currentPath = currentConfig.getFilePath();

            if (currentListeningPath == null || !currentListeningPath.equals(currentPath)) {
                log.info("检测到监听路径配置变更: {} -> {}", currentListeningPath, currentPath);
                reinitializeWatcher();
            }
        } catch (Exception e) {
            log.error("检查配置变更时出错: {}", e.getMessage(), e);
        }
    }
}
