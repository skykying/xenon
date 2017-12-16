package com.abubusoft.xenon;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;

import com.abubusoft.xenon.context.XenonBeanContext;

public class DeviceInfo {

	private static DeviceInfo instance;

	public static final DeviceInfo instance() {
		if (instance == null) {
			instance = new DeviceInfo();
		}

		return instance;
	}

	/**
	 * <p>
	 * Gets the number of cores available in this device, across all processors. Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu"
	 * </p>
	 * 
	 * <p>
	 * da <a href="http://stackoverflow.com/questions/7962155/how-can-you-detect-a-dual-core-cpu-on-an-android-device-from-code">qui</a>
	 * </p>
	 * 
	 * @return The number of cores, or 1 if failed to get result
	 */
	static long retrieveCPUCores() {
		// Private Class to display only CPU devices in the directory listing
		class CpuFilter implements FileFilter {
			@Override
			public boolean accept(File pathname) {
				// Check if filename is "cpu", followed by a single digit number
				if (Pattern.matches("cpu[0-9]+", pathname.getName())) {
					return true;
				}
				return false;
			}
		}

		try {
			// Get directory containing CPU info
			File dir = new File("/sys/devices/system/cpu/");
			// Filter to only list the devices we care about
			File[] files = dir.listFiles(new CpuFilter());
			// Return the number of cores (virtual CPU devices)
			return files.length;
		} catch (Exception e) {
			// Default to return 1 core
			return 1;
		}
	}

	static long retrieveRAM() {
		Context context = XenonBeanContext.getContext();
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(memoryInfo);

		long availableMegs = memoryInfo.totalMem / 1048576L;

		return availableMegs;
	}

	/**
	 * memoria del dispositivo
	 */
	private long availableRAM;

	/**
	 * numero di core della cpu
	 */
	private long cpuCores;

	DeviceInfo() {
		cpuCores = retrieveCPUCores();
		availableRAM = retrieveRAM();		
	}

	/**
	 * @return the avaiableRAM in mebibyte
	 */
	public long getAvailableRAM() {
		return availableRAM;
	}

	/**
	 * numero di cpu
	 * 
	 * @return the cpuCores
	 */
	public long getCpuCores() {
		return cpuCores;
	}
}
