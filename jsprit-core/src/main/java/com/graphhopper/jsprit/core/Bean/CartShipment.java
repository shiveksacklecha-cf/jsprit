package com.graphhopper.jsprit.core.Bean;

import java.util.ArrayList;

public class CartShipment {

    String userId;
    Double latitude;
    Double longitude;
    private ArrayList<String> shipmentIds;
    private String cartShipmentId;
    Long deliveryStartTime;
    Long deliveryEndTime;
    String orderId;
    String deliveryType;
    private Long createdTime;

    public Long getCreatedTime() {
        return createdTime;
    }


    public String getDeliveryType() {
        return deliveryType;
    }

    public CartShipment(String userId, String orderId, String cartShipmentId, Long deliveryStartTime, Long deliveryEndTime, Double latitude, Double longitude, Long createdTime, String deliveryType) {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.cartShipmentId = cartShipmentId;
        this.deliveryStartTime = deliveryStartTime;
        this.deliveryEndTime = deliveryEndTime;
        this.orderId = orderId;
        this.createdTime = createdTime;
        this.deliveryType = deliveryType;

        shipmentIds = new ArrayList<>();
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

    public ArrayList<String> getShipmentIds() {
        return shipmentIds;
    }

    public String getCartShipmentId() {
        return cartShipmentId;
    }

    public Long getDeliveryStartTime() {
        return deliveryStartTime;
    }

    public Long getDeliveryEndTime() {
        return deliveryEndTime;
    }

    public String getOrderId() {
        return orderId;
    }

    public void addShipment(String shipmentId)
    {

        shipmentIds.add(shipmentId);
    }
    public Integer getNumberOfShipments()
    {
        return shipmentIds.size();

    }
}
