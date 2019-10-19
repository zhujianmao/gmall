package com.atguigu.gmall0508logger.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.dw.gmall.mock.constant.ConstantUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoggerController {

    @PostMapping("/log")
    public String doLogger(@RequestParam("log") String log) {

        String logWithTS = addTS(log);

        saveLog2File(logWithTS);

        saveLog2Kafka(logWithTS);

        return "ok";
    }

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void saveLog2Kafka(String logWithTS) {
        String topic = ConstantUtil.EVENT_TOPIC();
        if (!topic.equals(JSON.parseObject(logWithTS).get("logType"))) {
            topic = ConstantUtil.START_TOPIC();
        }
        kafkaTemplate.send(topic,logWithTS);
    }

    private final Logger logger = LoggerFactory.getLogger(LoggerController.class);

    /**
     * 日志落盘
     *
     * @param logWithTS
     */
    public void saveLog2File(String logWithTS) {
        logger.info(logWithTS);
    }

    /**
     * 给日志添加时间戳
     *
     * @param logWithTS
     * @return
     */
    private String addTS(String logWithTS) {
        JSONObject jsonObject = JSON.parseObject(logWithTS);

        jsonObject.put("ts", System.currentTimeMillis());

        return jsonObject.toJSONString();
    }


}
