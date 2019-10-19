package com.atguigu.realtime.gmall0508publisher.service;

import java.math.BigDecimal;
import java.util.Map;

public interface OrderService {

    double getDayTotalAmount(String date);

    Map<String, BigDecimal> getHourAmount(String date);

}
