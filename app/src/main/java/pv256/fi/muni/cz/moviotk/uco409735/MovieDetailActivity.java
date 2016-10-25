package pv256.fi.muni.cz.moviotk.uco409735;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import static pv256.fi.muni.cz.moviotk.uco409735.MainActivity.APP_NAME;
import static pv256.fi.muni.cz.moviotk.uco409735.MainActivity.PREF_THEME;

/**
 * Activity displaying detail of selected movie from MainActivity. Only on screens < 900px.
 * @author Tobias <tobias.kamenicky@gmail.com>
 */

public class MovieDetailActivity extends AppCompatActivity {
    public static final String EXTRA_MOVIE = "extra_movie";
    private int mCurrentTheme;
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = getSharedPreferences(APP_NAME,MODE_PRIVATE);
        mCurrentTheme = mPrefs.getInt(PREF_THEME, R.style.Theme_NoActionBar);
        setTheme(mCurrentTheme);

        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if(savedInstanceState == null) {
            Movie movie = getIntent().getParcelableExtra(EXTRA_MOVIE);
            FragmentManager fm = getSupportFragmentManager();
            DetailFragment fragment = (DetailFragment)fm.findFragmentById(R.id.movie_detail_container);

            if(fragment == null) {
                fragment = DetailFragment.newInstance(movie);
                fm.beginTransaction().add(R.id.movie_detail_container,fragment)
                        .commit();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                //NavUtils.navigateUpFromSameTask(this);
                //navigateUpTo(new Intent(this,MainActivity.class));
                //NavUtils.navigateUpTo(this, NavUtils.getParentActivityIntent(this));
                return true;
            case R.id.action_switch_theme: switchThemeOnClick(null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail,menu);
        return true;
    }

    public void switchThemeOnClick(View view) {
        int newTheme = mCurrentTheme == R.style.Theme_NoActionBar ? R.style.Theme_ThemeSecondary_NoActionBar : R.style.Theme_NoActionBar;
        mPrefs.edit().putInt(PREF_THEME, newTheme).apply();

        Intent intent = getIntent();

        finish();

        startActivity(intent);
    }
}
