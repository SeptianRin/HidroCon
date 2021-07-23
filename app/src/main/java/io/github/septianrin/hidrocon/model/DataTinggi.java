package io.github.septianrin.hidrocon.model;

public class DataTinggi {
    private String date;
    private Float value;

    public DataTinggi(String date, Float value) {
        this.date = date;
        this.value = value;
    }

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

    @Override
    public String toString() {
        return "DataSuhu{" +
                "date='" + date + '\'' +
                ", value=" + value +
                '}';
    }
}
