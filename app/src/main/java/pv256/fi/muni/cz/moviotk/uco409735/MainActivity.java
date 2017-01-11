package pv256.fi.muni.cz.moviotk.uco409735;

import android.animation.ValueAnimator;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.CompoundButton;
import android.widget.Switch;

import pv256.fi.muni.cz.moviotk.uco409735.database.MovieManager;
import pv256.fi.muni.cz.moviotk.uco409735.database.MovioContract;
import pv256.fi.muni.cz.moviotk.uco409735.main.MainContract;
import pv256.fi.muni.cz.moviotk.uco409735.main.MainPresenter;
import pv256.fi.muni.cz.moviotk.uco409735.models.Movie;
import pv256.fi.muni.cz.moviotk.uco409735.sync.UpdaterSyncAdapter;

/**
 * Launching activity of the program.
 *
 * @author Tobias Kamenicky <tobias.kamenicky@gmail.com>
 */
public class MainActivity extends AppCompatActivity implements MainFragment.OnMovieSelectedListener, NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor>, MainContract.View {

    private SharedPreferences mPrefs;
    public static final String PREF_THEME = "PREF_THEME";
    public static final String SELECTED_GENRES = "SELECTED_GENRES";
    public static final String SELECTED_SOURCE = "SELECTED_SOURCE";
    public static final String APP_NAME = "MovioTK";
    private int mCurrentTheme;
    private boolean mTwoPane;
    private MainContract.UserInteractions mPresenter;

    private boolean mSource;
    private ActionBarDrawerToggle mToggle;
    private FragmentManager.OnBackStackChangedListener mOnBackStackChangedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(MainActivity.class.getName(), "onCreate");

        mPresenter = new MainPresenter(this);

        mPrefs = getSharedPreferences(APP_NAME, MODE_PRIVATE);
        mCurrentTheme = mPrefs.getInt(PREF_THEME, R.style.Theme_NoActionBar);
        setTheme(mCurrentTheme);

        getLoaderManager().initLoader(1, null, this);
        UpdaterSyncAdapter.initializeSyncAdapter(this);

