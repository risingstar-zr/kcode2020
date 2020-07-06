package com.kuaishou.kcode;

import static java.lang.Double.parseDouble;

import java.util.Objects;

/**
 * @author kcode
 * Created on 2020-06-15
 */
public class CheckResponderResult {
    private static final int DIFF = 5;
    private int successate;

    public CheckResponderResult(String result) {
        this.successate = (int)(parseDouble(result.replace("%", "")) * 100);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CheckResponderResult that = (CheckResponderResult) o;
        return that.successate >= successate - DIFF && that.successate <= successate + DIFF;
    }

    @Override
    public int hashCode() {
        return Objects.hash(successate);
    }

    @Override
    public String toString() {
        return successate + "%";
    }
}