package com.ruoyi.medical.service;

import java.util.List;
import com.ruoyi.medical.domain.Mutation;

/**
 * 多肽变异Service接口
 * 
 * @author 唐宏伟
 * @date 2023-11-04
 */
public interface IMutationService 
{
    /**
     * 查询多肽变异
     * 
     * @param id 多肽变异主键
     * @return 多肽变异
     */
    public Mutation selectMutationById(Long id);

    /**
     * 查询多肽变异列表
     * 
     * @param mutation 多肽变异
     * @return 多肽变异集合
     */
    public List<Mutation> selectMutationList(Mutation mutation);

    /**
     * 新增多肽变异
     * 
     * @param mutation 多肽变异
     * @return 结果
     */
    public int insertMutation(Mutation mutation);

    /**
     * 修改多肽变异
     * 
     * @param mutation 多肽变异
     * @return 结果
     */
    public int updateMutation(Mutation mutation);

    /**
     * 批量删除多肽变异
     * 
     * @param ids 需要删除的多肽变异主键集合
     * @return 结果
     */
    public int deleteMutationByIds(Long[] ids);

    /**
     * 删除多肽变异信息
     * 
     * @param id 多肽变异主键
     * @return 结果
     */
    public int deleteMutationById(Long id);
}
