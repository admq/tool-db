package com.cootoo.tool;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by larry on 16/6/29.
 */
public class Log {
    private String logID;
    private String loginAccount;
    private String ip;
    private Date operatorTime;
    private String refer;
    private String url;


    public String getLogID() {
        return logID;
    }

    public void setLogID(String logID) {
        this.logID = logID;
    }

    public String getLoginAccount() {
        return loginAccount;
    }

    public void setLoginAccount(String loginAccount) {
        this.loginAccount = loginAccount;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getOperatorTime() {
        return operatorTime;
    }

    public void setOperatorTime(Date operatorTime) {
        this.operatorTime = operatorTime;
    }
    public void setOperatorTime(String operatorTime) {
        this.operatorTime = new Date(operatorTime);
    }
    public void setOperatorTime(Timestamp operatorTime) {
        this.operatorTime = new Date(operatorTime.getTime());
    }

    public String getRefer() {
        return refer;
    }

    public void setRefer(String refer) {
        this.refer = refer;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public String toString() {
        return  logID + " : " +
                loginAccount + " : " +
                ip + " : " +
                refer + " : " +
                operatorTime + " : " +
                url;
    }
}
