package client;

import ocsf.client.*;
import client.*;
import common.ChatIF;
import logic.Book;
import logic.Subscriber;
import logic.Librarian;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.util.Duration;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
	
  // Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  public static Subscriber s1 = new Subscriber(0, 0, null, null, null);
  public static Librarian l1 = new Librarian(0, null);
  public static List<Book> bookList = new ArrayList<>(); // List to hold books
  public static List<String[][]> br = new ArrayList<>(); 
  public static String[] BorrowedBookInfo ;
  
  public static boolean awaitResponse = false;

  // Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  public ChatClient(String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); // Call the superclass constructor
    this.clientUI = clientUI;
    //openConnection();
  }

  // Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
	    System.out.println("--> handleMessageFromServer");
	    if (!(msg instanceof String)) {
	        System.out.println("Invalid message type received.");
	        return;
	    }

	    String response = (String) msg;
	    System.out.println(response);

	    // Dispatch handling based on message prefix
	    if (response.startsWith("subscriber_id:")) {
	        handleSubscriberData(response);
	    } else if (response.startsWith("librarian_id:")) {
	        handleLibrarianData(response);
	    } else if (response.equals("Subscriber ID does not exist.") || response.equals("Librarian ID does not exist.") || response.equals("ID does not exist.")) {
	        handleNonexistentID();
	    } else if (response.startsWith("Could not connect to the server.")) {
	        handleServerConnectionIssue(false);
	    } else if (response.startsWith("Client connected to IP: ")) {
	        handleServerConnectionIssue(true);
	    } else if (response.startsWith("returnedBookData:")) {
	        handleBookData(response.substring("returnedBookData:".length()));
	    } else if (response.startsWith("BookInfo:")){
	    	handleBookInfo(response.substring("BookInfo:".length()));
	    } else if (response.startsWith("FetchedBorrowedBooks:")){
	    	handleFetchedBorrowedBooks(response.substring("FetchedBorrowedBooks:".length()));
	    }else {
	    	handleUnknownResponse(response);
	    }
	}
  private void handleBookInfo(String data) {
	    try {
	        if (data.equals("NoBooksFound")) {
	            System.out.println("No books found in the database.");
	            BorrowedBookInfo = null;
	        } else if (data.startsWith("Error")) {
	            System.out.println("Error from server: " + data);
	            BorrowedBookInfo = null;
	        } else {
	            BorrowedBookInfo = data.split(",");
	            if (BorrowedBookInfo.length < 6) {
	                throw new IllegalArgumentException("Incomplete book data received.");
	            }
	          /*  System.out.println("Book ID: " + BorrowedBookInfo[0] + "\n" +
	                    "Book Name: " + BorrowedBookInfo[1] + "\n" +
	                    "Subject: " + BorrowedBookInfo[2] + "\n" +
	                    "Description: " + BorrowedBookInfo[3] + "\n" +
	                    "Available Copies: " + BorrowedBookInfo[4] + "\n" +
	                    "Location on Shelf: "+ BorrowedBookInfo[5]);
	          */
	        }
	    } catch (Exception e) {
	        System.out.println("Error handling book info: " + e.getMessage());
	        BorrowedBookInfo = null;
	    }
	}

	private void handleSubscriberData(String response) {
	    l1.setLibrarian_id(-1); // Reset librarian data
	    processSubscriberData(response);
	}

	private void handleLibrarianData(String response) {
	    s1.setSubscriber_id(-1); // Reset subscriber data
	    processLibrarianData(response);
	}

	private void handleNonexistentID() {
	    l1.setLibrarian_id(-1);
	    s1.setSubscriber_id(-1);
	}
	
	private void handleFetchedBorrowedBooks(String data) {
	    br.clear(); // Clear the existing list to avoid appending duplicate data

	    // Split the input data by semicolon (;) to separate each request
	    String[] requests = data.split(";");

	    // Iterate over each request (which is now a string) and split it by comma (,) to get the fields
	    for (String request : requests) {
	        // Split each request into fields by comma
	        String[] bookDetails = request.split(",");

	        // Ensure the bookDetails array has the expected length (8 fields)
	        if (bookDetails.length == 8) {
	            br.add(new String[][]{bookDetails}); // Add the book details array to the br list
	        } else {
	            System.out.println("Invalid book data received: " + String.join(",", bookDetails));
	        }
	    }
	}
	private void handleServerConnectionIssue(boolean isConnected) {
	    ClientUI.isIPValid = isConnected;
	    String message = isConnected ? "Server connection successful." : "Failed to connect to the server.";
	    System.out.println(message);
	}

	private void handleBookData(String data) {
	    if (data.equals("NoBooksFound")) {
	        System.out.println("No books found in the database.");
	    } else if (data.startsWith("Error")) {
	        System.out.println("Error from server: " + data);
	    } else {
	        parseBookData(data);
	    }
	}

	private void handleUnknownResponse(String response) {
	    System.out.println("Unknown response from server: " + response);
	}

	private void parseBookData(String data) {
	    try {
	        String[] bookStrings = data.split(";");
	        bookList.clear(); // Clear existing book data
	        for (String bookData : bookStrings) {
	            String[] fields = bookData.split(",");
	            int id = Integer.parseInt(fields[0]);
	            String name = fields[1];
	            String subject = fields[2];
	            String description = fields[3];
	            int availableCopies = Integer.parseInt(fields[4]);
	            String location = fields[5];
	            bookList.add(new Book(id, name, description, subject, availableCopies, location));
	        }
	    } catch (Exception e) {
	        System.out.println("Error parsing book data: " + e.getMessage());
	    }
	}
  
  public void handleMessageFromClientUI(String message) {
        awaitResponse = true;
        try {
            openConnection(); // Open connection for sending messages
            sendToServer(message);

            // Use a thread to wait for the response from the server
            new Thread(() -> {
                while (awaitResponse) {
                    try {
                        Thread.sleep(100);  // Delay in checking response
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Once the response is received, update the UI
                Platform.runLater(() -> {
                    try {
                        closeConnection();  // Close the connection after receiving the response
                        awaitResponse = false;
                        // Update the UI here, e.g., show the subscriber info
                        // You can now update your GUI elements safely here
                    } catch (IOException e) {
                        e.printStackTrace();
                        clientUI.display("Error closing connection: " + e.getMessage());
                    }
                });
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
            clientUI.display("Could not send message to server: Terminating client." + e);
            quit();
        }
    }
  private void processSubscriberData(String msg) {
      try {
          String[] parts = msg.split(",");
          String subscriberId = extractValue(parts[0]);
          String subscriberName = extractValue(parts[1]);
          String subscriptionHistory = extractValue(parts[2]);
          String phone = extractValue(parts[3]);
          String email = extractValue(parts[4]);

          // Set values to the Subscriber object
          s1.setSubscriber_id(Integer.parseInt(subscriberId));
          s1.setSubscriber_name(subscriberName);
          s1.setDetailed_subscription_history(Integer.parseInt(subscriptionHistory));
          s1.setSubscriber_phone_number(phone);
          s1.setSubscriber_email(email);
      } catch (Exception e) {
          System.out.println("Error parsing subscriber data: " + e.getMessage());
          s1.setSubscriber_id(-1); // Mark as not found
      }
  }

  private void processLibrarianData(String msg) {
      try {
          String[] parts = msg.split(",");
          String librarianId = extractValue(parts[0]);
          String librarianName = extractValue(parts[1]);

          // Set values to the Librarian object
          l1.setLibrarian_id(Integer.parseInt(librarianId));
          l1.setLibrarian_name(librarianName);
      } catch (Exception e) {
          System.out.println("Error parsing librarian data: " + e.getMessage());
          l1.setLibrarian_id(-1); // Mark as not found
      }
  }

  private String extractValue(String part) {
      return part.split(":")[1].trim();
  }
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
}
//End of ChatClient class
