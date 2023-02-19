package cn.itcast.search.service;


import cn.itcast.hmall.dto.common.PageDTO;
import cn.itcast.hmall.dto.search.SearchReqDTO;
import cn.itcast.hmall.pojo.item.ItemDoc;

import java.util.List;
import java.util.Map;

public interface SearchService {
    List<String> getSuggestion(String key);
    PageDTO<ItemDoc> getList(SearchReqDTO dto);
    Map<String, List<String>> getFilters(SearchReqDTO dto);

    void insert(Long id);

    void delete(Long id);
}
