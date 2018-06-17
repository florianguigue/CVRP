package model;

import java.util.LinkedList;

public class Circuit implements Cloneable{
    private LinkedList<Client> customers;
    private Integer quantity;
    private Double fitness;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Circuit{" + "customers={");
        for(Client c : customers)
            sb.append(c);
        sb.append("}, quantity=")
                .append(quantity)
                .append(", fitness=")
                .append(fitness)
                .append('}');
        return sb.toString();
    }

    public Circuit(LinkedList<Client> customers) {
        this.customers = customers;
        this.quantity = 0;
        for(Client c : customers)
            this.quantity += c.getQuantite();
    }

    public LinkedList<Client> getCustomers() {
        return customers;
    }

    public void setFitness(Double fitness) {
        this.fitness = fitness;
    }

    public void addCustomerAt(Client c, Integer index){
        quantity += c.getQuantite();
        customers.add(index, c);
    }

    public void addCustomer(Client c){
        quantity += c.getQuantite();
        customers.add(c);
    }

    public void removeCustomer(Client c){
        quantity -= c.getQuantite();
        customers.remove(c);
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Client computeGravityCenter(){
        Integer X = 0;
        Integer Y = 0;
        for(Client c : customers){
            X += c.getLongitude();
            Y += c.getLatitude();
        }
        return new Client(0, X/customers.size(), Y/customers.size(), 0);
    }

    public Double computeFitness(){
        Double fitness = 0d;
        for(int i = 0; i < customers.size()-1; ++i){
            fitness += customers.get(i).getDistance(customers.get(i+1));
        }
        fitness += customers.getLast().getDistance(customers.getFirst());
        return fitness;
    }

    public Circuit clone() throws CloneNotSupportedException {
        Circuit c = (Circuit)super.clone();
        c.customers = (LinkedList<Client>)customers.clone();
        return c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Circuit circuit = (Circuit) o;

        if (customers != null ? !customers.equals(circuit.customers) : circuit.customers != null) return false;
        if (quantity != null ? !quantity.equals(circuit.quantity) : circuit.quantity != null) return false;
        return fitness != null ? fitness.equals(circuit.fitness) : circuit.fitness == null;
    }

    @Override
    public int hashCode() {
        int result = customers != null ? customers.hashCode() : 0;
        result = 31 * result + (quantity != null ? quantity.hashCode() : 0);
        result = 31 * result + (fitness != null ? fitness.hashCode() : 0);
        return result;
    }
}
