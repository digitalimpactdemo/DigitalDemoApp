package digital.demo.application.managers;

import digital.demo.application.logging.AppLogger;
import digital.demo.application.services.DatabaseService;

import java.io.Serializable;

public class UserPreferenceManager implements Serializable {

    @SuppressWarnings("compatibility")
    private static final long serialVersionUID = 1L;

    private static AppLogger logger = AppLogger.getLogger(UserPreferenceManager.class);

    private static DatabaseService getDatabaseService() {
        if (databaseService == null) {
            databaseService = new DatabaseService ();
        }
        return databaseService;
    }

    private static DatabaseService databaseService = null;

    public static String getUserPreference(String type) {
        final String METHOD_NAME = "getUserPreference";
        logger.begin(METHOD_NAME);
        logger.debug(METHOD_NAME, "type:" + type);
        String value = getDatabaseService().getUserPreference(type);
        logger.debug(METHOD_NAME, "value:" + value);
        logger.end(METHOD_NAME);
        return value;
    }

    public static void saveUserPreference(String type, String value) {
        final String METHOD_NAME = "saveUserPreference";
        logger.begin(METHOD_NAME);
        logger.debug(METHOD_NAME, "type:" + type);
        logger.debug(METHOD_NAME, "value:" + value);
        getDatabaseService().saveUserPreference(type, value);
        logger.end(METHOD_NAME);
    }
    
    public static void incrementNoOfClicks() {
        final String METHOD_NAME = "incrementNoOfClicks";
        logger.begin(METHOD_NAME);
        getDatabaseService().incrementNoOfClicks();
        logger.end(METHOD_NAME);
    }
    
    public static void resetNoOfClicks() {
        final String METHOD_NAME = "resetNoOfClicks";
        logger.begin(METHOD_NAME);
        getDatabaseService().resetNoOfClicks();
        logger.end(METHOD_NAME);
    }
    
    public static int getNoOfClicks() {
        final String METHOD_NAME = "getNoOfClicks";
        logger.begin(METHOD_NAME);
        int noOfClicks = getDatabaseService().getNoOfClicks();
        logger.end(METHOD_NAME);
        return noOfClicks;
    }

    public static int getSystemNoOfClicks() {
        final String METHOD_NAME = "getSystemNoOfClicks";
        logger.begin(METHOD_NAME);
        int noOfClicks = getDatabaseService().getSystemNoOfClicks();
        logger.end(METHOD_NAME);
        return noOfClicks;
    }
}
