package org.example;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BinPackLag {
    private static final Logger log = LogManager.getLogger(BinPackLag.class);
    static Instant lastUpScaleDecision = Instant.now();

    static double wsla = 0.5; // Static value for simulation
    static double rebTime = 0.05; // Static value for simulation
    static List<Consumer> assignment = new ArrayList<>();
    static List<Consumer> currentAssignment = assignment;

    static double mu = 200.0; // Static value for simulation

    static {
        float fup = 0.7f; // Static value for simulation
        long consumerLagCapacity = (long) (mu * wsla * fup); // Ensure the capacity is long
        currentAssignment.add(new Consumer("0", consumerLagCapacity, mu * fup));
        for (Partition p : ArrivalProducer.topicpartitions) {
            currentAssignment.get(0).assignPartition(p);
        }
    }

    public static void scaleAsPerBinPack() {
        log.info("Currently we have this number of consumers group: " + BinPackState.size);

        for (int i = 0; i < 5; i++) {
            Partition partition = ArrivalProducer.topicpartitions.get(i);
            long additionalLag = (long) ((ArrivalProducer.totalArrivalrate * rebTime) / 5.0);
            partition.setLag(partition.getLag() + additionalLag);
        }

        if (BinPackState.action.equals("up") || BinPackState.action.equals("REASS")) {
            int neededSize = binPackAndScale();
            log.info("We currently need the following consumers (as per the bin pack): " + neededSize);
            int replicasForScale = neededSize - BinPackState.size;
            if (replicasForScale > 0) {
                log.info("We have to upscale by " + replicasForScale);
                BinPackState.size = neededSize;
                lastUpScaleDecision = Instant.now();
                currentAssignment = assignment;
                // Simulate Kubernetes scaling
                log.info("I have Upscaled group; you should have {}", neededSize);
            } else if (replicasForScale == 0) {
                currentAssignment = assignment;
            }
        } else if (BinPackState.action.equals("down")) {
            int neededSizeDown = binPackAndScaled();
            int replicasForScaled = BinPackState.size - neededSizeDown;
            if (replicasForScaled < BinPackState.size) {
                log.info("We have to downscale by " + replicasForScaled);
                BinPackState.size = neededSizeDown;
                lastUpScaleDecision = Instant.now();
                // Simulate Kubernetes scaling
                log.info("I have downscaled group; you should have {}", neededSizeDown);
                currentAssignment = assignment;
            }
        }
        log.info("===================================");
    }

    private static int binPackAndScale() {
        log.info("Shall we upscale group {}", "testgroup1");
        List<Consumer> consumers = new ArrayList<>();
        int consumerCount = 1;
        List<Partition> parts = new ArrayList<>(ArrivalProducer.topicpartitions);

        float fup = 0.7f; // Static value for simulation

        for (Partition partition : parts) {
            long lagCapacity = (long) (mu * wsla * fup);
            if (partition.getLag() > lagCapacity) {
                log.info("Since partition {} has lag {} higher than consumer capacity times wsla {}" +
                        " we are truncating its lag", partition.getId(), partition.getLag(), lagCapacity);
                partition.setLag(lagCapacity);
            }
        }

        for (Partition partition : parts) {
            double arrivalRateCapacity = mu * fup;
            if (partition.getArrivalRate() > arrivalRateCapacity) {
                log.info("Since partition {} has arrival rate {} higher than consumer service rate {}" +
                        " we are truncating its arrival rate", partition.getId(),
                        String.format("%.2f", partition.getArrivalRate()), String.format("%.2f", arrivalRateCapacity));
                partition.setArrivalRate(arrivalRateCapacity);
            }
        }

        Collections.sort(parts, Collections.reverseOrder());

        while (true) {
            int j;
            consumers.clear();
            for (int t = 0; t < consumerCount; t++) {
                long consumerLagCapacity = (long) (mu * wsla * fup);
                consumers.add(new Consumer(String.valueOf(t), consumerLagCapacity, mu * fup));
            }

            for (j = 0; j < parts.size(); j++) {
                int i;
                Collections.sort(consumers, Collections.reverseOrder());
                for (i = 0; i < consumerCount; i++) {
                    if (consumers.get(i).getRemainingLagCapacity() >= parts.get(j).getLag()
                            && consumers.get(i).getRemainingArrivalCapacity() >= parts.get(j).getArrivalRate()) {
                        consumers.get(i).assignPartition(parts.get(j));
                        break;
                    }
                }
                if (i == consumerCount) {
                    consumerCount++;
                    break;
                }
            }
            if (j == parts.size())
                break;
        }
        log.info("The BP up scaler recommended for group {} {}", "testgroup1", consumers.size());
        return consumers.size();
    }

    private static int binPackAndScaled() {
        log.info("Shall we down scale group {}", "testgroup1");
        List<Consumer> consumers = new ArrayList<>();
        int consumerCount = 1;
        float fdown = 0.2f; // Static value for simulation
        List<Partition> parts = new ArrayList<>(ArrivalProducer.topicpartitions);
        double fractionDynamicAverageMaxConsumptionRate = mu * fdown;

        for (Partition partition : parts) {
            long lagCapacity = (long) (fractionDynamicAverageMaxConsumptionRate * wsla);
            if (partition.getLag() > lagCapacity) {
                log.info("Since partition {} has lag {} higher than consumer capacity times wsla {}" +
                        " we are truncating its lag", partition.getId(), partition.getLag(), lagCapacity);
                partition.setLag(lagCapacity);
            }
        }

        for (Partition partition : parts) {
            double arrivalRateCapacity = fractionDynamicAverageMaxConsumptionRate;
            if (partition.getArrivalRate() > arrivalRateCapacity) {
                log.info("Since partition {} has arrival rate {} higher than consumer service rate {}" +
                        " we are truncating its arrival rate", partition.getId(),
                        String.format("%.2f", partition.getArrivalRate()),
                        String.format("%.2f", arrivalRateCapacity));
                partition.setArrivalRate(arrivalRateCapacity);
            }
        }

        Collections.sort(parts, Collections.reverseOrder());
        while (true) {
            int j;
            consumers.clear();
            for (int t = 0; t < consumerCount; t++) {
                long consumerLagCapacity = (long) (fractionDynamicAverageMaxConsumptionRate * wsla);
                consumers.add(new Consumer(String.valueOf(t), consumerLagCapacity, fractionDynamicAverageMaxConsumptionRate));
            }

            for (j = 0; j < parts.size(); j++) {
                int i;
                Collections.sort(consumers, Collections.reverseOrder());
                for (i = 0; i < consumerCount; i++) {
                    if (consumers.get(i).getRemainingLagCapacity() >= parts.get(j).getLag()
                            && consumers.get(i).getRemainingArrivalCapacity() >= parts.get(j).getArrivalRate()) {
                        consumers.get(i).assignPartition(parts.get(j));
                        break;
                    }
                }
                if (i == consumerCount) {
                    consumerCount++;
                    break;
                }
            }
            if (j == parts.size())
                break;
        }
        log.info("The BP down scaler recommended for group {} {}", "testgroup1", consumers.size());
        return consumers.size();
    }
}
