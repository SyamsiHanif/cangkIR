package page;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import handler.AlertHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import main.Main;
import model.Cup;
import util.Connect;

public class CupManagementPage {
	
	MenuBar navBar;
	Menu menu;
	MenuItem cupManagementMenu;
	MenuItem logOutMenu;
	
	Cup selectedCup;
	
	ObservableList<Cup> cupList;
	
	TableView<Cup> cupManagementTv;
	TableColumn<Cup, String> cupNameTc;
	TableColumn<Cup, Integer> cupPriceTc;
	
	Label cupManagementLb;
	Label cupNameLb;
	Label cupPriceLb;
	
	TextField cupNameTf;
	TextField cupPriceTf;
	
	Button addCupBt;
	Button updatePriceBt;
	Button removeCupBt;
	
	VBox cupTableVb;
	VBox cupInputVb;
	HBox cupManagementHb;
	
	BorderPane containerBp;
	
	Connect connect = Connect.getConnection();
	AlertHandler alertHandler;
	
	public void init() {
		menu = new Menu("Menu");
		navBar = new MenuBar(menu);
		cupManagementMenu = new MenuItem("Cup Management");
		logOutMenu = new MenuItem("Log Out");
		
		cupList = FXCollections.observableArrayList();
		
		cupManagementTv = new TableView<>();
		cupNameTc = new TableColumn<>("Cup Name");
		cupPriceTc = new TableColumn<>("Cup Price");
		
		cupManagementLb = new Label("Cup Management");
		cupNameLb = new Label ("Cup Name");
		cupPriceLb = new Label ("Cup Price");
		
		cupNameTf = new TextField();
		cupPriceTf = new TextField();
		
		addCupBt = new Button("Add Cup");
		updatePriceBt = new Button("Update Price");
		removeCupBt = new Button ("Remove Cup");
		
		cupTableVb = new VBox(10);
		cupInputVb = new VBox(15);
		cupManagementHb = new HBox(10);
		
		containerBp = new BorderPane();
		
		alertHandler = new AlertHandler();
	}
	
	private void setup() {
		menu.getItems().addAll(cupManagementMenu, logOutMenu);
		
		cupNameTc.setCellValueFactory(new PropertyValueFactory<>("cupName"));
		cupPriceTc.setCellValueFactory(new PropertyValueFactory<>("cupPrice"));
		cupManagementTv.getColumns().add(cupNameTc);
		cupManagementTv.getColumns().add(cupPriceTc);
		cupManagementTv.setItems(cupList);
		
		cupNameTf.setPromptText("Input cup name here");
		cupPriceTf.setPromptText("Input cup price here");
		
		cupTableVb.getChildren().addAll(cupManagementLb, cupManagementTv);
		cupInputVb.getChildren().addAll(cupNameLb, cupNameTf, cupPriceLb, cupPriceTf, addCupBt, updatePriceBt, removeCupBt);
		cupManagementHb.getChildren().addAll(cupTableVb, cupInputVb);
		
		containerBp.setTop(navBar);
		containerBp.setLeft(cupManagementHb);
	}
	
	private void style() {
		cupManagementLb.setFont(Font.font("Calibri", FontWeight.BOLD, 20));
		
		addCupBt.setPrefSize(180, 40);
		updatePriceBt.setPrefSize(180, 40);
		removeCupBt.setPrefSize(180, 40);
		
		cupInputVb.setAlignment(Pos.CENTER_LEFT);
		cupManagementHb.setAlignment(Pos.BOTTOM_LEFT);
		cupManagementHb.setPadding(new Insets(20));
		
		cupTableVb.setMaxHeight(400);
		cupInputVb.setMaxHeight(400);
	}
	
	private void action() {
		
		cupManagementMenu.setOnAction(e ->{
			Main.sceneHandler.toCupManagementPage();
		});
		
		logOutMenu.setOnAction(e -> {
			Main.sceneHandler.toLoginPage();
		});
		
		cupManagementTv.setOnMouseClicked(e -> {
			if (cupManagementTv.getSelectionModel().getSelectedItem() != null) {
				selectedCup = cupManagementTv.getSelectionModel().getSelectedItem();
				cupNameTf.setText(selectedCup.getCupName());
				cupPriceTf.setText(selectedCup.getCupPrice().toString());
			}
		});
		
		addCupBt.setOnMouseClicked(e -> {
			if (addIsValid()) {
				addCup();
				refreshTable();
				alertHandler.showAlert(AlertType.INFORMATION, "Information", "Add Information", "Cup successfully added");
			}
		});
		
		updatePriceBt.setOnMouseClicked(e -> {
			if (updateIsValid()) {
				updateCup();
				refreshTable();
				alertHandler.showAlert(AlertType.INFORMATION, "Information", "Update Information", "Cup price successfully updated");
			}
		});
		
		removeCupBt.setOnMouseClicked(e -> {
			if (removeIsValid()) {
				removeCup();
				refreshTable();
				alertHandler.showAlert(AlertType.INFORMATION, "Information", "Remove Information", "Cup price successfully removed");
			}
		});
	}
	
