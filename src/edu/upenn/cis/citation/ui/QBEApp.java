package edu.upenn.cis.citation.ui;

import edu.upenn.cis.citation.Corecover.Query;
import edu.upenn.cis.citation.Pre_processing.Gen_query;
import edu.upenn.cis.citation.Pre_processing.Query_operation;
import edu.upenn.cis.citation.Pre_processing.citation_view_operation;
import edu.upenn.cis.citation.Pre_processing.populate_db;
import edu.upenn.cis.citation.Pre_processing.view_operation;
import edu.upenn.cis.citation.citation_view.Head_strs;
//import edu.upenn.cis.citation.Pre_processing.insert_new_view;
import edu.upenn.cis.citation.citation_view.citation_view_vector;
import edu.upenn.cis.citation.dao.Database;
import edu.upenn.cis.citation.datalog.Query_converter;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning1;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning1_test;
import edu.upenn.cis.citation.reasoning1.Tuple_reasoning2;
import edu.upenn.cis.citation.user_query.query_storage;
import java_cup.internal_error;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import net.sf.jsqlparser.statement.select.FromItem;

import org.apache.poi.hssf.util.HSSFColor.YELLOW;
import org.apache.poi.poifs.property.Child;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.controlsfx.control.table.TableRowExpanderColumn;
import org.json.JSONException;

import sun.tools.jar.resources.jar;
import ucar.ma2.ArrayDouble.D3.IF;

import com.sun.javafx.geom.RectangularShape;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.xml.internal.bind.v2.TODO;

import java.awt.*;
//import java.awt.TextArea;
import java.awt.Dialog;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.*;
import java.util.*;
import java.util.List;


public class QBEApp extends Application {

	private Scene loginScene;
	private Scene viewScene;

	private Tab dbaDataViewDataTab;
	// citation view names connected with current dataview
	private ObservableList<String> dbaListCitationViews = FXCollections.observableArrayList();
	// block list
	private ObservableList<String> dbaListBlock = FXCollections.observableArrayList();
	
	// private Scene newScene;
	private Stage stage, citeStage, queryStage, formatStage;
	// private TableView<ObservableList> dataSelectedView = new TableView<>();
	// data shown in data preview table in dba or user, added in setDataView()
	private ObservableList dataViewList = FXCollections.observableArrayList();
	// data shown in data preview table in citation builder
	private ObservableList dataViewListNew = FXCollections.observableArrayList();
	// private ObservableList dataSelectedViewList = FXCollections.observableArrayList();
	private ObservableList lambdaData = FXCollections.observableArrayList();
	// Table tuples data
	private final ObservableList<Entry> data = FXCollections.observableArrayList();
	// table tuples in current citation
	private final ObservableList<Entry> dataNew = FXCollections.observableArrayList();
	// all citation queries
	private final ObservableList<CQuery> dataQuery = FXCollections.observableArrayList();
	// all generated queries
	private final ObservableList<GQuery> dataGenerated = FXCollections.observableArrayList();
	List<String> lambdas = new ArrayList<>();

	VBox vboxRightCQ = new VBox();
	// data preview shown after run in dba and user
	final TableView<ObservableList> dataView = new TableView(); 
	// data preview shown after run in citation builder
	final TableView<ObservableList> dataViewNew = new TableView(); 
	// data preview in user scene
	final TableView<ObservableList> userDataView = new TableView();
	// lambda vbox shown after run in dba and user
	final HBox hBoxLambda = new HBox();
	// lambda vbox shown after run in citation builder
	final HBox hBoxLambdaNew = new HBox();
	// build the search box of the search tree
	TextField searchBox = new TextField();
	
	// User
	private Scene userScene;
	private GridPane gridUser, gridUserSub;
	// DBA
	private Scene dbaScene;
	private GridPane gridDba, gridDbaSub;
	
	// add citation scene
	private Scene citationScene;
	private GridPane gridCitation, gridCitationSub;
	private Tab citationDataViewDataTab;
	
	// the generatedScene used to show generated citation query
	private Scene generatedScene;
	// the format Scene to show different format of citations
	private Scene formatScene;
	
	// TreeView share across user & dba screens
	private TreeItem<TreeNode> root;
	List<TreeItem<TreeNode>> removedTreeItems = new ArrayList<>();
	Query userGeneratedQuery =  null;

	// view names
	ObservableList<String> listDataViews = FXCollections.observableArrayList(Database.getDataViews());
	// get all citation views names
	ObservableList<String> listCitationViews = FXCollections.observableArrayList(Database.getCitationViews(null));
	// get all generated query ids
	ObservableList<String> listQueries = FXCollections.observableArrayList(Database.getGeneratedQueryList());
	List<List<String>> lambdasAll = new ArrayList<>();
	String lambdaSQL = null;
	String datalog = null;
	String dv = ""; // The name of edited view
	String cv = ""; // The name of edited citation
	// citation query name hand over to queryScene
	String listQuery = "";
	// datalog text area in dba or user
	TextArea datalogTextArea = new TextArea();
	// datalog text area in citation builder
	TextArea datalogTextAreaNew = new TextArea();
	List<Integer> lambdaIndex = new ArrayList<>();
	Vector<Integer> ids = new Vector<>();
	int count = 0; // count the untitled view
	double paddingCriteriaUser = 0;
	double paddingCriteriaDba = 0;
	double paddingJoin = 0;

	// HBox PrevNext used in dba or user
	HBox hBoxPrevNext, hBoxGenCitation;
	// HBox PreNext used in citation builder
	HBox hBoxPrevNextNew;
	// splitPane used in dba or user scene
	SplitPane splitPaneQbe;
	// splitPane used in citation builder
	SplitPane splitPaneQbeNew;
	
	TableRowExpanderColumn<Entry> expanderColumn;
	
	Vector<Vector<citation_view_vector>> c_views = null;

	// Dropdown Table Field
    ObservableList<String> optionsField = FXCollections.observableArrayList(Database.getTableList());
    
    final Clipboard clipboard = Clipboard.getSystemClipboard();
    final ClipboardContent content = new ClipboardContent();
    final Label lblMessage = new Label();
    
private Object String;

	Vector<Head_strs> heads = new Vector<Head_strs>();

	HashMap<Head_strs, Vector<Vector<citation_view_vector>>> citation_view_map = new HashMap<Head_strs, Vector<Vector<citation_view_vector>>>();


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
		this.queryStage = new Stage();
		this.formatStage = new Stage();
		
