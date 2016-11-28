package com.hgt.es.common;

import com.hgt.es.config.ESConfig;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import java.util.LinkedList;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

/**
 * README: es集群组合条件查询
 * Created by Yao on 11/28/16.
 * =============================================================================
 * CHANGELOG:
 */
public class ESAdvancedQuery<T> {

    public String hostList = "";
    public TransportClient client = null;
    IndicesAdminClient indicesAdminClient = null;

    public ESAdvancedQuery(ESConfig esConfig) {
        this.client = esConfig.createESClient();
        AdminClient adminClient = client.admin();
        this.indicesAdminClient = adminClient.indices();
    }

    public ESAdvancedQuery(String strClusterName, String strHost) {
        this.hostList = strHost;
        this.client = new ESConfig(strClusterName, strHost).createESClient();
    }


    public List<T> executeQuery(String strIndex, String strType, String strBody) {

        List<T> resultList = new LinkedList<T>();
        SearchRequestBuilder searchBuilder = client.prepareSearch(strIndex)
                .setTypes(strType)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setExplain(true);
        QueryBuilder qb;
        String qBody = strBody;

        if (qBody.contains("queryContext")) {
            qb = matchQuery(strBody, strBody);
            searchBuilder.setQuery(qb);
        }
        if (qBody.contains("filterContext")) {
            qb = matchQuery(strBody, strBody);
            searchBuilder.setPostFilter(QueryBuilders.rangeQuery("age").from(12).to(18));
        }

        if (qBody.contains("sizes")) {
            searchBuilder.setFrom(0).setSize(60).setExplain(true);
        }

        SearchResponse response = searchBuilder.execute().actionGet();

        for(SearchHit hit: response.getHits().getHits()) {
            System.out.println(hit.getId());
            if (hit.getFields().containsKey("title")) {
                String v = hit.getFields().get("title").getValue();
                System.out.println("field.title: "+ v);
                resultList.add((T)v);
            }
            System.out.println("source.title: " + hit.getSource().get("title"));
        }

        return resultList;

    }

}
