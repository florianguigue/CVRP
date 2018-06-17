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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
        Properties prop = new Properties();

        try {
            InputStream input = new FileInputStream("./src/config.properties");
            prop.load(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Button data1 = new Button("data01");
        data1.setOnAction(actionEvent -> {
            actualData = "data_01";
            initData(graph, root, prop.getProperty(actualData));
            graph.initCircuit();
            root.getChildren().removeAll(nodes);
            nodes.clear();
            createAllCircuit(graph.getAllCircuits(), graph.getWarehouse());
            root.getChildren().addAll(nodes);
        });
        data1.setLayoutX(140);
        root.getChildren().add(data1);
        Button data2 = new Button("data02");
        data2.setOnAction(actionEvent -> {
            actualData = "data_02";
            initData(graph, root, prop.getProperty(actualData));
            graph.initCircuit();
            root.getChildren().removeAll(nodes);
            nodes.clear();
            createAllCircuit(graph.getAllCircuits(), graph.getWarehouse());
            root.getChildren().addAll(nodes);
        });
        data2.setLayoutX(210);
        root.getChildren().add(data2);
        Button data3 = new Button("data03");
        data3.setOnAction(actionEvent -> {
            actualData = "data_03";
            initData(graph, root, prop.getProperty(actualData));
            graph.initCircuit();
            root.getChildren().removeAll(nodes);
            nodes.clear();
            createAllCircuit(graph.getAllCircuits(), graph.getWarehouse());
            root.getChildren().addAll(nodes);
        });
        data3.setLayoutX(280);
        root.getChildren().add(data3);
        Button data4 = new Button("data04");
        data4.setOnAction(actionEvent -> {
            actualData = "data_04";
            initData(graph, root, prop.getProperty(actualData));
            graph.initCircuit();
            root.getChildren().removeAll(nodes);
            nodes.clear();
            createAllCircuit(graph.getAllCircuits(), graph.getWarehouse());
            root.getChildren().addAll(nodes);
        });
        data4.setLayoutX(350);
        root.getChildren().add(data4);
        //Creating a Scene

        Integer tabouListSize = 3;
        Integer tabouIteration = 100;

        Button runTabou = new Button("Run tabou");
        runTabou.setLayoutX(450);
        runTabou.setOnAction(actionEvent -> {
            graph.setNbMaxIteration(tabouIteration);
            graph.setTabouListSize(tabouListSize);
            graph.tabou();
            root.getChildren().removeAll(nodes);
            nodes.clear();
            createAllCircuit(graph.getOptimizedCircuit(), graph.getWarehouse());
            root.getChildren().addAll(nodes);
        });
        root.getChildren().addAll(runTabou);

        Button runMultipleTabou = new Button("Find optimum");
        runMultipleTabou.setLayoutX(600);
        runMultipleTabou.setOnAction(actionEvent -> {
            Double meilleurFitness = 10000.0;
            List<Circuit> meilleurCircuit = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                initData(graph, root, prop.getProperty(actualData));
                graph.initCircuit();
                root.getChildren().removeAll(nodes);
                nodes.clear();
                createAllCircuit(graph.getAllCircuits(), graph.getWarehouse());
                root.getChildren().addAll(nodes);

                graph.setNbMaxIteration(tabouIteration);
                graph.setTabouListSize(tabouListSize);

                Double fitness = graph.tabou();
                root.getChildren().removeAll(nodes);
                nodes.clear();
                List<Circuit> circuits = graph.getOptimizedCircuit();
                createAllCircuit(circuits, graph.getWarehouse());
                root.getChildren().addAll(nodes);

                if (meilleurFitness > fitness) {
                    meilleurFitness = fitness;
                    meilleurCircuit = circuits;
                }
            }
            System.out.println("Meilleur fitness : " + meilleurFitness);
            root.getChildren().removeAll(nodes);
            nodes.clear();
            createAllCircuit(meilleurCircuit, graph.getWarehouse());
            root.getChildren().addAll(nodes);
        });
        root.getChildren().addAll(runMultipleTabou);

        Label tabouList = new Label("Choix des données : ");
        tabouList.setLayoutX(0);
        root.getChildren().addAll(tabouList);

        Scene scene = new Scene(root, 900, 600);

        //Setting title to the scene
        primaryStage.setTitle("VRP Problem");

        //Adding the scene to the stage
        primaryStage.setScene(scene);

        //Displaying the contents of a scene
        primaryStage.show();


    }

    public static void main(String args[]){
        launch(args);
    }

    private static void initData(Graph controller, Group root, String fileName){
        controller.initValue(fileName);
        List<Client> customers = controller.getAllCustomers();
        root.getChildren().removeAll(nodes);
        nodes.clear();
        for(Client c : customers){
            createCustomer(c, c.getId().toString(), Color.BLUE);
        }
        createCustomer(controller.getWarehouse(), "Entrepôt", Color.RED);

        root.getChildren().addAll(nodes);
    }

    private static void createCustomer(Client customer, String title, Color color){
        Circle customerCicle;
        Text text;
        customerCicle = new Circle(customer.getLongitude()*coeff + constante,
                customer.getLatitude()*coeff + constante,
                5);
        customerCicle.setFill(color);
        customerCicle.setStroke(color);
        customerCicle.setStrokeWidth(2);
        customerCicle.setStrokeType(StrokeType.OUTSIDE);

        nodes.add(customerCicle);
    }

    private static void createAllCircuit(List<Circuit> circuits, Client entrepot){
        Color color;
        Client customer;

        for(Circuit c : circuits){
            color = Color.color(Math.random(), Math.random(), Math.random());
            for(int i = 0; i < c.getCustomers().size() -1; ++i){
                customer = c.getCustomers().get(i);
                if(!(customer.getId() == 0))
                    createCustomer(customer, customer.getId().toString(), color);

                createLine(customer, c.getCustomers().get(i+1), color);
            }
            customer = c.getCustomers().getLast();
            createCustomer(customer, customer.getId().toString(), color);
            createLine(customer, entrepot, color);

        }
        createCustomer(entrepot, "Entrepôt", Color.RED);
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
