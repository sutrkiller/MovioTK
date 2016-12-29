package pv256.fi.muni.cz.moviotk.uco409735;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Model for data downloaded from API.
 * @author Tobias <tobias.kamenicky@gmail.com>
 */

public class Movie implements Parcelable {

    @SerializedName("id")
    private long mId;
    @SerializedName("release_date")
    private String mReleaseDate;
    @SerializedName("poster_path")
    private String mCoverPath;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("backdrop_path")
    private String mBackdropPath;
    @SerializedName("vote_average")
    private float mPopularity;
    private transient boolean mFromDb;

    //TODO: temporary until url is given
    //private int mBackdropId;
    //private int mCoverId;

    public Movie(long id, String title, float popularity, String coverPath, String backdrop, String releaseDate, boolean fromDb ) {
        mId = id;
        mBackdropPath = backdrop;
        mCoverPath = coverPath;
        mPopularity = popularity;
        mReleaseDate = releaseDate;
        mTitle = title;
        mFromDb = fromDb;
    }

    public Movie(Parcel in) {
        mId = in.readLong();
        mReleaseDate = in.readString();
        mCoverPath = in.readString();
        mTitle = in.readString();
        mBackdropPath = in.readString();
        mPopularity = in.readFloat();
        mFromDb = in.readInt() > 0;
    }

    public String getBackdropPath() {
        return mBackdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        mBackdropPath = backdropPath;
    }

    public String getCoverPath() {
        return mCoverPath;
    }

    public void setCoverPath(String coverPath) {
        mCoverPath = coverPath;
    }

    public float getPopularity() {
        return mPopularity;
    }

    public void setPopularity(float popularity) {
        mPopularity = popularity;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        mReleaseDate = releaseDate;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mReleaseDate);
        dest.writeString(mCoverPath);
        dest.writeString(mTitle);
        dest.writeString(mBackdropPath);
        dest.writeFloat(mPopularity);
        dest.writeInt(mFromDb ? 1 : 0);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };


    public boolean isFromDb() {
        return mFromDb;
    }

    public void setFromDb(boolean mFromDb) {
        this.mFromDb = mFromDb;
    }
}
