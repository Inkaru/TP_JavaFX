package drawingEditor.model;

import javafx.collections.ObservableList;

public interface Dessin {
	void ajouterForme(final Forme shape);

	void supprimerForme(final Forme shape);

	ObservableList<Forme> getFormes();
}