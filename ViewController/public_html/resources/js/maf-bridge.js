if (!window.adf) {
    window.adf = {};
}
adf.wwwPath = "../../../../www/";

var REST_BASE_URL = "http://sites.digitalimpactdemo.com:9111/apis/services/rest";
var SECURITY_BASE_URL = "http://sites.digitalimpactdemo.com:9111/apis/services/security";
var USER_NAME;

function goToFeature(featureid, reset) {
    if (reset) {
        adf.mf.api.resetFeature(featureid, function (req, res) {}, function (req, res) {});
    }
    adf.mf.api.gotoFeature (featureid, function (req, res) {}, function (req, res) {});
}

moveOnMax =function (field, nextFieldID) {
    if (field.value.length == 1) {
        document.getElementById(nextFieldID).focus();
    }
}


getLoggedInUser = function () {
    jQuery.ajax({
        url: SECURITY_BASE_URL + '/authorise',
        type: 'GET',
        headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
        },
        'dataType': 'json',
        success: function(result) {
            USER_NAME = result.username;
        },
         error: function (xhr, ajaxOptions, thrownError) {
            alert ('Error:' + thrownError);
         },
        async:   false
    });
    return USER_NAME;
}

