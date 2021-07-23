package io.github.septianrin.hidrocon.model;

public class ScreenItem {

    String Title, Descripstion;
    int ScreenImg;

    public ScreenItem(String title, String descripstion, int screenImg) {
        Title = title;
        Descripstion = descripstion;
        ScreenImg = screenImg;
    }

    public String getTitle() {
        return Title;
    }

    public String getDescripstion() {
        return Descripstion;
    }

    public int getScreenImg() {
        return ScreenImg;
    }
}
