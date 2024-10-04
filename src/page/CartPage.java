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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import jfxtras.labs.scene.control.window.Window;
import model.Cart;
import model.Courier;
import model.Transaction;
import model.User;
import util.Connect;

public class CartPage {
	
	User user;
	Cart selectedCart;
	Courier selectedCourier;
	
	Label cartOwnerLb;
	Label deleteItemLb;
	Label courierLb;
	Label courierPriceLb;
	Label totalPriceLb;
	
	Button deleteItemBt;
	Button checkOutBt;
	
	ComboBox<String> courierChoicesCb;
	ObservableList<Courier> courierList;
	
	CheckBox useDeliveryInsuranceCheckBox;
	
	ObservableList<Cart> cartList;
	
	TableView<Cart> cartTv;
	TableColumn<Cart, String> cupNameTc;
	TableColumn<Cart, Integer> cupPriceTc;
	TableColumn<Cart, Integer> quantityTc;
	TableColumn<Cart, Integer> totalTc;
	
	VBox cartVb;
	VBox cartInputVb;
	HBox cartHb;
	
	BorderPane containerBp;
	
	Connect connect = Connect.getConnection();
	
	AlertHandler alertHandler;
	
	Label confirmationLb;
	Button yesBt, noBt;
	
	HBox buttonHb;
	VBox confirmVb;
	
	Window confirmationWindow;
	
	private void init() {
		
		cartOwnerLb = new Label(user.getUsername() + "'s Cart");
		deleteItemLb = new Label("Delete Item");
		courierLb = new Label("Courier");
		courierPriceLb = new Label("Courier Price");
		totalPriceLb = new Label("Total Price");
		
		deleteItemBt = new Button("Delete Item");
		checkOutBt = new Button("Checkout");
		
		useDeliveryInsuranceCheckBox = new CheckBox("Use Delivery Insurance");
		
		cartList = FXCollections.observableArrayList();
		courierList = FXCollections.observableArrayList();
		
		getCourierData();
		ObservableList<String> courierNames = FXCollections.observableArrayList();
		for (Courier courier : courierList) {
			courierNames.add(courier.getCourierName());
		}
		courierChoicesCb = new ComboBox<>(courierNames);
		
		cartTv = new TableView<>();
		cupNameTc = new TableColumn<>("Cup Name");
		cupPriceTc = new TableColumn<>("Cup Price");
		quantityTc = new TableColumn<>("Quantity");
		totalTc = new TableColumn<>("Total");
		
		cartVb = new VBox();
		cartInputVb = new VBox(20);
		cartHb = new HBox(10);
		
		containerBp = new BorderPane();
		
		alertHandler = new AlertHandler();
		confirmationWindow = new Window();
		
		confirmationLb = new Label("Are you sure you want to purchase?");
		yesBt = new Button("Yes");
		noBt = new Button("No");
		
		buttonHb = new HBox(20);
		confirmVb = new VBox(20);
		confirmationWindow = new Window("Checkout Confirmation");
	}
	
	private void setup() {
		
		cupNameTc.setCellValueFactory(new PropertyValueFactory<>("cupName"));
		cupPriceTc.setCellValueFactory(new PropertyValueFactory<>("cupPrice"));
		quantityTc.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		totalTc.setCellValueFactory(new PropertyValueFactory<>("total"));
		cartTv.getColumns().add(cupNameTc);
		cartTv.getColumns().add(cupPriceTc);
		cartTv.getColumns().add(quantityTc);
		cartTv.getColumns().add(totalTc);
		cartTv.setItems(cartList);
		
		courierChoicesCb.setPromptText("Select Courier");
		
		cartVb.getChildren().addAll(cartOwnerLb, cartTv);
		cartInputVb.getChildren().addAll(deleteItemLb, deleteItemBt, courierLb, courierChoicesCb, courierPriceLb, useDeliveryInsuranceCheckBox, totalPriceLb, checkOutBt);
		cartHb.getChildren().addAll(cartVb, cartInputVb);
		
		containerBp.setLeft(cartHb);
		
		buttonHb.getChildren().addAll(yesBt, noBt);
		confirmVb.getChildren().addAll(confirmationLb, buttonHb);
		confirmationWindow.getContentPane().getChildren().add(confirmVb);
	}
	
