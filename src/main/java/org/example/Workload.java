package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Workload {
    private static final String csvSplitBy = ",";
    private static ArrayList<Double> datay = new ArrayList<>();
    private static ArrayList<Double> datax = new ArrayList<>();

    public ArrayList<Double> getDatay() {
        return datay;
    }

    public ArrayList<Double> getDatax() {
        return datax;
    }

    public Workload() throws IOException, URISyntaxException {
        this.loadWorkload();
    }

    private void loadWorkload() throws IOException, URISyntaxException {
        ClassLoader CLDR = this.getClass().getClassLoader();
        InputStream inputStream = CLDR.getResourceAsStream("defaultArrivalRatesm.csv");
    
        if (inputStream == null) {
            throw new IOException("Resource not found: defaultArrivalRatesm.csv");
        }
    
        List<String> out = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                out.add(line);
            }
        }
    
        for (String line : out) {
            String[] workFields = line.split(csvSplitBy);
            double targetXPointValue = Double.parseDouble(workFields[1]);
            datax.add(0d); // Placeholder, you can modify as needed
            datay.add(targetXPointValue);
        }
    }
    
}
