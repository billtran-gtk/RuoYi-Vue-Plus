package org.dromara.huahao.constant;

/**
 * 错误码常量
 * 用于前端国际化
 *
 * @author Kiro AI Assistant
 * @date 2026-02-10
 */
public class ErrorCodeConstants {

    /**
     * 导入数据失败
     * 前端国际化 key: huahao.common.importDataFailed
     */
    public static final String IMPORT_DATA_FAILED = "IMPORT_DATA_FAILED";

    /**
     * 导入成功
     * 前端国际化 key: huahao.common.importSuccess
     */
    public static final String IMPORT_SUCCESS = "IMPORT_SUCCESS";

    /**
     * 导入失败
     * 前端国际化 key: huahao.common.importFailed
     */
    public static final String IMPORT_FAILED = "IMPORT_FAILED";

    /**
     * 没有找到未提交的数据
     * 前端国际化 key: huahao.receiving.noUnsubmittedData
     */
    public static final String NO_UNSUBMITTED_DATA = "NO_UNSUBMITTED_DATA";

    /**
     * 生成ASN文件失败
     * 前端国际化 key: huahao.receiving.generateAsnFailed
     */
    public static final String GENERATE_ASN_FAILED = "GENERATE_ASN_FAILED";

    /**
     * 生成ASN文件失败, 请检查网络共享文件配置文件路径是否正确
     * 前端国际化 key: huahao.receiving.generateAsnFileFailed
     */
    public static final String GENERATE_ASN_FILE_FAILED = "GENERATE_ASN_FILE_FAILED";

    /**
     * 新增网络共享文件配置失败，已存在同类型配置，请勿重复添加
     * 前端国际化 key: huahao.receiving.networkFileFailed
     */
    public static final String NETWORK_FILE_ADD_FAILED = "NETWORK_FILE_ADD_FAILED";

    /**
     * 私有构造函数，防止实例化
     */
    private ErrorCodeConstants() {
        throw new IllegalStateException("Constant class");
    }

}
