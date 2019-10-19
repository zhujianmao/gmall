package com.atguigu.realtime.gmall0508publisher.service.impl;

import com.atguigu.dw.gmall.mock.constant.ConstantUtil;
import com.atguigu.dw.gmall.mock.util.ElasticSearchUtils;
import com.atguigu.realtime.gmall0508publisher.ESDSLUtils;
import com.atguigu.realtime.gmall0508publisher.service.SaleService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SaleServiceImpl implements SaleService {
    @Override
    public Map<String, Object> getSaleDetailAndAggResultByAggField(String date,
                                                                   String keyword,
                                                                   long startPage,
                                                                   long size,
                                                                   long aggSize,
                                                                   String field) {
        String dsl = ESDSLUtils.getSaleDSL(date, keyword, startPage, size, aggSize, field);
        Map<String, Object> resultMap = new HashMap<>();
        JestClient client = null;
        try {
            client = ElasticSearchUtils.getClient();
            Search search = new Search.Builder(dsl)
                    .addIndex(ConstantUtil.INDEX_NAME_SALE())
                    .addType("_doc")
                    .build();
            SearchResult result = client.execute(search);
            if(result==null)return resultMap;
            //设置总数
            Integer total = result.getTotal();
            resultMap.put(ConstantUtil.SALE_TOTAL(),total);
            //设置hits
            List<SearchResult.Hit<HashMap, Void>> hits = result.getHits(HashMap.class);
            if(!hits.isEmpty()){
                List<Map<String,Object>> sourceListMap = new ArrayList<>();
                for (SearchResult.Hit<HashMap, Void> hit : hits) {
                    HashMap<String,Object> source = hit.source;
                    sourceListMap.add(source);
                }
                resultMap.put(ConstantUtil.SALE_SOURCES(),sourceListMap);
            }
            //设置buckets
            List<TermsAggregation.Entry> buckets = result.getAggregations()
                    .getTermsAggregation("groupby_" + field)
                    .getBuckets();
            if(!buckets.isEmpty()){
                Map<String, Long> bucketMap = new HashMap<>();
                for (TermsAggregation.Entry bucket : buckets) {
                    bucketMap.put(bucket.getKey(),bucket.getCount());
                }
                resultMap.put(ConstantUtil.SALE_BUCKETS(),bucketMap);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("从es查询数据出错");
        } finally {
            if (client != null)
                client.shutdownClient();
        }
        return resultMap;
    }
}
