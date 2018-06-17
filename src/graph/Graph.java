package graph;

import model.Client;
import model.Liaison;

import java.io.*;
import java.util.*;

public class Graph {
    private final Integer POIDS_MAX_CAMION = 100;
    private final Integer TABOO_SIZE = (int) Math.pow(5, 2);

    private List<Client> clients;
    private List<Liaison> distances;
    private Integer nbCamions;
    private Graph bestGraph;
    ArrayList<Integer> tabooKey = new ArrayList<>();
    ArrayList<Integer> tabooValues = new ArrayList<>();

    public Graph() {
        init();
    }

    public Graph(Graph clone) {
        this.clients = new ArrayList<>();
        for (Client client : clone.clients) {
            this.clients.add(new Client(client));
        }
        this.distances = new ArrayList<>();
        for (Liaison liaison : clone.distances) {
            this.distances.add(new Liaison(liaison));
        }
        this.nbCamions = clone.getNbCamions();
    }

    /**
     * init the graph
     */
    public void init() {
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
            if (getClientById(client.getId() + 1) != null) {
                if (client.getQuantite() + getChargeCamion(client.getTournee()) <= POIDS_MAX_CAMION - 1) {
                    indexCompteur++;
                    if (indexCompteur == 1 && i == 0) {
                        liaison.setSource(getClientById(0));
                    }
                    liaison.setDestination(getClientById(client.getId() + 1));
                } else {
                    indexCompteur = 1;
                    i++;
                    Liaison liaisonDepot = new Liaison();
                    liaisonDepot.setSource(new Client(getClientById(0)));
                    liaisonDepot.getSource().setTournee(i);
                    liaisonDepot.getSource().setPosition(0);
                    liaisonDepot.setDestination(client);
                    liaisonDepot.setDistance(getDistance(liaisonDepot.getSource(), liaisonDepot.getDestination()));
                    distances.add(liaisonDepot);
                    Liaison oldDistance = distances.get(distances.size() - 2);
                    oldDistance.setDestination(getClient0ByTournee(i - 1));
                    oldDistance.setDistance(getDistance(oldDistance.getSource(),oldDistance.getDestination()));
                    client.setTournee(i);
                    client.setPosition(indexCompteur);
                    liaison.setDestination(getClientById(client.getId() + 1));
                }
            } else {
                liaison.setDestination(getClient0ByTournee(i));
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
        if (client1.getId() != client2.getId() && client1.getId() != 0 && client2.getId() != 0 &&
                canSwapClient(client1, client2)) {

            //On récupère les données necessaires pour recréer la matrice des distances
            int positionC1 = client1.getPosition();
            int positionC2 = client2.getPosition();

            Client clientPrecedent1 = null;
            Client clientSuivant1 = null;
            Client clientPrecedent2 = null;
            Client clientSuivant2 = null;

            //parmis les clients à livrer lequel est celui avant le client1 et le client 2
            for (Liaison l : distances) {
                if (l.getSource().getPosition() == positionC1 - 1 && l.getSource().getTournee() == client1.getTournee()) {
                    clientPrecedent1 = l.getSource();
                }
                if (l.getSource().getPosition() == positionC1 + 1 && l.getSource().getTournee() == client1.getTournee()) {
                    clientSuivant1 = l.getSource();
                }
                if (l.getSource().getPosition() == positionC2 - 1 && l.getSource().getTournee() == client2.getTournee()) {
                    clientPrecedent2 = l.getSource();
                }
                if ((l.getSource().getPosition() == positionC2 + 1) && l.getSource().getTournee() == client2.getTournee()) {
                    clientSuivant2 = l.getSource();
                }
            }

            if(clientSuivant1 == null){
                clientSuivant1 = getClient0ByTournee(client1.getTournee());
            }
            if(clientSuivant2 == null){
                clientSuivant2 = getClient0ByTournee(client2.getTournee());
            }

            //On modifie la matrice des distances
            for (Liaison distance : distances) {
                boolean hasChanged = false;

                //On modifie la matrice des distances du client précédent et du client
                if (distance.getSource().getId() == clientPrecedent1.getId()
                        && distance.getSource().getTournee() == clientPrecedent1.getTournee()) {
                    if (clientPrecedent1.getId() == client2.getId()) {
                        distance.setDestination(clientSuivant1);
                        hasChanged = true;
                    } else {
                        distance.setDestination(client2);
                        hasChanged = true;
                    }
                } else if (distance.getSource().getId() == client1.getId()
                        && distance.getSource().getTournee() == client1.getTournee()) {
                    if (clientSuivant2.getId() == client1.getId()) {
                        distance.setDestination(client2);
                        hasChanged = true;
                    } else {
                        distance.setDestination(clientSuivant2);
                        hasChanged = true;
                    }
                }

                //On modifie la matrice des distances du client2 précédent et du client2
                else if (distance.getSource().getId() == clientPrecedent2.getId()
                        && distance.getSource().getTournee() == clientPrecedent2.getTournee()) {
                    if (clientPrecedent2.getId() == client1.getId()) {
                        distance.setDestination(clientSuivant2);
                        hasChanged = true;
                    } else {
                        distance.setDestination(client1);
                        hasChanged = true;
                    }
                } else if (distance.getSource().getId() == client2.getId()
                        && distance.getSource().getTournee() == client2.getTournee()) {
                    if (clientSuivant1.getId() == client2.getId()) {
                        distance.setDestination(client1);
                        hasChanged = true;
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
            Integer tempPosition = client1.getPosition().intValue();
            Integer tempTournee = client1.getTournee().intValue();
            for (Liaison liaison : distances){
                if(liaison.getSource().getId() == client1.getId()){
                    liaison.getSource().setTournee(client2.getTournee());
                    liaison.getSource().setPosition(client2.getPosition());
                }
                if(liaison.getSource().getId() == client2.getId()){
                    liaison.getSource().setTournee(tempTournee);
                    liaison.getSource().setPosition(tempPosition);
                }
            }
            client1.setPosition(client2.getPosition());
            client1.setTournee(client2.getTournee());
            client2.setPosition(tempPosition);
            client2.setTournee(tempTournee);

            for (Liaison liaison : getDistances()){
                for (Liaison liaison2 : getDistances()){
                    if(liaison.getSource() != liaison2.getSource()){
                        if(liaison.getDestination().getId()  == liaison2.getDestination().getId() && liaison.getDestination().getTournee() == liaison2.getDestination().getTournee()){
                            System.out.println("source : "+liaison.getSource().getId() + " tournee :" + liaison.getSource().getTournee() + " position: " + liaison.getSource().getPosition());
                            System.out.println("destination : " + liaison.getDestination().getId() + " tournee :" + liaison.getDestination().getTournee() + " position : " + liaison.getDestination().getPosition());
                            System.out.println("source : "+liaison2.getSource().getId() + " tournee :" + liaison2.getSource().getTournee() + " position: " + liaison2.getSource().getPosition());
                            System.out.println("destination : " + liaison2.getDestination().getId() + " tournee :" + liaison2.getDestination().getTournee() + " position : " + liaison2.getDestination().getPosition());
                            System.out.println();
                        }
                    }
                }
            }
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
        bestGraph = new Graph(this);
        int currentFitness = getFitness();
        int maxFitness = 0;
        Client oldBestClient = null;
        Client oldBestNeighbor = null;
        Client bestNeighbor = null;
        Client bestClient = null;
        while (maxFitness < Integer.MAX_VALUE) {
            maxFitness = Integer.MAX_VALUE;
            //for (Client client : clients) {
            Client client = getRandomClient();
                if (client.getId() != 0) {
                    Map<Client, Integer> neighbors = getNeighbors(client);
                    for (Map.Entry<Client, Integer> fitness : neighbors.entrySet()) {
                        if (fitness.getValue() < maxFitness && !inTaboo(fitness.getKey().getPosition(), client.getPosition())) {
                            bestClient = client;
                            bestNeighbor = fitness.getKey();
                            maxFitness = fitness.getValue();
                        }
                    }
                }
            //}
            if (maxFitness != Integer.MAX_VALUE) {
                if (maxFitness >= currentFitness) {
                    currentFitness = maxFitness;
                    if (tabooKey.size() < TABOO_SIZE) {
                        tabooKey.add(oldBestClient.getPosition());
                        tabooValues.add(oldBestNeighbor.getPosition());
                    } else {
                        tabooKey.remove(0);
                        tabooValues.remove(0);
                        tabooKey.add(oldBestClient.getPosition());
                        tabooValues.add(oldBestNeighbor.getPosition());
                    }
                }
                this.getFitness();
                swapClients(bestClient, bestNeighbor);
                if (maxFitness != Integer.MAX_VALUE && maxFitness <= bestGraph.getFitness()) {
                    bestGraph = new Graph(this);
                }
                currentFitness = maxFitness;
            }
            oldBestClient = new Client(bestClient);
            oldBestNeighbor = new Client(bestNeighbor);
            System.out.println(maxFitness);
        }
        return bestGraph;
    }

    /**
     * return true if the element yet exist
     *
     * @param tourneeKey
     * @param tourneeValue
     * @return
     */
    private boolean inTaboo(Integer tourneeKey, Integer tourneeValue) {
        boolean inTaboo = false;
        for (int i = 0; i < tabooKey.size(); i++) {
            if (tabooKey.get(i) == tourneeKey && tabooValues.get(i) == tourneeValue ||
                    tabooKey.get(i) == tourneeValue && tabooValues.get(i) == tourneeKey) {
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
            Graph simulGraph = new Graph(this);
            int fitness = simulGraph.getFitness();
            if (client.getId() != clientToSwap.getId() && client.getId() != 0) {
                //on swap 2 clients, on calcule la fitness puis on remet les clients en place (simulation)
                simulGraph.swapClients(simulGraph.getClientById(clientToSwap.getId()), simulGraph.getClientById(client.getId()));
                fitness = simulGraph.getFitness();
                fitnessList.put(client, fitness);
            }
        }
        return fitnessList;
    }

    private String debugList(List<Liaison> list, Client c1, Client c2) {
        String temp = "";
        for (Liaison liaison : list) {
            if (liaison.getSource().getId() == c1.getId() || liaison.getSource().getId() == c2.getId()) {
                temp += liaison.getDistance();
                System.out.print(liaison.getDistance() + "\n");
            }
        }

        System.out.println();
        return temp;
    }

    public Integer getNbCamions() {
        return nbCamions;
    }


    /**
     * Return the truck weight
     *
     * @param tournee
     * @return
     */
    public Integer getChargeCamion(Integer tournee) {
        Integer chargeTot = 0;
        for (Client client : clients) {
            if (client.getTournee() == tournee)
                chargeTot += client.getQuantite();
        }
        return chargeTot;
    }

    public boolean canSwapClient(Client C1, Client C2) {
        if (getChargeCamion(C2.getTournee()) + C1.getQuantite() - C2.getQuantite() < POIDS_MAX_CAMION
                && getChargeCamion(C1.getTournee()) + C2.getQuantite() - C1.getQuantite() < POIDS_MAX_CAMION) {
            return true;
        }
        return false;
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

    private Client getClient0ByTournee(Integer tournee) {
        for (Liaison l : getDistances()) {
            if (l.getSource().getId() == 0 && l.getSource().getTournee() == tournee)
                return l.getSource();
        }
        return null;
    }
}
