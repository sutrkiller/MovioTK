package pv256.fi.muni.cz.moviotk.uco409735.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Model for crew
 */

@SuppressWarnings("unused")
public class Crew implements Parcelable {
    public static final Creator<Crew> CREATOR = new Creator<Crew>() {
        @Override
        public Crew createFromParcel(Parcel source) {
            return new Crew(source);
        }

        @Override
        public Crew[] newArray(int size) {
            return new Crew[size];
        }
    };
    @SerializedName("id")
    private long mId;
    @SerializedName("name")
    private String mName;
    @SerializedName("department")
    private String mDepartment;
    @SerializedName("job")
    private String mJob;
    @SerializedName("profile_path")
    private String mImagePath;

    public Crew(long id, String name, String department, String job, String imagePath) {
        mId = id;
        mName = name;
        mDepartment = department;
        mJob = job;
        mImagePath = imagePath;

    }

    private Crew(Parcel in) {
        mId = in.readLong();
        mName = in.readString();
        mDepartment = in.readString();
        mJob = in.readString();
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

    public String getDepartment() {
        return mDepartment;
    }

    public void setDepartment(String department) {
        this.mDepartment = department;
    }

    public String getJobDepartment() {
        return mJob;
    }

    public void setJob(String job) {
        this.mJob = job;
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
        dest.writeString(mDepartment);
        dest.writeString(mJob);
        dest.writeString(mImagePath);
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Crew crew = (Crew) o;

        if (mId != crew.mId) return false;
        if (!mName.equals(crew.mName)) return false;
        if (!mDepartment.equals(crew.mDepartment)) return false;
        return mJob.equals(crew.mJob) && (mImagePath != null ? mImagePath.equals(crew.mImagePath) : crew.mImagePath == null);

    }

    @Override
    public int hashCode() {
        int result = (int) (mId ^ (mId >>> 32));
        result = 31 * result + mName.hashCode();
        result = 31 * result + mDepartment.hashCode();
        result = 31 * result + mJob.hashCode();
        result = 31 * result + (mImagePath != null ? mImagePath.hashCode() : 0);
        return result;
    }
}
