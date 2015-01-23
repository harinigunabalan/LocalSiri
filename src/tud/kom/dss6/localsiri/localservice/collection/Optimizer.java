/**
 * 
 */
package tud.kom.dss6.localsiri.localservice.collection;

/**
 * @author Hariharan & Harini 
 *
 */
public class Optimizer {

	private int frequency;
	private int priority;
	
	/**
	 * Default Constructor
	 */
	public Optimizer() {
		this.frequency = 0;
		this.priority = 0;
	}

	/**
	 * @param frequency
	 * @param priority
	 */
	public Optimizer(int frequency, int priority) {
		
		this.frequency = frequency;
		this.priority = priority;
	}
	
	/**
	 * @return the frequency
	 */
	public int getFrequency() {
		return frequency;
	}
	/**
	 * @param frequency the frequency to set
	 */
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}
	/**
	 * @param priority the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toint()
	 */
	@Override
	public String toString() {
		return "optimizer [frequency=" + frequency + ", priority=" + priority
				+ "]";
	}
	
}
