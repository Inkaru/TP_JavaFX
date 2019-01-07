package drawingEditor.controller;

import drawingEditor.model.*;
import javafx.beans.binding.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;


import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ControllerDessin implements Initializable {

    @FXML
    ToggleButton recButton;
    @FXML
    ToggleButton delButton;
    @FXML
    ToggleButton ellButton;
    @FXML
    ToggleButton moveButton;
    @FXML
    Spinner<Double> width;
    @FXML
    Spinner<Double> height;
    @FXML
    ColorPicker colorPicker;
    @FXML
    ToggleGroup groupEditing;
    @FXML
    Label xLabel;
    @FXML
    Label yLabel;
    @FXML
    Pane pane;
    @FXML
    ScrollPane scrollPane;

    private Dessin dessin;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        recButton.setSelected(true);
        colorPicker.setValue(Color.RED);
        xLabel.setVisible(false);
        yLabel.setVisible(false);
        //scrollPane.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
        pane.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

        System.out.println(scrollPane.getWidth()+" "+scrollPane.getWidth());
        dessin = new DessinImpl();
        pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (groupEditing.getSelectedToggle().equals(recButton)) {
                    Rectangle rect = new Rectangle(event.getX(), event.getY(), width.getValue(), height.getValue(), colorPicker.getValue());
                    dessin.ajouterForme(rect);
                } else if (groupEditing.getSelectedToggle().equals(ellButton)) {
                    dessin.ajouterForme(new Ellipse(event.getX(), event.getY(), width.getValue(), height.getValue(), colorPicker.getValue()));
                }
            }
        });
        dessin.getFormes().addListener(new ListChangeListener<Forme>() {
            @Override
            public void onChanged(Change<? extends Forme> event) {
                if (event.next()){
                    if (event.wasAdded()) {
                        //updateSizePane();
                        List list = event.getAddedSubList();
                        for (Object f : list){
                            if (f instanceof Forme) {
                                Shape shape = createViewShapeFromShape((Forme)f);
                                pane.getChildren().add(shape);
                            }
                        }
                        updateSizePane();
                    }
                    if (event.wasRemoved()) {

                        List list = event.getRemoved();
                        for (Object f : list){
                            if (f instanceof Forme) {
                                for (Node s : pane.getChildren()) {
                                    if (s.getUserData().equals(f)) {
                                        pane.getChildren().remove(s);
                                        break;
                                    }
                                }

                            }
                        }
                        updateSizePane();
                    }
                }
            }
        });
        
        updateSizePane();


    }

    private Shape createViewShapeFromShape(final Forme forme){
        if (forme instanceof Rectangle){
            javafx.scene.shape.Rectangle res = new javafx.scene.shape.Rectangle(/*forme.getPositionX(),forme.getPositionY(),forme.getWidth(),forme.getHeight()*/);
            res.heightProperty().bindBidirectional(forme.heightProperty());
            res.widthProperty().bindBidirectional(forme.widthProperty());
            res.xProperty().bindBidirectional(forme.positionXProperty());
            res.yProperty().bindBidirectional(forme.positionYProperty());
            res.fillProperty().bindBidirectional(forme.couleurProperty());
            res.setUserData(forme);
            DnDToMoveShape dnd = new DnDToMoveShape(res);
            res.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (groupEditing.getSelectedToggle().equals(delButton)) {
                        System.out.println("Supression rect");
                        dessin.supprimerForme(forme);
                    }
                }
            });
            return res;
        } else if (forme instanceof Ellipse) {
            javafx.scene.shape.Ellipse res = new javafx.scene.shape.Ellipse(/*forme.getPositionX(),forme.getPositionY(),forme.getWidth(),forme.getHeight()*/);
            res.radiusXProperty().bind(Bindings.divide(forme.widthProperty(),2.0));
            res.radiusYProperty().bind(Bindings.divide(forme.heightProperty(),2.0));
            res.centerXProperty().bindBidirectional(forme.positionXProperty());
            res.centerYProperty().bindBidirectional(forme.positionYProperty());
            res.fillProperty().bindBidirectional(forme.couleurProperty());
            res.setUserData(forme);
            DnDToMoveShape dnd = new DnDToMoveShape(res);
            res.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (groupEditing.getSelectedToggle().equals(delButton)) {
                        //System.out.println("Supression ell");
                        dessin.supprimerForme((Forme)res.getUserData());
                    }
                }
            });
            return res;
        }
        return null;
    }

    private class DnDToMoveShape {
        private double pressPositionX;
        private double pressPositionY;
        //private Shape view;
        private double x_init;
        private double y_init;

        public DnDToMoveShape(Shape view){

                view.setOnMousePressed(evt -> {
                    if (moveButton.isSelected()) {
                        pressPositionX = evt.getX();
                        pressPositionY = evt.getY();
                        x_init = ((Forme) view.getUserData()).getPositionX();
                        y_init = ((Forme) view.getUserData()).getPositionY();
                    }
                });
                view.setOnMouseDragged(evt -> {
                    if (moveButton.isSelected()) {
                        ((Forme) view.getUserData()).setPosition(x_init + (evt.getX() - pressPositionX), y_init + (evt.getY() - pressPositionY));
                    }
                    updateSizePane();
                });
                view.setOnMouseReleased(evt -> {

                });

        }
    }

    private void updateSizePane() {
        double maxX = 0;
        double maxY = 0;
        for(Object s : dessin.getFormes()){
            System.out.println(s.getClass());
            if (s instanceof Forme){
                maxX = Math.max(maxX,((Forme)s).getPositionX()+((Forme)s).getWidth());
                maxY = Math.max(maxY,((Forme)s).getPositionY()+((Forme)s).getHeight());
            }
        }
        double sizeY = Math.max(scrollPane.getPrefHeight(),maxY);
        double sizeX = Math.max(scrollPane.getPrefWidth(),maxX);
        pane.setPrefSize(sizeX,sizeY);
        System.out.println("update" + pane.getPrefHeight());
        System.out.println(maxX);
       // pane.setBackground(new Background(new BackgroundFill(Color.WHITE,,null)));

        /**
        pane.setMaxHeight(Math.max(scrollPane.getMaxHeight(),maxY));
        pane.setMaxWidth(Math.max(scrollPane.getMaxWidth(),maxX));
         **/

    }

}
