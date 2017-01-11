package pv256.fi.muni.cz.moviotk.uco409735.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Model for cast
 */

public class Cast implements Parcelable {
    public static final Parcelable.Creator<Cast> CREATOR = new Parcelable.Creator<Cast>() {
        @Override
        public Cast createFromParcel(Parcel source) {
            return new Cast(source);
        }

        @Override
        public Cast[] newArray(int size) {
            return new Cast[size];
        }
    };
    @SerializedName("id")
    private long mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("character")
    private String mCharacter;
    @SerializedName("profile_path")
    private String mImagePath;

    public Cast(long id, String name, String character, String imagePath) {
        mId = id;
        mName = name;
        mCharacter = character;
        mImagePath = imagePath;

    }

    private Cast(Parcel in) {
        mId = in.readLong();
        mName = in.readString();
        mCharacter = in.readString();
        mImagePath = in.readString();
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getCharacter() {
        return mCharacter;
    }

    public void setCharacter(String mCharacter) {
        this.mCharacter = mCharacter;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String mImagePath) {
        this.mImagePath = mImagePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeString(mName);
        dest.writeString(mCharacter);
        dest.writeString(mImagePath);
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cast cast = (Cast) o;

        if (mId != cast.mId) return false;
        if (!mName.equals(cast.mName)) return false;
        if (mCharacter != null ? !mCharacter.equals(cast.mCharacter) : cast.mCharacter != null)
            return false;
        return mImagePath != null ? mImagePath.equals(cast.mImagePath) : cast.mImagePath == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (mId ^ (mId >>> 32));
        result = 31 * result + mName.hashCode();
        result = 31 * result + (mCharacter != null ? mCharacter.hashCode() : 0);
        result = 31 * result + (mImagePath != null ? mImagePath.hashCode() : 0);
        return result;
    }
}
