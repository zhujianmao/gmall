package com.atguigu.realtime.gmall0508publisher.service.impl;

import com.atguigu.realtime.gmall0508publisher.dao.DauDao;
import com.atguigu.realtime.gmall0508publisher.service.DauService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DauServiceImpl implements DauService {

    @Autowired
    private DauDao dauDao;

    @Override
    public Map<String, Long> getHourDau(String date) {
        List<Map> hourDau = dauDao.getHourDau(date);
        Map<String, Long> resultMap = new HashMap<>();
        if(hourDau.size()==0) return resultMap;

        for (Map map : hourDau) {
            long count = (long)map.get("COUNT");
            String hour = (String)map.get("LOGHOUR") ;
            resultMap.put(hour,count);
        }
        return resultMap;
    }



    @Override
    public Long getDayDau(String date) {
        return dauDao.getDayDau(date);
    }
}
