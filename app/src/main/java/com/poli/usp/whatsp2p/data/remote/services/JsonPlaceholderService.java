package com.poli.usp.whatsp2p.data.remote.services;

import com.poli.usp.whatsp2p.data.remote.models.PostsResponse;

import java.util.List;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by azul on 17/04/17.
 */

public interface JsonPlaceholderService {
    @GET("posts")
    Observable<List<PostsResponse>> getPosts();


}
