package edu.upenn.cis.citation.ui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
			ObservableList list = getTableView().getItems().get(getIndex());
			comboBox.setItems((ObservableList<String>) list.get(list.size()-1));
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

		setText((String) getItem());
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