package graph;

import model.Client;
import model.Liaison;

import java.io.*;
import java.util.*;

public class Graph {
    private final Integer POIDS_MAX_CAMION = 100;

    private List<Client> clients;
    private List<Liaison> distances;
    private Integer nbCamions;
    private Graph bestGraph;
    ArrayList<Integer> tabooKey = new ArrayList<>();
    ArrayList<Integer> tabooValues = new ArrayList<>();

    public Graph() {
        init();
    }

    /**
     * init the graph
     */
    public void init() {
        bestGraph = this;
        clients = new ArrayList<Client>();
        distances = new ArrayList<>();
        tabooKey = new ArrayList<>();
        tabooValues = new ArrayList<>();
        Properties prop = new Properties();
        try {
            InputStream input = new FileInputStream("./src/config.properties");
            prop.load(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        initClients(prop.getProperty("data_01"));
        initSchema();
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
     * Init the clients from the data file
     *
     * @param dataFile
     */
    public void initClients(String dataFile) {
        FileReader input = null;
        BufferedReader bufRead = null;
        String myLine = null;
        try {
            input = new FileReader(dataFile);
            bufRead = new BufferedReader(input);
            bufRead.readLine();
            while ((myLine = bufRead.readLine()) != null) {
                String[] array1 = myLine.split(";");
                Client client = new Client(Integer.parseInt(array1[0]), Integer.parseInt(array1[1]), Integer.parseInt(array1[2]), Integer.parseInt(array1[3]));
                clients.add(client);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Create a default schema with 3 roads from an arbitrary rule
     */
    private void initSchema() {
        int indexCompteur = 0, i = 0;
        for (Client client : clients) {
            Liaison liaison = new Liaison();
            liaison.setSource(client);
            client.setPosition(indexCompteur);
            client.setTournee(i);
            if (indexCompteur < clients.size() / 3 && getClientById(client.getId() + 1) != null && client.getId() != clients.size() - 1) {
                indexCompteur++;
                if (indexCompteur == 1 && i == 0) {
                    liaison.setSource(getClientById(0));
                } else if (indexCompteur == 1) {
                    Liaison liaisonDepot = new Liaison();
                    liaisonDepot.setSource(getClientById(0));
                    liaisonDepot.setDestination(getClientById(client.getId()));
                    liaisonDepot.setDistance(getDistance(liaisonDepot.getSource(), liaisonDepot.getDestination()));
                    distances.add(liaisonDepot);
                }
                liaison.setDestination(getClientById(client.getId() + 1));
            } else {
                liaison.setDestination(getClientById(0));
                indexCompteur = 0;
                i++;
            }
            liaison.setDistance(getDistance(liaison.getSource(), liaison.getDestination()));
            distances.add(liaison);
        }
    }

    /**
     * Swap position of 2 Clients
     *
     * @param client1
     * @param client2
     */
    public void swapClients(Client client1, Client client2) {

        int client1TourneeClient0 = 0;
        int client2TourneeClient0 = 0;
        //on vérifie que les clients sont différents
        if (client1 != client2 && client1.getId() != 0 && client2.getId() != 0) {

            //On récupère les données necessaires pour recréer la matrice des distances
            int positionC1 = client1.getPosition();
            int positionC2 = client2.getPosition();

            Client clientPrecedent1 = null;
            Client clientSuivant1 = null;
            Client clientPrecedent2 = null;
            Client clientSuivant2 = null;

            //parmis les clients à livrer lequel est celui avant le client1 et le client 2
            for (Client c : clients) {
                if (c.getPosition() == positionC1 - 1 && c.getTournee() == client1.getTournee()) {
                    clientPrecedent1 = c;
                }
                if (c.getPosition() == positionC1 + 1 && c.getTournee() == client1.getTournee()) {
                    clientSuivant1 = c;
                }
                if (c.getPosition() == positionC2 - 1 && c.getTournee() == client2.getTournee()) {
                    clientPrecedent2 = c;
                }
                if ((c.getPosition() == positionC2 + 1) && c.getTournee() == client2.getTournee()) {
                    clientSuivant2 = c;
                }
            }

            //si le client1 est le dernier de sa tournée
            if (clientSuivant1 == null || clientSuivant1 == getClientById(0)) {
                clientSuivant1 = getClientById(0);
            }
            //si le client2 est le dernier de sa tournée
            if (clientSuivant2 == null || clientSuivant2 == getClientById(0)) {
                clientSuivant2 = getClientById(0);
            }
            if (clientPrecedent1 == null || clientPrecedent1 == getClientById(0)) {
                client1TourneeClient0 = client1.getTournee();
                clientPrecedent1 = getClientById(0);
            }
            if (clientPrecedent2 == null || clientPrecedent2 == getClientById(0)) {
                client2TourneeClient0 = client2.getTournee();
                clientPrecedent2 = getClientById(0);
            }

            int iClient0 = 0;
            //On modifie la matrice des distances
            for (Liaison distance : distances) {
                boolean hasChanged = false;

                //On modifie la matrice des distances du client précédent et du client
                if (distance.getSource() == clientPrecedent1) {
                    if (clientPrecedent1 == getClientById(0)) {
                        if (iClient0 == client1TourneeClient0) {
                            distance.setDestination(client2);
                            hasChanged = true;
                        } else {
                            iClient0++;
                        }
                    } else if (clientPrecedent1 == client2) {
                        distance.setDestination(clientSuivant1);
                    } else {
                        distance.setDestination(client2);
                        hasChanged = true;
                    }
                } else if (distance.getSource() == client1) {
                    if (client1 == getClientById(0)) {
                        if (iClient0 == client1TourneeClient0) {
                            distance.setDestination(clientSuivant2);
                            hasChanged = true;
                        } else {
                            iClient0++;
                        }
                    } else if (clientSuivant2 == client1) {
                        distance.setDestination(client2);
                    } else {
                        distance.setDestination(clientSuivant2);
                        hasChanged = true;
                    }
                }

                //On modifie la matrice des distances du client2 précédent et du client2
                else if (distance.getSource() == clientPrecedent2) {
                    if (clientPrecedent2 == getClientById(0)) {
                        if (iClient0 == client2TourneeClient0) {
                            distance.setDestination(client1);
                            hasChanged = true;
                        } else {
                            iClient0++;
                        }
                    } else if (clientPrecedent2 == client1) {
                        distance.setDestination(clientSuivant2);
                    } else {
                        distance.setDestination(client1);
                        hasChanged = true;
                    }
                } else if (distance.getSource() == client2) {
                    if (client2 == getClientById(0)) {
                        if (iClient0 == client2TourneeClient0) {
                            distance.setDestination(clientSuivant1);
                            hasChanged = true;
                        } else {
                            iClient0++;
                        }
                    } else if (clientSuivant1 == client2) {
                        distance.setDestination(client1);
                    } else {
                        distance.setDestination(clientSuivant1);
                        hasChanged = true;
                    }
                }

                //recalcul des distances de la ligne modifiée
                if (hasChanged) {
                    try {
                        distance.setDistance(getDistance(distance.getSource(), distance.getDestination()));
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    hasChanged = false;
                }
            }

            //on inverse les points dans la matrice principale
            Integer tempPosition = client1.getPosition();
            Integer tempTournee = client1.getTournee();
            client1.setPosition(client2.getPosition());
            client1.setTournee(client2.getTournee());
            client2.setPosition(tempPosition);
            client2.setTournee(tempTournee);
        }
    }

    /**
     * Get random client from the client list "clients"
     * It can not return the clientID 0 (warehouse)
     *
     * @return
     */
    public Client getRandomClient() {
        int index = 0;
        do {
            Random randomGenerator = new Random();
            index = randomGenerator.nextInt(clients.size());
        } while (clients.get(index).getId() == 0);
        return clients.get(index);
    }

    /**
     * Calculate the fitness of the actual solution
     *
     * @return Integer
     */
    public Integer getFitness() {
        Integer fitness = 0;
        for (Liaison liaison : distances) {
            fitness += liaison.getDistance();
        }
        return fitness;
    }

    /**
     * Calculate the distance between two customers
     *
     * @param source
     * @param destination
     * @return Integer
     */
    private Integer getDistance(Client source, Client destination) throws NullPointerException {
        Double distance = Math.sqrt(Math.pow((source.getLatitude() - destination.getLatitude()), 2) + Math.pow((source.getLongitude() - destination.getLongitude()), 2));
        return distance.intValue();

    }

    /**
     * Used to run the optimisation process
     */
    public Graph runOpti() {
        Graph bestGraph = this;
        int currentFitness = getFitness();
        int maxFitness = 0;
        while (maxFitness < Integer.MAX_VALUE) {
            Client bestNeighbor = null;
            Client bestClient = null;
            maxFitness = Integer.MAX_VALUE;
            for (Client client : clients) {
                if (client.getId() != 0) {
                    Map<Client, Integer> neighbors = getNeighbors(client);
                    for (Map.Entry<Client, Integer> fitness : neighbors.entrySet()) {
                        if (fitness.getValue() < maxFitness && !inTaboo(client.getTournee(), fitness.getKey().getTournee())) {
                            bestClient = client;
                            bestNeighbor = fitness.getKey();
                            maxFitness = fitness.getValue();
                        }
                    }
                }
            }
            if (maxFitness != Integer.MAX_VALUE) {
                if (maxFitness > currentFitness) {
                    currentFitness = maxFitness;
                    if (tabooKey.size() < 9) {
                        tabooKey.add(bestClient.getTournee());
                        tabooValues.add(bestNeighbor.getTournee());
                    } else {
                        tabooKey.remove(0);
                        tabooValues.remove(0);
                        tabooKey.add(bestClient.getTournee());
                        tabooValues.add(bestNeighbor.getTournee());
                    }
                }
                swapClients(bestClient, bestNeighbor);
                if (maxFitness != Integer.MAX_VALUE && maxFitness < currentFitness) {
                    bestGraph = this;
                }
                currentFitness = maxFitness;
            }
            System.out.println(maxFitness);
        }
        return bestGraph;
    }

    private boolean inTaboo(Integer tourneeKey, Integer tourneeValue) {
        boolean inTaboo = false;
        for (int i = 0; i < tabooKey.size(); i++) {
            if (tabooKey.get(i) == tourneeKey && tabooValues.get(i) == tourneeValue) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return all the neighbors for a given client with the fitness associated
     *
     * @return
     */
    private Map<Client, Integer> getNeighbors(Client clientToSwap) {
        Map<Client, Integer> fitnessList = new HashMap<>();
        for (Client client : clients) {
            int fitness = getFitness();
            if (client != clientToSwap && client.getId() != 0) {
                //on swap 2 clients, on calcule la fitness puis on remet les clients en place (simulation)
                //debugList(distances);
                swapClients(clientToSwap, client);
                fitness = getFitness();
                //debugList(distances);
                fitnessList.put(client, fitness);
                swapClients(clientToSwap, client);
                fitness = getFitness();
                if (true) {
                    //debugList(distances);
                }
            }
        }
        return fitnessList;
    }

    private void debugList(List<Liaison> list) {
        for (Liaison liaison : list) {
            System.out.print(liaison.getDistance() + "\n");
        }

        System.out.println();
    }

    public Integer getNbCamions() {
        return nbCamions;
    }

    public void setNbCamions() {
        Integer poidsTotal = 0;
        for (Client client : clients) {
            poidsTotal += client.getQuantite();
        }
        Double nbCamionsDouble = Math.ceil(poidsTotal / POIDS_MAX_CAMION);
        nbCamions = nbCamionsDouble.intValue();
    }

    private Client getClientById(Integer id) {
        for (Client client : getClients()) {
            if (Objects.equals(client.getId(), id))
                return client;
        }
        return null;
    }
}
