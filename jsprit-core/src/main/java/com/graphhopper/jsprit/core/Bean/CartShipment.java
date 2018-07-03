package com.graphhopper.jsprit.core.Bean;

import com.graphhopper.jsprit.core.util.Coordinate;

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
    private String deliverySlot;
    private String deliveryStartTimeString;
    private String deliveryendTimeString;
    private Long createdTime;
    private Coordinate coordinate;
    public Long getCreatedTime() {
        return createdTime;
    }


    public String getDeliveryType() {
        return deliveryType;
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

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public CartShipment(String userId, String orderId, String cartShipmentId, Long deliveryStartTime, Long deliveryEndTime, Double latitude, Double longitude, Long createdTime, String deliveryType, String deliverySlot, String deliveryStartTimeString, String deliveryendTimeString) {
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.cartShipmentId = cartShipmentId;
        this.deliveryStartTime = deliveryStartTime;
        this.deliveryEndTime = deliveryEndTime;
        this.orderId = orderId;
        this.createdTime = createdTime;
        this.deliveryType = deliveryType;
        this.deliverySlot = deliverySlot;
        this.deliveryStartTimeString = deliveryStartTimeString;
        this.deliveryendTimeString = deliveryendTimeString;

        shipmentIds = new ArrayList<>();
        coordinate = new Coordinate(longitude,latitude);

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
