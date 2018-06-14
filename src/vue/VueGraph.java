package vue;

import graph.Graph;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import model.Client;
import model.Liaison;

public class VueGraph extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        Graph graph = new Graph();
        Group root =  new Group();
        for (Client client : graph.getClients()) {
            Circle circle = new Circle((client.getLongitude() * 5) + 50, (client.getLatitude() * 5 + 50), 2);
            root.getChildren().add(circle);
        }

        for (Liaison liaison : graph.getDistances()) {
            Line line = new Line((liaison.getSource().getLongitude() * 5) + 50, (liaison.getSource().getLatitude() * 5) + 50, (liaison.getDestination().getLongitude() * 5) + 50, (liaison.getDestination().getLatitude() * 5) + 50);
            root.getChildren().add(line);
        }

        Scene scene = new Scene(root, 1000, 1000);

        primaryStage.setTitle("VRP graph");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String args[]) {
        launch(args);
    }
}
