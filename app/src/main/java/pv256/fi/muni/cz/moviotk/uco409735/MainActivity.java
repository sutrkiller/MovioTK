package pv256.fi.muni.cz.moviotk.uco409735;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * Launching activity of the program.
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 */
public class MainActivity extends AppCompatActivity implements MainFragment.OnMovieSelectedListener {

    private SharedPreferences mPrefs;
    public static final String PREF_THEME = "PREF_THEME";
    public static final String APP_NAME = "MovioTK";
    private int mCurrentTheme;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Lifecycle","onCreate");
        mPrefs = getSharedPreferences(APP_NAME,MODE_PRIVATE);
        mCurrentTheme = mPrefs.getInt(PREF_THEME, R.style.Theme_NoActionBar);
        setTheme(mCurrentTheme);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if(findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            if(savedInstanceState == null) {
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.movie_detail_container,new DetailFragment(),DetailFragment.TAG)
//                        .commit();
            }
        } else {
            mTwoPane = false;
            if (getSupportActionBar() != null)
                getSupportActionBar().setElevation(0f);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        int newTheme = mPrefs.getInt(PREF_THEME, R.style.Theme_NoActionBar);
        if(newTheme!=mCurrentTheme) switchThemeOnClick(null);
        Log.i("Lifecycle","onResume");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        Log.i("Lifecycle","onCreateOptionsMenu");
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Lifecycle","onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Lifecycle","onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Lifecycle","onDestroy");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_switch_theme: switchThemeOnClick(null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void switchThemeOnClick(View view) {
        int newTheme = mCurrentTheme == R.style.Theme_NoActionBar ? R.style.Theme_ThemeSecondary_NoActionBar : R.style.Theme_NoActionBar;
        mPrefs.edit().putInt(PREF_THEME, newTheme).apply();

        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    @Override
    public void onMovieSelect(Movie movie) {
        if(mTwoPane) {
            FragmentManager fm = getSupportFragmentManager();

            DetailFragment fragment = DetailFragment.newInstance(movie);
            fm.beginTransaction()
                    .replace(R.id.movie_detail_container,fragment,DetailFragment.TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this,MovieDetailActivity.class);
            intent.putExtra(MovieDetailActivity.EXTRA_MOVIE,movie);
            startActivity(intent);
        }
    }
}
