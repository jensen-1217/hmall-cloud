package cn.itcast.user.controller;

import cn.itcast.hmall.pojo.user.Address;
import cn.itcast.user.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author jensen
 * @date 2024-10-06 14:51
 * @description
 */
@RestController
@RequestMapping("/address")
public class AddressController {
    @Autowired
    private AddressService addressService;
    //根据用户id查询地址列表
    @GetMapping("/uid")
    public List<Address> getUID(){
        return addressService.getUID();
    }
}
