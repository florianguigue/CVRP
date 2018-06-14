package model;

public class Client {
    private Integer id;
    private Integer tournee;
    private Integer position;
    private Integer latitude;
    private Integer longitude;
    private Integer quantite;

    public Client() {
    }

    public Client(Client client) {
        id = client.getId();
        tournee = client.getTournee();
        position = client.getPosition();
        latitude = client.getLatitude();
        longitude = client.getLongitude();
        quantite = client.getQuantite();
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

    public Integer getTournee() {
        return tournee;
    }

    public void setTournee(Integer tournee) {
        this.tournee = tournee;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
