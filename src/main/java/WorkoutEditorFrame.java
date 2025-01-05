import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class WorkoutEditorFrame extends JFrame {
    private WorkoutTreePanel treePanel;
    private DetailsPanel detailsPanel;
    private JPanel buttonPanel;

    public WorkoutEditorFrame(WorkoutEditorController controller) {
        setTitle("Workout Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Increase the size of the window
        setSize(1400, 800); // Updated width and height for more space
        setLayout(new BorderLayout());

        // Create UI components
        treePanel = new WorkoutTreePanel(controller);
        detailsPanel = new DetailsPanel(controller, treePanel);
        buttonPanel = new JPanel();

        // Create buttons
        JButton loadButton = new JButton("Load Workout");
        JButton saveButton = new JButton("Save Workout");
        JButton newWorkoutButton = new JButton("New Workout");

        // Add actions
        loadButton.addActionListener(e -> loadWorkout(controller));
        saveButton.addActionListener(e -> saveWorkout(controller));
        newWorkoutButton.addActionListener(e -> {
            Workout newWorkout = controller.createNewWorkout();
            treePanel.updateTree(newWorkout);
            detailsPanel.updateDetails(newWorkout);
            detailsPanel.getBlockDetailsPanel().resetUI(); // Reset the BlockDetails UI
        });


        // Add buttons to panel
        buttonPanel.add(loadButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(newWorkoutButton);

        // Add components to frame
        add(treePanel, BorderLayout.WEST);
        add(detailsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }


    private void loadWorkout(WorkoutEditorController controller) {
        // Define the default folder in the Documents directory
        String userHome = System.getProperty("user.home");
        File workoutFolder = new File(userHome, "Documents/Workouts");

        // Ensure the directory exists; if not, create it
        if (!workoutFolder.exists()) {
            workoutFolder.mkdirs();
        }

        // Create the file chooser and set the default directory
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(workoutFolder);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                controller.loadWorkout(file);
                treePanel.updateTree(controller.getCurrentWorkout());
                detailsPanel.updateDetails(controller.getCurrentWorkout());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to load workout: " + ex.getMessage());
            }
        }
    }


    private void saveWorkout(WorkoutEditorController controller) {
        Workout currentWorkout = controller.getCurrentWorkout();
        if (currentWorkout == null || currentWorkout.getId() == null || currentWorkout.getId().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Workout ID is missing or invalid. Please ensure the workout has a valid ID before saving.");
            return;
        }

        // Define the default folder in the Documents directory
        String userHome = System.getProperty("user.home");
        File workoutFolder = new File(userHome, "Documents/Workouts");

        // Ensure the directory exists
        if (!workoutFolder.exists()) {
            workoutFolder.mkdirs();
        }

        // Create the file chooser and set the default directory
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(workoutFolder);

        // Set default file name using the workout ID
        String fileName = currentWorkout.getId() + ".json";
        fileChooser.setSelectedFile(new File(workoutFolder, fileName));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            // Ensure the file has a .json extension
            if (!file.getName().endsWith(".json")) {
                file = new File(file.getParentFile(), file.getName() + ".json");
            }

            try {
                controller.saveWorkout(file);
                JOptionPane.showMessageDialog(this, "Workout saved successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to save workout: " + ex.getMessage());
            }
        }
    }


}
