package org.kvj.lima1.sync;

import android.os.Parcel;
import android.os.Parcelable;

public class QueryOperator implements Parcelable {

	public static final Parcelable.Creator<QueryOperator> CREATOR = new Parcelable.Creator<QueryOperator>() {

		public QueryOperator[] newArray(int size) {
			return new QueryOperator[size];
		}

		public QueryOperator createFromParcel(Parcel source) {
			return new QueryOperator(source.readString(), source.readString(),
					source.readString(), source.readInt());
		}
	};

	public static final int TYPE_STRING = 0;
	public static final int TYPE_NUMBER = 1;
	public static final int TYPE_DOUBLE = 2;

	private String name = "";
	private String value = "";
	private int type = TYPE_STRING;
	private String operator = "=";

	public QueryOperator(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public QueryOperator(String name, long value) {
		this.name = name;
		this.value = Long.toString(value);
		type = TYPE_NUMBER;
	}

	public QueryOperator(String name, String value, String operator) {
		this.name = name;
		this.value = value;
		this.operator = operator;
	}

	public QueryOperator(String name, long value, String operator) {
		this.name = name;
		this.value = Long.toString(value);
		type = TYPE_NUMBER;
		this.operator = operator;
	}

	public QueryOperator(String name, String value, String operator, int type) {
		this.name = name;
		this.value = value;
		this.operator = operator;
		this.type = type;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(value);
		dest.writeString(operator);
		dest.writeInt(type);
	}

	public String getName() {
		return name;
	}

	public String getOperator() {
		return operator;
	}

	public int getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

}
