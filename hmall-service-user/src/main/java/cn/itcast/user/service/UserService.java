package cn.itcast.user.service;

import cn.itcast.hmall.pojo.user.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author jensen
 * @date 2024-10-06 14:52
 * @description
 */
public interface UserService extends IService<User> {
    User getUser(Long id);
    void pay(Long money, Long id);
}
