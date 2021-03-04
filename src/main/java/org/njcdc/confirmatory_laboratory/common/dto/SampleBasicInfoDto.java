package org.njcdc.confirmatory_laboratory.common.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class SampleBasicInfoDto implements Serializable {
    @TableId(value = "sequenceNumber", type = IdType.AUTO)
    private Integer sequenceNumber;

    @TableField("acceptanceNumber")
    private String acceptanceNumber;

    @TableField("inspectionUnit")
    private String inspectionUnit;

    @TableField("inspectDate")
    private LocalDate inspectDate;

    @TableField("sampleType")
    private String sampleType;

    @TableField("inspecteeType")
    private String inspecteeType;

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

}
