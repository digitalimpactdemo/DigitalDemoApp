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
                            $("#myCarousel").carousel("pause").removeData();
                            var content_indi = "";
                            var content_inner = "";
                            $.each(result.notifications, function (i, obj) {
                                    var styleClass = '';
                                    if (i == 0) {
                                            styleClass = 'active';
                                    }
                                    content_indi += '<li data-target="#myCarousel" data-slide-to="' + i + '" class="' + styleClass + '"></li>';
                                    content_inner += '<div class="item ' + styleClass + '">';
                                    content_inner += '	<div class="carousel-caption">';
                                    content_inner += '	  <h3>'  + obj.title  + '</h3>';
                                    content_inner += '	  <p>' + obj.date + '</p>';
                                    content_inner += '	  <a class="view_article" href="' + obj.url + '" onClick="javascript: onClickArticle ();">View full article</a>';
                                    content_inner += '	</div>';
                                    content_inner += '</div>';
                            });
                            $('#car_id').html(content_indi);
                            $('#car_inner').html(content_inner);
                            if (result.notifications.length > 0) {
                                    $('#car_inner .item').first().addClass('active');
                                    $('#car_indi > li').first().addClass('active');
                            }
                            $('#myCarousel').carousel();
                    },
                    error: function (xhr, ajaxOptions, thrownError) {
                            alert ('Error:' + thrownError);
                    },
                    async:   false
            });
    }
    function initialise() {
        adf.mf.api.invokeMethod("digital.demo.application.managers.BeaconManager", "getRequestJSON", "data", "notifications", getLoggedInUser(),
                                                        function(request, response) {
                                                            notificationsResponse = getNotificationData(response);
                                                        },
                                                        function(request, response) {
                                                        }
                                );
    }

    onClickArticle = function() {
        adf.mf.api.invokeMethod("digital.demo.application.managers.UserPreferenceManager", "incrementNoOfClicks",
                                                        function(request, response) {},
                                                        function(request, response) {}
                                );
    }

    logMessage = function(method_name, console_message) {
        adf.mf.api.invokeMethod("digital.demo.application.managers.BeaconManager", "log", method_name, console_message, function() {}, function() {});
    }

    // Call the initialise method
    document.addEventListener("showpagecomplete", initialise, true);

}) ();