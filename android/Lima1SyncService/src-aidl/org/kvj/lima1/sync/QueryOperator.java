package org.kvj.lima1.sync;

import android.os.Parcel;
import android.os.Parcelable;

public class QueryOperator implements Parcelable {

	public static final Parcelable.Creator<QueryOperator> CREATOR = new Parcelable.Creator<QueryOperator>() {
		
		public QueryOperator[] newArray(int size) {
			return new QueryOperator[size];
		}
		
		public QueryOperator createFromParcel(Parcel source) {
			return new QueryOperator(source.readString(), source.readString(), source.readInt());
		}
	};
	
	public static final int TYPE_STRING = 0;
	public static final int TYPE_NUMBER = 1;
	public static final int TYPE_DOUBLE = 2;
	
	private String value = "";
	private int type = TYPE_STRING;
	private String operator = "=";
	
	public QueryOperator(String value) {
		this.value = value;
	}
	
	public QueryOperator(long value) {
		this.value = Long.toString(value);
		type = TYPE_NUMBER;
	}
	
	public QueryOperator(String value, String operator) {
		this.value = value;
		this.operator = operator;
	}
	
	public QueryOperator(long value, String operator) {
		this.value = Long.toString(value);
		type = TYPE_NUMBER;
		this.operator = operator;
	}
	
	public QueryOperator(String value, String operator, int type) {
		this.value = value;
		this.operator = operator;
		this.type = type;
	}
	
	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(value);
		dest.writeString(operator);
		dest.writeInt(type);
	}

}
