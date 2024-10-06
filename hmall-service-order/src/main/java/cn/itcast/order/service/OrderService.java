package cn.itcast.order.service;

import cn.itcast.hmall.dto.common.ResultDTO;
import cn.itcast.hmall.dto.order.OrderReqDTO;
import cn.itcast.hmall.pojo.order.Order;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author jensen
 * @date 2024-10-06 16:10
 * @description
 */
public interface OrderService extends IService<Order> {
    //提交订单
    String addOrder(OrderReqDTO dto);
    // 简单模拟支付
    ResultDTO pay(Long orderId, String password);
}
