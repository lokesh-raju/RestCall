/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iexceed.appzillon.utils.sql;

/**
 *
 * @author arthanarisamy
 */
public class DebugLevel {
	// only allowed values for debugging
	/**
	 * Turn debugging off
	 */
	public static final DebugLevel OFF = new DebugLevel();

	/**
	 * Turn debugging on
	 */
	public static final DebugLevel ON = new DebugLevel();

	/**
	 * Set debugging to verbose
	 */
	public static final DebugLevel VERBOSE = new DebugLevel();

	// private constructor keeps all instances within class
	private DebugLevel() {
	}

}
