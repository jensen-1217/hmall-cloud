package cn.itcast.item.config;

import cn.itcast.hmall.constants.MqConstants;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jensen
 * @date 2024-10-06 0:15
 * @description
 */
@Configuration
public class MqConfig {
    /**
     * 定义交换机
     * @return
     */
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(MqConstants.ITEM_EXCHANGE, true, false);
    }

    /**
     * 插入或者更新数据时的队列
     * @return
     */
    @Bean
    public Queue insertQueue(){
        return new Queue(MqConstants.ITEM_INSERT_QUEUE, true);
    }

    /**
     * 删除队列
     * @return
     */
    @Bean
    public Queue deleteQueue(){
        return new Queue(MqConstants.ITEM_DELETE_QUEUE, true);
    }

    /**
     * 绑定
     * @return
     */
    @Bean
    public Binding insertQueueBinding(){
        return BindingBuilder.bind(insertQueue()).to(topicExchange()).with(MqConstants.ITEM_INSERT_KEY);
    }

    @Bean
    public Binding deleteQueueBinding(){
        return BindingBuilder.bind(deleteQueue()).to(topicExchange()).with(MqConstants.ITEM_DELETE_KEY);
    }

}