	private void style() {
		cartOwnerLb.setFont(Font.font("Calibri", FontWeight.BOLD, 20));
		deleteItemLb.setFont(Font.font("Calibri", FontWeight.BOLD, 20));
		courierLb.setFont(Font.font("Calibri", FontWeight.BOLD, 20));
		courierPriceLb.setFont(Font.font("Calibri", FontWeight.BOLD, 20));
		totalPriceLb.setFont(Font.font("Calibri", FontWeight.BOLD, 20));
		confirmationLb.setFont(Font.font("Calibri", FontWeight.BOLD, 20));
		
		deleteItemBt.setPrefSize(80, 35);
		checkOutBt.setPrefSize(80, 35);
		
		cartInputVb.setAlignment(Pos.CENTER_LEFT);
		cartHb.setAlignment(Pos.BOTTOM_LEFT);
		cartHb.setPadding(new Insets(20));
		
		cartVb.setMaxHeight(400);
		cartInputVb.setMaxHeight(400);
		confirmVb.setAlignment(Pos.CENTER);
		buttonHb.setAlignment(Pos.CENTER);
		buttonHb.setPadding(new Insets(20));
		
	}
	
	private void action() {
		cartTv.setOnMouseClicked(e -> {
			if (cartTv.getSelectionModel().getSelectedItem() != null) {
				selectedCart = cartTv.getSelectionModel().getSelectedItem();
				refreshPrice();
			}
		});
		
		deleteItemBt.setOnMouseClicked(e -> {
			if (deleteItemValid()) {
				deleteItem();
				refreshTable();
				refreshPrice();
				alertHandler.showAlert(AlertType.INFORMATION, "Message", "Deletion Information", "Cart Item Deleted Successfully");
			} 
		});
		
		courierChoicesCb.setOnAction(e -> {
			selectedCourier = courierList.get(courierChoicesCb.getSelectionModel().getSelectedIndex());
			refreshPrice();
		});
		
		useDeliveryInsuranceCheckBox.setOnMouseClicked(e ->{
			refreshPrice();
		});
		
		checkOutBt.setOnAction(e -> {
			if (checkOutValid()) {
				HomePage.containerBp.setTop(null);
				HomePage.containerBp.setCenter(confirmationWindow);
			}
		});
		
		yesBt.setOnMouseClicked(e -> {
			checkOut();
			clearCart();
			refreshTable();
			alertHandler.showAlert(AlertType.INFORMATION, "Message", "Checkout Information", "Checkout successful");
			HomePage.containerBp.setTop(HomePage.navBar);
			HomePage.containerBp.setCenter(containerBp);
		});
		
		noBt.setOnMouseClicked(e -> {
			HomePage.containerBp.setTop(HomePage.navBar);
			HomePage.containerBp.setCenter(containerBp);
		});
	}
	
