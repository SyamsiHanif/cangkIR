package page;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import handler.AlertHandler;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import main.Main;
import model.Account;
import model.Admin;
import model.User;
import util.Connect;

public class LoginPage {
	
	Account account;
	
	ObservableList<Account> accountList;
	
	Label loginLb;
	Label usernameLb;
	Label passwordLb;
	
	TextField usernameTf;
	PasswordField passwordPf;
	
	Button loginBt;
	
	Hyperlink registerHp;
	
	VBox usernameVb;
	VBox passwordVb;
	VBox loginVb;
	
	BorderPane containerBp;
	
	AlertHandler alertHandler;
	
	Connect connect = Connect.getConnection();
	
	private void init() {
		alertHandler = new AlertHandler();
		
		loginLb = new Label("Login");
		usernameLb = new Label("Username");
		passwordLb = new Label("Password");
		
		usernameTf = new TextField();
		passwordPf = new PasswordField();
		
		loginBt = new Button("Login");
		
		registerHp = new Hyperlink("Don't have an account yet? Register here!");
		
		usernameVb = new VBox();
		passwordVb = new VBox();
		loginVb = new VBox(25);
		
		containerBp = new BorderPane();
		
	}
	
	private void setup() {
		usernameVb.getChildren().addAll(usernameLb, usernameTf);
		passwordVb.getChildren().addAll(passwordLb, passwordPf);
		
		usernameTf.setPromptText("Input your username here");
		passwordPf.setPromptText("Input your password here");
		
		loginVb.getChildren().addAll(loginLb, usernameVb, passwordVb, loginBt, registerHp);
		
		containerBp.setCenter(loginVb);
	}
	
	private void style() {
		loginLb.setFont(Font.font("Default", FontWeight.BOLD, 25));
		
		loginBt.setPrefSize(80, 35);
		
		loginVb.setAlignment(Pos.CENTER);
		loginVb.setMaxWidth(500);
	}
	
	private void action() {
		loginBt.setOnMouseClicked(e -> {
			Account account = validateUser();
			if (account instanceof Admin) {
				Main.sceneHandler.toCupManagementPage();
			} else if (account instanceof User) {
				Main.sceneHandler.toHomePage((User) account);
			}
		});
		
		registerHp.setOnMouseClicked(e -> {
			Main.sceneHandler.toRegisterPage();
		});
	}
	
	private Account validateUser() {
		String username = usernameTf.getText().trim();
		String password = passwordPf.getText();
		
		if (username.isEmpty()) {
			alertHandler.showAlert(AlertType.ERROR, "Error", "Login Error", "Please fill out your username");
			return null;
		}
		
		if (password.isEmpty()) {
			alertHandler.showAlert(AlertType.ERROR, "Error", "Login Error", "Please enter a password");
			return null;
		}
		
		String query = "SELECT * FROM msuser WHERE username = ? AND UserPassword = ?";
		try (PreparedStatement ps = connect.prepareStatement(query)) {
			ps.setString(1, username);
			ps.setString(2, password);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				String ID = rs.getString("UserID");
				String email = rs.getString("UserEmail");
				String gender = rs.getString("UserGender");
				boolean isAdmin = username.toLowerCase().contains("admin");
				if (isAdmin) {
					alertHandler.showAlert(AlertType.INFORMATION, "Information", "Role Assignment", "User assigned the role of Admin");
					return new Admin(ID, username, email, password, gender);
				} else {
					alertHandler.showAlert(AlertType.INFORMATION, "Information", "Role Assignment", "User assigned the role of User");
					return new User(ID, username, email, password, gender);
				}
			} else {
				alertHandler.showAlert(AlertType.ERROR, "Error", "Login Error", "Invalid username or password");
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			alertHandler.showAlert(AlertType.ERROR, "Error", "Database Error", "Failed to validate user");
			return null;
		}

	}
	
	public Parent getRoot() {
		return this.containerBp;
	}
	
	public LoginPage() {
		init();
		setup();
		style();
		action();
	}
}
