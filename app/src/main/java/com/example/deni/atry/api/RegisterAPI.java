package com.example.deni.atry.api;

import com.example.deni.atry.model.Value;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by deni on 13/06/2018.
 */

public interface RegisterAPI  {
    @FormUrlEncoded
    @POST ("insert.php")// input data android ke DB
    Call<Value> daftar(@Field("nim") String nim,
                       @Field("nama") String nama,
                       @Field("jurusan") String jurusan,
                       @Field("jk") String jk);
    @GET("view.php")
    Call<Value> view();

    @FormUrlEncoded
    @POST("search.php")
    Call<Value> search(@Field("search")String search);

    @FormUrlEncoded
    @POST("delete.php")
    Call<Value> hapus(@Field("nim")String nim);


    @FormUrlEncoded
    @POST("update.php")
    Call<Value> ubah(@Field("nim") String nim,
                     @Field("nama") String nama,
                     @Field("jurusan") String jurusan,
                     @Field("jk") String jk
                     );


}






















