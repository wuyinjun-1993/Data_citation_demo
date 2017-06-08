package edu.upenn.cis.citation.ui;

import edu.upenn.cis.citation.Corecover.Query;
//import edu.upenn.cis.citation.Pre_processing.insert_new_view;
import edu.upenn.cis.citation.citation_view.citation_view_vector;
import edu.upenn.cis.citation.dao.Database;
import edu.upenn.cis.citation.reasoning.Tuple_reasoning2;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.table.TableRowExpanderColumn;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.List;

public class QBEApp extends Application {

	private Scene viewScene;
	private Scene loginScene;

	private Tab dbaDataViewDataTab;
	private ObservableList<String> dbaListCitationViews;
	
	// private Scene newScene;
	private Stage stage, citeStage;
	// private TableView<ObservableList> dataSelectedView = new TableView<>();
	private ObservableList dataViewList = FXCollections.observableArrayList();
	// private ObservableList dataSelectedViewList = FXCollections.observableArrayList();
	private ObservableList lambdaData = FXCollections.observableArrayList();
	// Table tuples data
	private final ObservableList<Entry> data = FXCollections.observableArrayList();
	List<String> lambdas = new ArrayList<>();

	VBox vboxRightCQ = new VBox();
	
	// User
	private Scene userScene;
	private GridPane gridUser, gridUserSub;
	// DBA
	private Scene dbaScene;
	private GridPane gridDba, gridDbaSub;
	// TreeView share across user & dba screens
	private TreeItem<TreeNode> root;
	List<TreeItem<TreeNode>> removedTreeItems = new ArrayList<>();

	private ObservableList<ObservableList> dataTable = FXCollections.observableArrayList();
	List<List<String>> lambdasAll = new ArrayList<>();
	String lambdaSQL = null;
	String datalog = null;
	List<Integer> lambdaIndex = new ArrayList<>();
	Vector<Integer> ids = new Vector<>();

	HBox hBoxPrevNext, hBoxGenCitation;
	//
	SplitPane splitPaneQbe;
	
	TableRowExpanderColumn<Entry> expanderColumn;

	
	Vector<Vector<citation_view_vector>> c_views = null;

