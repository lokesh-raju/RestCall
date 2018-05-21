package com.iexceed.appzillon.domain.entity;

import java.io.Serializable;

import javax.persistence.*;


/**
 * The persistent class for the tb_asmi_quartz_expressions database table.
 * 
 */
@Entity
@Table(name="TB_ASMI_SCHEDULER_JOB_EXP")
public class TbAsmiJobExpression implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="JOB_NAME")
	private String jobName;

	@Column(name="END_DATE")
	private String endDate;

	@Column(name="EXP_DOM")
	private String expDom;

	@Column(name="EXP_DOW")
	private String expDow;

	@Column(name="EXP_HR")
	private String expHr;

	@Column(name="EXP_MIN")
	private String expMin;

	@Column(name="EXP_MONTH")
	private String expMonth;

	@Column(name="EXP_SEC")
	private String expSec;

	@Column(name="EXP_START_SEC")
	private String expStartSec;

	@Column(name="EXP_YEAR")
	private String expYear;

	private String expression;

	@Column(name="START_DATE")
	private String startDate;

	public TbAsmiJobExpression() {
	}

	public String getJobName() {
		return this.jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getEndDate() {
		return this.endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getExpDom() {
		return this.expDom;
	}

	public void setExpDom(String expDom) {
		this.expDom = expDom;
	}

	public String getExpDow() {
		return this.expDow;
	}

	public void setExpDow(String expDow) {
		this.expDow = expDow;
	}

	public String getExpHr() {
		return this.expHr;
	}

	public void setExpHr(String expHr) {
		this.expHr = expHr;
	}

	public String getExpMin() {
		return this.expMin;
	}

	public void setExpMin(String expMin) {
		this.expMin = expMin;
	}

	public String getExpMonth() {
		return this.expMonth;
	}

	public void setExpMonth(String expMonth) {
		this.expMonth = expMonth;
	}

	public String getExpSec() {
		return this.expSec;
	}

	public void setExpSec(String expSec) {
		this.expSec = expSec;
	}

	public String getExpStartSec() {
		return this.expStartSec;
	}

	public void setExpStartSec(String expStartSec) {
		this.expStartSec = expStartSec;
	}

	public String getExpYear() {
		return this.expYear;
	}

	public void setExpYear(String expYear) {
		this.expYear = expYear;
	}

	public String getExpression() {
		return this.expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getStartDate() {
		return this.startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	@Override
	public String toString()
	{
		return "TbAsmiJobExperssion [jobName=" + jobName + ", endDate=" + endDate + ", expDom=" + expDom + ", expDow="
				+ expDow + ", expHr=" + expHr + ", expMin=" + expMin + ", expMonth=" + expMonth + ", expSec=" + expSec
				+ ", expStartSec=" + expStartSec + ", expYear=" + expYear + ", expression=" + expression
				+ ", startDate=" + startDate + "]" ;
	}

}