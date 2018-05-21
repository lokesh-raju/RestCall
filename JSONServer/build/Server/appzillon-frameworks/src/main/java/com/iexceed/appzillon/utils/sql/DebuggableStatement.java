/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iexceed.appzillon.utils.sql;

import java.sql.*;
import java.io.*;
import java.math.*;
import java.util.*;
import java.lang.reflect.*;
import java.net.URL;

import com.iexceed.appzillon.utils.ServerConstants;

/**
 *
 * @author arthanarisamy
 */
public class DebuggableStatement implements PreparedStatement{

    private PreparedStatement ps;
    private String sql;
    private String filteredSql;
    private DebugObject[] variables;
    private SqlFormatter formatter;
    private long startTime;
    private long executeTime;
    private DebugLevel debugLevel;

    /**
        Construct new DebugableStatement.
        Uses the SqlFormatter to format date, time, timestamp outputs
        @param con Connection to be used to construct PreparedStatement
        @param sqlStatement sql statement to be sent to database.
        @param debugLevel DebugLevel can be ON, OFF, VERBOSE.

    */
    protected DebuggableStatement(Connection con, String sqlStatement, SqlFormatter formatter, DebugLevel debugLevel) throws SQLException{
        if (con == null){
            throw new SQLException("Connection object is null");
        }
        this.ps = con.prepareStatement(sqlStatement);
        this.sql = sqlStatement;
        this.debugLevel = debugLevel;
        this.formatter = formatter;
        boolean isString = false;
        char[] sqlString = sqlStatement.toCharArray();
        for (int i = 0; i < sqlString.length; i++){
            if (sqlString[i] == '\''){
                isString = !isString;
            }
            if (sqlString[i] == '?' && isString){
                sqlString[i] = '\u0007';
            }
        }
        filteredSql = new String(sqlString);
        int count = 0;
        int index = -1;
        while ((index = filteredSql.indexOf("?",index+1)) != -1){
            count++;
        }

        if (debugLevel == DebugLevel.VERBOSE){
            System.out.println("count= " + count);
        }
        variables = new DebugObject[count];
    }

    /**
     * Facade for PreparedStatement
     */
    public void addBatch() throws SQLException{
        ps.addBatch();
    }

    /**
     * Facade for PreparedStatement
     */
    public void addBatch(String sql) throws SQLException{
        ps.addBatch();
    }

    /**
     * Facade for PreparedStatement
     */
    public void cancel() throws SQLException{
        ps.cancel();
    }

    /**
     * Facade for PreparedStatement
     */
    public void clearBatch() throws SQLException{
        ps.clearBatch();
    }

    /**
     * Facade for PreparedStatement
     */
    public void clearParameters() throws SQLException{
        ps.clearParameters();
    }

    /**
     * Facade for PreparedStatement
     */
    public void clearWarnings() throws SQLException{
        ps.clearWarnings();
    }

    /**
     * Facade for PreparedStatement
     */
    public void close() throws SQLException{
        ps.close();
    }

    /**
     * Executes query and Calculates query execution time if DebugLevel = VERBOSE
     * @return results of query
     */
    public boolean execute() throws SQLException{
        Boolean results = null;
        try{
            results = (Boolean)executeVerboseQuery("execute",null);
        }catch(Exception e){
           throw new SQLException("Could not execute sql command - Original message: " + e.getMessage());
        }
        return results.booleanValue();
    }

    /**
     * This method is only here for convenience. If a different sql string is executed
     * than was passed into Debuggable, unknown results will occur.
     * Executes query and Calculates query execution time if DebugLevel = VERBOSE
     * @param sql should be same string that was passed into Debuggable
     * @return results of query
     */
    public boolean execute(String sql) throws SQLException{
        Boolean results = null;
        try{
            results = (Boolean)executeVerboseQuery("execute",new Class[]{sql.getClass()});
        }catch(Exception e){
            throw new SQLException("Could not execute sql command - Original message: " + e.getMessage());
        }
        return results.booleanValue();
    }

