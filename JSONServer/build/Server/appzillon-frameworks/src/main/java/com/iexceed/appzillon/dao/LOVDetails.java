package com.iexceed.appzillon.dao;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class LOVDetails {
	private DataSource dataSource;
	private JdbcTemplate jdbcTemplateObject;
	
	
	public DataSource getDataSource() {
		return dataSource;
	}
	// Setting Datasource and JDBCTemplate Object from the context xml
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
		this.jdbcTemplateObject = new JdbcTemplate(dataSource);

	}
	public JdbcTemplate getJdbcTemplateObject() {
		return jdbcTemplateObject;
	}
	public void setJdbcTemplateObject(JdbcTemplate jdbcTemplateObject) {
		this.jdbcTemplateObject = jdbcTemplateObject;
	}
	

}
