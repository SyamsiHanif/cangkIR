package handler;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.User;
import page.CartPage;
import page.CupManagementPage;
import page.HomePage;
import page.LoginPage;
import page.RegisterPage;

public class SceneHandler {
	
	private Scene primaryScene;
	
	public void toLoginPage() {
		LoginPage loginPage = new LoginPage();
		primaryScene.setRoot(loginPage.getRoot());
	}

	public void toRegisterPage() {
		RegisterPage registerPage = new RegisterPage();
		primaryScene.setRoot(registerPage.getRoot());
	}

	public void toHomePage(User user) {
		HomePage homePage = new HomePage(user);
		primaryScene.setRoot(homePage.getRoot());
	}

	public void toCartPage(User user) {
		CartPage cartPage = new CartPage(user);
		primaryScene.setRoot(cartPage.getRoot());
	}

	public void toCupManagementPage() {
		CupManagementPage cupManagementPage = new CupManagementPage();
		primaryScene.setRoot(cupManagementPage.getRoot());
	}
	
	public SceneHandler(Scene primaryScene) {
		this.primaryScene = primaryScene;
	}
	
}
