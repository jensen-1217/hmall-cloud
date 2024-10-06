package cn.itcast.user.service;

import cn.itcast.hmall.pojo.user.Address;
import cn.itcast.hmall.pojo.user.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author jensen
 * @date 2024-10-06 14:52
 * @description
 */
public interface AddressService extends IService<Address> {
    //根据用户id查询地址列表
    List<Address> getUID();

}
