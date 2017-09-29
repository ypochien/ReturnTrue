package com.returntrue.util;

import java.text.DecimalFormat;

/**
 * @author JosephWang
 */
public class Memory {

	/**
	 * Unit.
	 */
	public static final long UNIT_BYTES = 1L;

	/**
	 * Unit.
	 */
	public static final long UNIT_KB = 1024L;

	/**
	 * Unit.
	 */
	public static final long UNIT_MB = 1024L * 1024L;

	/**
	 * Inner unit.
	 */
	private static final long DEFAULT_UNIT = UNIT_MB;

	/**
	 * Get run time.
	 * 
	 * @return runtime
	 */
	private static Runtime getRuntime() {
		return Runtime.getRuntime();
	}

	/**
	 * Get max heap size (MB).
	 * 
	 * @return max heap size
	 */
	public static long max() {
		return max(DEFAULT_UNIT);
	}

	/**
	 * Get max heap size with unit, see {@link #UNIT_BYTES}, {@link #UNIT_KB},
	 * {@link #UNIT_MB}.
	 * 
	 * @param unit
	 * @return max heap size
	 */
	public static long max(long unit) {
		return getRuntime().maxMemory() / unit;
	}

	/**
	 * Get used heap size (MB).
	 * 
	 * @return used heap size
	 */
	public static long total() {
		return total(DEFAULT_UNIT);
	}

	/**
	 * Get used heap size with unit, see {@link #UNIT_BYTES}, {@link #UNIT_KB},
	 * {@link #UNIT_MB}.
	 * 
	 * @param unit
	 * @return used heap size
	 */
	public static long total(long unit) {
		return (getRuntime().totalMemory() / unit) - (getRuntime().freeMemory() / unit);
	}

	/**
	 * Get available heap size (MB).
	 * 
	 * @return available heap size
	 */
	public static long available() {
		return available(DEFAULT_UNIT);
	}

	/**
	 * Get available heap size with unit, see {@link #UNIT_BYTES},
	 * {@link #UNIT_KB}, {@link #UNIT_MB}.
	 * 
	 * @param unit
	 * @return available heap size
	 */
	public static long available(long unit) {
		return max(unit) - total(unit);
	}

	/**
	 * Get the available memory percent.
	 * 
	 * @return percent
	 */
	public static String availableMemoryPercent() {
		double used = Long.valueOf(total()).doubleValue();
		double max = Long.valueOf(max()).doubleValue();
		double percent = (1 - (used / max)) * 100;
		DecimalFormat df = new DecimalFormat("#");

		return df.format(percent);
	}
}