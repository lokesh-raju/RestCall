package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the tb_asmi_quartz_jobs database table.
 * 
 */
@Entity
@Table(name="TB_ASMI_SCHEDULER_JOB")
public class TbAsmiJobMaster implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="JOB_NAME")
	private String jobName;

	@Column(name="APP_ID")
	private String appId;

	@Column(name="JOB_CLASS")
	private String jobClass;

	@Column(name="JOB_CREATED_TS")
	private Timestamp jobCreatedTs;

	@Column(name="JOB_DATA")
	private String jobData;

	@Column(name="JOB_EXECUTED_TS")
	private Timestamp jobExecutedTs;

	@Column(name="JOB_STATUS")
	private String jobStatus;

	public TbAsmiJobMaster() {
	}

	public String getJobName() {
		return this.jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getAppId() {
		return this.appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getJobClass() {
		return this.jobClass;
	}

	public void setJobClass(String jobClass) {
		this.jobClass = jobClass;
	}

	public Timestamp getJobCreatedTs() {
		return this.jobCreatedTs;
	}

	public void setJobCreatedTs(Timestamp jobCreatedTs) {
		this.jobCreatedTs = jobCreatedTs;
	}

	public String getJobData() {
		return this.jobData;
	}

	public void setJobData(String jobData) {
		this.jobData = jobData;
	}

	public Timestamp getJobExecutedTs() {
		return this.jobExecutedTs;
	}

	public void setJobExecutedTs(Timestamp jobExecutedTs) {
		this.jobExecutedTs = jobExecutedTs;
	}

	public String getJobStatus() {
		return this.jobStatus;
	}

	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}

	@Override
	public String toString()
	{
		return "TbAsmiJobMaster [jobName=" + jobName + ", appId=" + appId + ", jobClass=" + jobClass
				+ ", jobCreatedTs=" + jobCreatedTs + ", jobData=" + jobData + ", jobExecutedTs=" + jobExecutedTs
				+ ", jobStatus=" + jobStatus + "]" ;
	}

	
	
}