package main;

import handler.SceneHandler;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import page.LoginPage;

public class Main extends Application {
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public static SceneHandler sceneHandler;
	Scene primaryScene;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryScene = new Scene(new LoginPage().getRoot());
		sceneHandler = new SceneHandler(primaryScene);
		sceneHandler.toLoginPage();
		primaryStage.setScene(primaryScene);
		primaryStage.setTitle("cangkIR");
		primaryStage.setWidth(720);
		primaryStage.setHeight(680);
		primaryStage.show();
	}
}
