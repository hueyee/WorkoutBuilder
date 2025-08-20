import javax.swing.*;
import javafx.embed.swing.JFXPanel;

public class WorkoutEditorApp {
    public static void main(String[] args) {
        // Initialize JavaFX runtime at application startup
        // This needs to be done before any JavaFX components are used
        initializeJavaFX();
        
        SwingUtilities.invokeLater(() -> {
            WorkoutManager workoutManager = new WorkoutManager();
            WorkoutEditorController controller = new WorkoutEditorController(workoutManager);
            WorkoutEditorFrame frame = new WorkoutEditorFrame(controller);
            frame.setVisible(true);
        });
    }
    
    /**
     * Initialize JavaFX runtime at application startup.
     * This is better practice than initializing it within UI components.
     */
    private static void initializeJavaFX() {
        // This will initialize JavaFX runtime
        new JFXPanel();
    }
}
