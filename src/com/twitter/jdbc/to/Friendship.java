package com.twitter.jdbc.to;

import java.util.Date;

public class Friendship {
	private int requserId;
	private int accuserId;
    private Date dof;
 
    public int getReqUserId() {
        return requserId;
    }
    public void setReqUserId(int requserId) {
        this.requserId = requserId;
    }
    public int getAccUserId() {
        return accuserId;
    }
    public void setAccUserId(int accuserId) {
        this.accuserId = accuserId;
    }
    public Date getDof() {
        return dof;
    }
    public void setDof(Date dof) {
        this.dof = dof;
    }
    public String toString() {
    	return "[" + requserId + "," + accuserId + "," + dof + "]";
    }
}