    /**
     * Executes query and Calculates query execution time if DebugLevel = VERBOSE
     * @return results of query
     */
    public int[] executeBatch() throws SQLException{
        int[] results = null;
        try{
            results = (int[])executeVerboseQuery("executeBatch",null);
        }catch(Exception e){
            throw new SQLException("Could not execute sql command - Original message: " + e.getMessage());
        }
        return results;
    }

    /**
     * Executes query and Calculates query execution time if DebugLevel = VERBOSE
     * @return results of query
     */
    public ResultSet executeQuery() throws SQLException{
        ResultSet results = null;
        try{
            results = (ResultSet)executeQuery(sql);
        }catch(Exception e){
            throw new SQLException("Could not execute sql command - Original message: " + e.getMessage());
        }
        return results;
    }

    /**
     * This method is only here for convenience. If a different sql string is executed
     * than was passed into Debuggable, unknown results will occur.
     * Executes query and Calculates query execution time if DebugLevel = VERBOSE
     * @param sql should be same string that was passed into Debuggable
     * @return results of query
     */
    public ResultSet executeQuery(String sql) throws SQLException{
        ResultSet results = null;
        try{
            results = (ResultSet)executeVerboseQuery("executeQuery",new Class[]{sql.getClass()});
        }catch(Exception e){
            throw new SQLException("Could not execute sql command - Original message: " + e.getMessage());
        }
        return results;
    }

    /**
     * Executes query and Calculates query execution time if DebugLevel = VERBOSE
     * @return results of query
     */
    public int executeUpdate() throws SQLException{
        Integer results = null;
        try{
            results = (Integer)executeVerboseQuery("executeUpdate",null);
        }catch(Exception e){
            throw new SQLException("Could not execute sql command - Original message: " + e.getMessage());
        }
        return results.intValue();
    }

    /**
     * This method is only here for convenience. If a different sql string is executed
     * than was passed into Debuggable, unknown results will occur.
     * Executes query and Calculates query execution time if DebugLevel = VERBOSE
     * @param sql should be same string that was passed into Debuggable
     * @return results of query
     */
    public int executeUpdate(String sql) throws SQLException{
        Integer results = null;
        try{
            results = (Integer)executeVerboseQuery("executeUpdate",new Class[]{sql.getClass()});
        }catch(Exception e){
            throw new SQLException("Could not execute sql command - Original message: " + e.getMessage());
        }
        return results.intValue();
    }

    /**
     * Facade for PreparedStatement
     */
    public Connection getConnection() throws SQLException{
        return ps.getConnection();
    }

    /**
     * Facade for PreparedStatement
     */
    public int getFetchDirection() throws SQLException{
        return ps.getFetchDirection();
    }

    /**
     * Facade for PreparedStatement
     */
    public int getFetchSize() throws SQLException{
        return ps.getFetchSize();
    }

    /**
     * Facade for PreparedStatement
     */
    public int getMaxFieldSize() throws SQLException{
        return ps.getMaxFieldSize();
    }

    /**
     * Facade for PreparedStatement
     */
    public int getMaxRows() throws SQLException{
        return ps.getMaxRows();
    }

    /**
     * Facade for PreparedStatement
     */
    public ResultSetMetaData getMetaData() throws SQLException{
        return ps.getMetaData();
    }

    /**
     * Facade for PreparedStatement
     */
    public boolean getMoreResults() throws SQLException{
        return ps.getMoreResults();
    }

    /**
     * Facade for PreparedStatement
     */
    public int getQueryTimeout() throws SQLException{
        return ps.getQueryTimeout();
    }

    /**
     * Facade for PreparedStatement
     */
    public ResultSet getResultSet() throws SQLException{
        return ps.getResultSet();
    }

    /**
     * Facade for PreparedStatement
     */
    public int getResultSetConcurrency() throws SQLException{
        return ps.getResultSetConcurrency();
    }

