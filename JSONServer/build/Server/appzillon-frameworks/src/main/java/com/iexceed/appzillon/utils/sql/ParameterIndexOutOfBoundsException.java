/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iexceed.appzillon.utils.sql;

import java.sql.SQLException;
/**
 *
 * @author arthanarisamy
 */
public class ParameterIndexOutOfBoundsException extends SQLException {

	private static final long serialVersionUID = 1L;

	public ParameterIndexOutOfBoundsException() {
        super();
    }

    public ParameterIndexOutOfBoundsException(String s) {
        super(s);
    }
}