	private void getData() {
		cupList.clear();

		String query = "SELECT * FROM mscup";
		try {
			ResultSet resultSet = connect.executeQuery(query);

			while (resultSet.next()) {
				String cupID = resultSet.getString("CupID");
				String cupName = resultSet.getString("CupName");
				Integer cupPrice = resultSet.getInt("CupPrice");

				Cup cup = new Cup(cupID, cupName, cupPrice);
				cupList.add(cup);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void addCup() {
		String query = "INSERT INTO mscup (CupID, CupName, CupPrice) VALUES (?, ?, ?)";

		try (PreparedStatement ps = connect.prepareStatement(query)) {
			ps.setString(1, generateID());
			ps.setString(2, cupNameTf.getText());
			ps.setInt(3, Integer.parseInt(cupPriceTf.getText()));

			if (ps.executeUpdate() == 0) {
				alertHandler.showAlert(AlertType.ERROR, "Error", "Insertion Error", "Failed to insert cup. Please try again.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void updateCup() {
		String query = "UPDATE mscup SET CupName=?, CupPrice=? WHERE CupID=?";
		
		try (PreparedStatement ps = connect.prepareStatement(query)){
			ps.setString(1, cupNameTf.getText());
			ps.setInt(2, Integer.parseInt(cupPriceTf.getText()));
			ps.setString(3, selectedCup.getCupID());
			
			if (ps.executeUpdate() == 0) {
				alertHandler.showAlert(AlertType.ERROR, "Error", "Update Error", "Failed to update cup. Please try again.");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void removeCup() {
		String query = "DELETE FROM mscup WHERE CupID=?";
		try (PreparedStatement ps = connect.prepareStatement(query)){
			ps.setString(1, selectedCup.getCupID());
			
			if (ps.executeUpdate() == 0) {
				alertHandler.showAlert(AlertType.INFORMATION, "Information", "Remove Information", "Cup is successfuly removed from database");
			}
			selectedCup = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private String generateID() {
		try {
			String query = "SELECT MAX(CupID) FROM mscup";
			ResultSet rs = connect.executeQuery(query);
			if (rs.next()) {
				String maxID = rs.getString(1);
				if (maxID != null) {
					int numericPart = Integer.parseInt(maxID.substring(2)) + 1;
					return String.format("CU%03d", numericPart);
				} else {
					return "CU001";
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void refreshTable() {
		getData();
		ObservableList<Cup> thisObs = FXCollections.observableArrayList(cupList);
		cupManagementTv.setItems(thisObs);
	}
	
	private boolean addIsValid() {
		String name = cupNameTf.getText();
		String price = cupPriceTf.getText();
		if (name.isEmpty()) {
			alertHandler.showAlert(AlertType.ERROR, "Error", "Cup Management", "Please fill out the cup name");
			return false;
		}
		
		for (Cup cup : cupList) {
			if (cup.getCupName().equals(name)) {
				alertHandler.showAlert(AlertType.ERROR, "Error", "Cup Management", "Cup Already Exists.");
				return false;
			}
		}
		
		if (!price.matches("\\d+")) {
			alertHandler.showAlert(AlertType.ERROR, "Error", "Cup Management", "Cup price must be numbers");
			return false;
		}
		
		Integer priceInt = Integer.parseInt(price);
		if (priceInt < 5000 || priceInt > 1000000) {
			alertHandler.showAlert(AlertType.ERROR, "Error", "Cup Management", "Cup price must be 5000 - 1000000");
			return false;
		}
		return true;
	}
	
	private boolean updateIsValid() {
		String price = cupPriceTf.getText();
		if (selectedCup == null) {
			alertHandler.showAlert(AlertType.ERROR, "Error", "Cup Management", "Please select a cup from the table to be updated");
			return false;
		}
		
		if (!price.matches("\\d+")) {
			alertHandler.showAlert(AlertType.ERROR, "Error", "Cup Management", "Cup price must be numbers");
			return false;
		}
		
		Integer priceInt = Integer.parseInt(price);
		if (priceInt < 5000 || priceInt > 1000000) {
			alertHandler.showAlert(AlertType.ERROR, "Error", "Cup Management", "Cup price must be 5000 - 1000000");
			return false;
		}
		return true;
	}
	
	private boolean removeIsValid() {
		if (selectedCup == null) {
			alertHandler.showAlert(AlertType.ERROR, "Error", "Cup Management", "Please choose a cup");
			return false;
		}
		return true;
	}
	
	public Parent getRoot() {
		return this.containerBp;
	}
	
	public CupManagementPage() {
		init();
		setup();
		style();
		action();
		getData();
	}
	
}
