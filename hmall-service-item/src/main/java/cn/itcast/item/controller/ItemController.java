package cn.itcast.item.controller;

import cn.itcast.hmall.dto.common.PageDTO;
import cn.itcast.hmall.dto.item.SearchItemDTO;
import cn.itcast.hmall.pojo.item.Item;
import cn.itcast.item.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jensen
 * @date 2024-10-03 23:02
 * @description
 */
@Slf4j
@RestController
@RequestMapping("/item")
public class ItemController {
    @Autowired
    private ItemService itemService;

    /**
     * 分页查询商品
     * @param dto
     * @return
     */
    @PostMapping("/list")
    public PageDTO<Item> list(@RequestBody SearchItemDTO dto){
        log.info("ItemController list   请求参数: {}",dto);
        return itemService.getList(dto);
    }
}
