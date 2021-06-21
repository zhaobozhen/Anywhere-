package com.absinthe.anywhere_.model.database;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.absinthe.anywhere_.constants.AnywhereType;
import com.absinthe.anywhere_.constants.GlobalValues;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "anywhere_table")
public class AnywhereEntity implements Parcelable {

    public static final String APP_NAME = "app_name";
    public static final String PARAM_1 = "param_1";
    public static final String PARAM_2 = "param_2";
    public static final String PARAM_3 = "param_3";
    public static final String DESCRIPTION = "description";
    public static final String TYPE = "type";
    public static final String CATEGORY = "category";
    public static final String TIMESTAMP = "time_stamp";
    public static final String COLOR = "color";
    public static final String ICON_URI = "iconUri";
    public static final String EXEC_WITH_ROOT = "execWithRoot";

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = BaseColumns._ID)
    @SerializedName("id")
    private String mId;

    @NonNull
    @ColumnInfo(name = APP_NAME)
    @SerializedName(APP_NAME)
    private String mAppName;

    @NonNull
    @ColumnInfo(name = PARAM_1)
    @SerializedName(PARAM_1)
    private String mParam1;

    @ColumnInfo(name = PARAM_2)
    @SerializedName(PARAM_2)
    private String mParam2;

    @ColumnInfo(name = PARAM_3)
    @SerializedName(PARAM_3)
    private String mParam3;

    @ColumnInfo(name = DESCRIPTION)
    @SerializedName(DESCRIPTION)
    private String mDescription;

    @NonNull
    @ColumnInfo(name = TYPE)
    @SerializedName(TYPE)
    private Integer mType;

    @ColumnInfo(name = CATEGORY)
    @SerializedName(CATEGORY)
    private String mCategory;

    @NonNull
    @ColumnInfo(name = TIMESTAMP)
    @SerializedName(TIMESTAMP)
    private String mTimeStamp;

    @NonNull
    @ColumnInfo(name = COLOR)
    @SerializedName(COLOR)
    private Integer mColor;

    @ColumnInfo(name = ICON_URI)
    @SerializedName(ICON_URI)
    private String mIconUri;

    @NonNull
    @ColumnInfo(name = EXEC_WITH_ROOT)
    @SerializedName(EXEC_WITH_ROOT)
    private Boolean mExecWithRoot;

    public AnywhereEntity() {
        String time = String.valueOf(System.currentTimeMillis());
        mId = time;
        mAppName = "";
        mParam1 = "";
        mParam2 = "";
        mParam3 = "";
        mDescription = "";
        mType = AnywhereType.Card.NOT_CARD;
        mCategory = GlobalValues.INSTANCE.getCategory();
        mTimeStamp = time;
        mColor = 0;
        mIconUri = "";
        mExecWithRoot = false;
    }

    public AnywhereEntity(AnywhereEntity ae) {
        mId = ae.getId();
        mAppName = ae.getAppName();
        mParam1 = ae.getParam1();
        mParam2 = ae.getParam2();
        mParam3 = ae.getParam3();
        mDescription = ae.getDescription();
        mType = ae.getType();
        mCategory = ae.getCategory();
        mTimeStamp = ae.getTimeStamp();
        mColor = ae.getColor();
        mIconUri = ae.getIconUri();
        mExecWithRoot = ae.getExecWithRoot();
    }

    protected AnywhereEntity(Parcel in) {
        mId = in.readString();
        mAppName = in.readString();
        mParam1 = in.readString();
        mParam2 = in.readString();
        mParam3 = in.readString();
        mDescription = in.readString();
        if (in.readByte() == 0) {
            mType = 0;
        } else {
            mType = in.readInt();
        }
        mCategory = in.readString();
        mTimeStamp = in.readString();
        if (in.readByte() == 0) {
            mColor = 0;
        } else {
            mColor = in.readInt();
        }
        mIconUri = in.readString();
        byte tmpMExecWithRoot = in.readByte();
        mExecWithRoot = tmpMExecWithRoot == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mAppName);
        dest.writeString(mParam1);
        dest.writeString(mParam2);
        dest.writeString(mParam3);
        dest.writeString(mDescription);
        dest.writeByte((byte) 1);
        dest.writeInt(mType);
        dest.writeString(mCategory);
        dest.writeString(mTimeStamp);
        dest.writeByte((byte) 1);
        dest.writeInt(mColor);
        dest.writeString(mIconUri);
        dest.writeByte((byte) (mExecWithRoot ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AnywhereEntity> CREATOR = new Creator<AnywhereEntity>() {
        @Override
        public AnywhereEntity createFromParcel(Parcel in) {
            return new AnywhereEntity(in);
        }

        @Override
        public AnywhereEntity[] newArray(int size) {
            return new AnywhereEntity[size];
        }
    };

    @NonNull
    public static AnywhereEntity Builder() {
        String time = String.valueOf(System.currentTimeMillis());
        return new AnywhereEntity(
                time,
                "",
                "",
                "",
                "",
                "",
                AnywhereType.Card.ACTIVITY,
                GlobalValues.INSTANCE.getCategory(),
                time,
                0,
                "",
                false);
    }

    public AnywhereEntity(@NonNull String id, @NonNull String appName, @NonNull String param1,
                          String param2, String param3, String description,
                          @NonNull Integer type, String category, @NonNull String timeStamp,
                          @NonNull Integer color, String iconUri, @NonNull Boolean execWithRoot) {
        mId = id;
        mAppName = appName;
        mParam1 = param1;
        mParam2 = param2;
        mParam3 = param3;
        mDescription = description;
        mType = type;
        mCategory = category;
        mTimeStamp = timeStamp;
        mColor = color;
        mIconUri = iconUri;
        mExecWithRoot = execWithRoot;

        if (param2 == null) {
            mParam2 = "";
        }

        if (param3 == null) {
            mParam3 = "";
        }

        if (description == null) {
            mDescription = "";
        }

        if (category == null) {
            mCategory = "";
        }

        if (iconUri == null) {
            mIconUri = "";
        }
    }

    public static AnywhereEntity getClearedEntity(@NonNull AnywhereEntity entity) {
        entity.setCategory("");
        entity.setIconUri("");
        return entity;
    }

    @NonNull
    public String getId() {
        return mId;
    }

    @NonNull
    public String getAppName() {
        return mAppName;
    }

    @NonNull
    public String getParam1() {
        return mParam1;
    }

    @NonNull
    public String getParam2() {
        return mParam2 != null ? mParam2 : "";
    }

    @NonNull
    public String getParam3() {
        return mParam3 != null ? mParam3 : "";
    }

    @NonNull
    public String getDescription() {
        return mDescription != null ? mDescription : "";
    }

    @NonNull
    public Integer getType() {
        return mType;
    }

    @NonNull
    public String getTimeStamp() {
        return mTimeStamp;
    }

    public String getCategory() {
        return mCategory == null ? GlobalValues.INSTANCE.getCategory() : mCategory;
    }

    public void setCategory(@NonNull String mCategory) {
        this.mCategory = mCategory;
    }

    public void setId(@NonNull String mId) {
        this.mId = mId;
    }

    public void setAppName(@NonNull String mAppName) {
        this.mAppName = mAppName;
    }

    public void setParam1(@NonNull String mParam1) {
        this.mParam1 = mParam1;
    }

    public void setParam2(@NonNull String mParam2) {
        this.mParam2 = mParam2;
    }

    public void setParam3(@NonNull String mParam3) {
        this.mParam3 = mParam3;
    }

    public void setDescription(@NonNull String mDescription) {
        this.mDescription = mDescription;
    }

    public void setType(@NonNull Integer mType) {
        this.mType = mType;
    }

    public void setTimeStamp(@NonNull String mTimeStamp) {
        this.mTimeStamp = mTimeStamp;
    }

    @NonNull
    public Integer getColor() {
        return mColor;
    }

    public void setColor(@NonNull Integer mColor) {
        this.mColor = mColor;
    }

    @NonNull
    public String getIconUri() {
        return mIconUri == null ? "" : mIconUri;
    }

    public void setIconUri(String mIconUri) {
        this.mIconUri = mIconUri;
    }

    public Boolean getExecWithRoot() {
        return mExecWithRoot;
    }

    public void setExecWithRoot(Boolean mExecWithRoot) {
        this.mExecWithRoot = mExecWithRoot;
    }

    public String getPackageName() {
        switch (mType) {
            case AnywhereType.Card.URL_SCHEME:
                return mParam2;
            case AnywhereType.Card.ACTIVITY:
            case AnywhereType.Card.QR_CODE:
                return mParam1;
            default:
                return "";
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "id=" + mId + ", " +
                "appName=" + mAppName + ", " +
                "param1=" + mParam1 + ", " +
                "param2=" + mParam2 + ", " +
                "param3=" + mParam3 + ", " +
                "desc=" + mDescription + ", " +
                "type=" + mType + ", " +
                "category=" + mCategory + ", " +
                "timeStamp=" + mTimeStamp + ", " +
                "color=" + mColor + ", " +
                "execWithRoot=" + mExecWithRoot;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof AnywhereEntity) {
            return mId.equals(((AnywhereEntity) obj).mId) &&
                    mAppName.equals(((AnywhereEntity) obj).mAppName) &&
                    mParam1.equals(((AnywhereEntity) obj).mParam1) &&
                    mParam2.equals(((AnywhereEntity) obj).mParam2) &&
                    mParam3.equals(((AnywhereEntity) obj).mParam3) &&
                    mDescription.equals(((AnywhereEntity) obj).mDescription) &&
                    mType.equals(((AnywhereEntity) obj).mType) &&
                    mCategory.equals(((AnywhereEntity) obj).mCategory) &&
                    mTimeStamp.equals(((AnywhereEntity) obj).mTimeStamp) &&
                    mColor.equals(((AnywhereEntity) obj).mColor) &&
                    mExecWithRoot.equals(((AnywhereEntity) obj).mExecWithRoot);
        } else {
            return false;
        }
    }
}
