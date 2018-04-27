package graph;

import model.Client;
import model.Liaison;

import java.io.*;
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
        initClients("C:\\Users\\Epulapp\\Documents\\Cours\\Semestre 8\\Optimisation discrete\\resources\\data01.txt");
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

    public void initClients(String dataFile) {
        FileReader input = null;
        BufferedReader bufRead = null;
        String myLine = null;
        try {
            input = new FileReader(dataFile);
            bufRead = new BufferedReader(input);
            bufRead.readLine();
            while ( (myLine = bufRead.readLine()) != null)
            {
                String[] array1 = myLine.split(";");
                Client client = new Client(Integer.parseInt(array1[1]), Integer.parseInt(array1[2]), Integer.parseInt(array1[3]));
                clients.add(client);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
