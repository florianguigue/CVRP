package graph;

import model.Circuit;
import model.Client;

import java.io.*;
import java.util.*;

public class Graph {
    private List<Circuit> allCircuits;
    private List<Circuit> optimizedCircuit;
    private List<Client> allCustomers;
    private Client entrepot;
    private Integer nbTrucks;
    private Integer allQuantities;
    private LinkedList<List<Circuit>> tabouList;
    private Map<List<Circuit>, Double> allNeighbor;
    private Integer tabouListSize;
    private Integer nbMaxIteration;

    public Graph(){}

    private void run() {
        try {
            initValue("./resources/data01.txt");
            initCircuit();
            searchOptimizedNeighbor();


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private List<Circuit> getOptimizedNeighbor(List<Circuit> circuits, Double fitness) throws Exception{
        List<Circuit> cloneCircuit;
        List<Circuit> optimizedNeighbor = null;
        Double optimizedFitness = null;
        Circuit circuit;
        Double calculatedFitness;
        Integer customerIndex;
        for(int i = 0; i < circuits.size(); ++i){
            circuit = circuits.get(i);
            for(Client customer : circuit.getCustomers()){
                if(customer.getId() == 0){
                    continue;
                }
                cloneCircuit = cloneList(circuits);
                customerIndex = cloneCircuit.get(i).getCustomers().indexOf(customer);
                cloneCircuit.get(i).removeCustomer(customer);
                for(Circuit clonedCircuit : cloneCircuit){
                    for(int j = 1; j < clonedCircuit.getCustomers().size(); ++j){
                        if(clonedCircuit.getQuantity() + customer.getQuantite() <= 100) {
                            clonedCircuit.addCustomerAt(customer, j);
                            calculatedFitness = getTotalFitness(cloneCircuit);
                            if (optimizedFitness == null || optimizedFitness > calculatedFitness) {
                                optimizedFitness = calculatedFitness;
                                optimizedNeighbor = cloneCircuit;
                            }
                            clonedCircuit.removeCustomer(customer);
                        }
                    }
                }
                cloneCircuit.get(i).addCustomerAt(customer, customerIndex);
            }
        }
        if(fitness.equals(optimizedFitness)) throw new Exception("Valeur optimisée");
        return optimizedNeighbor;
    }

    private List<Circuit> tabouMethod(List<Circuit> circuits, Double fitness) throws Exception{
        allNeighbor = new HashMap<>();
        List<Circuit> cloneCircuit;
        Circuit circuit;
        Double calculatedFitness;
        Integer customerIndex;
        for(int i = 0; i < circuits.size(); ++i){
            circuit = circuits.get(i);
            for(Client customer : circuit.getCustomers()){
                if(customer.getId() == 0){
                    continue;
                }
                cloneCircuit = cloneList(circuits);
                customerIndex = cloneCircuit.get(i).getCustomers().indexOf(customer);
                cloneCircuit.get(i).removeCustomer(customer);
                for(Circuit clonedCircuit : cloneCircuit){
                    for(int j = 1; j < clonedCircuit.getCustomers().size(); ++j){
                        if(clonedCircuit.getQuantity() + customer.getQuantite() <= 100) {
                            clonedCircuit.addCustomerAt(customer, j);
                            List<Circuit> keyCircuit = cloneList(cloneCircuit);
                            calculatedFitness = getTotalFitness(keyCircuit);
                            allNeighbor.put(keyCircuit, calculatedFitness);
                            clonedCircuit.removeCustomer(customer);
                        }
                    }
                }
                cloneCircuit.get(i).addCustomerAt(customer, customerIndex);
            }
        }
        Map.Entry<List<Circuit>, Double> min;
        min = Collections.min(allNeighbor.entrySet(), Comparator.comparing(Map.Entry::getValue));
        while (tabouList.contains(min.getKey())){
            allNeighbor.remove(min.getKey());
            if(allNeighbor.size() == 0) return null;
            min = Collections.min(allNeighbor.entrySet(), Comparator.comparing(Map.Entry::getValue));
        }

        if(min.getValue() > fitness) {
            if (tabouList.size() == tabouListSize)
                tabouList.removeLast();
            if (!tabouList.contains(min.getKey()))
                tabouList.addFirst(min.getKey());
        }
        return min.getKey();
    }

    private List<Circuit> cloneList(List<Circuit> list) {
        try{
            List<Circuit> clone = new ArrayList<>(list.size());
            for (Circuit item : list) clone.add(item.clone());
            return clone;
        }catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static Double getTotalFitness(List<Circuit> circuits){
        Double fitnessTotal = 0d;
        for(Circuit c : circuits) {
            c.setFitness(c.computeFitness());
            fitnessTotal += c.computeFitness();
        }
        return fitnessTotal;
    }

    public void initValue(String fileName){
        try{
            allCustomers = new ArrayList<>();
            File f = new File(fileName);
            InputStreamReader isr = new InputStreamReader(new FileInputStream(f));
            BufferedReader reader = new BufferedReader(isr);
            String s = reader.readLine();
            String[] values;
            allQuantities = 0;
            while (s != null) {
                if (!s.trim().equals("") && !s.startsWith("i")) {
                    values = s.split(";");
                    if(Integer.valueOf(values[0]) == 0)
                        entrepot = new Client(Integer.valueOf(values[0]),
                                Integer.valueOf(values[1]),
                                Integer.valueOf(values[2]),
                                0);
                    else
                        allCustomers.add(new Client(Integer.valueOf(values[0]),
                                Integer.valueOf(values[1]),
                                Integer.valueOf(values[2]),
                                Integer.valueOf(values[3])));
                    allQuantities += Integer.valueOf(values[3]);
                }
                s = reader.readLine();
            }
            nbTrucks = (allQuantities/100+1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void initCircuit(){
        allCircuits = new ArrayList<>();
        Circuit circuit;
        LinkedList<Client> customers;
        Client customer;
        Random r = new Random();
        Integer index;

        //Init all circuit with one customer selected randomly
        for(int i = 0; i < nbTrucks; ++i){
            customers = new LinkedList<>();
            index = r.nextInt(allCustomers.size());
            customer = allCustomers.get(index);
            allCustomers.remove(customer);
            customers.add(entrepot);
            customers.add(customer);
            circuit = new Circuit(customers);
            allCircuits.add(circuit);
        }

        Double minDistance = null;
        Double distance;
        Integer selectedCircuit = null;
        Circuit circuit1;
        for(Client c : allCustomers){
            for(int i = 0; i < allCircuits.size(); i++){
                circuit1 = allCircuits.get(i);
                distance = c.getDistance(circuit1.computeGravityCenter());
                if((minDistance == null || minDistance > distance) && circuit1.getQuantity() + c.getQuantite() <= 100) {
                    minDistance = distance;
                    selectedCircuit = i;
                }
            }
            if(selectedCircuit == null){
                Circuit newCircuit = new Circuit(new LinkedList<>());
                newCircuit.addCustomer(c);
                allCircuits.add(newCircuit);
            }
            else
                allCircuits.get(selectedCircuit).addCustomer(c);
            minDistance = null;
            selectedCircuit = null;
        }

        Double circuitDistance;
        Double fitnessTotal = 0d;
        for(Circuit c : allCircuits) {
            circuitDistance = c.computeFitness();
            c.setFitness(circuitDistance);
            fitnessTotal += circuitDistance;
        }
        System.out.println("Fitness total : " + fitnessTotal);
    }

    public void searchOptimizedNeighbor(){
        List<Circuit> c1 = allCircuits;
        try {
            for (int i = 0; i < 50; ++i) {
                c1 = getOptimizedNeighbor(c1, getTotalFitness(c1));
            }
            System.out.println("Fitness après tabou : " + getTotalFitness(c1));
        }catch (Exception e){
            System.out.println("Fitness après tabou : " + getTotalFitness(c1));
        }
        optimizedCircuit = c1;
    }

    public Double tabou(){
        tabouList = new LinkedList<>();
        List<Circuit> c1 = allCircuits;
        try {
            for (int i = 0; i < nbMaxIteration; ++i) {
                c1 = tabouMethod(c1, getTotalFitness(c1));
            }
            System.out.println("Fitness après tabou : " + getTotalFitness(c1));
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Fitness après tabou : " + getTotalFitness(c1));
        }
        optimizedCircuit = c1;

        return getTotalFitness(c1);
    }

    public static void main(String[] args) {
        Graph graph = new Graph();
        graph.run();
    }

    public List<Circuit> getAllCircuits() {
        return allCircuits;
    }

    public List<Client> getAllCustomers() {
        return allCustomers;
    }

    public Client getWarehouse() {
        return entrepot;
    }

    public List<Circuit> getOptimizedCircuit() {
        return optimizedCircuit;
    }

    public void setTabouListSize(Integer tabouListSize) {
        this.tabouListSize = tabouListSize;
    }

    public void setNbMaxIteration(Integer nbMaxIteration) {
        this.nbMaxIteration = nbMaxIteration;
    }
}
