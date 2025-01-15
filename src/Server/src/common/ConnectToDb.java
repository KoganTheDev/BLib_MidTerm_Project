package common;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
//import org.omg.CORBA.Request;


public class ConnectToDb {
    // Method to establish a connection to the database
    public static Connection getConnection() throws SQLException {
        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            System.out.println("Driver definition succeed");
        } catch (Exception ex) {
            System.out.println("Driver definition failed");
            throw new SQLException("Driver loading failed", ex);
        }

        // Return the established connection
        return DriverManager.getConnection("jdbc:mysql://localhost/blib?serverTimezone=IST&allowPublicKeyRetrieval=true&useSSL=false", "root", "Aa123456");
    }
    /*
        // Method to fetch all data from the subscriber table
        public static List<String> fetchAllData(Connection conn) {
            List<String> result = new ArrayList<>();
            String query = "SELECT * FROM subscriber";

            try (PreparedStatement pstmt = conn.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {

                // Process each row in the result set
                while (rs.next()) {
                    int id = rs.getInt("subscriber_id");
                    String name = rs.getString("subscriber_name");
                    int detailed = rs.getInt("detailed_subscription_history");
                    String phone = rs.getString("subscriber_phone_number");
                    String email = rs.getString("subscriber_email");

                    String row = "subscriber_id:" + id + ", subscriber_name:" + name +
                                 ", detailed_subscription_history:" + detailed +
                                 ", subscriber_phone_number:" + phone +
                                 ", subscriber_email:" + email;

                    result.add(row);
                }
            } catch (SQLException e) {
                System.out.println("Error while fetching data: " + e.getMessage());
            }

            return result; // Return all data as a list of strings
        }
    */
    // Method to return the book by removing it from the borrowed_books table
    public static String returnbook(Connection dbConnection, String subscriberId, String borrowid) {
        String result = "Book return failed"; // Default return status

        // SQL query to delete the book from the borrowed_books table based on subscriber_id and book_name
        String sql = "DELETE FROM borrowed_books WHERE subscriber_id = ? AND borrow_id = ?";

        try (PreparedStatement stmt = dbConnection.prepareStatement(sql)) {
            stmt.setString(1, subscriberId); // Set the subscriber ID
            stmt.setString(2, borrowid); // Set the book name
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                result = "Book returned successfully"; // Success message
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle database exceptions
            result = "Error while returning book";
        }

        return result; // Return the status message
    }
    // Method to fetch data for a specific subscriber based on their ID
    public static String fetchSubscriberData(Connection conn, String subscriberId) {
        String query = "SELECT * FROM subscriber WHERE subscriber_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, Integer.parseInt(subscriberId));

            try (ResultSet rs = pstmt.executeQuery()) {
                // Check if a record exists
                if (rs.next()) {
                    int id = rs.getInt("subscriber_id");
                    String name = rs.getString("subscriber_name");
                    int detailed = rs.getInt("detailed_subscription_history");
                    String phone = rs.getString("subscriber_phone_number");
                    String email = rs.getString("subscriber_email");
                    String status = rs.getString("status");
                    
                    return "subscriber_id:" + id + ", subscriber_name:" + name +
                            ", detailed_subscription_history:" + detailed +
                            ", subscriber_phone_number:" + phone +
                            ", subscriber_email:" + email +
                            ", status:" + status;
                    
                } else {
                    return "No subscriber found";
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while fetching subscriber data: " + e.getMessage());
            return "Error fetching subscriber data.";
        }
    }
    public static String fetchLibrarianData(Connection conn, String librarianId) {
        String query = "SELECT * FROM librarian WHERE librarian_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, Integer.parseInt(librarianId));

            try (ResultSet rs = pstmt.executeQuery()) {
                // Check if a record exists
                if (rs.next()) {
                    int id = rs.getInt("librarian_id");
                    String name = rs.getString("librarian_name");

                    return "librarian_id:" + id + ", librarian_name:" + name;

                } else {
                    return "No labrarian found";
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while fetching subscriber data: " + e.getMessage());
            return "Error fetching subscriber data.";
        }
    }
    
    
    public static String fetchHistoryData(Connection conn, String subscriberId) {
    	String historyData = "";
        String query = "SELECT history FROM detailed_subscription_history WHERE detailed_subscription_history = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, subscriberId);  // Bind the subscriberId parameter
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                historyData = rs.getString("history");  // Corrected column name to "history"
            }

        } catch (SQLException e) {
            System.out.println("Error fetching history data: " + e.getMessage());
        }

        return historyData;
    }


    
    
    
    
    public static List<String> fetchBorrowedBooksBySubscriberId(Connection conn, String subscriberId) {
        String query = "SELECT * FROM blib.borrowed_books WHERE subscriber_id = ?";

        List<String> borrowedBooks = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, subscriberId); // Set the subscriber_id parameter

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Combine fields into a single delimited string for sending to the client
                    String bookData = rs.getInt("borrow_id") + "," +
                    				  rs.getInt("subscriber_id") + "," +
                                      rs.getString("Name") + "," +
                                      rs.getString("Subject") + "," +
                                      rs.getString("Borrowed_Time") + "," +
                                      rs.getString("Return_Time") + "," +
                                      rs.getString("ISBN");
                    borrowedBooks.add(bookData);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching borrowed books: " + e.getMessage());
        }

        return borrowedBooks; // Return the list of borrowed books
    }



    
    public static boolean checkIfIdExists(Connection dbConnection, String RegisterId) throws SQLException {
        String query = "SELECT COUNT(*) FROM requests WHERE RequestedById = ?";
        try (PreparedStatement stmt = dbConnection.prepareStatement(query)) {
            stmt.setString(1, RegisterId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0; // If count > 0, the ID exists
            }
        }
        return false; // Default to false if no records are found
    }

    public static List<String> fetchBooksData(Connection conn) {
        String query = "SELECT * FROM blib.books"; // Adjust the query as needed

        List<String> booksList = new ArrayList<>(); // Initialize the list to store books as strings

        try (PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            // Check if ResultSet has any rows
            if (!rs.next()) {
                System.out.println("No data found in books table.");
                return booksList; // Return empty list if no rows are found
            }

            // Loop through the result set and convert each row into a string
            do {
                String id = rs.getString("ISBN"); // Treat ISBN as a String
                String name = rs.getString("Name");
                String subject = rs.getString("Subject");
                String description = rs.getString("ShortDescription");
                int availableCopies = rs.getInt("NumCopies");
                String location = rs.getString("ShelfLocation");

                // Handle potential null values
                if (id == null) id = "Unknown ID";
                if (name == null) name = "Unknown";
                if (subject == null) subject = "N/A";
                if (description == null) description = "No description available";
                if (location == null) location = "Unknown location";

                // Format the book data into a single string (e.g., CSV format)
                String bookData = id + "," + name + "," + subject + "," + description + ","
                                  + availableCopies + "," + location;

                booksList.add(bookData); // Add the formatted string to the list
            } while (rs.next());

        } catch (SQLException e) {
            e.printStackTrace(); // This will show the full exception stack trace
            System.out.println("Error while fetching books data: " + e.getMessage());
            return null; // Return null in case of an error (or handle this as needed)
        }

        return booksList; // Return the list of books as strings
    }


    // Check if a subscriber exists based on their ID(PK)
    public static boolean checkSubscriberExists(Connection conn, String subscriberId) throws SQLException {
        String query = "SELECT * FROM subscriber WHERE subscriber_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, Integer.parseInt(subscriberId));
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();  // Returns true if subscriber exists, otherwise false
            }
        }
    }
    public static boolean checkLibrarianExists(Connection conn, String librarianId) throws SQLException {
        String query = "SELECT * FROM librarian WHERE librarian_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, Integer.parseInt(librarianId));
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();  // Returns true if subscriber exists, otherwise false
            }
        }
    }
    // Update a subscriber's phone and email
    public static void updateSubscriber(Connection conn, String subscriberId, String phone, String email) throws SQLException {
        String query = "UPDATE subscriber SET subscriber_phone_number = ?, subscriber_email = ? WHERE subscriber_id = ?";

        // Debug log to check inputs
        System.out.println("Updating subscriber: " + subscriberId + " with phone: " + phone + " and email: " + email);

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, phone);
            pstmt.setString(2, email);
            pstmt.setInt(3, Integer.parseInt(subscriberId));

            // Execute update and check the number of affected rows
            int affectedRows = pstmt.executeUpdate();

            // Debugging: Check if rows were affected
            if (affectedRows > 0) {
                System.out.println("Update successful: " + affectedRows + " row(s) affected.");
            } else {
                System.out.println("Update failed: No rows updated. Subscriber ID might not exist.");
            }
        }
    }
    public static String fetchBookInfo(Connection conn, String bookId) {
        String query = "SELECT * FROM books WHERE ISBN = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, bookId); // Use setString

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String id = rs.getString("ISBN"); // Get ID as String
                    String name = rs.getString("Name");
                    String subject = rs.getString("Subject");
                    String description = rs.getString("ShortDescription");
                    int availableCopies = rs.getInt("NumCopies");
                    String location = rs.getString("ShelfLocation");

                    name = (name == null) ? "Unknown" : name;
                    subject = (subject == null) ? "N/A" : subject;
                    description = (description == null) ? "No description available" : description;
                    location = (location == null) ? "Unknown location" : location;

                    return String.format(
                            "%s,%s,%s,%s,%d,%s",
                            id, name, subject, description, availableCopies, location);
                } else {
                    return "No book found";
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while fetching book info: " + e.getMessage());
            return "Error fetching book info.";
        }
    }
    public static void insertRequest(Connection conn, String requestType, String requestedByID, String requestedByName,
            String bookName, String bookId, String borrowTime, String returnTime, String extendTime)
            throws SQLException {

    // SQL query to insert a new record into the requests table
    String query = "INSERT INTO requests (requestType, requestedByID, requestedByName, bookName, bookId, borrowTime, returnTime, extendTime) "
                 + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
        // Set the values for each field in the query
        pstmt.setString(1, requestType);
        pstmt.setString(2, requestedByID);
        pstmt.setString(3, requestedByName);
        pstmt.setString(4, bookName);
        pstmt.setString(5, bookId);
        pstmt.setString(6, (borrowTime != null && !borrowTime.isEmpty()) ? borrowTime : "temp");
        pstmt.setString(7, (returnTime != null && !returnTime.isEmpty()) ? returnTime : "temp");
        pstmt.setString(8, (extendTime != null && !extendTime.isEmpty()) ? extendTime : "temp");

        // Execute the insert and get the number of affected rows
        int affectedRows = pstmt.executeUpdate();

        // Debugging: Check if rows were inserted
        if (affectedRows > 0) {
            System.out.println("Insert successful: " + affectedRows + " row(s) inserted.");
        } else {
            System.out.println("Insert failed: No rows inserted.");
        }
    }
}
    public static String fetchBorrowRequest(Connection conn) throws SQLException {
        StringBuilder result = new StringBuilder();

        // SQL query to fetch Borrow request based on the requestedByID and bookId
        String query = "SELECT * FROM requests WHERE requestType = 'Borrow For Subscriber'";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Execute the query and get the result set
            try (ResultSet rs = pstmt.executeQuery()) {
                // Process each result
                while (rs.next()) {
                    // Concatenate the fields with commas
                    result.append(rs.getString("requestType")).append(",")
                          .append(rs.getString("requestedByID")).append(",")
                          .append(rs.getString("requestedByName")).append(",")
                          .append(rs.getString("bookName")).append(",")
                          .append(rs.getString("bookId")).append(",")
                          .append(rs.getString("borrowTime")).append(",")
                          .append(rs.getString("returnTime")).append(",")
                          .append(rs.getString("extendTime"));

                    // Append a semicolon to separate each request
                    result.append(";");
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
            e.printStackTrace();
        }

        // Remove the trailing semicolon if there are any results
        if (result.length() > 0) {
            result.setLength(result.length() - 1);
        }

        return result.toString();
    }
    @SuppressWarnings("unused")
	public static void decreaseNumCopies(Connection conn, String bookId) throws SQLException {
        // SQL query to decrease NumCopies by 1 for the given bookId
        String query = "UPDATE books SET NumCopies = NumCopies - 1 WHERE ISBN = ? AND NumCopies > 0";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            // Set the bookId parameter
            pstmt.setString(1, bookId);

            // Execute the update statement
            int affectedRows = pstmt.executeUpdate();

            // If no rows were updated, it means there are no copies left or the bookId does not exist
            if (affectedRows == 0) {
                System.out.println("No copies available or invalid bookId: " + bookId);
            } else {
                System.out.println("Successfully decreased NumCopies for bookId: " + bookId);
            }
        }
    }
    public static String fetchReturnRequest(Connection conn) throws SQLException {
        StringBuilder result = new StringBuilder();

        // SQL query to fetch Return request based on the requestedByID and bookId
        String query = "SELECT * FROM requests WHERE requestType = 'Return For Subscriber'";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Execute the query and get the result set
            try (ResultSet rs = pstmt.executeQuery()) {
                // Process each result
                while (rs.next()) {
                    // Concatenate the fields with commas
                    result.append(rs.getString("requestType")).append(",")
                          .append(rs.getString("requestedByID")).append(",")
                          .append(rs.getString("requestedByName")).append(",")
                          .append(rs.getString("bookName")).append(",")
                          .append(rs.getString("bookId")).append(",")
                          .append(rs.getString("borrowTime")).append(",")
                          .append(rs.getString("returnTime")).append(",")
                          .append(rs.getString("extendTime"));

                    // Append a semicolon to separate each request
                    result.append(";");
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
            e.printStackTrace();
        }

        // Remove the trailing semicolon if there are any results
        if (result.length() > 0) {
            result.setLength(result.length() - 1);
        }

        return result.toString();
    }

    public static String fetchExtendRequest(Connection conn) throws SQLException {
        StringBuilder result = new StringBuilder();

        // SQL query to fetch Extend request based on the requestedByID and bookId
        String query = "SELECT * FROM requests WHERE requestType = 'Extend For Subscriber'";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Execute the query and get the result set
            try (ResultSet rs = pstmt.executeQuery()) {
                // Process each result
                while (rs.next()) {
                    // Concatenate the fields with commas
                    result.append(rs.getString("requestType")).append(",")
                          .append(rs.getString("requestedByID")).append(",")
                          .append(rs.getString("requestedByName")).append(",")
                          .append(rs.getString("bookName")).append(",")
                          .append(rs.getString("bookId")).append(",")
                          .append(rs.getString("borrowTime")).append(",")
                          .append(rs.getString("returnTime")).append(",")
                          .append(rs.getString("extendTime"));

                    // Append a semicolon to separate each request
                    result.append(";");
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
            e.printStackTrace();
        }

        // Remove the trailing semicolon if there are any results
        if (result.length() > 0) {
            result.setLength(result.length() - 1);
        }

        return result.toString();
    }
    
    
    public static void insertBorrowBook(Connection conn, String body) throws SQLException {
        // Split the body string by commas
        String[] parts = body.split(",");

        // Assuming the body contains the following values:
        // SName, SID, BName, ISBN, Btime in the respective order
        String SName = parts.length > 0 ? parts[0] : "temp"; // Subscriber Name
        int SID = parts.length > 1 ?   Integer.parseInt(parts[1]) : -1; // Subscriber ID
        String BName = parts.length > 2 ? parts[2] : "temp"; // Book Name (not used in the query, but included for reference)
        String ISBN = parts.length > 3 ? parts[3] : "temp"; // Book ISBN
        String Btime = parts.length > 4 ? parts[4] : "temp"; // Borrow Time

        // SQL query to insert a new record into the borrowed_books table
        String query = "INSERT INTO borrowed_books (ISBN, subscriber_id, Name, Subject, Borrowed_Time, Return_Time) "
                     + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            // Set the values for each field in the query
            pstmt.setString(1, ISBN); // ISBN
            pstmt.setInt(2, SID); // subscriber_id
            pstmt.setString(3, SName); // Name
            pstmt.setString(4, "temp"); // Subject (since it's not provided, use "temp")
            pstmt.setString(5, Btime); // Borrow Time
            pstmt.setString(6, "temp"); // Return Time (since it's not provided, use "temp")

            // Execute the insert and get the number of affected rows
            int affectedRows = pstmt.executeUpdate();

            // Debugging: Check if rows were inserted
            if (affectedRows > 0) {
                System.out.println("Insert successful: " + affectedRows + " row(s) inserted.");
            } else {
                System.out.println("Insert failed: No rows inserted.");
            }
        }
    }

    public static String fetchRegisterRequest(Connection conn) throws SQLException {
        StringBuilder result = new StringBuilder();

        // SQL query to fetch Register request based on the requestedByID and bookId
        String query = "SELECT * FROM requests WHERE requestType = 'Request For Register'";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Execute the query and get the result set
            try (ResultSet rs = pstmt.executeQuery()) {
                // Process each result
                while (rs.next()) {
                    // Concatenate the fields with commas
                    result.append(rs.getString("requestType")).append(",")
                          .append(rs.getString("requestedByID")).append(",")
                          .append(rs.getString("requestedByName")).append(",")
                          .append(rs.getString("bookName")).append(",")
                          .append(rs.getString("bookId")).append(",")
                          .append(rs.getString("borrowTime")).append(",")
                          .append(rs.getString("returnTime")).append(",")
                          .append(rs.getString("extendTime"));

                    // Append a semicolon to separate each request
                    result.append(";");
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
            e.printStackTrace();
        }

        // Remove the trailing semicolon if there are any results
        if (result.length() > 0) {
            result.setLength(result.length() - 1);
        }

        return result.toString();
    }
    
    public static void updateReturnDateAfterExtension(int borrowId, Connection con) {
        // SQL query to update the return date for a specific borrowed book
        String query = "UPDATE borrowed_books SET Return_Time = ? WHERE borrow_id = ?";
/* FIX
        try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
            // Set the query parameters
            preparedStatement.setString(1, newReturnDate); // New return date
            preparedStatement.setInt(2, borrowId); // Borrow ID of the book
            
            // Execute the update
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Return date updated successfully for Borrow ID: " + borrowId);
            } else {
                System.out.println("No record found for Borrow ID: " + borrowId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to update the return date for Borrow ID: " + borrowId);
        }*/
    }






}
