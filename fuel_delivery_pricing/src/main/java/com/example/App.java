package com.example;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject; 
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        ArrayList<Delivery> deliveries = new ArrayList<>();
        List<String> rows = new ArrayList<>();
        
        try {
            Files.lines(Paths.get("shipments.json")).forEach(row -> rows.add(row));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        for (String row : rows) {
            row = row.replace("[", "").replace("]", "");
            if (!row.equals("")) {
                
                JSONObject jsonObject = new JSONObject(row);
                String id;
                String type;
                String client;
                String date;
                BigDecimal volume;
                BigDecimal distance;
                BigDecimal basePrice;
                try {
                    id = jsonObject.getString("id");
                } catch (Exception e) {
                    id = "";
                }
                try {
                    type = jsonObject.getString("type");
                } catch (Exception e) {
                    type = "";
                }
                try {
                    client = jsonObject.getString("client");
                } catch (Exception e) {
                    client = "";
                }
                try {
                    date = jsonObject.getString("date"); 
                } catch (Exception e) {
                    date = "";
                }
                try {
                    volume = jsonObject.getBigDecimal("volume");
                } catch (Exception e) {
                    volume = BigDecimal.valueOf(-1);
                }
                try {
                    distance = jsonObject.getBigDecimal("distance");
                } catch (Exception e) {
                    distance = BigDecimal.valueOf(-1);
                }
                try {
                    basePrice = jsonObject.getBigDecimal("basePrice");
                } catch (Exception e) {
                    basePrice = BigDecimal.valueOf(-1);
                }
                deliveries.add(new Delivery(id, type, client, date, volume, distance, basePrice));
            };
        }
        for(Delivery delivery: deliveries){
            System.out.println("Delivery: ");
            System.out.println(delivery);
        }
    }
}

