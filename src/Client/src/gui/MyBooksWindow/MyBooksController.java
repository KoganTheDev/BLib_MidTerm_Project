package gui.MyBooksWindow;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import client.ChatClient;
import client.ClientUI;
import gui.baseController.BaseController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import logic.BorrowedBook;
import logic.ClientTimeDiffController;

/**
 * Controller for the Search Window in the Library Management Tool.
 * 
 * <p>This class allows users to search for books based on name, description, 
 * and subject filters, and displays the results in a TableView. It also provides 
 * an option to navigate back to the Main Menu.</p>
 */
public class MyBooksController extends BaseController implements Initializable {
	private MyBooksController mbc;
    public static String FlagForSearch = "";
	
    @FXML
	private Label extensionDynamicLabel;
	
	@FXML
    private Button btnExit;
	
    @FXML
    private Button btnBackF;
    
    @FXML
    private Button btnHistory;
    
    @FXML
    private TableView<BorrowedBook> tableView;

    @FXML
    private TableColumn<BorrowedBook, Integer> tableID;
    
    @FXML
    private TableColumn<BorrowedBook, String> tableBorrowDate;
    
    @FXML
    private TableColumn<BorrowedBook, String> tableReturnDate;
    
    @FXML
    private TableColumn<BorrowedBook, String> tableName;
    
    @FXML
    private TableColumn<BorrowedBook, Integer> tableTimeLeft;
    
    @FXML
    private TableColumn<BorrowedBook, Void> tableActions;


    @FXML
    private TextField nameInput;

    @FXML
    private TextField descriptionInput;

    /**
     * Initializes the Search Window by setting up the TableView and ComboBox.
     * Populates the table with all available books when the page loads.
     *
     * @param url the location of the FXML file.
     * @param resourceBundle the resource bundle for internationalization.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize TableView columns
        tableID.setCellValueFactory(new PropertyValueFactory<>("borrowId")); // ID column
        tableName.setCellValueFactory(new PropertyValueFactory<>("name")); // Name column
        tableBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowedTime")); // Borrow Date column
        tableReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnTime")); // Return Date column
        tableTimeLeft.setCellValueFactory(new PropertyValueFactory<>("timeLeftToReturn")); // Time Left column

        setupActionsColumn();
        loadBooks();
    }

    
    private void loadBooks() {
        new Thread(() -> {
            ClientUI.chat.accept("GetBorrowedBooks:" + ChatClient.s1.getSubscriber_id());
            Platform.runLater(() -> {
                if (ChatClient.borrowedBookList != null && !ChatClient.borrowedBookList.isEmpty()) {
                    tableView.getItems().clear();
                    tableView.getItems().addAll(ChatClient.borrowedBookList);
                } else {
                    System.out.println("No borrowed books to display.");
                }
            });
        }).start();
    }
    
    




        // after TODO is done and subscriber's book are in DB we fetch them here into the table.
//        // Fetch and populate books
//        new Thread(() -> {
//        	ClientUI.chat.accept("GetBorrowedBooks:");
//            Platform.runLater(this::loadBooks); // Populate the table after data is fetched
//        }).start();
//    }
    /**
     * Loads borrowed books into the TableView by fetching them from the server.
     * If no books are borrowed, displays a message in the console.
     * TODO: Implement after adding subscriber's books to database. 
     */
//    private void loadBooks() {
//        // Send "GetBooks" request to the server to fetch the books
//        //ClientUI.chat.accept("GetBooks:");
//
//        // Ensure bookList is not empty
//        if (ChatClient.bookList != null && !ChatClient.bookList.isEmpty()) {
//            // Populate the TableView with all books initially
//            Platform.runLater(() -> {
//                tableView.getItems().clear();  // Clear any existing data
//                tableView.getItems().addAll(ChatClient.bookList);  // Add all books to the table
//            });
//        } else {
//            System.out.println("No books are currently borrowed.");
//        }
//    }
        private void setupActionsColumn() {
            tableActions.setCellFactory(param -> new TableCell<BorrowedBook, Void>() { // Explicitly specify the generic types
                private final Button extendButton = new Button("Extend borrowing length");
                private final Button returnButton = new Button("Return");
                private final HBox buttonBox = new HBox(10, extendButton, returnButton);

                {
                    buttonBox.setStyle("-fx-alignment: CENTER;");

                    extendButton.setOnAction(event -> {
                        BorrowedBook borrowedBook = getTableView().getItems().get(getIndex());
                        System.out.println("Extend borrowing length for book: " + borrowedBook.getName());
                        
                    });

                    returnButton.setOnAction(event -> {
                        BorrowedBook borrowedBook = getTableView().getItems().get(getIndex());
                        System.out.println("Return book: " + borrowedBook.getName());
                        ClientUI.chat.accept("Return request: Subscriber ID is "+ChatClient.s1.getSubscriber_id()+" "+ChatClient.s1.getSubscriber_name()+" Borrow info: "+borrowedBook);
                        ClientUI.chat.accept("Return Book: Subscriber ID is"+ChatClient.s1.getSubscriber_id()+" Book info is:"+borrowedBook);
                        
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null); // Clear the cell if empty
                    } else {
                        setGraphic(buttonBox); // Add the buttons to the cell
                    }
                }
            });
        }
        
