package drawingEditor.model;

import drawingEditor.drawingEditor;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.paint.Paint;

public interface Forme {
	Paint getCouleur();
	
	double getPositionX();
	
	double getPositionY();
	
	double getWidth();
	
	double getHeight();
	
	void setCouleur(final Paint col);
	
	void setPosition(final double x, final double y);
	
	void deplacer(final double tx, final double ty);
	
//	void setWidth(final double w);
	
//	void setHeight(final double h);
	
	ObjectProperty<Paint> couleurProperty();
	
	DoubleProperty positionXProperty();
	
	DoubleProperty positionYProperty();
	
	DoubleProperty widthProperty();
	
	DoubleProperty heightProperty();
}
