package com.atguigu.realtime.gmall0508publisher.service;

import java.util.List;
import java.util.Map;

public interface DauService {
    Long  getDayDau(String date);

    Map<String,Long> getHourDau(String date);
}
