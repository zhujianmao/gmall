package com.atguigu.realtime.gmall0508publisher.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.atguigu.dw.gmall.mock.constant.ConstantUtil;
import com.atguigu.realtime.gmall0508publisher.bean.DauBean;
import com.atguigu.realtime.gmall0508publisher.bean.Option;
import com.atguigu.realtime.gmall0508publisher.bean.SaleInfo;
import com.atguigu.realtime.gmall0508publisher.bean.Stat;
import com.atguigu.realtime.gmall0508publisher.service.DauService;
import com.atguigu.realtime.gmall0508publisher.service.OrderService;
import com.atguigu.realtime.gmall0508publisher.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@RestController
public class PublisherController {

    @Autowired
    private DauService dauService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private SaleService saleService;
    /*
     接口: http://localhost:8070/sale_detail?date=2019-10-11&&startpage=1&&size=5&&keyword=手机小米
    */

    @GetMapping("/sale_detail")
    public String getSaleDetailAndAggResult(@RequestParam  String date,
                                            @RequestParam long startpage,
                                            @RequestParam long size,
                                            @RequestParam String keyword){
        //{"total":200,"sources":[{"sku_num":38.0,"order_count":11.0,"sku_category1_name":"手机","es_metadata_id":"s9l8v20BVOT31mPBGs_D","user_age":22.0,"sku_id":"4","user_gender":"F","order_price":1442.0,"sku_category2_name":"手机通讯","dt":"2019-10-11","user_id":"103","sku_category3_id":"61","order_amount":54796.0,"sku_category2_id":"13","user_level":"1","sku_tm_id":"1","sku_category1_id":"2","sku_name":"小米Play 流光渐变AI双摄 4GB+64GB 梦幻蓝 全网通4G 双卡双待 小水滴全面屏拍照游戏智能手机","spu_id":"1","sku_category3_name":"手机"}],"buckets":{"F":99,"M":101}}
        Map<String, Object> genderMap = saleService.getSaleDetailAndAggResultByAggField(date,
                keyword, startpage, size, 2, "user_gender");
       // {"total":200,"sources":[{"sku_num":38.0,"order_count":11.0,"sku_category1_name":"手机","es_metadata_id":"s9l8v20BVOT31mPBGs_D","user_age":22.0,"sku_id":"4","user_gender":"F","order_price":1442.0,"sku_category2_name":"手机通讯","dt":"2019-10-11","user_id":"103","sku_category3_id":"61","order_amount":54796.0,"sku_category2_id":"13","user_level":"1","sku_tm_id":"1","sku_category1_id":"2","sku_name":"小米Play 流光渐变AI双摄 4GB+64GB 梦幻蓝 全网通4G 双卡双待 小水滴全面屏拍照游戏智能手机","spu_id":"1","sku_category3_name":"手机"}],"buckets":{"44":5,"45":1,"46":5,"47":4,"48":5,"49":3,"50":2,"51":5,"52":4,"53":2,"54":6,"10":5,"55":6,"11":1,"12":6,"56":3,"13":6,"57":3,"14":5,"58":4,"15":3,"59":1,"16":4,"17":7,"18":2,"19":2,"9":4,"20":1,"21":2,"22":5,"23":6,"24":4,"25":4,"26":4,"27":3,"28":3,"29":6,"30":2,"31":6,"32":3,"33":1,"34":4,"35":3,"36":6,"37":3,"38":5,"39":3,"40":2,"41":6,"42":5,"43":9}}
        Map<String, Object> ageMap = saleService.getSaleDetailAndAggResultByAggField(date,
                keyword, startpage, size, 100, "user_age");
        SaleInfo saleInfo = new SaleInfo();
        //设置total
        long total = (Integer)genderMap.getOrDefault(ConstantUtil.SALE_TOTAL(), 0);
        saleInfo.setTotal(total);
        //设置details
        List<Map<String,Object>> details  = (List<Map<String,Object>> )genderMap.get(ConstantUtil.SALE_SOURCES());
        saleInfo.setDetails(details);
        //设置stat
        Stat genderStat = getGenderStat((Map<String, Long>)genderMap.get(ConstantUtil.SALE_BUCKETS()));
        Stat ageStat = getAgeStat((Map<String, Long>)ageMap.get(ConstantUtil.SALE_BUCKETS()));
        List<Stat> stats = new ArrayList<>();
        stats.add(genderStat);
        stats.add(ageStat);
        saleInfo.setStats(stats);

        return JSON.toJSONString(saleInfo);
    }

