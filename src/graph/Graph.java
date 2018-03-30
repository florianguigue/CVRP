package graph;

import model.Client;
import model.Liaison;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private List<Client> clients;
    private List<Liaison> distances;

    public Graph() {
        init();
    }

    public void init(){
        clients = new ArrayList<Client>();
    }

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }

    public List<Liaison> getDistances() {
        return distances;
    }

    public void setDistances(List<Liaison> distances) {
        this.distances = distances;
    }
}
