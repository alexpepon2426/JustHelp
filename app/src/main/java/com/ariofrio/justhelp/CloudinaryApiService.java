package com.ariofrio.justhelp;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface CloudinaryApiService {


        @Multipart
        @POST("v1_1/{dntghzeqe}/image/upload")  // Usando tu Cloud Name en la URL
        Call<CloudinaryResponse> uploadImage(
                @Part MultipartBody.Part image,
                @Part("upload_preset") RequestBody uploadPreset
        );

}


