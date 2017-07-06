package edu.upenn.cis.citation.ui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class GQuery {

	private final StringProperty id = new SimpleStringProperty();
	private final StringProperty content = new SimpleStringProperty();
	
	public GQuery(String id, String content) {
		setId(id);
		setContent(content);
	}
	
	public final StringProperty idProperty() {
		return this.id;
	}
	
	public final StringProperty contentProperty() {
		return this.content;
	}
	
	public final String getId() {
		return this.idProperty().get();
	}
	
	public String getContentk() {
		return this.contentProperty().get();
	}
	
	public final void setId (final String id) {
		this.idProperty().set(id);
	}
	
	public final void setContent (final String content) {
		this.contentProperty().set(content);
	}

}

