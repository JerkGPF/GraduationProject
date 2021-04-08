package com.gpfei.graduationproject.beans;

import com.gpfei.graduationproject.beans.DayBean;
import com.gpfei.graduationproject.beans.User;

import cn.bmob.v3.BmobObject;

public class SelectAndResume extends BmobObject {
    private DayBean dayBean ;
    private User user;
    private Boolean delivery;
    private Boolean collect;

    public Boolean getDelivery() {
        return delivery;
    }
    public void setDelivery(Boolean delivery) {
        this.delivery = delivery;
    }
    public Boolean getCollect() {
        return collect;
    }
    public void setCollect(Boolean collect) {
        this.collect = collect;
    }

    public DayBean getDayBean() {
        return dayBean;
    }

    public DayBean getdayBean(){
        return dayBean;
    }
    public void setDayBean(DayBean dayBean) {
        this.dayBean = dayBean;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "SelectAndResume{" +
                "dayBean=" + dayBean +
                ", user=" + user +
                ", delivery=" + delivery +
                ", collect=" + collect +
                '}';
    }
}
