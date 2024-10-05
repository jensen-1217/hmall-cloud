package cn.itcast.search.controller;

import cn.itcast.hmall.dto.common.PageDTO;
import cn.itcast.hmall.dto.search.SearchReqDTO;
import cn.itcast.hmall.pojo.item.ItemDoc;
import cn.itcast.search.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author jensen
 * @date 2024-10-05 2:29
 * @description
 */
@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {
    @Autowired
    private SearchService searchService;
    //搜索栏自动补全功能
    @GetMapping("/suggestion")
    public List<String> getSuggestion(@RequestParam("key") String key){
        return searchService.getSuggestion(key);
    }
    //过滤项聚合功能
    @PostMapping("/filters")
    public Map<String ,List<String>> getFilters(@RequestBody SearchReqDTO params){
        return searchService.getFilters(params);
    }
    //实现基本搜索功能
    @PostMapping("/list")
    public PageDTO<ItemDoc> getList(@RequestBody SearchReqDTO params){
        return searchService.getList(params);
    }
}
