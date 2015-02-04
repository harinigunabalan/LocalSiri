package tud.kom.dss6.localsiri.monitor;

public class MonitorPojo {

	int mBatteryLevel;
	String mUserPreference_Location;
	String mUserPreference_Upload;
	int mLocationFrequency;
	int mLocationFrequency_Adapted;
	int mLocationMode;
	int mLocationMode_Adapted;
	int mUploadMode;

	/**
	 * @return the mBatteryLevel
	 */
	public int getmBatteryLevel() {
		return mBatteryLevel;
	}

	/**
	 * @param mBatteryLevel
	 *            the mBatteryLevel to set
	 */
	public void setmBatteryLevel(int mBatteryLevel) {
		this.mBatteryLevel = mBatteryLevel;
	}

	/**
	 * @return the mUserPreference_Location
	 */
	public String getmUserPreference_Location() {
		return mUserPreference_Location;
	}

	/**
	 * @param mUserPreference_Location
	 *            the mUserPreference_Location to set
	 */
	public void setmUserPreference_Location(String mUserPreference_Location) {
		this.mUserPreference_Location = mUserPreference_Location;
	}

	/**
	 * @return the mUserPreference_Upload
	 */
	public String getmUserPreference_Upload() {
		return mUserPreference_Upload;
	}

	/**
	 * @param mUserPreference_Upload
	 *            the mUserPreference_Upload to set
	 */
	public void setmUserPreference_Upload(String mUserPreference_Upload) {
		this.mUserPreference_Upload = mUserPreference_Upload;
	}

	/**
	 * @return the mLocationFrequency
	 */
	public int getmLocationFrequency() {
		return mLocationFrequency;
	}

	/**
	 * @param mLocationFrequency
	 *            the mLocationFrequency to set
	 */
	public void setmLocationFrequency(int mLocationFrequency) {
		this.mLocationFrequency = mLocationFrequency;
	}

	/**
	 * @return the mLocationFrequency_Adapted
	 */
	public int getmLocationFrequency_Adapted() {
		return mLocationFrequency_Adapted;
	}

	/**
	 * @param mLocationFrequency_Adapted
	 *            the mLocationFrequency_Adapted to set
	 */
	public void setmLocationFrequency_Adapted(int mLocationFrequency_Adapted) {
		this.mLocationFrequency_Adapted = mLocationFrequency_Adapted;
	}

	/**
	 * @return the mLocationMode
	 */
	public int getmLocationMode() {
		return mLocationMode;
	}

	/**
	 * @param mLocationMode
	 *            the mLocationMode to set
	 */
	public void setmLocationMode(int mLocationMode) {
		this.mLocationMode = mLocationMode;
	}

	/**
	 * @return the mLocationMode_Adapted
	 */
	public int getmLocationMode_Adapted() {
		return mLocationMode_Adapted;
	}

	/**
	 * @param mLocationMode_Adapted
	 *            the mLocationMode_Adapted to set
	 */
	public void setmLocationMode_Adapted(int mLocationMode_Adapted) {
		this.mLocationMode_Adapted = mLocationMode_Adapted;
	}

	/**
	 * @return the mUploadMode
	 */
	public int getmUploadMode() {
		return mUploadMode;
	}

	/**
	 * @param mUploadMode
	 *            the mUploadMode to set
	 */
	public void setmUploadMode(int mUploadMode) {
		this.mUploadMode = mUploadMode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MonitorPojo [mBatteryLevel=" + mBatteryLevel
				+ ", mUserPreference_Location=" + mUserPreference_Location
				+ ", mUserPreference_Upload=" + mUserPreference_Upload
				+ ", mLocationFrequency=" + mLocationFrequency
				+ ", mLocationFrequency_Adapted=" + mLocationFrequency_Adapted
				+ ", mLocationMode=" + mLocationMode
				+ ", mLocationMode_Adapted=" + mLocationMode_Adapted
				+ ", mUploadMode=" + mUploadMode + "]";
	}

}
