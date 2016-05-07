package com.oneliang.android.common.model;

import java.io.Serializable;

public class Option implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 4229253637127774147L;

	private String label=null;
	private String value=null;
	private String[] otherInformation=null;
	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	/**
	 * @return the otherInformation
	 */
	public String[] getOtherInformation() {
		return otherInformation;
	}
	/**
	 * @param otherInformation the otherInformation to set
	 */
	public void setOtherInformation(String[] otherInformation) {
		this.otherInformation = otherInformation;
	}
}
