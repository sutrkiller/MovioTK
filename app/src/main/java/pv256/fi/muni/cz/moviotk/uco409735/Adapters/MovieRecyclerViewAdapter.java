package pv256.fi.muni.cz.moviotk.uco409735.Adapters;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import pv256.fi.muni.cz.moviotk.uco409735.Movie;
import pv256.fi.muni.cz.moviotk.uco409735.R;

/**
 * Adapter for movie list on main page/main fragment.
 * @author Tobias <tobias.kamenicky@gmail.com>
 */

public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<MovieRecyclerViewAdapter.ViewHolder>
{
    public interface OnItemClickListener {
        void onItemClick(Movie movie);
        void onItemLongClick(Movie movie);
    }
    private final OnItemClickListener mListener;

    private Context mAppContext;
    private ArrayList<Movie> mMovies;

    public MovieRecyclerViewAdapter(Context context, ArrayList<Movie> movies, OnItemClickListener listener) {
        mAppContext = context.getApplicationContext();
        mMovies = movies;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_movie,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindView(mAppContext,mMovies.get(position),mListener);
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mBackdropImage;
        private TextView mTitle;
        private TextView mRating;
        public final View mView;
        public Movie mItem;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            mBackdropImage = (ImageView) itemView.findViewById(R.id.list_item_backdrop_image);
            mTitle = (TextView) itemView.findViewById(R.id.list_item_title);
            mRating = (TextView) itemView.findViewById(R.id.list_item_rating);
        }

        public void bindView(Context context, final Movie movie, final OnItemClickListener listener) {
            if(movie==null) return;
            mItem = movie;
            mTitle.setText(movie.getTitle());
            mRating.setText(String.format("%s", movie.getPopularity()));

            //TODO: donwload from internet
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBackdropImage.setImageDrawable(context.getDrawable(movie.getBackdropId()));
            } else {
                mBackdropImage.setImageDrawable(context.getResources().getDrawable(movie.getBackdropId()));
            }



            if(listener == null) return;

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
    }
}
