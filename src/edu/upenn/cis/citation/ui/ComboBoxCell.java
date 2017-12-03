package edu.upenn.cis.citation.ui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Vector;
import edu.upenn.cis.citation.Pre_processing.populate_db;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning1_full_test_opt;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;

class ComboBoxCell extends TableCell<ObservableList, String> {

	private ComboBox<String> comboBox;

	public ComboBoxCell() {
		comboBox = new ComboBox<>();
	}

	@Override
	public void startEdit() {
		if (!isEmpty()) {
			super.startEdit();
			
			ObservableList<ObservableList> table_view = getTableView().getItems();
			
			ObservableList list = getTableView().getItems().get(getIndex());
			
			Vector<String> names = new Vector<String>();
			
			for(int i = 0; i<list.size(); i++)
			{
			  String name = (String) list.get(i);
			  
			  names.add(name);
			}
			
			HashSet<String> citations = new HashSet<String>();
			
			
            try {
              Class.forName("org.postgresql.Driver");
              
              Connection conn;
              conn = DriverManager.getConnection(populate_db.db_url, populate_db.usr_name, populate_db.passwd);
              
              PreparedStatement st = null;
              
              citations = Tuple_reasoning1_full_test_opt.tuple_gen_citation_per_tuple(names, conn, st);

              conn.close();
              
            } catch (Exception e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
			
			
			
			ObservableList<String> temp = FXCollections.observableArrayList();
			for (String s : citations) {
				temp.add(s);
			}
			comboBox.setItems(temp);
			comboBox.getSelectionModel().select(getItem());
			comboBox.focusedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
					if (!newValue) {
						commitEdit(comboBox.getSelectionModel().getSelectedItem());
					}
				}
			});

			setText(null);
			setGraphic(comboBox);
		}
	}

	@Override
	public void cancelEdit() {
		super.cancelEdit();

		setText(getItem());
		setGraphic(null);
	}

	@Override
	public void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);

		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			if (isEditing()) {
				setText(null);
				setGraphic(comboBox);
			} else {
				setText(getItem());
				setGraphic(null);
			}
		}
	}

}