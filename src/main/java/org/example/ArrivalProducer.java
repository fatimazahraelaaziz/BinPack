package org.example;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ArrivalProducer {
    private static final Logger log = LogManager.getLogger(ArrivalProducer.class);
    static List<Partition> topicpartitions = new ArrayList<>();
    static double totalArrivalrate;

    static {
        for (int i = 0; i <= 4; i++) {
            topicpartitions.add(new Partition(i, 0, 0));
        }
    }

    public static void callForArrivals() {
        try {
            log.info("Requesting arrival rate...");
            Workload workload = new Workload(); // Load the CSV data
            ArrayList<Double> arrivalRates = workload.getDatay();

            // Répartir chaque taux d'arrivée dans les partitions
            for (int i = 0; i < arrivalRates.size(); i++) {
                double arrivalRate = arrivalRates.get(i);
                double partitionArrivalRate = arrivalRate / topicpartitions.size();
                log.info("Total messages to send: {}", arrivalRate);
                for (int j = 0; j < topicpartitions.size(); j++) {
                    Partition partition = topicpartitions.get(j);
                    partition.setArrivalRate(partitionArrivalRate);
                    log.info("Partition {} arrival rate set to {}", partition.getId(), partitionArrivalRate);
                }
                BinPackState.scaleAsPerBinPack();
            }
        } catch (IOException | URISyntaxException e) {
            log.error("Error calling for arrivals: {}", e.getMessage(), e);
        }
    }
}
