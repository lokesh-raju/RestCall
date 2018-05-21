package com.iexceed.appzillon.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import org.apache.camel.InvalidPayloadException;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.commons.codec.binary.Base64;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.RenderOption;
import org.eclipse.birt.report.engine.api.ReportEngine;
import org.eclipse.core.internal.registry.RegistryProviderFactory;

import com.iexceed.appzillon.dao.ReportDetails;
import com.iexceed.appzillon.dbutils.DBUtils ;
import com.iexceed.appzillon.domain.DomainStartup;
import com.iexceed.appzillon.exception.Utils;
import com.iexceed.appzillon.frameworks.FrameworksStartup;
import com.iexceed.appzillon.iface.ExternalServicesRouter;
import com.iexceed.appzillon.iface.IReportServiceBean;
import com.iexceed.appzillon.json.JSONException;
import com.iexceed.appzillon.json.JSONObject;
import com.iexceed.appzillon.jsonutils.JSONUtils;
import com.iexceed.appzillon.logging.Logger;
import com.iexceed.appzillon.logging.LoggerFactory;
import com.iexceed.appzillon.message.Error;
import com.iexceed.appzillon.message.Message;
import com.iexceed.appzillon.propertyutils.PropertyUtils;
import com.iexceed.appzillon.utils.ExternalServicesRouterException;
import com.iexceed.appzillon.utils.ExternalServicesRouterException.EXCEPTION_CODE;
import com.iexceed.appzillon.utils.ServerConstants;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;

/**
 *
 * @author Ripu
 */
public class BirtService implements IReportServiceBean{
	private static final Logger LOG = LoggerFactory.getLoggerFactory().getFrameWorksLogger(ServerConstants.LOGGER_FRAMEWORKS,BirtService.class.toString());
	private ReportDetails reportDtls= null;
	private static String cPassword = null;

