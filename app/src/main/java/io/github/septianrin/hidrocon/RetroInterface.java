package io.github.septianrin.hidrocon;

import java.util.ArrayList;
import java.util.List;

import io.github.septianrin.hidrocon.model.DataHidro;
import io.github.septianrin.hidrocon.model.DataManual;
import io.github.septianrin.hidrocon.model.DataOto;
import retrofit2.Call;
import retrofit2.http.GET;

public interface RetroInterface {

    @GET("/api/lihatdata")
    Call<DataHidro> lihatdata();

    @GET("/mode/manual")
    Call<DataManual> gomanual();

    @GET("/mode/otomatis")
    Call<DataOto> gootom();
}
