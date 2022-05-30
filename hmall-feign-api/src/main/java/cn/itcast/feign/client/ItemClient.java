package cn.itcast.feign.client;
import cn.itcast.hmall.dto.common.PageDTO;
import cn.itcast.hmall.pojo.item.Item;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient("itemservice")
public interface ItemClient {
    @GetMapping("/item/list")
    public PageDTO<Item> list(@RequestBody Map params);

    @GetMapping("/item/{id}")
    public Item getOne(@PathVariable("id")Long id);
    @PutMapping("/item/stock/{itemId}/{num}")
    public void stock(@PathVariable("itemId") Long itemId ,@PathVariable("num")Integer num);



    @RequestMapping("/item/add/{id}/{num}")
    void add(@PathVariable("id") Long id,@PathVariable("num")Integer num);

}
