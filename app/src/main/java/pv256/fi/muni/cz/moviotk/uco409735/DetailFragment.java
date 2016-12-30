package pv256.fi.muni.cz.moviotk.uco409735;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
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

import pv256.fi.muni.cz.moviotk.uco409735.Adapters.MovieRecyclerViewAdapter;
import pv256.fi.muni.cz.moviotk.uco409735.Data.MovieDbApi;
import pv256.fi.muni.cz.moviotk.uco409735.Db.MovieManager;

/**
 * Movie detail is part of MainLayout on screens < 900px, otherwise single fragment.
 * @author Tobias <tobias.kamenicky@gmail.com>
 */

public class DetailFragment extends Fragment {

    public static final String TAG = DetailFragment.class.getSimpleName();
    private static final String ARGS_MOVIE = "args_movie";

    private Context mContext;
    private Movie mMovie;
    private MovieManager mManager;

    public static DetailFragment newInstance(Movie movie) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARGS_MOVIE,movie);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        Bundle args = getArguments();
        if(args != null) {
            mMovie = args.getParcelable(ARGS_MOVIE);
        }
        mManager = new MovieManager(getActivity());
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_detail,container,false);

        TextView titleTv = (TextView) view.findViewById(R.id.detail_movie);
        TextView titleLowTv = (TextView) view.findViewById(R.id.detail_movie_low);
        TextView movieTitle = (TextView) view.findViewById(R.id.detail_movie_title);
        TextView movieRating = (TextView) view.findViewById(R.id.detail_movie_rating);
        ImageView backdropImg = (ImageView) view.findViewById(R.id.detail_backdrop_image);
        ProgressBar backdropLoader = (ProgressBar) view.findViewById(R.id.detail_backdrop_loader);
        ProgressBar coverLoader = (ProgressBar) view.findViewById(R.id.detail_cover_loader);

        ImageView coverImg = (ImageView) view.findViewById(R.id.detail_cover_image);
        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_detail);

        if(mMovie != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMovie.setFromDb(!mMovie.isFromDb());
                    if (!mMovie.isFromDb()) {
                        Log.d(DetailFragment.class.getName(),"Remove from db clicked");
                        mManager.remove(mMovie.getId());
                    } else {
                        Log.d(DetailFragment.class.getName(),"Add to db clicked");
                        mManager.add(mMovie);
                    }

                    fab.setImageDrawable(ContextCompat.getDrawable(getActivity(),mMovie.isFromDb() ? R.drawable.ic_grade_black_24dp : R.drawable.ic_add_black_24dp));
                }
            });
            fab.setImageDrawable(ContextCompat.getDrawable(getActivity(),mMovie.isFromDb() ? R.drawable.ic_grade_black_24dp : R.drawable.ic_add_black_24dp));

            titleTv.setText(mMovie.getTitle());

            String year = mMovie.getReleaseDate();
            SpannableString textTitle = new SpannableString(mMovie.getTitle());
            SpannableString textDate = new SpannableString(" ("+year+")");
            textDate.setSpan(new RelativeSizeSpan(0.7f),0,textDate.length(),0);
            CharSequence titleFinal =  TextUtils.concat(textTitle,textDate);

            movieTitle.setText(titleFinal);
            setImage(backdropImg,backdropLoader,mMovie.getBackdropPath(),R.drawable.backdrop_placeholder);
            setImage(coverImg,coverLoader,mMovie.getCoverPath(),R.drawable.cover_placeholder);

            Drawable star = ResourcesCompat.getDrawable(mContext.getResources(),R.drawable.star,mContext.getTheme());

            Resources r = getResources();
            int pxBound = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, r.getDisplayMetrics());
            if (star != null) star.setBounds(0,0,pxBound,pxBound);
            movieRating.setCompoundDrawables(star,null,null,null);

            movieRating.setText(String.valueOf(mMovie.getPopularity()));
        }

        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_switch_source);
        item.setVisible(false);
    }

    private void setImage(final ImageView imageView, final ProgressBar loader, String path, int placeHolderId) {
        loader.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.INVISIBLE);
        Picasso.with(mContext).setIndicatorsEnabled(true);
        Picasso.with(mContext).setLoggingEnabled(true);
        Picasso.with(mContext).load(MovieDbApi.IMAGES_URL+path)
                .placeholder(placeHolderId)
                .error(R.drawable.image_not_available)
                .fit()
                .centerCrop()
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d(MovieRecyclerViewAdapter.class.getName(),"Image downloaded.");
                        loader.setVisibility(View.INVISIBLE);
                        imageView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError() {
                        Log.d(MovieRecyclerViewAdapter.class.getName(),"Image downloading failed.");
                        loader.setVisibility(View.INVISIBLE);
                        imageView.setVisibility(View.VISIBLE);
                    }
                });
    }
}
