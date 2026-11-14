package com.example;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

public class Delivery {
    private String id;
    private String type;
    private String client;
    private String date;
    private BigDecimal volume;
    private BigDecimal distance;
    private BigDecimal basePrice;
    private errorCodes errorCode;

    public Delivery(String id, String type, String client, String date, BigDecimal volume, BigDecimal distance, BigDecimal basePrice){
        this.id = id;
        this.type = type;
        this.client = client;
        this.date = date;
        this.volume = volume;
        this.distance = distance;
        this.basePrice = basePrice;
        this.errorCode = errorCodes.NONE;
    }
    public String getID(){
        return this.id;
    }
    public boolean isValid(ArrayList<Delivery> deliveries, int deliveryIndex){
        if (hasMissingField() == true) {
            this.errorCode = errorCodes.MISSING_FIELD;
            return false;
        }
        if (typeIsUnknown() == true){
            this.errorCode = errorCodes.UNKNOWN_TYPE;
            return false;
        }
        if (clientIsUnknown() == true) {
            this.errorCode = errorCodes.UNKNOWN_CLIENT;
            return false;
        }
        if (dateIsNotValid() == true) {
            this.errorCode = errorCodes.INVALID_DATE;
            return false;
        }
        if (valueIsNotValid() == true) {
            this.errorCode = errorCodes.INVALID_VALUE;
            return false;
        }
        if (isDuplicateID(deliveries, deliveryIndex) == true) {
            this.errorCode = errorCodes.DUPLICATE_ID;
            return false;
        }
        return true;
    }
    private boolean isDuplicateID(ArrayList<Delivery> deliveries, int deliveryIndex) {
        for (Delivery delivery : deliveries) {
            if (delivery.getID().equals(this.id) && deliveries.indexOf(delivery) != deliveryIndex && deliveries.indexOf(delivery) < deliveryIndex) {
                return true;
            }
        }
        return false;
    }
    private boolean valueIsNotValid() {
        if (this.volume == BigDecimal.valueOf(0) || this.volume.compareTo(BigDecimal.valueOf(0)) < 0) {
            return true;
        }
        if (this.distance.compareTo(BigDecimal.valueOf(0)) < 0) {
            return true;
        }
        if (this.basePrice.compareTo(BigDecimal.valueOf(0)) < 0) {
            return true;
        }
        return false;
    }
    private boolean dateIsNotValid() {
        
        String regexDateFormat = "^([0-2][0-9]||3[0-1])/(0[0-9]||1[0-2])/([0-9][0-9])?[0-9][0-9]$";

        try {
            if (this.date.matches(regexDateFormat)) {
                return false;
            }
            LocalDate.parse(this.date);
            return false;
        } catch (Exception e) {
            return true;
        }
    }
    private boolean clientIsUnknown() {
        if (this.client.equalsIgnoreCase("PRIVATE") || this.client.equalsIgnoreCase("EXEMPT")) {
            return false;
        }
        return true;
    }
    private boolean typeIsUnknown() {
        if (this.type.equalsIgnoreCase("GASOLINE") || this.type.equalsIgnoreCase("DIESEL") || this.type.equalsIgnoreCase("LPG")) {
            return false;
        }
        return true;
    }
    private boolean hasMissingField(){
        if (this.id == "" || this.type == "" || this.client == "" || this.date == "" || this.volume == null || this.distance == null || this.basePrice == null) {
            return true;
        }
        return false;
    }
    public String toString(){
        if (this.errorCode != errorCodes.NONE) {
            return "ID: " 
            + this.id + "\n" 
            + "Type: " + this.type + "\n" 
            + "Client: " + this.client + "\n"
            + "Date: " + this.date + "\n"
            + "Volume: " + this.volume + "\n"
            + "Distance: " + this.distance + "\n"
            + "BasePrice: " + this.basePrice + "\n"
            + "Error Code: " + this.errorCode + "\n";
        } else {
            return "ID: " 
            + this.id + "\n" 
            + "Type: " + this.type + "\n" 
            + "Client: " + this.client + "\n"
            + "Date: " + this.date + "\n"
            + "Volume: " + this.volume + "\n"
            + "Distance: " + this.distance + "\n"
            + "BasePrice: " + this.basePrice + "\n";
        }
        
    }
    
}
