package gui.SubscriberRequestsWindows;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import client.ChatClient;
import client.ClientUI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import logic.Subscriber;
import gui.MainMenu.MainMenuController;

public class SubscriberRequestsWindowsController implements Initializable {
    private SubscriberRequestsWindowsController srwc;

    @FXML
    private Button btnExit = null;

    @FXML
    private Button btnSend = null;

    @FXML
    private Label LBL1;
    @FXML
    private Label LBL2;
    @FXML
    private Label LBL3;
    @FXML
    private Label LBL4;
    @FXML
    private Label LBL5;

    @FXML
    private TextField TXTF1;
    @FXML
    private TextField TXTF2;
    @FXML
    private TextField TXTF3;
    @FXML
    private TextField TXTF4;
    @FXML
    private TextField TXTF5;

    @FXML
    private ComboBox<String> RequestTypeCB;

    @FXML
    private ComboBox<String> RequestedByCB;
    @FXML
    private ComboBox<String> RequestCB;
    
    private List<String[]> borrowRequests = new ArrayList<>();
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize request types
        RequestTypeCB.getItems().addAll("", "Registers", "Extend Book Borrow", "Borrow For Subscriber", "Return For Subscriber");
        RequestedByCB.getItems().add(""); // Add an empty item for default value

        // Set event listeners
        RequestedByCB.setOnAction(event -> autofillSubscriberData()); // Set the event listener for ComboBox selection change
        RequestTypeCB.setOnAction(event -> updateLabels());
        
        ClientUI.chat.accept("FetchBorrowRequest:");
        handleFetchedBorrowedBooks();
    }

    // Method to update labels based on the selected request type
    public void updateLabels() {
        String selectedRequestType = RequestTypeCB.getValue();

        // Reset fields to empty by default
        LBL1.setText("");
        LBL2.setText("");
        LBL3.setText("");
        LBL4.setText("");
        LBL5.setText("");

        // Clear the RequestedByCB ComboBox
        RequestedByCB.getItems().clear();

        // Set the fields and populate the RequestedByCB based on the selected request type
        switch (selectedRequestType) {
            case "Registers":
                LBL1.setText("Subscriber Name:");
                LBL2.setText("Subscriber ID:");
                LBL3.setText("Email:");
                LBL4.setText("Phone Number:");
                TXTF4.setVisible(true);
                TXTF5.setVisible(false);
                break;
            case "Borrow For Subscriber":
                LBL1.setText("Subscriber Name:");
                LBL2.setText("Subscriber ID:");
                LBL3.setText("Book Name:");
                LBL4.setText("Book ID:");
                LBL5.setText("Borrow Time:");
                TXTF4.setVisible(true);
                TXTF5.setVisible(true);
                ClientUI.chat.accept("FetchBorrowRequest:");
                handleFetchedBorrowedBooks(); // Call the method to handle the fetched data
                break;
            case "Return For Subscriber":
                LBL1.setText("Subscriber Name:");
                LBL2.setText("Subscriber ID:");
                LBL3.setText("Book Name:");
                LBL4.setText("Book ID:");
                LBL5.setText("Return Time:");
                TXTF4.setVisible(true);
                TXTF5.setVisible(true);
                break;
            case "Extend Book Borrow":
                LBL1.setText("Subscriber Name:");
                LBL2.setText("Subscriber ID:");
                LBL3.setText("Book Name:");
                LBL4.setText("Book ID:");
                LBL5.setText("Extend Time:");
                TXTF4.setVisible(true);
                TXTF5.setVisible(true);
                break;
            default:
                LBL1.setText("");
                LBL2.setText("");
                LBL3.setText("");
                LBL4.setText("");
                LBL5.setText("");
                TXTF4.setVisible(true);
                TXTF5.setVisible(true);
                break;
        }
    }

    // Method to autofill text fields based on the selected subscriber
    private void autofillSubscriberData() {
        String selectedName = RequestedByCB.getValue();

        if (selectedName != null && !selectedName.isEmpty()) {
            // Clear the RequestCB ComboBox before adding new items
            RequestCB.getItems().clear();

            // Create a list to store borrow requests for the selected subscriber
            List<String[]> selectedRequests = new ArrayList<>();

            // Search for the selected subscriber's borrow requests
            for (String[] request : borrowRequests) {
                if (request[2].equals(selectedName)) { // Assuming name is in index 2
                    selectedRequests.add(request);  // Add the borrow request to the list
                }
            }

            // Populate the RequestCB ComboBox with book names and book IDs
            for (String[] request : selectedRequests) {
                // Format the book information as "Book Name (Book ID)"
                RequestCB.getItems().add(request[3] + " (Book ID: " + request[4] + ")");
            }

            // Set an action listener for RequestCB to autofill the text fields when a request is selected
            RequestCB.setOnAction(event -> autofillRequestData(selectedRequests));
        }
    }

    // Method to autofill text fields based on the selected borrow request in RequestCB
    private void autofillRequestData(List<String[]> selectedRequests) {
        String selectedRequest = RequestCB.getValue();

        if (selectedRequest != null && !selectedRequest.isEmpty()) {
            // Iterate through the selected requests and autofill the text fields based on the selected request
            for (String[] request : selectedRequests) {
                // Match the selected request format with "Book Name (Book ID)"
                if ((request[3] + " (Book ID: " + request[4] + ")").equals(selectedRequest)) {
                    // Autofill the text fields based on the selected borrow request
                    TXTF1.setText(request[2]);  // Subscriber Name
                    TXTF2.setText(request[1]);  // Subscriber ID
                    TXTF3.setText(request[3]);  // Book Name
                    TXTF4.setText(request[4]);  // Book ID
                    TXTF5.setText(request[5]);  // Borrow Time (or any other relevant field)
                    break;
                }
            }
        }
    }
    // Method to handle the fetched borrow requests from ChatClient
    public void handleFetchedBorrowedBooks() {
        borrowRequests.clear();  // Clear the existing list to avoid duplicate data

        // Ensure ChatClient.br is not null or empty
        if (ChatClient.br != null && ChatClient.br.size() > 0) {
            // Iterate over each row in ChatClient.br (each row is a borrow request)
            for (int i = 0; i < ChatClient.br.size(); i++) {
                String[][] borrowRequestArray = ChatClient.br.get(i); // Get the i-th 2D array

                // Iterate over each borrow request in the i-th 2D array (assuming each request has 8 fields)
                for (String[] request : borrowRequestArray) {
                    if (request.length == 8) {
                        borrowRequests.add(request);  // Add the borrow request (which is a String array) to the list
                    } else {
                        System.out.println("Invalid borrow request data at index " + i + ": " + String.join(",", request));
                    }
                }
            }

            // Populate the ComboBox with subscriber names from the borrow requests
            ObservableList<String> requestedByList = FXCollections.observableArrayList();
            for (String[] borrowRequest : borrowRequests) {
                requestedByList.add(borrowRequest[2]);  // Assuming the name is at index 2
            }
            RequestedByCB.setItems(requestedByList);
        } else {
            System.out.println("No borrow requests available.");
        }
    }

    // Method to handle Exit button click
    public void getExitBtn(ActionEvent event) throws Exception {
        try {
            ((Node) event.getSource()).getScene().getWindow().hide();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainMenu/MainMenuFrame.fxml"));
            Pane root = loader.load();

            MainMenuController mainMenuController = loader.getController();

            Stage primaryStage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/gui/MainMenu/MainMenuFrame.css").toExternalForm());
            primaryStage.setTitle("MainMenu");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to display messages (for debugging or logging)
    public void display(String message) {
        System.out.println(message);
    }
}