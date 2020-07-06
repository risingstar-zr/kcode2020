package com.kuaishou.kcode;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author risingstar_zr 修改
 * date 2020-06-28
 */
public class Utils {

    /**
     * 一个主被调对的数据，包含耗时列表以及调用响应情况
     */
    static class PairData {
        List<Integer> elapsedTimes; /* 调用耗时列表 */
        int trueCount;              /* 调用成功次数 */
        int pairTotal;              /* 调用总次数 */

        PairData() {
            this.elapsedTimes = new ArrayList<>();
        }
    }

    /**
     * 一个响应者(方法名)在某一时刻的响应情况
     */
    static class ResponderData {
        int trueCount;      /* 调用成功次数 */
        int responseTotal;      /* 调用总次数 */
    }

    /* 查询 1 答案映射 */
    private Map<String, List<String>> pairAnswerMap = new HashMap<>();
    /* 查询 1 数据映射 */
    private Map<String, PairData> pairDataMap = new HashMap<>();

    /* 查询 2 一阶段的答案映射 */
    private Map<String, String> responderAnswerMap = new HashMap<>();
    /* 查询 2 每一分钟的答案 */
    private Map<String, String> responderResult = new HashMap<>();

    /* 查询 2 数据映射 */
    private Map<String, ResponderData> responderDataMap = new HashMap<>();

    /* 时间格式化对象 */
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public Utils() {
        //初始化
    }

    /**
     * 添加此次的耗时以及调用响应情况到该主被调对数据中
     *
     * @param methodAndIpPairString 调用方法和 IP 对组合字符串
     * @param elapsedTime 此次调用耗时(ms)
     * @param success 调用成功否？
     */
    public void addPairData(final String methodAndIpPairString,
                            final int elapsedTime, final boolean success) {
        //computeIfAbsent JDK8新增 用于简化操作
        PairData pairData =
                this.pairDataMap.computeIfAbsent(methodAndIpPairString, k -> new PairData());
        pairData.elapsedTimes.add(elapsedTime); /* 耗时 */
        pairData.pairTotal++;                   /* 调用总次数 */
        if(success) pairData.trueCount++;       /* 成功调用总次数 */
    }

    /**
     * 添加一个响应者在某一时刻(分)的数据
     *
     * @param responderOnStartTime 响应者+开始时间(分)
     * @param success 调用成功否？
     */
    public void addResponderData(final String responderOnStartTime, final boolean success) {
        ResponderData responderData =
                this.responderDataMap.computeIfAbsent(responderOnStartTime, k -> new ResponderData());
        responderData.responseTotal++;              /* 调用总次数 */
        if(success) responderData.trueCount++;  /* 成功调用总次数 */
    }

    /*
     * 快排
     */
    public void sort( List<Integer> a, int low, int hight) {
        int i, j, index;
        if (low > hight) {
            return;
        }
        i = low;
        j = hight;
        index = a.get(i); // 用子表的第一个记录做基准
        while (i < j) { // 从表的两端交替向中间扫描
            while (i < j && a.get(j) >= index)
                j--;
            if (i < j){
                a.set(i++,a.get(j));// 用比基准小的记录替换低位记录
            }
            while (i < j && a.get(i) < index)
                i++;
            if (i < j) // 用比基准大的记录替换高位记录
                a.set(j--,a.get(i));
        }
        a.set(i,index);// 将基准数值替换回 a[i]
        sort(a, low, i - 1); // 对低子表进行递归排序
        sort(a, i + 1, hight); // 对高子表进行递归排序
    }
    /**
     * 计算在这一分钟内主被调按ip聚合的成功率和P99
     *
     * @param methodAndIpPairString 调用方法和 IP 对组合字符串
     * @param startTime 开始时间(分)
     *  sdf
     *
     */
    public void pairUtilsCompute(final String methodAndIpPairString, int startTime) {
        PairData data = this.pairDataMap.get(methodAndIpPairString);
        if(data.pairTotal == 0) return;    /* 本次时间该主被调对未出现调用 */

        /* 首先排序 */
        List<Integer> elapsedTimes = data.elapsedTimes;
        this.sort(elapsedTimes, 0, elapsedTimes.size() - 1);
//        Collections.sort(elapsedTimes);

        /* P99 */
        int P99 = elapsedTimes.get((int) Math.ceil(elapsedTimes.size() * 0.99) - 1);

        /* 调用成功率，截取后两位小数，不进位 */
        double SR = Math.floor((double) data.trueCount / (double) data.pairTotal * 10000) / 100;

        /* 存入答案 */
        String[] methodAndIpPair = methodAndIpPairString.split("\\|");
        String key = methodAndIpPair[0] + dateFormat.format((long)startTime * 60 * 1000);
        String SRS;
        if(SR == 0.0) SRS = ".00%"; /* 0% ？那么直接是 ".00%" */
        else {
            SRS = String.format("%.2f%%", SR);
            if(SR < 1.0) {          /* 在 (0%, 1%) 开区间？截断前面的 "0"，例如 "0.75%"，那么截断为 ".75%" */
                SRS = SRS.substring(1);
            }
        }
        List<String> list = this.pairAnswerMap.computeIfAbsent(key, k -> new ArrayList<>());
        list.add(methodAndIpPair[1] + "," + SRS + "," + P99);

        /* 为了复用 */
        elapsedTimes.clear();
        data.trueCount = data.pairTotal = 0;

    }

