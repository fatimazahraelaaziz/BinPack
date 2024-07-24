package org.example;

import java.util.ArrayList;
import java.util.List;

public class Consumer implements Comparable<Consumer> {
    private String id;
    private long remainingLagCapacity;
    private double remainingArrivalCapacity;
    private List<Partition> assignedPartitions;

    public Consumer(String id, long remainingLagCapacity, double remainingArrivalCapacity) {
        this.id = id;
        this.remainingLagCapacity = remainingLagCapacity;
        this.remainingArrivalCapacity = remainingArrivalCapacity;
        this.assignedPartitions = new ArrayList<>();
    }

    public long getRemainingLagCapacity() {
        return remainingLagCapacity;
    }

    public double getRemainingArrivalCapacity() {
        return remainingArrivalCapacity;
    }

    public void assignPartition(Partition partition) {
        this.assignedPartitions.add(partition);
        this.remainingLagCapacity -= partition.getLag();
        this.remainingArrivalCapacity -= partition.getArrivalRate();
    }

    public List<Partition> getAssignedPartitions() {
        return assignedPartitions;
    }

    @Override
    public int compareTo(Consumer other) {
        return Long.compare(other.getRemainingLagCapacity(), this.remainingLagCapacity);
    }
}
