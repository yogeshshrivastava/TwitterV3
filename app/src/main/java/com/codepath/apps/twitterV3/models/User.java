package com.codepath.apps.twitterV3.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * author yvastavaus.
 */
public class User implements Parcelable {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("id")
    @Expose
    private long id;

    @SerializedName("screen_name")
    @Expose
    private String screenName;

    @SerializedName("profile_image_url")
    @Expose
    private String profileImageUrl;

    @SerializedName("description")
    @Expose
    private String tagLine;

    @SerializedName("followers_count")
    @Expose
    private int followersCount;


    @SerializedName("friends_count")
    @Expose
    private String followingCount;


    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getFollowingCount() {
        return followingCount;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public String getTagLine() {
        return tagLine;
    }


    public User() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeLong(this.id);
        dest.writeString(this.screenName);
        dest.writeString(this.profileImageUrl);
        dest.writeString(this.tagLine);
        dest.writeInt(this.followersCount);
        dest.writeString(this.followingCount);
    }

    protected User(Parcel in) {
        this.name = in.readString();
        this.id = in.readLong();
        this.screenName = in.readString();
        this.profileImageUrl = in.readString();
        this.tagLine = in.readString();
        this.followersCount = in.readInt();
        this.followingCount = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
