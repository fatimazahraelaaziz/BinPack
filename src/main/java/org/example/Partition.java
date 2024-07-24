package org.example;

public class Partition implements Comparable<Partition> {
    private int id;
    private double arrivalRate;
    private long lag;

    public Partition(int id, double arrivalRate, long lag) {
        this.id = id;
        this.arrivalRate = arrivalRate;
        this.lag = lag;
    }

    public int getId() {
        return id;
    }

    public double getArrivalRate() {
        return arrivalRate;
    }

    public void setArrivalRate(double arrivalRate) {
        this.arrivalRate = arrivalRate;
    }

    public long getLag() {
        return lag;
    }

    public void setLag(long lag) {
        this.lag = lag;
    }

    @Override
    public int compareTo(Partition other) {
        // Vous pouvez ajuster cette comparaison selon votre besoin.
        return Double.compare(this.arrivalRate, other.arrivalRate);
    }
}
