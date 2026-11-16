package com.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

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
        
        getDeliveries(deliveries);

        ArrayList<Delivery> invalidDeliveries = new ArrayList<>();
        ArrayList<Delivery> validDeliveries = new ArrayList<>();

        for(Delivery delivery: deliveries){
            if (delivery.isValid(deliveries, deliveries.indexOf(delivery)) == true) {
                delivery.calculate();
                validDeliveries.add(delivery);
            } else {
                invalidDeliveries.add(delivery);
            }
        }

        try {
            Files.createDirectories(Paths.get("output"));
        } catch (IOException e) {
            System.out.println("Failed to create output directory.");
            return;
        }

        printToFile("output/errors.csv", "ShipmentId,ErrorCode", invalidDeliveries);
        printToFile("output/task1.csv", "ShipmentID,NetFuelPrice,LogisticsCost,TotalTax,GrandTotal", validDeliveries);
        
        BigDecimal customVATRate;
        BigDecimal customExciseRateGasoline;
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter custom VAT rate for Private clients (%): ");
            try {
                customVATRate = new BigDecimal(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid value format, try again.");
                continue;
            }
            if (customVATRate.compareTo(BigDecimal.valueOf(0)) < 0) {
                System.out.println("Invalid value, try again");
                continue;   
            }
            customVATRate = customVATRate
                .divide(BigDecimal.valueOf(100));
            break;
        }
        while (true) {
            System.out.println("Enter custom Excise rate for Gasoline: ");
            try {
                customExciseRateGasoline = new BigDecimal(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid value format, try again.");
                continue;
            }
            
            if (customExciseRateGasoline.compareTo(BigDecimal.valueOf(0)) < 0) {
                System.out.println("Invalid value, try again");
                continue;   
            }
            break;
        }
        scanner.close();
        for (Delivery delivery : validDeliveries) {
            delivery.calculate(customVATRate, customExciseRateGasoline);
        }
        printToFile("output/task2.csv", "ShipmentID,NetFuelPrice,LogisticsCost,TotalTax,GrandTotal", validDeliveries);
        
    }
    public static void printToFile(String fileName, String headerText, ArrayList<Delivery> deliveries){
        File writeFileName = new File(fileName);
        try {
            writeFileName.createNewFile();
        } catch (IOException e) {
            System.out.println("Failed to create " + fileName);
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(writeFileName))) {
            writer.write("Karolis_Markevičius");
            writer.newLine();
            writer.write(headerText);
            writer.newLine();
            for (Delivery delivery : deliveries) {
                if (delivery.getErrorCode() == errorCodes.NONE) {
                    writer.write(delivery.getCalculationValues());
                    writer.newLine();
                } else {
                    if (delivery.getID() == null) {
                        writer.write("," + delivery.getErrorCode());
                        writer.newLine();
                    } else {
                        writer.write(delivery.getID() + "," + delivery.getErrorCode());
                        writer.newLine();
                    }
                }
            
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    public static void getDeliveries(ArrayList<Delivery> deliveries){
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
    }
}

