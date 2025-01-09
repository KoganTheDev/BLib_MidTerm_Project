package gui.ServerPort;

import gui.baseController.BaseController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import server.ServerUI;

public class ServerPortFrameController extends BaseController{
	
	String temp="";
	
	@FXML
	private Button btnExit = null;
	@FXML
	private Button btnDone = null;
	@FXML
	private Label lbllist;
	
	@FXML
	private TextField portxt;
	ObservableList<String> list;
	
	private String getport() {
		return portxt.getText();			
	}
	
	public void Done(ActionEvent event) throws Exception {
		String p;
		
		p=getport();
		if(p.trim().isEmpty()) {
			System.out.println("You must enter a port number");
					
		}
		else
		{
			((Node)event.getSource()).getScene().getWindow().hide(); //hiding primary window
			ServerUI.runServer(p);
			
			openWindow(event,
					"/gui/ServerLog/ServerLogFrame.fxml",
					"/gui/ServerLog/ServerLogFrame.css",
					"Server Log");
		}
	}

	public void start(Stage primaryStage) throws Exception {	
		start(primaryStage,
			  "/gui/ServerPort/ServerPort.fxml",
			  "/gui/ServerPort/ServerPort.css",
			  "Server - Enter Port");
	}
	
	public void getExitBtn(ActionEvent event) throws Exception {
		System.out.println("exit Academic Tool");
		System.exit(0);			
	}
}