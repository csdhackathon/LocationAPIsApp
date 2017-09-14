package com.pb.locationapis.asynctask;

import android.os.AsyncTask;

import com.pb.locationapis.listener.IParserEventListener;
import com.pb.locationapis.model.bo.routes.BranchLocation;
import com.pb.locationapis.model.bo.routes.BranchVo;
import com.pb.locationapis.model.bo.routes.CheckInCheckOutVo;
import com.pb.locationapis.model.bo.routes.Coordinates;
import com.pb.locationapis.model.bo.routes.CustomConstraintVO;
import com.pb.locationapis.model.bo.routes.CustomerVo;
import com.pb.locationapis.model.bo.routes.Geometry;
import com.pb.locationapis.model.bo.routes.GeometryJson;
import com.pb.locationapis.model.bo.routes.RoutesBO;
import com.pb.locationapis.model.bo.routes.StartEndTime;
import com.pb.locationapis.parser.ParserType;
import com.pb.locationapis.utility.ConstantUnits;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by NEX7IMH on 20-June-17.
 */
public class ParseResponseAsyncTask extends AsyncTask<Void, Void, ParserType>
{
    private IParserEventListener iParserEventListener;
    private ParserType mParserType;
    private JSONObject mResponseJsonObject;
    private ConstantUnits mConstantUnits = ConstantUnits.getInstance();
    private JSONObject mJsonOutputObject = null;
    private RoutesBO mRoutesBO;

    public ParseResponseAsyncTask(JSONObject mJsonObject, ParserType parserType, IParserEventListener eventListener)
    {
        this.mParserType = parserType;
        this.mResponseJsonObject = mJsonObject;
        this.iParserEventListener = eventListener;
        mConstantUnits = ConstantUnits.getInstance();
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
    }

