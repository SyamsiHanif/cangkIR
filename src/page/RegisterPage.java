package page;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import handler.AlertHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import main.Main;
import model.Account;
import model.Admin;
import model.User;
import util.Connect;

public class RegisterPage {
	
	Account account;
	
	AlertHandler alertHandler;
	
	Label registerLb;
	Label emailLb;
	Label usernameLb;
	Label passwordLb;
	Label genderLb;
	
	TextField usernameTf;
	TextField emailTf;
	PasswordField passwordPf;
	
	RadioButton maleRb;
	RadioButton femaleRb;
	ToggleGroup genderTg;
	
	Button registerBt;
	
	Hyperlink loginHp;
	
	HBox genderHb;
	
	VBox usernameVb;
	VBox emailVb;
	VBox passwordVb;
	VBox loginVb;
	VBox genderLbVb;
	VBox genderVb;
	
	BorderPane containerBp;
	
	Connect connect = Connect.getConnection();
	
	private void init() {
		alertHandler = new AlertHandler();
		
		registerLb = new Label("Register");
		usernameLb = new Label("Username");
		emailLb = new Label("Email");
		passwordLb = new Label("Password");
		genderLb = new Label("Gender");
		
		usernameTf = new TextField();
		emailTf = new TextField();
		passwordPf = new PasswordField();
		
		maleRb = new RadioButton("Male");
		femaleRb = new RadioButton("Female");
		genderTg = new ToggleGroup();
		
		registerBt = new Button("Register");
		loginHp = new Hyperlink("Alraedy have an account? Click here to login!");
		
		genderHb = new HBox(20);
		
		usernameVb = new VBox();
		emailVb = new VBox();
		passwordVb = new VBox();
		loginVb = new VBox(25);
		genderLbVb = new VBox();
		genderVb = new VBox();
		
		containerBp = new BorderPane();
	}
	
	private void setup() {
		genderTg.getToggles().addAll(maleRb, femaleRb);
		
		usernameVb.getChildren().addAll(usernameLb, usernameTf);
		emailVb.getChildren().addAll(emailLb, emailTf);
		passwordVb.getChildren().addAll(passwordLb, passwordPf);
		genderLbVb.getChildren().add(genderLb);
		genderHb.getChildren().addAll(maleRb, femaleRb);
		
		usernameTf.setPromptText("Input your username here");
		emailTf.setPromptText("Input your email here");
		passwordPf.setPromptText("Input your password here");
		
		loginVb.getChildren().addAll(registerLb, usernameVb, emailVb, passwordVb, genderLbVb, genderHb, registerBt, loginHp);
		
		containerBp.setCenter(loginVb);
	}
	
	private void style() {
		registerLb.setFont(Font.font("Default", FontWeight.BOLD, 25));
		
		genderLb.setFont(Font.font("Default", FontWeight.BOLD, 25));
		genderLbVb.setAlignment(Pos.BASELINE_LEFT);
		
		registerBt.setPrefSize(80, 35);
		
		loginVb.setAlignment(Pos.CENTER);
		loginVb.setMaxWidth(500);
	}
	
	private void action() {
		registerBt.setOnMouseClicked(e -> {
			if (validateUser()) {
				insertData(account);
				alertHandler.showAlert(AlertType.INFORMATION, "Success", "Register Success", "Account successfully registered");
				Main.sceneHandler.toLoginPage();
			}
		});
		
		loginHp.setOnMouseClicked(e -> {
			Main.sceneHandler.toLoginPage();
		});
	}
	
	private boolean validateUser() {
		String id = generateID();
		String username = usernameTf.getText().trim();
		String email = emailTf.getText().trim();
		String password = passwordPf.getText();
		String gender = maleRb.isSelected() ? "Male" : "Female";
		boolean isGenderSelected = maleRb.isSelected() || femaleRb.isSelected();
		
		if (username.isEmpty()) {
			alertHandler.showAlert(AlertType.ERROR, "Error", "Register Error", "Please fill out your username");
			return false;
		}
		
		if (isUsernameExists(username)) {
			alertHandler.showAlert(AlertType.ERROR, "Error", "Register Error", "Please choose a different username");
			return false;
		}

		if (email.isEmpty()) {
			alertHandler.showAlert(AlertType.ERROR, "Error", "Register Error", "Please enter an email address");
			return false;
		}
		
		if (!email.toLowerCase().endsWith("@gmail.com")) {
			alertHandler.showAlert(AlertType.ERROR, "Error", "Register Error", "Make sure your email ends with @gmail.com");
			return false;
		}
		
		if (isEmailExists(email)) {
			alertHandler.showAlert(AlertType.ERROR, "Error", "Register Error", "Please choose a different email");
			return false;
		}

		if (password.isEmpty()) {
			alertHandler.showAlert(AlertType.ERROR, "Error", "Register Error", "Please enter a password");
			return false;
		}
		
		if (password.length() < 8 || password.length() > 15) {
			alertHandler.showAlert(AlertType.ERROR, "Error", "Register Error", "Make sure your password has a length of 8 - 15 characters");
			return false;
		}
		
		if (!password.matches("^[a-zA-Z0-9]+")) {
			alertHandler.showAlert(AlertType.ERROR, "Error", "Register Error", "Password must be alphanumeric");
			return false;
		}

		if (!isGenderSelected) {
			alertHandler.showAlert(AlertType.ERROR, "Error", "Register Error", "Please select a gender");
			return false;
		}

		if (username.toLowerCase().contains("admin")) {
			account = new Admin(id, username, email, password, gender);
		} else {
			account = new User(id, username, email, password, gender);
		}
		return true;
	}

	private boolean isUsernameExists(String username) {
		String query = "SELECT UserID FROM msuser WHERE Username = ?";
		try (PreparedStatement ps = connect.prepareStatement(query)) {
			ps.setString(1, username);

			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean isEmailExists(String email) {
		String query = "SELECT UserID FROM msuser WHERE UserEmail = ?";
		try (PreparedStatement ps = connect.prepareStatement(query)) {
			ps.setString(1, email);

			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private void insertData(Account account) {
		String query = "INSERT INTO msuser (UserID, Username, UserEmail, UserPassword, UserGender, UserRole) VALUES (?, ?, ?, ?, ?, ?)";

		try (PreparedStatement ps = connect.prepareStatement(query)) {
			ps.setString(1, account.getUserID());
			ps.setString(2, account.getUsername());
			ps.setString(3, account.getEmail());
			ps.setString(4, account.getPassword());
			ps.setString(5, account.getGender());
			ps.setString(6, account instanceof Admin ? "Admin" : "User");

			int rowsAffected = ps.executeUpdate();

			if (rowsAffected == 0) {
				alertHandler.showAlert(AlertType.ERROR, "Error", "Registration Error", "Failed to register the account. Please try again.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private String generateID() {
		try {
			String query = "SELECT MAX(UserID) FROM msuser";
			ResultSet rs = connect.executeQuery(query);
			if (rs.next()) {
				String maxID = rs.getString(1);
				if (maxID != null) {
					int numericPart = Integer.parseInt(maxID.substring(2)) + 1;
					return String.format("US%03d", numericPart);
				} else {
					return "US001";
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
	
	public RegisterPage() {
		init();
		setup();
		style();
		action();
	}
}
