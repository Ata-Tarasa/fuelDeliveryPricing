package com.example;

import java.math.BigDecimal;
import java.time.LocalDate;

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
    public String toString(){
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
