package br.com.mobile2you.m2ybase.data.remote;

import java.util.List;

import br.com.mobile2you.m2ybase.data.local.PreferencesHelper;
import br.com.mobile2you.m2ybase.data.remote.models.PollsResponse;
import br.com.mobile2you.m2ybase.data.remote.services.ApiaryService;
import rx.Observable;

/**
 * Created by mobile2you on 28/11/16.
 */

public class ApiaryDataManager {
    private final PreferencesHelper mPreferencesHelper;
    private final ApiaryService mApiaryService;

    private static ApiaryDataManager sInstance = new ApiaryDataManager();

    public static ApiaryDataManager getInstance() {
        return sInstance;
    }

    private ApiaryDataManager() {
        mPreferencesHelper = PreferencesHelper.getInstance();
        mApiaryService = ServiceGenerator.createServiceWithCookieInterceptors(ApiaryService.class);
    }

    public PreferencesHelper getPreferencesHelper() {
        return mPreferencesHelper;
    }

    public Observable<List<PollsResponse>> getPolls() {
        return mApiaryService.getPolls();
    }
    public Observable<List<PollsResponse>> deletePolls() {
        return mApiaryService.deletePolls();
    }

}
