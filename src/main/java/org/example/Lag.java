package org.example;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.ConsumerGroupDescription;

public class Lag {
    private static final Logger log = LogManager.getLogger(Lag.class);
    private static Map<String, ConsumerGroupDescription> consumerGroupDescriptionMap = new HashMap<>();

    static double totalArrivalrate = 0.0;
    static int partitions = 5; // Assume there are 5 partitions for simplicity

    public static void readEnvAndCreateAdminClient() {
        log.info("Environment variables read and admin client created (simulated)");
    }

    public static void getCommittedLatestOffsetsAndLag() {
        for (int i = 0; i < partitions; i++) {
            long lag = (long) (Math.random() * 1000);
            log.info("partition {} has lag {}", i, lag);
            totalArrivalrate += lag;
        }
        log.info("total lag {} at {}", totalArrivalrate, java.time.Instant.now());
    }

    

    public static int queryConsumerGroup() {
        // Simuler la logique de requête du groupe de consommateurs sans utiliser Kafka

        // Exemple : ajouter des descriptions de groupes de consommateurs
        consumerGroupDescriptionMap.put("testgroup1", new ConsumerGroupDescription("testgroup1", 5));

        int members = consumerGroupDescriptionMap.get("testgroup1").members();

        log.info("consumers nb as per simulated kafka {}", members);

        return members;
    }


    private static class ConsumerGroupDescription {
        private String groupId;
        private int members;

        public ConsumerGroupDescription(String groupId, int members) {
            this.groupId = groupId;
            this.members = members;
        }

        public int members() {
            return members;
        }
    }
}
