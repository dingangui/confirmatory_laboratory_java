package org.njcdc.confirmatory_laboratory.common.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class UserDto implements Serializable {

    /**
     * 工作人员唯一ID
     */
    private Integer id;

    /**
     * 工作人员用户名
     */
    private String username;

    /**
     * 管理员权限
     */
    private Boolean admin;

    /**
     * 样品录入权限
     */
    private Boolean sampleInput;

    /**
     * 检测结果输入权限
     */
    private Boolean detectionDataInput;

    /**
     * 审核结果权限
     */
    private Boolean dataVerify;

    /**
     * 报表导出权限
     */
    private Boolean formsOutput;
}