    /**
     * Facade for PreparedStatement
     */
    public int getResultSetType() throws SQLException{
        return ps.getResultSetType();
    }

    /**
     * Facade for PreparedStatement
     */
    public String getStatement(){
        return sql;
    }

    /**
     * Facade for PreparedStatement
     */
    public int getUpdateCount() throws SQLException{
        return ps.getUpdateCount();
    }

    /**
     * Facade for PreparedStatement
     */
    public SQLWarning getWarnings() throws SQLException{
        return ps.getWarnings();
    }

    /**
     * Tests Object o for parameterIndex (which parameter is being set) and places
     * object in array of variables.
     * @param parameterIndex which PreparedStatement parameter is being set.
     * Sequence begins at 1.
     * @param o Object being stored as parameter
     * @exception Thrown if index exceeds number of variables.
     */
    private void saveObject(int parameterIndex, Object o)throws ParameterIndexOutOfBoundsException {
        if(parameterIndex > variables.length){
            throw new ParameterIndexOutOfBoundsException("Parameter index of " +
            parameterIndex + " exceeds actual parameter count of " + variables.length);
        }
        variables[parameterIndex-1] = new DebugObject(o);
    }

    /**
        Adds name of the Array's internal class type(by using x.getBaseTypeName())
         to the debug String. If x is null, NULL is added to debug String.
        @param i index of parameter
        @param x parameter Object
    */
    public void setArray(int i, java.sql.Array x) throws SQLException{
        saveObject(i,x);
        ps.setArray(i,x);
    }

    /**
        Debug string prints NULL if InputStream is null, or adds "stream length = " + length
    */
    public void setAsciiStream(int parameterIndex,InputStream x, int length) throws SQLException{
        saveObject(parameterIndex, (x==null?"NULL":"<stream length= " + length+">"));
        ps.setAsciiStream(parameterIndex,x,length);
    }

    /**
        Adds BigDecimal to debug string in parameterIndex position.
        @param parameterIndex index of parameter
        @param x parameter Object
    */
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException{
        saveObject(parameterIndex, x);
        ps.setBigDecimal(parameterIndex,x);
    }

    /**
        Debug string prints NULL if InputStream is null, or adds "stream length= " + length.
        @param parameterIndex index of parameter
        @param x parameter Object
        @param length length of InputStream
    */
    public void setBinaryStream(int parameterIndex,InputStream x, int length) throws SQLException{
        saveObject(parameterIndex, (x==null?"NULL":"<stream length= " + length+">"));
        ps.setBinaryStream(parameterIndex,x,length);
    }

    /**
        Adds name of the object's class type(Blob) to the debug String. If
        object is null, NULL is added to debug String.
        @param parameterIndex index of parameter
        @param x parameter Object
    */
    public void setBlob(int parameterIndex, Blob x) throws SQLException{
        saveObject(parameterIndex, x);
        ps.setBlob(parameterIndex, x);
    }

    /**
        Adds boolean to debug string in parameterIndex position.
        @param parameterIndex index of parameter
        @param x parameter Object
    */
    public void setBoolean(int parameterIndex, boolean x) throws SQLException{
        saveObject(parameterIndex, new Boolean(x));
        ps.setBoolean(parameterIndex,x);
    }

    /**
        Adds byte to debug string in parameterIndex position.
        @param parameterIndex index of parameter
        @param x parameter Object
    */
    public void setByte(int parameterIndex, byte x) throws SQLException{
        saveObject(parameterIndex, new Byte(x));
        ps.setByte(parameterIndex,x);
    }

    /**
        Adds byte[] to debug string in parameterIndex position.
        @param parameterIndex index of parameter
        @param x parameter Object
    */
    public void setBytes(int parameterIndex, byte[] x) throws SQLException{
        saveObject(parameterIndex, (x==null?"NULL":"byte[] length="+x.length));
        ps.setBytes(parameterIndex,x);
    }

