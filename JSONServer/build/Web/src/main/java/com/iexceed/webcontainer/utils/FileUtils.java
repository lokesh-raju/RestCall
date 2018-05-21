package com.iexceed.webcontainer.utils;

import static com.iexceed.webcontainer.utils.AppzillonConstants.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;

import org.json.JSONObject;
import org.json.JSONException;

import com.iexceed.webcontainer.logger.Logger;
import com.iexceed.webcontainer.logger.LoggerFactory;
import com.iexceed.webcontainer.utils.hash.Utility;

public  class FileUtils {
	
	static Logger LOG = LoggerFactory.getLoggerFactory().getWebContainerLogger(FileUtils.class.getName());
	
	private FileUtils() {}	

	public static void createUpdateSettingsFile( String pUserId, String pAppId,  String pSettings, String pContextPath) throws IOException {
		 StringBuilder path = new StringBuilder(pContextPath);
		 File lUsrSttFolder = new File(path.toString());

		if (lUsrSttFolder.isDirectory()) {
			LOG.debug(path.toString() + " Exists");
			if (path.toString().endsWith(FWDSLASH) || path.toString().endsWith(BWDSLASH)) {
				path.append(pAppId);
			} else {
				path.append(File.separator + pAppId);
			}

			lUsrSttFolder = new File(path.toString());
			if (lUsrSttFolder.isDirectory()) {
				LOG.debug(path.toString() + " Exists");
				path.append(FWDSLASH + pUserId);
				lUsrSttFolder = new File(path.toString());
				LOG.debug("UserSettings folder : " + path.toString());
			} else {
				LOG.debug("Creating folder for userID : " + path.toString());
				if (makeDirectory(lUsrSttFolder)) {
					LOG.debug("Created");
					path.append(File.separator + pUserId);
					lUsrSttFolder = new File(path.toString());
				}
			}
		} else {
			LOG.debug("Creating folder : " + path.toString());
			if (makeDirectory(lUsrSttFolder)) {
				LOG.debug("Created");
				if (path.toString().endsWith(FWDSLASH)|| path.toString().endsWith(BWDSLASH)) {
					path.append(pAppId);
				} else {
					path.append(File.separator + pAppId);
				}
				lUsrSttFolder = new File(path.toString());
				LOG.debug("Creating folder for appID : " + path.toString());
				if (makeDirectory(lUsrSttFolder)) {
					LOG.debug("Created");
					path.append(FWDSLASH + pUserId);
					lUsrSttFolder = new File(path.toString());
				}
			}
		}

		LOG.debug("Checking for UserSettings Folder : " + lUsrSttFolder);
		if (lUsrSttFolder.isDirectory()) {
			LOG.debug("Exists"); 
			 FilenameFilter lFilenameFilter = new FilenameFilter() {
				public boolean accept(File dir, String name) {
					if (name.lastIndexOf('.') > 0) {
						// get last index for '.' char
						 int lastIndex = name.lastIndexOf('.');
						// get extension
						 String str = name.substring(lastIndex);
						// match path name extension
						if (".json".equals(str)) {
							return true;
						}
					}
					return false;
				}
			};
			 File[] userSettingFiles = lUsrSttFolder.listFiles(lFilenameFilter);
			 LOG.debug("UserSettingFile length :" + userSettingFiles.length);
			if (userSettingFiles.length > 0) {
				LOG.debug("UserSettings File exists:" + userSettingFiles[0]);
				writeFileContent(userSettingFiles[0], pSettings, false);
			} else {
				createUserSettings(lUsrSttFolder, pSettings);				
			}
		} else {			
			if (lUsrSttFolder.mkdir()) {
				createUserSettings(lUsrSttFolder, pSettings);
			}
		}
	}
	
	private static void createUserSettings(File lUsrSttFolder, String pSettings){
		try {
			LOG.debug("Creating User Settings File : "+ SETTINGSDATAJSON);
			File lUserSettingsFile = new File(lUsrSttFolder.getPath() + File.separator+ SETTINGSDATAJSON);
			lUserSettingsFile.createNewFile();
			LOG.debug("Created");
			writeFileContent(lUserSettingsFile, pSettings, false);
		} catch (IOException ex) {
			LOG.error("IOException", ex);
		}
	}

	public static JSONObject readUserSettings( String userId, ServletContext context) throws IOException {
		JSONObject defaultSettings = null;
		String fileContent = null;		
		String lAppID = WebProperties.getAppId();
		LOG.debug("Creating/Reading UserSettings(settings.json) file");
		String settingsFilePath = WebProperties.getSettingsPath()+ lAppID + File.separator + userId + 
				File.separator	+ SETTINGSDATAJSON;
		File lUserSettingFile = new File(settingsFilePath);
		LOG.debug("Path for UserSettings is :" + lUserSettingFile);
		if (FileUtils.isFile(lUserSettingFile)) {
			LOG.debug("File exists: Reading the contents");
			fileContent = FileUtils.readFileContent(lUserSettingFile);
		}
		if (fileContent == null) {
			LOG.debug("Reading file contents from Application resource");
			InputStream inpStr = context.getResourceAsStream(APPS_PATH + lAppID + CONFIG_PATH + SETTINGSDATAJSON);
			String jsonData = Utility.getStringFromInputStream(inpStr);
			try {
				defaultSettings = new JSONObject(jsonData);
			} catch (JSONException e) {
				LOG.error("JSONException", e);
			}
			FileUtils.createUpdateSettingsFile(userId, lAppID, jsonData, WebProperties.getSettingsPath());
		} else {
			try {
				defaultSettings = new JSONObject(fileContent);
			} catch (JSONException e) {
				LOG.error("JSONException", e);
			}
		}
		return defaultSettings;
	}

	private static boolean makeDirectory(File file) {		
		boolean status = file.mkdirs();
		if (status) {
			LOG.debug(file.getName() + " Created");
		} else {
			LOG.debug("Failed to create folder : " + file.getName());
		}
		return status;
	}

	public static String readFileContent(File pFile) {
		BufferedReader buffReader = null;
		String result = null;
		 StringBuilder lUserFileContent = new StringBuilder();
		try {
			LOG.debug("Reading File : " + pFile.getName());
			buffReader = new BufferedReader(new FileReader(pFile));
			String fileContent = null;
			if (buffReader != null) {
				while ((fileContent = buffReader.readLine()) != null) {
					lUserFileContent.append(fileContent);
				}
			}
			result = lUserFileContent.toString();
		} catch (FileNotFoundException ex) {
			LOG.error("FileNotFoundException", ex);
		} catch (IOException ex) {
			LOG.error("IOException", ex);
		} finally {
			try {
				if (buffReader != null) {
					buffReader.close();
				}
			} catch (IOException ex) {
				LOG.error("IOException", ex);
			}
		}
		return result;
	}

	public static boolean isFile(File file) {
		return file.exists();
	}

	public static void writeFileContent(File pFile,String pContent, boolean appendData) throws IOException {
		if (!pFile.exists()) {
			pFile.createNewFile();
		}
		LOG.debug("Writing String content to file : " + pFile.getName());
		FileWriter fileWriter = new FileWriter(pFile.getAbsoluteFile(), appendData);
		BufferedWriter buffWriter = new BufferedWriter(fileWriter);
		buffWriter.write(pContent);
		if (appendData) {
			buffWriter.newLine();
		}
		buffWriter.flush();
		buffWriter.close();
	}
}
