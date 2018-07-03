package com.graphhopper.jsprit.core.Bean;

import java.util.Date;

public class UserBean {
    String userId;
    Double latitude;
    private Long createdTime;
    Double longitude;
    private String shipmentId;
    private String cartShipmentId;
    Long deliveryStartTime;
    Long deliveryEndTime;
    String orderId;
    private String deliveryType;
    private String deliverySlot;
    private String deliveryStartTimeString;
    private String deliveryendTimeString;

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

    public Long getCreatedTime() {
        return createdTime;
    }


    public String getDeliverySlot() {
        return deliverySlot;
    }

    public String getDeliveryStartTimeString() {
        return deliveryStartTimeString;
    }

    public String getDeliveryendTimeString() {
        return deliveryendTimeString;
    }

    public UserBean(String userId, String orderId, String shipmentId, String cartShipmentId, Long deliveryStartTime, Long deliveryEndTime, Double longitude, Double latitude, Long createdTime, String deliveryType, String deliverySlot, String deliveryStartTimeString, String deliveryendTimeString) {
        this.userId = userId;
        this.orderId = orderId;
        this.shipmentId = shipmentId;
        this.cartShipmentId = cartShipmentId;
        this.deliveryStartTime = deliveryStartTime;

        this.deliveryEndTime = deliveryEndTime;
        this.longitude = longitude;
        this.latitude = latitude;
        this.createdTime = createdTime;
        this.deliveryType = deliveryType;
        this.deliverySlot = deliverySlot;
        this.deliveryStartTimeString = deliveryStartTimeString;
        this.deliveryendTimeString = deliveryendTimeString;
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
    public String getDeliveryType() {
        return deliveryType;
    }


}
