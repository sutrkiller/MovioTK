package pv256.fi.muni.cz.moviotk.uco409735.Data;


import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pv256.fi.muni.cz.moviotk.uco409735.Movie;

/**
 * Created by Tobias on 11/8/2016.
 */

public class ApiClient {
    private static ApiClient mInstance = null;
    private final OkHttpClient mClient = new OkHttpClient();
    private Map<String,ArrayList<Movie>> mMovieMap = new HashMap<>();

    private ApiClient() {
    }

    public static ApiClient getInstance() {
        Log.d(ApiClient.class.getName(),"getInstance() called");
        if (mInstance == null) {
            mInstance = new ApiClient();
        }
        return mInstance;
    }

    public Map<String,ArrayList<Movie>> getMovieMap() {
        Log.d(ApiClient.class.getName(),"getMovieMap() called");
        return mMovieMap;
    }

    public ArrayList<Movie> getMovieCategory(String category) {
        Log.d(ApiClient.class.getName(),"getMovieCategory() called");
        ArrayList<Movie> list=  mMovieMap.get(category);
        return list == null ? new ArrayList<Movie>() : list;
    }

    public void setMovieMap(Map<String,ArrayList<Movie>> map) {
        Log.d(ApiClient.class.getName(),"setMovieMap() called");
        if (map!= null) {
            mMovieMap = map;
        }
    }

    public void addMovieCategory(String category, ArrayList<Movie> movies) {
        Log.d(ApiClient.class.getName(),"addMovieCategory() called");
        mMovieMap.put(category,movies);
    }

    public void addMovieMap(Map<String,ArrayList<Movie>> map) {
        Log.d(ApiClient.class.getName(),"addMovieMap() called");
        mMovieMap.putAll(map);
    }


    public Response doGet(String url) {
        Log.d(ApiClient.class.getName(),"doGet() called");
        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = mClient.newCall(request);
        try {
            Response response = call.execute();
            return response;
        } catch (IOException ex) {
            Log.d(ApiClient.class.getName(),"Exception caught, " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
            ex.printStackTrace();
        }

        return null;
    }


}
