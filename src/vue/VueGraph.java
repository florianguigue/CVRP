package vue;

import graph.Graph;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Circuit;
import model.Client;

import java.util.ArrayList;
import java.util.List;

public class VueGraph extends Application {

    private static Integer coeff = 5;
    private static Integer constante = 50;
    private static List<Node> nodes;
    private static String actualData = "data_01";

    @Override
    public void start(Stage primaryStage) throws Exception {

        Graph graph = new Graph();
        nodes = new ArrayList<>();
        Group root = new Group();

        Button data1 = new Button("data01");
        data1.setOnAction(actionEvent -> {
            initCircuits(root, graph, "./resources/data01.txt");
        });
        data1.setLayoutX(140);
        root.getChildren().add(data1);
        Button data2 = new Button("data02");
        data2.setOnAction(actionEvent -> {
            initCircuits(root, graph, "./resources/data02.txt");
        });
        data2.setLayoutX(210);
        root.getChildren().add(data2);
        Button data3 = new Button("data03");
        data3.setOnAction(actionEvent -> {
            initCircuits(root, graph, "./resources/data03.txt");
        });
        data3.setLayoutX(280);
        root.getChildren().add(data3);

        Button data4 = new Button("data04");
        data4.setOnAction(actionEvent -> {
            initCircuits(root, graph, "./resources/data04.txt");
        });
        data4.setLayoutX(350);
        root.getChildren().add(data4);

        Button data5 = new Button("data05");
        data5.setOnAction(actionEvent -> {
            initCircuits(root, graph, "./resources/data05.txt");
        });
        data5.setLayoutX(420);
        root.getChildren().add(data5);

        Integer tabouListSize = 3;
        Integer tabouIteration = 100;

        Button runTabou = new Button("Run tabou");
        runTabou.setLayoutX(490);
        runTabou.setOnAction(actionEvent -> {
            graph.setNbMaxIteration(tabouIteration);
            graph.setTailleListTabou(tabouListSize);
            graph.tabou();
            root.getChildren().removeAll(nodes);
            nodes.clear();
            createAllCircuit(graph.getCircuitOptimise(), graph.getEntrepot());
            root.getChildren().addAll(nodes);
        });
        root.getChildren().addAll(runTabou);

        Button runMultipleTabou = new Button("Find optimum");
        runMultipleTabou.setLayoutX(600);
        runMultipleTabou.setOnAction(actionEvent -> {
            Double meilleurFitness = 10000.0;
            List<Circuit> meilleurCircuit = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                initData(graph, root, actualData);
                graph.initCircuit();
                root.getChildren().removeAll(nodes);
                nodes.clear();
                createAllCircuit(graph.getAllCircuits(), graph.getEntrepot());
                root.getChildren().addAll(nodes);

                graph.setNbMaxIteration(tabouIteration);
                graph.setTailleListTabou(tabouListSize);

                Double fitness = graph.tabou();
                root.getChildren().removeAll(nodes);
                nodes.clear();
                List<Circuit> circuits = graph.getCircuitOptimise();
                createAllCircuit(circuits, graph.getEntrepot());
                root.getChildren().addAll(nodes);

                if (meilleurFitness > fitness) {
                    meilleurFitness = fitness;
                    meilleurCircuit = circuits;
                }
            }
            System.out.println("Meilleure fitness : " + meilleurFitness);
            root.getChildren().removeAll(nodes);
            nodes.clear();
            createAllCircuit(meilleurCircuit, graph.getEntrepot());
            root.getChildren().addAll(nodes);
        });
        root.getChildren().addAll(runMultipleTabou);

        Label tabouList = new Label("Choix des données : ");
        tabouList.setLayoutX(0);
        root.getChildren().addAll(tabouList);

        Scene scene = new Scene(root, 900, 600);

        primaryStage.setTitle("VRP Problem");

        primaryStage.setScene(scene);

        primaryStage.show();


    }

    private void initCircuits(Group root, Graph graph, String data) {
        actualData = data;
        initData(graph, root, data);
        graph.initCircuit();
        root.getChildren().removeAll(nodes);
        nodes.clear();
        createAllCircuit(graph.getAllCircuits(), graph.getEntrepot());
        root.getChildren().addAll(nodes);
    }

    public static void main(String args[]){
        launch(args);
    }

    private static void initData(Graph controller, Group root, String fileName){
        controller.initValue(fileName);
        List<Client> listClients = controller.getListClients();
        root.getChildren().removeAll(nodes);
        nodes.clear();
        for(Client c : listClients){
            nouveauClient(c, c.getId().toString(), Color.BLUE);
        }
        nouveauClient(controller.getEntrepot(), "Entrepôt", Color.RED);

        root.getChildren().addAll(nodes);
    }

    private static void nouveauClient(Client customer, String title, Color color){
        Circle customerCicle;
        customerCicle = new Circle(customer.getLongitude()*coeff + constante, customer.getLatitude()*coeff + constante, 5);
        customerCicle.setFill(color);
        customerCicle.setStroke(color);
        customerCicle.setStrokeWidth(2);
        customerCicle.setStrokeType(StrokeType.OUTSIDE);

        nodes.add(customerCicle);
    }

    private static void createAllCircuit(List<Circuit> listCircuits, Client entrepot){
        Color color;
        Client client;

        for(Circuit c : listCircuits){
            color = Color.color(Math.random(), Math.random(), Math.random());
            for(int i = 0; i < c.getClients().size() -1; ++i){
                client = c.getClients().get(i);
                if(!(client.getId() == 0))
                    nouveauClient(client, client.getId().toString(), color);

                createLine(client, c.getClients().get(i+1), color);
            }
            client = c.getClients().getLast();
            nouveauClient(client, client.getId().toString(), color);
            createLine(client, entrepot, color);

        }
        nouveauClient(entrepot, "Entrepôt", Color.RED);
    }

    private static void createLine(Client source, Client dest, Color color){
        Line line = new Line(source.getLongitude()*coeff + constante + 5, source.getLatitude()*coeff + constante - 5, dest.getLongitude()*coeff + constante + 5, dest.getLatitude()*coeff + constante - 5);
        line.setFill(color);
        line.setStroke(color);
        line.setStrokeWidth(2);
        line.setStrokeType(StrokeType.OUTSIDE);
        nodes.add(line);
    }
}
