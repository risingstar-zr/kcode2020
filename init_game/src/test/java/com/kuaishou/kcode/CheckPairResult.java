package com.kuaishou.kcode;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

import java.util.Objects;

/**
 * @author kcode
 * Created on 2020-06-15
 */
public class CheckPairResult {
    private static final int DIFF = 5;

    private String callerIP;
    private String responderIP;
    private int successate;
    private int p99;

    public CheckPairResult(String result) {
        String[] split = result.split(",");
        this.callerIP = split[0];
        this.responderIP = split[1];
        this.successate = (int) (parseDouble(split[2].replace("%", "")) * 100);
        this.p99 = parseInt(split[3]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CheckPairResult that = (CheckPairResult) o;

        boolean res = (that.successate >= (successate - DIFF) && that.successate <= (successate + DIFF)) &&
                p99 == that.p99 &&
                Objects.equals(callerIP, that.callerIP) &&
                Objects.equals(responderIP, that.responderIP);
        if (!res) {
            System.err.println(that.toString() + "  " + that.successate);
            System.err.println(this.toString() + "  " + successate);
        }
        return res;
    }

    @Override
    public int hashCode() {
        return Objects.hash(callerIP, responderIP, p99);
    }

    @Override
    public String toString() {
        return callerIP + "," + responderIP + "," + successate + "%," + p99;
    }
}

