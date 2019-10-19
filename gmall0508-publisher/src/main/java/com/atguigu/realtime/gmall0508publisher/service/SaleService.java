package com.atguigu.realtime.gmall0508publisher.service;

import java.util.Map;

public interface SaleService {
     Map<String,Object> getSaleDetailAndAggResultByAggField(String date,
                                                   String keyword,
                                                   long startPage,
                                                   long size,
                                                   long aggSize,
                                                   String field);
}
