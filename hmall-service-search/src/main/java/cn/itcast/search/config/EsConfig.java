package cn.itcast.search.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mrchen
 * @date 2022/5/30 14:12
 */
@Configuration
public class EsConfig {

    @Value("${elasticsearch.host:127.0.0.1}")
    private String host;

    @Value("${elasticsearch.port:9200}")
    private int port;

    @Bean
    public ElasticsearchClient restHighLevelClient(){
        RestClient restClient = RestClient.builder(
                new HttpHost(host, port)
        ).build();
        // 创建一个 Transport 和 JacksonJsonpMapper 序列化实例
        RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        // 创建 Elasticsearch 客户端
        ElasticsearchClient client = new ElasticsearchClient(transport);
        return client;
    }
//    @Bean
//    public RestHighLevelClient client(){
//        return new RestHighLevelClient(
//                RestClient.builder(
//                        new HttpHost(host,port)
//                )
//        );
//    }
}
