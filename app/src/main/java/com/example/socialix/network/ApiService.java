package com.example.socialix.network;

import com.example.socialix.models.AnalyticsModel;
import com.example.socialix.models.AuthRequest;
import com.example.socialix.models.AuthResponse;
import com.example.socialix.models.PostModel;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @POST("api/auth/register")
    Call<AuthResponse> register(@Body AuthRequest request);

    @POST("api/auth/login")
    Call<AuthResponse> login(@Body AuthRequest request);

    @GET("api/posts")
    Call<List<PostModel>> getPosts();

    @POST("api/posts")
    Call<PostModel> createPost(@Body PostModel post);

    @GET("api/analytics")
    Call<AnalyticsModel> getAnalytics(@Query("range") String range);
}