    /**
     * 计算一个被调服务名 在某一时刻(分)的平均成功率
     * @param responderAndTime 被调名和时间组合的字符串
     */
    public void responderUtilsCompute(final String responderAndTime) {
        ResponderData responderData = this.responderDataMap.get(responderAndTime);
        if(responderData.responseTotal == 0) return;    /* 本次时间 被调服务名未出现调用 */
        /* 计算当前分钟的成功率 */
        double sucRate = 0;             /* 默认为0 */
        if(responderData != null) {
            sucRate = Math.floor((double) responderData.trueCount / (double) responderData.responseTotal * 10000) / 100;
        }
        this.responderResult.put(responderAndTime, ""+sucRate);
        /* 为了复用 */
        responderData.trueCount = responderData.responseTotal = 0;
    }

    /**
     * 计算响应者在某个时间范围内的答案
     * @param responder 响应者
     * @param start 开始时间(分)
     * @param end 结束时间(分)
     * @param responderAndStartTimeSet 响应者与时间范围组合字符串，作为答案的 key
     * @return 答案字符串(平均成功率)
     */
    public String responderAnswerCompute(String responder, String start, String end,
                                         final String responderAndStartTimeSet) {
        double sucRate, sucRateTotal = 0;
        int existTotal = 0;
        String AVG_RS_STRING = null;
        try {
            /* 得到开始时间戳(分)和结束时间戳(分) */
            int startTime = (int) (dateFormat.parse(start).getTime() / (1000 * 60));
            int endTime = (int) (dateFormat.parse(end).getTime() / (1000 * 60));

            /* 遍历闭区间内时间范围的所有调用情况并计算平均成功率 */
            while ( startTime <= endTime ) {
                /* 计算当前时间的成功率 */
                String cur  = this.responderResult.get(responder + startTime);
                if(cur != null){
                    sucRate = Double.parseDouble(cur);
                    existTotal++;/* 当前分钟存在调用 */
                    /* 加入到总成功率 */
                    sucRateTotal += sucRate;
                }
                /* 下一分钟 */
                startTime++;
            }
            /* 现在计算平均成功率 */
            AVG_RS_STRING = "-1.00%";
            if(existTotal > 0) {
                sucRate = Math.floor(sucRateTotal / existTotal * 100) / 100;
                /* 转换为字符串 */
                AVG_RS_STRING = String.format("%.2f%%", sucRate);
            }
            /* 存入答案 */
            this.responderAnswerMap.put(responderAndStartTimeSet, AVG_RS_STRING);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (existTotal > 0 ? AVG_RS_STRING : "-1.00%");
    }

    /**
     * 获取一个主被调对在某一时刻(分)的答案
     *
     * @param caller 调用者
     * @param responder 响应者
     * @param time 时间
     * @return 答案
     */
    public List<String> getPairUtils(String caller, String responder, String time) {
        String key = caller + responder + time;
        List<String> ans = this.pairAnswerMap.get(key);
        return (ans != null ? ans : new ArrayList<>());
    }

    /**
     * 获取一个响应者在某个时间范围内的答案(平均调用成功率)
     *
     * @param responder 响应者
     * @param start 开始时间(分)
     * @param end 结束时间(分)
     * @return 答案
     */
    public String getResponderUtils(String responder, String start, String end) {
        /* 如果该区间未被计算过，先进行计算 */
        final String key = responder + start + end;
        String ans = this.responderAnswerMap.get(key);
        if(ans == null) {
            ans = this.responderAnswerCompute(responder, start, end, key);
        }
        return ans;
    }


    /**
     * 计算响应者在某个时间范围内的答案
     *
     * @param responder 响应者
     * @param start 开始时间(分)
     * @param end 结束时间(分)
     * @param responderAndTimeScope 响应者与时间范围组合字符串，作为答案的 key
     * @return 答案字符串(平均成功率)
     */
//    public String responderAnswerCompute(String responder, String start, String end,
//                                         final String responderAndTimeScope) {
//        double SR, TSR = 0;
//        int totalExist = 0;
//        ResponderData data;
//        String AVG_RS_STRING = null;
//        try {
//            /* 得到开始时间戳(分)和结束时间戳(分) */
//            int startTime = (int) (dateFormat.parse(start).getTime() / (1000 * 60));
//            int endTime = (int) (dateFormat.parse(end).getTime() / (1000 * 60));
//
//            /* 遍历闭区间内时间范围的所有调用情况并计算平均成功率 */
//            while ( startTime <= endTime ) {
//                /* 计算当前时间的成功率 */
//                data = this.responderDataMap.get(responder + startTime);
//                SR = 0;             /* 假设该分钟不存在调用 */
//                if(data != null) {
//                    SR = Math.floor((double) data.trueCount / (double) data.responseTotal * 10000) / 100;
//                    totalExist++;   /* 当前分钟存在调用 */
//                }
//
//                /* 加入到总成功率 */
//                TSR += SR;
//
//                /* 下一分钟 */
//                startTime++;
//            }
//
//            /* 好的，现在计算平均成功率 */
//            AVG_RS_STRING = "-1.00%";
//            if(totalExist > 0) {
//                SR = Math.floor(TSR / totalExist * 100) / 100;
//
//                /* 转换为字符串 */
//                AVG_RS_STRING = String.format("%.2f%%", SR);
//            }
//
//            /* 存入答案 */
//            this.responderAnswerMap.put(responderAndTimeScope, AVG_RS_STRING);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return (totalExist > 0 ? AVG_RS_STRING : "-1.00%");
//    }
}
