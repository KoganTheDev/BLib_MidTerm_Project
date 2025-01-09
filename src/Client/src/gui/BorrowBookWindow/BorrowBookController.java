package gui.BorrowBookWindow;

import java.io.IOException;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import client.ChatClient;
import client.ClientController;
import client.ClientUI;
import common.ChatIF;
import gui.MainMenu.MainMenuController;
import gui.SearchWindow.SearchFrameController;
import gui.SubscriberRequestsWindows.SubscriberRequestsWindowsController;
import gui.SubscriberWindow.SubscriberWindowController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.Subscriber;
import gui.baseController.*;

/**
 * Controller class for the IP Input window of the Library Management Tool.
 * 
 * <p>This class manages the user interactions for inputting the server IP address,
 * validating it, and navigating to the main menu if the connection is successful.</p>
 */
public class BorrowBookController extends BaseController {

    @FXML
    private Button btnExit = null;

    @FXML
    private Button btnSubmit = null;

    @FXML
    private Button btnBack = null;
    @FXML
    private Button btnSearch = null;
    @FXML
    private Button btnSubmitToLibrarian = null;
    @FXML
    private Button btnMainMenu = null;
    @FXML
    private TextField IDtxt;

    @FXML
    private Label Book_Description;
    String bookId ="";
    String bookName = "";
    public void start(Stage primaryStage) throws Exception {
        start(primaryStage, 
              "/gui/BorrowBookWindow/BorrowBookFrame.fxml", 
              "/gui/BorrowBookWindow/BorrowBookFrame.css", 
              "Library Management Tool");
    }
    public void Submit(ActionEvent event) throws Exception {
    	bookId = IDtxt.getText();
        ChatClient.awaitResponse = true; // Ensure waiting for response
        ClientUI.chat.accept("GetBookInfo:" + bookId);  // Request book info

        // Wait until response is received
        PauseTransition pause = new PauseTransition(Duration.seconds(0.1)); // Adjust duration if necessary
        pause.setOnFinished(e -> {
            if (ChatClient.BorrowedBookInfo != null) {
                Book_Description.setText(
                    "Book ID: " + bookId + "\n" +
                    "Book Name: " + ChatClient.BorrowedBookInfo[1] + "\n" +
                    "Subject: " + ChatClient.BorrowedBookInfo[2] +"\n" +
                    "Description: " + ChatClient.BorrowedBookInfo[3] + "\n" +
                    "Available Copies: " + ChatClient.BorrowedBookInfo[4] +"\n" +
                    "Location on Shelf: " + ChatClient.BorrowedBookInfo[5]
                    		
                    
                );
                bookName = ChatClient.BorrowedBookInfo[1];
            } else {
                Book_Description.setText("No Book Found");
            }
        });
        pause.play();
    }

    public void Search(ActionEvent event) throws Exception{
    	SearchFrameController.FlagForSearch = "SubscriberBorrower";
	    openWindow(event,
	    		"/gui/SearchWindow/SearchFrame.fxml",
	    		"/gui/SearchWindow/SearchFrame.css",
	    		"Search a Book");
	
    }
    public void Back(ActionEvent event) throws Exception{
    	openWindow(event,
    			"/gui/SubscriberWindow/SubscriberWindow.fxml",
    			"/gui/SubscriberWindow/SubscriberWindow.css",
    			"MainMenu");
    }
    @SuppressWarnings("unused")
	public void Submit_Borrow_Request(ActionEvent event) throws Exception {
        // Collect subscriber and book details
        String subscriberId = "" +SubscriberWindowController.currentSubscriber.getSubscriber_id();
        String subscriberName = SubscriberWindowController.currentSubscriber.getSubscriber_name();

        
        String PlaceHolder = ""; // change to borrowed time and return time
        // Create a new BorrowRequest object
        String borrowRequest = ""+subscriberId+","+ subscriberName+","+ bookId+","+ bookName+","+ subscriberId+","+ subscriberId;
        ClientUI.chat.accept("BorrowRequest:" + borrowRequest);
        // Feedback to the user
        if (borrowRequest != null) {
            showAlert("Success", "Borrow request submitted successfully!\nAwaiting Librarian approval");
        } else {
            showAlert("Error", "Failed to submit borrow request. Please try again.");
        }
    }


    public void Main_Menu(ActionEvent event) throws Exception{
    	SearchFrameController.FlagForSearch = "";
    	openWindow(event, 
                "/gui/MainMenu/MainMenuFrame.fxml", 
                "/gui/MainMenu/MainMenuFrame.css", 
                "MainMenu");
    }

    /**
     * Displays a message in the console.
     *
     * @param message the message to display.
     */
    public void display(String message) {
        System.out.println(message);
    }
}
