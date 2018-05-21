package com.iexceed.appzillon.securityutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

public class CRCUtils {
	 private static CheckedInputStream check;

	public static long getCRC(File f) 
		      throws IOException {
		        
		        FileInputStream file = new FileInputStream(f);
		        check = new CheckedInputStream(file, new CRC32());
		        return check.getChecksum().getValue();
		    }
}