    private Stat getAgeStat(Map<String, Long> ageAggMap) {
        Stat stat = new Stat();
        if(ageAggMap == null && ageAggMap.isEmpty())
            return stat;
        stat.setTitle("用户年龄占比");
        //"44":5,"45":1
        Set<Map.Entry<String, Long>> entries = ageAggMap.entrySet();
        long lt20 = 0;
        long lt30 =0;
        long less =0;
        for (Map.Entry<String, Long> entry : entries) {
            int key = Integer.parseInt(entry.getKey());
            long value = entry.getValue();
            if(key<20){
                lt20 += value;
            }else if(key<30){
                lt30 += value;
            }else if(key<101){
                less += value;
            }
        }
        List<Option> ops = new ArrayList<>();
        ops.add(new Option("20岁以下",lt20));
        ops.add(new Option("20岁到30岁",lt30));
        ops.add(new Option("30岁及30岁以上",less));
        stat.setOptions(ops);
        return stat;
    }

    private Stat getGenderStat(Map<String, Long> genderAggMap) {
        Stat stat = new Stat();
        if(genderAggMap == null && genderAggMap.isEmpty())
            return stat;
        stat.setTitle("用户性别占比");
        //{"F":99,"M":101}
        List<Option> ops = new ArrayList<>();
        ops.add(new Option("男",genderAggMap.getOrDefault("M",0L)));
        ops.add(new Option("女",genderAggMap.getOrDefault("F",0L)));
        stat.setOptions(ops);
        return stat;
    }

    /**
     *  日活分时统计  http://localhost:8070/realtime-hour?id=dau&date=2019-02-20
     *                  http://localhost:8070/realtime-hour?id=order_amount&date=2019-02-01
     *      {"yesterday":{"11":383,"12":123,"17":88,"19":200 },
     *      "today":{"12":38,"13":1233,"17":123,"19":688 }}
     * @param id
     * @param date
     * @return
     */
    @GetMapping("/realtime-hour")
    public String getHourDau(@RequestParam String id,@RequestParam String date){
        List<Map>  resultList = new ArrayList<>();
        if("dau".equals(id)){
            Map<String, Long> today = dauService.getHourDau(date);
            Map<String, Long> yesterday = dauService.getHourDau(getYesterday(date));
            Map<String, Map> map = getTwoDaysMap(today, yesterday);
            resultList.add(map);
        }else if("order_amount".equals(id)){
            Map<String, BigDecimal> today = orderService.getHourAmount(date);
            Map<String, BigDecimal> yesterday = orderService.getHourAmount(date);
            Map<String, Map> twoDaysMap = getTwoDaysMap(today, yesterday);
            resultList.add(twoDaysMap);
        } else{
            //处理别的逻辑
        }
        return JSON.toJSONString(resultList);
    }

    private Map<String, Map> getTwoDaysMap(Map today, Map yesterday) {
        Map<String, Map> map = new HashMap<>();
        map.put("today",today);
        map.put("yesterday",yesterday);
        return map;
    }

    private String getYesterday(String date) {
        return LocalDate.parse(date).minusDays(1).toString();
    }

    /**
     * 日活总数:  http://localhost:8070/realtime-total?date=2019-09-20
     *
     * [{"id":"dau","name":"新增日活","value":1200},
     *  {"id":"new_mid","name":"新增设备","value":233} ,
     *    {"id":"order_amount","name":"新增交易额","value":1000.2 }]
     *
     * @param date
     * @return
     */
    @GetMapping("/realtime-total")
    public String getDayTotal(@RequestParam String date){
        Long dayDauCount = dauService.getDayDau(date);

        List<Map> resultList = new ArrayList<>();

        HashMap<Object, Object> map1 = new HashMap<>();
        map1.put("id","dau");
        map1.put("name","新增日活");
        map1.put("value",dayDauCount);
        resultList.add(map1);
        HashMap<Object, Object> map2 = new HashMap<>();
        map2.put("id","new_mid");
        map2.put("name","新增设备");
        map2.put("value",75);
        resultList.add(map2);
        //return JSON.toJSONString(resultList);

        List<DauBean> resultList1 = new ArrayList<>();
        DauBean bean1 = new DauBean("dau","新增日活",dayDauCount);
        DauBean bean2 = new DauBean("new_mid","新增设备",176L);
        DauBean bean3= new DauBean("order_amount","新增交易额",orderService.getDayTotalAmount(date));
        resultList1.add(bean1);
        resultList1.add(bean2);
        resultList1.add(bean3);
        return JSONArray.toJSONString(resultList1);
    }



}
