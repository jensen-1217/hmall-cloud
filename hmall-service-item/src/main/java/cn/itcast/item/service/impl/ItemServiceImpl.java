package cn.itcast.item.service.impl;

import cn.itcast.hmall.dto.common.PageDTO;
import cn.itcast.hmall.dto.item.SearchItemDTO;
import cn.itcast.hmall.pojo.item.Item;
import cn.itcast.item.mapper.ItemMapper;
import cn.itcast.item.service.ItemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements ItemService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    //分页
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    @Override
    public PageDTO<Item> getList(SearchItemDTO dto) {
        LambdaQueryWrapper<Item> wrapper = Wrappers.<Item>lambdaQuery();
        wrapper.like(StringUtils.isNotBlank(dto.getBrand()), Item::getBrand, dto.getBrand());
        wrapper.like(StringUtils.isNotBlank(dto.getCategory()), Item::getCategory, dto.getCategory());
        wrapper.like(StringUtils.isNotBlank(dto.getName()), Item::getName, dto.getName());
        wrapper.ge(dto.getBeginDate() != null, Item::getCreateTime, dto.getBeginDate());
        wrapper.le(dto.getEndDate() != null, Item::getCreateTime, dto.getEndDate());
        Page<Item> result = this.page(new Page<>(dto.getPage(), dto.getSize()),wrapper);
        return new PageDTO<Item>(result.getTotal(), result.getRecords());
    }

    //添加
    @Override
    public void addone(Item item) {
        item.setStatus(1);
        this.save(item);
    }

    //上下架
    @Override
    public void updateStatus(Long id, Integer status) {
        if (id == null || status == null) {
            throw new RuntimeException("id不能为空或状态出错");
        }
        if (status != 2 && status != 1) {
            throw new RuntimeException("状态错误");
        }
        UpdateWrapper<Item> objectUpdateWrapper = new UpdateWrapper<>();
        objectUpdateWrapper.eq("id", id);
        objectUpdateWrapper.set("status", status);
        this.baseMapper.update(null, objectUpdateWrapper);
        if (status == 2) {
            // 发下架消息
            rabbitTemplate.convertAndSend("item.delete", id);
        } else {
            // 发上架消息
            rabbitTemplate.convertAndSend("item.update", id);
        }
    }

    //根据ID查询
    @Override
    public Item getOneItem(Long id) {
        if (id == null) {
            throw new RuntimeException("id不能为空");
        }
        return this.getById(id);
    }

    //修改
    @Override
    public void updateOne(Item item) {
        this.updateById(item);
    }

    //删除
    @Override
    public void deleteOne(Long id) {
        this.removeById(id);
    }

    //减少库存
    @Override
    public void stock(Long itemId, Integer num) {
        if (itemId != null && num != null) {
            UpdateWrapper<Item> itemUpdateWrapper = new UpdateWrapper<>();
            itemUpdateWrapper.eq("id", itemId).setSql("stock=stock-" + num);
            this.update(itemUpdateWrapper);
        }
    }
}
