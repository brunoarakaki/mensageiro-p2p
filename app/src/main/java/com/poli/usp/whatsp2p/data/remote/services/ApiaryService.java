package com.poli.usp.whatsp2p.data.remote.services;

import java.util.List;

import com.poli.usp.whatsp2p.data.remote.models.PollsResponse;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by mobile2you on 28/11/16.
 */

public interface ApiaryService {

    @GET("questions")
    Observable<List<PollsResponse>> getPolls();

    @DELETE("questions")
    Observable<List<PollsResponse>> deletePolls();
}
