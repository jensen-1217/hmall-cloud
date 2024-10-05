package cn.itcast.search.mq;

import cn.itcast.hmall.constants.MqConstants;
import cn.itcast.search.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

/**
 * @author jensen
 * @date 2024-10-05 23:13
 * @description
 */
@Component
@Slf4j
public class ItemListener{
    @Autowired
    private SearchService searchService;

    /**
     * 监听商品新增或修改的业务
     * @param id 商品id
     */
    @RabbitListener(queuesToDeclare = @Queue(MqConstants.ITEM_INSERT_QUEUE))
    public void listenHotelInsertOrUpdate(Long id){
        searchService.insertById(id);
        log.info("增加或修改成功");
    }

    /**
     * 监听商品删除的业务
     * @param id 商品id
     */
    @RabbitListener(queuesToDeclare = @Queue(MqConstants.ITEM_DELETE_QUEUE))
    public void listenHotelDelete(Long id){
        searchService.deleteById(id);
        log.info("删除成功");
    }
}
