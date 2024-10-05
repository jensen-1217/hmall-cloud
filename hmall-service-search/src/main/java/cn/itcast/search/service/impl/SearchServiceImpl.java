package cn.itcast.search.service.impl;

import cn.itcast.feign.client.ItemClient;
import cn.itcast.hmall.dto.common.PageDTO;
import cn.itcast.hmall.dto.item.SearchItemDTO;
import cn.itcast.hmall.dto.search.SearchReqDTO;
import cn.itcast.hmall.pojo.item.Item;
import cn.itcast.hmall.pojo.item.ItemDoc;
import cn.itcast.search.service.SearchService;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionBoostMode;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggestOption;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.core.search.Suggester;
import co.elastic.clients.json.JsonData;
import com.alibaba.cloud.commons.lang.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    //全量导入
    @Override
    public void bulkInsect() {
        int page = 1;
        int size = 5000;
        while (true) {
            //组装查询参数
            SearchItemDTO searchItemDTO = SearchItemDTO.builder().page(page).size(size).build();
            //通过feign调用item-service,分页查询商品
            PageDTO<Item> pageDTO = itemClient.list(searchItemDTO);
            //获取分页查询商品列表
            List<Item> pageDTOList = pageDTO.getList();
            // 如果列表为空，退出循环
            if (pageDTOList == null || pageDTOList.isEmpty()) {
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

    //搜索栏自动补全功能
    @Override
    public List<String> getSuggestion(String key) {
        try {
            // 1.准备Request
//        SearchRequest request = new SearchRequest.Builder().index("item").build();
            // 1.准备Request 准备DSL
            SearchRequest request = SearchRequest.of(s -> s
                    .index("item") // 指定索引名称
                    .suggest(Suggester.of(su -> su
                            .text(key)//关键字
                            .suggesters("suggestions", sug -> sug
                                    .completion(c -> c
                                            .field("suggestion")
                                            .skipDuplicates(true)//跳过重复
                                            .size(10)
                                    ))))
            );
            // 2.发起请求
            SearchResponse<ItemDoc> response = client.search(request, ItemDoc.class);
            // 4.解析结果
            // 4.1.根据补全查询名称，获取补全结果
            // 4.2.获取options
            List<CompletionSuggestOption<ItemDoc>> options = response.suggest().get("suggestions").get(0).completion().options();
            // 4.3.遍历
            List<String> list = new ArrayList<>(options.size());
            options.forEach(option -> {
                String text = option.text();
                list.add(text);
            });
            return list;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    //过滤项聚合功能
    @Override
    public Map<String, List<String>> getFilters(SearchReqDTO params) {
        try {
            // 1.准备Request
            SearchRequest.Builder request = new SearchRequest.Builder().index("item");
            //2.准备DSL
            buildBasicQuery(params, request);
            //3.聚合
            buildAggregation(request);
            //4.发送请求
            SearchResponse<ItemDoc> search = client.search(request.build(), ItemDoc.class);
            // 4.解析结果
            Map<String, List<String>> result = new HashMap<>();
            Map<String, Aggregate> aggregations = search.aggregations();
            // 4.1.根据品牌名称，获取品牌结果
            List<String> brandList = getAggByName(aggregations, "brandAgg");
            result.put("brand", brandList);
            // 4.2.根据分类名称，获取分类结果
            List<String> categoryList = getAggByName(aggregations, "categoryAgg");
            result.put("category", categoryList);
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //实现基本搜索功能
    @Override
    public PageDTO<ItemDoc> getList(SearchReqDTO params) {
        try {
            // 1.准备Request
            SearchRequest.Builder request = new SearchRequest.Builder().index("item");
            //2.准备DSL
            buildBasicQuery(params, request);
            //3.发送请求
            SearchResponse<ItemDoc> search = client.search(request.build(), ItemDoc.class);
            return handleResponse(search);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //添加
    @Override
    public void insertById(Long id) {
        try {
            // 0.通过feign调用item-service根据id查询商品数据
            Item one = itemClient.getOne(id);
            // 转换为文档类型
            ItemDoc itemDoc = new ItemDoc(one);
            // 1.准备请求参数
            IndexResponse indexResponse = client.index(i -> i.index("item").document(itemDoc).id(id.toString()));
//            UpdateRequest<String, ItemDoc> updateRequest = UpdateRequest.of(s -> s
//                    .index("item")
//                    .id(id.toString())
//                    .doc(itemDoc));
//            UpdateResponse<String> updateResponse = client.update(updateRequest, ItemDoc.class);
            log.info("版本：{}",indexResponse.version());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    //删除
    @Override
    public void deleteById(Long id) {
        try {
            // 1.准备Request
            // 2.发送请求
            DeleteResponse deleteResponse = client.delete(i -> i.index("item").id(id.toString()));
            Result result = deleteResponse.result();
            System.out.println("Deleted".equals(result.toString())?"删除成功":"没有找到");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //准备DSL
    private void buildBasicQuery(SearchReqDTO params, SearchRequest.Builder request) {
        // 1. 准备Boolean查询
        String key = params.getKey();
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();
        if (StringUtils.isNotBlank(key)) {
            boolQuery.must(m -> m.match(match -> match.field("all").query(key)));
        } else {
            boolQuery.must(m -> m.matchAll(MatchAllQuery.of(match -> match)));
        }
        // 1.2. 品牌
        String brand = params.getBrand();
        if (StringUtils.isNotBlank(brand)) {
            boolQuery.filter(f -> f.term(t -> t.field("brand").value(brand)));
        }
        // 1.3. 分类
        String category = params.getCategory();
        if (StringUtils.isNotBlank(category)) {
            boolQuery.filter(f -> f.term(t -> t.field("category").value(category)));
        }
        // 1.5. 价格范围
        Double minPrice = params.getMinPrice();
        Double maxPrice = params.getMaxPrice();
        if (minPrice != null && maxPrice != null) {
            maxPrice = maxPrice == 0 ? Double.MAX_VALUE : maxPrice;
            Double finalMaxPrice = maxPrice;
            boolQuery.filter(f -> f.range(r -> r.field("price").gte(JsonData.of(minPrice * 100)).lte(JsonData.of(finalMaxPrice * 100))));
        }
        // 2. 算分函数查询
        //算分查询  判断isAD是否为true，为true及进行加分 没有设置方法就是相乘 乘以10  当有其他排序的时候，得分算法就会失效，
        request.query(q -> q.functionScore(fs -> fs
                .query(b -> b.bool(boolQuery.build())) // 直接传入构建的布尔查询
                .functions(fn -> fn
                        .filter(f -> f.term(t -> t.field("isAD").value(true))) // 过滤条件
                        .weight(10.0) // 算分函数
                )
                .boostMode(FunctionBoostMode.Multiply) // 设定boost模式
        ));
        //判断页码是否为空
        int page = 1;
        int size = 10;
        if (params.getPage() != null && params.getSize() != null) {
            page = params.getPage();
            size = params.getSize();
        }
        //设置分页
        request.from((page - 1) * size).size(size);
        //判断是否按照价钱排序
        if (params.getSortBy().equals("price")) {
            request.sort(sort -> sort.field(f -> f.field("price").order(SortOrder.Asc)))
                    .sort(sort -> sort.field(f -> f.field("sold").order(SortOrder.Desc)));
        }
        //判断是否按照评分排序
        if (params.getSortBy().equals("sold")) {
            request.sort(sort -> sort.field(f -> f.field("sold").order(SortOrder.Desc)))
                    .sort(sort -> sort.field(f -> f.field("price").order(SortOrder.Asc)));
        }
    }

    //聚合
    private void buildAggregation(SearchRequest.Builder request) {
        request.aggregations("brandAgg", a -> a
                .terms(h -> h.
                        field("brand")
                        .size(20)));
        request.aggregations("categoryAgg", a -> a
                .terms(h -> h.
                        field("category")
                        .size(20)));
    }

    //获取结果
    private List<String> getAggByName(Map<String, Aggregate> aggregations, String starAgg) {
        // 4.1.根据聚合名称获取聚合结果
        // 4.2.获取buckets
        List<StringTermsBucket> brandTerms = aggregations.get(starAgg).sterms().buckets().array();
        ArrayList<String> brandList = new ArrayList<>();
        // 4.3.遍历
        brandTerms.forEach(bucket -> {
            // 4.4.获取key
            brandList.add(bucket.key().stringValue());
        });
        return brandList;
    }

    //处理响应结果
    private PageDTO<ItemDoc> handleResponse(SearchResponse response) {
        HitsMetadata<ItemDoc> searchHits = response.hits();
        // 4.1.总条数
        long total = searchHits.total().value();
        // 4.2.获取文档数组
        List<Hit<ItemDoc>> hits = searchHits.hits();
        List<ItemDoc> itemDocs = new ArrayList<>(hits.size());
        // 4.3.遍历
        hits.forEach(hit -> {
            // 获取文档source
            ItemDoc itemDoc = hit.source();
            // 检查高亮字段是否存在
            if (hit.highlight() != null && hit.highlight().containsKey("name")) {
                String name = hit.highlight().get("name").get(0); // 获取高亮结果
                itemDoc.setName(name); // 设置高亮结果
            }
            // 4.9.放入集合
            itemDocs.add(itemDoc);

        });
        return new PageDTO<ItemDoc>(total, itemDocs);
    }
}
