package model;

public class Client implements Cloneable{
    private Integer id;
    private Integer latitude;
    private Integer longitude;
    private Integer quantite;

    public Client() {
    }

    public Client(Integer id, Integer latitude, Integer longitude, Integer quantite) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.quantite = quantite;
    }

    public Integer getLatitude() {
        return latitude;
    }

    public void setLatitude(Integer latitude) {
        this.latitude = latitude;
    }

    public Integer getLongitude() {
        return longitude;
    }

    public void setLongitude(Integer longitude) {
        this.longitude = longitude;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getDistance(Client c){
        return Math.sqrt(Math.pow(longitude - c.getLongitude(), 2) + Math.pow(latitude - c.getLatitude(), 2));
    }
}
