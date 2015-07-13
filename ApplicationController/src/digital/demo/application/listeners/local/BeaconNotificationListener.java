package digital.demo.application.listeners.local;

import digital.demo.application.logging.AppLogger;
import digital.demo.application.managers.BeaconManager;
import digital.demo.application.managers.UserPreferenceManager;

import java.util.HashMap;

import oracle.adfmf.dc.ws.rest.RestServiceAdapter;
import oracle.adfmf.framework.api.AdfmfContainerUtilities;
import oracle.adfmf.framework.api.Model;
import oracle.adfmf.framework.event.Event;
import oracle.adfmf.framework.event.EventListener;
import oracle.adfmf.framework.event.NativeLocalNotificationEvent;
import oracle.adfmf.framework.exception.AdfException;
import oracle.adfmf.json.JSONException;
import oracle.adfmf.json.JSONObject;

public class BeaconNotificationListener implements EventListener {

    private AppLogger logger = AppLogger.getLogger(this.getClass());
    
    private final String FEATURES_PREFIX = "digital.demo.";
    
    public void onMessage(Event event) {
        final String METHOD_NAME = "onMessage";
        String payload  =   event.getPayload();
        logger.begin(METHOD_NAME);

        if (!"{}".equals(payload)) {
            try {
                HashMap<String, Object> payloadMap = ((NativeLocalNotificationEvent) event).getPayloadObject();
                logger.debug(METHOD_NAME, "payloadMap:" + payloadMap);
                JSONObject jsonPayload = (JSONObject) payloadMap.get("payload");
                logger.debug(METHOD_NAME, "jsonPayload:" + jsonPayload);
                boolean inside = jsonPayload.getBoolean("inside");
                logger.debug(METHOD_NAME, "inside:" + inside);
                String username = jsonPayload.getString("username");
                logger.debug(METHOD_NAME, "username:" + username);

                // If inside, start ranging (If outside, ranging already has been stopped)
                if (inside) {
                    
                    String disableEngage = UserPreferenceManager.getUserPreference("disableengage");
                    logger.debug(METHOD_NAME, "disableEngage: " + disableEngage);
                    if ("No".equalsIgnoreCase (disableEngage)) {
                        
                        int userNoOfClicks = UserPreferenceManager.getNoOfClicks();
                        int systemNoOfClicks = UserPreferenceManager.getSystemNoOfClicks();
                        logger.debug(METHOD_NAME, "userNoOfClicks: " + userNoOfClicks);
                        logger.debug(METHOD_NAME, "systemNoOfClicks: " + systemNoOfClicks);
                        
                        if (userNoOfClicks >= systemNoOfClicks) {
                            logger.debug(METHOD_NAME, "Go to Feature by Id: " + FEATURES_PREFIX + "engage");
                            UserPreferenceManager.resetNoOfClicks();
                            // Check if the user has engaged with us more than a number of times
                            AdfmfContainerUtilities.gotoFeature(FEATURES_PREFIX + "engage");
                            return;
                        }
                    }
                    
                    RestServiceAdapter restServiceAdapter = Model.createRestServiceAdapter();
                    restServiceAdapter.clearRequestProperties();
                    restServiceAdapter.setConnectionName("apisConn");
                    restServiceAdapter.addRequestProperty("Accept", "application/json; charset=UTF-8");
                    restServiceAdapter.addRequestProperty("Content-Type", "application/json");
                    restServiceAdapter.setRetryLimit(0);
                    restServiceAdapter.setRequestType("POST");
                    restServiceAdapter.setRequestURI("/services/rest/notifications");
                    
                    String payloadJSON = BeaconManager.getRequestJSON("decision", "notifications", username);
                    //variable holding the response
                    String response = "";

                    try {
                        logger.debug(METHOD_NAME, "payloadJSON: " + payloadJSON);
                        response = (String) restServiceAdapter.send(payloadJSON);
                        JSONObject jsonObject = new JSONObject (response);
                        logger.debug(METHOD_NAME, "REST call response: " + response);
                        String featureId = jsonObject.getString("target");
                        logger.debug(METHOD_NAME, "REST response 'target': " + featureId);
                        
                        logger.debug(METHOD_NAME, "Go to Feature by Id: " + FEATURES_PREFIX + featureId);
                        AdfmfContainerUtilities.gotoFeature(FEATURES_PREFIX + featureId);
                        
                    } catch (Exception e) {
                        //log error
                        logger.error(METHOD_NAME, "Error calling REST service.", e);
                    }
                }

            } catch (JSONException e) {
                logger.error(METHOD_NAME, "Local Notification JSON error: " + e.getMessage());
            }
        }

        logger.end(METHOD_NAME);
    }

    public void onError(AdfException adfException) {
        logger.debug("onError", "Local Notification error: " + adfException.getMessage());
    }

    public void onOpen(String token) {
        logger.debug("onOpen", "Local Notification opened: " + token);
    }
}
