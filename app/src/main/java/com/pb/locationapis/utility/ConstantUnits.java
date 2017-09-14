package com.pb.locationapis.utility;

/**
 * Created by NEX7IMH on 14-Jun-17.
 */
public class ConstantUnits
{
    private static ConstantUnits _instance;

    /**
     * Constructor is defined as PRIVATE, as following the Singleton Design Pattern
     */
    private ConstantUnits() {}

    /**
     * To get the instance object of the class
     * @return _instance
     */
    public synchronized static ConstantUnits getInstance()
    {
        try {
            if (_instance == null)
            {
                _instance = new ConstantUnits();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return _instance;
    }

    public static String ROOT_DIRECTORY = "/FFO/images/";

    public final String ARG_PARAM1 = "param1";
    public final String ARG_PARAM2 = "param2";

    public final String EMPTY = "";
    public final String CONTENT_TYPE = "Content-Type";
    public final String ACCEPT = "Accept";
    public final String ENCODING_UTF_8 = "UTF-8";
    public final String RESPONSE_CODE = "RESPONSE_CODE";
    public final String RESPONSE_MESSAGE = "RESPONSE_MESSAGE";
    public final String NO_RESPONSE = "NO RESPONSE";
    public final String STATUS_TWO_HUNDRED = "200";
    public final String STATUS_TWO_ZERO_TWO = "202";
    public final String STATUS_TWO = "002";
    public final String STATUS_SIX_HUNDRED = "600";
    public final String MESSAGE = "message";
    public final String TRUE = "true";
    public final String ONE = "1";
    public final String ZERO = "0";
    public final String PBGEOMAP_URL = "PBGEOMAP_URL";
    public final String PBGEOMAP_ACCESS_TOKEN = "API_KEY";
    public final String PBGEO_GEOMAP_THEME = "PBGEO_GEOMAP_THEME";
    public final String PBGEOMAP_SECRET_KEY = "SECRET";
    public final String PREMIUM = "premium";

    public final String IS_KEY_SUBMITTED = "is_key_submitted";

    public final String admin = "admin";

    public final String APPLICATION_JSON_TYPE = "application/json";

    public final String Status = "Status";
    public final String Output = "Output";
    public final String ErrorMessage = "ErrorMessage";
    public final String userid = "userid";
    public final String user_type = "user_type";

    public final String username = "username";
    public final String userpwd = "userpwd";
    public final String email = "email";
    public final String newUserPassword = "newUserPassword";
    public final String result = "result";
    public final String agentId = "agentId";
    public final String error = "error";
    public final String First_Name = "First_Name";
    public final String managerId = "managerId";
    public final String creationDate = "creationDate";
    public final String customerIds = "customerIds";
    public final String checkinLatitude = "checkinLatitude";
    public final String checkinLongitude = "checkinLongitude";
    public final String userCommentsIfany = "userCommentsIfany";
    public final String checkoutLatitude = "checkoutLatitude";
    public final String checkoutLongitude = "checkoutLongitude";
    public final String rescheduleDate = "rescheduleDate";

    public final String customerVO = "customerVO";
    public final String geometryJson = "geometryJson";
    public final String isRouteModified = "isRouteModified";
    public final String branchLocation = "branchLocation";
    public final String customerId = "customerId";
    public final String isCustomerActive = "isCustomerActive";
    public final String name = "name";
    public final String phone = "phone";
    public final String status = "status";
    public final String latitude = "latitude";
    public final String longitude = "longitude";
    public final String addressLine1 = "addressLine1";
    public final String addressLine2 = "addressLine2";
    public final String city = "city";
    public final String state = "state";
    public final String country = "country";
    public final String postalCode = "postalCode";
    public final String checkInCheckOutVO = "checkInCheckOutVO";
    public final String customConstraintVO = "customConstraintVO";
    public final String expectedStartTime = "expectedStartTime";
    public final String expectedFinishTime = "expectedFinishTime";
    public final String costs = "costs";
    public final String distanceCost = "distanceCost";
    public final String timeCost = "timeCost";
    public final String serviceCost = "serviceCost";
    public final String cicoId = "cicoId";
    public final String isCheckinDone = "isCheckinDone";
    public final String isCheckoutDone = "isCheckoutDone";
    public final String routeId = "routeId";
    public final String agentArriveTime = "agentArriveTime";
    public final String agentFinishTime = "agentFinishTime";
    public final String checkinLat = "checkinLat";
    public final String checkinLong = "checkinLong";
    public final String checkinTime = "checkinTime";
    public final String checkoutLat = "checkoutLat";
    public final String checkoutLong = "checkoutLong";
    public final String checkoutTime = "checkoutTime";
    public final String userComment = "userComment";
    public final String routeOrder = "routeOrder";
    public final String capacityDemand = "capacityDemand";
    public final String distance = "distance";
    public final String endTime = "endTime";
    public final String fixed = "fixed";
    public final String isCostsInvolve = "isCostsInvolve";
    public final String service = "service";
    public final String startTime = "startTime";
    public final String time = "time";
    public final String type = "type";
    public final String wait = "wait";
    public final String startNEndTime = "startNEndTime";
    public final String distanceUnit = "distanceUnit";
    public final String timeUnit = "timeUnit";
    public final String geometry = "geometry";
    public final String coordinates = "coordinates";
    public final String intermediatePoints = "IntermediatePoints";
    public final String branchVOList = "branchVOList";
    public final String branchAddressLine1 = "branchAddressLine1";
    public final String branchAddressLine2 = "branchAddressLine2";
    public final String branchCity = "branchCity";
    public final String branchCountry = "branchCountry";
    public final String branchId = "branchId";
    public final String branchName = "branchName";
    public final String branchPhonenumber = "branchPhonenumber";
    public final String branchPostalCode = "branchPostalCode";
    public final String branchUserID = "branchUserID";
    public final String successMessage = "successMessage";
    public final String errorMessage = "errorMessage";


    public final String activity = "activity";
    public final String id = "id";

}
