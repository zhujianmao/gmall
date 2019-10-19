package com.atguigu.realtime.gmall0508publisher.dao;

import java.util.List;
import java.util.Map;

public interface DauDao {

    Long  getDayDau(String date);

    List<Map> getHourDau(String date);
}
