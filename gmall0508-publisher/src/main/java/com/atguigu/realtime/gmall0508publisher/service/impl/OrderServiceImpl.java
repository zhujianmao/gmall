package com.atguigu.realtime.gmall0508publisher.service.impl;

import com.atguigu.realtime.gmall0508publisher.dao.OrderDao;
import com.atguigu.realtime.gmall0508publisher.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderDao orderDao;

    @Override
    public double getDayTotalAmount(String date) {
        Double result = orderDao.getDayTotalAmount(date);
        return result == null ? 0.0 : result;
    }

    @Override
    public Map<String, BigDecimal> getHourAmount(String date) {
        List<Map> hourAmount = orderDao.getHourAmount(date);
        Map<String,BigDecimal> resultMap = new HashMap<>();
        if(!hourAmount.isEmpty()) {
            for (Map map : hourAmount) {
                BigDecimal sum = (BigDecimal) map.get("SUM");
                String hour = (String) map.get("CREATE_HOUR");
                resultMap.put(hour,sum);
            }
        }
        return resultMap;
    }
}
