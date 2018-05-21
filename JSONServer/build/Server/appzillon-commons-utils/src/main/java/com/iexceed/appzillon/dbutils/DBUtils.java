package com.iexceed.appzillon.dbutils;

/**
 *
 * @author ripu
 **/
import java.sql.Connection ;
import java.sql.DriverManager ;
import java.sql.SQLException ;
import javax.naming.InitialContext ;
import javax.naming.NamingException ;
import javax.persistence.EntityManager ;
import javax.persistence.EntityManagerFactory ;
import javax.persistence.Persistence ;
import javax.sql.DataSource ;
import com.iexceed.appzillon.logging.LoggerFactory ;
import com.iexceed.appzillon.utils.ServerConstants ;

public class DBUtils {

	private static final com.iexceed.appzillon.logging.Logger LOG = LoggerFactory.getLoggerFactory()
            .getRestServicesLogger(ServerConstants.LOGGER_RESTFULL_SERVICES, DBUtils.class.toString());
	
	private static EntityManagerFactory entityManagerFactory = null;
	
	/**
     * Gives Connection object from database using supplied parameters
     * @param jdbcDriverName A string jdbcDriver name.
     * @param dbURL A String data base url.
     * @param dbUserName A String data base user name.
     * @param dbPassword A String data base password.
     * @return A Connection Object of database.
     * @throws ClassNotFoundException, SQLException
     */
	public static Connection myDBConnection(String jdbcDriverName, String dbURL, String dbUserName, String dbPassword){
		Connection conn = null;
		
			try {
				Class.forName(jdbcDriverName);
				conn = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
			} catch (ClassNotFoundException e) {
				LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + " ClassNotFoundException ", e);
			} catch (SQLException e) {
				LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + " SQLException ", e);
			}	      
			
			
		return conn;
	}
	

	/**
     * Gives Connection object from datasource using provided datasource name
     * @param pdataSource A string.
     * @return A Connection Object of database.
     * @throws NamingException, SQLException
     */
	public static Connection getConnectionFromDataSource(String pdataSource){
		Connection conn = null;

			InitialContext lCtx;
			try {
				lCtx = new InitialContext();
				DataSource dataSource = (javax.sql.DataSource) lCtx.lookup(pdataSource);
				conn = dataSource.getConnection();
			} catch (NamingException e) {
				LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + " NamingException ", e);
			} catch (SQLException e) {
				LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + " SQLException ", e);
			}
			
		
		return conn;
	}
	
	public static void closeDbConnection(Connection pConn) {
        try {
        	if(pConn != null){
        		pConn.close();
        	}
        } catch (Exception ex) {
        	LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + " Exception while closing the connection - ", ex);
        }
    }
	
	public static EntityManagerFactory getEntityMangerFactory(String persistentUnitName){
		if(entityManagerFactory == null){
			entityManagerFactory = Persistence.createEntityManagerFactory(persistentUnitName);
		}
		return entityManagerFactory;
	}
	
	public static EntityManager getEntityManager(String persistentUnitName){
		return getEntityMangerFactory(persistentUnitName).createEntityManager();
	}
	
	public static void CloseEntityManager(EntityManager em){
		try{
			if(em != null){
				em.close();
			}
		}catch(Exception ex){
        	LOG.error(ServerConstants.LOGGER_PREFIX_RESTFULL + " Exception while closing the connection - ", ex);

		}
	}
}

