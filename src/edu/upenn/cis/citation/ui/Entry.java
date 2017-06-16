package edu.upenn.cis.citation.ui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class Entry {

	private final StringProperty table;
	private final StringProperty field;
	private final BooleanProperty show;
	private final StringProperty criteria;
	private final StringProperty join;
	private final BooleanProperty lambda;


	public Entry(String fTable, String fField, boolean fShow,
				 String fCriteria, String fJoin, boolean fLambda) {
		super();
		this.table = new SimpleStringProperty(fTable);
		this.field = new SimpleStringProperty(fField);
        this.show = new SimpleBooleanProperty(fShow);
        this.criteria = new SimpleStringProperty(fCriteria);
        this.join = new SimpleStringProperty(fJoin);
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

	public String getJoin() {
		return join.get();
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
		// eg. =3, >4
		criteria.set(fCriteria);
	}

	public void setJoin(String fJoin) {
		// eg. =table.field 
		join.set(fJoin);
	}

	public void setLambda(boolean fLambda) {
        lambda.set(fLambda);
    }

}