	private void getData() {
		cartList.clear();

		String query = "SELECT c.UserID, c.CupID, c.Quantity, m.CupName, m.CupPrice " +
					   "FROM cart c " +
					   "INNER JOIN mscup m ON c.CupID = m.CupID " +
					   "WHERE c.UserID LIKE ?";
		
		try (PreparedStatement ps = connect.prepareStatement(query)) {
			ps.setString(1, user.getUserID());
			
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				String userID = rs.getString("UserID");
				String cupID = rs.getString("CupID");
				Integer quantity = rs.getInt("Quantity");
				String cupName = rs.getString("CupName");
				Integer cupPrice = rs.getInt("CupPrice");

				Integer total = quantity * cupPrice;

				Cart cart = new Cart(userID, cupID, cupName, cupPrice, quantity, total);
				cartList.add(cart);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void deleteItem() {
		String query = "DELETE FROM cart WHERE UserID=? AND CupID=?";
		try (PreparedStatement ps = connect.prepareStatement(query)){
			ps.setString(1, selectedCart.getUserID());
			ps.setString(2, selectedCart.getCupID());
			
			if (ps.executeUpdate() == 0) {
				alertHandler.showAlert(AlertType.ERROR, "Error", "Delete Error", "Failed to delete item. Please try again.");
			}
			selectedCart = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void clearCart() {
		String query = "DELETE FROM cart WHERE UserID=?";
		try (PreparedStatement ps = connect.prepareStatement(query)){
			ps.setString(1, user.getUserID());
			
			if (ps.executeUpdate() == 0) {
				alertHandler.showAlert(AlertType.ERROR, "Error", "Delete Error", "Failed to delete item. Please try again.");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private boolean deleteItemValid() {
		if (selectedCart == null) {
			alertHandler.showAlert(AlertType.ERROR, "Error", "Delete Error", "Please choose an item");
			return false;
		}
		return true;
	}
	
	private void refreshPrice() {
		Integer courierPrice = selectedCourier == null ? 0 : selectedCourier.getCourierPrice();
		courierPriceLb.setText("Courier Price " + courierPrice);
		totalPriceLb.setText("Total Price: " + calculateTotal());
	}
	
	private void refreshTable() {
		getData();
		ObservableList<Cart> thisObs = FXCollections.observableArrayList(cartList);
		cartTv.setItems(thisObs);
	}
	
	private void getCourierData() {
		courierList.clear();

		String query = "SELECT * FROM mscourier";
		try {
			ResultSet resultSet = connect.executeQuery(query);

			while (resultSet.next()) {
				String courierID = resultSet.getString("CourierID");
				String courierName = resultSet.getString("CourierName");
				Integer courierPrice = resultSet.getInt("CourierPrice");
				
				Courier courier = new Courier(courierID, courierName, courierPrice);
				courierList.add(courier);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	private Integer calculateTotal() {
		Integer totalPrice = 0;
		Integer insurance = useDeliveryInsuranceCheckBox.isSelected() ? 2000 : 0;
		Integer courierPrice = selectedCourier == null ? 0 : selectedCourier.getCourierPrice();
		totalPrice += courierPrice;
		totalPrice += insurance;
		for (Cart cart : cartList) {
			totalPrice += cart.getTotal();
		}
		return totalPrice;
	}
	
	private void checkOut() {
		Transaction transaction = new Transaction(generateID(), user.getUserID(), selectedCourier.getCourierID(), null, useDeliveryInsuranceCheckBox.isSelected());
		String headerQuery = "INSERT INTO transactionHeader (TransactionID, UserID, CourierID, TransactionDate, UseDeliveryInsurance) VALUES (?, ?, ?, CURRENT_DATE(), ?)";

		try (PreparedStatement ps = connect.prepareStatement(headerQuery)) {
			ps.setString(1, transaction.getTransactionID());
			ps.setString(2, transaction.getUserID());
			ps.setString(3, transaction.getCourierID());
			ps.setInt(4, transaction.isUserDeliveryInsurance() ? 1 : 0);
			
			if (ps.executeUpdate() == 0) {
				alertHandler.showAlert(AlertType.ERROR, "Error", "Insertion Error", "Failed to insert cup. Please try again.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		String detailQuery = "INSERT INTO transactionDetail (TransactionID, CupID, Quantity) VALUES (?, ?, ?)";
		try (PreparedStatement ps = connect.prepareStatement(detailQuery)) {
			for (Cart cart : cartList) {
				ps.setString(1, transaction.getTransactionID());
				ps.setString(2, cart.getCupID());
				ps.setInt(3, cart.getQuantity());
			}

			if (ps.executeUpdate() == 0) {
				alertHandler.showAlert(AlertType.ERROR, "Error", "Insertion Error", "Failed to insert cup. Please try again.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private boolean checkOutValid() {
		if (cartList.isEmpty()) {
			alertHandler.showAlert(AlertType.ERROR, "Error", "Checkout Error", "Cart is empty");
			return false;
		}
		
		if (courierChoicesCb.getValue() == null) {
			alertHandler.showAlert(AlertType.ERROR, "Error", "Checkout Error", "Please select a courier");
			return false;
		}
		return true;
	}
	
	private String generateID() {
		try {
			String query = "SELECT MAX(TransactionID) FROM transactionheader";
			ResultSet rs = connect.executeQuery(query);
			if (rs.next()) {
				String maxID = rs.getString(1);
				if (maxID != null) {
					int numericPart = Integer.parseInt(maxID.substring(2)) + 1;
					return String.format("TR%03d", numericPart);
				} else {
					return "TR001";
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Parent getRoot() {
		return this.containerBp;
	}
	
	public CartPage(User user) {
		this.user = user;
		init();
		setup();
		style();
		action();
		getData();
		refreshPrice();
	}
}
