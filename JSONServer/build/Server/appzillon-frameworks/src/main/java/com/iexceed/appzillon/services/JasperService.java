/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package com.iexceed.appzillon.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import javax.naming.NamingException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.apache.camel.InvalidPayloadException;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.commons.codec.binary.Base64;

import com.iexceed.appzillon.dao.ReportDetails;
import com.iexceed.appzillon.dbutils.DBUtils;
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

/**
 *
 * @author ripu
 */
public class JasperService implements IReportServiceBean{

	private static final Logger LOG = LoggerFactory.getLoggerFactory().getFrameWorksLogger(
			ServerConstants.LOGGER_FRAMEWORKS,JasperService.class.toString());

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
						mailStatus = this.sendMailtoUser(pMessage);
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
				LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Password Protected "+ fileType +" Report can not be generated.");
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
				LOG.warn(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " fileType is null..");
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

	@Override
	public String generateEncodedReport(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "inside generateEncodedReport()..");
		JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
		JSONObject responseJson = null;
		String response = "";
		try{
			JSONObject reportData = (JSONObject) requestJson.get(ServerConstants.REPORT_DATA);
			JSONObject reportDetails = (JSONObject) reportData.get(ServerConstants.REPORT_DETAILS);
			String lFileType = (String) reportDetails.get(ServerConstants.REPORT_FILE_TYPE);

			if(ServerConstants.REPORT_PDF_FILE.equalsIgnoreCase(lFileType) || ServerConstants.REPORT_HTML.equalsIgnoreCase(lFileType)){
				response = genreratePDFReport(pMessage);
			}else if(ServerConstants.REPORT_XLS.equalsIgnoreCase(lFileType) || ServerConstants.REPORT_XLSX.equalsIgnoreCase(lFileType)){
				response = generateExcelReport(pMessage);						
			}
			responseJson = new JSONObject(response);
		} catch (JSONException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS , e);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_030.toString());
			exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_030));
			exsrvcallexp.setPriority("1");
			throw exsrvcallexp;
		} 
		return responseJson.toString();
	}

	@Override
	public String genreratePDFReport(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "inside genreratePDFReport()..");
		
		JSONObject reqJson = pMessage.getRequestObject().getRequestJson();
		JSONObject reportData = reqJson.getJSONObject(ServerConstants.REPORT_DATA);
		JSONObject reportDetail = reportData.getJSONObject(ServerConstants.REPORT_DETAILS);
		String lFileType = reportDetail.getString(ServerConstants.REPORT_FILE_TYPE);
		String reportName = "";
		if(reportDetail.has("fileName") && !reportDetail.getString("fileName").isEmpty()){
			reportName = reportDetail.getString("fileName");
		}else{
			Date date = new java.util.Date();
			reportName = Long.toString(date.getTime());
		}
		String lFileExt = "";
		String destinationfilePath = PropertyUtils.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.REPORT_JRXML_PATH);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "destinationfilePath :: "+destinationfilePath);
		
		String base64 = null;
		JSONObject responseJson = new JSONObject();
		Connection lConnection = null;
		try{
			//List<JasperPrint> list = new ArrayList<JasperPrint>();
			lConnection = DBUtils.getConnectionFromDataSource(reportDtls.getDataSource());
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Taking Connection From Data source :: " + lConnection);
			
			//list.add(this.loadJasperFile(pMessage, lConnection));
			//JRPdfExporter exporter = new JRPdfExporter();
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "File Type : "+ lFileType);
			JRExporter exporter = null;
			if(ServerConstants.REPORT_PDF_FILE.equalsIgnoreCase(lFileType)){
				lFileExt = ServerConstants.REPORT_PDF_FILE_EXT;
				exporter = new JRPdfExporter();
			}else if(ServerConstants.REPORT_HTML.equalsIgnoreCase(lFileType)){
				lFileExt = ServerConstants.REPORT_HTML_EXT;
				exporter = new JRHtmlExporter();
			}
			String pdfGeneratedLocation = destinationfilePath  + reportName + lFileExt;
				//exporter.setParameter(JRExporterParameter.JASPER_PRINT_LIST, this.loadJasperFile(pMessage, lConnection));
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, this.loadJasperFile(pMessage, lConnection));
				exporter.setParameter(JRExporterParameter.OUTPUT_FILE, new File(pdfGeneratedLocation));
			
			if(ServerConstants.YES.equalsIgnoreCase(reportDtls.getPasswordRequired())){
				exporter.setParameter(JRPdfExporterParameter.IS_ENCRYPTED,true);  
				exporter.setParameter(JRPdfExporterParameter.USER_PASSWORD, cPassword);  
			}
			Utils.setExtTime(pMessage,"S");
			exporter.exportReport();
			Utils.setExtTime(pMessage,"E");
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Report file generated at :: "+pdfGeneratedLocation);
			if("ENCODED".equalsIgnoreCase(reportDtls.getReportType())){
				File pdfReport = new File(pdfGeneratedLocation);
				byte[] byteFile = (byte[]) bytesFromFile(pdfReport);
				base64 = Base64.encodeBase64String(byteFile);

				responseJson.put(ServerConstants.REPORT_FILE, base64);
				responseJson.put(ServerConstants.REPORT_FILENAME, reportName + lFileExt);

				if(pdfReport.delete()){
					LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "File deleted :: "+pdfGeneratedLocation);
				}

				responseJson.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
			}else if("URL".equalsIgnoreCase(reportDtls.getReportType())){
				responseJson.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
				responseJson.put("filePath", pdfGeneratedLocation);
			}
		} catch (JRException jrExp) {
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " PDF JRException : ",jrExp);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_031.toString());
			exsrvcallexp.setMessage(jrExp.getMessage());
			exsrvcallexp.setPriority("1");
			throw exsrvcallexp;
		} catch (IOException ioExp) {
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " PDF IOException :: ",ioExp);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_033.toString());
			exsrvcallexp.setMessage(ioExp.getMessage());
			exsrvcallexp.setPriority("1");
			throw exsrvcallexp;
		} catch (NamingException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " NamingException :: ",e);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_028.toString());
			exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_028));
			exsrvcallexp.setPriority("1");
			throw exsrvcallexp;
		} catch (SQLException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " SQLException :: ",e);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_029.toString());
			exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_029));
			exsrvcallexp.setPriority("1");
			throw exsrvcallexp;
		} finally {
			DBUtils.closeDbConnection(lConnection);
			LOG.debug("DB Connection Closed.");
		}
		return responseJson.toString();
	}

	@Override
	public String generateExcelReport(Message pMessage) {
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "inside generateExcelReport()..");
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Request : "+pMessage.getRequestObject().getRequestJson());
		JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
		JSONObject reportData = (JSONObject) requestJson.get(ServerConstants.REPORT_DATA);
		JSONObject reportDetails = (JSONObject) reportData.get(ServerConstants.REPORT_DETAILS);
		String lFileType = (String) reportDetails.get(ServerConstants.REPORT_FILE_TYPE);
		String destinationfilePath = PropertyUtils.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.REPORT_JRXML_PATH);
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "destination file Path :: "+destinationfilePath);
		
		String lFileExt = "";
		JSONObject responseJson = new JSONObject();
		String reportName = "";
		if(reportDetails.has("fileName") && !reportDetails.getString("fileName").isEmpty()){
			reportName = reportDetails.getString("fileName");
		}else{
			Date date = new java.util.Date();
			reportName = Long.toString(date.getTime());
		}
		Connection lConnection = null;
		try{
			JRExporter exporter = null;
			if(ServerConstants.REPORT_XLS.equalsIgnoreCase(lFileType)){
				lFileExt = ServerConstants.REPORT_XLS_EXT;
				exporter = new JRXlsExporter();
			}else if(ServerConstants.REPORT_XLSX.equalsIgnoreCase(lFileType)){
				lFileExt = ServerConstants.REPORT_XLSX_EXT;
				exporter = new JRXlsxExporter();
			}
			String fileName = destinationfilePath + reportName + lFileExt ;
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "File Name :: "+fileName);
			
			lConnection = DBUtils.getConnectionFromDataSource(reportDtls.getDataSource());
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Taking Connection From Data source :: " + lConnection);
			
			File xlsReport = new File(fileName);
			OutputStream outputfile = new FileOutputStream(xlsReport);
			exporter.setParameter(JRXlsExporterParameter.JASPER_PRINT, this.loadJasperFile(pMessage, lConnection));
			exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, outputfile);
			exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
			exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
			exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
			exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);

			/*if(reportDtls.getPasswordRequired()){
				 exporter.setParameter(JRXlsExporterParameter.PASSWORD,cPassword );
			 }*/
			exporter.exportReport();
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Exported.....:: "+fileName);
			outputfile.close();


			byte[] byteFile = (byte[]) bytesFromFile(xlsReport);
			String base64 = Base64.encodeBase64String(byteFile);
			responseJson.put(ServerConstants.REPORT_FILE, base64);
			responseJson.put(ServerConstants.REPORT_FILENAME, reportName + lFileExt);

			if(xlsReport.delete()){
				LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Filedeleted :: "+fileName);
			}
			responseJson.put(ServerConstants.MESSAGE_HEADER_STATUS, ServerConstants.SUCCESS);
		} catch (JRException jrExp) {
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Excel JRException : ", jrExp);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_031.toString());
			exsrvcallexp.setMessage(jrExp.getMessage());
			exsrvcallexp.setPriority("1");
			throw exsrvcallexp;
		} catch (IOException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Excel IOException : ", e);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_033.toString());
			exsrvcallexp.setMessage(e.getMessage());
			exsrvcallexp.setPriority("1");
			throw exsrvcallexp;
		} catch (NamingException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " Naming Exception :: ",e);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_028.toString());
			exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_028));
			exsrvcallexp.setPriority("1");
			throw exsrvcallexp;
		} catch (SQLException e) {
			LOG.error(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " SQL Exception :: ",e);
			ExternalServicesRouterException exsrvcallexp = ExternalServicesRouterException.getExternalServicesRouterExceptionInstance();
			exsrvcallexp.setCode(EXCEPTION_CODE.APZ_FM_EX_029.toString());
			exsrvcallexp.setMessage(exsrvcallexp.getFrameWorksExceptionMessage(EXCEPTION_CODE.APZ_FM_EX_029));
			exsrvcallexp.setPriority("1");
			throw exsrvcallexp;
		} finally {
			DBUtils.closeDbConnection(lConnection);
			LOG.debug("DB Connection Closed.");
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


	public static byte[] bytesFromFile(File file) throws IOException {
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

	private JasperPrint loadJasperFile(Message pMessage, Connection connection) throws FileNotFoundException, JRException, NamingException, SQLException{
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "inside loadJasperFile()..");
		String jrxmlSourceFileName = pMessage.getHeader().getAppId() + "_" + pMessage.getHeader().getInterfaceId() + ServerConstants.REPORT_JRXML_FILE_EXT;
		String jasperSourceFileName = pMessage.getHeader().getAppId() + "_" + pMessage.getHeader().getInterfaceId() + ServerConstants.REPORT_JASPER_FILE_EXT;

		String sourceFileLocation = PropertyUtils.getPropValue(pMessage.getHeader().getAppId(), ServerConstants.REPORT_JRXML_PATH);
		LOG.info(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + " sourceFileLocation : "+sourceFileLocation);
		File file = new File(sourceFileLocation + jasperSourceFileName);

		if(! file.exists()){
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Loading Report Designs");
			InputStream input = new FileInputStream(new File(sourceFileLocation + jrxmlSourceFileName));
			JasperDesign jasperDesign = JRXmlLoader.load(input);
			LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Compiling Report Designs");
			JasperCompileManager.compileReportToFile(jasperDesign, sourceFileLocation + jasperSourceFileName);
		}
		JSONObject requestJson = pMessage.getRequestObject().getRequestJson();
		JSONObject reportData = (JSONObject) requestJson.get(ServerConstants.REPORT_DATA);
		JSONObject reportParam = (JSONObject) reportData.get(ServerConstants.REPORT_PARAMS);
		Map<String, Object> parameters = JSONUtils.buildParamMap(reportParam);
		/*Connection connection = null;
		connection = JDBCUtils.getConnectionFromDataSource(reportDtls.getDataSource());
		LOG.debug(ServerConstants.LOGGER_PREFIX_FRAMEWORKS + "Connection :: " + connection);*/

		return JasperFillManager.fillReport(sourceFileLocation + jasperSourceFileName, parameters, connection);
	}
}
