package org.njcdc.confirmatory_laboratory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
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
@TableName("m_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工作人员唯一ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 密码
     */
    private String password;

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
    @TableField("sampleInput")
    private Boolean sampleInput;

    /**
     * 检测结果输入权限
     */
    @TableField("detectionDataInput")
    private Boolean detectionDataInput;

    /**
     * 审核结果权限
     */
    @TableField("dataVerify")
    private Boolean dataVerify;

    /**
     * 报表导出权限
     */
    @TableField("formsOutput")
    private Boolean formsOutput;


}
