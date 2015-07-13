package digital.demo.application.managers;

import digital.demo.application.logging.AppLogger;

import java.io.Serializable;

import oracle.adf.model.datacontrols.device.DeviceManagerFactory;

public class DeviceManager implements Serializable {
    
    @SuppressWarnings("compatibility")
    private static final long serialVersionUID = 1L;

    private static AppLogger logger = AppLogger.getLogger(DeviceManager.class);
    
    public static String invokeCamera () {
        final String METHOD_NAME = "invokeCamera";
        logger.begin(METHOD_NAME);
        logger.debug (METHOD_NAME, "DeviceManagerFactory.getDeviceManager(): " + DeviceManagerFactory.getDeviceManager());
        String pictureUri = DeviceManagerFactory.getDeviceManager().getPicture(100, 0, 1, false, 0, 300, 300);
        logger.debug (METHOD_NAME, "pictureUri: " + pictureUri);
        pictureUri = "data:image/jpeg;base64," + pictureUri;
        logger.end(METHOD_NAME);
        return pictureUri;
    }

    public static void log(String methodName, String message) {
        logger.debug("selfservice.js:" + methodName, message);
    }
    
}
