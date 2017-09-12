package com.pb.locationapis.model.bo.routes;

import java.io.Serializable;

/**
 * Created by NEX7IMH on 6/20/2017.
 */

public class CustomerVo implements Serializable{

    private String customerId;
    private String isCustomerActive;
    private String name;
    private String phone;
    private String status;
    private String latitude;
    private String longitude;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private CheckInCheckOutVo checkInCheckOutVo;
    private CustomConstraintVO customConstraintVO;

    public CheckInCheckOutVo getCheckInCheckOutVo() {
        return checkInCheckOutVo;
    }

    public void setCheckInCheckOutVo(CheckInCheckOutVo checkInCheckOutVo) {
        this.checkInCheckOutVo = checkInCheckOutVo;
    }

    public CustomConstraintVO getCustomConstraintVO() {
        return customConstraintVO;
    }

    public void setCustomConstraintVO(CustomConstraintVO customConstraintVO) {
        this.customConstraintVO = customConstraintVO;
    }

    public CustomerVo() {
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getIsCustomerActive() {
        return isCustomerActive;
    }

    public void setIsCustomerActive(String isCustomerActive) {
        this.isCustomerActive = isCustomerActive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

}
