package edu.upenn.cis.citation.ui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import org.controlsfx.control.table.TableRowExpanderColumn;

import edu.upenn.cis.citation.citation_view.citation_view_vector;
import edu.upenn.cis.citation.dao.Database;
import edu.upenn.cis.citation.reasoning.Tuple_reasoning2;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;

public class QBEApp extends Application {

	private Scene qbeScene;
	private Scene loginScene;
	private Scene dbaScene;
	private Stage stage;
	// Table view
	private TableView<Entry> tableView = new TableView<Entry>();
	private TableView<ObservableList> dataView = new TableView();
	private ObservableList dataViewList = FXCollections.observableArrayList();
	// private ObservableList lambdaData = FXCollections.observableArrayList();
	private HBox hBoxLambda = new HBox();
	// Table tuples data
	private final ObservableList<Entry> data = FXCollections.observableArrayList();
	List<String> lambdas = new ArrayList<>();

	private GridPane grid;
	private TreeView<TreeNode> samplesTreeView;
	private TreeItem<TreeNode> root;
	List<TreeItem<TreeNode>> removedTreeItems = new ArrayList<>();

	private ObservableList<ObservableList> dataTable = FXCollections.observableArrayList();
	List<List<String>> lambdasAll = new ArrayList<>();
	String lambdaSQL = null;
	String datalog = null;
	List<Integer> lambdaIndex = new ArrayList<>();
	Vector<Integer> ids = new Vector<>();

	VBox vboxDatalog;
	HBox hBoxPrevNext, hBoxGenCitation;
	//
	SplitPane splitPaneQbe;
	
	TableRowExpanderColumn<Entry> expanderColumn;
	TableColumn<Entry, String> tableColumn;
	TableColumn<Entry, String> fieldColumn;
	TableColumn<Entry, Boolean> showColumn;
	TableColumn<Entry, String> criteraColumn;
	TableColumn<Entry, Boolean> lambdaColumn;
	
	Vector<Vector<citation_view_vector>> c_views = null;
	boolean isDba;

