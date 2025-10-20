package com.ruoyi.medical.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 亲和度对象 peptide
 * 
 * @author 唐宏伟
 * @date 2023-10-26
 */

public class Peptide extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** HLA */
    @Excel(name = "HLA氨基酸序列")
    private String hla;

    /** 多肽 */
    @Excel(name = "抗原氨基酸序列")
    private String peptide;

    /** 亲和力 */
    private Long binding;

    /** 预测值 */
    private String prediction;

    /** 得分 */
    private Long score;

    /** 热力图数据 */
    private String thermodynamic;

    private String xAxis;

    private String yAxis;

    @Excel(name = "亲合度标签")
    private Float yPred;

    @Excel(name = "模型预测概率")
    private Float yProb;

    private Float maxValue;

    private Float minValue;

    @Excel(name = "预测模型类型", dictType = "sys_model")
    private Long modelType;

    @Excel(name = "是否已突变", dictType = "sys_change")
    private int changeFlag;

    private String chainNum;

    private String startNum;

    private String myModel;

    private String originalStructure;

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
    public void setPeptide(String peptide) 
    {
        this.peptide = peptide;
    }

    public String getPeptide() 
    {
        return peptide;
    }
    public void setBinding(Long binding) 
    {
        this.binding = binding;
    }

    public Long getBinding() 
    {
        return binding;
    }
    public void setPrediction(String prediction) 
    {
        this.prediction = prediction;
    }

    public String getPrediction() 
    {
        return prediction;
    }
    public void setScore(Long score) 
    {
        this.score = score;
    }

    public Long getScore() 
    {
        return score;
    }
    public void setThermodynamic(String thermodynamic) 
    {
        this.thermodynamic = thermodynamic;
    }

    public String getThermodynamic() 
    {
        return thermodynamic;
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

    public Long getModelType() {
        return modelType;
    }

    public void setModelType(Long modelType) {
        this.modelType = modelType;
    }

    public int getChangeFlag() {
        return changeFlag;
    }

    public void setChangeFlag(int changeFlag) {
        this.changeFlag = changeFlag;
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

    public String getMyModel() {
        return myModel;
    }

    public void setMyModel(String myModel) {
        this.myModel = myModel;
    }

    public String getOriginalStructure() {
        return originalStructure;
    }

    public void setOriginalStructure(String originalStructure) {
        this.originalStructure = originalStructure;
    }


    public int getIsTcr() {
        return isTcr;
    }

    public void setIsTcr(int isTcr) {
        this.isTcr = isTcr;
    }

    @Override
    public String toString() {
        return "Peptide{" +
                "id=" + id +
                ", hla='" + hla + '\'' +
                ", peptide='" + peptide + '\'' +
                ", binding=" + binding +
                ", prediction='" + prediction + '\'' +
                ", score=" + score +
                ", thermodynamic='" + thermodynamic + '\'' +
                ", xAxis='" + xAxis + '\'' +
                ", yAxis='" + yAxis + '\'' +
                ", yPred=" + yPred +
                ", yProb=" + yProb +
                ", maxValue=" + maxValue +
                ", minValue=" + minValue +
                ", modelType=" + modelType +
                ", changeFlag=" + changeFlag +
                ", chainNum='" + chainNum + '\'' +
                ", startNum='" + startNum + '\'' +
                ", myModel='" + myModel + '\'' +
                ", originalStructure='" + originalStructure + '\'' +
                ", isTcr=" + isTcr +
                '}';
    }
}
