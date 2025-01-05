import javax.swing.*;

public class WorkoutEditorApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WorkoutManager workoutManager = new WorkoutManager();
            WorkoutEditorController controller = new WorkoutEditorController(workoutManager);
            WorkoutEditorFrame frame = new WorkoutEditorFrame(controller);
            frame.setVisible(true);
        });
    }
}
