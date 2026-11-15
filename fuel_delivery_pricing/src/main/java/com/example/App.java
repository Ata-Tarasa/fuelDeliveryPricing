package com.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
                    volume = null;
                }
                try {
                    distance = jsonObject.getBigDecimal("distance");
                } catch (Exception e) {
                    distance = null;
                }
                try {
                    basePrice = jsonObject.getBigDecimal("basePrice");
                } catch (Exception e) {
                    basePrice = null;
                }
                deliveries.add(new Delivery(id, type, client, date, volume, distance, basePrice));
            };
        }

        ArrayList<Delivery> invalidDeliveries = new ArrayList<>();
        for(Delivery delivery: deliveries){
            if (delivery.isValid(deliveries, deliveries.indexOf(delivery)) == true) {
                
            } else {
                invalidDeliveries.add(delivery);
            }
        }

        System.out.println("Found " + invalidDeliveries.size() + " invalid deliveries. Writing to output/errors.csv");
        try {
            Files.createDirectories(Paths.get("output"));
        } catch (IOException e) {
            System.out.println("Failed to create output directory.");
            return;
        }
        File errorFile = new File("output/errors.csv");
        try {
            errorFile.createNewFile();
        } catch (IOException e) {
            System.out.println("Failed to create output/errors.csv.");
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(errorFile))) {
            writer.write("Karolis_Markevičius");
            writer.newLine();
            writer.write("ShipmentID,ErrorCode");
            writer.newLine();
            for (Delivery delivery : invalidDeliveries) {
                if (delivery.getID() == null) {
                    writer.write("," + delivery.getErrorCode());
                    writer.newLine();
                } else {
                    writer.write(delivery.getID() + "," + delivery.getErrorCode());
                    writer.newLine();
                }
            
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}

