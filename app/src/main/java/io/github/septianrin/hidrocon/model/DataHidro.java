package io.github.septianrin.hidrocon.model;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class DataHidro {
    private ArrayList<DataPH> dataPH;
    private ArrayList<DataEC> dataEC;
    private ArrayList<DataTinggi> dataTinggi;

    @Override
    public String toString() {
        return "DataHidro{" +
                "dataPH=" + dataPH +
                ", dataEC=" + dataEC +
                ", dataTinggi=" + dataTinggi +
                '}';
    }

    public ArrayList<DataPH> getDataPH() {
        return dataPH;
    }

    public void setDataPH(ArrayList<DataPH> dataPH) {
        this.dataPH = dataPH;
    }

    public ArrayList<DataEC> getDataEC() {
        return dataEC;
    }

    public void setDataEC(ArrayList<DataEC> dataEC) {
        this.dataEC = dataEC;
    }

    public ArrayList<DataTinggi> getDataTinggi() {
        return dataTinggi;
    }

    public void setDataTinggi(ArrayList<DataTinggi> dataTinggi) {
        this.dataTinggi = dataTinggi;
    }

    public DataHidro(ArrayList<DataPH> dataPH, ArrayList<DataEC> dataEC, ArrayList<DataTinggi> dataTinggi) {
        this.dataPH = dataPH;
        this.dataEC = dataEC;
        this.dataTinggi = dataTinggi;
    }
}

