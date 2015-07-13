package digital.demo.application.services;

import digital.demo.application.logging.AppLogger;
import digital.demo.application.utils.DataSourceUtil;

import java.io.Serializable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseService implements Serializable {
    
    @SuppressWarnings("compatibility")
    private static final long serialVersionUID = 1L;

    private transient AppLogger logger = AppLogger.getLogger(this.getClass ());

    private static final String NO_OF_USER_CLICKS = "NO_OF_CLICKS";
    
    private Connection getConnection() throws Exception {
        return DataSourceUtil.getConnection();
    }
    
    public void saveUserPreference (String type, String value) {
        final String METHOD_NAME = "saveUserPreference";
        logger.begin(METHOD_NAME);
        logger.error (METHOD_NAME, "type:" + type);
        logger.error (METHOD_NAME, "value:" + value);
        Connection connection       =   null;
        PreparedStatement statement =   null;
        ResultSet resultSet         =   null;
        try {
            connection          =   getConnection ();
            connection.setAutoCommit(false);
            if (existsUserPreference(type)) {
                statement = connection.prepareStatement("UPDATE USER_PREFERENCES SET PREF_VALUE = ? WHERE PREF_TYPE = ?;");
            }
            else {
                statement = connection.prepareStatement("INSERT INTO USER_PREFERENCES (PREF_VALUE,PREF_TYPE) VALUES (?,?);");
            }
            statement.clearParameters();
            statement.setString(1, value);
            statement.setString(2, type);
            statement.executeUpdate();
            DataSourceUtil.closeAll (statement, resultSet);
            connection.commit();
        } catch (Exception expGeneral) {
            logger.error (METHOD_NAME, "Error while saving User Preference.", expGeneral);
        }
        finally {
            DataSourceUtil.closeAll (statement, resultSet);
        }
        logger.end(METHOD_NAME);
    }
    
    public String getUserPreference (String type)  {
        final String METHOD_NAME = "getUserPreference";
        logger.begin(METHOD_NAME);
        logger.error (METHOD_NAME, "type:" + type);
        Connection connection       =   null;
        PreparedStatement statement =   null;
        ResultSet resultSet         =   null;
        String value                =   null;
        try {
            connection          =   getConnection ();
            statement = connection.prepareStatement("SELECT PREF_VALUE FROM USER_PREFERENCES WHERE PREF_TYPE = ?;");
            statement.clearParameters();
            statement.setString(1, type);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                value = resultSet.getString("PREF_VALUE");
            }
            DataSourceUtil.closeAll (statement, resultSet);
        } catch (Exception expGeneral) {
            logger.error (METHOD_NAME, "Error while retrieving User Preference [" + type + "]", expGeneral);
        }
        finally {
            DataSourceUtil.closeAll (statement, resultSet);
        }
        if (value == null) {
            value = "";
        }
        logger.error (METHOD_NAME, "value:" + value);
        return value;
    }
    
    public boolean existsUserPreference (String type)  {
        final String METHOD_NAME = "existsUserPreference";
        logger.begin(METHOD_NAME);
        logger.error (METHOD_NAME, "type:" + type);
        Connection connection       =   null;
        PreparedStatement statement =   null;
        ResultSet resultSet         =   null;
        boolean exists              =   false;
        try {
            connection          =   getConnection ();
            statement = connection.prepareStatement("SELECT PREF_VALUE FROM USER_PREFERENCES WHERE PREF_TYPE = ?;");
            statement.clearParameters();
            statement.setString(1, type);
            resultSet = statement.executeQuery();
            if(resultSet.next()) {
                exists = true;
            }
            DataSourceUtil.closeAll (statement, resultSet);
        } catch (Exception expGeneral) {
            logger.error (METHOD_NAME, "Error while retrieving User Preference [" + type + "]", expGeneral);
        }
        finally {
            DataSourceUtil.closeAll (statement, resultSet);
        }

        logger.debug (METHOD_NAME, "exists:" + exists);
        return exists;
    }

    public void incrementNoOfClicks() {
        final String METHOD_NAME = "incrementNoOfClicks";
        logger.begin(METHOD_NAME);
        int noOfClicks  =   getNoOfClicks();
        logger.error (METHOD_NAME, "Original noOfClicks:" + noOfClicks);
        noOfClicks++;
        saveUserPreference(NO_OF_USER_CLICKS, String.valueOf(noOfClicks));
        logger.error (METHOD_NAME, "Updated noOfClicks:" + noOfClicks);
        logger.end(METHOD_NAME);
    }
    
    public void resetNoOfClicks() {
        final String METHOD_NAME = "resetNoOfClicks";
        logger.begin(METHOD_NAME);
        saveUserPreference(NO_OF_USER_CLICKS, String.valueOf(0));
        logger.end(METHOD_NAME);
    }
    
    public int getNoOfClicks ()  {
        final String METHOD_NAME = "getNoOfClicks";
        logger.begin(METHOD_NAME);
        int noOfClicks  =   0;
        String value    =   getUserPreference(NO_OF_USER_CLICKS);
        try {
            if (value != null && !value.equals("")) {
                noOfClicks = Integer.parseInt (value);
            }
        }
        catch (NumberFormatException expNE) {
            logger.error (METHOD_NAME, "Error while retrieving User Preference [" + NO_OF_USER_CLICKS + "]", expNE);
        }
        logger.debug (METHOD_NAME, "noOfClicks:" + noOfClicks);
        logger.end(METHOD_NAME);
        return noOfClicks;
    }
    
    public int getSystemNoOfClicks ()  {
        final String METHOD_NAME = "getSystemNoOfClicks";
        logger.begin(METHOD_NAME);
        int noOfClicks  =   0;
        String value    =   getUserPreference("noofclicks");
        try {
            if (value != null && !value.equals("")) {
                noOfClicks = Integer.parseInt (value);
            }
        }
        catch (NumberFormatException expNE) {
            logger.error (METHOD_NAME, "Error while retrieving System User Preference [noofclicks]", expNE);
        }
        logger.debug (METHOD_NAME, "noOfClicks:" + noOfClicks);
        logger.end(METHOD_NAME);
        return noOfClicks;
    }
}
