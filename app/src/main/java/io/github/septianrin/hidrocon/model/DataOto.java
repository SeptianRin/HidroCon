package io.github.septianrin.hidrocon.model;

public class DataOto {

    String ubah;
    String mode;

    public DataOto(String ubah, String mode) {
        this.ubah = ubah;
        this.mode = mode;
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

    @Override
    public String toString() {
        return "DataManual{" +
                "ubah='" + ubah + '\'' +
                ", mode='" + mode + '\'' +
                '}';
    }
}
