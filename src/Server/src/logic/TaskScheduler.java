package logic;

import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TaskScheduler {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private FreezeController freezeController = new FreezeController();
    
    public void startDailyTasks() {
        Runnable dailyTask = new Runnable() {
            public void run() {
            	try { // TODO: Add more functions that need to run daily in here.
					Thread.sleep(1000); // Allow the SQL connection to be set so on startup so the function can work correctly.
					freezeController.freezeControllerDailyActivities(); // Run all of the daily activities needed from the freezeController.
				} catch (InterruptedException e) {
					e.printStackTrace();
				}catch (SQLException e) {
					e.printStackTrace();
				}
            	
				
                updateDatabase();
            }
        };
        scheduler.scheduleAtFixedRate(dailyTask, 0, 1, TimeUnit.SECONDS); // Change timeUnit value to DAYS, WEEKS, MONTHS etc. accordingly to your needs.
    }

    private void updateDatabase() {
        // Your database update logic here
        System.out.println("Database updated!");
    }

    public void stop() {
        scheduler.shutdown();
    }
}
