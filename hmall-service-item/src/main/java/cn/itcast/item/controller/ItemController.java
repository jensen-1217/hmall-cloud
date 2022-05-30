package cn.itcast.item.controller;

import cn.itcast.hmall.dto.common.PageDTO;
import cn.itcast.hmall.dto.common.ResultDTO;
import cn.itcast.hmall.dto.item.SearchItemDTO;
import cn.itcast.hmall.pojo.item.Item;
import cn.itcast.item.service.ItemService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/item")
public class ItemController {
    @Autowired
    private ItemService itemService;

    //分页
    @PostMapping("/list")
    public PageDTO<Item> list(@RequestBody SearchItemDTO dto) {
        log.info("ItemController list   请求参数: {}", dto);
        return itemService.getList(dto);
    }

    //根据id查询
    @GetMapping("/{id}")
    public Item getOne(@PathVariable("id") Long id) {
        log.info("ItemController getOne   请求参数: {}", id);
        return itemService.getOneItem(id);
    }

    //添加
    @PostMapping
    public ResultDTO add(@RequestBody Item item) {
        log.info("ItemController add   请求参数: {}", item);
        itemService.addone(item);
        return ResultDTO.ok();
    }

    //上下架
    @PutMapping("/status/{id}/{status}")
    public ResultDTO updateStatus(@PathVariable("id") Long id, @PathVariable("status") Integer status) {
        log.info("ItemController updateStatus   请求参数: {} {}", id, status);
        try {
            itemService.updateStatus(id, status);
            return ResultDTO.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.error("修改上下架状态失败,原因:" + e.getMessage());
        }
    }

    //修改
    @PutMapping
    public ResultDTO update(@RequestBody Item item) {
        log.info("ItemController update   请求参数: {}", item);
        try {
            itemService.updateOne(item);
            return ResultDTO.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.error("修改商品失败,原因:" + e.getMessage());
        }
    }

    //删除
    @DeleteMapping("/{id}")
    public ResultDTO deleteOne(@PathVariable("id") Long id) {
        log.info("ItemController deleteOne   请求参数: {}", id);
        try {
            itemService.deleteOne(id);
            return ResultDTO.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.error("删除商品失败,原因:" + e.getMessage());
        }
    }

    // 减少库存
    @PutMapping("/stock/{itemId}/{num}")
    public ResultDTO stock(@PathVariable("itemId") Long itemId, @PathVariable("num") Integer num) {
        log.info("ItemController stock   请求参数: {}   {}", itemId, num);
        itemService.stock(itemId, num);
        return ResultDTO.ok();
    }

    // 补加库存
    @RequestMapping("/item/add/{id}/{num}")
    public ResultDTO add(@PathVariable("id") Long id, @PathVariable("num") Integer num) {
        log.info("ItemController add   请求参数: {}   {}", id, num);
        UpdateWrapper<Item> itemUpdateWrapper = new UpdateWrapper<>();
        itemUpdateWrapper.eq("id", id).setSql("stock=stock+" + num);
        itemService.update(itemUpdateWrapper);
        return ResultDTO.ok();
    }
}