	/**
	 * GUI Main Method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		this.stage = stage;
		buildLoginScene();
		buildQbeScene();
		buildDbaScene();
		stage.setScene(loginScene);
		stage.setMinWidth(300);
		stage.setMinHeight(300);
		// set width / height values to be 75% of users screen resolution
		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		stage.setWidth(screenBounds.getWidth() * 0.75);
		stage.setHeight(screenBounds.getHeight() * 0.75);
		stage.show();
		samplesTreeView.requestFocus();
	}

	private void buildDbaScene() {
		// Adding HBox
		HBox hb = new HBox();
		hb.setPadding(new Insets(20, 20, 20, 30));
		DropShadow dropShadow = new DropShadow();
		dropShadow.setOffsetX(5);
		dropShadow.setOffsetY(5);
		Text text = new Text("Citation Management");
		text.setId("text");
		text.setFont(Font.font("Courier New", FontWeight.BOLD, 28));
		text.setEffect(dropShadow);
		hb.getChildren().add(text);

		// Reflection for gridPane
		BorderPane bp = new BorderPane();
		bp.setId("bp");
		bp.setPadding(new Insets(10, 50, 50, 50));
		// GridPane data views
		GridPane gridPaneDataViews = new GridPane();
		gridPaneDataViews.setPadding(new Insets(20, 20, 20, 20));
		gridPaneDataViews.setAlignment(Pos.CENTER);
		gridPaneDataViews.setHgap(5);
		gridPaneDataViews.setVgap(5);
		Label lableDataviews = new Label("Data Views");
		// ListView Data Views
		ListView<String> listViewDataViews = new ListView<>();
		ObservableList<String> listDataViews = FXCollections.observableArrayList(Database.getDataViews());
		System.out.println(Database.getDataViews());
		listViewDataViews.setItems(listDataViews);
		Button buttonAddDataView = new Button("Add Data View");
		buttonAddDataView.setOnAction(event -> {
			dbaMode(true);
			this.stage.setScene(qbeScene);
		});
		Button buttonDeleteDataView = new Button("Delete Data View");
		buttonDeleteDataView.setOnAction(event -> {
			dbaMode(true);
			this.stage.setScene(qbeScene);
		});
		gridPaneDataViews.add(lableDataviews, 0, 0);
		gridPaneDataViews.add(listViewDataViews, 0, 1, 2, 1);
		gridPaneDataViews.add(buttonAddDataView, 0, 2);
		gridPaneDataViews.add(buttonDeleteDataView, 1, 2);
		Reflection r1 = new Reflection();
		r1.setFraction(0.7f);
		gridPaneDataViews.setEffect(r1);
		gridPaneDataViews.setId("bproot");
		
		
		// GridPane citation views
		GridPane gridPaneCitationViews = new GridPane();
		gridPaneCitationViews.setPadding(new Insets(20, 20, 20, 20));
		gridPaneCitationViews.setAlignment(Pos.CENTER);
		gridPaneCitationViews.setHgap(5);
		gridPaneCitationViews.setVgap(5);
		Label lableCitationViews = new Label("Citation Views");
		// ListView Citation Views
		ListView<String> listViewCitationView = new ListView<>();
		ObservableList<String> listCitationViews = FXCollections.observableArrayList(Database.getDataViews());
		listViewCitationView.setItems(listCitationViews);
		Button buttonAddCitationView = new Button("Add Citation View");
		buttonAddCitationView.setOnAction(event -> {
			dbaMode(true);
			this.stage.setScene(qbeScene);
		});
		Button buttonDeleteCitationView = new Button("Delete Citation View");
		buttonDeleteCitationView.setOnAction(event -> {
			dbaMode(true);
			this.stage.setScene(qbeScene);
		});
		GridPane.setHgrow(listViewCitationView, Priority.ALWAYS);
		GridPane.setVgrow(listViewCitationView, Priority.ALWAYS);
		gridPaneCitationViews.setAlignment(Pos.CENTER);
		gridPaneCitationViews.setPadding(new Insets(10, 10, 10, 10));
		gridPaneCitationViews.add(lableCitationViews, 0, 0);
		gridPaneCitationViews.add(listViewCitationView, 0, 1, 2, 1);
		gridPaneCitationViews.add(buttonAddCitationView, 0, 2);
		gridPaneCitationViews.add(buttonDeleteCitationView, 1, 2);
		Reflection r2 = new Reflection();
		r2.setFraction(0.7f);
		gridPaneCitationViews.setEffect(r2);
		gridPaneCitationViews.setId("bproot");

		SplitPane splitPane = new SplitPane();
		splitPane.setId("root");
		splitPane.getItems().add(gridPaneDataViews);
		splitPane.getItems().add(gridPaneCitationViews);
		// Add HBox and GridPane layout to BorderPane Layout
		bp.setTop(hb);
		bp.setCenter(splitPane);
		dbaScene = new Scene(bp);
		dbaScene.getStylesheets().add(QBEApp.class.getResource("style.css").toExternalForm());
	}

	private void buildLoginScene() {		
		GridPane gp = new GridPane();
		gp.setPadding(new Insets(10, 50, 50, 50));
		// Adding HBox
		HBox hb = new HBox();
		hb.setPadding(new Insets(20, 20, 20, 30));
		HBox hbox = new HBox();
		// Adding GridPane
		GridPane gridPane = new GridPane();
		GridPane gridPane2 = new GridPane();
		gridPane2.add(new Label("IUPHAR/BPS"), 0, 0);
		Hyperlink l1 = new Hyperlink();
		l1.setText("Targets");
		l1.setOnAction(Event -> {
			try {
				Desktop.getDesktop().browse(new URL("http://www.guidetopharmacology.org/targets.jsp").toURI());
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		});
		Hyperlink l2 = new Hyperlink();
		l2.setText("Ligands");
		l2.setOnAction(Event -> {
			try {
				Desktop.getDesktop().browse(new URL("http://www.guidetopharmacology.org/GRAC/LigandListForward?database=all").toURI());
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		});
		Hyperlink l3 = new Hyperlink();
		l3.setText("Advanced Search");
		l3.setOnAction(Event -> {
			try {
				Desktop.getDesktop().browse(new URL("http://www.guidetopharmacology.org/targets.jsp").toURI());
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		});
		gridPane2.add(l1, 0, 1);
		gridPane2.add(l2, 0, 2);
		gridPane2.add(l3, 0, 3);
		Button citeButton = new Button("Cite a dataset");
		citeButton.setOnAction(event -> {
			dbaMode(false);
			this.stage.setScene(qbeScene);
		});
		gridPane2.add(citeButton, 0, 4);
		gridPane.setMaxSize(300, 300);
		gridPane.setMinSize(300, 300);
		gridPane2.setMaxSize(300, 300);
		gridPane2.setMinSize(300, 300);
		HBox.setMargin(gridPane, new Insets(20, 20, 0, 20));
		HBox.setMargin(gridPane2, new Insets(20, 20, 0, 20));
		hbox.setAlignment(Pos.CENTER);
		gp.setAlignment(Pos.CENTER);
		gridPane.setPadding(new Insets(20, 30, 20, 30));
		gridPane2.setPadding(new Insets(20, 30, 20, 30));
		gridPane.setHgap(10);
		gridPane.setVgap(10);
		GridPane.setHgrow(gridPane, Priority.ALWAYS);
		GridPane.setVgrow(gridPane, Priority.ALWAYS);
		gridPane2.setHgap(10);
		gridPane2.setVgap(10);
		GridPane.setHgrow(gridPane2, Priority.ALWAYS);
		GridPane.setVgrow(gridPane2, Priority.ALWAYS);
		// Implementing Nodes for GridPane
		Label lblUserName = new Label("Username");
		final TextField txtUserName = new TextField();
		Label lblPassword = new Label("Password");
		final PasswordField pf = new PasswordField();
		Button btnLogin = new Button("Login");
		final Label lblMessage = new Label();
		final Label dbsMessage = new Label("Admin Login");
		// Adding Nodes to GridPane layout
		gridPane.add(dbsMessage, 0, 0);
		gridPane.add(lblUserName, 0, 1);
		gridPane.add(txtUserName, 1, 1);
		gridPane.add(lblPassword, 0, 2);
		gridPane.add(pf, 1, 2);
		gridPane.add(btnLogin, 0, 3);
		gridPane.add(lblMessage, 1, 3);
		// Reflection for gridPane
		Reflection r = new Reflection();
		r.setFraction(0.7f);
		gridPane.setEffect(r);
		Reflection r2 = new Reflection();
		r2.setFraction(0.7f);
		gridPane2.setEffect(r2);
		// DropShadow effect
		DropShadow dropShadow = new DropShadow();
		dropShadow.setOffsetX(5);
		dropShadow.setOffsetY(5);
		// Adding text and DropShadow effect to it
		Text text = new Text("Data Citation System");
		text.setTextAlignment(TextAlignment.CENTER);
		text.setFont(Font.font("Courier New", FontWeight.BOLD, 38));
		text.setEffect(dropShadow);
		// Adding text to HBox
		hb.setAlignment(Pos.CENTER);
		hb.getChildren().add(text);
		// Add ID's to Nodes
		gp.setId("bp");
		gridPane.setId("bproot");
		gridPane2.setId("bproot");
		btnLogin.setId("btnLogin");
		text.setId("text");
		// Action for btnLogin
		btnLogin.setOnAction(event -> {
			String checkUser = txtUserName.getText().toString();
			String checkPw = pf.getText().toString();
			if (checkUser.equals("u") && checkPw.equals("p")) {
				lblMessage.setText("Congratulations!");
				lblMessage.setTextFill(Color.GREEN);
			} else {
				lblMessage.setText("Incorrect user or pw.");
				lblMessage.setTextFill(Color.RED);
			}
			txtUserName.setText("");
			pf.setText("");
			this.stage.setScene(dbaScene);
		});
		// Add HBox and GridPane layout to BorderPane Layout
		gp.add(hb, 0, 0);
		hbox.getChildren().addAll(gridPane, gridPane2);
		gp.add(hbox, 0, 1);
		// LOGIN SCENE
		loginScene = new Scene(gp);
		loginScene.getStylesheets().add(QBEApp.class.getResource("style.css").toExternalForm());
	}

	private void buildQbeScene() {
		// QBE SCENE
		buildSampleTree(null, true);
		grid = new GridPane();
		grid.setPadding(new Insets(0, 10, 10, 10));
		grid.setHgap(10);
		grid.setVgap(10);

		final Label label_0 = new Label("Query Builder");
		label_0.setId("prompt-text");
		GridPane.setHgrow(label_0, Priority.ALWAYS);
		grid.add(label_0, 1, 0, 2, 1);

		// Search box
		final TextField searchBox = new TextField();
		searchBox.setMinWidth(200);
		searchBox.setPromptText("Search Table");
		searchBox.textProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable o) {
				buildSampleTree(searchBox.getText(), false);
			}
		});
		GridPane.setMargin(searchBox, new Insets(5, 0, 0, 0));
		GridPane.setHgrow(searchBox, Priority.NEVER);
		grid.add(searchBox, 0, 0);

		// TreeView
		samplesTreeView = new TreeView<>(root);
		samplesTreeView.setShowRoot(false);
		samplesTreeView.getStyleClass().add("samples-tree");
		samplesTreeView.setMinWidth(200);
		samplesTreeView.setCellFactory(new Callback<TreeView<TreeNode>, TreeCell<TreeNode>>() {
			@Override
			public TreeCell<TreeNode> call(TreeView<TreeNode> param) {
				return new TreeCell<TreeNode>() {
					@Override
					protected void updateItem(TreeNode item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setText("");
						} else {
							setText(item.getName());
						}
					}
				};
			}
		});
		samplesTreeView.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<TreeItem<TreeNode>>() {
					@Override
					public void changed(ObservableValue<? extends TreeItem<TreeNode>> observable,
							TreeItem<TreeNode> oldValue, TreeItem<TreeNode> node) {
						if (node == null) return;
						String table = node.getParent().getValue().getName();
						if (node.getParent() == samplesTreeView.getRoot()) {
							if (node.getChildren().size() > 0) return;
							for (String attr : Database.getAttrList(node.getValue().getName())) {
								node.getChildren().add(new TreeItem<TreeNode>(new TreeNode(attr)));
					        }
							return;
						} else {
							String field = node.getValue().getName();
							data.add(new Entry(table, field, true, "", "", false));
						}
						tableView.refresh();
					}
				});
		GridPane.setVgrow(samplesTreeView, Priority.ALWAYS);
		grid.add(samplesTreeView, 0, 1, 1, 4);

		// Pane
		splitPaneQbe = new SplitPane();
		GridPane.setHgrow(splitPaneQbe, Priority.ALWAYS);
		GridPane.setVgrow(splitPaneQbe, Priority.ALWAYS);
		splitPaneQbe.getItems().add(tableView);
		final HBox hBox = new HBox();
		hBox.setAlignment(Pos.CENTER_RIGHT);
		Label datalogLabel = new Label("Datalog Generated:");
		TextArea datalogTextArea = new TextArea();
		datalogTextArea.setWrapText(true);
		final Button runButton = new Button("Run");
		final Button clearButton = new Button("Clear");
		final Button backButton = new Button("Go back");
		runButton.setId("bevel-grey");
		clearButton.setId("bevel-grey");
		backButton.setId("bevel-grey");
		runButton.setOnAction(e -> {
			if (data.isEmpty())
				return;
			List<Entry> list = new ArrayList<>();
			list.addAll(data);
			datalogTextArea.appendText(Util.convertToDatalog(list) + "\n");
			generateDataView(list);
		});
		clearButton.setOnAction(e -> {
			data.clear();
			datalogTextArea.clear();
		});
		backButton.setOnAction(e -> {
			this.stage.setScene(loginScene);
		});
		HBox.setMargin(runButton, new Insets(0, 5, 0, 5));
		HBox.setMargin(clearButton, new Insets(0, 5, 0, 5));
		HBox.setMargin(backButton, new Insets(0, 5, 0, 5));
		hBox.getChildren().addAll(runButton, clearButton, backButton);
		vboxDatalog = new VBox();
		vboxDatalog.setSpacing(5);
		vboxDatalog.setPadding(new Insets(10, 0, 0, 10));
		Button btDataView = new Button("Create Data View");
		btDataView.setOnAction(e -> {
			// TODO
		});
		btDataView.setId("buttonGen");
		TextField tfDataView = new TextField();
		Label viewNameLabel = new Label("View Name:");
		vboxDatalog.getChildren().addAll(datalogLabel, datalogTextArea, viewNameLabel, tfDataView, btDataView);
		vboxDatalog.setPadding(new Insets(25, 25, 25, 25));
		splitPaneQbe.getItems().add(vboxDatalog);
		grid.add(hBox, 2, 0);
		grid.add(splitPaneQbe, 1, 1, 2, 1);

		// Table View
		tableView.setEditable(true);
		tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		expanderColumn = new TableRowExpanderColumn<>(this::createEditor);
		expanderColumn.setMaxWidth(30);
		expanderColumn.setMinWidth(30);
		tableColumn = new TableColumn<>("Table");
		fieldColumn = new TableColumn<>("Field");
		showColumn = new TableColumn<>("Show");
		criteraColumn = new TableColumn<>("Criteria");
		lambdaColumn = new TableColumn<>("Lambda");
		tableColumn.setCellValueFactory(new PropertyValueFactory<Entry, String>("table"));
		fieldColumn.setCellValueFactory(new PropertyValueFactory<Entry, String>("field"));
		showColumn.setCellValueFactory(new PropertyValueFactory<Entry, Boolean>("show"));
		showColumn.setCellFactory(CheckBoxTableCell.forTableColumn(showColumn));
		showColumn.setEditable(true);
		criteraColumn.setCellValueFactory(new PropertyValueFactory<Entry, String>("criteria"));
		criteraColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		lambdaColumn.setCellValueFactory(new PropertyValueFactory<Entry, Boolean>("lambda"));
		lambdaColumn.setCellFactory(CheckBoxTableCell.forTableColumn(lambdaColumn));
		lambdaColumn.setEditable(true);

		tableView.setItems(data);
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		// Label data preview
		final Label label_1 = new Label("Data Preview");
		label_1.setId("prompt-text");
		GridPane.setHgrow(label_1, Priority.ALWAYS);
		grid.add(label_1, 1, 2);

		GridPane.setVgrow(dataView, Priority.ALWAYS);
		GridPane.setHgrow(dataView, Priority.ALWAYS);
		dataView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		dataView.setItems(dataViewList);
		grid.add(dataView, 1, 3, 2, 1);

		hBoxLambda.getChildren().add(new Label("Lambda Terms:  "));
		hBoxLambda.getStyleClass().add("hBoxLambda");
		GridPane.setHgrow(hBoxLambda, Priority.ALWAYS);
		grid.add(hBoxLambda, 1, 4);

		hBoxPrevNext = new HBox();
		hBoxPrevNext.setAlignment(Pos.CENTER_RIGHT);
		final Button prevButton = new Button("Prev");
		final Button nextButton = new Button("Next");
		nextButton.setOnAction(e -> {
			next();
			setDataView();
		});
		hBoxPrevNext.getChildren().addAll(prevButton, nextButton);
		GridPane.setHgrow(hBoxPrevNext, Priority.ALWAYS);
		grid.add(hBoxPrevNext, 2, 4);

		hBoxGenCitation = new HBox();
		hBoxGenCitation.setAlignment(Pos.CENTER_RIGHT);
		Button genButton = new Button("Generate Citation");
		genButton.setOnAction(e -> {
			int index = 0;
			ObservableList<Integer> indices = dataView.getSelectionModel().getSelectedIndices();
			System.out.println("[DEBUG] index = " + index);
			ids.clear();
			ids.addAll(indices);
			Vector<String> subset_agg_citations = new Vector<>();
			try {
				subset_agg_citations = Tuple_reasoning2.tuple_gen_agg_citations(c_views, ids);
			} catch (ClassNotFoundException | SQLException e1) {
				e1.printStackTrace();
			}
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Citation Generated");
			alert.setHeaderText(null);
			StringBuilder stringBuilder = new StringBuilder();
			for (int i = 0; i < subset_agg_citations.size(); i++) {
				stringBuilder.append("Citation #" + (i+1) + ":  ");
				stringBuilder.append(subset_agg_citations.get(i) + "\n\n");
			}
			alert.setContentText(stringBuilder.toString());
			alert.showAndWait();
		});
		genButton.setId("buttonGen");
		grid.add(hBoxGenCitation, 2, 2);
		// ===========================================================================
		qbeScene = new Scene(grid);
		qbeScene.getStylesheets().add(QBEApp.class.getResource("style.css").toExternalForm());
	}

	private GridPane createEditor(TableRowExpanderColumn.TableRowDataFeatures<Entry> param) {
		GridPane editor = new GridPane();
		editor.setPadding(new Insets(10));
		editor.setHgap(10);
		editor.setVgap(5);
		Entry entry = param.getValue();
		TextField criteriaField = new TextField(entry.getCriteria());
		TextField orFiled = new TextField(entry.getOr());
		editor.addRow(0, new Label("Criteria"), criteriaField);
		editor.addRow(1, new Label("Or"), orFiled);
		Button saveButton = new Button("Save");
		saveButton.setOnAction(event -> {
			entry.setCriteria(criteriaField.getText());
			entry.setOr(orFiled.getText());
			param.toggleExpanded();
		});
		Button cancelButton = new Button("Cancel");
		cancelButton.setOnAction(event -> param.toggleExpanded());
		Button deleteButton = new Button("Delete Row");
		deleteButton.setOnAction(event -> {
			data.remove(entry);
			tableView.refresh();
		});
		editor.addRow(2, saveButton, cancelButton, deleteButton);
		return editor;
	}

	private void buildSampleTree(String searchText, boolean rebuild) {
		if (rebuild) {
			removedTreeItems.clear();
			root = new TreeItem<TreeNode>(new TreeNode("Tables"));
			root.setExpanded(true);
			for (String table : Database.getTableList()) {
				root.getChildren().add(TreeNode.createTreeItem(table));
			}
		}
		if (searchText != null) {
			pruneSampleTree(root, searchText);
			samplesTreeView.setRoot(root);
		}
		sort(root, Comparator.comparing(o -> o.getValue().getName()));
	}

	private boolean pruneSampleTree(TreeItem<TreeNode> treeItem, String searchText) {
		if (searchText == null)
			return true;
		treeItem.getChildren().addAll(removedTreeItems);
		removedTreeItems.clear();
		for (TreeItem<TreeNode> child : treeItem.getChildren()) {
			if (!child.getValue().getName().toUpperCase().startsWith(searchText.toUpperCase())) {
				removedTreeItems.add(child);
			}
		}
		treeItem.getChildren().removeAll(removedTreeItems);
		return !removedTreeItems.isEmpty();
	}

	private void sort(TreeItem<TreeNode> node, Comparator<TreeItem<TreeNode>> comparator) {
		node.getChildren().sort(comparator);
		for (TreeItem<TreeNode> child : node.getChildren()) {
			sort(child, comparator);
		}
	}

	private void generateDataView(List<Entry> list) {
		lambdasAll.clear();
		lambdaIndex.clear();
		lambdaSQL = Util.convertToSQLWithLambda(list);
		datalog = Util.convertToDatalog(list);
		System.out.println("[DEBUG] lambdaSQL: " + lambdaSQL);
		lambdas = Util.getLambda(list);
		for (String lambda : lambdas) {
			System.out.println(lambda);
			String table = lambda.substring(0, lambda.indexOf('.'));
			String field = lambda.substring(lambda.indexOf('.') + 1);
			List<String> temp = Database.getDistincts(table, field);
			lambdasAll.add(temp);
			lambdaIndex.add(0);
		}
		setDataView();
	}

	private void setDataView() {
		if (lambdaSQL == null) return;
		
		try {
			Connection conn;
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(Database.DB_ADDR, Database.DB_USERNAME, Database.DB_PASSWORD);
			PreparedStatement st = conn.prepareStatement(lambdaSQL);
			// lambdaData.clear();
			hBoxLambda.getChildren().clear();
			hBoxLambda.getChildren().add(new Label("Lambda Terms:   "));
			int p = 0;
			for (int idx : lambdaIndex) {
				System.out.println("DEBUG: lambdasAll size = " + lambdasAll.size());
				System.out.println("DEBUG: lambdasAll 0 size = " + lambdasAll.get(0).size());
				try {
					int n = Integer.valueOf(lambdasAll.get(p).get(idx));
					st.setInt(p + 1, n);
				} catch (Exception e) {
					st.setString(p + 1, lambdasAll.get(p).get(idx));
				}
				System.out.println("DEBUG: " + lambdas.get(p));
				// lambdaData.add(lambdas.get(p) + ": " +
				// lambdasAll.get(p).get(idx));
				hBoxLambda.getChildren().add(new Label(lambdas.get(p) + ": " + lambdasAll.get(p).get(idx) + "   "));
				p++;
			}
			System.out.println(st.toString());
			st.execute();
			ResultSet rs = st.getResultSet();
			dataViewList.clear();
			dataView.getColumns().clear();
			dataView.setEditable(true);
			
			final int size = rs.getMetaData().getColumnCount();
			for (int i = 1; i <= size; i++) {
				final int j = i - 1;
				TableColumn<ObservableList, String> col = new TableColumn(rs.getMetaData().getColumnName(i));
				col.setCellValueFactory(
						new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
							public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
								if (param.getValue() == null || param.getValue().get(j) == null)
									return new SimpleStringProperty("");
								return new SimpleStringProperty(param.getValue().get(j).toString());
							}
						});
				dataView.getColumns().addAll(col);
			}
			int num_rows = 0;
			while (rs.next()) {
				ids.add(num_rows);
				ObservableList row = FXCollections.observableArrayList();
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					row.add(rs.getString(i));
				}
				dataViewList.add(row);
				num_rows++;
			}
			if (!isDba) {
				Vector<Vector<String>> citation_strs = new Vector<Vector<String>>();
				TableColumn citationColomn = new TableColumn("Citation");
				Callback<TableColumn, TableCell> cellFactory = new Callback<TableColumn, TableCell>() {
					@Override
					public TableCell call(TableColumn p) {
						return new ComboBoxCell();
					}
				};
				ids.clear();
				try {
					System.out.println("[DEBUG] datalog: " + datalog);
					  c_views = Tuple_reasoning2.tuple_reasoning(datalog, citation_strs);
					  // Vector<String> agg_citations = Tuple_reasoning2.tuple_gen_agg_citations(c_views);
					  // Vector<String> subset_agg_citations = Tuple_reasoning2.tuple_gen_agg_citations(c_views, ids);
				} catch (ClassNotFoundException | SQLException | IOException | InterruptedException e) {
					e.printStackTrace();
				}
				for (int i = 0; i < dataViewList.size(); i++) {
					ObservableList<String> lambdaData = FXCollections.observableArrayList(citation_strs.get(i));
					((ObservableList) dataViewList.get(i)).add(lambdaData);
				}
				citationColomn.setCellFactory(cellFactory);
				citationColomn.setCellValueFactory(
						new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
							public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
								if (param.getValue() == null || param.getValue().get(size) == null) return new SimpleStringProperty("");
								ObservableList<String> list =  (ObservableList<String>) param.getValue().get(size);
								if (list == null || list.size() == 0) return new SimpleStringProperty("");
								return new SimpleStringProperty(list.get(0));
							}
						});
				dataView.getColumns().addAll(citationColomn);
			}
			
			dataView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

	private void next() {
		for (int i = lambdaIndex.size() - 1; i >= 0; i--) {
			lambdaIndex.set(i, lambdaIndex.get(i) + 1);
			if (lambdaIndex.get(i) >= lambdasAll.get(i).size()) {
				lambdaIndex.set(i, 0);
			} else {
				break;
			}
		}
	}

	private void dbaMode(boolean b) {
		isDba = b;
		vboxDatalog.setVisible(b);
		hBoxPrevNext.setVisible(b);
		hBoxLambda.setVisible(b);
		hBoxGenCitation.setVisible(!b);
		splitPaneQbe.getItems().clear();
		splitPaneQbe.getItems().add(tableView);
		if (b) {
			splitPaneQbe.getItems().add(vboxDatalog);
			tableView.getColumns().clear();
			tableView.getColumns().addAll(expanderColumn, tableColumn, fieldColumn, showColumn, criteraColumn, lambdaColumn);
		} else {
			tableView.getColumns().clear();
			tableView.getColumns().addAll(expanderColumn, tableColumn, fieldColumn, showColumn, criteraColumn);
		}
	}
}
