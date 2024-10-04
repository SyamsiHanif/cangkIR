package handler;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertHandler {
	
	public void showAlert(AlertType alertType, String title,String header, String content) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}
	
}
