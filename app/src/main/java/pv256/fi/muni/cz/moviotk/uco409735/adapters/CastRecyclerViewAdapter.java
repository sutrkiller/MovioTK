package pv256.fi.muni.cz.moviotk.uco409735.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import pv256.fi.muni.cz.moviotk.uco409735.BuildConfig;
import pv256.fi.muni.cz.moviotk.uco409735.R;
import pv256.fi.muni.cz.moviotk.uco409735.data.MovieDbApi;
import pv256.fi.muni.cz.moviotk.uco409735.helpers.AvatarTransformation;
import pv256.fi.muni.cz.moviotk.uco409735.helpers.Log;
import pv256.fi.muni.cz.moviotk.uco409735.models.Cast;

/**
 * Adapter for displaying cast list
 */

public class CastRecyclerViewAdapter extends RecyclerView.Adapter<CastRecyclerViewAdapter.ViewHolder> {

    private Context mContext;
    private Cast[] mCast;
    private int mMaxSize;

    public CastRecyclerViewAdapter(Context mContext, Cast[] mCast, int maxSize) {
        this.mContext = mContext;
        this.mCast = mCast;
        this.mMaxSize = maxSize;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_cast, parent, false);
        Log.i(CastRecyclerViewAdapter.class.getName(), "Creating view");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.i(CastRecyclerViewAdapter.class.getName(), "Binding view for position: " + position);
        holder.bindView(mContext, mCast[position]);
    }

    @Override
    public int getItemCount() {
        return mCast.length >= mMaxSize ? mMaxSize : mCast.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mAvatar;
        private TextView mName;
        private ProgressBar mLoader;

        ViewHolder(View itemView) {
            super(itemView);
            mAvatar = (ImageView) itemView.findViewById(R.id.cast_avatar);
            mName = (TextView) itemView.findViewById(R.id.cast_name);
            mLoader = (ProgressBar) itemView.findViewById(R.id.cast_image_loader);
        }

        void bindView(final Context context, final Cast cast) {
            if (cast == null) return;

            mName.setText(cast.getName());

            mLoader.setVisibility(View.VISIBLE);
            mAvatar.setVisibility(View.INVISIBLE);

            final Transformation transformation = new AvatarTransformation();

            if (BuildConfig.LOGGING_ENABLED) {
                Picasso.with(context).setIndicatorsEnabled(true);
                Picasso.with(context).setLoggingEnabled(true);
            }
            Picasso.with(context).load(MovieDbApi.IMAGES_URL + cast.getImagePath())
                    .placeholder(R.drawable.ic_account_circle_black_24dp)
                    .error(R.drawable.ic_account_circle_black_24dp)
                    .transform(transformation)
                    .fit()
                    .centerCrop()
                    .into(mAvatar, new Callback() {
                        @Override
                        public void onSuccess() {
                            mAvatar.setVisibility(View.VISIBLE);
                            mLoader.setVisibility(View.INVISIBLE);
                            Log.d(CastRecyclerViewAdapter.class.getName(), "Image downloaded");
                        }

                        @Override
                        public void onError() {
                            mAvatar.setVisibility(View.VISIBLE);
                            mLoader.setVisibility(View.INVISIBLE);
                        }
                    });


        }

    }
}
