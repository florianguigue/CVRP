package graph;

import model.Circuit;
import model.Client;

import java.io.*;
import java.util.*;

public class Graph {
    private List<Circuit> allCircuits;
    private List<Circuit> circuitOptimise;
    private List<Client> listClients;
    private Client entrepot;
    private Integer nbCamions;
    private Integer listQuantites;
    private LinkedList<List<Circuit>> tabouList;
    private Map<List<Circuit>, Double> listVoisins;
    private Integer tailleListTabou;
    private Integer nbMaxIteration;

    public Graph(){}

    private void run() {
        try {
            initValue("./resources/data01.txt");
            initCircuit();
            rechercheMeilleurVoisins();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private List<Circuit> getMeilleurVoisin(List<Circuit> circuits, Double fitness) throws Exception{
        List<Circuit> graphClone;
        List<Circuit> voisinOptimise = null;
        Double fitnessOptimise = null;
        Circuit circuit;
        Double fitnessGraphCLone;
        Integer iClient;
        for(int i = 0; i < circuits.size(); ++i){
            circuit = circuits.get(i);
            for(Client client : circuit.getClients()){
                if(client.getId() != 0){
                    graphClone = cloneListCircuits(circuits);
                    iClient = graphClone.get(i).getClients().indexOf(client);
                    graphClone.get(i).supprimerClient(client);
                    for(Circuit clonedCircuit : graphClone){
                        for(int j = 1; j < clonedCircuit.getClients().size(); ++j){
                            if(clonedCircuit.getQuantite() + client.getQuantite() <= 100) {
                                clonedCircuit.ajouterClientAtIndex(client, j);
                                fitnessGraphCLone = getTotalFitness(graphClone);
                                if (fitnessOptimise == null || fitnessOptimise > fitnessGraphCLone) {
                                    fitnessOptimise = fitnessGraphCLone;
                                    voisinOptimise = graphClone;
                                }
                                clonedCircuit.supprimerClient(client);
                            }
                        }
                    }
                    graphClone.get(i).ajouterClientAtIndex(client, iClient);
                }
            }
        }
        return voisinOptimise;
    }

    private List<Circuit> runTabou(List<Circuit> circuits, Double fitness) throws Exception{
        listVoisins = new HashMap<>();
        List<Circuit> graphClone;
        Circuit circuit;
        Double fitnessGraphClone;
        Integer iClient;
        for(int i = 0; i < circuits.size(); ++i){
            circuit = circuits.get(i);
            for(Client client : circuit.getClients()){
                if(client.getId() != 0){
                    graphClone = cloneListCircuits(circuits);
                    iClient = graphClone.get(i).getClients().indexOf(client);
                    graphClone.get(i).supprimerClient(client);
                    for(Circuit clonedCircuit : graphClone){
                        for(int j = 1; j < clonedCircuit.getClients().size(); ++j){
                            if(clonedCircuit.getQuantite() + client.getQuantite() <= 100) {
                                clonedCircuit.ajouterClientAtIndex(client, j);
                                List<Circuit> keyCircuit = cloneListCircuits(graphClone);
                                fitnessGraphClone = getTotalFitness(keyCircuit);
                                listVoisins.put(keyCircuit, fitnessGraphClone);
                                clonedCircuit.supprimerClient(client);
                            }
                        }
                    }
                    graphClone.get(i).ajouterClientAtIndex(client, iClient);
                }
            }
        }
        Map.Entry<List<Circuit>, Double> min;
        min = Collections.min(listVoisins.entrySet(), Comparator.comparing(Map.Entry::getValue));
        while (tabouList.contains(min.getKey())){
            listVoisins.remove(min.getKey());
            if(listVoisins.size() == 0) return null;
            min = Collections.min(listVoisins.entrySet(), Comparator.comparing(Map.Entry::getValue));
        }

        if(min.getValue() > fitness) {
            if (tabouList.size() == tailleListTabou)
                tabouList.removeLast();
            if (!tabouList.contains(min.getKey()))
                tabouList.addFirst(min.getKey());
        }
        return min.getKey();
    }

    private List<Circuit> cloneListCircuits(List<Circuit> list) {
        try{
            List<Circuit> clone = new ArrayList<>(list.size());
            for (Circuit item : list) {
                clone.add(item.clone());
            }
            return clone;
        }catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static Double getTotalFitness(List<Circuit> circuits){
        Double fitnessTotal = 0.0;
        for(Circuit c : circuits) {
            c.setFitness(c.computeFitness());
            fitnessTotal += c.computeFitness();
        }
        return fitnessTotal;
    }

    public void initValue(String fileName){
        try{
            listClients = new ArrayList<>();
            File f = new File(fileName);
            InputStreamReader isr = new InputStreamReader(new FileInputStream(f));
            BufferedReader reader = new BufferedReader(isr);
            String s = reader.readLine();
            String[] values;
            listQuantites = 0;
            while (s != null) {
                if (!s.trim().equals("") && !s.startsWith("i")) {
                    values = s.split(";");
                    if(Integer.valueOf(values[0]) == 0)
                        entrepot = new Client(Integer.valueOf(values[0]),
                                Integer.valueOf(values[1]),
                                Integer.valueOf(values[2]),
                                0);
                    else
                        listClients.add(new Client(Integer.valueOf(values[0]),
                                Integer.valueOf(values[1]),
                                Integer.valueOf(values[2]),
                                Integer.valueOf(values[3])));
                    listQuantites += Integer.valueOf(values[3]);
                }
                s = reader.readLine();
            }
            nbCamions = (listQuantites /100+1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void initCircuit(){
        allCircuits = new ArrayList<>();
        Circuit circuit;
        LinkedList<Client> listClients;
        Client client;
        Random r = new Random();
        Integer index;

        for(int i = 0; i < nbCamions; ++i){
            listClients = new LinkedList<>();
            index = r.nextInt(this.listClients.size());
            client = this.listClients.get(index);
            this.listClients.remove(client);
            listClients.add(entrepot);
            listClients.add(client);
            circuit = new Circuit(listClients);
            allCircuits.add(circuit);
        }

        Double minDistance = null;
        Double distance;
        Integer selectedCircuit = null;
        Circuit circuit1;
        for(Client c : this.listClients){
            for(int i = 0; i < allCircuits.size(); i++){
                circuit1 = allCircuits.get(i);
                distance = c.getDistance(circuit1.computeGravityCenter());
                if((minDistance == null || minDistance > distance) && circuit1.getQuantite() + c.getQuantite() <= 100) {
                    minDistance = distance;
                    selectedCircuit = i;
                }
            }
            if(selectedCircuit == null){
                Circuit newCircuit = new Circuit(new LinkedList<>());
                newCircuit.ajouterClient(c);
                allCircuits.add(newCircuit);
            }
            else
                allCircuits.get(selectedCircuit).ajouterClient(c);
            minDistance = null;
            selectedCircuit = null;
        }

        Double circuitDistance;
        Double fitnessTotal = 0.;
        for(Circuit c : allCircuits) {
            circuitDistance = c.computeFitness();
            c.setFitness(circuitDistance);
            fitnessTotal += circuitDistance;
        }
        System.out.println("Fitness total : " + fitnessTotal);
    }

    public void rechercheMeilleurVoisins(){
        List<Circuit> listCircuits = allCircuits;
        try {
            for (int i = 0; i < 50; ++i) {
                listCircuits = getMeilleurVoisin(listCircuits, getTotalFitness(listCircuits));
            }
            System.out.println("Fitness après tabou : " + getTotalFitness(listCircuits));
        }catch (Exception e){
            System.out.println("Fitness après tabou : " + getTotalFitness(listCircuits));
        }
        circuitOptimise = listCircuits;
    }

    public Double tabou(){
        tabouList = new LinkedList<>();
        List<Circuit> listCircuits = allCircuits;
        try {
            for (int i = 0; i < nbMaxIteration; ++i) {
                listCircuits = runTabou(listCircuits, getTotalFitness(listCircuits));
            }
            System.out.println("Fitness après tabou : " + getTotalFitness(listCircuits));
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Fitness après tabou : " + getTotalFitness(listCircuits));
        }
        circuitOptimise = listCircuits;

        return getTotalFitness(listCircuits);
    }

    public static void main(String[] args) {
        Graph graph = new Graph();
        graph.run();
    }

    public List<Circuit> getAllCircuits() {
        return allCircuits;
    }

    public List<Client> getListClients() {
        return listClients;
    }

    public Client getEntrepot() {
        return entrepot;
    }

    public List<Circuit> getCircuitOptimise() {
        return circuitOptimise;
    }

    public void setTailleListTabou(Integer tailleListTabou) {
        this.tailleListTabou = tailleListTabou;
    }

    public void setNbMaxIteration(Integer nbMaxIteration) {
        this.nbMaxIteration = nbMaxIteration;
    }
}
