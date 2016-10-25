package pv256.fi.muni.cz.moviotk.uco409735;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Dummy data for MovieAdapter
 * @author Tobias <tobias.kamenicky@gmail.com>
 */

public class MovieData {
    private static MovieData sInstance;
    private ArrayList<Movie> mMovieList = new ArrayList<>();

    private MovieData() {initMoviesList();}

    public static MovieData getInstance() {
        if (sInstance == null) {
            sInstance = new MovieData();
        }
        return sInstance;
    }

    private void initMoviesList() {
        for(int i=0;i<4;++i) {
            mMovieList.add(new Movie(i*3 +1,"","",4.6F,getCurrentTime(),"Captain America: Civil War ", R.drawable.captain_america_backdrop, R.drawable.captain_america_cover));
            mMovieList.add(new Movie(i*3+2,"","",4.9F,getCurrentTime(),"Mechanic: Resurrection ", R.drawable.mechanic_backdrop, R.drawable.mechanic_cover));
            mMovieList.add(new Movie(i*3+3,"","",4.2F,getCurrentTime(),"X-men: Apocalypse", R.drawable.xmen_backdrop, R.drawable.xmen_cover));
        }
    }

    private long getCurrentTime() {
        return Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00")).getTime().getTime();
    }

    public ArrayList<Movie> getMovieList() {return mMovieList;}

    public void setMovieList(ArrayList<Movie> movieList) {mMovieList = movieList;}

}
