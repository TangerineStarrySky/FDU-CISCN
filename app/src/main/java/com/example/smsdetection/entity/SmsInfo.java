package com.example.smsdetection.entity;
//fwk
import android.os.Parcel;
import android.os.Parcelable;

public class SmsInfo implements Parcelable {

    public int id;
    public String datetime;
    public String sender;
    public String content;
    public int type;

    public boolean isSelected = false;

    // 账单类型，0 收入，1 支出
    public static final int SMS_TYPE_COMMON = 0;
    public static final int SMS_TYPE_DECEIVE = 1;

    public SmsInfo() {
    }

    protected SmsInfo(Parcel in) {
        id = in.readInt();
        datetime = in.readString();
        sender = in.readString();
        content = in.readString();
        type = in.readInt();
        isSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(datetime);
        dest.writeString(sender);
        dest.writeString(content);
        dest.writeInt(type);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SmsInfo> CREATOR = new Creator<SmsInfo>() {
        @Override
        public SmsInfo createFromParcel(Parcel in) {
            return new SmsInfo(in);
        }

        @Override
        public SmsInfo[] newArray(int size) {
            return new SmsInfo[size];
        }
    };
//fwk

    @Override
    public String toString() {
        return "SmsInfo{" +
                "id=" + id +
                ", datetime='" + datetime + '\'' +
                ", sender='" + sender + '\'' +
                ", content='" + content + '\'' +
                ", type=" + type +
                '}';
    }
}
