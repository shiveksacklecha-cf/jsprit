package com.graphhopper.jsprit.core.Bean;

import java.util.Date;

public class UserBean {
    String userId;
    Double latitude;
    Double longitude;
    private String shipmentId;
    private String cartShipmentId;
    Long deliveryStartTime;
    Long deliveryEndTime;
    String orderId;

    public Long getDeliveryStartTime() {
        return deliveryStartTime;
    }

    public Long getDeliveryEndTime() {
        return deliveryEndTime;
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public String getCartShipmentId() {
        return cartShipmentId;
    }

    public UserBean(String userId, String orderId, String shipmentId, String cartShipmentId, Long deliveryStartTime, Long deliveryEndTime, Double latitude, Double longitude) {
        this.userId = userId;
        this.orderId = orderId;
        this.shipmentId = shipmentId;
        this.cartShipmentId = cartShipmentId;
        this.deliveryStartTime = deliveryStartTime;

        this.deliveryEndTime = deliveryEndTime;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getUserId() {
        return userId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getOrderId() {
        return orderId;
    }

}
