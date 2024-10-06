package cn.itcast.order.service.impl;

import cn.itcast.feign.client.ItemClient;
import cn.itcast.feign.client.UserClient;
import cn.itcast.hmall.dto.order.OrderReqDTO;
import cn.itcast.hmall.pojo.item.Item;
import cn.itcast.hmall.pojo.order.Order;
import cn.itcast.hmall.pojo.order.OrderDetail;
import cn.itcast.hmall.pojo.order.OrderLogistics;
import cn.itcast.hmall.pojo.user.Address;
import cn.itcast.order.mapper.OrderMapper;
import cn.itcast.order.service.OrderDetailService;
import cn.itcast.order.service.OrderLogisticService;
import cn.itcast.order.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jensen
 * @date 2024-10-06 16:11
 * @description
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Autowired
    private ItemClient itemClient;
    @Autowired
    private UserClient userClient;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private OrderLogisticService orderLogisticService;

    //提交订单
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public String addOrder(OrderReqDTO dto) {
        // 1. 远程查询对应商品信息
        Item one = itemClient.getOne(dto.getItemId());
        Long totalFee = one.getPrice() * dto.getNum();
        int status = 1;
        Order order = new Order();
        order.setStatus(status);
        order.setTotalFee(totalFee);
        order.setPaymentType(dto.getPaymentType());
        Address addressID = userClient.getAddressID(dto.getAddressId());
        order.setUserId(addressID.getUserId());
        //插入订单
        this.save(order);
        // 2. 插入订单描述信息
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(order.getId());
        orderDetail.setItemId(one.getId());
        orderDetail.setNum(dto.getNum());
        orderDetail.setName(one.getName());
        orderDetail.setPrice(one.getPrice());
        orderDetail.setSpec(one.getSpec());
        orderDetail.setImage(one.getImage());
        orderDetailService.save(orderDetail);
        // 3. 保存物流信息
        OrderLogistics orderLogistics = new OrderLogistics();
        orderLogistics.setOrderId(order.getId());
        orderLogistics.setContact(addressID.getContact());
        orderLogistics.setMobile(addressID.getMobile());
        orderLogistics.setProvince(addressID.getProvince());
        orderLogistics.setCity(addressID.getCity());
        orderLogistics.setTown(addressID.getTown());
        orderLogistics.setStreet(addressID.getStreet());
        orderLogisticService.save(orderLogistics);
        // 4.调用商品服务扣减库存
        itemClient.stock(one.getId(), dto.getNum());
        return String.valueOf(order.getId());
    }
}
