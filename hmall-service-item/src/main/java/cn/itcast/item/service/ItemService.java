package cn.itcast.item.service;

import cn.itcast.hmall.dto.common.PageDTO;
import cn.itcast.hmall.dto.item.SearchItemDTO;
import cn.itcast.hmall.pojo.item.Item;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author jensen
 * @date 2024-10-03 23:14
 * @description
 */
public interface ItemService extends IService<Item> {
    /**
     * 分页查询商品
     * @param dto 参数
     * @return 返回值
     */
    PageDTO<Item> getList(SearchItemDTO dto);

    /**
     * 根据id查询商品
     * @param id
     * @return
     */
    Item selectById(Long id);

    /**
     * 新增商品
     * @param item
     * @return
     */
    void add(Item item);
}
