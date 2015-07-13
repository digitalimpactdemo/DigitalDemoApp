(function() {

	// Region Identifier definition
	var uuid;
	var uuname;
	var beaconRegion;
	function initialise() {
			var METHOD_NAME = 'initialise';
			logMessage (METHOD_NAME, 'Entered');
            var delegate 	= 	new cordova.plugins.locationManager.Delegate();
            // Callback for enter/exit region whilst monitoring
            delegate.didDetermineStateForRegion = function (pluginResult) {
                var notificationMessage;
                var fireNotification = false;
                var inside = false;
                if (pluginResult.state == "CLRegionStateInside") {
					logMessage (METHOD_NAME, 'Start ranging immediate');
                    // Start ranging immediately
                    startRanging ();

                } else if (pluginResult.state == "CLRegionStateOutside") {
					logMessage (METHOD_NAME, 'Stop ranging immediate');
                    // Stop ranging immediately
                    stopRanging();

                }
            };

            // Callback for ranging region
            // IMPORTANT: You cannot call Javascript methods here.
            delegate.didRangeBeaconsInRegion = function(pluginResult) {
				logMessage (METHOD_NAME, 'Entered Beacons Ranging');
                var beacons = pluginResult.beacons;
                for (var i = 0; i < beacons.length; i++) {
                    // Add to list of beacons in BeaconManager
                    adf.mf.api.invokeMethod("digital.demo.application.managers.BeaconManager",
                                                "beaconRanged",
                                                beacons[i].uuid, beacons[i].major, beacons[i].minor, beacons[i].proximity,
                                                function() {},
                                                function() {});
                }
                logMessage (METHOD_NAME, '# of Beacons:' + beacons.length);
				if (beacons.length > 0) {
					// Check with BeaconManager if we can send notification
					adf.mf.api.invokeMethod("digital.demo.application.managers.BeaconManager",
												"canSendNotification",
												function(request, response) {
													logMessage (METHOD_NAME, 'canSendNotification:' + response);
													if (response == 'true') {
														logMessage (METHOD_NAME, 'Fire local notification');
														// Fire local notification
														var options = {"alert": "Welcome to Digital Impact demo.", "sound": "SYSTEM_DEFAULT", "vibration": "SYSTEM_DEFAULT", "payload":{"id": uuid, "inside":true, "username": getLoggedInUser()}};
														adf.mf.api.localnotification.add(options,  function() {}, function() {});
														logMessage (METHOD_NAME, 'Local notification sent');
													}
												},
												function() {});
				}
				logMessage (METHOD_NAME, 'Leaving Beacons Ranging');
            };

            // Set delegate
            cordova.plugins.locationManager.setDelegate(delegate);

            // Required in iOS 8+
            cordova.plugins.locationManager.requestAlwaysAuthorization();

            // Start monitoring by default each time the app is launched
            startMonitoring();
            logMessage (METHOD_NAME, 'Leaving');
	}

	function initialiseRegion() {
		var METHOD_NAME = 'initialiseRegion';
		logMessage (METHOD_NAME, 'Entered');
            jQuery.ajax({
                 url:    REST_BASE_URL + '/regions',
                 success: function(regionsResponse) {
                    for (i = 0; i < regionsResponse.length; i++) {
						logMessage (METHOD_NAME, 'Is Active?' + regionsResponse [i].active);
                        if (regionsResponse [i].active) {
                            uuid   = regionsResponse [i].uuid;
                            uuname = regionsResponse [i].uuname;
                            break;
                        }
                    }
                    logMessage (METHOD_NAME, '-- Start System Setting --');
                    logMessage (METHOD_NAME, 'uuid:' + uuid);
                    logMessage (METHOD_NAME, 'uuname:' + uuname);
                    logMessage (METHOD_NAME, '-- Stop  System Setting --');
                 },
                 error: function (xhr, ajaxOptions, thrownError) {
                    alert ('Cannot load Region Information:' + thrownError);
                 },
                 async:   false
            });
            adf.mf.api.invokeMethod("digital.demo.application.managers.UserPreferenceManager",
                                    "getUserPreference", "beacon",
                                    function(request, response) {
										logMessage (METHOD_NAME, 'response:' + response);
                                        if (response != '') {
                                            uuname = 'User Beacon';
                                            uuid = response;
                                        }
										logMessage (METHOD_NAME, '-- Start User Preference --');
										logMessage (METHOD_NAME, 'uuid:' + uuid);
										logMessage (METHOD_NAME, 'uuname:' + uuname);
										logMessage (METHOD_NAME, '-- Stop  User Preference --');
                                        // Monitor any beacons in this UUID-based region
                                        adf.mf.api.invokeMethod("digital.demo.application.managers.BeaconManager", "setActiveUUID", uuid, function() {}, function() {});
                                        beaconRegion = new cordova.plugins.locationManager.BeaconRegion(uuname, uuid);
                                        initialise ();
                                    },
                                    function(request, response) {
                                        // Monitor any beacons in this UUID-based region
                                        adf.mf.api.invokeMethod("digital.demo.application.managers.BeaconManager", "setActiveUUID", uuid, function() {}, function() {});
                                        beaconRegion = new cordova.plugins.locationManager.BeaconRegion(uuname, uuid);
                                        initialise ();
                                    }
            );
            logMessage (METHOD_NAME, 'Leaving');
	}

	// Callable externally
	startMonitoring = function() {
            cordova.plugins.locationManager.startMonitoringForRegion(beaconRegion).fail(console.error).done();
            adf.mf.api.setValue({"name": "#{applicationScope.region}", "value": beaconRegion.uuid}, function() {}, function() {});
            adf.mf.api.setValue({"name": "#{applicationScope.monitoring}", "value": true}, function() {}, function() {});
	}

	// Callable externally
	stopMonitoring = function() {
            stopRanging(); // Must stop ranging first
            cordova.plugins.locationManager.stopMonitoringForRegion(beaconRegion).fail(console.error).done();
            adf.mf.api.setValue({"name": "#{applicationScope.monitoring}", "value": false}, function() {}, function() {});
	}

	// Callable externally
	startRanging = function() {
            cordova.plugins.locationManager.startRangingBeaconsInRegion(beaconRegion).fail(console.error).done();
            adf.mf.api.setValue({"name": "#{applicationScope.ranging}", "value": true}, function() {}, function() {});
	}

	// Callable externally
	stopRanging = function() {
            cordova.plugins.locationManager.stopRangingBeaconsInRegion(beaconRegion).fail(console.error).done();
            adf.mf.api.setValue({"name": "#{applicationScope.ranging}", "value": false}, function() {}, function() {});
            // Clear list of beacons
            adf.mf.api.invokeMethod("digital.demo.application.managers.BeaconManager", "clearBeacons", function() {}, function() {});
	}

	// Callable externally
	logMessage = function(method_name, console_message) {
            adf.mf.api.invokeMethod("digital.demo.application.managers.BeaconManager", "log", method_name, console_message, function() {}, function() {});
	}

    // Call the initialise method
    document.addEventListener("showpagecomplete", initialiseRegion, false);

}) ();