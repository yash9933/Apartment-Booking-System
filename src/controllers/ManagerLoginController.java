package controllers;

import java.net.URL;
import java.sql.Statement;
import java.util.ResourceBundle;

import dao.DBConnect;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import models.CRUDModel;
import models.ViewsRouting;

public class ManagerLoginController implements Initializable {
	@FXML
	private AnchorPane manager_view_pane;

	@FXML
	private TextField txtManagerUsername;
	@FXML
	private PasswordField txtManagerPassword;
	@FXML
	private Label lblError;

	DBConnect conn = null;
	Statement stmt = null;
	ViewsRouting viewr = null;
	CRUDModel model = null;

	public ManagerLoginController() {
		conn = new DBConnect();
		viewr = new ViewsRouting();
		model = new CRUDModel();
	}

	@FXML
	private void onManagerLogin() {

		lblError.setText("");
		String username = this.txtManagerUsername.getText();
		String password = this.txtManagerPassword.getText();

		// Validations
		if (username == null || username.trim().equals("")) {
			lblError.setText("Username Cannot be empty or spaces");
			return;
		}
		if (password == null || password.trim().equals("")) {
			lblError.setText("Password Cannot be empty or spaces");
			return;
		}
		if (username == null || username.trim().equals("") && (password == null || password.trim().equals(""))) {
			lblError.setText("User name / Password Cannot be empty or spaces");
			return;
		}

		// authentication check
		checkCredentials(username, password);

	}

	@FXML
	private void onManagerBackButton() {
		viewr.handleRouting("/views/HomescreenView.fxml", manager_view_pane,"Home");
	}

	private void checkCredentials(String username, String password) {
		// TODO Auto-generated method stub
		Boolean isValid = model.getManagerCredentials(username, password);
		if (!isValid) {
			lblError.setText("User does not exist!");
			return;
		}
		try {
			if (model.isAdmin() && isValid) {
				// If user is admin, inflate admin view
				viewr.handleRouting("/views/ManagerPageView.fxml", manager_view_pane,"Manager Home");

			}

		} catch (Exception e) {
			System.out.println("Error occured while inflating view: " + e);
		}

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

	}

}
