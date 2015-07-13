(function() {
    var contexts;
    var user_profile;
    function initialise() {
        jQuery.ajax({
            url: REST_BASE_URL + '/contexts',
            type: 'GET',
            headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
            },
            'dataType': 'json',
            success: function(result) {
                    contexts = result;
                var context = document.getElementById("context");
                $.each(result, function (i, obj) {
                    opt = document.createElement("option");
                    opt.value = obj.name;
                    opt.text  = obj.name;
                    context.appendChild(opt);
                });
            },
             error: function (xhr, ajaxOptions, thrownError) {
                    alert ('Error:' + thrownError);
             },
            async:   false
        });

        jQuery.ajax({
            url: REST_BASE_URL + '/user/' + getLoggedInUser (),
            type: 'GET',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            'dataType': 'json',
            success: function(result) {
                user_profile = result;
                $('#fullname').text(user_profile.firstName + ' ' + user_profile.lastName);
                $('#email').text(user_profile.email);
                if (user_profile.picture != '') {
                    $("#profile_pic").attr("src", user_profile.picture);
                }
                $('select[name^="context"] option[value="' + user_profile.context.name + '"]').attr("selected","selected");
                var found = false;
                var interests_checkbox_html = '';
                $.each(contexts, function (i, obj) {
                    if (obj.name == user_profile.context.name) {
                        found = true;
                        $.each(obj.interests, function (j, interest) {
                            interests_checkbox_html += '     <input type="checkbox" id="interests" name="interests" value="' + interest + '"/> ' + interest + '<br>';
                        });
                    }
                });
                if (found) {
                    $('#interestsDiv').html(interests_checkbox_html);
                }
                $.each(user_profile.context.interests, function (i, interest) {
                    $('input[name^="interests"]').each( function() {
                        if ($(this).val() == interest) {
                            $(this).prop("checked",true);
                        }
                    });
                    $('input[name^="interests"] [value="' + interest + '"]').prop("checked",true);
                });
            },
             error: function (xhr, ajaxOptions, thrownError) {
                alert ('Error:' + thrownError);
             },
            async:   false
        });
    }

    // Callable externally
    onChangeContext = function() {
        var selectedContext = $( "#context" ).val();
        var found = false;
        var interests_html = '';
        var interests_checkbox_html = '';
        $.each(contexts, function (i, obj) {
            if (obj.name == selectedContext) {
                found = true;
                $.each(obj.interests, function (j, interest) {
                    interests_checkbox_html += '     <input type="checkbox" id="interests_' + j + '" name="interests_' + j + '" value="' + interest + '"/> ' + interest + '<br>';
                });
            }
        });
        if (found) {
            $('#interestsDiv').html(interests_checkbox_html);
        }
        $.each(user_profile.context.interests, function (i, interest) {
            $('input[name^="interests"]').each( function() {
                if ($(this).val() == interest) {
                        $(this).prop("checked",true);
                }
            });
            $('input[name^="interests"] [value="' + interest + '"]').prop("checked",true);
        });
    }

    // Callable externally
    onSubmit = function() {
        var interests = [];
        $('input[name^="interests"]').each( function() {
            if ($(this).prop("checked")) {
                interests.push ($(this).val());
            }
        });
        var returnOnError = false;
        if (interests.length < 1) {
            $("#interestsDiv").closest(".form-group").find(".help-block").html('<ul role="alert"><li>Please select your interests.</li></ul>');
            returnOnError = true;
        }
        else {
            $("#interestsDiv").closest(".form-group").find(".help-block").html('');
        }

        if ($('#context :selected').val() == '') {
            $("#interestsDiv").closest(".form-group").find(".help-block").html('<ul role="alert"><li>Please select your context.</li></ul>');
            returnOnError = true;
        }
        else {
            $("#context").closest(".form-group").find(".help-block").html('');
        }

        var pinError = false;
        var pinErrorMsg = '';
        var pin = '';
        $.each([ 1, 2, 3, 4 ], function( index, value ) {
            var pinId = '#pin' + value;
            if ($(pinId).val() == '') {
                pinError = true;
                pinErrorMsg += '<li>Please enter pin[' + value + '].</li>';
            }
            else {
                pin += $(pinId).val();
            }
        });
        if (pinError) {
            $('#pin1').closest(".form-group").find(".help-block").html('<ul role="alert">' + pinErrorMsg + '</ul>');
            returnOnError = true;
        }
        else {
            $('#pin1').closest(".form-group").find(".help-block").html('');
        }
        if (returnOnError) {
            return;
        }

        $('.help-block').each( function() {
            $(this).html ('');
        });
        var context = {
                        "name": $('#context :selected').val(),
                        "interests": interests
                      }
        // Create user
        var user = 	{
                            "username": getLoggedInUser(),
                            "pin": pin,
                            "context": context,
                            "picture": $("#profile_pic").attr("src")
                        };
        jQuery.ajax({
            url: REST_BASE_URL + '/user/' + getLoggedInUser(),
            type: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            data: JSON.stringify (user),
            'dataType': 'json',
            success: function(result) {
            },
             error: function (xhr, ajaxOptions, thrownError) {
                alert ('Error:' + thrownError);
             },
            async:   false
        });
    }
    
    // Callable externally
    editProfilePicture = function() {
        var METHOD_NAME = 'editProfilePicture';
        logMessage (METHOD_NAME, 'Entered');
        // invoke Camera Feature
        adf.mf.api.invokeMethod("digital.demo.application.managers.DeviceManager",
                                "invokeCamera",
                                function(request, response) {
                                    logMessage (METHOD_NAME, 'response:' + response);
                                    $("#profile_pic").attr("src", response);
                                },
                                function() {});
        logMessage (METHOD_NAME, 'Leaving');
    }

    // Callable externally
    logMessage = function(method_name, console_message) {
        adf.mf.api.invokeMethod("digital.demo.application.managers.DeviceManager", "log", method_name, console_message, function() {}, function() {});
    }
    
    // Call the initialise method
    document.addEventListener("showpagecomplete", initialise, true);

}) ();