	// Dropdown Table Field
    ObservableList<String> optionsField = FXCollections.observableArrayList(Database.getTableList());

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
		this.citeStage = new Stage();
		buildLoginScene();
		buildQbeSceneDba();
		buildQbeSceneUser();
		buildViewScene();
		stage.setScene(loginScene);
		stage.setMinWidth(300);
		stage.setMinHeight(300);
		// set width / height values to be 75% of users screen resolution
		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		stage.setWidth(screenBounds.getWidth() * 0.8);
		stage.setHeight(screenBounds.getHeight() * 0.75);
		stage.show();
	}

	private void buildViewScene() {
        HBox hbox = new HBox();
        TextField textFieldDataViewDataLog = new TextField();
        DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetX(5);
        dropShadow.setOffsetY(5);
		Text textDataLog = new Text("DataLog: ");
        textDataLog.setId("text");
        textDataLog.setFont(Font.font("Courier New", FontWeight.BOLD, 18));
        textDataLog.setFill(Color.WHITE);
        textDataLog.setEffect(dropShadow);
        hbox.getChildren().addAll(textDataLog, textFieldDataViewDataLog);
        HBox.setHgrow(textFieldDataViewDataLog, Priority.ALWAYS);
        hbox.setAlignment(Pos.CENTER);

		// Adding HBox
		HBox hb = new HBox();
		hb.setPadding(new Insets(20, 20, 20, 30));
		DropShadow dropShadow_2 = new DropShadow();
        dropShadow_2.setOffsetX(5);
        dropShadow_2.setOffsetY(5);
		Text text = new Text("Citation Management");
		text.setId("text");
		text.setFont(Font.font("Courier New", FontWeight.BOLD, 28));
		text.setEffect(dropShadow_2);
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
		listViewDataViews.setItems(listDataViews);

		listViewDataViews.setOnMouseClicked(event -> {
            String dv = listViewDataViews.getSelectionModel().getSelectedItem();
            if (dv == null) return;
            String dataViewDataLog = Database.getDataViewDataLog(dv);
            if (!hbox.isVisible()) hbox.setVisible(true);
            textFieldDataViewDataLog.setText(dataViewDataLog);
        });

        Button buttonEditDataView = new Button("Edit");
        buttonEditDataView.setOnAction(event -> {
            String dv = listViewDataViews.getSelectionModel().getSelectedItem();
            hbox.setVisible(false);
            if (dv == null) return;
            dbaDataViewDataTab.setText("Data View: " + dv);
            dbaListCitationViews.setAll(Database.getCitationViews(dbaDataViewDataTab.getText().split(":")[1].trim()));
            stage.setScene(dbaScene);
        });
		Button buttonAddDataView = new Button("Add");
		buttonAddDataView.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog(null);
            dialog.setTitle("Enter Data View Name");
            dialog.setHeaderText(null);
            Optional<String> result = dialog.showAndWait();
            String entered;
            if (result.isPresent()) {
                entered = result.get();
            } else {
                return;
            }
			Database.createDataViewByName(entered);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succeed");
            alert.setHeaderText(null);
            alert.setContentText("The data view is successfully created");
            alert.showAndWait();
			listDataViews.add(entered);
		});
		Button buttonDeleteDataView = new Button("Delete");
		buttonDeleteDataView.setOnAction(event -> {
            String dv = listViewDataViews.getSelectionModel().getSelectedItem();
            dbaListCitationViews.remove(dv);
            Database.deleteDataViewByName(dv);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succeed");
            alert.setHeaderText(null);
            alert.setContentText("The data view is successfully deleted");
            alert.showAndWait();
			listDataViews.remove(dv);
		});
		GridPane.setHgrow(listViewDataViews, Priority.ALWAYS);
		GridPane.setVgrow(listViewDataViews, Priority.ALWAYS);
		gridPaneDataViews.add(lableDataviews, 0, 0, 3, 1);
		gridPaneDataViews.add(listViewDataViews, 0, 1, 3, 1);
        gridPaneDataViews.add(buttonEditDataView, 0, 2);
		gridPaneDataViews.add(buttonAddDataView, 1, 2);
		gridPaneDataViews.add(buttonDeleteDataView, 2, 2);
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
		ObservableList<String> listCitationViews = FXCollections.observableArrayList(Database.getCitationViews(null));
		listViewCitationView.setItems(listCitationViews);
		Button buttonAddCitationView = new Button("Add Citation View");
		buttonAddCitationView.setOnAction(event -> {
			this.stage.setScene(dbaScene);
		});
		Button buttonDeleteCitationView = new Button("Delete Citation View");
		buttonDeleteCitationView.setOnAction(event -> {
			this.stage.setScene(dbaScene);
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
        hbox.setVisible(false);
		bp.setBottom(hbox);
		viewScene = new Scene(bp);
		viewScene.getStylesheets().add(QBEApp.class.getResource("style.css").toExternalForm());
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
				Desktop.getDesktop().browse(new URL("http://www.guidetopharmacology.org/GRAC/searchPage.jsp").toURI());
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		});
		gridPane2.add(l1, 0, 1);
		gridPane2.add(l2, 0, 2);
		gridPane2.add(l3, 0, 3);
		Button citeButton = new Button("Cite a dataset");
		citeButton.setOnAction(event -> {
			this.stage.setScene(userScene);
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
		btnLogin.setOnAction(event -> {
			// TODO: add OAuth Authentication
			String checkUser = txtUserName.getText().toString();
			String checkPw = pf.getText().toString();
			if (checkUser.equals("admin") && checkPw.equals("admin")) {
				lblMessage.setText("Congratulations!");
				lblMessage.setTextFill(Color.GREEN);
			} else {
				lblMessage.setText("Incorrect user or password.");
				lblMessage.setTextFill(Color.RED);
				return;
			}
			txtUserName.setText("");
			pf.setText("");
			this.stage.setScene(viewScene);
		});
		// Add HBox and GridPane layout to BorderPane Layout
		gp.add(hb, 0, 0);
		hbox.getChildren().addAll(gridPane, gridPane2);
		gp.add(hbox, 0, 1);
		// LOGIN SCENE
		loginScene = new Scene(gp);
		loginScene.getStylesheets().add(QBEApp.class.getResource("style.css").toExternalForm());
	}

	
	private void buildQbeSceneUser() {
		gridUser = new GridPane();
		gridUser.setPadding(new Insets(5, 10, 10, 10));
        gridUser.setHgap(10);
        gridUser.setVgap(10);
        
        TabPane tabPane = new TabPane();
		Tab dataTab = new Tab("Citation Generator");
		dataTab.setClosable(false);
		tabPane.getTabs().setAll(dataTab);
		tabPane.setTabClosingPolicy(TabClosingPolicy.SELECTED_TAB);
		tabPane.getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);
		GridPane.setHgrow(tabPane, Priority.ALWAYS);
		GridPane.setVgrow(tabPane, Priority.ALWAYS);
		
		gridUser.add(tabPane, 1, 0, 1, 2);
		
        gridUserSub = new GridPane();
        gridUserSub.setPadding(new Insets(2, 5, 2, 5));
        gridUserSub.setHgap(5);
        gridUserSub.setVgap(5);
        dataTab.setContent(gridUserSub);

		final Label label_0 = new Label("Query Builder");
		label_0.setId("prompt-text");
		GridPane.setHgrow(label_0, Priority.ALWAYS);
		gridUserSub.add(label_0, 0, 0);

		// TreeView and search box
		TreeView<TreeNode> samplesTreeView = new TreeView<>();
		buildSampleTree(samplesTreeView, null, true);
		buildSampleTreeView(samplesTreeView);
		GridPane.setVgrow(samplesTreeView, Priority.ALWAYS);
		gridUser.add(samplesTreeView, 0, 1);
		TextField searchBox = buildSearchBox(samplesTreeView);
		GridPane.setMargin(searchBox, new Insets(5, 0, 0, 0));
		GridPane.setHgrow(searchBox, Priority.NEVER);
		gridUser.add(searchBox, 0, 0);

		final TableView<ObservableList> dataView = new TableView();
		GridPane.setVgrow(dataView, Priority.ALWAYS);
		GridPane.setHgrow(dataView, Priority.ALWAYS);
		dataView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		dataView.setItems(dataViewList);
		gridUserSub.add(dataView, 0, 3, 2, 1);
		
		// Pane
		TableView<Entry> tableView = new TableView<Entry>();
		splitPaneQbe = new SplitPane();
		GridPane.setHgrow(splitPaneQbe, Priority.ALWAYS);
		GridPane.setVgrow(splitPaneQbe, Priority.ALWAYS);
		splitPaneQbe.getItems().add(tableView);
		
		gridUserSub.add(buildTopMenu(null, null, dataView, true), 1, 0);
		gridUserSub.add(splitPaneQbe, 0, 1, 2, 1);

		// Table View
		tableView.setEditable(true);
		tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		expanderColumn = new TableRowExpanderColumn<>(this::createEditor);
		expanderColumn.setMaxWidth(30);
		expanderColumn.setMinWidth(30);
		TableColumn<Entry, String> tableColumn = new TableColumn<>("Table");
		TableColumn<Entry, String> fieldColumn = new TableColumn<>("Field");
		TableColumn<Entry, Boolean> showColumn = new TableColumn<>("Show");
		TableColumn<Entry, String> criteraColumn = new TableColumn<>("Criteria");
        TableColumn<Entry, String> joinColumn = new TableColumn<>("Join");
		tableColumn.setCellValueFactory(new PropertyValueFactory<>("table"));
		fieldColumn.setCellValueFactory(new PropertyValueFactory<>("field"));
		showColumn.setCellValueFactory(new PropertyValueFactory<>("show"));
		showColumn.setCellFactory(CheckBoxTableCell.forTableColumn(showColumn));
		showColumn.setEditable(true);
		criteraColumn.setCellValueFactory(new PropertyValueFactory<>("criteria"));
		criteraColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        joinColumn.setCellValueFactory(new PropertyValueFactory<>("join"));
        joinColumn.setCellFactory(TextFieldTableCell.forTableColumn());

		tableView.getColumns().addAll(expanderColumn, tableColumn, fieldColumn, showColumn, criteraColumn, joinColumn);
		tableView.setItems(data);
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		// Label data preview
		final Label label_1 = new Label("Data Preview");
		label_1.setId("prompt-text");
		GridPane.setHgrow(label_1, Priority.ALWAYS);
		gridUserSub.add(label_1, 0, 2);

		hBoxPrevNext = new HBox();
		hBoxPrevNext.setAlignment(Pos.CENTER_RIGHT);
		final Button prevButton = new Button("<<");
		final Button nextButton = new Button(">>");
		prevButton.setId("prevnext");
		nextButton.setId("prevnext");
		nextButton.setOnAction(e -> {
			next();
			setDataView(null, dataView, true);
		});
		hBoxPrevNext.getChildren().addAll(prevButton, nextButton);
		GridPane.setHgrow(hBoxPrevNext, Priority.ALWAYS);
		gridUserSub.add(hBoxPrevNext, 1, 4);

		hBoxGenCitation = new HBox();
		hBoxGenCitation.setAlignment(Pos.CENTER_RIGHT);
		Button genButton = new Button("Generate Citation");
		genButton.setOnAction(e -> {
			System.out.println(dataView.getSelectionModel().getSelectedItems().size());
			ObservableList<Integer> indices = dataView.getSelectionModel().getSelectedIndices();
			ids.clear();
			ids.addAll(indices);
			Vector<String> subset_agg_citations = new Vector<>();
			try {
				subset_agg_citations = Tuple_reasoning2.tuple_gen_agg_citations(c_views, ids);
			} catch (ClassNotFoundException | SQLException e1) {
				e1.printStackTrace();
			}
			ObservableList<String> listCitations = FXCollections.observableArrayList();
			for (String s : subset_agg_citations) {
				listCitations.add(s);
			}
			
			GridPane gridCg = buildGridCg(listCitations);
			this.citeStage.setScene(new Scene(gridCg, 500, 300));
			this.citeStage.setTitle("Citations");
			this.citeStage.show();
		});
		genButton.setId("buttonGen");
		hBoxGenCitation.getChildren().add(genButton);
		gridUserSub.add(hBoxGenCitation, 1, 2);
		// ===========================================================================
		userScene = new Scene(gridUser);
		userScene.getStylesheets().add(QBEApp.class.getResource("style.css").toExternalForm());
	}
	
	private HBox buildTopMenu(TextField textArea, HBox hBoxLambda, TableView<ObservableList> dataView, boolean toCite) {
		final HBox hBox = new HBox();
		hBox.setAlignment(Pos.CENTER_RIGHT);
		final Button runButton = new Button(" Run  ");
		final Button clearButton = new Button("Clear ");
		final Button backButton = new Button("Return");
		runButton.setId("bevel-grey");
		clearButton.setId("bevel-grey");
		backButton.setId("bevel-grey");
		runButton.setOnAction(e -> {
			if (data.isEmpty()) return;
			List<Entry> list = new ArrayList<>();
			list.addAll(data);
			if (textArea != null) textArea.setText(Util.convertToDatalogOriginal(list) + "\n");
			lambdasAll.clear();
			lambdaIndex.clear();
			lambdaSQL = Util.convertToSQLWithLambda(list, toCite);
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
			setDataView(hBoxLambda, dataView, toCite);
		});
		clearButton.setOnAction(e -> {
			data.clear();
			if (textArea != null) textArea.clear();
			dataView.getColumns().clear();
			dataViewList.clear();
		});
		backButton.setOnAction(e -> {
			data.clear();
			if (textArea != null) textArea.clear();
			dataView.getColumns().clear();
			dataViewList.clear();
			if (gridDba.getChildren().contains(vboxRightCQ)) {
				stage.setWidth(stage.getWidth()-220);
                gridDba.getChildren().remove(vboxRightCQ);
                Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
                stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
                stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
			}
			this.stage.setScene(loginScene);
		});
		HBox.setMargin(runButton, new Insets(0, 5, 0, 5));
		HBox.setMargin(clearButton, new Insets(0, 5, 0, 5));
		HBox.setMargin(backButton, new Insets(0, 5, 0, 5));
		hBox.getChildren().addAll(runButton, clearButton, backButton);
		return hBox;
	}

	private TextField buildSearchBox(TreeView<TreeNode> treeView) {
		TextField searchBox = new TextField();
		searchBox.setMinWidth(180);
        searchBox.setMaxWidth(180);
		searchBox.setPromptText("Search Table");
		searchBox.textProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable o) {
				buildSampleTree(treeView, searchBox.getText(), false);
			}
		});
		return searchBox;
	}

	private void buildQbeSceneDba() {
		gridDba = new GridPane();
        gridDba.setPadding(new Insets(5, 10, 10, 10));
        gridDba.setHgap(10);
        gridDba.setVgap(10);
        
		// TabPane
		TabPane tabPane = new TabPane();
		dbaDataViewDataTab = new Tab("View Builder");
		dbaDataViewDataTab.setClosable(false);
		tabPane.getTabs().setAll(dbaDataViewDataTab);
		tabPane.setTabClosingPolicy(TabClosingPolicy.SELECTED_TAB);
		tabPane.getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);
		GridPane.setHgrow(tabPane, Priority.ALWAYS);
		GridPane.setVgrow(tabPane, Priority.ALWAYS);
		gridDba.add(tabPane, 1, 0, 1, 2);
		
		VBox vboxRight = new VBox();
		vboxRight.setAlignment(Pos.CENTER);
        vboxRight.setMinWidth(180);
        vboxRight.setMaxWidth(180);

		Label labelCv = new Label("Citation Views");
		labelCv.setId("prompt-text");
		labelCv.setMinWidth(165);
		labelCv.setStyle("-fx-font-size: 16px;");
		
		ListView<String> listViewCv = new ListView<>();
		listViewCv.setMinWidth(180);
		listViewCv.setCellFactory(lv -> {
			ListCell<String> cell = new ListCell<>();
			ContextMenu contextMenu = new ContextMenu();
			MenuItem editItem = new MenuItem();
			editItem.textProperty().bind(Bindings.format("Edit \"%s\"", cell.itemProperty()));
			editItem.setOnAction(event -> {
				String item = cell.getItem();
				// code to edit item...
			});
			MenuItem deleteItem = new MenuItem();
			deleteItem.textProperty().bind(Bindings.format("Delete \"%s\"", cell.itemProperty()));
			deleteItem.setOnAction(event -> listViewCv.getItems().remove(cell.getItem()));
			contextMenu.getItems().addAll(editItem, deleteItem);
			cell.textProperty().bind(cell.itemProperty());
			cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
				if (isNowEmpty) {
					cell.setContextMenu(null);
				} else {
					cell.setContextMenu(contextMenu);
				}
			});
			return cell;
		});
		dbaListCitationViews = FXCollections.observableArrayList();
		listViewCv.setItems(dbaListCitationViews);
		
		TextField tfDataView = new TextField();
		Label viewNameLabel = new Label("Citation View Name:");
		
		Button btDataView = new Button("Add ");
        btDataView.setFont(Font.font("Courier New", FontWeight.BOLD, 12));
        btDataView.setOnAction(e -> {
        	stage.setWidth(stage.getWidth()+230);
            Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
            stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
        	gridDba.add(vboxRightCQ, 3, 0, 1, 2);
		});
        
		Button btDataViewSave = new Button("Save");
        btDataViewSave.setFont(Font.font("Courier New", FontWeight.BOLD, 12));
		btDataViewSave.setOnAction(e -> {
			Query query = null;
//			try {
////				insert_new_view.add(query, "");
//			} catch (ClassNotFoundException | SQLException e1) {
//				e1.printStackTrace();
//			}
		});
		HBox addSaveHBox = new HBox();
		addSaveHBox.getChildren().addAll(btDataView, btDataViewSave);
        addSaveHBox.setAlignment(Pos.CENTER);

		VBox.setVgrow(listViewCv, Priority.ALWAYS);
		vboxRight.getChildren().addAll(labelCv, listViewCv, viewNameLabel, tfDataView, addSaveHBox);
		gridDba.add(vboxRight, 2, 0, 1, 2);
		
		ListView<String> listViewRightCQ = new ListView<String>();
		listViewRightCQ.setMinWidth(180);
		listViewRightCQ.setCellFactory(lv -> {
			ListCell<String> cell = new ListCell<>();
			ContextMenu contextMenu = new ContextMenu();
			MenuItem editItem = new MenuItem();
			editItem.textProperty().bind(Bindings.format("Edit \"%s\"", cell.itemProperty()));
			editItem.setOnAction(event -> {
				String item = cell.getItem();
				// code to edit item...
			});
			MenuItem deleteItem = new MenuItem();
			deleteItem.textProperty().bind(Bindings.format("Delete \"%s\"", cell.itemProperty()));
			deleteItem.setOnAction(event -> listViewCv.getItems().remove(cell.getItem()));
			contextMenu.getItems().addAll(editItem, deleteItem);
			cell.textProperty().bind(cell.itemProperty());
			cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
				if (isNowEmpty) {
					cell.setContextMenu(null);
				} else {
					cell.setContextMenu(contextMenu);
				}
			});
			return cell;
		});
		listViewRightCQ.setOnMouseClicked(event -> {
			String dv = dbaDataViewDataTab.getText().split(":")[1].trim();
			String cv = listViewRightCQ.getSelectionModel().getSelectedItem();
			dbaListCitationViews.add(cv);
			Database.insertDCTuple(dv, cv);
		});
		ObservableList<String> listRightCitationViews = FXCollections.observableArrayList(Database.getCitationViews(null));
		listViewRightCQ.setItems(listRightCitationViews);
		
		Label rightCQLabel = new Label("All Citation Views");
		rightCQLabel.setId("prompt-text");
		rightCQLabel.setMinWidth(180);
		rightCQLabel.setStyle("-fx-font-size: 16px;");
		
		Button rightBtCQ = new Button("Hide");
		rightBtCQ.setId("buttonGen");
		rightBtCQ.setOnAction(e -> {
        	gridDba.getChildren().remove(vboxRightCQ);
        	stage.setWidth(stage.getWidth()-220);
            Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
            stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
		});
		// Right side pop-up selection citation queries
		vboxRightCQ = new VBox();
		vboxRightCQ.setMaxWidth(190);
        vboxRightCQ.setMinWidth(190);
		vboxRightCQ.setAlignment(Pos.CENTER);
		VBox.setVgrow(listViewRightCQ, Priority.ALWAYS);
		vboxRightCQ.setStyle("-fx-border-color: black;-fx-border-insets: 4;-fx-border-width: 2;-fx-border-style: dashed;-fx-border-radius: 5;");
		vboxRightCQ.getChildren().addAll(rightCQLabel, listViewRightCQ, rightBtCQ);
        vboxRightCQ.setAlignment(Pos.CENTER);
		
        gridDbaSub = new GridPane();
        gridDbaSub.setPadding(new Insets(2, 5, 2, 5));
        gridDbaSub.setHgap(5);
        gridDbaSub.setVgap(5);
        dbaDataViewDataTab.setContent(gridDbaSub);

        
		Label label_0 = new Label("View Builder");
		label_0.setId("prompt-text");
		GridPane.setHgrow(label_0, Priority.ALWAYS);
		gridDbaSub.add(label_0, 0, 0);

		// TreeView and search box
		TreeView<TreeNode> samplesTreeView = new TreeView<TreeNode>();
		buildSampleTree(samplesTreeView, null, true);
		buildSampleTreeView(samplesTreeView);
		GridPane.setVgrow(samplesTreeView, Priority.ALWAYS);
		gridDba.add(samplesTreeView, 0, 1);
		TextField searchBox = buildSearchBox(samplesTreeView);
		GridPane.setMargin(searchBox, new Insets(5, 0, 0, 0));
		GridPane.setHgrow(searchBox, Priority.NEVER);
		gridDba.add(searchBox, 0, 0);

		// Pane
		TableView<Entry> tableView = new TableView<Entry>();
		splitPaneQbe = new SplitPane();
		GridPane.setHgrow(splitPaneQbe, Priority.ALWAYS);
		GridPane.setVgrow(splitPaneQbe, Priority.ALWAYS);
		splitPaneQbe.getItems().add(tableView);
		
		final TableView<ObservableList> dataView = new TableView();
		GridPane.setVgrow(dataView, Priority.ALWAYS);
		GridPane.setHgrow(dataView, Priority.ALWAYS);
		dataView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		dataView.setItems(dataViewList);
		gridDbaSub.add(dataView, 0, 4, 2, 1);
		
		// Lambda
		final HBox hBoxLambda = new HBox();
		hBoxLambda.getChildren().add(new Label("Lambda Terms:  "));
		hBoxLambda.getStyleClass().add("hBoxLambda");
		GridPane.setHgrow(hBoxLambda, Priority.ALWAYS);
		gridDbaSub.add(hBoxLambda, 0, 5);
		
		Label datalogLabel = new Label("Datalog:");
        TextField datalogTextArea = new TextField();
        datalogLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 12));
        datalogTextArea.setFont(Font.font("Courier New", FontWeight.BOLD, 8));
		final HBox hBox = buildTopMenu(datalogTextArea, hBoxLambda, dataView, false);
		final HBox vboxDatalog = new HBox();
		vboxDatalog.setSpacing(5);
		vboxDatalog.setPadding(new Insets(5, 5, 5, 5));
		vboxDatalog.getChildren().addAll(datalogLabel, datalogTextArea);
        vboxDatalog.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(datalogTextArea, Priority.ALWAYS);
		// splitPaneQbe.getItems().add(vboxDatalog);
		gridDbaSub.add(hBox, 1, 0);
		gridDbaSub.add(splitPaneQbe, 0, 1, 2, 1);
		gridDbaSub.add(vboxDatalog, 0, 2, 2, 1);

		// Table View
		tableView.setEditable(true);
		tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		expanderColumn = new TableRowExpanderColumn<>(this::createEditor);
		expanderColumn.setMaxWidth(30);
		expanderColumn.setMinWidth(30);
		TableColumn<Entry, String> tableColumn = new TableColumn<>("Table");
		TableColumn<Entry, String> fieldColumn = new TableColumn<>("Field");
		TableColumn<Entry, Boolean> showColumn = new TableColumn<>("Show");
		TableColumn<Entry, String> criteraColumn = new TableColumn<>("Criteria");
        TableColumn<Entry, String> joinColumn = new TableColumn<>("Join");
		TableColumn<Entry, Boolean> lambdaColumn = new TableColumn<>("Lambda");
		tableColumn.setCellValueFactory(new PropertyValueFactory<>("table"));
		fieldColumn.setCellValueFactory(new PropertyValueFactory<>("field"));
		showColumn.setCellValueFactory(new PropertyValueFactory<>("show"));
		showColumn.setCellFactory(CheckBoxTableCell.forTableColumn(showColumn));
		showColumn.setEditable(true);
		criteraColumn.setCellValueFactory(new PropertyValueFactory<>("criteria"));
		criteraColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        joinColumn.setCellValueFactory(new PropertyValueFactory<>("join"));
        joinColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		lambdaColumn.setCellValueFactory(new PropertyValueFactory<>("lambda"));
		lambdaColumn.setCellFactory(CheckBoxTableCell.forTableColumn(lambdaColumn));
		lambdaColumn.setEditable(true);

		tableView.getColumns().addAll(expanderColumn, tableColumn, fieldColumn, showColumn, criteraColumn, joinColumn, lambdaColumn);
		tableView.setItems(data);
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


		// Label data preview
		final Label label_1 = new Label("Data Preview");
		label_1.setId("prompt-text");
		GridPane.setHgrow(label_1, Priority.ALWAYS);
		gridDbaSub.add(label_1, 0, 3);
		
		HBox hboxSq = new HBox();
		hboxSq.setAlignment(Pos.CENTER_RIGHT);
		Button saveViewButton = new Button("Save View Query");
        saveViewButton.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog(null);
            dialog.setTitle("Enter view query name");
            dialog.setHeaderText(null);
            Optional<String> result = dialog.showAndWait();
            String entered;
            if (result.isPresent()) {
                entered = result.get();
            } else {
                return;
            }
            // TODO: Wei
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succeed");
            alert.setHeaderText(null);
            alert.setContentText("The view queries is successfully saved as " + entered);
            alert.showAndWait();
        });
		hboxSq.getChildren().add(saveViewButton);

		gridDbaSub.add(hboxSq, 1, 3);

		hBoxPrevNext = new HBox();
		hBoxPrevNext.setAlignment(Pos.CENTER_RIGHT);
		final Button prevButton = new Button("<<");
		final Button nextButton = new Button(">>");
		prevButton.setId("prevnext");
		nextButton.setId("prevnext");
		nextButton.setOnAction(e -> {
			next();
			setDataView(hBoxLambda, dataView, false);
		});
		hBoxPrevNext.getChildren().addAll(prevButton, nextButton);
		GridPane.setHgrow(hBoxPrevNext, Priority.ALWAYS);
		gridDbaSub.add(hBoxPrevNext, 1, 5);

		// ===========================================================================
		dbaScene = new Scene(gridDba);
		dbaScene.getStylesheets().add(QBEApp.class.getResource("style.css").toExternalForm());
	}

	private GridPane buildGridCg(ObservableList<String> observableList) {
		GridPane gridCg = new GridPane();
        gridCg.setPadding(new Insets(2, 5, 2, 5));
        gridCg.setHgap(5);
        gridCg.setVgap(5);
		
		ListView<String> list = new ListView<>();
		list.setCellFactory(list1 -> new ListCell<String>() {
            {
                Text text = new Text();
                text.wrappingWidthProperty().bind(list1.widthProperty().subtract(15));
                text.textProperty().bind(itemProperty());
                setPrefWidth(0);
                setGraphic(text);
            }
        });
		list.setItems(observableList);
		GridPane.setHgrow(list, Priority.ALWAYS);
		GridPane.setVgrow(list, Priority.ALWAYS);
		gridCg.add(list, 0, 0);
		
		HBox hBox = new HBox();
		hBox.setAlignment(Pos.CENTER_RIGHT);
		Button button = new Button("Copy Selected Citation");
		button.setId("prevnext");
		GridPane.setHgrow(hBox, Priority.ALWAYS);
		hBox.getChildren().add(button);
		gridCg.add(hBox, 0, 1);
		
		return gridCg;
	}

	private GridPane createEditor(TableRowExpanderColumn.TableRowDataFeatures<Entry> param) {
		GridPane editor = new GridPane();
		editor.setPadding(new Insets(10));
		editor.setHgap(10);
		editor.setVgap(5);
		Entry entry = param.getValue();
		TextField criteriaField = new TextField(entry.getCriteria());
		// TextField joinField = new TextField(entry.getJoin());
		editor.addRow(0, new Label("Criteria"), criteriaField);
        ObservableList<String> optionsTable = FXCollections.observableArrayList(Database.getTableList());
        final ComboBox<String> comboBoxTable = new ComboBox(optionsTable);
        comboBoxTable.setPromptText("Table");
        comboBoxTable.valueProperty().addListener((ov, t, t1) -> {
            optionsField.clear();
            optionsField.addAll(Database.getAttrList(t1));
        });
        final ComboBox<String> comboBoxField = new ComboBox(optionsField);
        comboBoxField.setPromptText("Field");
		editor.addRow(1, new Label("Join"), comboBoxTable, comboBoxField);
		Button saveButton = new Button("Save");
		saveButton.setOnAction(event -> {
			entry.setCriteria(criteriaField.getText());
			entry.setJoin(comboBoxTable.getValue() + "." + comboBoxField.getValue());
			param.toggleExpanded();
		});
		Button cancelButton = new Button("Cancel");
		cancelButton.setOnAction(event -> param.toggleExpanded());
		Button deleteButton = new Button("Delete Row");
		deleteButton.setOnAction(event -> data.remove(entry));
		editor.addRow(2, saveButton, cancelButton, deleteButton);
		return editor;
	}

	private void buildSampleTree(TreeView<TreeNode> samplesTreeView, String searchText, boolean rebuild) {
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
	
	private TreeView<TreeNode> buildSampleTreeView(TreeView<TreeNode> samplesTreeView) {
		samplesTreeView.setRoot(root);
		samplesTreeView.setShowRoot(true);
		samplesTreeView.getStyleClass().add("samples-tree");
		samplesTreeView.setMinWidth(180);
        samplesTreeView.setMaxWidth(180);
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
						if (node == null || node == root) return;
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
					}
				});
		return samplesTreeView;
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

	private void setDataView(HBox hBoxLambda, TableView<ObservableList> dataView, boolean toCite) {
		if (lambdaSQL == null) return;
		
		try {
			Connection conn;
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(Database.DB_ADDR, Database.DB_USERNAME, Database.DB_PASSWORD);
			PreparedStatement st = conn.prepareStatement(lambdaSQL);
			// lambdaData.clear();
			if (hBoxLambda != null) hBoxLambda.getChildren().clear();
			if (hBoxLambda != null) hBoxLambda.getChildren().add(new Label("Lambda Terms:   "));
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
				if (hBoxLambda != null)  hBoxLambda.getChildren().add(new Label(lambdas.get(p) + ": " + lambdasAll.get(p).get(idx) + "   "));
				p++;
			}
			System.out.println(st.toString());
			st.execute();
			ResultSet rs = st.getResultSet();
			dataViewList.clear();
			dataView.getColumns().clear();

			final int size = rs.getMetaData().getColumnCount();
			for (int i = 1; i <= size; i++) {
				final int j = i - 1;
				String str = rs.getMetaData().getColumnName(i);
				if (str.contains("_c_")) {
					str = str.substring(str.indexOf("_c_")+3);
				}
				TableColumn<ObservableList, String> col = new TableColumn(str);
				col.setCellValueFactory(
						param -> {
                            if (param.getValue() == null || param.getValue().get(j) == null)
                                return new SimpleStringProperty("");
                            return new SimpleStringProperty(param.getValue().get(j).toString());
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
			if (toCite) {
				Vector<Vector<String>> citation_strs = new Vector<Vector<String>>();
				TableColumn citationColomn = new TableColumn("Citation");
				Callback<TableColumn, TableCell> cellFactory = new Callback<TableColumn, TableCell>() {
					@Override
					public TableCell call(TableColumn p) {
						return new ComboBoxCell();
					}
				};
				ids.clear();
//				try {
//					System.out.println("[DEBUG] datalog: " + datalog);
////					  c_views = Tuple_reasoning2.tuple_reasoning(datalog, citation_strs);
//					  // Vector<String> agg_citations = Tuple_reasoning2.tuple_gen_agg_citations(c_views);
//					  // Vector<String> subset_agg_citations = Tuple_reasoning2.tuple_gen_agg_citations(c_views, ids);
//				} catch (ClassNotFoundException | SQLException | IOException | InterruptedException e) {
//					e.printStackTrace();
//				}
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
			dataView.setEditable(true);
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

}
