package org.kvj.lima1.sync;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class PJSONObject extends JSONObject implements Parcelable {

	public static final Parcelable.Creator<PJSONObject> CREATOR = new Creator<PJSONObject>() {
		
		public PJSONObject[] newArray(int size) {
			return new PJSONObject[size];
		}
		
		public PJSONObject createFromParcel(Parcel source) {
			String data = source.readString();
			try {
				return new PJSONObject(data);
			} catch (Exception e) {
			}
			return new PJSONObject();
		}
	};
	
	public PJSONObject(String data) throws JSONException {
		super(data);
	}

	public PJSONObject() {
		super();
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(toString());
	}

}
