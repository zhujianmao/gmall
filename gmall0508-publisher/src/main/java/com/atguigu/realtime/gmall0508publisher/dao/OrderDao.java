package com.atguigu.realtime.gmall0508publisher.dao;

import java.util.List;
import java.util.Map;

public interface OrderDao {

    Double  getDayTotalAmount(String date);

    List<Map> getHourAmount(String date);
}
