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

    public void init() {
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

    /**
     * Swap position of 2 Clients
     *
     * @param client1
     * @param client2
     */
    public void swapClients(Client client1, Client client2) {
        //On récupère les données necessaires pour recréer la matrice des distances
        int positionC1 = client1.getPosition();
        int positionC2 = client2.getPosition();

        Client clientPrecedent1 = null;
        Client clientSuivant1 = null;
        Client clientPrecedent2 = null;
        Client clientSuivant2 = null;

        //parmis les clients à livrer lequel est celui avant le client1 et le client 2
        for (Client c : clients) {
            if (c.getPosition() == positionC1 - 1) {
                clientPrecedent1 = c;
            }
            if (c.getPosition() == positionC1 + 1) {
                clientSuivant1 = c;
            }
            if (c.getPosition() == positionC2 - 1) {
                clientPrecedent2 = c;
            }

            if (c.getPosition() == positionC2 + 1) {
                clientSuivant2 = c;
            }
        }
        //On modifie la matrice des distances
        for (Liaison distance : distances) {
            boolean hasChanged = false;

            //On modifie la matrice des distances du client1 précédent et du client1
            if (distance.getSource() == clientPrecedent1) {
                distance.setDestination(client2);
                hasChanged = true;
            }
            if (distance.getSource() == client1) {
                distance.setDestination(clientSuivant2);
                hasChanged = true;
            }

            //On modifie la matrice des distances du client2 précédent et du client2
            if (distance.getSource() == clientPrecedent2) {
                distance.setDestination(client1);
                hasChanged = true;
            }
            if (distance.getSource() == client2) {
                distance.setDestination(clientSuivant1);
                hasChanged = true;
            }

            //recalcule des distances de la ligne modifiée
            if (hasChanged == true) {
                distance.setDistance(getDistance(distance.getSource(), distance.getDestination()));
            }
        }

        //on inverse les points dans la matrice principale
        Client temp = client1;
        client1.setPosition(client2.getPosition());
        client1.setTournee(client2.getTournee());
        client2.setPosition(temp.getPosition());
        client2.setTournee(temp.getTournee());
    }

    private int getDistance(Client source, Client destination) {
        return 1;
    }

}
