package com.ruoyi.medical.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.medical.mapper.MutationMapper;
import com.ruoyi.medical.domain.Mutation;
import com.ruoyi.medical.service.IMutationService;

/**
 * 多肽变异Service业务层处理
 * 
 * @author 唐宏伟
 * @date 2023-11-04
 */
@Service
public class MutationServiceImpl implements IMutationService 
{
    @Autowired
    private MutationMapper mutationMapper;

    /**
     * 查询多肽变异
     * 
     * @param id 多肽变异主键
     * @return 多肽变异
     */
    @Override
    public Mutation selectMutationById(Long id)
    {
        return mutationMapper.selectMutationById(id);
    }

    /**
     * 查询多肽变异列表
     * 
     * @param mutation 多肽变异
     * @return 多肽变异
     */
    @Override
    public List<Mutation> selectMutationList(Mutation mutation)
    {
        return mutationMapper.selectMutationList(mutation);
    }

    /**
     * 新增多肽变异
     * 
     * @param mutation 多肽变异
     * @return 结果
     */
    @Override
    public int insertMutation(Mutation mutation)
    {
        mutation.setCreateTime(DateUtils.getNowDate());
        return mutationMapper.insertMutation(mutation);
    }

    /**
     * 修改多肽变异
     * 
     * @param mutation 多肽变异
     * @return 结果
     */
    @Override
    public int updateMutation(Mutation mutation)
    {
        mutation.setUpdateTime(DateUtils.getNowDate());
        return mutationMapper.updateMutation(mutation);
    }

    /**
     * 批量删除多肽变异
     * 
     * @param ids 需要删除的多肽变异主键
     * @return 结果
     */
    @Override
    public int deleteMutationByIds(Long[] ids)
    {
        return mutationMapper.deleteMutationByIds(ids);
    }

    /**
     * 删除多肽变异信息
     * 
     * @param id 多肽变异主键
     * @return 结果
     */
    @Override
    public int deleteMutationById(Long id)
    {
        return mutationMapper.deleteMutationById(id);
    }
}
