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

    public Graph() {
        init();
    }

    /**
     * init the graph
     */
    public void init() {
        clients = new ArrayList<Client>();
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
            if (indexCompteur <= clients.size() / 3) {
                client.setTournee(i);
                client.setPosition(indexCompteur);
                indexCompteur++;
            }
            if (indexCompteur == clients.size() / 3) {
                indexCompteur = 0;
                i++;
            }
        }
    }

    /**
     * Swap position of 2 Clients
     *
     * @param client1
     * @param client2
     */
    public void swapClients(Client client1, Client client2) {
        //on vérifie que les clients sont différents
        if (client1 != client2) {

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

                if (distance.getSource() == clientPrecedent1) {
                    distance.setDestination(client2);
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

                //recalcul des distances de la ligne modifiée
                if (hasChanged == true) {
                    distance.setDistance(getDistance(distance.getSource(), distance.getDestination()));
                    hasChanged = false;
                }
            }

            //on inverse les points dans la matrice principale
            Client temp = client1;
            client1.setPosition(client2.getPosition());
            client1.setTournee(client2.getTournee());
            client2.setPosition(temp.getPosition());
            client2.setTournee(temp.getTournee());
        }
    }

    /**
     * Get random client from the client list "clients"
     *
     * @return
     */
    public Client getRandomClient() {
        Random randomGenerator = new Random();
        int index = randomGenerator.nextInt(clients.size());
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
     * @param source
     * @param destination
     * @return Integer
     */
    private Integer getDistance(Client source, Client destination) {
        Double distance = Math.sqrt(Math.pow((source.getLatitude() - destination.getLatitude()), 2) + Math.pow((source.getLongitude() - destination.getLongitude()), 2));
        return distance.intValue();
    }

    /**
     * Used to run the optimisation process
     */
    public Graph runOpti() {
        Graph bestGraph = this;
        int currentFitness = Integer.MAX_VALUE;
        int tabooFake = 0;
        while (tabooFake < 4) {
            Client clientToSwap = getRandomClient();
            Map<Client, Integer> neighbors = getNeighbors(clientToSwap);
            Collection<Integer> listFitnessValues = neighbors.values();
            int maxFitness = 0;
            Client bestNeighbor = null;
            for (Map.Entry<Client, Integer> fitness : neighbors.entrySet()) {
                if (fitness.getValue() > maxFitness)
                    bestNeighbor = fitness.getKey();
                    maxFitness = fitness.getValue();
            }
            if (maxFitness >= currentFitness) {
                swapClients(clientToSwap,bestNeighbor);
                bestGraph = this;
            } else {
                tabooFake++;
            }
        }
        return bestGraph;
    }

    /**
     * Return all the neighbors for a given client with the fitness associated
     *
     * @return
     */
    private Map<Client, Integer> getNeighbors(Client clientToSwap) {
        Map<Client, Integer> fitnessList = new HashMap<>();
        for (Client client : clients) {
            if (client != clientToSwap) {
                //on swap 2 client, on calcul la fitness puis on remet les clients en place (simulation)
                swapClients(clientToSwap, client);
                int fitness = getFitness();
                fitnessList.put(client, fitness);
                swapClients(clientToSwap, client);
            }
        }
        return fitnessList;
    }
    public Integer getNbCamions() {
        return nbCamions;
    }

    public void setNbCamions() {
        Integer poidsTotal = 0;
        for (Client client: clients) {
            poidsTotal += client.getQuantite();
        }
        Double nbCamionsDouble = Math.ceil(poidsTotal / POIDS_MAX_CAMION);
        nbCamions = nbCamionsDouble.intValue();
    }
}
