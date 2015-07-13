(function() {
    
    function initialise() {
        jQuery.ajax({
                url: REST_BASE_URL + '/regions',
                type: 'GET',
                headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json'
                },
                'dataType': 'json',
                success: function(result) {
                    var region_identifier = document.getElementById("uuid");
                    $.each(result, function (i, obj) {
                        opt = document.createElement("option");
                        opt.value = obj.uuid;
                        opt.text  = obj.uuname;
                        opt.selected  = obj.active;
                        region_identifier.appendChild(opt);
                    });
                },
                 error: function (xhr, ajaxOptions, thrownError) {
                        alert ('Error:' + thrownError);
                 },
                async:   false
        });

        adf.mf.api.invokeMethod("digital.demo.application.managers.UserPreferenceManager", "getUserPreference", "beacon", 
                                                        function(request, response) {
                                                            if (response != '') {
                                                                selectPreference ('beacon', response);
                                                            }
                                                        },
                                                        function(request, response) {}
                                );

        adf.mf.api.invokeMethod("digital.demo.application.managers.UserPreferenceManager", "getUserPreference", "noofclicks", 
                                                        function(request, response) {
                                                            if (response != '') {
                                                                selectPreference ('noofclicks', response);
                                                            }
                                                        },
                                                        function(request, response) {}
                                );

        adf.mf.api.invokeMethod("digital.demo.application.managers.UserPreferenceManager", "getUserPreference", "disableengage", 
                                                        function(request, response) {
                                                            if (response != '') {
                                                                selectPreference ('disableengage', response);
                                                            }
                                                        },
                                                        function(request, response) {}
                                );
    }
    
    // Callable externally
    selectPreference = function(name, value) {
        $('select[name^="' + name + '"] option[value="' + value + '"]').attr("selected","selected");
    }
    
    savePreferences = function() {
        var uuid = $('select[name^="uuid"]').val ();
        var noofclicks = $('select[name^="noofclicks"]').val ();
        var disableengage = $('select[name^="disableengage"]').val ();
        adf.mf.api.invokeMethod("digital.demo.application.managers.UserPreferenceManager", "saveUserPreference", "beacon", uuid,
                                                        function(request, response) {},
                                                        function(request, response) {}
                                );
        adf.mf.api.invokeMethod("digital.demo.application.managers.UserPreferenceManager", "saveUserPreference", "noofclicks", noofclicks,
                                                        function(request, response) {},
                                                        function(request, response) {}
                                );
        adf.mf.api.invokeMethod("digital.demo.application.managers.UserPreferenceManager", "saveUserPreference", "disableengage", disableengage,
                                                        function(request, response) {},
                                                        function(request, response) {}
                                );
    }

    // Call the initialise method
    document.addEventListener("showpagecomplete", initialise, true);

}) ();