package digital.demo.application.logging;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class AppLogger {

    private Class clazz = null;
    
    private boolean enabled = false;

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public Class getClazz() {
        return clazz;
    }

    private static Map<String, AppLogger> logMap = new HashMap <String, AppLogger> ();

    public static AppLogger getLogger (Class clazz) {

        AppLogger logger = null;
        if (logMap.containsKey (clazz.getName())) {
            logger = logMap.get(clazz.getName());
        }
        else {
            logger = new AppLogger ();
            logger.setClazz (clazz);
            if (clazz.getSimpleName().equals("BeaconManager") || clazz.getSimpleName().equals("DeviceManager")) {
                logger.setEnabled(true);
            }
            logMap.put(clazz.getName(), logger);
        }
        return logger;
    }

    public void info (String methodName, String consoleMessage) {
        log(Level.INFO, methodName, consoleMessage);
    }

    public void debug (String methodName, String consoleMessage) {
        log(Level.FINEST, methodName, consoleMessage);
    }

    public void begin (String methodName) {
        log(Level.INFO, methodName, "Entered");
    }

    public void end  (String methodName) {
        log(Level.INFO, methodName, "Leaving");
    }

    public void error (String methodName, String consoleMessage, Exception exp) {
        log(Level.SEVERE, methodName, consoleMessage);
        exp.printStackTrace(System.out);
    }

    public void error (String methodName, String consoleMessage) {
        log(Level.SEVERE, methodName, consoleMessage);
    }

    private void log (Level level, String methodName, String consoleMessage) {
        //Trace.log(Utility.ApplicationLogger, level, clazz, methodName, consoleMessage);
        if (isEnabled()) {
            System.out.println  (
                                "<" + level.toString() + "> " +
                                "<" + new Date () + "> " +
                                "<" + clazz.getSimpleName() + "> " +
                                "<" + methodName + "> " +
                                "<" + consoleMessage + "> "
                            );
        }
    }
}
