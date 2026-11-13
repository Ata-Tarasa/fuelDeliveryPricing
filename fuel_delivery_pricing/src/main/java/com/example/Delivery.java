package com.example;

import java.math.BigDecimal;
import java.util.Date;

public class Delivery {
    private String id;
    private String type;
    private String client;
    private Date date;
    private BigDecimal volume;
    private BigDecimal distance;
    private BigDecimal basePrice;
    private errorCodes errorCode;

    public Delivery(String id, String type, String client, Date date, BigDecimal volume, BigDecimal distance, BigDecimal basePrice){
        this.id = id;
        this.type = type;
        this.client = client;
        this.date = date;
        this.volume = volume;
        this.basePrice = basePrice;
        this.errorCode = errorCodes.NONE;
    }

    
}