	@Override
	public Object callService(Message pMessage, SpringCamelContext pContext) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "inside callService()..");
		String reportResponse = "";
		JSONObject finalResponseJson = null;
		JSONObject reqJson = null;
		JSONObject reportData = null;
		JSONObject reportDetail = null;
		String fileType = null;
		this.getReportDetails(pMessage, pContext);

		if(ServerConstants.YES.equalsIgnoreCase(reportDtls.getPasswordRequired())){
			cPassword = generatePassword(pMessage, pContext);
		}
		try{
			this.validateRequest(pMessage);

			reqJson = pMessage.getRequestObject().getRequestJson();
			reportData = (JSONObject) reqJson.get(ServerConstants.REPORT_DATA);
			reportDetail = (JSONObject) reportData.get(ServerConstants.REPORT_DETAILS);
			fileType = (String) reportDetail.get(ServerConstants.REPORT_FILE_TYPE);

			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Password Required : "+ reportDtls.getPasswordRequired());

			if(ServerConstants.YES.equalsIgnoreCase(reportDtls.getPasswordRequired()) && ServerConstants.REPORT_PDF_FILE.equalsIgnoreCase(fileType)){
				String reportStatus = null;
				String mailStatus = null;
				reportResponse = this.generateEncodedReport(pMessage);
				JSONObject reportResponseJson = new JSONObject(reportResponse);
				if(reportResponseJson.has(ServerConstants.MESSAGE_HEADER_STATUS)){
					reportStatus = (String) reportResponseJson.get(ServerConstants.MESSAGE_HEADER_STATUS);
					if(ServerConstants.SUCCESS.equalsIgnoreCase(reportStatus)){
						String linterfaceId = pMessage.getHeader().getInterfaceId();
						mailStatus = this.sendMailtoUser(pMessage);
						pMessage.getHeader().setInterfaceId(linterfaceId);
						if(ServerConstants.SUCCESS.equalsIgnoreCase(mailStatus)){
							finalResponseJson = new JSONObject(reportResponse);
						}else{
							pMessage.getHeader().setStatus(false);
							Error error = Error.getInstance();
							ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
							error.setErrorCode(EXCEPTION_CODE.APZ_FM_EX_032.toString());
							error.setErrorDesc(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_032));
							pMessage.getErrors().add(error);
						}
					}else {
						finalResponseJson = new JSONObject(reportResponse);
					}
				}
			}else if(ServerConstants.NO.equalsIgnoreCase(reportDtls.getPasswordRequired())){ // cpasswordRequired
				finalResponseJson =  new JSONObject(this.generateEncodedReport(pMessage));
			}else{
				LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Password Protected "+ fileType +" Report can not be generated.");
				ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
				exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_027.toString());
				exsrvcallexp.setMessage("Password Protected "+ fileType +" Report cannot be generated.");
				exsrvcallexp.setPriority("1");
				throw exsrvcallexp;
			}
		}catch(JSONException exp){
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + ServerConstants.JSON_EXCEPTION, exp);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_026.toString());
			exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_026));
			exsrvcallexp.setPriority("1");
			throw exsrvcallexp;
		}
		return  finalResponseJson;
	}

	@Override
	public void validateRequest(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "inside validateRequest()..");
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "payLoad :: "+pMessage.getRequestObject().getRequestJson());
		try{
			JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
			JSONObject reportData = (JSONObject) requestJson.get(ServerConstants.REPORT_DATA);
			JSONObject reportDetails = (JSONObject) reportData.get(ServerConstants.REPORT_DETAILS);
			String fileType = (String) reportDetails.get(ServerConstants.REPORT_FILE_TYPE);
			JSONObject reportParam = (JSONObject) reportData.get(ServerConstants.REPORT_PARAMS);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "report parameter : "+ reportParam);

			if(Utils.isNullOrEmpty(fileType)){
				LOG.warn(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "fileType is null..");
				ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
				exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_026.toString());
				exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_026));
				exsrvcallexp.setPriority("1");
				throw exsrvcallexp;
			}
			if(! ServerConstants.REPORT_PDF_FILE.equalsIgnoreCase(fileType) && !ServerConstants.REPORT_XLS.equalsIgnoreCase(fileType) 
					&& ! ServerConstants.REPORT_XLSX.equalsIgnoreCase(fileType) && ! ServerConstants.REPORT_HTML.equalsIgnoreCase(fileType)){
				LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " fileType is : "+fileType);
				ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
				exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_026.toString());
				exsrvcallexp.setMessage("Report can not be generated of "+fileType+" type.");
				exsrvcallexp.setPriority("1");
				throw exsrvcallexp;
			}
		}catch(JSONException jsonExp){
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS,jsonExp);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_026.toString());
			exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_026));
			exsrvcallexp.setPriority("1");
			throw exsrvcallexp;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public String generateEncodedReport(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "inside generateEncodedReport()..");
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Request : "+pMessage.getRequestObject().getRequestJson());
		JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
		JSONObject reportData = (JSONObject) requestJson.get(ServerConstants.REPORT_DATA);
		JSONObject reportDetails = (JSONObject) reportData.get(ServerConstants.REPORT_DETAILS);
		String lFileType = (String) reportDetails.get(ServerConstants.REPORT_FILE_TYPE);
		JSONObject reportParam = (JSONObject) reportData.get(ServerConstants.REPORT_PARAMS);
		Map<String, Object> parameters = JSONUtils.buildParamMap(reportParam);
		String birtDesignfile = pMessage.getHeader().getAppId() + "_" + pMessage.getHeader().getInterfaceId() + ".rptdesign";
		String destinationfilePath = PropertyUtils.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.REPORT_JRXML_PATH);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "destination file Path :: "+destinationfilePath);
		//Changes made by Samy on 27/07/2017 for Bug-18302
		if(destinationfilePath != null && destinationfilePath.startsWith("${sys:")){
            String destinationFilePathSysVar = destinationfilePath.replace("${sys:", "").replace("}", "");
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Modified destination file Path :: " + destinationFilePathSysVar);
            destinationfilePath = System.getProperty(destinationFilePathSysVar);
            LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "destination file Path From System property :: " + destinationfilePath);
        } 
		
		//Date date = new java.util.Date();
		String lFileExt = "";
		JSONObject responseJson = new JSONObject();
		//String reportName = Long.toString(date.getTime());
		String reportName = "";
		if(reportDetails.has("fileName") && !reportDetails.getString("fileName").isEmpty()){
			reportName = reportDetails.getString("fileName");
		}else{
			Date date = new java.util.Date();
			reportName = Long.toString(date.getTime());
		}
		Connection connection = DBUtils.getConnectionFromDataSource(reportDtls.getDataSource());
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Connection :: " + connection);
		
		IRunAndRenderTask runAndRender = null;
		ReportEngine re = null;
		try{
			EngineConfig config = new EngineConfig();  
			RegistryProviderFactory.releaseDefault();
			Platform.startup(config);
			re = new ReportEngine(config);
			IReportRunnable reportRunnable = null;
			if(destinationfilePath.endsWith("\\")){
				LOG.debug("Going to Load the design file from : "+ destinationfilePath + birtDesignfile);
				reportRunnable = re.openReportDesign(destinationfilePath + birtDesignfile);
			}else if(destinationfilePath.endsWith("/")){
				LOG.debug("Going to Load the design file from : "+ destinationfilePath + birtDesignfile);
				reportRunnable = re.openReportDesign(destinationfilePath + birtDesignfile);
			}else{
				LOG.debug("Going to Load the design file from : "+ destinationfilePath + "/"+ birtDesignfile);
				reportRunnable = re.openReportDesign(destinationfilePath+"/"+birtDesignfile);
			}
			runAndRender = re.createRunAndRenderTask(reportRunnable);
			runAndRender.setParameterValues(parameters);
			//IRenderOption options = null;
			RenderOption options = new RenderOption();
			if(ServerConstants.REPORT_PDF_FILE.equalsIgnoreCase(lFileType)){
				LOG.info("Report Type is : PDF");
				//options = new PDFRenderOption();
				lFileExt = ServerConstants.REPORT_PDF_FILE_EXT;
				//options.setOutputFormat(ServerConstants.REPORT_PDF_FILE);
				options.setOutputFormat(RenderOption.OUTPUT_FORMAT_PDF);
				options.setOutputFileName(destinationfilePath + reportName + lFileExt);
			} else if(ServerConstants.REPORT_XLS.equalsIgnoreCase(lFileType)){
				LOG.info("Report Type is : XLS");
				//options = new EXCELRenderOption();
				lFileExt = ServerConstants.REPORT_XLS_EXT;
				options.setOutputFormat(ServerConstants.REPORT_XLS);
				options.setOutputFileName(destinationfilePath + reportName + lFileExt);
			} else if(ServerConstants.REPORT_XLSX.equalsIgnoreCase(lFileType)){
				LOG.info("Report Type is : XLSX");
				//options = new EXCELRenderOption();
				lFileExt = ServerConstants.REPORT_XLSX_EXT;
				//options.setOutputFormat(RenderOption.OUTPUT_FORMAT);
				options.setEmitterID("uk.co.spudsoft.birt.emitters.excel.XlsxEmitter");
				//options.setOutputFormat(ServerConstants.REPORT_XLSX);
				options.setOutputFileName(destinationfilePath + reportName + lFileExt);
			} else if(ServerConstants.REPORT_HTML.equalsIgnoreCase(lFileType)){
				LOG.info("Report Type is : HTML");
				lFileExt = ServerConstants.REPORT_HTML_EXT;
				options.setOutputFormat(RenderOption.OUTPUT_FORMAT_HTML);//HTMLRenderOption.OUTPUT_FORMAT_HTML
				options.setOutputFileName(destinationfilePath + reportName + lFileExt);
			}
			runAndRender.setRenderOption(options);
			runAndRender.getAppContext().put("OdaJDBCDriverPassInConnection", connection);
			// setting start time for external service
			Utils.setExtTime(pMessage,"S");
			runAndRender.run();
			// setting end time for external service
			Utils.setExtTime(pMessage,"E");

			String fileName = destinationfilePath + reportName + lFileExt ;
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "File Name :: "+fileName);
			
			if(ServerConstants.YES.equalsIgnoreCase(reportDtls.getPasswordRequired())){
				LOG.debug("PDF file is password protected.. So going to apply password in pdf.");
				String password_protected_pdf_Location = destinationfilePath  + reportName+"-temp"+ServerConstants.REPORT_PDF_FILE_EXT;
				PdfReader reader = new PdfReader(fileName); // reading generated pdf file
				PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(password_protected_pdf_Location));
				stamper.setEncryption(cPassword.getBytes(), cPassword.getBytes(),
						PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128);
				stamper.close();
				reader.close();
				//File pdfReporttemp = new File(pdfGeneratedLocation);
				/*if(pdfReporttemp.delete()){
					LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Temp File deleted :: "+pdfGeneratedLocation);
				}*/
				fileName = password_protected_pdf_Location;
			}
			
			File finalGeneratedReportFile = new File(fileName);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Exported.....:: "+fileName);
			byte[] byteFile = (byte[]) bytesFromFile(finalGeneratedReportFile);
			String base64 = Base64.encodeBase64String(byteFile);
			responseJson.put(ServerConstants.REPORT_FILE, base64);
			responseJson.put(ServerConstants.REPORT_FILENAME, reportName + lFileExt);

