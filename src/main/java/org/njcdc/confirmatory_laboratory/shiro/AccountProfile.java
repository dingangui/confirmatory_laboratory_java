package org.njcdc.confirmatory_laboratory.shiro;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import java.io.Serializable;

@Data
public class AccountProfile implements Serializable {

    /**
     * 工作人员唯一ID
     */
    @TableId(value = "id", type = IdType.AUTO)
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
