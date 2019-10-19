package com.atguigu.realtime.gmall0508publisher.bean;

import java.util.List;
import java.util.Map;

public class SaleInfo {

    private long total;
    private List<Stat> stats;
    private List<Map<String, Object>> details;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<Stat> getStats() {
        return stats;
    }

    public void setStats(List<Stat> stats) {
        this.stats = stats;
    }

    public List<Map<String, Object>> getDetails() {
        return details;
    }

    public void setDetails(List<Map<String, Object>> details) {
        this.details = details;
    }
}
