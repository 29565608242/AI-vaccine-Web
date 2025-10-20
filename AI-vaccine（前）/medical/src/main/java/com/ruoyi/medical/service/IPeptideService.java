package com.ruoyi.medical.service;

import java.util.List;
import com.ruoyi.medical.domain.Peptide;

/**
 * 亲和度Service接口
 * 
 * @author 唐宏伟
 * @date 2023-10-26
 */
public interface IPeptideService 
{
    /**
     * 查询亲和度
     * 
     * @param id 亲和度主键
     * @return 亲和度
     */
    public Peptide selectPeptideById(Long id);

    /**
     * 查询亲和度列表
     * 
     * @param peptide 亲和度
     * @return 亲和度集合
     */
    public List<Peptide> selectPeptideList(Peptide peptide);

    /**
     * 新增亲和度
     * 
     * @param peptide 亲和度
     * @return 结果
     */
    public int insertPeptide(Peptide peptide);

    /**
     * 修改亲和度
     * 
     * @param peptide 亲和度
     * @return 结果
     */
    public int updatePeptide(Peptide peptide);

    /**
     * 批量删除亲和度
     * 
     * @param ids 需要删除的亲和度主键集合
     * @return 结果
     */
    public int deletePeptideByIds(Long[] ids);

    /**
     * 删除亲和度信息
     * 
     * @param id 亲和度主键
     * @return 结果
     */
    public int deletePeptideById(Long id);
}
