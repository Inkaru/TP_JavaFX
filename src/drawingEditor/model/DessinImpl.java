package drawingEditor.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DessinImpl implements Dessin {
    private ObservableList<Forme> formes;

    public DessinImpl(){
        formes = FXCollections.observableArrayList();
    }

    @Override
    public void ajouterForme(Forme shape) {
        formes.add(shape);
    }

    @Override
    public void supprimerForme(Forme shape) {
        formes.remove(shape);
    }

    @Override
    public ObservableList<Forme> getFormes() {
        return formes;
    }
}
