package gui.MainMenu;

import java.io.IOException;

import gui.SearchWindow.SearchFrameController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MainMenuController {

		private MainMenuController mmc;	
		
		@FXML
		private Button btnExit = null;
		
		@FXML
		private Button btnSearch = null;
		@FXML
		private Button btnLogin = null;
		@FXML
		private Button btnBack = null;
		
		public void search(ActionEvent event) throws Exception {
		        openSearchWindow(event);
		}
		
//		private void checkSubscriberResponse(ActionEvent event) {
//		    if (ChatClient.s1.getSubscriber_id() == -1) {
//		        System.out.println("Subscriber ID Not Found");
//		    } else {
//		        System.out.println("Subscriber ID Found");
//
//		        try {
//		            // Hide the current window
//		            ((Node) event.getSource()).getScene().getWindow().hide();
//
//		            // Load the SubscriberForm window
//		            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SubscriberForm.fxml"));
//		            Pane root = loader.load();
//
//		            SubscriberFormController subscriberFormController = loader.getController();
//		            subscriberFormController.loadSubscriber(ChatClient.s1);
//
//		            Stage primaryStage = new Stage();
//		            Scene scene = new Scene(root);
//		            scene.getStylesheets().add(getClass().getResource("/gui/SubscriberForm.css").toExternalForm());
//		            primaryStage.setTitle("Subscriber Management Tool");
//		            primaryStage.setScene(scene);
//		            primaryStage.show();
//		        } catch (IOException e) {
//		            e.printStackTrace();
//		        }
//		    }
		
		
		private void openSearchWindow(ActionEvent event){
			try {
	            // Hide the current window
	            ((Node) event.getSource()).getScene().getWindow().hide();

	            // Load the SubscriberForm window
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SearchWindow/SearchFrame.fxml"));
	            Pane root = loader.load();

	            SearchFrameController searchWindowController = loader.getController();
	            //mainMenuController.loadSubscriber(ChatClient.s1);

	            Stage primaryStage = new Stage();
	            Scene scene = new Scene(root);
	            scene.getStylesheets().add(getClass().getResource("/gui/SearchWindow/SearchFrame.css").toExternalForm());
	            primaryStage.setTitle("Search a Book");
	            primaryStage.setScene(scene);
	            primaryStage.show();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}

		public void start(Stage primaryStage) throws Exception {	
			Parent root = FXMLLoader.load(getClass().getResource("/gui/MainMenuController/MainMenuFrame.fxml"));
					
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/gui/MainMenuController/MainMenuFrame.css").toExternalForm());
			primaryStage.setTitle("Library Managment Tool");
			primaryStage.setScene(scene);
			
			primaryStage.show();	 	   
		}
		
		public void getExitBtn(ActionEvent event) throws Exception {
			System.out.println("exit Library Tool");	
			System.exit(1);
		}
}