    /**
        Debug string prints NULL if reader is null, or adds "stream length= " + length.
        @param parameterIndex index of parameter
        @param x parameter Object
        @param length length of InputStream
    */
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException{
        saveObject(parameterIndex, (reader==null?"NULL":"<stream length= " + length+">"));
        ps.setCharacterStream(parameterIndex,reader,length);
    }

    /**
        Adds name of the object's class type(Clob) to the debug String. If
        object is null, NULL is added to debug String.
        @param parameterIndex index of parameter
        @param x parameter Object
    */
    public void setClob(int i, Clob x) throws SQLException{
        saveObject(i, x);
        ps.setClob(i,x);
    }

    public void setCursorName(String name) throws SQLException{
        ps.setCursorName(name);
    }

    /**
        Debug string displays date in YYYY-MM-DD HH24:MI:SS.# format.
        @param parameterIndex index of parameter
        @param x parameter Object
    */
    public void setDate(int parameterIndex, java.sql.Date x) throws SQLException{
        saveObject(parameterIndex,x);
        ps.setDate(parameterIndex,x);
    }

    /**
        this implementation assumes that the Date has the date, and the
        calendar has the local info. For the debug string, the cal date
        is set to the date of x. Debug string displays date in YYYY-MM-DD HH24:MI:SS.# format.
        @param parameterIndex index of parameter
        @param x parameter Object
        @param cal uses x to set time
    */
    public void setDate(int parameterIndex, java.sql.Date x, Calendar cal) throws SQLException{
        cal.setTime(new java.util.Date(x.getTime()));
        saveObject(parameterIndex,cal);
        ps.setDate(parameterIndex,x,cal);
    }

    /**
        Adds double to debug string in parameterIndex position.
        @param parameterIndex index of parameter
        @param x parameter Object
    */
    public void setDouble(int parameterIndex,double x) throws SQLException{
        saveObject(parameterIndex, new Double(x));
        ps.setDouble(parameterIndex,x);
    }

    /**
     * Facade for PreparedStatement
     */
    public void setEscapeProcessing(boolean enable) throws SQLException{
        ps.setEscapeProcessing(enable);
    }

    /**
     * Facade for PreparedStatement
     */
    public void setFormatter(SqlFormatter formatter){
        this.formatter = formatter;
    }

    /**
     * Facade for PreparedStatement
     */
    public void setFetchDirection(int direction) throws SQLException{
        ps.setFetchDirection(direction);
    }

    /**
     * Facade for PreparedStatement
     */
    public void setFetchSize(int rows) throws SQLException{
        ps.setFetchSize(rows);
    }

    /**
        Adds float to debug string in parameterIndex position.
        @param parameterIndex index of parameter
        @param x parameter Object
    */
    public void setFloat(int parameterIndex, float x) throws SQLException{
        saveObject(parameterIndex, new Float(x));
        ps.setFloat(parameterIndex,x);
    }

    /**
        Adds int to debug string in parameterIndex position.
        @param parameterIndex index of parameter
        @param x parameter Object
    */
    public void setInt(int parameterIndex, int x) throws SQLException{
        saveObject(parameterIndex, new Integer(x));
        ps.setInt(parameterIndex,x);
    }

    /**
        Adds long to debug string in parameterIndex position.
        @param parameterIndex index of parameter
        @param x parameter Object
    */
    public void setLong(int parameterIndex, long x) throws SQLException{
        saveObject(parameterIndex, new Long(x));
        ps.setLong(parameterIndex,x);
    }

    /**
     * Facade for PreparedStatement
     */
    public void setMaxFieldSize(int max) throws SQLException{
        ps.setMaxFieldSize(max);
    }

    /**
     * Facade for PreparedStatement
     */
    public void setMaxRows(int max) throws SQLException{
        ps.setMaxRows(max);
    }

    /**
        Adds a NULL to the debug String.
        @param parameterIndex index of parameter
        @param x parameter Object
    */
    public void setNull(int parameterIndex, int sqlType) throws SQLException{
        saveObject(parameterIndex, "NULL");
        ps.setNull(parameterIndex,sqlType);
    }