        public void navigateToHistory(ActionEvent event) {
        	openWindow(event,
        			"/gui/HistoryWindow/HistoryFrame.fxml",
        			"/gui/HistoryWindow/HistoryFrame.css",
        			"History");
        	
        }
//
        public void extendButton(ActionEvent event, BorrowedBook selectedBook) {
        	ClientTimeDiffController timeDiffController = new ClientTimeDiffController();
        	LocalDateTime today = LocalDateTime.now();
        	String newReturnDate;}
        	/*
    		newReturnDate = timeDiffController.extendReturnDate(selectedBook.getReturnDate(), 14);
        	if(timeDiffController.hasEnoughTimeBeforeDeadline(today, selectedBook.getReturnDate(), 7)){
        		ClientUI.chat.accept("UpdateReturnDate:"+selectedBook.getBorrowId());
        		// TODO update the new return date in the DB
        	}
        	else 
        		showColoredLabelMessageOnGUI(extensionDynamicLabel, "Extension denied", "-fx-text-fill: red;");
        }*/

    /**
     * Searches for books based on filters entered in the name, description, and subject fields.
     * Displays the filtered results in the TableView.
     *
     * @param event the event triggered by clicking the search button.
     * @throws Exception if an error occurs during the search.
     */
//    public void Search(ActionEvent event) throws Exception {
//        ClientUI.chat.accept("GetBooks:");  // Get the latest books
//
//        // Ensure bookList is not empty
//        if (ChatClient.bookList != null && !ChatClient.bookList.isEmpty()) {
//            // If all filter fields are empty, show all books
//            if (nameInput.getText().isEmpty() && descriptionInput.getText().isEmpty() && (subjectInput.getValue() == null || subjectInput.getValue().toString().isEmpty())) {
//                // No filtering, show all books
//                Platform.runLater(() -> {
//                    tableView.getItems().clear();  // Clear any existing data
//                    tableView.getItems().addAll(ChatClient.bookList);  // Add all books to the table
//                });
//            } else {
//                // Otherwise, filter books based on the input fields
//                List<Book> filteredBooks = new ArrayList<>();
//
//                for (Book book : ChatClient.bookList) {
//                    boolean matches = true;
//
//                    // Check for name filter
//                    if (!nameInput.getText().isEmpty() && !book.getName().toLowerCase().contains(nameInput.getText().toLowerCase())) {
//                        matches = false;
//                    }
//
//                    // Check for description filter
//                    if (!descriptionInput.getText().isEmpty() && !book.getDescription().toLowerCase().contains(descriptionInput.getText().toLowerCase())) {
//                        matches = false;
//                    }
//
//                    // Check for subject filter from ComboBox
//                    if (subjectInput.getValue() != null && !subjectInput.getValue().toString().isEmpty() && !book.getSubject().toLowerCase().contains(subjectInput.getValue().toString().toLowerCase())) {
//                        matches = false;
//                    }
//
//                    // If all filters match, add the book to the filtered list
//                    if (matches) {
//                        filteredBooks.add(book);
//                    }
//                }
//
//                // Populate the TableView with the filtered books
//                Platform.runLater(() -> {
//                    tableView.getItems().clear();  // Clear any existing data
//                    tableView.getItems().addAll(filteredBooks);  // Add the filtered books to the table
//                });
//            }
//        } else {
//            System.out.println("No books to display.");
//        }
//    }


    /**
     * Handles the Exit button action, navigating back to the Main Menu.
     *
     * @param event the event triggered by clicking the exit button.
     */
    public void getExitBtn(ActionEvent event) {
    	openWindow(event,
    			"/gui/MainMenu/MainMenuFrame.fxml",
    			"/gui/MainMenu/MainMenuFrame.css",
    			"MainMenu");
    }
   
    public void backFromUser(ActionEvent event) {
    	openWindow(event,
    			"/gui/SubscriberWindow/SubscriberWindow.fxml",
    			"/gui/SubscriberWindow/SubscriberWindow.css",
    			"Subscriber View");
    	
    }

}
