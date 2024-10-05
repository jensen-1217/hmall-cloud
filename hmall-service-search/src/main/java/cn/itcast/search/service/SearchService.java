package cn.itcast.search.service;


import cn.itcast.hmall.dto.search.SearchReqDTO;

import java.util.List;
import java.util.Map;

/**
 * @author jensen
 * @date 2024-10-05 0:42
 * @description
 */
public interface SearchService {
    //全量数据导入
    public void bulkInsect();
    //搜索栏自动补全功能
    List<String> getSuggestion(String key);
    //过滤项聚合功能
    Map<String, List<String>> getFilters(SearchReqDTO params);
}