		buildLoginScene();
//		buildDbaScene();
//		buildUserScene();
//		buildViewScene();
//		buildCitationScene();
		stage.setScene(loginScene);
		stage.setMinWidth(400);
		stage.setMinHeight(400);
		// set width / height values to be 75% of users screen resolution
		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		stage.setWidth(screenBounds.getWidth() * 0.9);
		stage.setHeight(screenBounds.getHeight() * 0.85);
		stage.show();
	}
	
    /** ===================================================================
     * Build the Login Scene 
     */
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
		gridPane2.add(new Label(" IUPHAR/BPS Links: "), 0, 1);
		Hyperlink l1 = new Hyperlink();
		l1.setId("linkLogin");
		l1.setText("- Targets");
		l1.setOnAction(Event -> {
			try {
				Desktop.getDesktop().browse(new URL("http://www.guidetopharmacology.org/targets.jsp").toURI());
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		});
		Hyperlink l2 = new Hyperlink();
		l2.setId("linkLogin");
		l2.setText("- Ligands");
		l2.setOnAction(Event -> {
			try {
				Desktop.getDesktop().browse(new URL("http://www.guidetopharmacology.org/GRAC/LigandListForward?database=all").toURI());
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		});
		Hyperlink l3 = new Hyperlink();
		l3.setId("linkLogin");
		l3.setText("- Advanced Search");
		l3.setOnAction(Event -> {
			try {
				Desktop.getDesktop().browse(new URL("http://www.guidetopharmacology.org/GRAC/searchPage.jsp").toURI());
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
			}
		});
		gridPane2.add(l1, 0, 2);
		gridPane2.add(l2, 0, 3);
		gridPane2.add(l3, 0, 4);
		Button citeButton = new Button("Cite an IUPHAR dataset");
		citeButton.setId("btnLogin");
		citeButton.setOnAction(event -> {
			buildUserScene();
			searchBox.clear();
			this.stage.setScene(userScene);
			citeStage.hide();
		});
		gridPane2.add(citeButton, 0, 0);
		gridPane.setMaxSize(300, 200);
		gridPane.setMinSize(300, 200);
		gridPane2.setMaxSize(300, 200);
		gridPane2.setMinSize(300, 200);
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
		Label lblUserName = new Label("Username:");
		final TextField txtUserName = new TextField();
		Label lblPassword = new Label("Password:");
		final PasswordField pf = new PasswordField();
		Button btnLogin = new Button("Login");
		btnLogin.setId("btnLogin");
		final Label dbsMessage = new Label("Admin Login");
		dbsMessage.setId("labelAdmin");
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
		dropShadow.setOffsetX(1);
		dropShadow.setOffsetY(1);
		dropShadow.setRadius(5);
		dropShadow.setColor(Color.gray(0.3));
		// Adding text and DropShadow effect to it
		Text text = new Text("DataCite: A Data Citation System");
		text.setTextAlignment(TextAlignment.CENTER);
//		text.setFont(Font.font("Courier New", FontWeight.BOLD, 38));
//		text.setEffect(dropShadow);
		// Adding text to HBox
		hb.setAlignment(Pos.CENTER);
		hb.getChildren().add(text);
		// Add ID's to Nodes
		gp.setId("loginPane");
		gridPane.setId("loginPaneChild");
		gridPane2.setId("loginPaneChild");
		btnLogin.setId("btnLogin");
		text.setId("title");
		btnLogin.setOnAction(event -> {
			// TODO: add OAuth Authentication
			String checkUser = txtUserName.getText().toString();
			String checkPw = pf.getText().toString();
//			if (checkUser.equals("admin") && checkPw.equals("admin")) {
//				lblMessage.setTextFill(Color.GREEN);
//			} else {
//				lblMessage.setText("Incorrect user or password.");
//				lblMessage.setTextFill(Color.RED);
//				return;
//			}
			buildViewScene();
			txtUserName.setText("");
			pf.setText("");
			this.stage.setScene(viewScene);
		});
		// Add HBox and GridPane layout to BorderPane(id is bp) Layout
		gp.add(hb, 0, 0);
		hbox.getChildren().addAll(gridPane, gridPane2);
		gp.add(hbox, 0, 1);
		// LOGIN SCENE
		loginScene = new Scene(gp);
		loginScene.getStylesheets().add(QBEApp.class.getResource("style.css").toExternalForm());
	}
	
	/** ===================================================================
	 * Build the View scene 
	 * Show all data view and citation views */
	private void buildViewScene() {
        TextArea textFieldDataViewDataLog = new TextArea();
        textFieldDataViewDataLog.setEditable(false);
        textFieldDataViewDataLog.setPrefHeight(60);
        DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetX(1);
        dropShadow.setOffsetY(1);
        dropShadow.setRadius(2);
		Text textDataLog = new Text("    SQL: ");
		Text textDataLog2 = new Text("Datalog: ");
        textDataLog.setId("text");
        textDataLog2.setId("text");
        textDataLog.setFont(Font.font("Courier New", FontWeight.BOLD, 18));
        textDataLog.setFill(Color.WHITE);
        textDataLog.setEffect(dropShadow);
        textDataLog2.setFont(Font.font("Courier New", FontWeight.BOLD, 18));
        textDataLog2.setFill(Color.WHITE);
        textDataLog2.setEffect(dropShadow);
//        hbox.getChildren().addAll(textDataLog, textFieldDataViewDataLog);
        HBox hbox = new HBox();
        HBox.setHgrow(textFieldDataViewDataLog, Priority.ALWAYS);
        hbox.setAlignment(Pos.CENTER);
        hbox.setPadding(new Insets(5,0,0,0));
        
        
        Button signOut = new Button("Sign Out");
		signOut.setFont(Font.font("Courier New", FontWeight.BLACK, 14));
		signOut.setId("buttonGen");
		signOut.setOnAction(e ->{
			data.clear();
			dataViewList.clear();
			lblMessage.setText("");
			this.stage.setScene(loginScene);
		});
		HBox hboxSignout = new HBox();
		hboxSignout.setSpacing(5);
		hboxSignout.setAlignment(Pos.CENTER_RIGHT);
		Button btgeneratedQueries = new Button("User Generated Queries");
		btgeneratedQueries.setId("buttonGen");
		btgeneratedQueries.setFont(Font.font("Courier New", FontWeight.BLACK, 14));
		hboxSignout.getChildren().addAll(btgeneratedQueries, signOut);
		
		// Show generated queries button
		btgeneratedQueries.setOnAction(e -> {
			dataGenerated.clear();
			setGQ();
			buildGeneratedScene();
			this.citeStage.setScene(generatedScene);
			citeStage.show();
		});
		
        VBox vboxBottom = new VBox();
        vboxBottom.setSpacing(5);
        vboxBottom.getChildren().addAll(hbox, hboxSignout);
        vboxBottom.setAlignment(Pos.CENTER_RIGHT);
        vboxBottom.setPadding(new Insets(0, 150, 0, 55));

		// Adding HBox
		HBox hb = new HBox();
		hb.setPadding(new Insets(20,20, 20, 150));
		DropShadow dropShadow_2 = new DropShadow();
        dropShadow_2.setOffsetX(2);
        dropShadow_2.setOffsetY(2);
		dropShadow_2.setColor(Color.gray(0.3));
		Text text = new Text("Citation Management");
		text.setId("viewTitle");
		text.setFont(Font.font("Courier New", FontWeight.BOLD, 28));
		text.setEffect(dropShadow_2);
		hb.getChildren().add(text);

		// Reflection for gridPane
		BorderPane bp = new BorderPane();
		bp.setId("bp");
		bp.setPadding(new Insets(10, 50, 20, 50));
		
		// GridPane data views
		GridPane gridPaneDataViews = new GridPane();
		gridPaneDataViews.setPadding(new Insets(20, 20, 20, 20));
		gridPaneDataViews.setAlignment(Pos.CENTER);
		gridPaneDataViews.setHgap(5);
		gridPaneDataViews.setVgap(5);
		Label lableDataviews = new Label("View Definitions");
		lableDataviews.setFont(Font.font("Courier New", FontWeight.BLACK, 18));
		// ListView Data Views
		ListView<String> listViewDataViews = new ListView<>();
		listViewDataViews.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		listViewDataViews.setId("coloredList");
//		listViewDataViews.setPrefWidth(400);
		listViewDataViews.setStyle("-fx-font-size:15.0;");
//		ObservableList<String> listDataViews = FXCollections.observableArrayList(Database.getDataViews());
		listViewDataViews.setItems(listDataViews);
		// ListView Citation Views
		ListView<String> listViewCitationView = new ListView<>();
		listViewCitationView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		listViewCitationView.setId("coloredList");
//				listViewCitationView.setPrefWidth(400);
		listViewCitationView.setStyle("-fx-font-size:15.0;");
		listViewCitationView.setItems(listCitationViews);
		
		//checkbox for view definition
		HBox hboxCheck = new HBox();
	    CheckBox check = new CheckBox();
	    check.setPadding(new Insets(0, 0, 0, 5));
		check.setOnAction((event) -> {
			        if(!check.isSelected()) {
			        	listViewDataViews.getSelectionModel().clearSelection();
			        }
		});
		hboxCheck.getChildren().addAll(lableDataviews, check);
		hboxCheck.setSpacing(120);
		
		// checkbod for citation views
		HBox hboxCheck1 = new HBox();
		CheckBox check1 = new CheckBox();
		check1.setPadding(new Insets(0, 0, 0, 5));
		check1.setOnAction((event) -> {
					        if(!check1.isSelected()) {
					        	listViewCitationView.getSelectionModel().clearSelection();
					        }
		});

		listViewDataViews.setOnMouseClicked(event -> {
            String dv = listViewDataViews.getSelectionModel().getSelectedItem();
            if (dv == null) return;
            check.setSelected(true);
//            String dataViewDataLog = Database.getDataViewDataLog(dv);
            // get connection with citations
            Vector<String> block_names = new Vector<String>();
    		if (dv !=null && !dv.isEmpty()) {
					try {
						Vector<String> q_names = Query_operation.get_connection_citation_with_query(dv, block_names);
						Vector<String> cites = q_names;
						ObservableList<java.lang.String> allCites = listViewCitationView.getItems();
						for(int i = 0; i < allCites.size(); i++) {
							if(cites.contains(allCites.get(i))) {
								listViewCitationView.getSelectionModel().select(i);
								check1.setSelected(true);
							} else {
								listViewCitationView.getSelectionModel().clearSelection(i);
							}
						}
//						for(Node n : listViewCitationView.lookupAll(".list-cell")) {
//	    	            	Cell cell = (Cell) n;
//	    	            	if(cell.getText() != null) {
//	    	            		for(String s : cites) {
//	    	            			if(cell.getText().equals(s)) {
//	    	            				
//	    	            				listViewCitationView.se
////	    	            				cell.setStyle("-fx-background-color: tomato;");
//	    	            			} else {
//	    	            				cell.setStyle("-fx-border-color: none;");
//	    	            			}
//	    	            		}
//	    	            	}
//	    	            }
					} catch (Exception e1) {
						e1.printStackTrace();
					}
    		}
    		
            try {
				Query currentQuery = view_operation.get_view_by_name(dv);
				System.out.println("[current view]" + dv);
				addDataFromQuery(currentQuery);
			} catch (Exception e) {
				e.printStackTrace();
			}
            hbox.getChildren().clear();
            hbox.getChildren().addAll(textDataLog2, textFieldDataViewDataLog);
            if (!hbox.isVisible()) hbox.setVisible(true);
            List<Entry> list = new ArrayList<>();
    		list.addAll(data);
    		if (textFieldDataViewDataLog != null) textFieldDataViewDataLog.setText(splitSQL(Util.convertToDatalogOriginal(list)));
    		data.clear();
        });
		
        Button buttonEditDataView = new Button("Edit");
        buttonEditDataView.setOnAction(event -> {
        	dv = listViewDataViews.getSelectionModel().getSelectedItem();
        	if (dv == null || dv.isEmpty()) return;
        	dataQuery.clear();
        	setCQ(dv);
        	buildDbaScene();
			stage.setScene(dbaScene);
            hbox.setVisible(false);
            if (dv == null) return;
            dbaDataViewDataTab.setText("Data View: " + dv);
//            dbaListCitationViews.setAll(Database.getCitationViews(dbaDataViewDataTab.getText().split(":")[1].trim()));
//            dbaListCitationViews.setAll(Database.getCitationViews(dv));
            Query currentQuery;
			data.clear();
			searchBox.clear();
			try {
//				// show view info in dba scene
				currentQuery = view_operation.get_view_by_name(dv);
				addDataFromQuery(currentQuery);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("data view edit error");
			}
			citeStage.hide();
        });
		Button buttonAddDataView = new Button("Add");
		buttonAddDataView.setOnAction(event -> {
			dataQuery.clear();
			searchBox.clear();
			dbaListCitationViews = FXCollections.observableArrayList();
			dbaListCitationViews.clear();
			dbaListBlock.clear();
			buildDbaScene();
			stage.setScene(dbaScene);
			count ++;
			String randomName = new java.lang.String();
			randomName = "Untitled" + count;
			// reset the lambda hox and the data preview tableview
			data.clear();
			datalogTextArea.clear();
			hBoxLambda.getChildren().clear();
			dataView.getItems().clear();
			dataView.getColumns().clear();
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.setTitle("Succeed");
//            alert.setHeaderText(null);
//            alert.setContentText("The data view " + randomName + " is successfully created");
//            alert.showAndWait();
//			listDataViews.add(randomName);
			dv = randomName;
            dbaDataViewDataTab.setText("Data View: " + randomName);
//            dbaListCitationViews.setAll(Database.getCitationViews(dbaDataViewDataTab.getText().split(":")[1].trim()));
            citeStage.hide();
		});
		Button buttonDeleteDataView = new Button("Delete");
		buttonDeleteDataView.setOnAction(event -> {
            ObservableList<java.lang.String> items = listViewDataViews.getSelectionModel().getSelectedItems();
//            dbaListCitationViews.remove(dv);
//            Database.deleteDataViewByName(dv);
            for(String s : items) {
            	String dv = s;
            	try {
    				citation_view_operation.delete_connection_view_with_citations(dv, dv);
    				citation_view_operation.delete_citation_views(dv);
    				view_operation.delete_view_by_name(dv);
//    				Alert alert = new Alert(Alert.AlertType.INFORMATION);
//    	            alert.setTitle("Succeed");
//    	            alert.setHeaderText(null);
//    	            alert.setContentText("The data view is successfully deleted");
//    	            alert.showAndWait();
    				System.out.println("The data view is successfully deleted");
    				listDataViews.remove(dv);
    			} catch (Exception e) {
    				System.out.println("view delete error");
    				e.printStackTrace();
    			}
            }
		});
		GridPane.setHgrow(listViewDataViews, Priority.ALWAYS);
		GridPane.setVgrow(listViewDataViews, Priority.ALWAYS);
		
		gridPaneDataViews.add(hboxCheck, 0, 0, 3, 1);
		gridPaneDataViews.add(listViewDataViews, 0, 1, 3, 1);
		HBox hboxButton = new HBox();
		hboxButton.setSpacing(5);
		hboxButton.getChildren().addAll(buttonEditDataView, buttonAddDataView, buttonDeleteDataView);
		gridPaneDataViews.add(hboxButton, 0, 2);
//        gridPaneDataViews.add(buttonEditDataView, 0, 2);
//		gridPaneDataViews.add(buttonAddDataView, 1, 2);
//		gridPaneDataViews.add(buttonDeleteDataView, 2, 2);
		gridPaneDataViews.setId("bproot");

		// GridPane citation views
		GridPane gridPaneCitationViews = new GridPane();
		gridPaneCitationViews.setPadding(new Insets(20, 20, 20, 20));
		gridPaneCitationViews.setAlignment(Pos.CENTER);
		gridPaneCitationViews.setHgap(5);
		gridPaneCitationViews.setVgap(5);
		Label lableCitationViews = new Label("Citation Queries");
		lableCitationViews.setFont(Font.font("Courier New", FontWeight.BLACK, 18));
//		// ListView Citation Views
//		ListView<String> listViewCitationView = new ListView<>();
//		listViewCitationView.setId("coloredList");
////		listViewCitationView.setPrefWidth(400);
//		listViewCitationView.setStyle("-fx-font-size:15.0;");
//		listViewCitationView.setItems(listCitationViews);
		
	    hboxCheck1.getChildren().addAll(lableCitationViews, check1);
		hboxCheck1.setSpacing(120);
		listViewCitationView.setOnMouseClicked(event -> {
			cv = listViewCitationView.getSelectionModel().getSelectedItem();
			if (cv == null || cv.isEmpty()) return;
			else {
				check1.setSelected(true);
				try {
					Vector<String> views = citation_view_operation.get_views(cv);
					if(views != null) {
						ObservableList<String> allViews = listViewDataViews.getItems();
						for(int i = 0; i < allViews.size(); i++) {
							if(views.contains(allViews.get(i))) {
								check.setSelected(true);
								listViewDataViews.getSelectionModel().select(i);
							} else {
								listViewDataViews.getSelectionModel().clearSelection(i);
							}
						}
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			
				hbox.getChildren().clear();
				hbox.getChildren().addAll(textDataLog, textFieldDataViewDataLog);
				if (!hbox.isVisible()) hbox.setVisible(true);
				try {
					Query q_datalog = Query_operation.get_query_by_name(cv);
					String q_sql = Query_converter.datalog2sql(q_datalog);
					if (textFieldDataViewDataLog != null) textFieldDataViewDataLog.setText(splitSQL(q_sql));
					cv = "";
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
		Button buttonAddCitationView = new Button("Add");
		buttonAddCitationView.setOnAction(event -> {
			buildCitationScene();
			dv = "";
			dataNew.clear();
			searchBox.clear();
			datalogTextAreaNew.clear();
			hBoxLambdaNew.getChildren().clear();
			dataViewNew.getItems().clear();
			dataViewNew.getColumns().clear();
			cv = "Untitled";
			this.citeStage.setScene(citationScene);
			this.citeStage.setTitle("Build Citation Query");
			this.citeStage.show();
		});
		Button buttonDeleteCitationView = new Button("Delete");
		buttonDeleteCitationView.setOnAction(event -> {
			ObservableList<java.lang.String> items = listViewCitationView.getSelectionModel().getSelectedItems();
			if (items == null || items.isEmpty()) return;
			for(String s : items) {
				cv = s;
				try {
//					citation_view_operation.delete_citation_views(cv);
					Query_operation.delete_query_by_name(cv);
//					Alert alert = new Alert(Alert.AlertType.INFORMATION);
//		            alert.setTitle("Succeed");
//		            alert.setHeaderText(null);
//		            alert.setContentText("The citation query view is successfully deleted");
//		            alert.showAndWait();
					System.out.println("The citation query view is successfully deleted");
		            listCitationViews.remove(cv);
				} catch (Exception e) {
					System.out.println("citation delete error!");
					e.printStackTrace();
				}
			}
		});
		GridPane.setHgrow(listViewCitationView, Priority.ALWAYS);
		GridPane.setVgrow(listViewCitationView, Priority.ALWAYS);

		gridPaneCitationViews.add(hboxCheck1, 0, 0);
		gridPaneCitationViews.add(listViewCitationView, 0, 1, 2, 1);
		HBox hboxButton2 = new HBox();
		hboxButton2.getChildren().addAll(buttonAddCitationView, buttonDeleteCitationView);
		hboxButton2.setSpacing(5);
		gridPaneCitationViews.add(hboxButton2, 0, 2);
//		gridPaneCitationViews.add(buttonAddCitationView, 0, 2);
//		gridPaneCitationViews.add(buttonDeleteCitationView, 1, 2);
		gridPaneCitationViews.setId("bproot");
		
		// Generated query views
		//TODO
		
		GridPane gridPaneQueryViews = new GridPane();
		gridPaneQueryViews.setPadding(new Insets(0, 20, 0, 20));
		gridPaneQueryViews.setAlignment(Pos.CENTER);
		gridPaneQueryViews.setHgap(5);
		gridPaneQueryViews.setVgap(5);
		gridPaneQueryViews.setId("bproot");
		
		Label labelQuery = new Label("Generated Queries");
		labelQuery.setFont(Font.font("Courier New", FontWeight.BLACK, 18));
		gridPaneQueryViews.add(labelQuery, 0, 0);
		ListView<String> listViewQuery = new ListView<>();
		listViewQuery.setStyle("-fx-font-size:15.0;");
		listViewQuery.setItems(listQueries);
		gridPaneQueryViews.add(listViewQuery, 0, 1, 2, 1);
		GridPane.setHgrow(listViewQuery, Priority.ALWAYS);
		GridPane.setVgrow(listViewQuery, Priority.ALWAYS);
		listViewQuery.setOnMouseClicked(event ->{
			String qv = listViewQuery.getSelectionModel().getSelectedItem();
			if (qv == null || qv.isEmpty()) return;
			else {
				hbox.getChildren().clear();
				hbox.getChildren().addAll(textDataLog2, textFieldDataViewDataLog);
				if (!hbox.isVisible()) hbox.setVisible(true);
				try {
					Query query = query_storage.get_user_query_by_id(Integer.parseInt(qv));
					if (textFieldDataViewDataLog != null) textFieldDataViewDataLog.setText(query.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
		});
		
		// Add HBox and GridPane layout to BorderPane Layout
		gridPaneDataViews.setPrefWidth(400);
		gridPaneCitationViews.setPrefWidth(400);
		listViewDataViews.setPrefWidth(300);
		listViewCitationView.setPrefWidth(300);
		
		HBox hboxCenter = new HBox();
		gridPaneDataViews.setId("viewPaneChild");
		gridPaneCitationViews.setId("viewPaneChild");
		hboxCenter.setSpacing(40);
		hboxCenter.setAlignment(Pos.CENTER);
		hboxCenter.getChildren().addAll(gridPaneDataViews, gridPaneCitationViews);
		bp.setTop(hb);
		bp.setCenter(hboxCenter);
        hbox.setVisible(false);
//		bp.setBottom(hbox);
        bp.setId("viewPane");
        bp.setBottom(vboxBottom);
		viewScene = new Scene(bp);
		viewScene.getStylesheets().add(QBEApp.class.getResource("style.css").toExternalForm());
	}

	/** ===================================================================
	 * Build the User scene
	 */
	private void buildUserScene() {
		gridUser = new GridPane();
		gridUser.setId("userPane");
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

//		final TableView<ObservableList> userDataView = new TableView();
		GridPane.setVgrow(userDataView, Priority.ALWAYS);
		GridPane.setHgrow(userDataView, Priority.ALWAYS);
		userDataView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		userDataView.setItems(dataViewList);
		gridUserSub.add(userDataView, 0, 3, 2, 1);
		
		// Pane
		TableView<Entry> tableViewUser = new TableView<Entry>();
		splitPaneQbe = new SplitPane();
		GridPane.setHgrow(splitPaneQbe, Priority.ALWAYS);
		GridPane.setVgrow(splitPaneQbe, Priority.ALWAYS);
		splitPaneQbe.getItems().add(tableViewUser);
		
		gridUserSub.add(buildTopMenu(null, null, userDataView, true), 1, 0);
		gridUserSub.add(splitPaneQbe, 0, 1, 2, 1);

		// Table View
		tableViewUser.setEditable(true);
		tableViewUser.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		expanderColumn = new TableRowExpanderColumn<>(this::createAnotherEditor);
		expanderColumn.setMaxWidth(30);
		expanderColumn.setMinWidth(30);
		TableColumn<Entry, String> tableColumnUser = new TableColumn<>("Table");
		TableColumn<Entry, String> fieldColumnUser = new TableColumn<>("Field");
		TableColumn<Entry, Boolean> showColumnUser = new TableColumn<>("Show");
		TableColumn<Entry, String> criteraColumnUser = new TableColumn<>("Criteria");
        TableColumn<Entry, String> joinColumnUser = new TableColumn<>("Join");
		tableColumnUser.setCellValueFactory(new PropertyValueFactory<>("table"));
		fieldColumnUser.setCellValueFactory(new PropertyValueFactory<>("field"));
		showColumnUser.setCellValueFactory(new PropertyValueFactory<>("show"));
//		showColumnUser.setCellFactory(CheckBoxTableCell.forTableColumn(showColumnUser));
		showColumnUser.setCellFactory(new Callback<TableColumn<Entry, Boolean>, //
		        TableCell<Entry, Boolean>>() {
		            @Override
		            public TableCell<Entry, Boolean> call(TableColumn<Entry, Boolean> p) {
		                CheckBoxTableCell<Entry, Boolean> cell = new CheckBoxTableCell<Entry, Boolean>();
		                cell.setAlignment(Pos.TOP_CENTER);
		                cell.setPadding(new Insets(5,0,0,0));
		                return cell;
		            }
		        });
		showColumnUser.setEditable(true);
		criteraColumnUser.setCellValueFactory(new PropertyValueFactory<>("criteria"));
		criteraColumnUser.setCellFactory(TextFieldTableCell.forTableColumn());
        joinColumnUser.setCellValueFactory(new PropertyValueFactory<>("join"));
        joinColumnUser.setCellFactory(TextFieldTableCell.forTableColumn());

		tableViewUser.getColumns().addAll(expanderColumn, tableColumnUser, fieldColumnUser, showColumnUser, criteraColumnUser, joinColumnUser);
		tableViewUser.setItems(data);
//		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tableColumnUser.setPrefWidth(200);
		fieldColumnUser.setPrefWidth(200);
		showColumnUser.setPrefWidth(100);
		criteraColumnUser.setPrefWidth(205);
		joinColumnUser.setPrefWidth(260);
		paddingCriteriaUser = expanderColumn.getPrefWidth() + tableColumnUser.getPrefWidth() + fieldColumnUser.getPrefWidth() + showColumnUser.getPrefWidth();
//		System.out.println("padding"+paddingCriteriaUser);

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
			setDataView(null, userDataView, true);
		});
		hBoxPrevNext.getChildren().addAll(prevButton, nextButton);
		GridPane.setHgrow(hBoxPrevNext, Priority.ALWAYS);
		gridUserSub.add(hBoxPrevNext, 1, 4);

		hBoxGenCitation = new HBox();
		hBoxGenCitation.setAlignment(Pos.CENTER_RIGHT);
		Button genButton = new Button("Generate Citation");
		// TODO 
		genButton.setOnAction(e -> {
			if (data == null || data.isEmpty()) {
				Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Alert");
                alert.setHeaderText(null);
                alert.setContentText("Empty Query!");
                alert.showAndWait();
                return;
			}
//			System.out.println(dataView.getSelectionModel().getSelectedItems().size());
			ObservableList<String> listCitations = FXCollections.observableArrayList();
			ObservableList<Integer> indices = userDataView.getSelectionModel().getSelectedIndices();
			if (indices == null || indices.isEmpty()) {
				try {
					// generate all citations
					
					Vector<String> agg_citations = Tuple_reasoning1.tuple_gen_agg_citations(userGeneratedQuery);
					for (String s : agg_citations) {
						listCitations.add(s);
					}
				} catch (ClassNotFoundException | SQLException | JSONException e1) {
					e1.printStackTrace();
				}
			} else {
				ObservableList<ObservableList> tuples = userDataView.getSelectionModel().getSelectedItems();
				System.out.println(tuples);
				Vector<Vector<String>> names = new Vector<Vector<String>>();
				int l = tuples.size();
				for(int i = 0; i < l; i++) {
					Vector<String> t = new Vector<String>();
					int len = tuples.get(i).size();
					for(int j = 0; j < len - 1; j++) {
						t.add("" + tuples.get(i).get(j) + "");
					}
					names.add(t);
				}
				System.out.println("names" + names);
				// generated selected citations
				try {
					Vector<String> subset_agg_citations = Tuple_reasoning1.tuple_gen_agg_citations(ids, userGeneratedQuery, heads, citation_view_map);
					for (String s : subset_agg_citations) {
						listCitations.add(s);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			System.out.println("[Citation List] " + listCitations);
			GridPane gridCg = buildGridCg(listCitations);
			this.citeStage.setScene(new Scene(gridCg, 500, 300));
			this.citeStage.setTitle("Citations");
			this.citeStage.show();
		
		});
		genButton.setId("buttonGen");
		hBoxGenCitation.getChildren().add(genButton);
		gridUserSub.add(hBoxGenCitation, 1, 2);
		userScene = new Scene(gridUser);
		userScene.getStylesheets().add(QBEApp.class.getResource("style.css").toExternalForm());
	}
	
	private HBox buildTopMenu(TextArea textArea, HBox hBoxLambda, TableView<ObservableList> dataView, boolean toCite) {
		final HBox hBox = new HBox();
		hBox.setAlignment(Pos.CENTER_RIGHT);
		final Button runButton = new Button(" Run  ");
		final Button clearButton = new Button("Clear ");
		final Button backButton = new Button("Return");
		runButton.setId("bevel-grey");
		clearButton.setId("bevel-grey");
		backButton.setId("bevel-grey");
		
		runButton.setOnAction(e -> {
			if (stage.getScene() == dbaScene || stage.getScene() == userScene) {
				if (data.isEmpty() ) return;
				List<Entry> list = new ArrayList<>();
				list.addAll(data);
				if (textArea != null) textArea.setText(Util.convertToDatalogOriginal(list) + "\n");
				lambdasAll.clear();
				lambdaIndex.clear();
				lambdaSQL = Util.convertToSQLWithLambda(list, toCite);
				datalog = Util.convertToDatalogOriginal(list);
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
			} else if (citeStage.isShowing()) {
				if ( dataNew.isEmpty()) return;
				List<Entry> listNew = new ArrayList<>();
				listNew.addAll(dataNew);
				if (datalogTextAreaNew != null) datalogTextAreaNew.setText(Util.convertToDatalogOriginal(listNew) + "\n");
				lambdasAll.clear();
				lambdaIndex.clear();
				lambdaSQL = Util.convertToSQLWithLambda(listNew, toCite);
				System.out.println("[DEBUG] lambdaSQL: " + lambdaSQL);
				lambdas = Util.getLambda(listNew);
				for (String lambda : lambdas) {
					System.out.println(lambda);
					String table = lambda.substring(0, lambda.indexOf('.'));
					String field = lambda.substring(lambda.indexOf('.') + 1);
					List<String> temp = Database.getDistincts(table, field);
					lambdasAll.add(temp);
					lambdaIndex.add(0);
				}
				setDataView(hBoxLambda, dataView, toCite);
			}
		});
		clearButton.setOnAction(e -> {
			
			if ((stage.getScene() == dbaScene || stage.getScene() == userScene) && !citeStage.isShowing()) {
				data.clear();
				if (textArea != null) textArea.clear();
				dataView.getColumns().clear();
				dataViewList.clear();
			} else if (citeStage.isShowing()) {
				dataNew.clear();
				if (textArea != null) textArea.clear();
				dataView.getColumns().clear();
				dataViewListNew.clear();
			}
		});
		backButton.setOnAction(e -> {
			if (textArea != null) textArea.clear();
			dataView.getColumns().clear();
			
			if (citeStage.isShowing()){
				dataNew.clear();
				dataViewListNew.clear();
				citeStage.hide();
				stage.show();
			}
			else if(queryStage.isShowing()) {
				data.clear();
				dataViewList.clear();
				dataView.getColumns().clear();
				queryStage.hide();
				stage.show();
				buildViewScene();
				this.stage.setScene(viewScene);
			}
			else if (stage.getScene() == dbaScene && !citeStage.isShowing()){
//				System.out.println("dbaScene");
				data.clear();
				dataViewList.clear();
				this.stage.setScene(viewScene);
				if (gridDba.getChildren().contains(vboxRightCQ)) {
					stage.setWidth(stage.getWidth()-220);
	                gridDba.getChildren().remove(vboxRightCQ);
	                Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
	                stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
	                stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
				}
			}
			else if (stage.getScene() == viewScene && !citeStage.isShowing()) {
//				System.out.println("viewScene");
				this.stage.setScene(loginScene);
			}
			else if (stage.getScene() == userScene) {
//				System.out.println("UserScene");
				data.clear();
				dataViewList.clear();
				this.stage.setScene(loginScene);
			}
		});
		HBox.setMargin(runButton, new Insets(0, 5, 0, 5));
		HBox.setMargin(clearButton, new Insets(0, 5, 0, 5));
		HBox.setMargin(backButton, new Insets(0, 5, 0, 5));
		if (citeStage.isShowing())
			backButton.setVisible(false);
		hBox.getChildren().addAll(runButton, clearButton, backButton);
		return hBox;
	}

	private TextField buildSearchBox(TreeView<TreeNode> treeView) {
//		TextField searchBox = new TextField();
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

	/** ===================================================================
	 * Build the Dba scene
	 */
	private void buildDbaScene() {
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
        vboxRight.setPadding(new Insets(8,0,2,0));

		Label labelCv = new Label("Citation Queries");
		labelCv.setId("whiteLabel");
		labelCv.setMinWidth(165);
//		labelCv.setStyle("-fx-font-size: 16px;");
		
//		ListView<String> listViewCv = new ListView<>();
		HBox hboxQuery = new HBox();
		ComboBox<String> comboBlock = new ComboBox<>();
		hboxQuery.getChildren().addAll(comboBlock);
//		listViewCv.setCellFactory(lv -> {
//			ListCell<String> cell = new ListCell<>();
//			ContextMenu contextMenu = new ContextMenu();
//			MenuItem editItem = new MenuItem();
//			editItem.textProperty().bind(Bindings.format("Edit \"%s\"", cell.itemProperty()));
//			editItem.setOnAction(event -> {
//				String item = cell.getItem();
//				// code to edit item...
//			});
//			MenuItem deleteItem = new MenuItem();
//			deleteItem.textProperty().bind(Bindings.format("Delete \"%s\"", cell.itemProperty()));
//			deleteItem.setOnAction(event -> listViewCv.getItems().remove(cell.getItem()));
//			contextMenu.getItems().addAll(editItem, deleteItem);
//			cell.textProperty().bind(cell.itemProperty());
//			cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
//				if (isNowEmpty) {
//					cell.setContextMenu(null);
//				} else {
//					.setContextMenu(contextMenu);
//				}
//			});
//			return cell;
//		});
		TableView<CQuery> listViewCv = new TableView<CQuery>();
		listViewCv.setItems(dataQuery);
		listViewCv.setMinWidth(180);
		listViewCv.setEditable(true);
		TableColumn<CQuery, String> nameCol = new TableColumn<CQuery, String>("Name");
		TableColumn<CQuery, String> blockCol = new TableColumn<CQuery, String>("Block");
		listViewCv.getColumns().addAll(nameCol, blockCol);
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		nameCol.setMinWidth(100);
		nameCol.setStyle( "-fx-alignment: CENTER-LEFT;");
		blockCol.setMinWidth(75);
		blockCol.setStyle( "-fx-alignment: CENTER-LEFT;");
//		blockCol.setCellValueFactory(cellData -> cellData.getValue().blockProperty());
		blockCol.setCellValueFactory(new PropertyValueFactory<>("block"));
		ObservableList<String> blockList = FXCollections.observableArrayList(
				new java.lang.String("Author"),
				new java.lang.String("Title"));
//		blockCol.setCellFactory(ComboBoxTableCell.forTableColumn(blockList));
		blockCol.setCellFactory(new Callback<TableColumn<CQuery,String>, TableCell<CQuery,String>>() {
			public TableCell<CQuery, String> call(TableColumn<CQuery,String> tc) {
//				TableCell<CQuery, String> cell = new TableCell<CQuery, String>(){
//					protected void updateItem(final String item, boolean empty) {
//						super.updateItem(item, empty);
//						ComboBox combo = new ComboBox(blockList);
//						if (item != null && !empty) {
//							setGraphic(combo);
//							combo.setValue(item);
//						}
//					}
//				};
				TableCell<CQuery, String> cell = new ComboBoxTableCell<CQuery, String>(blockList);
				cell.setBackground(Background.EMPTY);
			    cell.setId("CQ-combo");
				return cell;
			}
		});
		
		listViewCv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		// setTooltip to the each query name on each row
		nameCol.setCellFactory(col -> new TextFieldTableCell<CQuery, String>(){
			private Tooltip tp = new Tooltip();
			public void updateItem (String name, boolean empty) {
				super.updateItem(name, empty);
				if(name == null|| name.isEmpty()) {
					setTooltip(null);
				} else {
					try {
						Query query_datalog = Query_operation.get_query_by_name(name);
						String query_sql = Query_converter.datalog2sql(query_datalog);
						query_sql = splitSQL(query_sql);
						tp.setText(query_sql);
						setTooltip(tp);
					} catch (ClassNotFoundException | SQLException e) {
						e.printStackTrace();
					}
				}
			}
		});
//		listViewCv.setRowFactory(tv -> new TableRow<CQuery>() {
//			private Tooltip tp = new Tooltip();
//			public void updateItem (CQuery CQ, boolean empty) {
//				super.updateItem(CQ, empty);
//				if(CQ == null || empty) {
//					setTooltip(null);
//				} else {
//					try {
//						Query query_datalog = Query_operation.get_query_by_name(CQ.getName());
//						String query_sql = Query_converter.datalog2sql(query_datalog);
//						query_sql = splitSQL(query_sql);
//						tp.setText(query_sql);
//						setTooltip(tp);
//					} catch (ClassNotFoundException | SQLException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		});
		
		Button btDataViewExisted = new Button("Add");
        btDataViewExisted.setOnAction(e -> {
        	stage.setWidth(stage.getWidth()+230);
            Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
            stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
        	gridDba.add(vboxRightCQ, 3, 0, 1, 2);
		});
        
		Button btDataViewSave = new Button("Save");
		btDataViewSave.setOnAction(e -> {
			if (listViewCv.getItems() == null || listViewCv.getItems().isEmpty()) {
				Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Alert");
                alert.setHeaderText(null);
                alert.setContentText("Empty Query");
                alert.showAndWait();
                return;
			}
			// save a valid view before save the corresponding queries
			if (data == null || data.isEmpty() || !listDataViews.contains(dv)) {
				Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Alert");
                alert.setHeaderText(null);
                alert.setContentText("Pls save a valid view first!");
                alert.showAndWait();
                return;
			}
			Vector<String[]> qname_block = new Vector<String[]>();
			for (int i = 0; i < listViewCv.getItems().size(); i++) {
				CQuery CQ = listViewCv.getItems().get(i);
				String q_name = CQ.getName();
				String b_name = CQ.getBlock();
				if (b_name == null || b_name.isEmpty() || b_name.equals("Undef")) {
					Alert alert = new Alert(Alert.AlertType.WARNING);
		            alert.setHeaderText(null);
		            alert.setContentText("Specify block to each query before save!");
		            alert.showAndWait();
					return;
				}
				String[] q_b = {q_name, b_name};
				qname_block.add(q_b);
			}
			try {
				Query_operation.delete_connection_citation_with_query(dv);
				System.out.println("[name+block] " + qname_block);
				Query_operation.add_connection_citation_with_query(dv, qname_block);
			} catch (Exception e1) {
				System.out.println("Add new CQ connection error");
				e1.printStackTrace();
			}
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Citation queries are successfully saved to " + dv);
            alert.showAndWait();
			
		});
		Button btDataViewDelete = new Button("Delete");
        btDataViewDelete.setOnAction(event -> {
        	CQuery CQ = listViewCv.getSelectionModel().getSelectedItem();
        	String cv = CQ.getName();
        	String bname = CQ.getBlock();
        	if (cv !=null && !cv.isEmpty()) {
        		dbaListCitationViews.remove(cv);
        		dbaListBlock.remove(bname);
        		dataQuery.remove(CQ);
        	}

        });
        
		VBox addSaveHBox = new VBox();
		addSaveHBox.setVgrow(btDataViewExisted, Priority.ALWAYS);
		addSaveHBox.setVgrow(btDataViewSave, Priority.ALWAYS);
		addSaveHBox.setSpacing(2);
		addSaveHBox.setPadding(new Insets(2,0,0,0));
		btDataViewExisted.setId("buttonGen");
		btDataViewSave.setId("buttonGen");
		btDataViewDelete.setId("buttonGen");
		btDataViewDelete.setPrefWidth(130);
		btDataViewExisted.setPrefWidth(130);
		btDataViewSave.setPrefWidth(130);
		addSaveHBox.getChildren().addAll(btDataViewExisted, btDataViewDelete, btDataViewSave);
        addSaveHBox.setAlignment(Pos.CENTER);

		VBox.setVgrow(listViewCv, Priority.ALWAYS);
		vboxRight.getChildren().addAll(labelCv, listViewCv, addSaveHBox);
		gridDba.add(vboxRight, 2, 0, 1, 2);
		
		ListView<String> listViewRightCQ = new ListView<String>();
		listViewRightCQ.setMinWidth(180);
//		listViewRightCQ.setCellFactory(lv -> {
//			ListCell<String> cell = new ListCell<String>();
//			ContextMenu contextMenu = new ContextMenu();
//			MenuItem editItem = new MenuItem();
//			editItem.textProperty().bind(Bindings.format("Edit \"%s\"", cell.itemProperty()));
//			editItem.setOnAction(event -> {
//				String item = cell.getItem();
//				// code to edit item...
//			});
//			MenuItem deleteItem = new MenuItem();
//			deleteItem.textProperty().bind(Bindings.format("Delete \"%s\"", cell.itemProperty()));
//			deleteItem.setOnAction(event -> listViewCv.getItems().remove(cell.getItem()));
//			contextMenu.getItems().addAll(editItem, deleteItem);
//			cell.textProperty().bind(cell.itemProperty());
//			cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
//				if (isNowEmpty) {
//					cell.setContextMenu(null);
//				} else {
//				}
//			});
//			return cell;
//		});
		
		listViewRightCQ.setOnMouseClicked(event -> {
//			String dv = dbaDataViewDataTab.getText().split(":")[1].trim();
			String cv = listViewRightCQ.getSelectionModel().getSelectedItem();
			System.out.println(dbaListCitationViews.contains(cv));
			if (!dbaListCitationViews.contains(cv)) {
				dbaListCitationViews.add(cv);
				dataQuery.add(new CQuery(cv, "Undef"));
			}
//			Database.insertDCTuple(dv, cv);
		});
		listViewRightCQ.setItems(listCitationViews);
		
		// set Tooltip to each cell of listViewRightCQ
		listViewRightCQ.setCellFactory(col ->
			new TextFieldListCell<String>(){
				private Tooltip tp = new Tooltip();
				public void updateItem (String name, boolean empty) {
					super.updateItem(name, empty);
					if(name == null|| name.isEmpty()) {
						setTooltip(null);
					} else {
						try {
							Query query_datalog = Query_operation.get_query_by_name(name);
							String query_sql = Query_converter.datalog2sql(query_datalog);
							query_sql = splitSQL(query_sql);
							tp.setText(query_sql);
							setTooltip(tp);
						} catch (ClassNotFoundException | SQLException e) {
							e.printStackTrace();
						}
					}
				}
		});
		
		Label rightCQLabel = new Label("Existing Queries");
		rightCQLabel.setId("whiteLabel");
		rightCQLabel.setMinWidth(180);
//		rightCQLabel.setStyle("-fx-font-size: 16px;");
		
		Button rightBtCQ = new Button("Hide   ");
		rightBtCQ.setId("buttonGen");
		rightBtCQ.setOnAction(e -> {
        	gridDba.getChildren().remove(vboxRightCQ);
        	stage.setWidth(stage.getWidth()-220);
            Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
            stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
		});
		Button btDataView = new Button("Add New");
		btDataView.setId("buttonGen");
        btDataView.setOnAction(e -> {
        	buildCitationScene();
			dataNew.clear();
			datalogTextAreaNew.clear();
			hBoxLambdaNew.getChildren().clear();
			dataViewNew.getItems().clear();
			dataViewNew.getColumns().clear();
        	cv = "Untitled";
			this.citeStage.setScene(citationScene);
			this.citeStage.setTitle("Build Citation Query");
			this.citeStage.show();
//        	stage.setWidth(stage.getWidth()+230);
//            Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
//            stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
//            stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);
//        	gridDba.add(vboxRightCQ, 3, 0, 1, 2);
		});
		
		// Right side pop-up selection citation queries
		vboxRightCQ = new VBox();
		vboxRightCQ.setMaxWidth(190);
        vboxRightCQ.setMinWidth(190);
        vboxRightCQ.setSpacing(2);
		vboxRightCQ.setAlignment(Pos.CENTER);
		btDataView.setPrefWidth(130);
		rightBtCQ.setPrefWidth(130);
		VBox.setVgrow(listViewRightCQ, Priority.ALWAYS);
		vboxRightCQ.setStyle("-fx-border-color: black;-fx-border-insets: 4;-fx-border-width: 2;-fx-border-style: dashed;-fx-border-radius: 5;");
		vboxRightCQ.getChildren().addAll(rightCQLabel, listViewRightCQ, btDataView, rightBtCQ);
		
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
		
//		final TableView<ObservableList> dataView = new TableView();
		GridPane.setVgrow(dataView, Priority.ALWAYS);
		GridPane.setHgrow(dataView, Priority.ALWAYS);
		dataView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		dataView.setItems(dataViewList);
		gridDbaSub.add(dataView, 0, 4, 2, 1);
		
		// Lambda
//		final HBox hBoxLambda = new HBox();
		hBoxLambda.getChildren().add(new Label("Lambda Terms:  "));
		hBoxLambda.getStyleClass().add("hBoxLambda");
		GridPane.setHgrow(hBoxLambda, Priority.ALWAYS);
		gridDbaSub.add(hBoxLambda, 0, 5);
		
		Label datalogLabel = new Label("Datalog:");
//        TextField datalogTextArea = new TextField();
        datalogLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 16));
        datalogTextArea.setFont(Font.font("Courier New", FontWeight.BLACK, 14));
        datalogTextArea.setWrapText(true);
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
		expanderColumn = new TableRowExpanderColumn<>(this::createAnotherEditor);
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
//		showColumn.setCellFactory(check.forTableColumn(showColumn));
		showColumn.setCellFactory(new Callback<TableColumn<Entry, Boolean>, //
		        TableCell<Entry, Boolean>>() {
		            @Override
		            public TableCell<Entry, Boolean> call(TableColumn<Entry, Boolean> p) {
		                CheckBoxTableCell<Entry, Boolean> cell = new CheckBoxTableCell<Entry, Boolean>();
		                cell.setAlignment(Pos.TOP_CENTER);
		                cell.setPadding(new Insets(5,0,0,0));
		                return cell;
		            }
		           
		        });
		showColumn.setEditable(true);
		criteraColumn.setCellValueFactory(new PropertyValueFactory<>("criteria"));
		criteraColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        joinColumn.setCellValueFactory(new PropertyValueFactory<>("join"));
        joinColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		lambdaColumn.setCellValueFactory(new PropertyValueFactory<>("lambda"));
//		lambdaColumn.setCellFactory(CheckBoxTableCell.forTableColumn(lambdaColumn));
		lambdaColumn.setCellFactory(new Callback<TableColumn<Entry, Boolean>, //
		        TableCell<Entry, Boolean>>() {
		            @Override
		            public TableCell<Entry, Boolean> call(TableColumn<Entry, Boolean> p) {
		                CheckBoxTableCell<Entry, Boolean> cell = new CheckBoxTableCell<Entry, Boolean>();
		                cell.setAlignment(Pos.TOP_CENTER);
		                cell.setPadding(new Insets(5,0,0,0));
		                return cell;
		            }
		        });
		lambdaColumn.setEditable(true);

		tableView.getColumns().addAll(expanderColumn, tableColumn, fieldColumn, showColumn, criteraColumn, joinColumn, lambdaColumn);
		tableView.setItems(data);
//		List<Entry> list = new ArrayList<>();
//		list.addAll(data);
//		if (datalogTextArea != null) datalogTextArea.setText(Util.convertToDatalogOriginal(list) + "\n");
//		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		tableColumn.setPrefWidth(100);
		fieldColumn.setPrefWidth(100);
		showColumn.setPrefWidth(70);
		lambdaColumn.setPrefWidth(70);
		criteraColumn.setPrefWidth(205);
		joinColumn.setPrefWidth(260);
//		tableColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.13));
//		fieldColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.13));
//		showColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.07));
//		lambdaColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.075));
//		criteraColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.24));
//		joinColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(0.32));
		paddingCriteriaDba = expanderColumn.widthProperty().get() + tableColumn.widthProperty().get() + fieldColumn.widthProperty().get() + showColumn.widthProperty().get();
		paddingJoin = paddingCriteriaDba + criteraColumn.widthProperty().get();
		// Label data preview
		final Label label_1 = new Label("Data Preview");
		label_1.setId("prompt-text");
		GridPane.setHgrow(label_1, Priority.ALWAYS);
		gridDbaSub.add(label_1, 0, 3);
		
		HBox hboxSq = new HBox();
		hboxSq.setAlignment(Pos.CENTER_RIGHT);
		Button saveViewButton = new Button("Save View Query");
        saveViewButton.setOnAction(event -> {
        	// Edited: Yan
//    		ObservableList<String> listDataViews = FXCollections.observableArrayList(Database.getDataViews());
//        	if (listDataViews.contains(dv)) {
//        		try {
//    				view_operation.delete_view_by_name(dv);
//    				listDataViews.remove(dv);
//    				citation_view_operation.delete_connection_view_with_citations(dv, dv);
//    				citation_view_operation.delete_citation_views(dv);
//    			} catch (Exception e2) {
//    				e2.printStackTrace();
//    			}
//        	}
        	String oldName = dv;
            TextInputDialog dialog = new TextInputDialog(dv);
            dialog.setContentText("Please enter the view query name:");
            dialog.setHeaderText(null);
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String tempName = result.get();
                if (listDataViews.contains(tempName)) {
                	if(!tempName.equals(dv)) {
                		Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Alert");
                        alert.setHeaderText(null);
                        alert.setContentText("Duplicate name, pls re-enter!");
                        alert.showAndWait();
                        return;
                	}
                } else
                	dv = result.get();
            } else {
                return;
            }
            if (data.isEmpty()) return;
            
            Query generatedQuery = addQueryByName(dv,data);
            if (listDataViews.contains(dv)) {
            	try {
                	view_operation.save_view_by_name(oldName, dv, generatedQuery);
                	citation_view_operation.update_citation_view(oldName, dv);
                	Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Succeed");
                    alert.setHeaderText(null);
                    alert.setContentText("The view queries are successfully saved as " + dv);
                    alert.showAndWait();
    			} catch (Exception e1) {
    				e1.printStackTrace();
    			}
            } else {
            	// add new view
            	try {
					view_operation.add(generatedQuery, dv);
					citation_view_operation.add_citation_view(dv);
					citation_view_operation.add_connection_view_with_citations(dv, dv);
					listDataViews.add(dv);
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Succeed");
                    alert.setHeaderText(null);
                    alert.setContentText("The new view queries are successfully saved as " + dv);
                    alert.showAndWait();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
            	
            }
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
		gridDba.setId("dbaPane");

		dbaScene = new Scene(gridDba);
		dbaScene.getStylesheets().add(QBEApp.class.getResource("style.css").toExternalForm());
	}
	
	/** ===================================================================
	 * Build the new citation scene
	 */
    private void buildCitationScene() {
    	gridCitation = new GridPane();
    	gridCitation.setId("citationPane");
        gridCitation.setPadding(new Insets(5, 10, 10, 10));
        gridCitation.setHgap(10);
        gridCitation.setVgap(10);
        gridCitation.setMaxSize(1000, 600);
        
		// TabPane
		TabPane tabPane = new TabPane();
		citationDataViewDataTab = new Tab("Citation Query Builder");
		citationDataViewDataTab.setText("Citation Query: Untitled");
		citationDataViewDataTab.setClosable(false);
		tabPane.getTabs().setAll(citationDataViewDataTab);
		tabPane.setTabClosingPolicy(TabClosingPolicy.SELECTED_TAB);
		tabPane.getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);
		GridPane.setHgrow(tabPane, Priority.ALWAYS);
		GridPane.setVgrow(tabPane, Priority.ALWAYS);
		gridCitation.add(tabPane, 1, 0, 1, 2);
		
        gridCitationSub = new GridPane();
        gridCitationSub.setPadding(new Insets(2, 5, 2, 5));
        gridCitationSub.setHgap(5);
        gridCitationSub.setVgap(5);
        citationDataViewDataTab.setContent(gridCitationSub);

        
		Label label_0 = new Label("Citation Query Builder");
		label_0.setId("prompt-text");
		GridPane.setHgrow(label_0, Priority.ALWAYS);
		gridCitationSub.add(label_0, 0, 0);

		// TreeView and search box
		TreeView<TreeNode> samplesTreeView = new TreeView<TreeNode>();
		buildSampleTree(samplesTreeView, null, true);
		buildSampleTreeView(samplesTreeView);
		GridPane.setVgrow(samplesTreeView, Priority.ALWAYS);
		gridCitation.add(samplesTreeView, 0, 1);
		TextField searchBox = buildSearchBox(samplesTreeView);
		GridPane.setMargin(searchBox, new Insets(5, 0, 0, 0));
		GridPane.setHgrow(searchBox, Priority.NEVER);
		gridCitation.add(searchBox, 0, 0);

		// Pane
		TableView<Entry> tableView = new TableView<Entry>();
		splitPaneQbeNew = new SplitPane();
		GridPane.setHgrow(splitPaneQbeNew, Priority.ALWAYS);
		GridPane.setVgrow(splitPaneQbeNew, Priority.ALWAYS);
		splitPaneQbeNew.getItems().add(tableView);
		
		// dataViewNew is the data preview table in citation builder
		GridPane.setVgrow(dataViewNew, Priority.ALWAYS);
		GridPane.setHgrow(dataViewNew, Priority.ALWAYS);
		dataViewNew.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		dataViewNew.setItems(dataViewListNew);
		gridCitationSub.add(dataViewNew, 0, 4, 2, 1);
		
//		// Lambda
		hBoxLambdaNew.getChildren().add(new Label("Lambda Terms:  "));
		hBoxLambdaNew.getStyleClass().add("hBoxLambda");
		GridPane.setHgrow(hBoxLambdaNew, Priority.ALWAYS);
		gridCitationSub.add(hBoxLambdaNew, 0, 5);
		
		final HBox hBox = buildTopMenu(null, hBoxLambdaNew, dataViewNew, false);
		
		gridCitationSub.add(hBox, 1, 0);
		gridCitationSub.add(splitPaneQbeNew, 0, 1, 2, 1);

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
		showColumn.setCellFactory(new Callback<TableColumn<Entry, Boolean>, 
		        TableCell<Entry, Boolean>>() {
		            @Override
		            public TableCell<Entry, Boolean> call(TableColumn<Entry, Boolean> p) {
		                CheckBoxTableCell<Entry, Boolean> cell = new CheckBoxTableCell<Entry, Boolean>();
		                cell.setAlignment(Pos.TOP_CENTER);
		                cell.setPadding(new Insets(5,0,0,0));
		                return cell;
		            }
		           
		        });
		showColumn.setEditable(true);
		criteraColumn.setCellValueFactory(new PropertyValueFactory<>("criteria"));
		criteraColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        joinColumn.setCellValueFactory(new PropertyValueFactory<>("join"));
        joinColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		lambdaColumn.setCellValueFactory(new PropertyValueFactory<>("lambda"));
		lambdaColumn.setCellFactory(new Callback<TableColumn<Entry, Boolean>, //
		        TableCell<Entry, Boolean>>() {
		            @Override
		            public TableCell<Entry, Boolean> call(TableColumn<Entry, Boolean> p) {
		                CheckBoxTableCell<Entry, Boolean> cell = new CheckBoxTableCell<Entry, Boolean>();
		                cell.setAlignment(Pos.TOP_CENTER);
		                cell.setPadding(new Insets(5,0,0,0));
		                return cell;
		            }
		        });
		lambdaColumn.setEditable(true);

		tableView.getColumns().addAll(expanderColumn, tableColumn, fieldColumn, showColumn, criteraColumn, joinColumn, lambdaColumn);
		tableView.setItems(dataNew);

		tableColumn.setPrefWidth(100);
		fieldColumn.setPrefWidth(100);
		showColumn.setPrefWidth(70);
		lambdaColumn.setPrefWidth(70);
		criteraColumn.setPrefWidth(205);
		joinColumn.setPrefWidth(260);
		paddingCriteriaDba = expanderColumn.widthProperty().get() + tableColumn.widthProperty().get() + fieldColumn.widthProperty().get() + showColumn.widthProperty().get();
		// Label data preview
		final Label label_1 = new Label("Data Preview");
		label_1.setId("prompt-text");
		GridPane.setHgrow(label_1, Priority.ALWAYS);
		gridCitationSub.add(label_1, 0, 3);
		
		HBox hboxSq = new HBox();
		hboxSq.setAlignment(Pos.CENTER_RIGHT);
		Button saveCitationButton = new Button("Save Citation Query");
        saveCitationButton.setOnAction(event -> {
//        	if (listCitationViews.contains(cv)) {
//        		try {
//    				citation_view_operation.delete_citation_views(cv);
//    				listCitationViews.remove(cv);
//    			} catch (Exception e2) {
//    				e2.printStackTrace();
//    			}
//        	}
        	if (dataNew.isEmpty()) {
            	Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Alert");
                alert.setHeaderText(null);
                alert.setContentText("Empty citation query!");
                alert.showAndWait();
                return;
            } 
            TextInputDialog dialog = new TextInputDialog(cv);
            dialog.setContentText("Please enter the citation query name:");
            dialog.setHeaderText(null);
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String tempName = result.get();
                if (listCitationViews.contains(tempName)) {
                	Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Alert");
                    alert.setHeaderText(null);
                    alert.setContentText("Duplicate name, pls re-enter!");
                    alert.showAndWait();
                    return;
                } else if (tempName.equals("Untitled")) {
                	Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Alert");
                    alert.setHeaderText(null);
                    alert.setContentText("Pls give it a reasonable name");
                    alert.showAndWait();
                    return;
                } else
                	cv = result.get();
            } else {
                return;
            }
            // generate query
            Query currentQuery = addQueryByName(cv, dataNew);
            if (stage.getScene() == viewScene) {
            	// at this stage, dv = ""
            	try {
    				if (!listCitationViews.contains(cv)) {
    					Query_operation.add(currentQuery, cv);
    					listCitationViews.add(cv);
    					Alert alert = new Alert(Alert.AlertType.INFORMATION);
    	                alert.setTitle("Succeed");
    	                alert.setHeaderText(null);
    	                alert.setContentText("The citation queries are successfully saved as " + cv);
    	                alert.showAndWait();
    				}
    			} catch (Exception e1) {
    				e1.printStackTrace();
    			}
            } else if (stage.getScene() == dbaScene) {
            	try {
    				if (!dbaListCitationViews.contains(cv)) {
    					Query_operation.add(currentQuery, cv);
        				dbaListCitationViews.add(cv);
        				dataQuery.add(new CQuery(cv, "Undef"));
        				listCitationViews.add(cv);
        				Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Succeed");
                        alert.setHeaderText(null);
                        alert.setContentText("The citation queries " + cv + " are successfully add to " + dv);
                        alert.showAndWait();
    				}
    			} catch (Exception e1) {
    				e1.printStackTrace();
    			}
            }
            citeStage.hide();
        });
        

		hboxSq.getChildren().add(saveCitationButton);

		gridCitationSub.add(hboxSq, 1, 3);

		hBoxPrevNextNew = new HBox();
		hBoxPrevNextNew.setAlignment(Pos.CENTER_RIGHT);
		final Button prevButton = new Button("<<");
		final Button nextButton = new Button(">>");
		prevButton.setId("prevnext");
		nextButton.setId("prevnext");
		nextButton.setOnAction(e -> {
			next();
			setDataView(hBoxLambdaNew, dataViewNew, false);
		});
		hBoxPrevNextNew.getChildren().addAll(prevButton, nextButton);
		GridPane.setHgrow(hBoxPrevNextNew, Priority.ALWAYS);
		gridCitationSub.add(hBoxPrevNextNew, 1, 5);

		citationScene = new Scene(gridCitation);
		citationScene.getStylesheets().add(QBEApp.class.getResource("style.css").toExternalForm());
    }
    /**
     * Build the scene to show all the generated citation query for admin
     */
    private void buildGeneratedScene() {
    	GridPane gridGenerated = new GridPane();
    	gridGenerated.setPrefSize(500, 400);
    	gridGenerated.setPadding(new Insets(10));
    	Label labelQuery = new Label("Generated Queries");
    	labelQuery.setFont(Font.font("Courier New", FontWeight.BLACK, 18));
		gridGenerated.add(labelQuery, 0, 0);
		// build generated table view
    	TableView<GQuery> generatedTable = new TableView<GQuery>();
    	GridPane.setVgrow(generatedTable, Priority.ALWAYS);
		GridPane.setHgrow(generatedTable, Priority.ALWAYS);
//		generatedTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		generatedTable.setItems(dataGenerated);
		// generated query col
		TableColumn<GQuery, String> idCol = new TableColumn<GQuery, String>("ID");
		idCol.prefWidthProperty().bind(generatedTable.widthProperty().multiply(0.3));
		idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
		// query content col
		TableColumn<GQuery, String> contentCol = new TableColumn<GQuery, String>("Datalog");
		contentCol.prefWidthProperty().bind(generatedTable.widthProperty().multiply(0.7));
		contentCol.setCellValueFactory(new PropertyValueFactory<>("content"));
		contentCol.setCellFactory(new Callback<TableColumn<GQuery,String>, TableCell<GQuery,String>>() {
			public TableCell<GQuery, String> call(TableColumn<GQuery,String> tc) {
				TableCell<GQuery, String> cell = new TextFieldTableCell<GQuery, String>() {
					public void updateItem(String content, boolean empty) {
						super.updateItem(content, empty);
						if (empty) return;
						else {
							String sub1 = content.split(":")[0];
							String sub2 = content.split(":")[1];
							setText(sub1 + ":\n" + sub2);
						}
					}
				};
				return cell;
			}
		});
		generatedTable.getColumns().addAll(idCol, contentCol);
		gridGenerated.add(generatedTable, 0, 1);
    	
    	generatedScene = new Scene(gridGenerated);
		generatedScene.getStylesheets().add(QBEApp.class.getResource("style.css").toExternalForm());
    }
    /**
     * Build the format Scene to show different format of citations
     */
 // string that is set to the clipboard
    String citationString = "";
    private void buildFormatScene(String citation, String type) {
    	GridPane gridFormat = new GridPane();
    	gridFormat.setPrefSize(500, 400);
    	gridFormat.setPadding(new Insets(10));
    	// build the textField
    	TextArea contentString = new TextArea();
    	contentString.setEditable(false);
    	GridPane.setVgrow(contentString, Priority.ALWAYS);
		GridPane.setHgrow(contentString, Priority.ALWAYS);
		String formatedCitation = formatCitation(citation);
		if (type.equals("json")) {
			String jsonString = formatJson(citation);
			contentString.setText(jsonString);
			citationString = jsonString;
		} else if (type.equals("xml")) {
			try {
				String xmlString = CitationConverter.convertToXML(formatedCitation);
				contentString.setText(xmlString);
				citationString = xmlString;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (type.equals("biblatex")) {
			try {
				String biblatexString = CitationConverter.convertToBiblatex(formatedCitation, "others");
				contentString.setText(biblatexString);
				citationString = biblatexString;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (type.equals("ris")) {
			try {
				String risString = CitationConverter.convertToRIS(formatedCitation);
				contentString.setText(risString);
				citationString = risString;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		gridFormat.add(contentString, 0, 1);
		// build format switch link
    	Hyperlink labelJson = new Hyperlink("JSON");
    	labelJson.setId("link");
    	labelJson.setOnMouseClicked(event -> {
    		String jsonString = formatJson(citation);
			contentString.setText(jsonString);
			citationString = jsonString;
    	});
    	Hyperlink labelXml = new Hyperlink("XML");
    	labelXml.setId("link");
    	labelXml.setOnMouseClicked(event -> {
    		try {
				String xmlString = CitationConverter.convertToXML(formatedCitation);
				contentString.setText(xmlString);
				citationString = xmlString;
			} catch (Exception e) {
				e.printStackTrace();
			}
    	});
    	Hyperlink labelBiblatex = new Hyperlink("Biblatex");
    	labelBiblatex.setId("link");
    	labelBiblatex.setOnMouseClicked(event -> {
    		try {
    			String biblatexString = CitationConverter.convertToBiblatex(formatedCitation, "others");
				contentString.setText(biblatexString);
				citationString = biblatexString;
			} catch (Exception e) {
				e.printStackTrace();
			}
    	});
    	Hyperlink labelRis = new Hyperlink("RIS");
    	labelRis.setId("link");
    	labelRis.setOnMouseClicked(event -> {
    		try {
    			String risString = CitationConverter.convertToRIS(formatedCitation);
				contentString.setText(risString);
				citationString = risString;
			} catch (Exception e) {
				e.printStackTrace();
			}
    	});
    	HBox hboxSwitch = new HBox();
    	hboxSwitch.setSpacing(5);
    	hboxSwitch.setAlignment(Pos.CENTER_RIGHT);
    	hboxSwitch.getChildren().addAll(labelJson, labelXml, labelBiblatex, labelRis);
    	gridFormat.add(hboxSwitch, 0, 0);
		// build the download link
		Hyperlink copy = new Hyperlink("Copy");
		copy.setId("link");
		copy.setOnAction(event->{
			if (citationString != null && !citationString.isEmpty()) {
			    content.putString(citationString);
			    clipboard.setContent(content);
			}
		});
		HBox hboxDown = new HBox();
		hboxDown.getChildren().add(copy);
		hboxDown.setAlignment(Pos.CENTER_RIGHT);
		gridFormat.add(hboxDown, 0, 2);
		
    	formatScene = new Scene(gridFormat);
		formatScene.getStylesheets().add(QBEApp.class.getResource("style.css").toExternalForm());
    }
    private String formatCitation(String citation) {
    	String[] split = citation.split("\\},");
		String jsonString = "";
    	for (int i = 0; i < split.length; i++ ) {
    		if(i == 0)
    			jsonString += "{\"citation\":[" + split[i];
    		else {
    			jsonString += split[i] + "},";
    		}
    	}
    	jsonString += "]}";
    	return jsonString;
    }
    private String formatJson(String citation) {
    	String[] split = citation.split("\\{");
    	String jsonString = "";
    	for (int i = 0; i < split.length; i++ ) {
    		if(!split[i].isEmpty())
    			jsonString += "{\n    " + split[i];
    	}
    	split = jsonString.split("\\[");
    	jsonString = "";
    	for (int i = 0; i < split.length; i++) {
    		jsonString  += split[i] + "[\n        ";
    	}
    	jsonString = jsonString.substring(0, jsonString.length()-10);
    	split = jsonString.split("\\,");
    	jsonString = "";
    	for (int i = 0; i < split.length; i++) {
    		jsonString += split[i] + ",\n        ";
    	}
    	jsonString = jsonString.substring(0, jsonString.length()-10);
    	split = jsonString.split("\\]");
    	jsonString = "";
    	for (int i = 0; i < split.length; i++) {
    		if(!split[i].isEmpty())
    			jsonString += split[i] + "\n        ]";
    	}
    	jsonString = jsonString.substring(0, jsonString.length()-10);
    	split = jsonString.split("\\}");
    	jsonString = "";
    	for (int i = 0; i < split.length; i++) {
    		jsonString += split[i] + "\n}";
    	}
    	return jsonString;
    }
	/** ===================================================================
	 * Build the gridScene to show generated citations
	 */
	private GridPane buildGridCg(ObservableList<String> observableList) {
		GridPane gridCg = new GridPane();
        gridCg.setPadding(new Insets(2, 5, 2, 5));
        gridCg.setHgap(5);
        gridCg.setVgap(5);
		
		ListView<String> list = new ListView<>();
//		list.setCellFactory(list1 -> new ListCell<String>() {
//            {
//                Text text = new Text();
//                text.wrappingWidthProperty().bind(list1.widthProperty().subtract(15));
//                text.textProperty().bind(itemProperty());
//                setPrefWidth(0);
//                setGraphic(text);
//            }
//        });
		list.setCellFactory(lv -> {
			ListCell<String> cell = new ListCell<String>() {
				public void updateItem (String name, boolean empty) {
					super.updateItem(name, empty);
					if(name == null|| name.isEmpty()) {
						
					} else {
							Text text = new Text();
			                text.wrappingWidthProperty().bind(list.widthProperty().subtract(15));
			                text.textProperty().bind(itemProperty());
			                setPrefWidth(0);
			                setGraphic(text);
					}
				}
			};
//			ContextMenu contextMenu = new ContextMenu();
//			MenuItem jsonItem = new MenuItem();
//			jsonItem.textProperty().bind(Bindings.format("Export as JSON"));
//			jsonItem.setOnAction(event -> {
//				String item = cell.getItem();
//				buildFormatScene(item, "json");
//				this.formatStage.setScene(formatScene);
//				this.formatStage.setTitle("Export Formats");
//				this.formatStage.show();
//			});
//			MenuItem xmlItem = new MenuItem();
//			xmlItem.textProperty().bind(Bindings.format("Export as XML"));
//			xmlItem.setOnAction(event -> {
//				String item = cell.getItem();
//				buildFormatScene(item, "xml");
//				this.formatStage.setScene(formatScene);
//				this.formatStage.setTitle("Export Formats");
//				this.formatStage.show();
//			});
//			MenuItem biblatexItem = new MenuItem();
//			biblatexItem.textProperty().bind(Bindings.format("Export as Biblatex"));
//			biblatexItem.setOnAction(event -> {
//				String item = cell.getItem();
//				buildFormatScene(item, "biblatex");
//				this.formatStage.setScene(formatScene);
//				this.formatStage.setTitle("Export Formats");
//				this.formatStage.show();
//			});
//			MenuItem risItem = new MenuItem();
//			risItem.textProperty().bind(Bindings.format("Export as RIS"));
//			risItem.setOnAction(event -> {
//				String item = cell.getItem();
//				buildFormatScene(item, "ris");
//				this.formatStage.setScene(formatScene);
//				this.formatStage.setTitle("Export Formats");
//				this.formatStage.show();
//			});
//			contextMenu.getItems().addAll(jsonItem, xmlItem, biblatexItem, risItem);
//			cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
//				if (isNowEmpty) {
//					cell.setContextMenu(null);
//				} else {
//					cell.setContextMenu(contextMenu);
//				}
//			});
			cell.textProperty().bind(cell.itemProperty());
			return cell;
		});
		list.setItems(observableList);
		GridPane.setHgrow(list, Priority.ALWAYS);
		GridPane.setVgrow(list, Priority.ALWAYS);
		gridCg.add(list, 0, 0);
		
		HBox hBox = new HBox();
		hBox.setAlignment(Pos.CENTER_RIGHT);
		hBox.setSpacing(5);
		Button button = new Button("Copy Selected Citation");
		button.setId("prevnext");
		button.setOnAction( event-> {
			String citationString = list.getSelectionModel().getSelectedItem();
			if (citationString != null && !citationString.isEmpty()) {
			    content.putString(citationString);
			    clipboard.setContent(content);
			}
		});
		
		MenuButton btnExport = new MenuButton("Export");
		MenuItem jsonItem = new MenuItem();
		jsonItem.textProperty().bind(Bindings.format("JSON"));
		jsonItem.setOnAction(event -> {
			String item = list.getSelectionModel().getSelectedItem();
			if(item == null) {
				item = list.getItems().get(0);
			}
			if(item != null && item.length() != 0) {
				buildFormatScene(item, "json");
				this.formatStage.setScene(formatScene);
				this.formatStage.setTitle("Export Formats");
				this.formatStage.show();
			} 
		});
		MenuItem xmlItem = new MenuItem();
		xmlItem.textProperty().bind(Bindings.format("XML"));
		xmlItem.setOnAction(event -> {
			String item = list.getSelectionModel().getSelectedItem();
			if(item == null) {
				item = list.getItems().get(0);
			}
			if(item != null && item.length() != 0) {
				buildFormatScene(item, "xml");
				this.formatStage.setScene(formatScene);
				this.formatStage.setTitle("Export Formats");
				this.formatStage.show();
			}
		});
		MenuItem biblatexItem = new MenuItem();
		biblatexItem.textProperty().bind(Bindings.format("Biblatex"));
		biblatexItem.setOnAction(event -> {
			String item = list.getSelectionModel().getSelectedItem();
			if(item == null) {
				item = list.getItems().get(0);
			}
			if(item != null && item.length() != 0) {
				buildFormatScene(item, "biblatex");
				this.formatStage.setScene(formatScene);
				this.formatStage.setTitle("Export Formats");
				this.formatStage.show();
			} 

		});
		MenuItem risItem = new MenuItem();
		risItem.textProperty().bind(Bindings.format("RIS"));
		risItem.setOnAction(event -> {
			String item = list.getSelectionModel().getSelectedItem();
			if(item == null) {
				item = list.getItems().get(0);
			}
			if(item != null && item.length() != 0) {
				buildFormatScene(item, "ris");
				this.formatStage.setScene(formatScene);
				this.formatStage.setTitle("Export Formats");
				this.formatStage.show();
			}
		});
		btnExport.getItems().addAll(jsonItem, xmlItem, biblatexItem, risItem);
		
		
		GridPane.setHgrow(hBox, Priority.ALWAYS);
		
		Button save = new Button("Save Citation");
		save.setId("prevnext");
		save.setOnAction(event ->{
			String name = "CiteQ" + count;
			Query citeQuery = addQueryByName(name, data);
			System.out.println("[citeQuery ]" + citeQuery);
			Vector<Integer> id_list = new Vector<Integer>();
			if (ids != null || !ids.isEmpty()) {
				for (int id : ids) {
					id_list.add(id);
				}
			}
			try {
				int query_list_id = query_storage.store_user_query(citeQuery, id_list);
					if (!listQueries.contains(query_list_id)) {
						listQueries.add(Integer.toString(query_list_id));
					}
//				if (query_list_id > 0) {
					// saved before
					Vector<Integer> selected_rows = new Vector<Integer>();
					System.out.println("[query id] " + query_list_id);
					selected_rows = query_storage.get_query_list_by_id(query_list_id);
					citeStage.hide();
					Alert alert = new Alert(Alert.AlertType.INFORMATION);
		            alert.setTitle("Succeed");
		            alert.setHeaderText(null);
		            alert.setContentText("The citation query is successfully saved");
		            alert.showAndWait();
//		            Optional<ButtonType> result = alert.showAndWait();
//		            if (result.get() == ButtonType.OK){
		                // ... user chose OK
//		            }
//					citeStage.hide();
//					userDataView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
//					userDataView.getSelectionModel().setCellSelectionEnabled(true);
//					if (selected_rows == null || selected_rows.isEmpty()) {
//						userDataView.getSelectionModel().selectAll();
//					} else {
//						for (int row : selected_rows) {
//							userDataView.getSelectionModel().select(row);
//							System.out.println(row);
//						}
//					}
//				} else {
//					// successful save
//					Alert alert = new Alert(Alert.AlertType.INFORMATION);
//		            alert.setTitle("Succeed");
//		            alert.setHeaderText(null);
//		            alert.setContentText("The citation query is successfully saved");
//		            alert.showAndWait();
//		            Optional<ButtonType> result = alert.showAndWait();
//		            if (result.get() == ButtonType.OK){
//		                // ... user chose OK
//		            	citeStage.hide();
//		            }
//				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			count++;
		});
		hBox.getChildren().addAll(btnExport, save);
		gridCg.add(hBox, 0, 1);
		
		return gridCg;
	}
	/** ===================================================================
	 * Supplement methods
	 */
	// table editor for citation builder
	private HBox createEditor(TableRowExpanderColumn.TableRowDataFeatures<Entry> param) {
		HBox hboxPane = new HBox();
		VBox vboxCriteria = new VBox();
		vboxCriteria.setPadding(new Insets(5,0,15,paddingCriteriaDba+5));
		vboxCriteria.setSpacing(5);
		VBox vboxJoin = new VBox();
		vboxJoin.setPadding(new Insets(5,15,5,10));
		vboxJoin.setSpacing(5);
		Entry entry = param.getValue();
		/* Edited: Yan
		 * Create criteria comboBox
		 * Keep criteria and join both a string 
		 */
		// XXX: change layout of expander
		final ComboBox comboBoxComparator = new ComboBox();
		comboBoxComparator.getItems().addAll("=","<>",">","<",">=","<=");
		comboBoxComparator.setValue(null);
		TextField comparatorValue = new TextField();
		comparatorValue.setPrefWidth(100);
		Button addCriteria = new Button();
		addCriteria.setText("+");
		addCriteria.setFont(Font.font(12));
		addCriteria.setPadding(new Insets(2,5,2,5));
		Button minusCriteria = new Button();
		minusCriteria.setText("-");
		minusCriteria.setFont(Font.font("Courier New", FontWeight.BOLD, 12));
		minusCriteria.setPadding(new Insets(2,5.45,2,5.45));
		ListView<String> listCriteria = new ListView<String>();
		listCriteria.setPrefHeight(60);
		listCriteria.setMaxWidth(170);
		String [] criterias = entry.getCriteria().split("\\,");
		List<String> list = new ArrayList<>();
		for (int i = 0; i < criterias.length; i++) list.add(criterias[i]);
		ObservableList<String> items = FXCollections.observableArrayList(list);
		listCriteria.setItems(items);
		// criteria should be split by ","
		addCriteria.setOnAction((event) -> {
			if(comboBoxComparator.getValue() != null && !comparatorValue.getText().isEmpty()) {
				String oldCriteria = entry.getCriteria();
				String[] oldCriteriaList = oldCriteria.split("\\,");
				String addString  = comboBoxComparator.getValue() + comparatorValue.getText();
				Vector<String> oldString = new Vector<String>();
				if (oldCriteria == null || oldCriteria.isEmpty()) {
					entry.setCriteria(addString);
				} else {
					// check duplicate criteria
					for (int j = 0; j < oldCriteriaList.length; j++ ) {
						oldString.add(oldCriteriaList[j]);
					}
					if (oldString.contains(addString)) {
						return;
					} else {
						entry.setCriteria(oldCriteria + "," + addString);
					}
				}
			}
			// renew the listCriteria
			String [] criteriasnew = entry.getCriteria().split("\\,");
			List<String> listnew = new ArrayList<>();
			for (int i = 0; i < criteriasnew.length; i++) listnew.add(criteriasnew[i]);
			ObservableList<String> itemsnew = FXCollections.observableArrayList(listnew);
			listCriteria.getItems().setAll(itemsnew);
			comboBoxComparator.setValue(null);
			comparatorValue.setText(null);
		});
		minusCriteria.setOnAction((event) -> {
			if(comboBoxComparator.getValue() != null && !comparatorValue.getText().isEmpty()) {
				String oldCriteria = entry.getCriteria();
				String[] oldCriteriaList = oldCriteria.split("\\,");
				String minusString  = comboBoxComparator.getValue() + comparatorValue.getText();
				Vector<String> oldString = new Vector<String>();
				if (oldCriteria != null && !oldCriteria.isEmpty()) {
					for (int j = 0; j < oldCriteriaList.length; j++ ) {
						oldString.add(oldCriteriaList[j]);
					}
					if (oldString.contains(minusString)) {
						oldString.remove(minusString);
					}
				}
				String newCriteria = "";
				for (int j = 0; j < oldString.size(); j++) {
					newCriteria += oldString.get(j) + ",";
				}
				if (newCriteria != null && !newCriteria.isEmpty()) {
					// prevent string index out of bound
					newCriteria = newCriteria.substring(0, newCriteria.length()-1);
				}
				entry.setCriteria(newCriteria);
			}
			// renew the listCriteria
			String [] criteriasnew = entry.getCriteria().split("\\,");
			List<String> listnew = new ArrayList<>();
			for (int i = 0; i < criteriasnew.length; i++) listnew.add(criteriasnew[i]);
			ObservableList<String> itemsnew = FXCollections.observableArrayList(listnew);
			listCriteria.getItems().setAll(itemsnew);
			comboBoxComparator.setValue(null);
			comparatorValue.setText(null);
		});
		
		listCriteria.setOnMouseClicked(event -> {
			String[] comparator = {"=", ">", "<", ">=", "<=", "<>"};
            String criteriaString = listCriteria.getSelectionModel().getSelectedItem();
            if (criteriaString == null) return;
            for (int j = 0; j < comparator.length; j++) {
				if (comparator[j].equals(Character.toString(criteriaString.charAt(0))) ) {
					comboBoxComparator.setValue(comparator[j]);
					comparatorValue.setText(criteriaString.substring(1, criteriaString.length()));
				}
				else if (comparator[j].equals(criteriaString.substring(0, 2))) {
					comboBoxComparator.setValue(comparator[j]);
					comparatorValue.setText(criteriaString.substring(2, criteriaString.length()));
				}
					
			}
        });
		HBox hb = new HBox();
		hb.setSpacing(5);
		hb.getChildren().addAll(comboBoxComparator, comparatorValue, addCriteria);
		
		final ComboBox comboBoxComparator1 = new ComboBox();
		comboBoxComparator1.getItems().addAll("=","<>",">","<",">=","<=");
		comboBoxComparator1.setValue(null);
		ObservableList<String> optionsTable = FXCollections.observableArrayList(Database.getTableList());
		final ComboBox<String> comboBoxTable = new ComboBox(optionsTable);
        comboBoxTable.setPromptText("Table");
        comboBoxTable.valueProperty().addListener((ov, t, t1) -> {
            optionsField.clear();
            optionsField.addAll(Database.getAttrList(t1));
        });
        final ComboBox<String> comboBoxField = new ComboBox(optionsField);
        comboBoxField.setPromptText("Field");
		vboxJoin.getChildren().addAll(new Label("Join: "), comboBoxTable, comboBoxField, comboBoxComparator1);
        
		Button saveButton = new Button("Save");
		saveButton.setOnAction(event -> {
			if(comboBoxComparator1.getValue() != null && !comboBoxTable.getValue().isEmpty() && !comboBoxField.getValue().isEmpty() ) {
				entry.setJoin(comboBoxComparator1.getValue() + comboBoxTable.getValue() + "." + comboBoxField.getValue());
			}
			List<Entry> entryList = new ArrayList<>();
			entryList.addAll(dataNew);
			if (datalogTextAreaNew != null) datalogTextAreaNew.setText(Util.convertToDatalogOriginal(entryList) + "\n");
			comboBoxComparator.setValue(null);
			comboBoxComparator1.setValue(null);
			param.toggleExpanded();
		});
		HBox hb3 = new HBox();
		hb3.setSpacing(5);
		hb3.getChildren().addAll(listCriteria, minusCriteria);
		vboxCriteria.getChildren().addAll(new Label("Criteria: "), hb, hb3);
		
		Button cancelButton = new Button("Cancel");
		cancelButton.setOnAction(event -> param.toggleExpanded());
		Button deleteButton = new Button("Delete Row");
		deleteButton.setOnAction(event -> dataNew.remove(entry));
		HBox hb2 = new HBox();
		hb2.setSpacing(5);
		hb2.getChildren().addAll(saveButton, cancelButton, deleteButton);
		vboxJoin.getChildren().add(hb2);
		hb2.setAlignment(Pos.CENTER_RIGHT);
		hboxPane.getChildren().addAll(vboxCriteria,vboxJoin);
		HBox.setHgrow(vboxJoin, Priority.ALWAYS);
		return hboxPane;
	}
	
	private HBox createAnotherEditor(TableRowExpanderColumn.TableRowDataFeatures<Entry> param) {
		HBox hboxPane = new HBox();
//		hboxPane.setPadding(new Insets(10,0,10,paddingCriteria));
		VBox vboxCriteria = new VBox();
		if (stage.getScene() == dbaScene) {
			vboxCriteria.setPadding(new Insets(5,0,15,paddingCriteriaDba+5));
		} else {
			vboxCriteria.setPadding(new Insets(5,0,15,paddingCriteriaUser-45));
		}
		vboxCriteria.setSpacing(5);
		VBox vboxJoin = new VBox();
		vboxJoin.setPadding(new Insets(5,15,5,10));
		vboxJoin.setSpacing(5);
		Entry entry = param.getValue();
//        ObservableList<String> optionsTable = FXCollections.observableArrayList(Database.getTableList());
		/* Edited: Yan
		 * Create criteria comboBox
		 * Keep criteria and join both a string 
		 */
		// XXX: change layout of expander
		final ComboBox comboBoxComparator = new ComboBox();
		comboBoxComparator.getItems().addAll("=","<>",">","<",">=","<=");
		comboBoxComparator.setValue(null);
		TextField comparatorValue = new TextField();
		comparatorValue.setPrefWidth(100);
		Button addCriteria = new Button();
		addCriteria.setText("+");
		addCriteria.setFont(Font.font(12));
		addCriteria.setPadding(new Insets(2,5,2,5));
		Button minusCriteria = new Button();
		minusCriteria.setText("-");
		minusCriteria.setFont(Font.font("Courier New", FontWeight.BOLD, 12));
		minusCriteria.setPadding(new Insets(2,5.45,2,5.45));
		ListView<String> listCriteria = new ListView<String>();
		listCriteria.setPrefHeight(60);
		listCriteria.setMaxWidth(170);
		String [] criterias = entry.getCriteria().split("\\,");
		List<String> list = new ArrayList<>();
		for (int i = 0; i < criterias.length; i++) list.add(criterias[i]);
		ObservableList<String> items = FXCollections.observableArrayList(list);
		listCriteria.setItems(items);
		// criteria should be split by ","
		addCriteria.setOnAction((event) -> {
			if(comboBoxComparator.getValue() != null && !comparatorValue.getText().isEmpty()) {
				String oldCriteria = entry.getCriteria();
				String[] oldCriteriaList = oldCriteria.split("\\,");
				String addString  = comboBoxComparator.getValue() + comparatorValue.getText();
				Vector<String> oldString = new Vector<String>();
				if (oldCriteria == null || oldCriteria.isEmpty()) {
					entry.setCriteria(addString);
				} else {
					// check duplicate criteria
					for (int j = 0; j < oldCriteriaList.length; j++ ) {
						oldString.add(oldCriteriaList[j]);
					}
					if (oldString.contains(addString)) {
						return;
					} else {
						entry.setCriteria(oldCriteria + "," + addString);
					}
				}
			}
			// renew the listCriteria
			String [] criteriasnew = entry.getCriteria().split("\\,");
			List<String> listnew = new ArrayList<>();
			for (int i = 0; i < criteriasnew.length; i++) listnew.add(criteriasnew[i]);
			ObservableList<String> itemsnew = FXCollections.observableArrayList(listnew);
			listCriteria.getItems().setAll(itemsnew);
			comboBoxComparator.setValue(null);
			comparatorValue.setText(null);
		});
		minusCriteria.setOnAction((event) -> {
			if(comboBoxComparator.getValue() != null && !comparatorValue.getText().isEmpty()) {
				String oldCriteria = entry.getCriteria();
				String[] oldCriteriaList = oldCriteria.split("\\,");
				String minusString  = comboBoxComparator.getValue() + comparatorValue.getText();
				Vector<String> oldString = new Vector<String>();
				if (oldCriteria != null && !oldCriteria.isEmpty()) {
					for (int j = 0; j < oldCriteriaList.length; j++ ) {
						oldString.add(oldCriteriaList[j]);
					}
					if (oldString.contains(minusString)) {
						oldString.remove(minusString);
					}
				}
				String newCriteria = "";
				for (int j = 0; j < oldString.size(); j++) {
					newCriteria += oldString.get(j) + ",";
				}
				if (newCriteria != null && !newCriteria.isEmpty()) {
					// prevent string index out of bound
					newCriteria = newCriteria.substring(0, newCriteria.length()-1);
				}
				entry.setCriteria(newCriteria);
			}
			// renew the listCriteria
			String [] criteriasnew = entry.getCriteria().split("\\,");
			List<String> listnew = new ArrayList<>();
			for (int i = 0; i < criteriasnew.length; i++) listnew.add(criteriasnew[i]);
			ObservableList<String> itemsnew = FXCollections.observableArrayList(listnew);
			listCriteria.getItems().setAll(itemsnew);
			comboBoxComparator.setValue(null);
			comparatorValue.setText(null);
		});
		
		listCriteria.setOnMouseClicked(event -> {
			String[] comparator = {"=", ">", "<", ">=", "<=", "<>"};
            String criteriaString = listCriteria.getSelectionModel().getSelectedItem();
            if (criteriaString == null) return;
            for (int j = 0; j < comparator.length; j++) {
				if (comparator[j].equals(Character.toString(criteriaString.charAt(0))) ) {
					comboBoxComparator.setValue(comparator[j]);
					comparatorValue.setText(criteriaString.substring(1, criteriaString.length()));
				}
				else if (comparator[j].equals(criteriaString.substring(0, 2))) {
					comboBoxComparator.setValue(comparator[j]);
					comparatorValue.setText(criteriaString.substring(2, criteriaString.length()));
				}
					
			}
//            listCriteria.getItems().remove(criteriaString);
//            Vector<String> newEntryVector = new Vector<String>();
//            String[] oldEntry  = entry.getCriteria().split("\\,");
//            for (int i = 0; i < oldEntry.length; i++) {
//            	if (criteriaString.equals(oldEntry[i])) 
//            		System.out.println(oldEntry[i]);
//            	else {
//            		newEntryVector.add(oldEntry[i]);
//            	}
//            }
//            String newEntry = "";
//            for (int i = 0; i < newEntryVector.size(); i++) {
//            	newEntry += newEntryVector.get(i);
//            }
//            entry.setCriteria(newEntry);
        });
		HBox hb = new HBox();
		hb.setSpacing(5);
		hb.getChildren().addAll(comboBoxComparator, comparatorValue, addCriteria);
		
		final ComboBox comboBoxComparator1 = new ComboBox();
		comboBoxComparator1.getItems().addAll("=","<>",">","<",">=","<=");
		comboBoxComparator1.setValue(null);
		ObservableList<String> optionsTable = FXCollections.observableArrayList(Database.getTableList());
		final ComboBox<String> comboBoxTable = new ComboBox(optionsTable);
        comboBoxTable.setPromptText("Table");
        comboBoxTable.valueProperty().addListener((ov, t, t1) -> {
            optionsField.clear();
            optionsField.addAll(Database.getAttrList(t1));
        });
        final ComboBox<String> comboBoxField = new ComboBox(optionsField);
        comboBoxField.setPromptText("Field");
//        HBox hb1 = new HBox();
//        hb1.setSpacing(5);
//        hb1.getChildren().addAll(new Label("Join: "), comboBoxComparator1, comboBoxTable, comboBoxField);
		vboxJoin.getChildren().addAll(new Label("Join: "), comboBoxTable, comboBoxField, comboBoxComparator1);
        
		Button saveButton = new Button("Save");
		saveButton.setOnAction(event -> {
			//entry.setCriteria(criteriaField.getText());
//			if(comboBoxComparator.getValue() != null && !comboBoxComparator.getValue().toString().isEmpty()) {
//				String oldCriteria = entry.getCriteria();
//				if (oldCriteria.equals(null) || oldCriteria.equals("")) {
//					entry.setCriteria(comboBoxComparator.getValue() + comparatorValue.getText());
//				} else {
//					entry.setCriteria(oldCriteria + "," + comboBoxComparator.getValue() + comparatorValue.getText());
//				}
//			}
			if(comboBoxComparator1.getValue() != null && !comboBoxTable.getValue().isEmpty() && !comboBoxField.getValue().isEmpty() ) {
				entry.setJoin(comboBoxComparator1.getValue() + comboBoxTable.getValue() + "." + comboBoxField.getValue());
			}
			List<Entry> entryList = new ArrayList<>();
			entryList.addAll(data);
			if (datalogTextArea != null) datalogTextArea.setText(Util.convertToDatalogOriginal(entryList) + "\n");
			comboBoxComparator.setValue(null);
			comboBoxComparator1.setValue(null);
			param.toggleExpanded();
		});
		HBox hb3 = new HBox();
		hb3.setSpacing(5);
		hb3.getChildren().addAll(listCriteria, minusCriteria);
		vboxCriteria.getChildren().addAll(new Label("Criteria: "), hb, hb3);
		
		Button cancelButton = new Button("Cancel");
		cancelButton.setOnAction(event -> param.toggleExpanded());
		Button deleteButton = new Button("Delete Row");
		deleteButton.setOnAction(event -> data.remove(entry));
		HBox hb2 = new HBox();
		hb2.setSpacing(5);
		hb2.getChildren().addAll(saveButton, cancelButton, deleteButton);
//		vboxCriteria.getChildren().add(hb2);
		vboxJoin.getChildren().add(hb2);
		hb2.setAlignment(Pos.CENTER_RIGHT);
		hboxPane.getChildren().addAll(vboxCriteria,vboxJoin);
		HBox.setHgrow(vboxJoin, Priority.ALWAYS);
		return hboxPane;
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
							if (!citeStage.isShowing() && (stage.getScene() == dbaScene || stage.getScene() == userScene)) {
								for (int j = 0; j < data.size(); j++) {
									if(table.equals(data.get(j).getTable()) && field.equals(data.get(j).getField()))
										return;
								}
								data.add(new Entry(table, field, true, "", "", false));
							}
							// add data to citation builder
							else if (citeStage.isShowing()){
								for (int j = 0; j < dataNew.size(); j++) {
									if(table.equals(dataNew.get(j).getTable()) && field.equals(dataNew.get(j).getField()))
										return;
								}
								dataNew.add(new Entry(table, field, true, "", "", false));
							}
			
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
			conn = DriverManager.getConnection(populate_db.db_url, populate_db.usr_name, populate_db.passwd);
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
//				System.out.println("DEBUG: " + lambdas.get(p));
				// lambdaData.add(lambdas.get(p) + ": " +
				// lambdasAll.get(p).get(idx));
				if (hBoxLambda != null)  hBoxLambda.getChildren().add(new Label(lambdas.get(p) + ": " + lambdasAll.get(p).get(idx) + "   "));
				p++;
			}
			System.out.println(st.toString());
			st.execute();
			ResultSet rs = st.getResultSet();
//			dataViewList.clear();
			dataView.getColumns().clear();
			int num_rows = 0;
			if ((stage.getScene() == dbaScene || stage.getScene() == userScene) && !citeStage.isShowing() ) {
				dataViewList.clear();
				while (rs.next()) {
					ids.add(num_rows);
					ObservableList row = FXCollections.observableArrayList();
					for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
						row.add(rs.getString(i));
					}
					dataViewList.add(row);
					num_rows++;
				}
//				Iterator<Head_strs> keySetIterator = citation_strs.keySet().iterator();
//				while(keySetIterator.hasNext()) {
//					Head_strs keys = keySetIterator.next();
//					ObservableList row = FXCollections.observableArrayList();
//					Vector<String> head_vals = keys.head_vals;
//					for(int i = 0; i<head_vals.size(); i++)
//					{
//						row.add(head_vals.get(i));
//					}
//					dataViewList.add(row);
//					num_rows++;
//				}
				
			}
			if (citeStage.isShowing()) {
				dataViewListNew.clear();
				while (rs.next()) {
					ObservableList row = FXCollections.observableArrayList();
					for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
						row.add(rs.getString(i));
					}
					dataViewListNew.add(row);
					num_rows++;
				}
			}
			
			final int size = rs.getMetaData().getColumnCount();
			for (int i = 1; i <= size; i++) {
				// each column
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
			
			
			if (toCite) {
				// toCite = true, in the user scene
				TableColumn citationColomn = new TableColumn("Citation");
				
				String qname = "qname" + count;
				count++;
				userGeneratedQuery = addQueryByName(qname, data);
				HashMap<Head_strs, HashSet<String> > citation_strs = new HashMap<Head_strs, HashSet<String> >();
				
				citation_view_map.clear();
				
				try {
					Tuple_reasoning1.tuple_reasoning(userGeneratedQuery, citation_strs, citation_view_map, conn, st);
				} catch (IOException | InterruptedException | JSONException e) {
					e.printStackTrace();
				}

				Callback<TableColumn, TableCell> cellFactory = new Callback<TableColumn, TableCell>() {
					@Override
					public TableCell call(TableColumn p) {
						return new ComboBoxCell();
					}
				};
				ids.clear();
//				try {
//					  // Vector<String> agg_citations = Tuple_reasoning2.tuple_gen_agg_citations(c_views);
//					  // Vector<String> subset_agg_citations = Tuple_reasoning2.tuple_gen_agg_citations(c_views, ids);
//					for (int i = 0; i < dataViewList.size(); i++) {
//						ObservableList<String> lambdaData = FXCollections.observableArrayList(citation_strs.get(0));
//						((ObservableList) dataViewList.get(i)).add(lambdaData);
//					}
//				} catch (ClassNotFoundException | SQLException | IOException | InterruptedException e) {
//					e.printStackTrace();
//				}
				citationColomn.setCellFactory(cellFactory);
				
				Iterator<Head_strs> keySetIterator = citation_strs.keySet().iterator();
				
				heads.clear();
				
				int rows = 0;
				while(keySetIterator.hasNext()) {
					Head_strs keys = keySetIterator.next();
//					ObservableList row = FXCollections.observableArrayList();
					Vector<String> head_vals = keys.head_vals;
					
					heads.add(keys);
					
					System.out.println(keys.toString());
					System.out.println(citation_strs.get(keys));
					ObservableList<String> lambdaData = FXCollections.observableArrayList(citation_strs.get(keys));
					((ObservableList) dataViewList.get(rows)).add(lambdaData);
					rows++;
				}
				
				citationColomn.setCellValueFactory(
						new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
							public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
								if (param.getValue() == null || param.getValue().get(size-1) == null) return new SimpleStringProperty("");
//								ObservableList<String> list = (ObservableList<java.lang.String>) param.getValue().get(size-1);
								ObservableList<String> list = FXCollections.observableArrayList(param.getValue());
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
	
	private void addDataFromQuery(Query currentQuery) {
		// show view info in dba scene
		HashMap<String, String> relation_mapping = new HashMap<String, String>();
        Vector<String[]> head_vars = new Vector<String[]>();
        Vector<String[]> condition_str = new Vector<String[]>();
        Vector<String[]> lambda_term_str = new Vector<String[]>();
        Gen_query.get_query_info(currentQuery, relation_mapping, head_vars, condition_str, lambda_term_str);
        String vars = "";
        System.out.println(currentQuery);
        Set keys = relation_mapping.keySet();
        Vector<String> headVarRelation = new Vector<String>();
        // add entry appeared in head vars
        if (!head_vars.equals(null) & !head_vars.isEmpty()) {
        	for (int i = 0; i < head_vars.size(); i++) {
        		headVarRelation.add(head_vars.get(i)[1]);
        		if (keys.contains(head_vars.get(i)[1])) {
        			// the attribute is set to show in headvars
        			vars = head_vars.get(i)[0];
        			// get criteria and set it to entry
        			String criteria = "";
        			String join  = "";
        			for (int j = 0; j < condition_str.size(); j ++) {
        				String conditionVar = condition_str.get(j)[0];
        				String conditionRelation = condition_str.get(j)[1];
        				String lastString  = condition_str.get(j)[4];
        				if (vars.equals(conditionVar) & head_vars.get(i)[1].equals(conditionRelation) ) {
//        					if (lastString.isEmpty() || lastString.equals(null) ) {
//        						criteria = condition_str.get(j)[2] + condition_str.get(j)[3].replaceAll("\\'", "");
//        					}
        					if (lastString!=null && !lastString.isEmpty()) {
        						// get join and set it to entry
        						join = condition_str.get(j)[2] + condition_str.get(j)[4] + "." + condition_str.get(j)[3];
        					} else {
        						criteria = condition_str.get(j)[2] + condition_str.get(j)[3].replaceAll("\\'", "");
        					}
        				}
        			}
        			
        			// get lambda term
        			Boolean lambda = false;
        			if (!lambda_term_str.equals(null) & !lambda_term_str.isEmpty()) {
        				for (int j = 0; j < lambda_term_str.size(); j ++ ) {
            				if (lambda_term_str.get(j)[0].equals(vars) & lambda_term_str.get(j)[1].equals(head_vars.get(i)[1]))
            					lambda = true;
            			}
        			}
        			data.add(new Entry(head_vars.get(i)[1], vars, true, criteria, join, lambda));
        		} else {
        			System.out.println("head var not in relation mapping");
        		}
        	}
        }
        // add entry in criteria conditions, but not in head vars
        // need to guarantee entry appeared in head vars just show once
        if (!condition_str.equals(null) & !condition_str.isEmpty()) {
        	for (int i = 0; i < condition_str.size(); i++) {
        		String conditionVar = condition_str.get(i)[0];
				String conditionRelation = condition_str.get(i)[1];
				String criteria = "";
        		if (condition_str.get(i)[4] == null && !headVarRelation.contains(conditionRelation)) {
        			criteria = condition_str.get(i)[2] + condition_str.get(i)[3].replaceAll("\\'", "");
        			Boolean lambda = false;
        			if (!lambda_term_str.equals(null) & !lambda_term_str.isEmpty()) {
        				for (int j = 0; j < lambda_term_str.size(); j ++ ) {
            				if (lambda_term_str.get(j)[0].equals(conditionVar) & lambda_term_str.get(j)[1].equals(conditionRelation))
            					lambda = true;
            			}
        			}
        			data.add(new Entry(conditionRelation, conditionVar, false, criteria, "", lambda));
        		}
        	}
        }
        	
        // XXX	edit according save view button and createEditor
        	
        List<Entry> list = new ArrayList<>();
		list.addAll(data);
		if (datalogTextArea != null) datalogTextArea.setText(Util.convertToDatalogOriginal(list) + "\n");
		lambdasAll.clear();
		lambdaIndex.clear();
		lambdaSQL = Util.convertToSQLWithLambda(list, false);//toCite is false
		datalog = Util.convertToDatalogOriginal(list);
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
		setDataView(hBoxLambda, dataView, false);
	}
	
	private Query addQueryByName (String qname, ObservableList<Entry> currentData) {
		HashMap<String, String> relation_mapping = new HashMap<String, String>();
		Vector<String[]> head_vars = new Vector<String[]>();
		Vector<String []> condition_str = new Vector<String[]>();
		Vector<String[]> lambda_term_str = new Vector<String[]>();
		// The list statement should stay here
		List<Entry> list = new ArrayList<>();
		list.addAll(currentData);
        for (Entry e : list) {
        	String table = e.getTable();
        	String field = e.getField();
        	if (!relation_mapping.containsKey(table)) {
        		relation_mapping.put(table, table);
        	}
        	String[] arg = {field, table};
        	String[] comparator = {"=", "<", ">", "<=", ">=", "<>"};
        	String comparatorValue = "";
    		String compareNumber = "";
        	if (e.getShow()) {
        		if (!head_vars.contains(arg)) {
        			head_vars.add(arg);
        		}
        	}
        	if (e.getCriteria() != null && !e.getCriteria().isEmpty()) {
        		String[] criteria = e.getCriteria().split("\\,");
        		for (int i = 0; i < criteria.length; i++) {
        			for (int j = 0; j < comparator.length; j++) {
        				if (comparator[j].equals(Character.toString(criteria[i].charAt(0))) ) {
        					comparatorValue = comparator[j];
        					compareNumber = criteria[i].substring(1, criteria[i].length());
        				}
        				else if (comparator[j].equals(criteria[i].substring(0, 2))) {
        					comparatorValue = comparator[j];
        					compareNumber = criteria[i].substring(2, criteria[i].length());
        				}
        					
        			}
        			String[] condition = {field, table, comparatorValue,"'" + compareNumber + "'", ""};
        			condition_str.add(condition);
        		}
        	}
            if (e.getJoin() != null && !e.getJoin().isEmpty()) {
                String[] join = e.getJoin().split("\\."); //correspond to the setjoin() in createEditor()
                // split according to dot
                String joinTable = "";
                for (int j = 0; j < comparator.length; j++) {
                	if (comparator[j].equals(Character.toString(join[0].charAt(0))) ) {
                		comparatorValue = comparator[j];
                		joinTable = join[0].substring(1, join[0].length());
                	}
                	else if (comparator[j].equals(join[0].substring(0, 2))) {
                		comparatorValue = comparator[j];
                		joinTable = join[0].substring(2, join[0].length());
                	}
                }
                String[] condition1  = {field, table, comparatorValue, join[1], joinTable};
                
                condition_str.add(condition1);
                if (!relation_mapping.containsKey(joinTable)) {
                	relation_mapping.put(joinTable, joinTable);
                }
            }
            if (e.getLambda() == true) {
        		lambda_term_str.add(arg);
        	}
        }
		
		Query generatedQuery = null;
		try {
			generatedQuery = Gen_query.gen_query_full(qname, relation_mapping, head_vars, condition_str, lambda_term_str);
			System.out.println("[generatedQuery] " + generatedQuery);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return generatedQuery;
	}
	
	private ObservableList<Entry> returnDataFromQuery(Query currentQuery) {
		ObservableList<Entry> returnData = FXCollections.observableArrayList();
		// show view info in dba scene
		HashMap<String, String> relation_mapping = new HashMap<String, String>();
        Vector<String[]> head_vars = new Vector<String[]>();
        Vector<String[]> condition_str = new Vector<String[]>();
        Vector<String[]> lambda_term_str = new Vector<String[]>();
        Gen_query.get_query_info(currentQuery, relation_mapping, head_vars, condition_str, lambda_term_str);
        String vars = "";
        System.out.println(currentQuery);
        Set keys = relation_mapping.keySet();
        Vector<String> headVarRelation = new Vector<String>();
        // add entry appeared in head vars
        if (!head_vars.equals(null) & !head_vars.isEmpty()) {
        	for (int i = 0; i < head_vars.size(); i++) {
        		headVarRelation.add(head_vars.get(i)[1]);
        		if (keys.contains(head_vars.get(i)[1])) {
        			// the attribute is set to show in headvars
        			vars = head_vars.get(i)[0];
        			// get criteria and set it to entry
        			String criteria = "";
        			String join  = "";
        			for (int j = 0; j < condition_str.size(); j ++) {
        				String conditionVar = condition_str.get(j)[0];
        				String conditionRelation = condition_str.get(j)[1];
        				String lastString  = condition_str.get(j)[4];
        				if (vars.equals(conditionVar) & head_vars.get(i)[1].equals(conditionRelation) ) {
//        					if (lastString.isEmpty() || lastString.equals(null) ) {
//        						criteria = condition_str.get(j)[2] + condition_str.get(j)[3].replaceAll("\\'", "");
//        					}
        					if (lastString!=null && !lastString.isEmpty()) {
        						// get join and set it to entry
        						join = condition_str.get(j)[2] + condition_str.get(j)[4] + "." + condition_str.get(j)[3];
        					} else {
        						criteria = condition_str.get(j)[2] + condition_str.get(j)[3].replaceAll("\\'", "");
        					}
        				}
        			}
        			
        			// get lambda term
        			Boolean lambda = false;
        			if (!lambda_term_str.equals(null) & !lambda_term_str.isEmpty()) {
        				for (int j = 0; j < lambda_term_str.size(); j ++ ) {
            				if (lambda_term_str.get(j)[0].equals(vars) & lambda_term_str.get(j)[1].equals(head_vars.get(i)[1]))
            					lambda = true;
            			}
        			}
        			returnData.add(new Entry(head_vars.get(i)[1], vars, true, criteria, join, lambda));
        		} else {
        			System.out.println("head var not in relation mapping");
        		}
        	}
        }
        // add entry in criteria conditions, but not in head vars
        // need to guarantee entry appeared in head vars just show once
        if (!condition_str.equals(null) & !condition_str.isEmpty()) {
        	for (int i = 0; i < condition_str.size(); i++) {
        		String conditionVar = condition_str.get(i)[0];
				String conditionRelation = condition_str.get(i)[1];
				String criteria = "";
        		if (condition_str.get(i)[4] == null && !headVarRelation.contains(conditionRelation)) {
        			criteria = condition_str.get(i)[2] + condition_str.get(i)[3].replaceAll("\\'", "");
        			Boolean lambda = false;
        			if (!lambda_term_str.equals(null) & !lambda_term_str.isEmpty()) {
        				for (int j = 0; j < lambda_term_str.size(); j ++ ) {
            				if (lambda_term_str.get(j)[0].equals(conditionVar) & lambda_term_str.get(j)[1].equals(conditionRelation))
            					lambda = true;
            			}
        			}
        			returnData.add(new Entry(conditionRelation, conditionVar, false, criteria, "", lambda));
        		}
        	}
        }
        return returnData;
        
	}
	
	private void setGQ() {
		Vector<Integer> ids = new Vector<Integer>();
		try {
			ids = query_storage.store_user_query_ids();
		} catch (ClassNotFoundException | SQLException e1) {
			e1.printStackTrace();
		}
//		if (listQueries!=null && !listQueries.isEmpty()) {
		if (ids!=null && !ids.isEmpty()) {
			for (int i = 0; i < ids.size(); i++) {
				int id = ids.get(i);
					try {
						Query content = query_storage.get_user_query_by_id(id);
						dataGenerated.add(new GQuery(Integer.toString(id), content.toString()));
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		}
	}
	
	private void setCQ(String dv) {
		Vector<String> block_names = new Vector<String>();
		if (dv !=null && !dv.isEmpty()) {
			try {
				Vector<String> q_names = Query_operation.get_connection_citation_with_query(dv, block_names);
				System.out.println("[query_names] " + q_names);
				System.out.println("[block_names] " + block_names);
				List<String> list_q = new ArrayList<>();
				List<String> list_b = new ArrayList<>();
				for (int i = 0; i < q_names.size(); i++) {
					if (!list_q.contains(q_names.get(i)))
						list_q.add(q_names.get(i));
					if (!list_b.contains(block_names.get(i)))
						list_b.add(block_names.get(i));
				}
				dbaListCitationViews = FXCollections.observableArrayList();
				dbaListBlock = FXCollections.observableArrayList();
				dbaListCitationViews.clear();
				dbaListBlock.clear();
				dbaListCitationViews.setAll(list_q);
				dbaListBlock.setAll(list_b);
				
				if (dv != null || !dv.isEmpty()) {
					if (dbaListCitationViews != null && !dbaListCitationViews.isEmpty()) {
						for (int i = 0; i < dbaListCitationViews.size(); i ++ ) {
							String qName = q_names.get(i);
							String qblock = block_names.get(i);
//							if (dbaListBlock != null && !dbaListBlock.isEmpty()){
//								try {
//									qblock = dbaListBlock.get(i);
//								} catch (IndexOutOfBoundsException e){ }
//							}
							dataQuery.add(new CQuery(qName, qblock));
						}
					}
				}
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private String splitSQL (String query) {
		String splitSQL = "";
		String[] q1 = null;
		String[] q2 = null;
		if (query.contains("from")) {
			q1 = query.split("from");
			splitSQL = q1[0].trim() + "\n";
			if (q1[1].contains("where")) {
				q2 = q1[1].split("where");
				splitSQL += "from " + q2[0].trim() + "\n" + "where " + q2[1].trim();
			}
		} else if (query.contains("FROM")){
			 q1 = query.split("FROM");
			 splitSQL = q1[0].trim() + "\n";
			 if (q1[1].contains("WHERE")) {
				 q2 = q1[1].split("WHERE");
				 splitSQL += "FROM " + q2[0].trim() + "\n" + "WHERE " + q2[1].trim();
				 }
		} else
			splitSQL = query;
		return splitSQL;
	}
	
}
