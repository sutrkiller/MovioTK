package pv256.fi.muni.cz.moviotk.uco409735;

import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * Dummy data for MovieAdapter
 * @author Tobias <tobias.kamenicky@gmail.com>
 */

public class MovieData {
    private static MovieData sInstance;
    private Map<String,ArrayList<Movie>> mMovieMap = new HashMap<>(); //TODO: ArrayMap from API 17

    private MovieData() {initMoviesList();}

    public static MovieData getInstance() {
        if (sInstance == null) {
            sInstance = new MovieData();
        }
        return sInstance;
    }

    private void initMoviesList() {
        for(int i=0;i<4;++i) {
            ArrayList<Movie> movieList = new ArrayList<>();
            movieList.add(new Movie(i*3 +1,"","",4.6F,getCurrentTime(),"Captain America: Civil War ", R.drawable.captain_america_backdrop, R.drawable.captain_america_cover));
            movieList.add(new Movie(i*3+2,"","",4.9F,getCurrentTime(),"Mechanic: Resurrection ", R.drawable.mechanic_backdrop, R.drawable.mechanic_cover));
            movieList.add(new Movie(i*3+3,"","",4.2F,getCurrentTime(),"X-men: Apocalypse", R.drawable.xmen_backdrop, R.drawable.xmen_cover));
            mMovieMap.put("Category "+(i+1),movieList);
        }
    }

    private long getCurrentTime() {
        return Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00")).getTime().getTime();
    }

    public Map<String,ArrayList<Movie>> getMovieMap() {return mMovieMap;}

    public void setMovieList(Map<String,ArrayList<Movie>> movieList) {mMovieMap = movieList;}

}