    /**
        Adds a NULL to the debug String.
        @param parameterIndex index of parameter
        @param x parameter Object
        @param typeName type of Object
    */
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException{
        saveObject(parameterIndex, "NULL");
        ps.setNull(parameterIndex,sqlType,typeName);
    }

    /**
        Adds name of the object's class type to the debug String. If
        object is null, NULL is added to debug String.
        @param parameterIndex index of parameter
        @param x parameter Object
    */
    public void setObject(int parameterIndex, Object x) throws SQLException{
        saveObject(parameterIndex, (x==null?"NULL":x.getClass().getName()));
        ps.setObject(parameterIndex, x);
    }

   /**
        Adds name of the object's class type to the debug String. If
        object is null, NULL is added to debug String.
        @param parameterIndex index of parameter
        @param x parameter Object
        @param targetSqlType database type
    */
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException{
        saveObject(parameterIndex, (x==null?"NULL":x.getClass().getName()));
        ps.setObject(parameterIndex, x, targetSqlType);
    }

    /**
        Adds name of the object's class type to the debug String. If
        object is null, NULL is added to debug String.
        @param parameterIndex index of parameter
        @param x parameter Object
        @param targetSqlType database type
        @param scale see PreparedStatement
    */
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scale) throws SQLException{
        saveObject(parameterIndex, (x==null?"NULL":x.getClass().getName()));
        ps.setObject(parameterIndex, x, targetSqlType, scale);
    }

    /**
     * Facade for PreparedStatement
     */
    public void setQueryTimeout(int seconds) throws SQLException{
        ps.setQueryTimeout(seconds);
    }

    /**
        From the javadocs:
            A reference to an SQL structured type value in the database.
            A Ref can be saved to persistent storage.
        The output from this method call in DebuggableStatement is a string representation
        of the Ref object by calling the Ref object's getBaseTypeName() method.
        Again, this will only be a String representation of the actual object
        being stored in the database.
        @param i index of parameter
        @param x parameter Object
    */

    public void setRef(int i, Ref x) throws SQLException{
        saveObject(i, x);
        ps.setRef(i,x);
    }

    /**
        Adds short to debug string in parameterIndex position.
        @param parameterIndex index of parameter
        @param x parameter Object
    */
    public void setShort(int parameterIndex, short x) throws SQLException{
        saveObject(parameterIndex, new Short(x));
        ps.setShort(parameterIndex, x);
    }

    /**
        Adds String to debug string in parameterIndex position.
        If String is null "NULL" is inserted in debug string.
        ****note****
        In situations where a single ' is in the string being
        inserted in the database. The debug string will need to be modified to
        reflect this when running the debug statement in the database.
        @param parameterIndex index of parameter
        @param x parameter Object
    */
    public void setString(int parameterIndex, String x) throws SQLException{
        saveObject(parameterIndex,x);
        ps.setString(parameterIndex,x);
    }

    /**
        Debug string displays Time in HH24:MI:SS.# format.
        @param parameterIndex index of parameter
        @param x parameter Object
    */
    public void setTime(int parameterIndex, Time x) throws SQLException{
        saveObject(parameterIndex,x);
        ps.setTime(parameterIndex,x);
    }

   /**
        This implementation assumes that the Time object has the time and
        Calendar has the locale info. For the debug string, the cal time
        is set to the value of x. Debug string displays time in HH24:MI:SS.# format.
        @param parameterIndex index of parameter
        @param x parameter Object
        @param cal sets time based on x
    */
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException{
        cal.setTime(new java.util.Date(x.getTime()));
        saveObject(parameterIndex, cal);
        ps.setTime(parameterIndex, x, cal);
    }

    /**
        Debug string displays timestamp in YYYY-MM-DD HH24:MI:SS.# format.
        @param parameterIndex index of parameter
        @param x parameter Object
    */
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException{
        saveObject(parameterIndex,x);
        ps.setTimestamp(parameterIndex, x);
    }

    /**
        This implementation assumes that the Timestamp has the date/time and
        Calendar has the locale info. For the debug string, the cal date/time
        is set to the default value of Timestamp which is YYYY-MM-DD HH24:MI:SS.#.
        Debug string displays timestamp in DateFormat.LONG format.
        @param parameterIndex index of parameter
        @param x parameter Object
        @param cal sets time based on x
    */
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException{
        cal.setTime(new java.util.Date(x.getTime()));
        saveObject(parameterIndex,cal);
        ps.setTimestamp(parameterIndex, x, cal);
    }

    /**
        Method has been deprecated in PreparedStatement interface.
        This method is present only to satisfy interface and does
        not do anything.
        Do not use...
        @deprecated
    */
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException{
        //ps.setUnicodeStream(parameterIndex, x, length);
    }

    /**
        this toString is overidden to return a String representation of
        the sql statement being sent to the database. If a bind variable
        is missing then the String contains a ? + (missing variable #)
        @return the above string representation
    */
    public String toString(){
        StringTokenizer st = new StringTokenizer(filteredSql,"?");
        int count = 1;
        StringBuffer statement = new StringBuffer();
        while(st.hasMoreTokens()){
            statement.append(st.nextToken());
            if(count <= variables.length){
                if(variables[count-1] != null && variables[count-1].isValueAssigned()){
                    try{
                         statement.append(formatter.format(variables[count-1].getDebugObject()));
                    }catch(SQLException e){
                         statement.append("SQLException");
                    }
                }else{
                    statement.append("? "+"(missing variable # " + count+" ) ");
                }
            }
            count++;
        }
        char[] unfilterSql = statement.toString().toCharArray();
        for(int i = 0; i < unfilterSql.length; i++){
            if (unfilterSql[i] == '\u0007'){
                unfilterSql[i] = '?';
            }
        }

        if (debugLevel == DebugLevel.ON){
            return new String(unfilterSql);
        }else{
            return new String(unfilterSql) +
                                System.getProperty("line.separator") +
                                System.getProperty("line.separator") +
                                "query executed in " + executeTime + " milliseconds" +
                                System.getProperty("line.separator");
        }

    }

    private Object executeVerboseQuery(String methodName,Class[] parameters)
                                throws SQLException,NoSuchMethodException,
                                       InvocationTargetException,IllegalAccessException{
        Method m = ps.getClass().getDeclaredMethod(methodName,parameters);

        if (debugLevel == DebugLevel.ON){
            return m.invoke(ps,parameters);
        }
        start();
        Object returnObject = m.invoke(ps,parameters);
        end();

        return returnObject;
    }

    private void start(){
        startTime = System.currentTimeMillis();
    }

    private void end(){
        executeTime = System.currentTimeMillis()-startTime;
    }

    public void setURL(int parameterIndex, URL x) throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    public ParameterMetaData getParameterMetaData() throws SQLException {
       return ps.getParameterMetaData();
    }

    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    public void setNString(int parameterIndex, String value) throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    public boolean getMoreResults(int current) throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    public boolean execute(String sql, String[] columnNames) throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    public int getResultSetHoldability() throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    public boolean isClosed() throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    public void setPoolable(boolean poolable) throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    public boolean isPoolable() throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    public void closeOnCompletion() throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    public boolean isCloseOnCompletion() throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException(ServerConstants.FRM_NOT_SUPPORTED_YET);
    }

    
private class DebugObject{
    private Object debugObject;
    private boolean valueAssigned;

    public DebugObject(Object debugObject){
        this.debugObject = debugObject;
        valueAssigned = true;
    }

    public Object getDebugObject(){
        return debugObject;
    }

    public boolean isValueAssigned(){
        return valueAssigned;
    }
}
}