/*			if(finalGeneratedReportFile.delete()){
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Filedeleted :: "+fileName);
			}*/
			responseJson.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
		} catch(EngineException engineException){
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "EngineException :: ", engineException);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode("APZ-FM-EX-034");
			exsrvcallexp.setMessage(engineException.getMessage());
			exsrvcallexp.setPriority("1");
			throw exsrvcallexp;
		} catch (BirtException bExp) {
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "BirtException :: ",bExp);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_033.toString());
			exsrvcallexp.setMessage(bExp.getMessage());
			exsrvcallexp.setPriority("1");
			throw exsrvcallexp;
		} catch (DocumentException docExp) {
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "DocumentException :: ",docExp);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_033.toString());
			exsrvcallexp.setMessage(docExp.getMessage());
			exsrvcallexp.setPriority("1");
			throw exsrvcallexp;
		} catch (IOException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "IOException : ", e);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_033.toString());
			exsrvcallexp.setMessage(e.getMessage());
			exsrvcallexp.setPriority("1");
			throw exsrvcallexp;
		} finally{
			LOG.info("Shutting Down Report Engine");
			runAndRender.close();
			re.destroy();
			Platform.shutdown();
			DBUtils.closeDbConnection(connection);
		}
		return responseJson.toString();
	}

	@Override
	public String generatePassword(Message pMessage, SpringCamelContext pContext) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Inside generatePassword()..");
		String lUserID = pMessage.getHeader().getUserId();
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " user ID : "+lUserID);
		Calendar calendar = Calendar.getInstance();
		return lUserID + calendar.get(Calendar.SECOND) + calendar.get(Calendar.MILLISECOND);
	}

	private String sendMailtoUser(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Inside sendMailtoUser..");
		String status = null;
		String emailId = null;
		JSONObject responseJson = null;
		pMessage.getHeader().setServiceType(ServerConstants.APPZUSERMAINTENANCESERV);
		pMessage.getHeader().setInterfaceId(ServerConstants.APPZILLON_ROOT_USER_EMAILID_REQ);
		try {
			DomainStartup.getInstance().processRequest(pMessage);
			responseJson = pMessage.getResponseObject().getResponseJson();
			emailId = (String) responseJson.get(ServerConstants.APPZILLON_ROOT_EMAILID);

			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "email id :: " + emailId);

			pMessage.getHeader().setInterfaceId("appzillonMailRequest");
			pMessage.getIntfDtls().setType("MAIL");

			Properties propfile = new Properties();
			String lFileName = ServerConstants.PDFREPORT_CONSTANTS_FILE_NAME + ".properties";
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "PDF Report - SendMail mail template file name - l_fileName:" + lFileName);
			propfile.load(PropertyUtils.class.getClassLoader().getResourceAsStream(lFileName));

			String templateBody = propfile.getProperty(ServerConstants.MAIL_CONSTANTS_BODY);
			templateBody = templateBody.replace("$password", cPassword);

			String templateSubject = propfile.getProperty(ServerConstants.MAIL_CONSTANTS_SUBJECT);

			String body = "{'appzillonMailRequest':{'emailid':'" + emailId + "', 'body':'" + templateBody + "', 'subject':'" + templateSubject + "'}}";
			JSONObject jsonBody = new JSONObject(body);
			pMessage.getRequestObject().setRequestJson(jsonBody);

			FrameworksStartup.getInstance().processRequest(pMessage);

			status = "success";
		} catch (ExternalServicesRouterException exp) {
			status = "Fail";
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "ExternalServicesRouterException -:" + Utils.getStackTrace(exp));
		} catch (JSONException exp) {
			status = "Fail";
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "JSONException -:" + Utils.getStackTrace(exp));
		} catch (IOException exp) {
			status = "Fail";
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "IOException -:" + Utils.getStackTrace(exp));
		} catch (ClassNotFoundException exp) {
			status = "Fail";
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "ClassNotFoundException -:" + Utils.getStackTrace(exp));
		} catch (InvalidPayloadException exp) {
			status = "Fail";
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "InvalidPayloadException -:" + Utils.getStackTrace(exp));
		}
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "mail sending status :: " + status);
		return status;
	}

	private void getReportDetails(Message pMessage, SpringCamelContext pContext){
		reportDtls = (ReportDetails) ExternalServicesRouter.injectBeanFromSpringContext(pMessage.getHeader().getAppId() + "_"+ pMessage.getHeader().getInterfaceId(), pContext);
	}

	private static byte[] bytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);
		long length = file.length();
		if (length > Integer.MAX_VALUE) {
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Sorry! Your given file is too large.");
			System.exit(0);
		}
		byte[] bytes = new byte[(int) length];
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead = is.read(bytes,
				offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}
		is.close();
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file " + file.getName());
		}
		return bytes;
	}

	@Override
	public String genreratePDFReport(Message pMessage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String generateExcelReport(Message pMessage) {
		// TODO Auto-generated method stub
		return null;
	}
}