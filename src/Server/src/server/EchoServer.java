package server;

import java.awt.print.Book;
import java.io.IOException;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import common.ConnectToDb;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;


/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 */
public class EchoServer extends AbstractServer {
    // Class variables *************************************************
    final public static int DEFAULT_PORT = 5555;

    // Instance variables ***********************************************
    private Connection dbConnection; // Single DB connection

    // Constructors ****************************************************
    public EchoServer(int port) {
        super(port);
    }

    // Instance methods ************************************************
    @Override
    protected void serverStarted() {
        System.out.println("Server listening for connections on port " + getPort());
        try {
            dbConnection = ConnectToDb.getConnection(); // Open the connection
            System.out.println("Connected to the database.");
        } catch (SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
        }
    }

    @Override
    protected void serverStopped() {
        System.out.println("Server has stopped listening for connections.");
        if (dbConnection != null) {
            try {
                dbConnection.close(); // Close the connection
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.out.println("Error closing the database connection: " + e.getMessage());
            }
        }
    }

    @Override
    public void clientConnected(ConnectionToClient client) {
        // Display client connection details once when a client connects
        String clientIP = client.getInetAddress().getHostAddress();
        String clientHost = client.getInetAddress().getHostName();
        String connectionStatus = isOpen(client) ? "Connected" : "Disconnected";

        System.out.println("Client connected:");
        System.out.println("Client IP: " + clientIP);
        System.out.println("Client Hostname: " + clientHost);
        System.out.println("Connection Status: " + connectionStatus);
        // Send a message to the client to inform them of the successful connection
        try {
            client.sendToClient("You have successfully connected to the server.");
        } catch (IOException e) {
            System.out.println("Error sending connection message to client: " + e.getMessage());
        }
    }

