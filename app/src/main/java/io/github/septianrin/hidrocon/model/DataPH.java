package io.github.septianrin.hidrocon.model;

public class DataPH {
    private String date;
    private Float value;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public DataPH(String date, Float value) {
        this.date = date;
        this.value = value;
    }

    @Override
    public String toString() {
        return "DataPH{" +
                "date='" + date + '\'' +
                ", value=" + value +
                '}';
    }
}

