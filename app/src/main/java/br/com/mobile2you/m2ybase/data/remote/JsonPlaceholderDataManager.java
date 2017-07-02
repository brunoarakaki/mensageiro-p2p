package br.com.mobile2you.m2ybase.data.remote;

import java.util.List;

import br.com.mobile2you.m2ybase.data.local.PreferencesHelper;
import br.com.mobile2you.m2ybase.data.remote.models.PostsResponse;
import br.com.mobile2you.m2ybase.data.remote.services.JsonPlaceholderService;
import rx.Observable;

/**
 * Created by mobile2you on 28/11/16.
 */

public class JsonPlaceholderDataManager {
    private final PreferencesHelper mPreferencesHelper;
    private final JsonPlaceholderService mApiaryService;

    private static JsonPlaceholderDataManager sInstance = new JsonPlaceholderDataManager();

    public static JsonPlaceholderDataManager getInstance() {
        return sInstance;
    }

    private JsonPlaceholderDataManager() {
        mPreferencesHelper = PreferencesHelper.getInstance();
        mApiaryService = ServiceGenerator.createServiceWithCookieInterceptors(JsonPlaceholderService.class, "https://jsonplaceholder.typicode.com/");
    }

    public PreferencesHelper getPreferencesHelper() {
        return mPreferencesHelper;
    }

    public Observable<List<PostsResponse>> getPolls() {
        return mApiaryService.getPosts();
    }

}
