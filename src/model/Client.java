package model;

public class Client {
    private int tournee;
    private int position;
    private int latitude;
    private int longitude;
    private int quantite;

    public Client() {
    }

    public Client(int latitude, int longitude, int quantite) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.quantite = quantite;
    }

    public int getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public int getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public int getTournee() {
        return tournee;
    }

    public void setTournee(int tournee) {
        this.tournee = tournee;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
