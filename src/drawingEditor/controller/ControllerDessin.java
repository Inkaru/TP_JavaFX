/**
 * @author : Pierre Antoine Cabaret, Nicolas Fouque
 *
 */


package drawingEditor.controller;

import drawingEditor.model.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
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
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ArrayList;
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
    @FXML
    ToggleButton lineButton;
    @FXML
    ToggleButton rainbowButton;
    @FXML
    ToggleButton eraseButton;
    @FXML
    Label heightLabel;
    @FXML
    Label widthLabel;
    @FXML
    Label sizeLabel;
    @FXML
    Spinner<Double> size;

    static int teinte_rainbow=0;
    static int rainbowSpeed=3;

    private static SimpleObjectProperty<Shape> cursor = new SimpleObjectProperty<>();

    private Dessin dessin;

    private static SimpleBooleanProperty heightWidthVisible = new SimpleBooleanProperty();
    private static SimpleBooleanProperty colorPickerVisible = new SimpleBooleanProperty();
    private static SimpleBooleanProperty sizeVisible = new SimpleBooleanProperty();
    private static SimpleBooleanProperty rainbowVisible = new SimpleBooleanProperty();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        colorPicker.setValue(Color.RED);
        xLabel.setVisible(false);
        yLabel.setVisible(false);
        pane.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        pane.setPrefSize(600,600);
        dessin = new DessinImpl();
        updateSizePane();
        pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (recButton.isSelected()) {
                    dessin.ajouterForme(new Rectangle(event.getX(), event.getY(), width.getValue(), height.getValue(), colorPicker.getValue()));
                } else if (ellButton.isSelected()) {
                    dessin.ajouterForme(new Ellipse(event.getX(), event.getY(), width.getValue(), height.getValue(), colorPicker.getValue()));
                }
            }
        });


        pane.setOnMouseDragged(evt -> {
            if (lineButton.isSelected()) {
                Color c = null;
                if (rainbowButton.isSelected()) {
                    c=Color.hsb(teinte_rainbow,1,1,0.7);
                    teinte_rainbow+=rainbowSpeed;
                } else c = colorPicker.getValue();
               dessin.ajouterForme(new Ellipse(evt.getX(), evt.getY(), size.getValue(),size.getValue(), c));
            } else if (eraseButton.isSelected()){
                ArrayList<Forme> list = new ArrayList<>();
                for(Forme f : dessin.getFormes()){
                    if ((Math.abs(f.getPositionX()-evt.getX())<size.getValue()/2)&&(Math.abs(f.getPositionY()-evt.getY())<size.getValue()/2)){
                        list.add(f);
                    }
                }
                for (Forme f : list){
                    dessin.supprimerForme(f);
                }
                Forme forme_curseur = new Ellipse(evt.getX(),evt.getY(),size.getValue(),size.getValue(),new Color(0,0,0,0.2));
                cursor.setValue(createViewShapeFromShape(forme_curseur));
            }

        });

        pane.setOnMouseMoved(evt->{
            //System.out.println(evt.getX() + " " +evt.getY());
            Forme forme_curseur = new Ellipse(0,0,0,0,new Color(0,0,0,0.2));
            if(recButton.isSelected()) {
                forme_curseur = new Rectangle(evt.getX(),evt.getY(),width.getValue(),height.getValue(),new Color(0,0,0,0.2));
            }
            else if(ellButton.isSelected()){
                forme_curseur = new Ellipse(evt.getX(),evt.getY(),width.getValue(),height.getValue(),new Color(0,0,0,0.2));
            }
            else if(lineButton.isSelected() || eraseButton.isSelected()){
                forme_curseur = new Ellipse(evt.getX(),evt.getY(),size.getValue(),size.getValue(),new Color(0,0,0,0.2));
            }
            cursor.setValue(createViewShapeFromShape(forme_curseur));

        });


        cursor.addListener(new ChangeListener<Shape>() {
            @Override
            public void changed(ObservableValue<? extends Shape> observable, Shape oldValue, Shape newValue) {
                pane.getChildren().add(newValue);
                pane.getChildren().remove(oldValue);
            }
        });


        dessin.getFormes().addListener(new ListChangeListener<Forme>() {
            @Override
            public void onChanged(Change<? extends Forme> event) {
                if (event.next()) {
                    if (event.wasAdded()) {
                        List list = event.getAddedSubList();
                        for (Object f : list) {
                            if (f instanceof Forme) {
                                Shape shape = createViewShapeFromShape((Forme) f);
                                pane.getChildren().add(shape);
                            }
                        }
                        updateSizePane();
                    }
                    if (event.wasRemoved()) {

                        List list = event.getRemoved();
                        for (Object f : list) {
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


        colorPicker.visibleProperty().bind(colorPickerVisible);
        widthLabel.visibleProperty().bind(heightWidthVisible);
        width.visibleProperty().bind(heightWidthVisible);
        heightLabel.visibleProperty().bind(heightWidthVisible);
        height.visibleProperty().bind(heightWidthVisible);
        rainbowButton.visibleProperty().bind(rainbowVisible);
        sizeLabel.visibleProperty().bind(sizeVisible);
        size.visibleProperty().bind(sizeVisible);

        groupEditing.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

            @Override
            public void changed(ObservableValue<? extends Toggle> arg0, Toggle arg1, Toggle arg2)
            {
                rainbowButton.selectedProperty().setValue(false);
                heightWidthVisible.setValue(false);
                colorPickerVisible.setValue(false);
                sizeVisible.setValue(false);
                rainbowVisible.setValue(false);

                if (recButton.isSelected() || ellButton.isSelected()){
                    heightWidthVisible.setValue(true);
                    colorPickerVisible.setValue(true);
                } else if (lineButton.isSelected()){
                    colorPickerVisible.setValue(true);
                    sizeVisible.setValue(true);
                    rainbowVisible.setValue(true);
                } else if (eraseButton.isSelected()){
                    sizeVisible.setValue(true);
                }
            }
        });

        rainbowButton.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (rainbowButton.isSelected()) colorPickerVisible.setValue(false);
                else colorPickerVisible.setValue(true);
            }
        });
        recButton.selectedProperty().setValue(true);
    }



    private Shape createViewShapeFromShape(final Forme forme) {
        if (forme instanceof Rectangle) {
            javafx.scene.shape.Rectangle res = new javafx.scene.shape.Rectangle();
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
                    if (delButton.isSelected()) {
                        dessin.supprimerForme(forme);
                    }
                }
            });
            return res;
        } else if (forme instanceof Ellipse) {
            javafx.scene.shape.Ellipse res = new javafx.scene.shape.Ellipse();
            res.radiusXProperty().bind(Bindings.divide(forme.widthProperty(), 2.0));
            res.radiusYProperty().bind(Bindings.divide(forme.heightProperty(), 2.0));
            res.centerXProperty().bindBidirectional(forme.positionXProperty());
            res.centerYProperty().bindBidirectional(forme.positionYProperty());
            res.fillProperty().bindBidirectional(forme.couleurProperty());
            res.setUserData(forme);
            DnDToMoveShape dnd = new DnDToMoveShape(res);
            res.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (delButton.isSelected()) {
                        dessin.supprimerForme((Forme) res.getUserData());
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
        private double x_init;
        private double y_init;

        public DnDToMoveShape(Shape view) {
            Forme f = (Forme) view.getUserData();
            view.setOnMousePressed(evt -> {
                if (moveButton.isSelected()) {
                    pressPositionX = evt.getX();
                    pressPositionY = evt.getY();
                    x_init = f.getPositionX();
                    y_init = f.getPositionY();
                    xLabel.setVisible(true);
                    xLabel.textProperty().bind(Bindings.createStringBinding(() -> "x: "+f.getPositionX(),f.positionXProperty()));
                    yLabel.setVisible(true);
                    yLabel.textProperty().bind(Bindings.createStringBinding(() -> "y: "+f.getPositionY(),f.positionYProperty()));
                    Color c = (Color)f.getCouleur();
                    f.setCouleur(new Color(c.getRed(),c.getGreen(),c.getBlue(),c.getOpacity()/2));
                }
            });
            view.setOnMouseDragged(evt -> {
                if (moveButton.isSelected()) {
                    f.setPosition(x_init + (evt.getX() - pressPositionX), y_init + (evt.getY() - pressPositionY));
                    //utiliser getSceneX et Y ? mais fait un décalage bizarre
                }
                updateSizePane();
            });

            view.setOnMouseReleased(evt -> {
                if (moveButton.isSelected()) {
                    xLabel.setVisible(false);
                    yLabel.setVisible(false);
                    Color c = (Color)f.getCouleur();
                    f.setCouleur(new Color(c.getRed(),c.getGreen(),c.getBlue(),c.getOpacity()*2));
                }
            });
        }
    }





    final StringConverter<Double> conversion = new StringConverter<Double>() {
        @Override
        public String toString(Double object) {
            return String.valueOf((object));
        }

        @Override
        public Double fromString(String string) {
            return (Double.valueOf(string));
        }
    };

    private void updateSizePane() {
        double maxX = 0;
        double maxY = 0;
        double minX = 0; //pane.getPrefWidth(); pour faire des trucs marrants
        double minY = 0; //pane.getPrefHeight();
        for (Object s : dessin.getFormes()) {

            if (s instanceof Rectangle) {
                maxX = Math.max(maxX, ((Forme) s).getPositionX() + ((Forme) s).getWidth());
                maxY = Math.max(maxY, ((Forme) s).getPositionY() + ((Forme) s).getHeight());
                //minX = Math.min(minX, ((Forme) s).getPositionX());
                //minY = Math.min(minY, ((Forme) s).getPositionY());
            }
            if (s instanceof Ellipse) {
                maxX = Math.max(maxX, ((Forme) s).getPositionX() + ((Forme) s).getWidth()/2);
                maxY = Math.max(maxY, ((Forme) s).getPositionY() + ((Forme) s).getHeight()/2);
                //minX = Math.min(minX, ((Forme) s).getPositionX() - ((Forme) s).getWidth()/2);
                //minY = Math.min(minY, ((Forme) s).getPositionY() - ((Forme) s).getHeight()/2);
            }
        }

        double sizeY = Math.max(scrollPane.getPrefHeight(), maxY);
        double sizeX = Math.max(scrollPane.getPrefWidth(), maxX);

        sizeX-=minX;  //on agrandi la taille du pane du décalage nécessaire
        sizeY-=minY;



        for(Forme s : dessin.getFormes()){

                s.deplacer(-minX, -minY); //on décale toutes les formes vers le bas droit -> probleme, on deplace auss // la forme qui update la taille lors du DnD ?
                // peut etre rajouter un argument forme à la fonction update ?

        }
        pane.setPrefSize(sizeX, sizeY);
    }

}
