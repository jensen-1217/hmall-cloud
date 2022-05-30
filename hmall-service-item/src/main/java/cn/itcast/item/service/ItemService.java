package cn.itcast.item.service;

import cn.itcast.hmall.dto.common.PageDTO;
import cn.itcast.hmall.dto.item.SearchItemDTO;
import cn.itcast.hmall.pojo.item.Item;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ItemService extends IService<Item> {
    PageDTO<Item> getList(SearchItemDTO dto);

    void addone(Item item);

    void updateStatus(Long id, Integer status);

    Item getOneItem(Long id);

    void updateOne(Item item);

    void deleteOne(Long id);

    void stock(Long itemId, Integer num);
}
