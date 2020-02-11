package com.absinthe.anywhere_.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "page_table")
public class PageEntity {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "id")
    private String mId;

    @NonNull
    @ColumnInfo(name = "title")
    private String mTitle;

    @NonNull
    @ColumnInfo(name = "priority")
    private Integer mPriority;

    @NonNull
    @ColumnInfo(name = "type")
    private Integer mType;

    @NonNull
    @ColumnInfo(name = "time_stamp")
    private String mTimeStamp;

    @ColumnInfo(name = "extra")
    private String mExtra;

    public static PageEntity Builder() {
        String time = System.currentTimeMillis() + "";
        return new PageEntity(time, "", 0, AnywhereType.CARD_PAGE, time);
    }

    public PageEntity(@NonNull String id, @NonNull String title, @NonNull int priority, @NonNull int type, @NonNull String timeStamp) {
        mId = id;
        mTitle = title;
        mPriority = priority;
        mType = type;
        mTimeStamp = timeStamp;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    public void setId(@NonNull String mid) {
        this.mId = mid;
    }

    @NonNull
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(@NonNull String mTitle) {
        this.mTitle = mTitle;
    }

    @NonNull
    public Integer getPriority() {
        return mPriority;
    }

    public void setPriority(@NonNull Integer mPriority) {
        this.mPriority = mPriority;
    }

    @NonNull
    public String getTimeStamp() {
        return mTimeStamp;
    }

    public void setTimeStamp(@NonNull String mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }

    @NonNull
    public Integer getType() {
        return mType;
    }

    public void setType(@NonNull Integer mType) {
        this.mType = mType;
    }

    public String getExtra() {
        return mExtra;
    }

    public void setExtra(String mExtra) {
        this.mExtra = mExtra;
    }
}
