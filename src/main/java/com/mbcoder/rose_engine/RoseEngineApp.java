package com.mbcoder.rose_engine;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.data.RangeDomain;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.lang.Math;

public class RoseEngineApp extends Application {

    private MapView mapView;
    private static int PULSES_PER_REV = 8000;
    private SimpleMarkerSymbol circleMarker;
    private  GraphicsOverlay graphicsOverlay;

    public static void main(String[] args) {

        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {

        // set the title and size of the stage and show it
        stage.setTitle("Rose Engine App");
        stage.setWidth(800);
        stage.setHeight(700);
        stage.show();

        // create a JavaFX scene with a stack pane as the root node and add it to the scene
        StackPane stackPane = new StackPane();
        Scene scene = new Scene(stackPane);
        stage.setScene(scene);

        circleMarker = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0xFFFF0000,1);

        // create a MapView to display the map and add it to the stack pane
        mapView = new MapView();
        stackPane.getChildren().add(mapView);

        // create an ArcGISMap with an imagery basemap
        ArcGISMap map = new ArcGISMap(Basemap.createImagery());
        //ArcGISMap map = new ArcGISMap();

        // display the map by setting the map on the map view
        mapView.setMap(map);

        graphicsOverlay = new GraphicsOverlay();
        mapView.getGraphicsOverlays().add(graphicsOverlay);

        mapView.setViewpointCenterAsync(new Point(0,0), 2000);

        int offsetAngle = 0;

        for(int radius=2; radius<=100; radius+=2) {
            System.out.println("rad " + radius);
            drawCycle(radius, 3, 12, offsetAngle);

            offsetAngle+=1;
        }

        //drawCycle(100,5,12);
        //drawSomething();




    }

    private void drawCycle (double radius, double waveAmplitude, double totalWaves, int offsetAngle) {

        int minSteps = PULSES_PER_REV / (int) totalWaves;

        double actualSteps = PULSES_PER_REV / totalWaves;

        //System.out.println("min " + minSteps);
        //System.out.println("actual " + actualSteps);

        int stepsSoFar = 0;
        int currentSteps = 0;

        for (int waveNumber = 0; waveNumber<totalWaves; waveNumber++) {
            // calculate where we should be
            double actual = (waveNumber+1) * actualSteps;

            // is min steps enough?
            double diff = actual - (stepsSoFar + minSteps);

            if (diff >= 1) {
                currentSteps = minSteps + 1;
            } else {
                currentSteps = minSteps;
            }

            drawWave(stepsSoFar, currentSteps, waveAmplitude, radius, offsetAngle);

             // keep tally of steps so far
            stepsSoFar = stepsSoFar + currentSteps;
            //System.out.println("wave " + currentSteps);

        }

        //System.out.println("---total steps " + stepsSoFar);

        // main revolution
        for (double step=0; step<PULSES_PER_REV; step++) {
            double angle = 360 * (step / PULSES_PER_REV);
            //System.out.println("angle " + angle);
        }
    }

    private void drawWave(int stepsSoFar, int waveSteps, double maxAmplitude, double radius, int offsetAngle) {

        // cycle through wave steps
        for (int step=0; step<waveSteps; step++) {
            // calculate the amplitude for that step
            double angleAtStep = 360 * ((double)step / waveSteps);
            double sineValue = maxAmplitude * Math.sin(Math.toRadians(angleAtStep));

            // calculate angle for actual position
            double rotationAngle = 360 * ((double)(stepsSoFar + step) / PULSES_PER_REV) + offsetAngle;

            //System.out.println("--- angle " + rotationAngle + " sine " + sineValue);

            Point point = new Point(calcXpos(radius + sineValue, rotationAngle), calcYpos(radius + sineValue, rotationAngle));

            Graphic graphic = new Graphic(point, circleMarker);
            graphicsOverlay.getGraphics().add(graphic);

        }

    }

    private void drawSomething() {
        GraphicsOverlay graphicsOverlay = new GraphicsOverlay();
        mapView.getGraphicsOverlays().add(graphicsOverlay);

        SimpleMarkerSymbol circleMarker = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, 0xFFFF0000,10);

        SimpleMarkerSymbol triangleMarker = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.TRIANGLE, 0xFF00FF00, 10);

        Point centre = new Point (0,0);
        Point start = new Point (100,0);

        Graphic centreGraphic = new Graphic(centre, circleMarker);
        Graphic startGraphic = new Graphic(start, triangleMarker);

        graphicsOverlay.getGraphics().add(centreGraphic);
        graphicsOverlay.getGraphics().add(startGraphic);

        for (int angle=0; angle<360; angle++) {
            System.out.println("angle " + angle);

            Point pt = new Point(calcXpos(100, angle), calcYpos(100, angle));
            Graphic graphic = new Graphic(pt, triangleMarker);

            graphicsOverlay.getGraphics().add(graphic);
        }
    }

    private double calcXpos(double radius, double angle) {
        double xpos = radius * Math.cos(Math.toRadians(angle));

        return  xpos;
    }

    private double calcYpos(double radius, double angle) {
        double yPos = radius * Math.sin(Math.toRadians(angle));

        return  yPos;
    }

    /**
     * Stops and releases all resources used in application.
     */
    @Override
    public void stop() {

        if (mapView != null) {
            mapView.dispose();
        }
    }
}

