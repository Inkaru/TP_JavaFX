package drawingEditor.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Paint;

public abstract class FormeImpl implements Forme{
    private DoubleProperty x;
    private DoubleProperty y;
    private DoubleProperty largeur;
    private DoubleProperty hauteur;
    private ObjectProperty<Paint> couleur;

    public FormeImpl(double x, double y, double hauteur, double largeur, Paint col){
        this.x = new SimpleDoubleProperty(x);
        this.y = new SimpleDoubleProperty(y);
        this.largeur = new SimpleDoubleProperty(largeur);
        this.hauteur = new SimpleDoubleProperty(hauteur);
        this.couleur = new SimpleObjectProperty<>(col);
    }

    @Override
    public Paint getCouleur() {
        return couleur.getValue();
    }

    @Override
    public double getPositionX() {
        return x.getValue();
    }

    @Override
    public double getPositionY() {
        return y.getValue();
    }

    @Override
    public double getWidth() {
        return largeur.getValue();
    }

    @Override
    public double getHeight() {
        return hauteur.getValue();
    }

    @Override
    public void setCouleur(Paint col) {
        couleur.setValue(col);
    }

    @Override
    public void setPosition(double x, double y) {
        this.x.setValue(x);
        this.y.setValue(y);
    }

    @Override
    public void deplacer(double tx, double ty) {
        this.x.setValue(tx+this.x.getValue());
        this.y.setValue(ty+this.y.getValue());
    }

    @Override
    public ObjectProperty<Paint> couleurProperty() {
        return couleur;
    }

    @Override
    public DoubleProperty positionXProperty() {
        return (x);
    }

    @Override
    public DoubleProperty positionYProperty() {
        return (y);
    }

    @Override
    public DoubleProperty widthProperty() {
        return (largeur);
    }

    @Override
    public DoubleProperty heightProperty() {
        return (hauteur);
    }
}
