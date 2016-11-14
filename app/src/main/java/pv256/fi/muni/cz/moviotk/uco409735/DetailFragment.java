package pv256.fi.muni.cz.moviotk.uco409735;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.text.TextUtilsCompat;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

/**
 * Movie detail is part of MainLayout on screens < 900px, otherwise single fragment.
 * @author Tobias <tobias.kamenicky@gmail.com>
 */

public class DetailFragment extends Fragment {

    public static final String TAG = DetailFragment.class.getSimpleName();
    private static final String ARGS_MOVIE = "args_movie";

    private Context mContext;
    private Movie mMovie;

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
        ImageView coverImg = (ImageView) view.findViewById(R.id.detail_cover_image);

        if(mMovie != null) {
            titleTv.setText(mMovie.getTitle());
            titleLowTv.setText(mMovie.getCoverPath());

            //Title + date
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(mMovie.getReleaseDate());
            String year = String.valueOf(cal.get(Calendar.YEAR));
            SpannableString textTitle = new SpannableString(mMovie.getTitle());
            SpannableString textDate = new SpannableString(" ("+year+")");
            textDate.setSpan(new RelativeSizeSpan(0.7f),0,textDate.length(),0);
            CharSequence titleFinal =  TextUtils.concat(textTitle,textDate);

            movieTitle.setText(titleFinal);
            setImage(backdropImg,mMovie.getBackdropId());
            setImage(coverImg,mMovie.getCoverId());

            Drawable star = ResourcesCompat.getDrawable(mContext.getResources(),R.drawable.star,mContext.getTheme());

            Resources r = getResources();
            int pxBound = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, r.getDisplayMetrics());
            if (star != null) star.setBounds(0,0,pxBound,pxBound);
            movieRating.setCompoundDrawables(star,null,null,null);

            movieRating.setText(String.valueOf(mMovie.getPopularity()));
        }

        return view;
    }

    private void setImage(ImageView imageView, int drawableId) {
        //TODO: internet
        imageView.setImageDrawable(ResourcesCompat.getDrawable(mContext.getResources(), drawableId,mContext.getTheme()));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            imageView.setImageDrawable(mContext.getDrawable(drawableId));
//        } else {
//            imageView.setImageDrawable(mContext.getResources().getDrawable(drawableId));
//        }
    }
}
