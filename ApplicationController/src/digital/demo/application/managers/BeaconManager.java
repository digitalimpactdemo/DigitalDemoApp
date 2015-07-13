package digital.demo.application.managers;

import digital.demo.application.logging.AppLogger;
import digital.demo.application.model.Beacon;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import oracle.adfmf.amx.event.ActionEvent;
import oracle.adfmf.framework.api.AdfmfContainerUtilities;
import oracle.adfmf.framework.api.AdfmfJavaUtilities;
import oracle.adfmf.java.beans.ProviderChangeListener;
import oracle.adfmf.java.beans.ProviderChangeSupport;

public class BeaconManager implements Serializable {
    
    @SuppressWarnings("compatibility")
    private static final long serialVersionUID = 1L;

    private static AppLogger logger = AppLogger.getLogger(BeaconManager.class);

    private static String uuid = null;
    private static boolean applicationActive = false;

    private static List<Beacon> beacons = new ArrayList<Beacon>();
    protected transient static ProviderChangeSupport providerChangeSupport = new ProviderChangeSupport(BeaconManager.class);
    
    public BeaconManager() {
        if (beacons == null) {
            beacons = new ArrayList<Beacon>();
            providerChangeSupport = new ProviderChangeSupport(this);
        }
    }

    public static void setApplicationActive(boolean applicationActive) {
        BeaconManager.applicationActive = applicationActive;
    }

    public static boolean isApplicationActive() {
        return applicationActive;
    }

    public void startMonitoring(ActionEvent actionEvent) {
        final String METHOD_NAME = "startMonitoring";
        logger.begin(METHOD_NAME);
        AdfmfContainerUtilities.invokeContainerJavaScriptFunction(AdfmfJavaUtilities.getFeatureId(), "startMonitoring", new Object[] {});
        logger.end(METHOD_NAME);
    }

    public void stopMonitoring(ActionEvent actionEvent) {
        final String METHOD_NAME = "stopMonitoring";
        logger.begin(METHOD_NAME);
        AdfmfContainerUtilities.invokeContainerJavaScriptFunction(AdfmfJavaUtilities.getFeatureId(), "stopMonitoring", new Object[] {});
        logger.end(METHOD_NAME);
        
    }

    public List<Beacon> getBeacons() {
        return beacons;
    }

    public static int getBeaconsSize() {
        return beacons.size ();
    }
    
    public static String getRequestJSON(String type, String dataType, String username) {
        final String METHOD_NAME = "getRequestJSON";
        logger.begin(METHOD_NAME);
        String requestJSON = "";
        requestJSON += "{";
        requestJSON += "\"uuid\":\"" + getUuid() + "\",";
        requestJSON += "\"username\":\"" + username + "\",";
        requestJSON += "\"type\":\"" + type + "\",";
        if (dataType != null) {
            requestJSON += "\"dataType\":\"" + dataType + "\",";
        }
        requestJSON += "\"beacons\": [";
        
        Iterator <Beacon> iterator = beacons.iterator();
        while (iterator.hasNext()) {
            Beacon beacon = iterator.next();
            requestJSON += "{\"major\": \"" + beacon.getMajor() + "\",\"minor\": \"" + beacon.getMinor() + "\",\"proximity\": \"" + beacon.getProximity() + "\"}";
            if (iterator.hasNext()) {
                requestJSON += ",";
            }
        }
        requestJSON += "]";
        requestJSON += "}";
        logger.debug(METHOD_NAME, "requestJSON:" + requestJSON);
        logger.end(METHOD_NAME);
        return requestJSON;
    }
    
    public static void beaconRanged(String uuid, int major, int minor, String proximity) {
        final String METHOD_NAME = "beaconRanged";
        logger.begin(METHOD_NAME);
        for (Beacon beacon : beacons) {
            if (beacon.getMajor() == major && beacon.getMinor() == minor) {
                // Existing beacon, update proximity
                if (!proximity.equalsIgnoreCase(beacon.getProximity())) {
                    beacon.setProximity(proximity);
                    logger.debug(METHOD_NAME, "Updated proximity for beacon" + beacon);
                }
                logger.end(METHOD_NAME);
                return;
            }
        }
        
        // No existing beacon found, add new one
        Beacon beacon = new Beacon(uuid, major, minor, proximity);
        beacons.add(beacon);
        logger.debug(METHOD_NAME, "beacons:" + beacons);
        providerChangeSupport.fireProviderRefresh("beacons");
        logger.end(METHOD_NAME);

    }
    
    public static boolean canSendNotification () {
        boolean canSendNotification = false;
        final String METHOD_NAME = "canSendNotification";
        logger.debug(METHOD_NAME, "Application Active? :" + isApplicationActive());
        if (!isApplicationActive()) {
            for (Beacon beacon : beacons) {
                if (beacon.isNotificationEnabled()) {
                    canSendNotification = beacon.isNotificationEnabled();
                    beacon.setNotificationEnabled(false);
                }
            }
        }
        return canSendNotification;
    }

    public static void clearBeacons() {
        final String METHOD_NAME = "clearBeacons";
        logger.begin(METHOD_NAME);
        beacons.clear();
        providerChangeSupport.fireProviderRefresh("beacons");
        logger.end(METHOD_NAME);
    }

    public void addProviderChangeListener(ProviderChangeListener l) {
        providerChangeSupport.addProviderChangeListener(l);
    }

    public void removeProviderChangeListener(ProviderChangeListener l) {
        providerChangeSupport.removeProviderChangeListener(l);
    }

    public static void setActiveUUID (String activeUuid) {
        BeaconManager.uuid = activeUuid;
        providerChangeSupport.fireProviderRefresh("uuid");
    }

    public static String getUuid() {
        return uuid;
    }
    
    public static void log(String methodName, String message) {
        logger.debug("beacon.js:" + methodName, message);
    }
    

}
