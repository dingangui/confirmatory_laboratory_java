package org.njcdc.confirmatory_laboratory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author dingangui
 * @since 2021-03-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SampleBasicInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "sequenceNumber", type = IdType.AUTO)
    private Integer sequenceNumber;

    @TableField("acceptanceNumber")
    private String acceptanceNumber;

    @TableField("inspectionUnit")
    private String inspectionUnit;

    @TableField("inspectionDate")
    private LocalDate inspectionDate;

    @TableField("sampleType")
    private String sampleType;

    @TableField("inspectedType")
    private String inspectedType;

    private String name;

    private Integer age;

    private String sex;

    private String profession;

    private String country;

    private String nation;

    private String marriage;

    @TableField("educationalLevel")
    private String educationalLevel;

    @TableField("IDNumber")
    @JsonProperty
    private String IDNumber;

    private String phone;

    @TableField("presentAddress")
    private String presentAddress;

    @TableField("residenceAddress")
    private String residenceAddress;

    @TableField("dataEntryStaffAccountID")
    private String dataEntryStaffAccountID;

    @TableField("dataEntryStaffName")
    private String dataEntryStaffName;

    private Integer issued;

    @TableField("isReadyForOutput")
    private Integer isReadyForOutput;

    @TableField("dataVerified")
    private Integer dataVerified;

    /**
     * 是否需要输入检测数据
     */
    @TableField("needInputDetectionData")
    private Integer needInputDetectionData;

    /**
     * 是否需要审核数据
     */
    @TableField("needVerifyData")
    private Integer needVerifyData;


}