    public void handleMessageFromClient(Object msg, ConnectionToClient client) {
        System.out.println("Message received: " + msg + " from " + client);

        try {
            String message = (String) msg;
            int delimiterIndex = message.indexOf(":");
            if (delimiterIndex == -1) {
                client.sendToClient("Invalid command format.");
                return;
            }

            String prefix = message.substring(0, delimiterIndex).trim(); // Extract the prefix
            String body = message.substring(delimiterIndex + 1).trim(); // Extract the body after the prefix

            switch (prefix) {
                case "Fetch": // Handle Fetch:ID
                    String identifier = body;

                    if (ConnectToDb.checkSubscriberExists(dbConnection, identifier)) {
                        String subscriberData = ConnectToDb.fetchSubscriberData(dbConnection, identifier);
                        client.sendToClient(subscriberData);
                    } else if (ConnectToDb.checkLibrarianExists(dbConnection, identifier)) {
                        String librarianData = ConnectToDb.fetchLibrarianData(dbConnection, identifier);
                        client.sendToClient(librarianData);
                    } else {
                        client.sendToClient("ID does not exist.");
                    }
                    break;

                case "Update": // Handle Update:ID,Phone,Email
                    String[] parts = body.split(",");
                    if (parts.length == 3) {
                        String subscriberId = parts[0].trim();
                        String phone = parts[1].trim();
                        String email = parts[2].trim();

                        if (ConnectToDb.checkSubscriberExists(dbConnection, subscriberId)) {
                            ConnectToDb.updateSubscriber(dbConnection, subscriberId, phone, email);
                            client.sendToClient("Subscriber updated successfully.");
                        } else {
                            client.sendToClient("Subscriber ID does not exist.");
                        }
                    } else {
                        client.sendToClient("Invalid Update command format. Expected 'Update:ID,Phone,Email'.");
                    }
                    break;

                case "IP": // Handle IP:<Address>
                    String clientIP = body;
                    String serverIP = InetAddress.getLocalHost().getHostAddress();

                    if (clientIP.equals(serverIP)) {
                        client.sendToClient("Client connected to IP: " + clientIP);
                    } else {
                        client.sendToClient("Could not connect to the server. The IP: " + clientIP + " does not match the server.");
                    }
                    break;

                case "GetBooks": // Handle GetBooks:
                    System.out.println("Received GetBooks request from client");

                    try {
                        List<String> booksData = ConnectToDb.fetchBooksData(dbConnection);

                        if (booksData == null || booksData.isEmpty()) {
                            client.sendToClient("returnedBookData:NoBooksFound");
                        } else {
                            String booksDataString = String.join(";", booksData);
                            client.sendToClient("returnedBookData:" + booksDataString);
                        }
                    } catch (Exception e) {
                        System.out.println("Error while processing GetBooks request: " + e.getMessage());
                        client.sendToClient("returnedBookData:Error:CouldNotFetchBooks");
                    }
                    break;
                case "GetBookInfo": // Handle GetBookInfo:BookId
                    String bookId = body; // The body contains the BookId
                    try {
                        // Fetch book information from the database
                        String bookInfo = ConnectToDb.fetchBookInfo(dbConnection, bookId);

                        if (bookInfo != null) {
                            client.sendToClient("BookInfo:" + bookInfo);
                        } else {
                            client.sendToClient("BookInfo:NotFound");
                        }
                    } catch (Exception e) {
                        System.out.println("Error while processing GetBookInfo request: " + e.getMessage());
                        client.sendToClient("BookInfo:Error:CouldNotFetchBookInfo");
                    }
                    break;
                case "BorrowRequest": // Handle BorrowRequest
                    System.out.println("Received BorrowRequest from client");
                    String[] borrowParts = body.split(",");
                    if (borrowParts.length == 6) {
                        String subscriberId = borrowParts[0].trim();
                        String subscriberName = borrowParts[1].trim();
                        String bookBorrowId = borrowParts[2].trim();
                        String bookName = borrowParts[3].trim();
                        String placeHolder1 = borrowParts[4].trim(); // Not used
                        String placeHolder2 = borrowParts[5].trim(); // Not used

                        // Debugging: Log the parameters before inserting
                        System.out.println("Subscriber ID: " + subscriberId);
                        System.out.println("Subscriber Name: " + subscriberName);
                        System.out.println("Book Borrow ID: " + bookBorrowId);
                        System.out.println("Book Name: " + bookName);
                        System.out.println("Placeholders: " + placeHolder1 + ", " + placeHolder2);

                        try {
                            // Example values for the time-related fields (can be empty strings if not needed)
                            String borrowTime = ""; // You can pass the actual borrow time if you have it
                            String returnTime = ""; // Leave empty if not used
                            String extendTime = ""; // Leave empty if not used

                            // Send the data to insertRequest
                            ConnectToDb.insertRequest(dbConnection, 
                                                       "Borrow For Subscriber", // requestType
                                                       subscriberId,             // requestedByID
                                                       subscriberName,           // requestedByName
                                                       bookName,                 // bookName
                                                       bookBorrowId,             // bookId
                                                       borrowTime,               // borrowTime (empty string if not available)
                                                       returnTime,               // returnTime (empty string if not available)
                                                       extendTime);              // extendTime (empty string if not available)
                        } catch (Exception e) {
                            client.sendToClient("An error occurred while processing the borrow request: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                case "FetchBorrowRequest": // Handle FetchBorrowRequest
                    System.out.println("Received FetchBorrowRequest from client");

                        try {
                            String borrowRequests = ConnectToDb.fetchBorrowRequest(dbConnection);
                            client.sendToClient("FetchedBorrowedBooks:"+borrowRequests);
                        } catch (Exception e) {
                            client.sendToClient("An error occurred while fetching the borrow request data: " + e.getMessage());
                            e.printStackTrace();
                        }
                    break;
                default: // Handle unknown commands
                    client.sendToClient("Unknown command.");
                    break;
            }
        } catch (SQLException | IOException e) {
            System.out.println("Error handling client message: " + e.getMessage());
            try {
                client.sendToClient("Server error: " + e.getMessage());
            } catch (IOException ioException) {
                System.out.println("Error sending message to client: " + ioException.getMessage());
            }
        }
    }


    private boolean isOpen(ConnectionToClient client) {
        return client != null;
    }
}
