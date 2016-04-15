package com.ghn.android.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Ribot implements Comparable<Ribot>, Parcelable {

  public static final Creator<Ribot> CREATOR = new Creator<Ribot>() {
    public Ribot createFromParcel(Parcel source) {
      return new Ribot(source);
    }

    public Ribot[] newArray(int size) {
      return new Ribot[size];
    }
  };
  public com.ghn.android.data.model.Profile profile;

  public Ribot() {
  }

  public Ribot(com.ghn.android.data.model.Profile profile) {
    this.profile = profile;
  }

  protected Ribot(Parcel in) {
    this.profile = in.readParcelable(com.ghn.android.data.model.Profile.class.getClassLoader());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Ribot ribot = (Ribot) o;

    return !(profile != null ? !profile.equals(ribot.profile) : ribot.profile != null);
  }

  @Override
  public int hashCode() {
    return profile != null ? profile.hashCode() : 0;
  }

  @Override
  public int compareTo(Ribot another) {
    return profile.name.first.compareToIgnoreCase(another.profile.name.first);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeParcelable(this.profile, 0);
  }
}