    @Override
    protected ParserType doInBackground(Void... params) {
        try {
            switch (mParserType) {


                case PARSER_GET_ROUTE_FOR_AN_AGENT:

                    mRoutesBO = parseRouteForAnAgentJsonResponse(mResponseJsonObject, mParserType);
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mParserType;
    }

    @Override
    protected void onPostExecute(ParserType parserType) {
        try {
            super.onPostExecute(parserType);
            if(iParserEventListener != null && parserType != null) {
                iParserEventListener.onParseCompleted(parserType, mRoutesBO);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private RoutesBO parseRouteForAnAgentJsonResponse(JSONObject mResponseJsonObject, ParserType mParserType) {

        RoutesBO mRoutesBO = new RoutesBO();
        try {
            if(mResponseJsonObject != null) {

                if(mResponseJsonObject.has(mConstantUnits.customerVO)) {
                    if(mResponseJsonObject.get(mConstantUnits.customerVO) instanceof JSONArray) {
                        parseJsonArrayCustomerVo(mResponseJsonObject.getJSONArray(mConstantUnits.customerVO), mRoutesBO);
                    }
                }

                if(mResponseJsonObject.has(mConstantUnits.geometryJson)) {
                    if(mResponseJsonObject.get(mConstantUnits.geometryJson) instanceof JSONObject) {
                        parseJsonGeometryJson(mResponseJsonObject.getJSONObject(mConstantUnits.geometryJson), mRoutesBO);
                    }
                }

                if(mResponseJsonObject.has(mConstantUnits.branchVOList)) {
                    if(mResponseJsonObject.get(mConstantUnits.branchVOList) instanceof JSONArray) {
                        parseJsonArrayBranchVo(mResponseJsonObject.getJSONArray(mConstantUnits.branchVOList), mRoutesBO);
                    }
                }

                if(mResponseJsonObject.has(mConstantUnits.isRouteModified)) {
                    mRoutesBO.setIsRouteModified(mResponseJsonObject.getString(mConstantUnits.isRouteModified));
                }
                if(mResponseJsonObject.has(mConstantUnits.routeId)) {
                    mRoutesBO.setRouteId(mResponseJsonObject.getString(mConstantUnits.routeId));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mRoutesBO;
    }

    private void parseJsonArrayBranchVo(JSONArray mJsonArrayBranchVo, RoutesBO mRoutesBO) {
        try {
            if(mJsonArrayBranchVo != null && mJsonArrayBranchVo.length() > 0) {
                List<BranchVo> mBranchVoList = new ArrayList<>();
                for (int i = 0; i < mJsonArrayBranchVo.length(); i++) {
                    BranchVo mBranchVo = new BranchVo();
                    if(mJsonArrayBranchVo.get(i) instanceof JSONObject) {
                        JSONObject mJsonBranchVo = mJsonArrayBranchVo.getJSONObject(i);

                        if(mJsonBranchVo.has(mConstantUnits.branchLocation)) {
                            if(mJsonBranchVo.get(mConstantUnits.branchLocation) instanceof JSONObject) {
                                parseJsonBranchLocation(mBranchVo, mJsonBranchVo.getJSONObject(mConstantUnits.branchLocation));
                            }
                        }

                        if(mJsonBranchVo.has(mConstantUnits.branchAddressLine1)) {
                            mBranchVo.setBranchAddressLine1(mJsonBranchVo.getString(mConstantUnits.branchAddressLine1));
                        }
                        if(mJsonBranchVo.has(mConstantUnits.branchAddressLine2)) {
                            mBranchVo.setBranchAddressLine2(mJsonBranchVo.getString(mConstantUnits.branchAddressLine2));
                        }
                        if(mJsonBranchVo.has(mConstantUnits.branchCity)) {
                            mBranchVo.setBranchCity(mJsonBranchVo.getString(mConstantUnits.branchCity));
                        }
                        if(mJsonBranchVo.has(mConstantUnits.branchCountry)) {
                            mBranchVo.setBranchCountry(mJsonBranchVo.getString(mConstantUnits.branchCountry));
                        }
                        if(mJsonBranchVo.has(mConstantUnits.branchId)) {
                            mBranchVo.setBranchId(mJsonBranchVo.getString(mConstantUnits.branchId));
                        }
                        if(mJsonBranchVo.has(mConstantUnits.branchName)) {
                            mBranchVo.setBranchName(mJsonBranchVo.getString(mConstantUnits.branchName));
                        }
                        if(mJsonBranchVo.has(mConstantUnits.branchPhonenumber)) {
                            mBranchVo.setBranchPhonenumber(mJsonBranchVo.getString(mConstantUnits.branchPhonenumber));
                        }
                        if(mJsonBranchVo.has(mConstantUnits.branchPostalCode)) {
                            mBranchVo.setBranchPostalCode(mJsonBranchVo.getString(mConstantUnits.branchPostalCode));
                        }
                        if(mJsonBranchVo.has(mConstantUnits.branchUserID)) {
                            mBranchVo.setBranchUserID(mJsonBranchVo.getString(mConstantUnits.branchUserID));
                        }
                    }

                    mBranchVoList.add(mBranchVo);
                }

                mRoutesBO.setBranchVos(mBranchVoList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseJsonBranchLocation(BranchVo mBranchVo, JSONObject mJsonBranchLocation) {
        try {
            if(mBranchVo != null && mJsonBranchLocation != null) {
                BranchLocation mBranchLocation = new BranchLocation();
                if (mJsonBranchLocation.has(mConstantUnits.type)) {
                    mBranchLocation.setType(mJsonBranchLocation.getString(mConstantUnits.type));
                }
                if(mJsonBranchLocation.has(mConstantUnits.coordinates)) {
                    if(mJsonBranchLocation.get(mConstantUnits.coordinates) instanceof JSONArray) {
                        JSONArray mArrayCoordinates = mJsonBranchLocation.getJSONArray(mConstantUnits.coordinates);
                        List<Coordinates> mCoordinatesList = new ArrayList<>();
                        if(mArrayCoordinates != null && mArrayCoordinates.length() > 0) {
                            for (int i = 0; i < mArrayCoordinates.length(); i++) {
                                if(mArrayCoordinates.get(i) instanceof JSONArray) {
                                    Coordinates mCoordinates = new Coordinates();
                                    mCoordinates.setLatitude(((JSONArray) mArrayCoordinates.get(i)).getString(0));
                                    mCoordinates.setLongitude(((JSONArray) mArrayCoordinates.get(i)).getString(1));

                                    mCoordinatesList.add(mCoordinates);
                                }
                            }
                        }

                        mBranchLocation.setCoordinates(mCoordinatesList);
                    }
                }

                mBranchVo.setBranchLocation(mBranchLocation);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseJsonGeometryJson(JSONObject mJsonGeometryJson, RoutesBO mRoutesBO) {
        try {
            if(mJsonGeometryJson != null) {
                GeometryJson mGeometryJson = new GeometryJson();

                if(mJsonGeometryJson.has(mConstantUnits.distance)) {
                    mGeometryJson.setDistance(mJsonGeometryJson.getString(mConstantUnits.distance));
                }
                if(mJsonGeometryJson.has(mConstantUnits.distanceUnit)) {
                    mGeometryJson.setDistanceUnit(mJsonGeometryJson.getString(mConstantUnits.distanceUnit));
                }
                if(mJsonGeometryJson.has(mConstantUnits.time)) {
                    mGeometryJson.setTime(mJsonGeometryJson.getString(mConstantUnits.time));
                }
                if(mJsonGeometryJson.has(mConstantUnits.timeUnit)) {
                    mGeometryJson.setTimeUnit(mJsonGeometryJson.getString(mConstantUnits.timeUnit));
                }

                if(mJsonGeometryJson.has(mConstantUnits.geometry)) {
                    if(mJsonGeometryJson.get(mConstantUnits.geometry) instanceof JSONObject) {
                        parseGeometryFromGeometryJson(mGeometryJson, mJsonGeometryJson.getJSONObject(mConstantUnits.geometry));
                    }
                }

                if(mJsonGeometryJson.has(mConstantUnits.intermediatePoints)) {
                    if(mJsonGeometryJson.get(mConstantUnits.intermediatePoints) instanceof JSONArray) {
                        parseIntermediatePointsFromGeometryJson(mGeometryJson, mJsonGeometryJson.getJSONArray(mConstantUnits.intermediatePoints));
                    }
                }

                mRoutesBO.setGeometryJson(mGeometryJson);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseIntermediatePointsFromGeometryJson(GeometryJson mGeometryJson, JSONArray mArrayIntermediatePoints) {
        try {
            if(mGeometryJson != null && mArrayIntermediatePoints != null) {
                List<Coordinates> mIntermediatePointsList = new ArrayList<>();
                for (int i = 0; i < mArrayIntermediatePoints.length(); i++) {
                    Coordinates mIntermediatePoints = new Coordinates();
                    String[] mArr = mArrayIntermediatePoints.getString(i).split(",");
                    mIntermediatePoints.setLongitude(mArr[0]);
                    mIntermediatePoints.setLatitude(mArr[1]);

                    mIntermediatePointsList.add(mIntermediatePoints);
                }
                mGeometryJson.setIntermediatePointses(mIntermediatePointsList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Coordinates> getIntermediatePointsFromRoutes(JSONObject jsonObject) {
        List<Coordinates> intermediatePoints = new ArrayList<>();
        try {
            if(jsonObject != null && jsonObject.has(mConstantUnits.intermediatePoints)) {
                if(jsonObject.get(mConstantUnits.intermediatePoints) instanceof JSONArray) {
                    JSONArray mJsonArrayIntermediatePoints = jsonObject.getJSONArray(mConstantUnits.intermediatePoints);
                    if(mJsonArrayIntermediatePoints != null && mJsonArrayIntermediatePoints.length() > 0) {
                        for (int i = 0; i < mJsonArrayIntermediatePoints.length(); i++) {
                            Coordinates mCoordinates = new Coordinates();
                            String[] arrCoordinates = mJsonArrayIntermediatePoints.getString(i).split(",");
                            mCoordinates.setLatitude(arrCoordinates[1]);
                            mCoordinates.setLongitude(arrCoordinates[0]);

                            intermediatePoints.add(mCoordinates);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return intermediatePoints;
    }

    private void parseGeometryFromGeometryJson(GeometryJson mGeometryJson, JSONObject mJsonGeometry) {
        try {
            if(mGeometryJson != null && mJsonGeometry != null) {
                Geometry mGeometry = new Geometry();
                if(mJsonGeometry.has(mConstantUnits.type)) {
                    mGeometry.setType(mJsonGeometry.getString(mConstantUnits.type));
                }
                if(mJsonGeometry.has(mConstantUnits.coordinates)) {
                    List<Coordinates> mCoordinatesList = new ArrayList<>();
                    if(mJsonGeometry.get(mConstantUnits.coordinates) instanceof JSONArray) {
                        JSONArray mJsonArrayCoordinates = mJsonGeometry.getJSONArray(mConstantUnits.coordinates);
                        for (int i = 0; i < mJsonArrayCoordinates.length(); i++) {
                            if(mJsonArrayCoordinates.get(i) instanceof JSONArray) {
                                JSONArray mArrayCoordinate = mJsonArrayCoordinates.getJSONArray(i);
                                Coordinates mCoordinates = new Coordinates();
                                mCoordinates.setLongitude(mArrayCoordinate.getString(0));
                                mCoordinates.setLatitude(mArrayCoordinate.getString(1));

                                mCoordinatesList.add(mCoordinates);
                            }
                        }
                    }

                    mGeometry.setCoordinates(mCoordinatesList);
                }

                mGeometryJson.setGeometry(mGeometry);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseJsonArrayCustomerVo(JSONArray mJsonArrayCustomerVoList, RoutesBO mRoutesBO) {
        try {
            if(mJsonArrayCustomerVoList != null && mJsonArrayCustomerVoList.length() > 0) {
                List<CustomerVo> mCustomerVosList = new ArrayList<>();
                for ( int i=0; i < mJsonArrayCustomerVoList.length(); i++) {
                    CustomerVo mCustomerVo = new CustomerVo();
                    if(mJsonArrayCustomerVoList.get(i) instanceof JSONObject) {
                        if(mJsonArrayCustomerVoList.getJSONObject(i).has(mConstantUnits.customerId)) {
                            mCustomerVo.setCustomerId(mJsonArrayCustomerVoList.getJSONObject(i).getString(mConstantUnits.customerId));
                        }
                        if(mJsonArrayCustomerVoList.getJSONObject(i).has(mConstantUnits.isCustomerActive)) {
                            mCustomerVo.setIsCustomerActive(mJsonArrayCustomerVoList.getJSONObject(i).getString(mConstantUnits.isCustomerActive));
                        }
                        if(mJsonArrayCustomerVoList.getJSONObject(i).has(mConstantUnits.name)) {
                            mCustomerVo.setName(mJsonArrayCustomerVoList.getJSONObject(i).getString(mConstantUnits.name));
                        }
                        if(mJsonArrayCustomerVoList.getJSONObject(i).has(mConstantUnits.phone)) {
                            mCustomerVo.setPhone(mJsonArrayCustomerVoList.getJSONObject(i).getString(mConstantUnits.phone));
                        }
                        if(mJsonArrayCustomerVoList.getJSONObject(i).has(mConstantUnits.status)) {
                            mCustomerVo.setStatus(mJsonArrayCustomerVoList.getJSONObject(i).getString(mConstantUnits.status));
                        }
                        if(mJsonArrayCustomerVoList.getJSONObject(i).has(mConstantUnits.latitude)) {
                            mCustomerVo.setLatitude(mJsonArrayCustomerVoList.getJSONObject(i).getString(mConstantUnits.latitude));
                        }
                        if(mJsonArrayCustomerVoList.getJSONObject(i).has(mConstantUnits.longitude)) {
                            mCustomerVo.setLongitude(mJsonArrayCustomerVoList.getJSONObject(i).getString(mConstantUnits.longitude));
                        }
                        if(mJsonArrayCustomerVoList.getJSONObject(i).has(mConstantUnits.addressLine1)) {
                            mCustomerVo.setAddressLine1(mJsonArrayCustomerVoList.getJSONObject(i).getString(mConstantUnits.addressLine1));
                        }
                        if(mJsonArrayCustomerVoList.getJSONObject(i).has(mConstantUnits.addressLine2)) {
                            mCustomerVo.setAddressLine2(mJsonArrayCustomerVoList.getJSONObject(i).getString(mConstantUnits.addressLine2));
                        }
                        if(mJsonArrayCustomerVoList.getJSONObject(i).has(mConstantUnits.city)) {
                            mCustomerVo.setCity(mJsonArrayCustomerVoList.getJSONObject(i).getString(mConstantUnits.city));
                        }
                        if(mJsonArrayCustomerVoList.getJSONObject(i).has(mConstantUnits.state)) {
                            mCustomerVo.setState(mJsonArrayCustomerVoList.getJSONObject(i).getString(mConstantUnits.state));
                        }
                        if(mJsonArrayCustomerVoList.getJSONObject(i).has(mConstantUnits.country)) {
                            mCustomerVo.setCountry(mJsonArrayCustomerVoList.getJSONObject(i).getString(mConstantUnits.country));
                        }
                        if(mJsonArrayCustomerVoList.getJSONObject(i).has(mConstantUnits.postalCode)) {
                            mCustomerVo.setPostalCode(mJsonArrayCustomerVoList.getJSONObject(i).getString(mConstantUnits.postalCode));
                        }

                        if(mJsonArrayCustomerVoList.getJSONObject(i).has(mConstantUnits.checkInCheckOutVO)) {
                            if(mJsonArrayCustomerVoList.getJSONObject(i).getJSONObject(mConstantUnits.checkInCheckOutVO) instanceof JSONObject) {
                                parseJsonCheckInCheckOutVo(mCustomerVo, mJsonArrayCustomerVoList.getJSONObject(i).getJSONObject(mConstantUnits.checkInCheckOutVO));
                            }
                        }

                        if(mJsonArrayCustomerVoList.getJSONObject(i).has(mConstantUnits.customConstraintVO)) {
                            if(mJsonArrayCustomerVoList.getJSONObject(i).getJSONObject(mConstantUnits.customConstraintVO) instanceof JSONObject) {
                                parseJsonCustomConstraintVO(mCustomerVo, mJsonArrayCustomerVoList.getJSONObject(i).getJSONObject(mConstantUnits.customConstraintVO));
                            }
                        }
                    }

                    mCustomerVosList.add(mCustomerVo);
                }

                mRoutesBO.setCustomerVos(mCustomerVosList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseJsonCheckInCheckOutVo(CustomerVo mCustomerVo, JSONObject mJsonCheckInCheckOutVo) {
        try {
            if(mCustomerVo != null && mJsonCheckInCheckOutVo != null) {
                CheckInCheckOutVo mCheckInCheckOutVo = new CheckInCheckOutVo();
                if(mJsonCheckInCheckOutVo.has(mConstantUnits.customerId)) {
                    mCheckInCheckOutVo.setCustomerId(mJsonCheckInCheckOutVo.getString(mConstantUnits.customerId));
                }
                if(mJsonCheckInCheckOutVo.has(mConstantUnits.expectedStartTime)) {
                    mCheckInCheckOutVo.setExpectedStartTime(mJsonCheckInCheckOutVo.getString(mConstantUnits.expectedStartTime));
                }
                if(mJsonCheckInCheckOutVo.has(mConstantUnits.expectedFinishTime)) {
                    mCheckInCheckOutVo.setExpectedFinishTime(mJsonCheckInCheckOutVo.getString(mConstantUnits.expectedFinishTime));
                }
                if(mJsonCheckInCheckOutVo.has(mConstantUnits.costs)) {
                    mCheckInCheckOutVo.setCosts(mJsonCheckInCheckOutVo.getString(mConstantUnits.costs));
                }
                if(mJsonCheckInCheckOutVo.has(mConstantUnits.distanceCost)) {
                    mCheckInCheckOutVo.setDistanceCost(mJsonCheckInCheckOutVo.getString(mConstantUnits.distanceCost));
                }
                if(mJsonCheckInCheckOutVo.has(mConstantUnits.timeCost)) {
                    mCheckInCheckOutVo.setTimeCost(mJsonCheckInCheckOutVo.getString(mConstantUnits.timeCost));
                }
                if(mJsonCheckInCheckOutVo.has(mConstantUnits.serviceCost)) {
                    mCheckInCheckOutVo.setServiceCost(mJsonCheckInCheckOutVo.getString(mConstantUnits.serviceCost));
                }
                if(mJsonCheckInCheckOutVo.has(mConstantUnits.cicoId)) {
                    mCheckInCheckOutVo.setCicoId(mJsonCheckInCheckOutVo.getString(mConstantUnits.cicoId));
                }
                if(mJsonCheckInCheckOutVo.has(mConstantUnits.isCheckinDone)) {
                    mCheckInCheckOutVo.setIsCheckinDone(mJsonCheckInCheckOutVo.getString(mConstantUnits.isCheckinDone));
                }
                if(mJsonCheckInCheckOutVo.has(mConstantUnits.isCheckoutDone)) {
                    mCheckInCheckOutVo.setIsCheckoutDone(mJsonCheckInCheckOutVo.getString(mConstantUnits.isCheckoutDone));
                }
                if(mJsonCheckInCheckOutVo.has(mConstantUnits.routeId)) {
                    mCheckInCheckOutVo.setRouteId(mJsonCheckInCheckOutVo.getString(mConstantUnits.routeId));
                }
                if(mJsonCheckInCheckOutVo.has(mConstantUnits.agentArriveTime)) {
                    mCheckInCheckOutVo.setAgentArriveTime(mJsonCheckInCheckOutVo.getString(mConstantUnits.agentArriveTime));
                }
                if(mJsonCheckInCheckOutVo.has(mConstantUnits.agentFinishTime)) {
                    mCheckInCheckOutVo.setAgentFinishTime(mJsonCheckInCheckOutVo.getString(mConstantUnits.agentFinishTime));
                }
                if(mJsonCheckInCheckOutVo.has(mConstantUnits.routeOrder)) {
                    mCheckInCheckOutVo.setRouteOrder(mJsonCheckInCheckOutVo.getString(mConstantUnits.routeOrder));
                }

                if(mJsonCheckInCheckOutVo.has(mConstantUnits.checkinLat)) {
                    mCheckInCheckOutVo.setCheckinLat(mJsonCheckInCheckOutVo.getString(mConstantUnits.checkinLat));
                }
                if(mJsonCheckInCheckOutVo.has(mConstantUnits.checkinLong)) {
                    mCheckInCheckOutVo.setCheckinLong(mJsonCheckInCheckOutVo.getString(mConstantUnits.checkinLong));
                }
                if(mJsonCheckInCheckOutVo.has(mConstantUnits.checkinTime)) {
                    mCheckInCheckOutVo.setCheckinTime(mJsonCheckInCheckOutVo.getString(mConstantUnits.checkinTime));
                }
                if(mJsonCheckInCheckOutVo.has(mConstantUnits.checkoutLat)) {
                    mCheckInCheckOutVo.setCheckoutLat(mJsonCheckInCheckOutVo.getString(mConstantUnits.checkoutLat));
                }
                if(mJsonCheckInCheckOutVo.has(mConstantUnits.checkoutLong)) {
                    mCheckInCheckOutVo.setCheckoutLong(mJsonCheckInCheckOutVo.getString(mConstantUnits.checkoutLong));
                }
                if(mJsonCheckInCheckOutVo.has(mConstantUnits.checkoutTime)) {
                    mCheckInCheckOutVo.setCheckoutTime(mJsonCheckInCheckOutVo.getString(mConstantUnits.checkoutTime));
                }
                if(mJsonCheckInCheckOutVo.has(mConstantUnits.userComment)) {
                    mCheckInCheckOutVo.setUserComment(mJsonCheckInCheckOutVo.getString(mConstantUnits.userComment));
                }

                mCustomerVo.setCheckInCheckOutVo(mCheckInCheckOutVo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseJsonCustomConstraintVO(CustomerVo mCustomerVo, JSONObject mJsonCustomConstraintVO) {
        try {
            if(mCustomerVo != null && mJsonCustomConstraintVO != null) {
                CustomConstraintVO mCustomConstraintVO = new CustomConstraintVO();
                if(mJsonCustomConstraintVO.has(mConstantUnits.capacityDemand)) {
                    mCustomConstraintVO.setCapacityDemand(mJsonCustomConstraintVO.getString(mConstantUnits.capacityDemand));
                }
                if(mJsonCustomConstraintVO.has(mConstantUnits.distance)) {
                    mCustomConstraintVO.setDistance(mJsonCustomConstraintVO.getString(mConstantUnits.distance));
                }
                if(mJsonCustomConstraintVO.has(mConstantUnits.endTime)) {
                    mCustomConstraintVO.setEndTime(mJsonCustomConstraintVO.getString(mConstantUnits.endTime));
                }
                if(mJsonCustomConstraintVO.has(mConstantUnits.fixed)) {
                    mCustomConstraintVO.setFixed(mJsonCustomConstraintVO.getString(mConstantUnits.fixed));
                }
                if(mJsonCustomConstraintVO.has(mConstantUnits.isCostsInvolve)) {
                    mCustomConstraintVO.setIsCostsInvolve(mJsonCustomConstraintVO.getString(mConstantUnits.isCostsInvolve));
                }
                if(mJsonCustomConstraintVO.has(mConstantUnits.service)) {
                    mCustomConstraintVO.setService(mJsonCustomConstraintVO.getString(mConstantUnits.service));
                }
                if(mJsonCustomConstraintVO.has(mConstantUnits.startTime)) {
                    mCustomConstraintVO.setStartTime(mJsonCustomConstraintVO.getString(mConstantUnits.startTime));
                }
                if(mJsonCustomConstraintVO.has(mConstantUnits.time)) {
                    mCustomConstraintVO.setTime(mJsonCustomConstraintVO.getString(mConstantUnits.time));
                }
                if(mJsonCustomConstraintVO.has(mConstantUnits.type)) {
                    mCustomConstraintVO.setType(mJsonCustomConstraintVO.getString(mConstantUnits.type));
                }
                if(mJsonCustomConstraintVO.has(mConstantUnits.wait)) {
                    mCustomConstraintVO.setWait(mJsonCustomConstraintVO.getString(mConstantUnits.wait));
                }
                if(mJsonCustomConstraintVO.has(mConstantUnits.startNEndTime)) {
                    if(mJsonCustomConstraintVO.get(mConstantUnits.startNEndTime) instanceof JSONArray) {
                        JSONArray mJsonArrayStartEndTime = mJsonCustomConstraintVO.getJSONArray(mConstantUnits.startNEndTime);
                        StartEndTime mStartEndTime = new StartEndTime();
                        mStartEndTime.setStartTime(mJsonArrayStartEndTime.getString(0));
                        mStartEndTime.setEndTime(mJsonArrayStartEndTime.getString(1));

                        mCustomConstraintVO.setStartEndTime(mStartEndTime);
                    }
                }

                mCustomerVo.setCustomConstraintVO(mCustomConstraintVO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
