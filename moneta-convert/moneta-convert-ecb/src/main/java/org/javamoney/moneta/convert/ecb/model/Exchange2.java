package org.javamoney.moneta.convert.ecb.model;

/**
 * Exchange model
 */
public class Exchange2 implements Comparable<Exchange2>{

    private String currency;
    private String date;
    private double rate;

    public Exchange2() {
    }

    public Exchange2(String date, String currency, double rate) {
        this.date = date;
        this.currency = currency;
        this.rate = rate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public int compareTo(Exchange2 r) {
        return this.getDate().compareTo(r.getDate());
    }
}
