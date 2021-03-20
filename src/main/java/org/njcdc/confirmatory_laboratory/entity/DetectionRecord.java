package org.njcdc.confirmatory_laboratory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * <p>
 * 
 * </p>
 *
 * @author dingangui
 * @since 2021-03-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DetectionRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 受理编号
     */
    @TableField("acceptanceNumber")
    private String acceptanceNumber;

    /**
     * 检测方式
     */
    @TableField("detectionMethod")
    private String detectionMethod;

    /**
     * 检测时间
     */
    @TableField("detectionDate")
    private LocalDate detectionDate;

    /**
     * 试剂和厂家
     */
    @TableField("reagentsAndManufacturers")
    private String reagentsAndManufacturers;

    /**
     * 批号
     */
    @TableField("batchNumber")
    private String batchNumber;

    /**
     * 有效日期
     */
    @TableField("effectiveDate")
    private LocalDate effectiveDate;

    /**
     * 检测结果
     */
    @TableField("testResult")
    private String testResult;

    /**
     * 结论/筛查结论
     */
    private String conclusion;

    /**
     * 当前受理编号下的检测次数
     */
    @TableField("sequence")
    private int sequence;

    /**
     * 检测人员ID
     */
    @TableField("inspectorAccountID")
    private String inspectorAccountID;

    /**
     * 检测人员姓名
     */
    @TableField("inspectorName")
    private String inspectorName;

    /**
     * 审核者ID
     */
    @TableField("reviewerAccountID")
    private String reviewerAccountID;

    /**
     * 审核者姓名
     */
    @TableField("reviewerName")
    private String reviewerName;

    /**
     * 数据是否已经审核
     */
    @TableField("dataVerified")
    private Boolean dataVerified;

    /**
     * 是否可以打印
     */
    @TableField("isReadyForOutput")
    private Integer isReadyForOutput;

    /**
     * 报告是否已签发
     */
    private Integer issued;

    /**
     * 签发者姓名
     */
    @TableField("issuerName")
    private String issuerName;

    /**
     * 签发者ID
     */
    @TableField("issuerAccountID")
    private String issuerAccountID;

    /**
     * 报告编号
     */
    @TableField("reportNumber")
    private String reportNumber;

    /**
     * 报告日期
     */
    @TableField("reportDate")
    private LocalDate reportDate;


}
