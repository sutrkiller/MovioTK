package pv256.fi.muni.cz.moviotk.uco409735.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import pv256.fi.muni.cz.moviotk.uco409735.Data.ApiClient;
import pv256.fi.muni.cz.moviotk.uco409735.Movie;
import pv256.fi.muni.cz.moviotk.uco409735.R;

/**
 * Adapter for movie list on main page/main fragment.
 *
 * @author Tobias <tobias.kamenicky@gmail.com>
 */

public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<MovieRecyclerViewAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(Movie movie);

        void onItemLongClick(Movie movie);
    }

    private final OnItemClickListener mListener;

    private Context mAppContext;
    private ArrayList<Movie> mMovies;
    private static OkHttpClient mClient = new OkHttpClient();

    public MovieRecyclerViewAdapter(Context context, ArrayList<Movie> movies, OnItemClickListener listener) {
        mAppContext = context.getApplicationContext();
        mMovies = movies;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_movie, parent, false);
        Log.i(MovieRecyclerViewAdapter.class.getName(), "Creating view ");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.i(MovieRecyclerViewAdapter.class.getName(), "Binding view for positin: " + position);
        holder.bindView(mAppContext, mMovies.get(position), mListener);
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mBackdropImage;
        private TextView mTitle;
        private TextView mRating;
        private RelativeLayout mRelativeLayout;

        private ProgressBar mImageLoader;

        public ViewHolder(View itemView) {
            super(itemView);
            //mView = itemView;
            mRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.list_item_text_layout);
            mBackdropImage = (ImageView) itemView.findViewById(R.id.list_item_backdrop_image);
            mTitle = (TextView) itemView.findViewById(R.id.list_item_title);
            mRating = (TextView) itemView.findViewById(R.id.list_item_rating);

            mImageLoader = (ProgressBar) itemView.findViewById(R.id.list_item_image_loader);
        }

        public void bindView(final Context context, final Movie movie, final OnItemClickListener listener) {
            if (movie == null) return;
            mTitle.setText(movie.getTitle());
            mRating.setText(String.format("%s", movie.getPopularity()));

            mBackdropImage.setVisibility(View.INVISIBLE);
            mImageLoader.setVisibility(View.VISIBLE);

            //TODO: donwload from internet
//            Drawable backDropImage = ResourcesCompat.getDrawable(context.getResources(),R.drawable.captain_america_backdrop,context.getTheme());
//            mBackdropImage.setImageDrawable(backDropImage);

            Picasso.with(context).setIndicatorsEnabled(true);
            Picasso.with(context).setLoggingEnabled(true);
            Picasso.with(context).load("https://image.tmdb.org/t/p/original" + movie.getBackdropPath())
                    .placeholder(R.drawable.backdrop_placeholder)
                    .error(R.drawable.image_not_available)
                    .fit()
                    .centerCrop()
                    .into(mBackdropImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            mBackdropImage.setVisibility(View.VISIBLE);
                            mImageLoader.setVisibility(View.INVISIBLE);

                            Log.d(MovieRecyclerViewAdapter.class.getName(), "Image downloaded.");
                            Bitmap myBitmap = drawableToBitmap(mBackdropImage.getDrawable());
                            if (myBitmap != null && !myBitmap.isRecycled()) {
                                Palette palette = Palette.from(myBitmap).generate();
                                int defaultT = ResourcesCompat.getColor(context.getResources(), R.color.colorBlack, context.getTheme());
                                mRelativeLayout.setBackgroundColor(palette.getDarkMutedColor(defaultT));
                            }
                        }

                        @Override
                        public void onError() {
                            Log.d(MovieRecyclerViewAdapter.class.getName(), "Image downloading failed.");
                            mBackdropImage.setVisibility(View.VISIBLE);
                            mImageLoader.setVisibility(View.INVISIBLE);
                        }
                    });

//            Glide.get(context)
//                    .register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(mClient));
//            Glide.with(context)
//                    .load("http://image.tmdp.org/t/p/original"+movie.getBackdropPath())
//                    .centerCrop()
//                    .placeholder(R.drawable.captain_america_backdrop)
//                    .crossFade()
//                    .error(R.drawable.mechanic_backdrop)
//                    .listener(new RequestListener<String, GlideDrawable>() {
//                        @Override
//                        public boolean onException(Exception e, String model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFirstResource) {
//                            if (e != null)e.printStackTrace();
//                            return false;
//                        }
//
//                        @Override
//                        public boolean onResourceReady(GlideDrawable resource, String model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                            return false;
//                        }
//                    })
//                    .into(mBackdropImage);

//            com.bumptech.glide.request.target.Target<GlideDrawable> t = Glide.with(context)
//                    .load("https://image.tmdp.org/t/p/original"+movie.getBackdropPath())
//                    .centerCrop()
//                    .placeholder(R.drawable.captain_america_backdrop)
//                    .crossFade()
//                    .error(R.drawable.mechanic_backdrop)
//                    .listener(new RequestListener<String, GlideDrawable>() {
//                        @Override
//                        public boolean onException(Exception e, String model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFirstResource) {
//                            e.printStackTrace();
//                            return false;
//                        }
//
//                        @Override
//                        public boolean onResourceReady(GlideDrawable resource, String model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                            return false;
//                        }
//                    })
//                    .into(mBackdropImage);

            //Bitmap myBitmap = BitmapFactory.decodeResource(context.getResources(),movie.getBackdropId());


            if (listener == null) return;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(movie);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onItemLongClick(movie);
                    return true;
                }
            });


        }

        private Bitmap drawableToBitmap(Drawable drawable) {
            Bitmap bitmap = null;

            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                if (bitmapDrawable.getBitmap() != null) {
                    return bitmapDrawable.getBitmap();
                }
            }

            if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
    }
}
