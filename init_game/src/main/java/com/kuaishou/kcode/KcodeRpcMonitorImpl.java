package com.kuaishou.kcode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * @author risingstar_zr
 * Created on 2020-06-22
 * 实际提交时请维持包名和类名不变
 */

public class KcodeRpcMonitorImpl implements KcodeRpcMonitor {
    /* 答案 */
    private Utils utils = new Utils();

    /* 数据区域 */
    private HashSet<String> methodAndIpPairStringSet = new HashSet<>();
    private HashSet<String> responderAndStartTimeSet = new HashSet<>();
//    private String[] methodAndIpPairStringArray = null;
//    private String[] responderAndStartTimeArray = null;

    // 不要修改访问级别
    public KcodeRpcMonitorImpl() {
    }

    public void prepare(String path)throws IOException {
        /* 初始化当前时间 */
        BufferedReader inputStream = new BufferedReader(new FileReader(path));
        int currTime = (int) (Long.parseLong(inputStream.readLine().split(",")[6]) / (1000 * 60));
        inputStream.close();

        /* 得到所有主调对 */
        inputStream = new BufferedReader(new FileReader(path));
        String line;
        String[] split;

        String methodAndIpPairString;
        String caller;      /* 调用方 */
        String callerIP;    /* 调用方 IP */
        String responder;      /* 被调用目标方 */
        String responderIP;    /* 被调用目标方 IP */
        boolean success;      /* 调用成功？ */
        int elapsedTime;    /* 调用耗时 */
        int startTime;      /* 调用开始时间 */
        while ((line = inputStream.readLine()) != null) {
            /* 先转换 */
            split = line.split(",");
            caller = split[0]; //调用方
            callerIP = split[1]; //调用方 IP
            responder = split[2]; //被调用目标方
            responderIP = split[3]; //被调用目标方IP
            success = Boolean.parseBoolean(split[4]);//调用成功or失败
            elapsedTime = Integer.parseInt(split[5]);//耗时(ms)
            startTime = (int) (Long.parseLong(split[6]) / (1000 * 60));   /* 按分钟算 */
            methodAndIpPairString = caller + responder + "|" + callerIP + "," + responderIP;
            //新增被调服务名平均成功率的计算
            String responderAndStartTime = responder+startTime;
            if(startTime != currTime) {
                /* 计算 */
                Iterator io_1= methodAndIpPairStringSet.iterator();
                while(io_1.hasNext()){
                    utils.pairUtilsCompute((String) io_1.next(), currTime);
                }
                Iterator io_2= responderAndStartTimeSet.iterator();
                while(io_2.hasNext()){
                    String temp = (String)  io_2.next();
                    utils.responderUtilsCompute(temp);
                }
                /* 更新当前时间 */
                currTime = startTime;
                methodAndIpPairStringSet.clear();
                responderAndStartTimeSet.clear();
            }
            /* 将一个主被调对加入集合 */

            methodAndIpPairStringSet.add(methodAndIpPairString);
            responderAndStartTimeSet.add(responderAndStartTime);
            /* 加入到输入数据区域 */
            utils.addPairData(methodAndIpPairString, elapsedTime, success);
            utils.addResponderData(responderAndStartTime, success);
        }
        //最后一分钟数据的计算
        Iterator io_1= methodAndIpPairStringSet.iterator();
        while(io_1.hasNext()){
            utils.pairUtilsCompute((String) io_1.next(), currTime);
        }
        Iterator io_2= responderAndStartTimeSet.iterator();
        while(io_2.hasNext()){
            String temp = (String)  io_2.next();
            utils.responderUtilsCompute(temp);
        }
    }

    public List<String> checkPair(String caller, String responder, String time) {
        return utils.getPairUtils(caller, responder, time);
    }

    public String checkResponder(String responder, String start, String end) {
        return utils.getResponderUtils(responder, start, end);
    }

}
