package com.iexceed.appzillon.rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.utils.ServerConstants;

public class Agreement {
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getRestServicesLogger(
            ServerConstants.LOGGER_RESTFULL_SERVICES, Agreement.class.toString());
	private static Date licenseExpiryDate;
	private static boolean checked = false;
	private static boolean license=false;

	static {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String expireDate = "17/05/2018";

		Date d = null;
		try {
			d = dateFormat.parse(expireDate);
		} catch (ParseException e) {
			LOG.error("ParseException",e);
		}
		Agreement.setLicenseExpiryDate(d);
		
		Agreement.updateCheck();
	}

	public static boolean isLicenseValid() {
		
		if (!isChecked()) {
			if (new Date().after(getLicenseExpiryDate())) {
				System.out.println(""+new Date().toString());
				license = false;

			} else{
				license = true;
			}
	    Agreement.setChecked(true);
		}

		return license;

	}

	public static Date getLicenseExpiryDate() {
		return licenseExpiryDate;
	}

	public static void setLicenseExpiryDate(Date licenseExpiryDate) {
		Agreement.licenseExpiryDate = licenseExpiryDate;
	}

	public static boolean isChecked() {
		return Agreement.checked;
	}

	public static void setChecked(boolean checked) {
		Agreement.checked = checked;
	}

	public static void updateCheck() {

		Timer t = new Timer();
      
		t.schedule(new TimerTask() {

			@Override
			public void run() {

				
				if (isChecked()) {
					Agreement.setChecked(false);
				}

			}
		}, 60000, 86400000);

	}


}
