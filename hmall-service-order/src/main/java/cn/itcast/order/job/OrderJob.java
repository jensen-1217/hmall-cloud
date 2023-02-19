package cn.itcast.order.job;

import cn.itcast.hmall.pojo.order.Order;
import cn.itcast.order.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName 类名
 * @Description 类说明
 */
@Slf4j
@Component
public class OrderJob {

    @Autowired
    private OrderService orderService;

    @XxlJob("cancelOrder")
    public ReturnT cancelOrder(String params){
        log.info(">>>>>>>>>>>>取消订单定时任务执行<<<<<<<<<<<<");
        LambdaQueryWrapper<Order> wrapper = Wrappers.<Order>lambdaQuery();
        // 查询未付款订单
        wrapper.eq(Order::getStatus,1);
        List<Order> list = orderService.list(wrapper);
        List<Order> orderList = list.stream().filter(order -> {
            return (System.currentTimeMillis() - order.getCreateTime().getTime()) > 120000; // 演示效果 大于2分钟
        }).map(order -> {
            order.setStatus(5);
            return order;
        }).collect(Collectors.toList());
        log.info(">>>>>>>>>>>>下列订单已超时<<<<<<<<<<<< {}",list);
        orderService.updateBatchById(orderList);
        return ReturnT.SUCCESS;
    }

}
