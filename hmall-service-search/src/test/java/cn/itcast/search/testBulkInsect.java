package cn.itcast.search;

import cn.itcast.search.service.SearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author jensen
 * @date 2024-10-05 1:15
 * @description
 */
@SpringBootTest
public class testBulkInsect {
    @Autowired
    private SearchService searchService;

    /**
     * 全量数据导入
     */
    @Test
    public void bulkTest(){
        searchService.bulkInsect();
    }
}
