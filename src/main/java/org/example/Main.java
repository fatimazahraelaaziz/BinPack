package org.example;

import java.util.concurrent.ExecutionException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {

        while (true) {
            Lag.readEnvAndCreateAdminClient();
            ArrivalProducer.callForArrivals();  // Appel de la méthode correcte
            Lag.getCommittedLatestOffsetsAndLag();

            log.info("--------------------");
            log.info("--------------------");

            // Appel de queryConsumerGroup et stockage du résultat
            int consumerGroupSize = Lag.queryConsumerGroup();
            try {
                scaleLogicTail(consumerGroupSize);
            } catch (ExecutionException e) {
                log.error("ExecutionException encountered: ", e);
            }

            double di = 1000.0;
            // Convert 'di' from milliseconds to seconds for logging purposes
            double diInSeconds = di / 1000;
            log.info("Sleeping for {} seconds", diInSeconds);
            log.info("******************************************");
            log.info("******************************************");
            Thread.sleep((long) di);
        }
    }

    private static void scaleLogicTail(int consumerGroupSize) throws InterruptedException, ExecutionException {
        if (consumerGroupSize != BinPackState.size) {
            log.info("no action, previous action is not seen yet");
            return;
        }
        BinPackState.scaleAsPerBinPack();
        if (BinPackState.action.equals("up") || BinPackState.action.equals("down") || BinPackState.action.equals("REASS")) {
            BinPackLag.scaleAsPerBinPack();
        }
    }
}
