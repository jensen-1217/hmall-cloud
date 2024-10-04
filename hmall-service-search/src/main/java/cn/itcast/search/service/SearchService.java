package cn.itcast.search.service;


import java.util.List;

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
}
