package cn.itcast.search.controller;
import cn.itcast.feign.client.ItemClient;
import cn.itcast.hmall.dto.common.PageDTO;
import cn.itcast.hmall.dto.common.ResultDTO;
import cn.itcast.hmall.dto.search.RequestParams;
import cn.itcast.hmall.dto.search.SearchReqDTO;
import cn.itcast.hmall.pojo.item.Item;
import cn.itcast.hmall.pojo.item.ItemDoc;
import cn.itcast.search.service.SearchService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {
    @Autowired
    private SearchService searchService;
    //补全
    @GetMapping("/suggestion")
    public List<String> getSuggestion(@RequestParam("key")String key){
        return searchService.getSuggestion(key);

    }
    //过滤
    @PostMapping("/filters")
    public Map<String,List<String>> getFilters(@RequestBody SearchReqDTO dto){
        return searchService.getFilters(dto);
    }
    //基本搜索
    @PostMapping("/list")
    public PageDTO<ItemDoc> getList(@RequestBody SearchReqDTO dto){
        return searchService.getList(dto);
    }
    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private ItemClient itemClient;

    //基本搜索
    @GetMapping("/importItemData")
    public ResultDTO importItemData(){
        try {
            int page=1;
            int size=1000;
            while (true){
                BulkRequest hotel = new BulkRequest("item");
                HashMap<String, Integer> params = new HashMap<>();
                params.put("page",page);
                params.put("size",size);
                PageDTO<Item> list1 = itemClient.list(params);
                List<Item> list = list1.getList();
                if(list.size()>0) {
                    for (Item item : list) {
                        IndexRequest item1 = new IndexRequest("item").id(item.getId().toString());
                        ItemDoc itemDoc = new ItemDoc(item);
                        item1.source(JSON.toJSONString(itemDoc), XContentType.JSON);
                        hotel.add(item1);
                    }
                    BulkResponse bulk   = client.bulk(hotel, RequestOptions.DEFAULT);
                    System.out.println(bulk.status());
                }else{
                    System.out.println("结束");
                    System.out.println(page);
                    break;
                }
                page++;
            }
            return ResultDTO.ok();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("批量导入es索引库数据失败   原因: {}",e.getMessage());
            return ResultDTO.error("批量导入es索引库数据失败");
        }
    }
}
