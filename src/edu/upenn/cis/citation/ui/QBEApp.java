package edu.upenn.cis.citation.ui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.controlsfx.control.table.TableRowExpanderColumn;

import edu.upenn.cis.citation.dao.Database;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
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
    private TableView dataView = new TableView();
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
    List<List <String>> lambdasAll = new ArrayList<>();
    String lambdaSQL = null;
    List<Integer> lambdaIndex = new ArrayList<>();


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
        gridPaneDataViews.setHgap(5);
        gridPaneDataViews.setVgap(5);
        Label lableDataviews = new Label("Data Views");
        ListView<String> listViewDataViews = new ListView<>();
        Button buttonAddDataView = new Button("Add Data View");
        GridPane.setHgrow(listViewDataViews, Priority.ALWAYS);
        gridPaneDataViews.add(lableDataviews, 0, 0);
        gridPaneDataViews.add(listViewDataViews, 0, 1);
        gridPaneDataViews.add(buttonAddDataView, 0, 2);
        Reflection r1 = new Reflection();
        r1.setFraction(0.7f);
        gridPaneDataViews.setEffect(r1);
        gridPaneDataViews.setId("bproot");
        // GridPane citation views
        GridPane gridPaneCitationViews = new GridPane();
        gridPaneCitationViews.setPadding(new Insets(20, 20, 20, 20));
        gridPaneCitationViews.setHgap(5);
        gridPaneCitationViews.setVgap(5);
        Label lableCitationViews = new Label("Citation Views");
        ListView<String> listViewCitationView = new ListView<>();
        Button buttonAddCitationView = new Button("Add Citation View");
        buttonAddCitationView.setOnAction(event -> { this.stage.setScene(qbeScene); });
        GridPane.setHgrow(listViewCitationView, Priority.ALWAYS);
        gridPaneCitationViews.add(lableCitationViews, 0, 0);
        gridPaneCitationViews.add(listViewCitationView, 0, 1);
        gridPaneCitationViews.add(buttonAddCitationView, 0, 2);
        Reflection r2 = new Reflection();
        r2.setFraction(0.7f);
        gridPaneCitationViews.setEffect(r2);
        gridPaneCitationViews.setId("bproot");
        
        SplitPane splitPane = new SplitPane();
        splitPane.setId("bp");
        splitPane.getItems().add(gridPaneDataViews);
        splitPane.getItems().add(gridPaneCitationViews);
        // Add HBox and GridPane layout to BorderPane Layout
        bp.setTop(hb);
        bp.setCenter(splitPane);
        dbaScene = new Scene(bp);
        dbaScene.getStylesheets().add(QBEApp.class.getResource("style.css").toExternalForm());
	}


	private void buildLoginScene() {
        BorderPane bp = new BorderPane();
        bp.setPadding(new Insets(10, 50, 50, 50));
        // Adding HBox
        HBox hb = new HBox();
        hb.setPadding(new Insets(20, 20, 20, 30));
        HBox hbox = new HBox();
        // Adding GridPane
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        // Implementing Nodes for GridPane
        Label lblUserName = new Label("Username");
        final TextField txtUserName = new TextField();
        Label lblPassword = new Label("Password");
        final PasswordField pf = new PasswordField();
        Button btnLogin = new Button("Login");
        final Label lblMessage = new Label();
        // Adding Nodes to GridPane layout
        gridPane.add(lblUserName, 0, 0);
        gridPane.add(txtUserName, 1, 0);
        gridPane.add(lblPassword, 0, 1);
        gridPane.add(pf, 1, 1);
        gridPane.add(btnLogin, 2, 1);
        gridPane.add(lblMessage, 1, 2);
        // Reflection for gridPane
        Reflection r = new Reflection();
        r.setFraction(0.7f);
        gridPane.setEffect(r);
        // DropShadow effect
        DropShadow dropShadow = new DropShadow();
        dropShadow.setOffsetX(5);
        dropShadow.setOffsetY(5);
        // Adding text and DropShadow effect to it
        Text text = new Text("Data Citation System");
        text.setFont(Font.font("Courier New", FontWeight.BOLD, 28));
        text.setEffect(dropShadow);
        // Adding text to HBox
        hb.getChildren().add(text);
        // Add ID's to Nodes
        bp.setId("bp");
        gridPane.setId("bproot");
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
        bp.setTop(hb);
        hbox.getChildren().add(gridPane);
        bp.setCenter(hbox);
        // LOGIN SCENE
        loginScene = new Scene(bp);
        loginScene.getStylesheets().add(QBEApp.class.getResource("style.css").toExternalForm());
    }

    private void buildQbeScene() {
        // QBE SCENE
        buildSampleTree(null, true);
        grid = new GridPane();
        grid.setPadding(new Insets(0, 10, 10, 10));
        grid.setHgap(10);
        grid.setVgap(10);

        final Label label_0 = new Label("Query by Example Datalog Generator");
        label_0.setId("prompt-text");
        GridPane.setHgrow(label_0, Priority.ALWAYS);
        grid.add(label_0, 1, 0, 2, 1);

        // Search box
        final TextField searchBox = new TextField();
        searchBox.setPromptText("Search Table");
        searchBox.textProperty().addListener(new InvalidationListener() {
            @Override public void invalidated(Observable o) {
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
        samplesTreeView.setMaxWidth(200);
        samplesTreeView.setCellFactory(new Callback<TreeView<TreeNode>, TreeCell<TreeNode>>() {
            @Override public TreeCell<TreeNode> call(TreeView<TreeNode> param) {
                return new TreeCell<TreeNode>() {
                    @Override protected void updateItem(TreeNode item, boolean empty) {
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
        samplesTreeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<TreeNode>>() {
            @Override public void changed(ObservableValue<? extends TreeItem<TreeNode>> observable, TreeItem<TreeNode> oldValue, TreeItem<TreeNode> node) {
                if (node == null) return;
                String table = node.getParent().getValue().getName();
                String field = node.getValue().getName();
                data.add(new Entry(table, field, true, "", "", false));
                tableView.refresh();
            }
        });
        GridPane.setVgrow(samplesTreeView, Priority.ALWAYS);
        GridPane.setHgrow(samplesTreeView, Priority.ALWAYS);
        grid.add(samplesTreeView, 0, 1, 1, 5);

        // Pane
        SplitPane splitPane = new SplitPane();
        GridPane.setHgrow(splitPane, Priority.ALWAYS);
        GridPane.setVgrow(splitPane, Priority.ALWAYS);
        splitPane.getItems().add(tableView);
        final HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        final Label datalogLabel = new Label("Datalog Generated:");
        final TextArea datalogTextArea = new TextArea();
        datalogTextArea.setWrapText(true);
        final Button runButton = new Button("Run");
        final Button clearButton = new Button("Clear");
        runButton.setOnAction(e -> {
            List<Entry> list = new ArrayList<>();
            list.addAll(data);
            datalogTextArea.appendText(Util.convertToDatalog(list) + "\n");
            generateDataView(list);
        });
        clearButton.setOnAction(e -> {
            datalogTextArea.clear();
        });
        hBox.setMargin(clearButton, new Insets(0, 5, 0, 5));
        hBox.getChildren().addAll(runButton, clearButton);
        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(datalogLabel, datalogTextArea, hBox);
        vbox.setPadding(new Insets(25, 25, 25, 25));
        splitPane.getItems().add(vbox);
        grid.add(splitPane, 1, 1, 2, 1);

        // Table View
        tableView.setEditable(true);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        TableRowExpanderColumn<Entry> expanderColumn = new TableRowExpanderColumn<>(this::createEditor);
        expanderColumn.setMaxWidth(30);
        expanderColumn.setMinWidth(30);
        TableColumn<Entry, String> tableColumn = new TableColumn<>("Table");
        TableColumn<Entry, String> fieldColumn = new TableColumn<>("Field");
        TableColumn<Entry, Boolean> showColumn = new TableColumn<>("Show");
        TableColumn<Entry, String> criteraColumn = new TableColumn<>("Criteria");
        TableColumn<Entry, Boolean> lambdaColumn = new TableColumn<>("Lambda");
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
        tableView.getColumns().addAll(expanderColumn, tableColumn, fieldColumn, showColumn, criteraColumn, lambdaColumn);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Label data preview
        final Label label_1 = new Label("Data Preview");
        label_1.setId("prompt-text");
        GridPane.setHgrow(label_1, Priority.ALWAYS);
        grid.add(label_1, 1, 2, 2, 1);

        GridPane.setVgrow(dataView, Priority.ALWAYS);
        GridPane.setHgrow(dataView, Priority.ALWAYS);
        dataView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        dataView.setItems(dataViewList);
        grid.add(dataView, 1, 3, 2, 1);

        // final ComboBox<String> tableComboBox = new ComboBox<>(lambdaData);
        // tableComboBox.setPromptText("Lambda Terms");
        // GridPane.setHgrow(tableComboBox, Priority.ALWAYS);
        hBoxLambda.getChildren().add(new Label("Lambda Terms:  "));
        hBoxLambda.getStyleClass().add("hBoxLambda");
        GridPane.setHgrow(hBoxLambda, Priority.ALWAYS);
        grid.add(hBoxLambda, 1, 4);

        final HBox hBox2 = new HBox();
        hBox2.setAlignment(Pos.CENTER_RIGHT);
        final Button prevButton = new Button("Prev");
        final Button nextButton = new Button("Next");
        nextButton.setOnAction(e -> { next(); setDataView(); });
        hBox2.getChildren().addAll(prevButton, nextButton);
        GridPane.setHgrow(hBox2, Priority.ALWAYS);
        grid.add(hBox2, 2, 4);

        final HBox hBox3 = new HBox();
        hBox3.setAlignment(Pos.CENTER_RIGHT);
        final Button genButton = new Button("Generate Citation");
        genButton.setId("buttonGen");
        hBox3.getChildren().add(genButton);
        grid.add(hBox3, 1, 5, 2, 1);
        //===========================================================================
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
            samplesTreeView.setRoot(null);
            samplesTreeView.setRoot(root);
        }
        sort(root, Comparator.comparing(o -> o.getValue().getName()));
    }

    private boolean pruneSampleTree(TreeItem<TreeNode> treeItem, String searchText) {
        if (searchText == null) return true;
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
        lambdaIndex.clear();;
        lambdaSQL = Util.convertToSQLWithLambda(list);
        System.out.println("[DEBUG] lambdaSQL: " + lambdaSQL);
        lambdas = Util.getLambda(list);
        for (String lambda : lambdas) {
            System.out.println(lambda);
            String table = lambda.substring(0, lambda.indexOf('.'));
            String field = lambda.substring(lambda.indexOf('.')+1);
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
                    st.setInt(p+1, n);
                } catch (Exception e) {
                    st.setString(p+1, lambdasAll.get(p).get(idx));
                }
                System.out.println("DEBUG: " + lambdas.get(p));
                // lambdaData.add(lambdas.get(p) + ": " + lambdasAll.get(p).get(idx));
                hBoxLambda.getChildren().add(new Label(lambdas.get(p) + ": " + lambdasAll.get(p).get(idx) + "   "));
                p++;
            }
            System.out.println(st.toString());
            st.execute();
            ResultSet rs = st.getResultSet();
            dataViewList.clear();
            dataView.getColumns().clear();
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                final int j = i-1;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i));
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        if (param.getValue() == null || param.getValue().get(j) == null)
                            return new SimpleStringProperty("");
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });
                dataView.getColumns().addAll(col);
            }

            while (rs.next()) {
                ObservableList row = FXCollections.observableArrayList();
                for(int i = 1 ; i <= rs.getMetaData().getColumnCount(); i++){
                    row.add(rs.getString(i));
                }
                System.out.println("Row added: "+ row);
                dataViewList.add(row);
            }
            dataView.refresh();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void next() {
        for (int i = lambdaIndex.size() - 1; i >= 0; i--) {
            lambdaIndex.set(i, lambdaIndex.get(i)+1);
            if (lambdaIndex.get(i) >= lambdasAll.get(i).size()) {
                lambdaIndex.set(i, 0);
            } else {
                break;
            }
        }
    }

}


