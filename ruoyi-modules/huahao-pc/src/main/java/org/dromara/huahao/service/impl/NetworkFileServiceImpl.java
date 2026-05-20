package org.dromara.huahao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.huahao.constant.ErrorCodeConstants;
import org.dromara.huahao.domain.NetworkFile;
import org.dromara.huahao.domain.bo.NetworkFileBo;
import org.dromara.huahao.domain.vo.NetworkFileVo;
import org.dromara.huahao.exception.ReceivingException;
import org.dromara.huahao.listener.NetworkFileListener;
import org.dromara.huahao.mapper.NetworkFileMapper;
import org.dromara.huahao.service.NetworkFileService;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NetworkFileServiceImpl implements NetworkFileService {

    private final NetworkFileMapper baseMapper;
    private final ApplicationContext applicationContext; // 添加ApplicationContext依赖

    @Override
    public Boolean insertByBo(NetworkFileBo bo) {
        NetworkFile networkFile = MapstructUtils.convert(bo, NetworkFile.class);
        if (networkFile != null) {
            validEntityBeforeSave(networkFile);
            baseMapper.insert(networkFile);
            if ("0".equals(networkFile.getIsEnable()) && "1".equals(networkFile.getType())) {
                try {
                    NetworkFileListener listener = applicationContext.getBean(NetworkFileListener.class);
                    listener.reinitializeWatcher();
                } catch (NoSuchBeanDefinitionException e) {
                    log.warn("NetworkFileListener Bean未找到，跳过重新初始化");
                }
            }
        }
        return true;
    }

    @Override
    public Boolean updateByBo(NetworkFileBo bo) {
        NetworkFile networkFile = MapstructUtils.convert(bo, NetworkFile.class);
        if (networkFile != null) {
            NetworkFileVo networkFileVo = baseMapper.selectVoById(networkFile.getId());
            baseMapper.updateById(networkFile);
            // 文件路径改变,且只监听验收单路径变化，则重新初始化监听
            if (!networkFileVo.getFilePath().equals(networkFile.getFilePath()) &&
                "1".equals(networkFileVo.getType()) &&
                "0".equals(networkFile.getIsEnable())) {
                try {
                    NetworkFileListener listener = applicationContext.getBean(NetworkFileListener.class);
                    listener.reinitializeWatcher();
                } catch (NoSuchBeanDefinitionException e) {
                    log.warn("NetworkFileListener Bean未找到，跳过重新初始化");
                }
            }
        }
        return true;
    }

    @Override
    public TableDataInfo<NetworkFileVo> queryPageList(NetworkFileBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<NetworkFile> lqw = buildQueryWrapper(bo);
        Page<NetworkFileVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    private LambdaQueryWrapper<NetworkFile> buildQueryWrapper(NetworkFileBo bo) {
        LambdaQueryWrapper<NetworkFile> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getFilePath()), NetworkFile::getFilePath, bo.getFilePath());
        lqw.eq(StringUtils.isNotBlank(bo.getType()), NetworkFile::getType, bo.getType());
        lqw.orderByDesc(NetworkFile::getUpdateTime);
        lqw.orderByDesc(NetworkFile::getCreateTime);
        return lqw;
    }

    @Override
    public NetworkFileVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    @Override
    public NetworkFileVo getAcceptanceFilePath() {
        return baseMapper.selectVoOne(new LambdaQueryWrapper<NetworkFile>()
            .eq(NetworkFile::getType, "1")
            .eq(NetworkFile::getIsEnable, "0")
            .orderByDesc(NetworkFile::getCreateTime)
            .last("LIMIT 1"));
    }

    @Override
    public Boolean deleteByIds(List<Long> ids) {
        return baseMapper.deleteByIds(ids) > 0;
    }

    public void validEntityBeforeSave(NetworkFile networkFile) {
        // 校验，查询该文件类型是否已经存在，且状态为启用
        NetworkFile one = baseMapper.selectOne(new LambdaQueryWrapper<NetworkFile>()
            .eq(NetworkFile::getIsEnable, "0")
            .eq(NetworkFile::getType, networkFile.getType()));
        if (one != null) {
            throw new ReceivingException(ErrorCodeConstants.NETWORK_FILE_ADD_FAILED, ErrorCodeConstants.NETWORK_FILE_ADD_FAILED);
        }
    }
}
