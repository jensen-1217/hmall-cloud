package cn.itcast.search.service.impl;

import cn.itcast.feign.client.ItemClient;
import cn.itcast.hmall.dto.common.PageDTO;
import cn.itcast.hmall.dto.item.SearchItemDTO;
import cn.itcast.hmall.pojo.item.Item;
import cn.itcast.hmall.pojo.item.ItemDoc;
import cn.itcast.search.service.SearchService;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jensen
 * @date 2024-10-05 0:47
 * @description
 */
@Service
@Slf4j
public class SearchServiceImpl implements SearchService {
    @Autowired
    private ItemClient itemClient;
    @Autowired
    private ElasticsearchClient client;
    @Override
    public void bulkInsect() {
        int page=1;
        int size=5000;
        while (true) {
            //组装查询参数
            SearchItemDTO searchItemDTO = SearchItemDTO.builder().page(page).size(size).build();
            //通过feign调用item-service,分页查询商品
            PageDTO<Item> pageDTO = itemClient.list(searchItemDTO);
            //获取分页查询商品列表
            List<Item> pageDTOList = pageDTO.getList();
            // 如果列表为空，退出循环
            if (pageDTOList==null|| pageDTOList.isEmpty()){
                break;
            }
            //创建 BulkOperation 列表
            ArrayList<BulkOperation> bulkOperations = new ArrayList<>();
            // 遍历商品列表，为每个商品添加 BulkOperation
            pageDTOList.forEach(item -> {
                // 将 item 转换为 itemDoc
                ItemDoc itemDoc = new ItemDoc(item);
                bulkOperations.add(BulkOperation.of(b -> b
                        .index(i -> i
                                .index("item")
                                .id(String.valueOf(itemDoc.getId()))
                                .document(itemDoc)
                        )
                ));
            });
            // 构建 BulkRequest
            BulkRequest bulkRequest = BulkRequest.of(br -> br.operations(bulkOperations));
            // 发送 BulkRequest 请求
            try {
                client.bulk(bulkRequest);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 更新页码，准备查询下一页
            page++;
        }
    }
}
