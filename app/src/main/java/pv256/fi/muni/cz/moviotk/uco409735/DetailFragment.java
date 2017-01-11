package pv256.fi.muni.cz.moviotk.uco409735;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import pv256.fi.muni.cz.moviotk.uco409735.adapters.CastRecyclerViewAdapter;
import pv256.fi.muni.cz.moviotk.uco409735.adapters.MovieRecyclerViewAdapter;
import pv256.fi.muni.cz.moviotk.uco409735.data.MovieDbApi;
import pv256.fi.muni.cz.moviotk.uco409735.data.MoviesStorage;
import pv256.fi.muni.cz.moviotk.uco409735.database.MovieManager;
import pv256.fi.muni.cz.moviotk.uco409735.detail.DetailContract;
import pv256.fi.muni.cz.moviotk.uco409735.detail.DetailPresenter;
import pv256.fi.muni.cz.moviotk.uco409735.helpers.Log;
import pv256.fi.muni.cz.moviotk.uco409735.models.CastWrapper;
import pv256.fi.muni.cz.moviotk.uco409735.models.Crew;
import pv256.fi.muni.cz.moviotk.uco409735.models.Movie;
import pv256.fi.muni.cz.moviotk.uco409735.service.MovieDownloadService;

/**
 * Movie detail is part of MainLayout on screens < 900px, otherwise single fragment.
 *
 * @author Tobias <tobias.kamenicky@gmail.com>
 */

public class DetailFragment extends Fragment implements DetailContract.View {

    public static final String TAG = DetailFragment.class.getSimpleName();

    private Context mContext;
    private View mView;
    private DetailContract.UserInteractions mPresenter;

    public static DetailFragment newInstance(Movie movie) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(DetailContract.ARGS_MOVIE, movie);
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        Bundle args = getArguments();
        Movie movie = null;
        if (args != null) {
            movie = args.getParcelable(DetailContract.ARGS_MOVIE);
        }
        setHasOptionsMenu(true);

        mPresenter = new DetailPresenter(this, movie, new MovieManager(mContext));
    }

    public void onSaveToFavoritesClicked() {
        mPresenter.onSaveToFavoritesClicked();
    }

    public void setFavoritesButtonDrawable(int id) {
        FloatingActionButton fab = (FloatingActionButton) mView.findViewById(R.id.fab_detail);
        fab.setImageDrawable(ContextCompat.getDrawable(getActivity(), id));
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_detail, container, false);
        if (mPresenter.valid()) {
            final Movie movie = mPresenter.getMovie();

            TextView movieTitle = (TextView) mView.findViewById(R.id.detail_movie_title);
            TextView movieRating = (TextView) mView.findViewById(R.id.detail_movie_rating);
            ImageView backdropImg = (ImageView) mView.findViewById(R.id.detail_backdrop_image);
            ProgressBar backdropLoader = (ProgressBar) mView.findViewById(R.id.detail_backdrop_loader);
            ProgressBar coverLoader = (ProgressBar) mView.findViewById(R.id.detail_cover_loader);
            ImageView coverImg = (ImageView) mView.findViewById(R.id.detail_cover_image);
            final FloatingActionButton fab = (FloatingActionButton) mView.findViewById(R.id.fab_detail);

            TextView overviewText = (TextView) mView.findViewById(R.id.detail_movie_overview);
            overviewText.setText(movie.getOverview());

            String year = movie.getReleaseDate();
            SpannableString textTitle = new SpannableString(movie.getTitle());
            SpannableString textDate = new SpannableString(" (" + year + ")");
            textDate.setSpan(new RelativeSizeSpan(0.7f), 0, textDate.length(), 0);
            CharSequence titleFinal = TextUtils.concat(textTitle, textDate);

            movieTitle.setText(titleFinal);
            setImage(backdropImg, backdropLoader, movie.getBackdropPath(), R.drawable.backdrop_placeholder, R.drawable.image_not_available);
            setImage(coverImg, coverLoader, movie.getCoverPath(), R.drawable.cover_placeholder, R.drawable.ic_broken_image_black_24dp);

            Drawable star = ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.star, mContext.getTheme());

            Resources r = getResources();
            int pxBound = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, r.getDisplayMetrics());
            if (star != null) star.setBounds(0, 0, pxBound, pxBound);
            movieRating.setCompoundDrawables(star, null, null, null);

            movieRating.setText(String.valueOf(movie.getPopularity()));

            setFavoritesButtonDrawable(movie.isFromDb() ? R.drawable.ic_grade_black_24dp : R.drawable.ic_add_black_24dp);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onSaveToFavoritesClicked();
                }
            });

            BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    setMovieDetails(mView, MoviesStorage.getInstance().getCast(movie.getId()));
                }
            };

            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver, new IntentFilter(MovieDownloadService.BROADCAST_INTENT_CAST));

            if (MoviesStorage.getInstance().getCast(movie.getId()) != null) {
                setMovieDetails(mView, MoviesStorage.getInstance().getCast(movie.getId()));
            } else {
                MovieDownloadService.startDownloadCast(getActivity(), movie.getId());
            }
        }

        return mView;
    }

    private void setMovieDetails(View mView, CastWrapper cast) {
        if (cast == null) {
            mView.findViewById(R.id.detail_header_cast).setVisibility(View.GONE);
            mView.findViewById(R.id.detail_cast_list).setVisibility(View.GONE);
            return;
        }

        TextView directorText = (TextView) mView.findViewById(R.id.detail_movie_director);
        String director = "unknown";
        for (Crew crew : cast.getCrew()) {
            if (crew.getJobDepartment().equals("Director")) {
                director = crew.getName();
                break;
            }
        }
        directorText.setText(director);

        RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.detail_cast_list);
        if (recyclerView.getAdapter() == null) {
            recyclerView.setAdapter(new CastRecyclerViewAdapter(getActivity(), cast.getCast(), 2));
        } else {
            recyclerView.swapAdapter(new CastRecyclerViewAdapter(getActivity(), cast.getCast(), 2), true);
        }

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (!MainActivity.mTwoPane) {
            MenuItem item = menu.findItem(R.id.action_switch_source);
            item.setVisible(false);
        }

    }

    private void setImage(final ImageView imageView, final ProgressBar loader, String path, int placeHolderId, int errorId) {
        loader.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.INVISIBLE);
        if (BuildConfig.LOGGING_ENABLED) {
            Picasso.with(mContext).setIndicatorsEnabled(true);
            Picasso.with(mContext).setLoggingEnabled(true);
        }
        Picasso.with(mContext).load(MovieDbApi.IMAGES_URL + path)
                .placeholder(placeHolderId)
                .error(errorId)
                .fit()
                .centerCrop()
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(MovieRecyclerViewAdapter.class.getName(), "Image downloaded.");
                        loader.setVisibility(View.INVISIBLE);
                        imageView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError() {
                        Log.d(MovieRecyclerViewAdapter.class.getName(), "Image downloading failed.");
                        loader.setVisibility(View.INVISIBLE);
                        imageView.setVisibility(View.VISIBLE);
                    }
                });
    }
}
