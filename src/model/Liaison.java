package model;

public class Liaison {
    private Client source;
    private Client destination;
    private Integer distance;

    public Liaison() {
    }

    public Client getSource() {
        return source;
    }

    public void setSource(Client source) {
        this.source = source;
    }

    public Client getDestination() {
        return destination;
    }

    public void setDestination(Client destination) {
        this.destination = destination;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
