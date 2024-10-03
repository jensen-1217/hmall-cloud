package cn.itcast.item.service.impl;

import cn.itcast.hmall.dto.common.PageDTO;
import cn.itcast.hmall.dto.item.SearchItemDTO;
import cn.itcast.hmall.pojo.item.Item;
import cn.itcast.item.mapper.ItemMapper;
import cn.itcast.item.service.ItemService;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author jensen
 * @date 2024-10-03 23:17
 */
@Service
@Transactional(propagation = Propagation.REQUIRED)
public class ItemServiceImpl  extends ServiceImpl<ItemMapper, Item> implements ItemService{

    //分页查询商品
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    @Override
    public PageDTO<Item> getList(SearchItemDTO dto) {
        LambdaQueryWrapper<Item> wrapper = Wrappers.lambdaQuery(Item.class);
//        LambdaQueryWrapper<Item> wrapper = Wrappers.<Item>lambdaQuery();
        wrapper.like(StringUtils.isNotBlank(dto.getBrand()), Item::getBrand, dto.getBrand())
                .like(StringUtils.isNotBlank(dto.getCategory()), Item::getCategory, dto.getCategory())
                .like(StringUtils.isNotBlank(dto.getName()), Item::getName, dto.getName())
                .ge(dto.getBeginDate() != null, Item::getCreateTime, dto.getBeginDate())
                .le(dto.getEndDate() != null, Item::getCreateTime, dto.getEndDate());
//        wrapper.like(StringUtils.isNotBlank(dto.getBrand()), Item::getBrand, dto.getBrand());
//        wrapper.like(StringUtils.isNotBlank(dto.getCategory()), Item::getCategory, dto.getCategory());
//        wrapper.like(StringUtils.isNotBlank(dto.getName()), Item::getName, dto.getName());
//        wrapper.ge(dto.getBeginDate() != null, Item::getCreateTime, dto.getBeginDate());
//        wrapper.le(dto.getEndDate() != null, Item::getCreateTime, dto.getEndDate());
        //构建分页对象：以下两行是分页例子
//        IPage<User> page=new Page<>(2,3);
//        userService.page(page,wrapper);

        Page<Item> result = this.page(new Page<>(dto.getPage(), dto.getSize()),wrapper);
        return new PageDTO<>(result.getTotal(), result.getRecords());
    }
}
