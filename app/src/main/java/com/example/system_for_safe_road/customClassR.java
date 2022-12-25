package com.example.system_for_safe_road;

public class customClassR {
    String flag,acctualtime,delay,reachedtime;
    int isDelay;

    public customClassR(String flag, String acctualtime, String delay, String reachedtime,int isDelay) {
        this.flag = flag;
        this.acctualtime = acctualtime;
        this.delay = delay;
        this.reachedtime = reachedtime;
        this.isDelay =isDelay;
    }

    public int getIsDelay() {
        return isDelay;
    }

    public String getFlag() {
        return flag;
    }

    public String getAcctualtime() {
        return acctualtime;
    }

    public String getDelay() {
        return delay;
    }

    public String getReachedtime() {
        return reachedtime;
    }
}
