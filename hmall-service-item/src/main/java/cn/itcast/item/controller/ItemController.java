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
     * @param dto 参数
     */
    @PostMapping("/list")
    public PageDTO<Item> list(@RequestBody SearchItemDTO dto){
        log.info("ItemController list   请求参数: {}",dto);
        return itemService.getList(dto);
    }

    /**
     * 根据id查询商品
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Item selectById(@PathVariable("id") Long id){
        log.info("ItemController getOne   请求参数: {}", id);
        return itemService.selectById(id);
    }

    /**
     * 新增商品
     * @param item
     * @return
     */
    @PostMapping
    public ResultDTO add(@RequestBody Item item){
        log.info("ItemController add   请求参数: {}", item);
        itemService.add(item);
        return ResultDTO.ok();
    }

    /**
     * 商品上架、下架
     * @param id 商品id
     * @param status 状态：1上架；2下架
     * @return 返回成功或失败信息
     */
    @PutMapping("/status/{id}/{status}")
    public ResultDTO updateStatus(@PathVariable("id") Long id,@PathVariable("status") Integer status){
        log.info("ItemController updateStatus   请求参数: {} {}", id, status);
        try {
            itemService.updateStatus(id,status);
            return ResultDTO.ok();
        }catch (Exception e){
            e.printStackTrace();
            return ResultDTO.error("修改上下架状态失败,原因:" + e.getMessage());
        }
    }

    /**
     * 修改商品信息
     * @param item
     * @return
     */
    @PutMapping
    public ResultDTO update(@RequestBody Item item){
        log.info("ItemController update   请求参数: {}", item);
        try {
            itemService.updateOne(item);
            return ResultDTO.ok();
        }catch (Exception e){
            e.printStackTrace();
            return ResultDTO.error("修改商品失败,原因:" + e.getMessage());
        }
    }

    /**
     * 根据id删除商品
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResultDTO delete(@PathVariable("id") Long id){
        log.info("ItemController deleteOne   请求参数: {}", id);
        try {
            itemService.deleteOne(id);
            return ResultDTO.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultDTO.error("删除商品失败,原因:" + e.getMessage());
        }
    }
    /**
     * 减少库存
     * @param itemId
     * @param num
     * @return
     */
    @PutMapping("/stock/{itemId}/{num}")
    public ResultDTO stock(@PathVariable("itemId") Long itemId, @PathVariable("num") Integer num) {
        log.info("ItemController stock   请求参数: {}   {}", itemId, num);
        itemService.stock(itemId, num);
        return ResultDTO.ok();
    }

    /**
     * 补加库存
     * @param id
     * @param num
     * @return
     */
    @RequestMapping("/item/add/{id}/{num}")
    public ResultDTO add(@PathVariable("id") Long id, @PathVariable("num") Integer num) {
        log.info("ItemController add   请求参数: {}   {}", id, num);
        UpdateWrapper<Item> itemUpdateWrapper = new UpdateWrapper<>();
        itemUpdateWrapper.eq("id", id).setSql("stock=stock+" + num);
        itemService.update(itemUpdateWrapper);
        return ResultDTO.ok();
    }

}
