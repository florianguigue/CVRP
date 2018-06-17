package vue;

import graph.Graph;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import model.Client;
import model.Liaison;

public class VueGraph extends Application {

    private static Graph graph;
    private static Integer COEF = 6;
    private static Integer ECART = 50;

    @Override
    public void start(Stage primaryStage) throws Exception {

        graph = new Graph();
        Group group;
        System.out.println(graph.getFitness());
        graph = graph.runOpti();
        System.out.println(graph.getFitness());
        group = draw(graph);

        Scene scene = new Scene(group, 1000, 1000);

        primaryStage.setTitle("VRP graph");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public Group draw(Graph graph) {
        Group root = new Group();
        for (Client client : graph.getClients()) {
            Circle circle = new Circle((client.getLongitude() * COEF) + ECART, (client.getLatitude() * COEF + ECART), 2);
            if (client.getId() == 0) {
                circle.setFill(Color.BLUE);
            }
            root.getChildren().add(circle);
        }
        Color color = Color.color(Math.random(), Math.random(), Math.random());

        for (Liaison liaison : graph.getDistances()) {
            if (liaison.getDestination().getId() == null) {
                System.out.println("aie");
            }
            Line line = new Line((liaison.getSource().getLongitude() * COEF) + ECART, (liaison.getSource().getLatitude() * COEF) + ECART, (liaison.getDestination().getLongitude() * COEF) + ECART, (liaison.getDestination().getLatitude() * COEF) + ECART);
            line.setFill(color);
            root.getChildren().add(line);

        }
        return root;
    }

    public static void main(String args[]) {
        launch(args);
    }
}
