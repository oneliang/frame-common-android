package com.oneliang.android.common.resources.parse.struct;

// Taken from http://developer.android.com/reference/android/R.html
public enum RType {
	ANIM, ANIMATOR, ARRAY, ATTR, BOOL, COLOR, DIMEN, DRAWABLE, FRACTION, ID, INTEGER, INTERPOLATOR, LAYOUT, MENU, MIPMAP, PLURALS, RAW, STRING, STYLE, STYLEABLE, TRANSITION, XML;

	public String toString() {
		return super.toString().toLowerCase();
	}
}