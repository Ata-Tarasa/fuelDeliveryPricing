package com.example;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
        Path base;
        try {
            base = Paths.get(App.class.getProtectionDomain()
                .getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Could not determine application location", e);
        }

        // If running from a jar, base is the jar file, so use its parent folder
        if (Files.isRegularFile(base)) {
            base = base.getParent();
        }

        Path file = base.resolve("*.json");
        System.out.println(file.toAbsolutePath() + " exists? " + Files.exists(file));
    }
}
