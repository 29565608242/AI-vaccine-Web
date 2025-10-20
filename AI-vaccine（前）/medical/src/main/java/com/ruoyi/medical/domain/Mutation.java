package com.ruoyi.medical.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 多肽变异对象 mutation
 * 
 * @author 唐宏伟
 * @date 2023-11-04
 */
public class Mutation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** hla序列 */
    @Excel(name = "hla序列")
    private String hla;

    /** 突变后的肽序列 */
    @Excel(name = "突变后的肽序列")
    private String mutatedPeptide;

    /** y_pred */
    @Excel(name = "y_pred")
    private Float yPred;

    /** 概率 */
    @Excel(name = "概率")
    private Float yProb;

    /** 突变后肽序列 */
    @Excel(name = "突变后肽序列")
    private String originalPeptide;

    /** 相似度 */
    @Excel(name = "相似度")
    private Float similarity;

    private String chainNum;

    private String startNum;

    private Long originalId;

    private Float maxValue;

    private Float minValue;

    private String thermodynamic;

    private String xAxis;

    private String yAxis;

    private String mutationSite;

    private int isTcr;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }
    public void setHla(String hla) 
    {
        this.hla = hla;
    }

    public String getHla() 
    {
        return hla;
    }
    public void setMutatedPeptide(String mutatedPeptide) 
    {
        this.mutatedPeptide = mutatedPeptide;
    }

    public String getMutatedPeptide() 
    {
        return mutatedPeptide;
    }

    public Float getyPred() {
        return yPred;
    }

    public void setyPred(Float yPred) {
        this.yPred = yPred;
    }

    public Float getyProb() {
        return yProb;
    }

    public void setyProb(Float yProb) {
        this.yProb = yProb;
    }

    public void setOriginalPeptide(String originalPeptide)
    {
        this.originalPeptide = originalPeptide;
    }

    public String getOriginalPeptide() 
    {
        return originalPeptide;
    }

    public Float getSimilarity() {
        return similarity;
    }

    public void setSimilarity(Float similarity) {
        this.similarity = similarity;
    }

    public Float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Float maxValue) {
        this.maxValue = maxValue;
    }

    public Float getMinValue() {
        return minValue;
    }

    public void setMinValue(Float minValue) {
        this.minValue = minValue;
    }

    public String getThermodynamic() {
        return thermodynamic;
    }

    public void setThermodynamic(String thermodynamic) {
        this.thermodynamic = thermodynamic;
    }

    public String getxAxis() {
        return xAxis;
    }

    public void setxAxis(String xAxis) {
        this.xAxis = xAxis;
    }

    public String getyAxis() {
        return yAxis;
    }

    public void setyAxis(String yAxis) {
        this.yAxis = yAxis;
    }

    public String getChainNum() {
        return chainNum;
    }

    public void setChainNum(String chainNum) {
        this.chainNum = chainNum;
    }

    public String getStartNum() {
        return startNum;
    }

    public void setStartNum(String startNum) {
        this.startNum = startNum;
    }

    public Long getOriginalId() {
        return originalId;
    }

    public void setOriginalId(Long originalId) {
        this.originalId = originalId;
    }


    public String getMutationSite() {
        return mutationSite;
    }

    public void setMutationSite(String mutationSite) {
        this.mutationSite = mutationSite;
    }

    public int getIsTcr() {
        return isTcr;
    }

    public void setIsTcr(int isTcr) {
        this.isTcr = isTcr;
    }

    @Override
    public String toString() {
        return "Mutation{" +
                "id=" + id +
                ", hla='" + hla + '\'' +
                ", mutatedPeptide='" + mutatedPeptide + '\'' +
                ", yPred=" + yPred +
                ", yProb=" + yProb +
                ", originalPeptide='" + originalPeptide + '\'' +
                ", similarity=" + similarity +
                ", chainNum='" + chainNum + '\'' +
                ", startNum='" + startNum + '\'' +
                ", originalId=" + originalId +
                ", maxValue=" + maxValue +
                ", minValue=" + minValue +
                ", thermodynamic='" + thermodynamic + '\'' +
                ", xAxis='" + xAxis + '\'' +
                ", yAxis='" + yAxis + '\'' +
                ", mutationSite='" + mutationSite + '\'' +
                ", isTcr=" + isTcr +
                '}';
    }
}
