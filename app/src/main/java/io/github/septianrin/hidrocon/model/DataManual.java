package io.github.septianrin.hidrocon.model;

public class DataManual {
    String ubah;
    String mode;
    int manph;
    int mantds;
    int mansuhu;

    public DataManual(String ubah, String mode, int manph, int mantds, int mansuhu) {
        this.ubah = ubah;
        this.mode = mode;
        this.manph = manph;
        this.mantds = mantds;
        this.mansuhu = mansuhu;
    }

    public String getUbah() {
        return ubah;
    }

    public void setUbah(String ubah) {
        this.ubah = ubah;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getManph() {
        return manph;
    }

    public void setManph(int manph) {
        this.manph = manph;
    }

    public int getMantds() {
        return mantds;
    }

    public void setMantds(int mantds) {
        this.mantds = mantds;
    }

    public int getMansuhu() {
        return mansuhu;
    }

    public void setMansuhu(int mansuhu) {
        this.mansuhu = mansuhu;
    }

    @Override
    public String toString() {
        return "DataManual{" +
                "ubah='" + ubah + '\'' +
                ", mode='" + mode + '\'' +
                ", manph=" + manph +
                ", mantds=" + mantds +
                ", mansuhu=" + mansuhu +
                '}';
    }
}
