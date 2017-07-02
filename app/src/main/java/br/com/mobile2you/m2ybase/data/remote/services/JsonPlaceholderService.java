package br.com.mobile2you.m2ybase.data.remote.services;

import java.util.List;

import br.com.mobile2you.m2ybase.data.remote.models.PostsResponse;
import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by azul on 17/04/17.
 */

public interface JsonPlaceholderService {
    @GET("posts")
    Observable<List<PostsResponse>> getPosts();


}
