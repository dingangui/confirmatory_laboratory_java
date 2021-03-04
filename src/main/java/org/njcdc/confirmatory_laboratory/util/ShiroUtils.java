package org.njcdc.confirmatory_laboratory.util;

import org.apache.shiro.SecurityUtils;
import org.njcdc.confirmatory_laboratory.shiro.AccountProfile;

public class ShiroUtils {

    public static AccountProfile getProfile(){
        return (AccountProfile) SecurityUtils.getSubject().getPrincipal();
    }

}
