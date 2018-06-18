package model;

import java.util.LinkedList;

public class Circuit implements Cloneable{
    private LinkedList<Client> clients;
    private Integer quantite;
    private Double fitness;

    public Circuit(LinkedList<Client> customers) {
        this.clients = customers;
        this.quantite = 0;
        for(Client c : customers)
            this.quantite += c.getQuantite();
    }

    public LinkedList<Client> getClients() {
        return clients;
    }

    public void setFitness(Double fitness) {
        this.fitness = fitness;
    }

    public void ajouterClientAtIndex(Client c, Integer index){
        quantite += c.getQuantite();
        clients.add(index, c);
    }

    public void ajouterClient(Client c){
        quantite += c.getQuantite();
        clients.add(c);
    }

    public void supprimerClient(Client c){
        quantite -= c.getQuantite();
        clients.remove(c);
    }

    public Integer getQuantite() {
        return quantite;
    }

    public Client computeGravityCenter(){
        Integer X = 0;
        Integer Y = 0;
        for(Client c : clients){
            X += c.getLongitude();
            Y += c.getLatitude();
        }
        return new Client(0, X/ clients.size(), Y/ clients.size(), 0);
    }

    public Double computeFitness(){
        Double fitness = 0d;
        for(int i = 0; i < clients.size()-1; ++i){
            fitness += clients.get(i).getDistance(clients.get(i+1));
        }
        fitness += clients.getLast().getDistance(clients.getFirst());
        return fitness;
    }

    public Circuit clone() throws CloneNotSupportedException {
        Circuit c = (Circuit)super.clone();
        c.clients = (LinkedList<Client>) clients.clone();
        return c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Circuit circuit = (Circuit) o;

        if (clients != null ? !clients.equals(circuit.clients) : circuit.clients != null) return false;
        if (quantite != null ? !quantite.equals(circuit.quantite) : circuit.quantite != null) return false;
        return fitness != null ? fitness.equals(circuit.fitness) : circuit.fitness == null;
    }

    @Override
    public int hashCode() {
        int result = clients != null ? clients.hashCode() : 0;
        result = 31 * result + (quantite != null ? quantite.hashCode() : 0);
        result = 31 * result + (fitness != null ? fitness.hashCode() : 0);
        return result;
    }
}
