package com.appster.models;

/**
 * Created by User on 8/22/2016.
 */
public class StarsToGemsModel {
    public int getExchangeId() {
        return ExchangeId;
    }

    public void setExchangeId(int exchangeId) {
        ExchangeId = exchangeId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public long getBean() {
        return Bean;
    }

    public void setBean(long bean) {
        Bean = bean;
    }

    public long getGold() {
        return Gold;
    }

    public void setGold(long gold) {
        Gold = gold;
    }

    private long Bean;
    private long Gold;
    private int ExchangeId;
    private String Title;
}
