/**
 * 
 */
package tud.kom.dss6.localsiri.localservice.collection;

import tud.kom.dss6.localsiri.localservice.Constants;

/**
 * @author Hariharan & Harini
 * 
 */
public class CriteriaSelector {

	public Optimizer getOptimizedCriteria(int batteryLevel) {
		
		Optimizer optimizer = new Optimizer();

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
		else if (batteryLevel > Constants.BATTERY_LEVEL.LOW
				&& batteryLevel <= Constants.BATTERY_LEVEL.MEDIUM) {
			optimizer.setFrequency(Constants.FREQUENCY_LEVEL.LOW);
			optimizer.setPriority(Constants.PRIORITY_LEVEL.LOW);
		}

		/* BATTERY LEVEL - "CRITICAL" */
		else if (batteryLevel > Constants.BATTERY_LEVEL.CRITICAL
				&& batteryLevel <= Constants.BATTERY_LEVEL.LOW) {
			optimizer.setFrequency(Constants.FREQUENCY_LEVEL.CRITICAL);
			optimizer.setPriority(Constants.PRIORITY_LEVEL.CRITICAL);
		}

		/* BATTERY LEVEL - "BELOW CRITICAL" */
		else if (batteryLevel <= Constants.BATTERY_LEVEL.CRITICAL) {
			optimizer.setFrequency(Constants.FREQUENCY_LEVEL.DEAD);
			optimizer.setPriority(Constants.PRIORITY_LEVEL.DEAD);
		}

		return optimizer;
	}

}
