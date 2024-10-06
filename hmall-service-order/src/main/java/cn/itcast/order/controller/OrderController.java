package cn.itcast.order.controller;

import cn.itcast.hmall.dto.order.OrderReqDTO;
import cn.itcast.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jensen
 * @date 2024-10-06 16:08
 * @description
 */
@RestController
public class OrderController {
    @Autowired
    private OrderService orderService;
    //提交订单
    @PostMapping("/order")
    public String getId(@RequestBody OrderReqDTO dto){
        return orderService.addOrder(dto);
    }
}
