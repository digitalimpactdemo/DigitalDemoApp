(function() {
	function getNotificationData (notificationRequest) {
		jQuery.ajax({
			url: REST_BASE_URL + '/notifications',
			type: 'POST',
			headers: {
				'Accept': 'application/json',
				'Content-Type': 'application/json'
			},
			data: notificationRequest,
			'dataType': 'json',
			success: function(result) {
				var content_title = "";
				var content_choices = "";
				content_choices = result.engage.title;
				$.each(result.engage.choices, function (i, obj) {
					content_choices += '<div class="form-group"><div class="col-sm-10"><div class="checkbox"><label><input type="checkbox"> ' + obj + '</label></div></div></div>'
				});
				$('#choices_form').html(content_choices);
				$('#content_title').html(content_title);
			},
			error: function (xhr, ajaxOptions, thrownError) {
				alert ('Error:' + thrownError);
			},
			async:   false
		});
	}
	function initialise() {
            adf.mf.api.invokeMethod("digital.demo.application.managers.BeaconManager", "getRequestJSON", "data", "engage", getLoggedInUser(),
                                                            function(request, response) {
                                                               	notificationsResponse = getNotificationData(response);
                                                            },
                                                            function(request, response) {
                                                            }
                                    );
	}

    // Call the initialise method
    document.addEventListener("showpagecomplete", initialise, true);

}) ();