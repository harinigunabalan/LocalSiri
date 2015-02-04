/**
 * 
 */
package tud.kom.dss6.localsiri.localservice.collection;

import tud.kom.dss6.localsiri.localservice.Constants;
import tud.kom.dss6.localsiri.monitor.PreferenceLocation;

/**
 * @author Hariharan & Harini
 * 
 */
public class CriteriaSelector {

	Optimizer optimizer = new Optimizer();

	int batteryLevel;
	Boolean isIntelligenceOverridden;
	String userScheme;

	/**
	 * Constructor with arguments for CriteriaSelector
	 * 
	 * @param preferenceLocation
	 *            Object of PreferenceLocation which includes <li>batteryLevel -
	 *            The current battery-level from the device</li> <li>
	 *            isIntelligenceOverridden - If user has overridden the
	 *            Intelligent Optimizer</li> <li>userScheme - In-case if the
	 *            user has overridden what is the chosen scheme</li>
	 */
	public CriteriaSelector(PreferenceLocation preferenceLocation) {
		super();
		this.batteryLevel = preferenceLocation.mBatteryLevel;
		this.isIntelligenceOverridden = preferenceLocation.isIntelligenceOverridden;
		this.userScheme = preferenceLocation.mUserScheme;
	}

	/**
	 * <p>
	 * <b>getOptimizedCriteria(): </b> Method that chooses the optimizer
	 * parameters
	 * <li>Frequency of Data collection</li>
	 * <li>Priority mode</li>
	 * </p>
	 * 
	 * @return optimizer(object)
	 */
	public Optimizer getOptimizedCriteria() {

		if (!isIntelligenceOverridden) {

			/* BATTERY LEVEL - "HIGH" */
			if (batteryLevel > Constants.BATTERY_LEVEL.HIGH) {
				optimizer.setFrequency(Constants.FREQUENCY_LEVEL.HIGH);
				optimizer.setPriority(Constants.PRIORITY_LEVEL.HIGH);
			}

			/* BATTERY LEVEL - "MEDIUM" */
			else if (batteryLevel > Constants.BATTERY_LEVEL.MEDIUM
					&& batteryLevel <= Constants.BATTERY_LEVEL.HIGH) {
				optimizer.setFrequency(Constants.FREQUENCY_LEVEL.MEDIUM);
				optimizer.setPriority(Constants.PRIORITY_LEVEL.MEDIUM);
			}

			/* BATTERY LEVEL - "LOW" */
			else if (batteryLevel <= Constants.BATTERY_LEVEL.MEDIUM) {
				mModerateMode();
			}

			return optimizer;
		}

		switch (userScheme) {
		case "moderate":
			mModerateMode();
			break;
		case "lazy":
			mLazyMode();
			break;
		}
		return optimizer;
	}

	/**
	 * Moderate Mode:
	 */
	public void mModerateMode() {
		/* BATTERY LEVEL - "LOW" */
		if (batteryLevel > Constants.BATTERY_LEVEL.LOW) {
			optimizer.setFrequency(Constants.FREQUENCY_LEVEL.LOW);
			optimizer.setPriority(Constants.PRIORITY_LEVEL.LOW);
		}

		/* BATTERY LEVEL - "CRITICAL" */
		else if (batteryLevel <= Constants.BATTERY_LEVEL.LOW) {
			mLazyMode();
		}
	}

	/**
	 * Lazy Mode:
	 */
	public void mLazyMode() {
		/* BATTERY LEVEL - "CRITICAL" */
		if (batteryLevel > Constants.BATTERY_LEVEL.CRITICAL) {
			optimizer.setFrequency(Constants.FREQUENCY_LEVEL.CRITICAL);
			optimizer.setPriority(Constants.PRIORITY_LEVEL.CRITICAL);
		}

		/* BATTERY LEVEL - "BELOW CRITICAL" */
		else if (batteryLevel <= Constants.BATTERY_LEVEL.CRITICAL) {
			optimizer.setFrequency(Constants.FREQUENCY_LEVEL.DEAD);
			optimizer.setPriority(Constants.PRIORITY_LEVEL.DEAD);
		}
	}

}
