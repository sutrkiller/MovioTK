package pv256.fi.muni.cz.moviotk.uco409735.models;

import com.google.gson.annotations.SerializedName;

/**
 * Wrapper for downloading cast details.
 */

public class CastWrapper {
    @SerializedName("id")
    private long mId;
    @SerializedName("cast")
    private Cast[] mCast;
    @SerializedName("crew")
    private Crew[] mCrew;

    public CastWrapper(long mId, Cast[] mCast, Crew[] mCrew) {
        this.mId = mId;
        this.mCast = mCast;
        this.mCrew = mCrew;
    }

    public long getId() {
        return mId;
    }

    public Cast[] getCast() {
        return mCast;
    }

    public Crew[] getCrew() {
        return mCrew;
    }
}
