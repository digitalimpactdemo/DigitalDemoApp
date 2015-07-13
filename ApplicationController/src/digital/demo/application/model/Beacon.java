package digital.demo.application.model;

import oracle.adfmf.java.beans.PropertyChangeListener;
import oracle.adfmf.java.beans.PropertyChangeSupport;

public class Beacon {

    private String uuid;
    private int major;
    private int minor;
    private String proximity;
    private boolean notificationEnabled;
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public static final String BEACON_PROXIMITY_NEAR        =   "ProximityNear";
    public static final String BEACON_PROXIMITY_FAR         =   "ProximityFar";
    public static final String BEACON_PROXIMITY_IMMEDIATE   =   "ProximityImmediate";
    
    public Beacon(String uuid, int major, int minor, String proximity) {
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
        setProximity (proximity);
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    @Override
    public String toString() {
        return "Beacon{" +
                        "uuid='" + uuid + "'," +
                        "major='"  + major + "'," +
                        "minor='"  + minor + "'," +
                        "notificationEnabled='"  + notificationEnabled + "'," +
                        "proximity='" + proximity + "'" +
                        '}';
    }

    public void setUuid(String uuid) {
        String oldUuid = this.uuid;
        this.uuid = uuid;
        propertyChangeSupport.firePropertyChange("uuid", oldUuid, uuid);
    }

    public String getUuid() {
        return uuid;
    }

    public void setMajor(int major) {
        int oldMajor = this.major;
        this.major = major;
        propertyChangeSupport.firePropertyChange("major", oldMajor, major);
    }

    public int getMajor() {
        return major;
    }

    public void setMinor(int minor) {
        int oldMinor = this.minor;
        this.minor = minor;
        propertyChangeSupport.firePropertyChange("minor", oldMinor, minor);
    }

    public int getMinor() {
        return minor;
    }

    public void setProximity(String proximity) {
        if (proximity.equalsIgnoreCase(BEACON_PROXIMITY_IMMEDIATE)) {
            setNotificationEnabled(true);
        }
        /*
        else if (proximity.equalsIgnoreCase(BEACON_PROXIMITY_NEAR)) {
            setNotificationEnabled(true);
        }
        */
        else {
            setNotificationEnabled(false);
        }
        String oldProximity = this.proximity;
        this.proximity = proximity;
        propertyChangeSupport.firePropertyChange("proximity", oldProximity, proximity);
        
        
    }

    public String getProximity() {
        return proximity;
    }

    public String getIdentifier() {
        return "Major: " + getMajor() + " Minor: " + getMinor();
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(l);
    }
}
