package edu.upenn.cis.citation;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class Entry {

	private final StringProperty table;
	private final StringProperty field;
	private final BooleanProperty show;
	private final StringProperty criteria;
	private final StringProperty or;
	private final BooleanProperty lambda;


	public Entry(String fTable, String fField, boolean fShow,
				 String fCriteria, String fOr, boolean fLambda) {
		super();
		this.table = new SimpleStringProperty(fTable);
		this.field = new SimpleStringProperty(fField);;
		this.show = new SimpleBooleanProperty(fShow);;
		this.criteria = new SimpleStringProperty(fCriteria);;
		this.or = new SimpleStringProperty(fOr);
		this.lambda = new SimpleBooleanProperty(fLambda);
	}

	// Getters
	public String getTable() {
		return table.get();
	}

	public String getField() {
		return field.get();
	}

	public boolean getShow() {
		return show.get();
	}

	public String getCriteria() {
		return criteria.get();
	}

	public String getOr() {
		return or.get();
	}

    public boolean getLambda() {
        return lambda.get();
    }

	public BooleanProperty showProperty() {
        return show;
    }

    public BooleanProperty lambdaProperty() {
	    return lambda;
    }

	// Setters
	public void setTable(String fTable) {
		table.set(fTable);
	}

	public void setField(String fField) {
		field.set(fField);
	}

	public void setShow(boolean fShow) {
		show.set(fShow);
	}

	public void setCriteria(String fCriteria) {
		criteria.set(fCriteria);
	}

	public void setOr(String fOr) {
		or.set(fOr);
	}

	public void setLambda(boolean fLambda) {
        lambda.set(fLambda);
    }

}
