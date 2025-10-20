package com.ruoyi.medical.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.medical.mapper.PeptideMapper;
import com.ruoyi.medical.domain.Peptide;
import com.ruoyi.medical.service.IPeptideService;

/**
 * 亲和度Service业务层处理
 * 
 * @author 唐宏伟
 * @date 2023-10-26
 */
@Service
public class PeptideServiceImpl implements IPeptideService 
{
    @Autowired
    private PeptideMapper peptideMapper;

    /**
     * 查询亲和度
     * 
     * @param id 亲和度主键
     * @return 亲和度
     */
    @Override
    public Peptide selectPeptideById(Long id)
    {
        return peptideMapper.selectPeptideById(id);
    }

    /**
     * 查询亲和度列表
     * 
     * @param peptide 亲和度
     * @return 亲和度
     */
    @Override
    public List<Peptide> selectPeptideList(Peptide peptide)
    {
        return peptideMapper.selectPeptideList(peptide);
    }

    /**
     * 新增亲和度
     * 
     * @param peptide 亲和度
     * @return 结果
     */
    @Override
    public int insertPeptide(Peptide peptide)
    {
        peptide.setCreateTime(DateUtils.getNowDate());
        return peptideMapper.insertPeptide(peptide);
    }

    /**
     * 修改亲和度
     * 
     * @param peptide 亲和度
     * @return 结果
     */
    @Override
    public int updatePeptide(Peptide peptide)
    {
        peptide.setUpdateTime(DateUtils.getNowDate());
        return peptideMapper.updatePeptide(peptide);
    }

    /**
     * 批量删除亲和度
     * 
     * @param ids 需要删除的亲和度主键
     * @return 结果
     */
    @Override
    public int deletePeptideByIds(Long[] ids)
    {
        return peptideMapper.deletePeptideByIds(ids);
    }

    /**
     * 删除亲和度信息
     * 
     * @param id 亲和度主键
     * @return 结果
     */
    @Override
    public int deletePeptideById(Long id)
    {
        return peptideMapper.deletePeptideById(id);
    }
}
