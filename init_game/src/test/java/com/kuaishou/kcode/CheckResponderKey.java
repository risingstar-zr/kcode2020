package com.kuaishou.kcode;

import java.util.Objects;

/**
 * @author kcode
 * Created on 2020-06-15
 */
public class CheckResponderKey {
    private String name;
    private String startTime;
    private String endTime;

    public CheckResponderKey(String name, String startTime, String endTime) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckResponderKey that = (CheckResponderKey) o;
        return name.equals(that.name) &&
                startTime.equals(that.startTime) &&
                endTime.equals(that.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, startTime, endTime);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CheckResponderKey{");
        sb.append("name='").append(name).append('\'');
        sb.append(", startTime='").append(startTime).append('\'');
        sb.append(", endTime='").append(endTime).append('\'');
        sb.append('}');
        return sb.toString();
    }
}