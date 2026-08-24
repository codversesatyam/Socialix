package com.example.socialix.network;

import com.example.socialix.models.PostModel;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @GET("api/posts")
    Call<List<PostModel>> getAllPosts();

    @POST("api/posts")
    Call<PostModel> createPost(@Body PostModel post);
}
