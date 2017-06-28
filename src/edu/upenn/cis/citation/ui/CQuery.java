package edu.upenn.cis.citation.ui;



import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class CQuery {

	private final StringProperty name = new SimpleStringProperty();
	private final StringProperty block = new SimpleStringProperty();
	
	public CQuery(String name, String block) {
		setName(name);
		setBlock(block);
	}
	
	public final StringProperty nameProperty() {
		return this.name;
	}
	
	public final StringProperty blockProperty() {
		return this.block;
	}
	
	public final String getName() {
		return this.nameProperty().get();
	}
	
	public String getBlock() {
		return this.blockProperty().get();
	}
	
	public final void setName (final String name) {
		this.nameProperty().set(name);
	}
	
	public final void setBlock (final String block) {
		this.blockProperty().set(block);
	}

}
