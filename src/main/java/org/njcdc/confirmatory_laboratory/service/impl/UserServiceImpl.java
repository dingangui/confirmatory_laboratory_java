package org.njcdc.confirmatory_laboratory.service.impl;

import org.njcdc.confirmatory_laboratory.entity.User;
import org.njcdc.confirmatory_laboratory.mapper.UserMapper;
import org.njcdc.confirmatory_laboratory.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author dingangui
 * @since 2021-03-01
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
