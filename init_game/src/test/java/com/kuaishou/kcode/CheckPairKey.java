package com.kuaishou.kcode;

import java.util.Objects;

/**
 * @author kcode
 * Created on 2020-05-16
 */
public class CheckPairKey {
    private String caller;
    private String responder;
    private String time;

    public CheckPairKey(String key) {
        String[] checkKey = key.split(",");
        caller = checkKey[0];
        responder = checkKey[1];
        time = checkKey[2];
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public String getResponder() {
        return responder;
    }

    public void setResponder(String responder) {
        this.responder = responder;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckPairKey checkPairKey = (CheckPairKey) o;
        return caller.equals(checkPairKey.caller) &&
                responder.equals(checkPairKey.responder) &&
                time.equals(checkPairKey.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(caller, responder, time);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CheckKey{");
        sb.append("caller='").append(caller).append('\'');
        sb.append(", responder='").append(responder).append('\'');
        sb.append(", time='").append(time).append('\'');
        sb.append('}');
        return sb.toString();
    }
}