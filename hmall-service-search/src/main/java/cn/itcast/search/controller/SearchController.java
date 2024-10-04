package cn.itcast.search.controller;

import cn.itcast.search.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
