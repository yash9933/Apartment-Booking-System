package controllers;

import java.net.URL;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicLong;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;

import dao.DBConnect;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import models.RoomModel;
import models.ViewsRouting;

public class AdminAvailableRoomController implements Initializable {

	@FXML
	private StackPane stackpane_availablerooms;

	@FXML
	private TableView<RoomModel> tblViewRooms;
	@FXML
	private TableColumn<RoomModel, String> roomId;
	@FXML
	private TableColumn<RoomModel, String> roomType;
	@FXML
	private TableColumn<RoomModel, String> roomNumber;
	@FXML
	private TableColumn<RoomModel, String> numberOfPeople;
	@FXML
	private TableColumn<RoomModel, String> roomPrice;
	@FXML
	private TableColumn<RoomModel, String> roomStatus;

	@FXML
	private TextField txtSearch;

	@FXML
	private Button btnSearchRoomNo;

	@FXML
	private Button btnMakeAvail;

	@FXML
	private Label lblSearch;

	private ObservableList<RoomModel> roomList;

	DBConnect conn = null;
	Statement stmt = null;
	ViewsRouting viewr = null;
	RoomModel roomModel = null;
	String loginUserName = null;
	String loginUserPass = null;

	public AdminAvailableRoomController() {
		conn = new DBConnect();
		viewr = new ViewsRouting();
		roomModel = new RoomModel();
	}

	public void initData(String username, String password) {
		this.loginUserName = username;
		this.loginUserPass = password;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

		String query = "SELECT * FROM  room_reserv_sample";
		roomList = roomModel.getRooms(query);
		loadData(query);
	}

	public void loadData(String query) {

		roomId.setCellValueFactory(new PropertyValueFactory<RoomModel, String>("roomId"));
		roomType.setCellValueFactory(new PropertyValueFactory<RoomModel, String>("roomType"));
		roomNumber.setCellValueFactory(new PropertyValueFactory<RoomModel, String>("roomNumber"));
		numberOfPeople.setCellValueFactory(new PropertyValueFactory<RoomModel, String>("numberOfPeople"));
		roomPrice.setCellValueFactory(new PropertyValueFactory<RoomModel, String>("roomPrice"));
		roomStatus.setCellValueFactory(new PropertyValueFactory<RoomModel, String>("roomStatus"));

		// auto adjust width of columns depending on their content
		tblViewRooms.setItems(roomList);
		tblViewRooms.setColumnResizePolicy((param) -> true);
		Platform.runLater(() -> customResize(tblViewRooms));
	}

	public void customResize(TableView<?> view) {
		AtomicLong width = new AtomicLong();
		view.getColumns().forEach(col -> {
			width.addAndGet((long) col.getWidth());
		});
		double tableWidth = view.getWidth();

		if (tableWidth > width.get()) {
			view.getColumns().forEach(col -> {
				col.setPrefWidth(col.getWidth() + ((tableWidth - width.get()) / view.getColumns().size()));
			});
		}
	}

	@FXML
	void makeAvailableRoom(ActionEvent event) {
		String searchText = txtSearch.getText().toString();
		if (searchText == "" || searchText == null) {
			handleDialog();
		} else {
			int roomNo = Integer.parseInt(searchText);
			int result = roomModel.updateRoomByRoomNumber(roomNo);
			if (result > 0) {
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Room Info Update");
				alert.setHeaderText("Modication");
				alert.setContentText("Record updated successfully!");
				alert.showAndWait();
				String query = "SELECT * FROM  room_reserv_sample where 1";
				roomList = roomModel.getRooms(query);
				loadData(query);
			} else {
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle("Room Info Update");
				alert.setHeaderText("Error");
				alert.setContentText("Database Error!");
				alert.showAndWait();
			}
		}
	}

	@FXML
	public void onAvailableRooms() {
		txtSearch.clear();
		String query = "SELECT * FROM  room_reserv_sample";
		roomList = roomModel.getRooms(query);
		loadData(query);
	}

	@FXML
	void searchByRoomNo(ActionEvent event) {
		String searchText = txtSearch.getText().toString();

		if (searchText == "" || searchText == null) {
			handleDialog();
		} else {
			String query = "SELECT * FROM  room_reserv_sample where roomnumber =" + Integer.parseInt(searchText) + ";";
			roomList = roomModel.getRooms(query);
			loadData(query);
		}
	}

	public void handleDialog() {
		JFXDialogLayout dialogLayout = new JFXDialogLayout();
		dialogLayout.setHeading(new Text("Alert"));
		dialogLayout.setBody(new Text("Please enter Valid Room Number"));

		JFXButton ok = new JFXButton("Ok");

		JFXDialog dialog = new JFXDialog(stackpane_availablerooms, dialogLayout, JFXDialog.DialogTransition.CENTER);

		ok.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				dialog.close();
			}
		});

		dialogLayout.setActions(ok);
		dialog.show();
	}

	@FXML
	public void onBackBtn() {
		viewr.handleRoutingAdminPage("/views/AdminPageView.fxml", stackpane_availablerooms, loginUserName, loginUserPass,"Admin Home");
	}

	@FXML
	public void onExitBtn() {
		System.exit(0);
	}

}
