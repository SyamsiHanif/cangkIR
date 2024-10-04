package page;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import handler.AlertHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import main.Main;
import model.Cart;
import model.Cup;
import model.User;
import util.Connect;

public class HomePage {
	
	User user;
	
	Cup selectedCup;
	Cart cart;
	
	public static MenuBar navBar;
	Menu menu;
	MenuItem homeMenu;
	MenuItem cartMenu;
	MenuItem logOutMenu;
	
	Label tableTitleLb;
	Label cupNameLb;
	Label priceLb;
	
	Button addToCartBt;
	
	ObservableList<Cup> cupList;
	
	TableView<Cup> cupListTv;
	TableColumn<Cup, String> cupNameTc;
	TableColumn<Cup, Integer> cupPriceTc;
	
	Spinner<Integer> quantitySp;
	
	VBox cupListVb;
	VBox cupInputVb;
	HBox homeHb;
	
	@SuppressWarnings("exports")
	public static BorderPane containerBp;
	
	Connect connect = Connect.getConnection();
	AlertHandler alertHandler;
	
	private void init() {
		menu = new Menu("Menu");
		navBar = new MenuBar(menu);
		homeMenu = new MenuItem("Home");
		cartMenu = new MenuItem("Cart");
		logOutMenu = new MenuItem("Log Out");
		
		tableTitleLb = new Label("Cup List");
		cupNameLb = new Label("Cup Name");
		priceLb = new Label("Price");
		
		addToCartBt = new Button("Add to Cart");
		
		cupList = FXCollections.observableArrayList();
		
		cupListTv = new TableView<>();
		cupNameTc = new TableColumn<>("Cup Name");
		cupPriceTc = new TableColumn<>("Cup Price");
		
		quantitySp = new Spinner<>(1, 20, 1);
		
		cupListVb = new VBox(10);
		cupInputVb = new VBox(30);
		homeHb = new HBox(10);
		
		containerBp = new BorderPane();
		alertHandler = new AlertHandler();
	}
	
	private void setup() {
		menu.getItems().addAll(homeMenu, cartMenu, logOutMenu);
		
		cupNameTc.setCellValueFactory(new PropertyValueFactory<>("cupName"));
		cupPriceTc.setCellValueFactory(new PropertyValueFactory<>("cupPrice"));
		cupListTv.getColumns().add(cupNameTc);
		cupListTv.getColumns().add(cupPriceTc);
		cupListTv.setItems(cupList);
		
		cupListVb.getChildren().addAll(tableTitleLb, cupListTv);
		cupInputVb.getChildren().addAll(cupNameLb, quantitySp, priceLb, addToCartBt);
		
		homeHb.getChildren().addAll(cupListVb, cupInputVb);
		containerBp.setTop(navBar);
		containerBp.setLeft(homeHb);
	}
	
	private void style() {
		tableTitleLb.setFont(Font.font("Calibri", FontWeight.BOLD, 20));
		cupNameLb.setFont(Font.font("Calibri", FontWeight.BOLD, 20));
		priceLb.setFont(Font.font("Calibri", FontWeight.BOLD, 20));
		
		cupInputVb.setAlignment(Pos.CENTER_LEFT);
		homeHb.setAlignment(Pos.BOTTOM_LEFT);
		homeHb.setPadding(new Insets(20));
		
		addToCartBt.setPrefSize(80, 35);
		
		cupListVb.setMaxHeight(400);
		cupInputVb.setMaxHeight(400);
		
	}
	
	private void action() {
		
		homeMenu.setOnAction(e-> {
			Main.sceneHandler.toHomePage(user);
		});
		
		cartMenu.setOnAction(e -> {
			containerBp.setLeft(null);
			containerBp.setCenter(new CartPage(user).getRoot());
		});
		
		logOutMenu.setOnAction(e -> {
			Main.sceneHandler.toLoginPage();
		});
		
		cupListTv.setOnMouseClicked(e -> {
			if (cupListTv.getSelectionModel().getSelectedItem() != null) {
				selectedCup = cupListTv.getSelectionModel().getSelectedItem();
				priceLb.setText("Total Price: " + (selectedCup.getCupPrice() * quantitySp.getValue()));
			}
		});
		quantitySp.setOnMouseClicked(e -> {
			if (cupListTv.getSelectionModel().getSelectedItem() != null) {
				priceLb.setText("Total Price: " + (selectedCup.getCupPrice() * quantitySp.getValue()));
			}
		});
		
		addToCartBt.setOnMouseClicked(e -> {
			if (addToCartValid()) {
				if (isCupExist(selectedCup)) {
					updateCart();
				} else {
					addToCart();
				}
			}
		});
	}
	
	private boolean addToCartValid() {
		if (selectedCup == null) {
			alertHandler.showAlert(AlertType.ERROR, "Error", "Cup Management", "Please choose a cup");
			return false;
		}
		return true;
	}
	
	private boolean isCupExist(Cup cup) {
		String query = "SELECT * FROM cart WHERE UserID=? AND CupID=?";

		try (PreparedStatement ps = connect.prepareStatement(query)) {
			ps.setString(1, user.getUserID());
			ps.setString(2, cup.getCupID());

			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {
				return true;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private void updateCart() {
		cart = new Cart(user.getUserID(), selectedCup.getCupID(), selectedCup.getCupName(), selectedCup.getCupPrice(), quantitySp.getValue(), (selectedCup.getCupPrice() * quantitySp.getValue()));
		String query = "UPDATE cart SET Quantity=? WHERE UserID=? AND CupID=?";
		
		try (PreparedStatement ps = connect.prepareStatement(query)) {
			ps.setInt(1, cart.getQuantity());
			ps.setString(2, cart.getUserID());
			ps.setString(3, cart.getCupID());

			int rowsAffected = ps.executeUpdate();

			if (rowsAffected == 0) {
				alertHandler.showAlert(AlertType.ERROR, "Error", "Cart Error", "Failed to add to cart");
			} else {
				alertHandler.showAlert(AlertType.INFORMATION, "Message", "Cart Info", "Quantity successfully updated to cart!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void addToCart() {
		cart = new Cart(user.getUserID(), selectedCup.getCupID(), selectedCup.getCupName(), selectedCup.getCupPrice(), quantitySp.getValue(), (selectedCup.getCupPrice() * quantitySp.getValue()));
		String query = "INSERT INTO cart (UserID, CupID, Quantity) VALUES (?, ?, ?)";
		
		try (PreparedStatement ps = connect.prepareStatement(query)) {
			ps.setString(1, cart.getUserID());
			ps.setString(2, cart.getCupID());
			ps.setInt(3, cart.getQuantity());

			int rowsAffected = ps.executeUpdate();

			if (rowsAffected == 0) {
				alertHandler.showAlert(AlertType.ERROR, "Error", "Cart Error", "Failed to add to cart");
			} else {
				alertHandler.showAlert(AlertType.INFORMATION, "Message", "Cart Info", "Item Successfully added to cart!");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
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
	
	public Parent getRoot() {
		return this.containerBp;
	}
	
	public HomePage(User user) {
		this.user = user;
		init();
		setup();
		style();
		action();
		getData();
	}
}