        setUpContentView(savedInstanceState);
        setUpToolbar();

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
        } else {
            mTwoPane = false;
            if (getSupportActionBar() != null)
                getSupportActionBar().setElevation(0f);
        }

        prepareNavigationDrawer();
        restoreSelectedGenres();
        mSource = mPrefs.getBoolean(SELECTED_SOURCE, false);
        reloadMovies(mSource);
    }

    public void reloadMovies(boolean fromDb) {
        MainFragment fr = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
        if (fr == null) return;
        View view = fr.getView();
        fr.loadMovies(view, getSelectedGenres(), fromDb);
    }

    private String[] mGenres = {"28", "12", "16", "35", "80", "99", "18", "10751", "14", "36", "27", "10402", "9648", "10749", "878", "10770", "53", "10752", "37"};

    private String getSelectedGenres() {
        NavigationView view = (NavigationView) findViewById(R.id.nav_view);
        if (view == null) return "";
        Menu menu = view.getMenu().findItem(R.id.nav_genres).getSubMenu();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < menu.size(); ++i) {
            if (!menu.getItem(i).isChecked()) continue;
            result.append(mGenres[i]).append(",");
        }
        if (result.length() > 0) result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    private void setUpContentView(Bundle savedInstanceState) {
        DetailFragment f = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DetailFragment.TAG);
        if (savedInstanceState != null && f != null) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) { //it was detail view, or two pain in horizontal
                getSupportFragmentManager().popBackStackImmediate();
            } else {
                getSupportFragmentManager().beginTransaction().remove(f).commit();
            }
            setContentView(R.layout.activity_main);

            if (findViewById(R.id.movie_detail_container) != null) { //two pane again
                getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container, f, DetailFragment.TAG).commit();
            } else {
                getSupportFragmentManager().executePendingTransactions();
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_main, f, MainFragment.TAG).addToBackStack(null).commit();
            }
        } else {
            setContentView(R.layout.activity_main);
        }
    }

    private void restoreSelectedGenres() {
        String genres = mPrefs.getString(SELECTED_GENRES, "");

        String[] split = genres.split(";");

        NavigationView view = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = view.getMenu().findItem(R.id.nav_genres).getSubMenu();

        if (split.length != menu.size()) return;
        for (int i = 0; i < split.length; ++i) {
            MenuItem item = menu.getItem(i);
            item.setChecked(split[i].equals("1"));
        }
    }

    private void saveSelectedGenres() {
        NavigationView view = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = view.getMenu().findItem(R.id.nav_genres).getSubMenu();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < menu.size(); ++i) {
            result.append(menu.getItem(i).isChecked() ? "1" : "0").append(";");
        }
        if (result.length() > 0) result.deleteCharAt(result.length() - 1);
        mPrefs.edit().putString(SELECTED_GENRES, result.toString()).apply();
    }

    private void prepareNavigationDrawer() {
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
                syncState();
                reloadMovies(mSource);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                syncState();
            }

        };
        drawer.addDrawerListener(mToggle);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mOnBackStackChangedListener = new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                final int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
                int start = backStackCount == 0 ? 1 : 0;
                final ValueAnimator anim = ValueAnimator.ofFloat(start, 1 - start);
                anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {
                        float slideOffset = (Float) valueAnimator.getAnimatedValue();
                        mToggle.onDrawerSlide(drawer, slideOffset);

                    }
                });
                anim.setInterpolator(new DecelerateInterpolator());
                anim.setDuration(500);

                anim.start();
                drawer.setDrawerLockMode(backStackCount == 0 ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
        };
        getSupportFragmentManager().addOnBackStackChangedListener(mOnBackStackChangedListener);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setUpToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        mToggle.onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        Log.i(MainActivity.class.getName(), "onResume");
        super.onResume();
        int newTheme = mPrefs.getInt(PREF_THEME, R.style.Theme_NoActionBar);
        if (newTheme != mCurrentTheme) switchThemeOnClick(null);

    }

    public void showMessage(String text) {
        Snackbar.make(findViewById(R.id.activity_main_frame_layout), text, Snackbar.LENGTH_SHORT).show();
    }

    public void restartLoader() {
        getLoaderManager().restartLoader(1, null, MainActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(MainActivity.class.getName(), "onCreateOptionsMenu");

        getMenuInflater().inflate(R.menu.menu_main, menu);
        Switch sourceSwitch = (Switch) menu.findItem(R.id.action_switch_source).getActionView().findViewById(R.id.switchForActionBar);
        sourceSwitch.setChecked(mSource);

        sourceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mSource = isChecked;
                mPresenter.onDbSourceCheckedChange(isChecked);
            }
        });
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(MainActivity.class.getName(), "onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(MainActivity.class.getName(), "onPause");
        saveSelectedGenres();
        mPrefs.edit().putBoolean(SELECTED_SOURCE, mSource).apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(MainActivity.class.getName(), "onDestroy");
        getSupportFragmentManager().removeOnBackStackChangedListener(mOnBackStackChangedListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0 && mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (id) {
            case android.R.id.home:
                getSupportFragmentManager().popBackStackImmediate();
                return true;
            case R.id.action_switch_theme:
                switchThemeOnClick(null);
                return true;
            case R.id.action_update_db:
                mPresenter.updateDb(this, mSource);
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
        FragmentManager fm = getSupportFragmentManager();
        DetailFragment fragment = DetailFragment.newInstance(movie);
        if (mTwoPane) {
            fm.beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DetailFragment.TAG)
                    .commit();
        } else {
            fm.beginTransaction()
                    .add(R.id.fragment_main, fragment, DetailFragment.TAG)
                    .addToBackStack(null)
                    .commit();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(MainActivity.class.getName(), "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        item.setChecked(!item.isChecked());
        return false;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, MovioContract.MovieEntry.CONTENT_URI, MovieManager.MOVIE_COLS, null, null, MovioContract.MovieEntry.COLUMN_RELEASE_DATE + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mPresenter.onLoaderFinished(data, mSource);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
