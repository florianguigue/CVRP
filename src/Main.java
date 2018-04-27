import graph.Graph;

public class Main {

    private static Graph graph;

    public static void main(String[] args) {
        graph = new Graph();
        graph.getClients();
        run();
        System.out.println();
    }

    public static void run(){
        graph.runOpti();
    }
}
