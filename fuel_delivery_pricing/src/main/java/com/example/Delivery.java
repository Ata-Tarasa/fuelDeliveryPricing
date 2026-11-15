package com.example;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    private BigDecimal NetFuelPrice;
    private BigDecimal LogisticsCost;
    private BigDecimal Excise;
    private BigDecimal VAT;
    private BigDecimal TotalTax;
    private BigDecimal GrandTotal;

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
    public errorCodes getErrorCode(){
        return this.errorCode;
    }
    public String getCalculationValues(){
        return this.id + "," + this.NetFuelPrice + "," + this.LogisticsCost + "," + this.TotalTax + "," + this.GrandTotal;
    }
    public void calculate(){
        BigDecimal discountRate = getDiscountRate();
        this.NetFuelPrice = this.volume
            .multiply(this.basePrice)
            .multiply(BigDecimal.ONE.subtract(discountRate))
            .setScale(2, RoundingMode.HALF_UP);
        
        BigDecimal loadFee = getLoadFee();
        this.LogisticsCost = this.distance
            .multiply(new BigDecimal("1.80"))
            .add(loadFee)
            .setScale(2, RoundingMode.HALF_UP);

        BigDecimal excisePerLiter = getExcisePerLiter();
        this.Excise = this.volume
            .multiply(excisePerLiter)
            .setScale(2, RoundingMode.HALF_UP);

        BigDecimal vatRate = getVatRate();
        this.VAT = this.NetFuelPrice
            .add(this.LogisticsCost)
            .add(this.Excise)
            .multiply(vatRate)
            .setScale(2, RoundingMode.HALF_UP);

        this.TotalTax = this.Excise
            .add(this.VAT)
            .setScale(2, RoundingMode.HALF_UP);

        this.GrandTotal = this.NetFuelPrice
            .add(this.LogisticsCost)
            .add(this.TotalTax)
            .setScale(2, RoundingMode.HALF_UP);
        
    }
    
    private BigDecimal getVatRate() {
        BigDecimal temp_rate = BigDecimal.ZERO;
        switch (this.client) {
            case "EXEMPT":
                temp_rate = new BigDecimal("0.00");
                break;
            case "PRIVATE":
                LocalDate localDate = parseDate(this.date);
                if (localDate.isBefore(LocalDate.parse("2024-01-01"))) {
                    temp_rate = new BigDecimal("0.21");
                } else {
                    temp_rate = new BigDecimal("0.23");
                }
                break;
            default:
                break;
        }
        return temp_rate;
    }
    private LocalDate parseDate(String date) {
        if (date.contains("/")) {
            return LocalDate.parse(date, DateTimeFormatter.ofPattern("dd/MM/uuuu"));
        } else {
            return LocalDate.parse(date);
        }
    }
    private BigDecimal getExcisePerLiter() {
        BigDecimal excPerL = BigDecimal.ZERO;
        switch (this.type) {
            case "GASOLINE":
                excPerL = new BigDecimal("0.47");
                break;
            case "DIESEL":
                excPerL =  new BigDecimal("0.33");
                break;
            case "LPG":
                excPerL = new BigDecimal("0.18");
                break;
            default:
                break;
        }
        return excPerL;
    }
    private BigDecimal getLoadFee() {
        if (this.volume.compareTo(BigDecimal.valueOf(1000)) < 0) {
            return new BigDecimal("150.00");
        } else {
            return BigDecimal.ZERO;
        }
    }
    private BigDecimal getDiscountRate() {
        BigDecimal temp_rate = BigDecimal.ZERO;
        if (this.volume.compareTo(BigDecimal.valueOf(20000)) > 0) {
            temp_rate = new BigDecimal("0.10");
        } else if (this.volume.compareTo(BigDecimal.valueOf(5000)) > 0) {
            temp_rate = new BigDecimal("0.05");
        }
        return temp_rate;
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
