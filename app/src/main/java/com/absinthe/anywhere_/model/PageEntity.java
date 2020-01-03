package com.absinthe.anywhere_.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "page_table")
public class PageEntity {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "title")
    private String mTitle;

    @NonNull
    @ColumnInfo(name = "priority")
    private Integer mPriority;

    @NonNull
    @ColumnInfo(name = "time_stamp")
    private String mTimeStamp;

    public PageEntity(@NonNull String title, @NonNull int priority, @NonNull String timeStamp) {
        mTitle = title;
        mPriority = priority;
        mTimeStamp = timeStamp;
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
}
