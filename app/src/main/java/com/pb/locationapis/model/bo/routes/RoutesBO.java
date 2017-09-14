package com.pb.locationapis.model.bo.routes;

import java.util.List;

/**
 * Created by NEX7IMH on 6/15/2017.
 */
public class RoutesBO
{
    private List<CustomerVo> customerVos;
    private GeometryJson geometryJson;
    private String isRouteModified;
    private List<BranchVo> branchVos;
    private String routeId;


    public List<CustomerVo> getCustomerVos() {
        return this.customerVos;
    }

    public void setCustomerVos(List<CustomerVo> customerVos) {
        this.customerVos = customerVos;
    }


    public String getIsRouteModified() {
        return isRouteModified;
    }

    public void setIsRouteModified(String isRouteModified) {
        this.isRouteModified = isRouteModified;
    }

    public GeometryJson getGeometryJson() {
        return geometryJson;
    }

    public void setGeometryJson(GeometryJson geometryJson) {
        this.geometryJson = geometryJson;
    }

    public List<BranchVo> getBranchVos() {
        return branchVos;
    }

    public void setBranchVos(List<BranchVo> branchVos) {
        this.branchVos = branchVos;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }
}
