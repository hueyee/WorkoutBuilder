import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.FileChooser;

public class WorkoutEditorFrame extends JFrame {
    private WorkoutTreePanel treePanel;
    private DetailsPanel detailsPanel;
    private JPanel buttonPanel;
    private boolean javaFXInitialized = false;

    public WorkoutEditorFrame(WorkoutEditorController controller) {
        // Initialize JavaFX
        initializeJavaFX();
        
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

    /**
     * Initialize JavaFX runtime for FileChooser
     */
    private void initializeJavaFX() {
        if (!javaFXInitialized) {
            // This will initialize JavaFX runtime
            new JFXPanel();
            javaFXInitialized = true;
        }
    }

    /**
     * Find the best Documents folder, prioritizing OneDrive on Windows
     */
    private File findBestDocumentsFolder() {
        String userHome = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase();
        
        // On Windows, try to find OneDrive Documents folder first
        if (os.contains("windows")) {
            // Try OneDrive Documents folder
            File oneDriveDocuments = new File(userHome, "OneDrive\\Documents");
            if (oneDriveDocuments.exists() && oneDriveDocuments.isDirectory()) {
                return new File(oneDriveDocuments, "Workouts");
            }
            
            // Try OneDrive for Business (various patterns)
            File userHomeDir = new File(userHome);
            if (userHomeDir.exists()) {
                File[] oneDriveFolders = userHomeDir.listFiles((dir, name) -> 
                    name.startsWith("OneDrive - ") && new File(dir, name).isDirectory());
                
                if (oneDriveFolders != null && oneDriveFolders.length > 0) {
                    File oneDriveBusinessDocs = new File(oneDriveFolders[0], "Documents");
                    if (oneDriveBusinessDocs.exists() && oneDriveBusinessDocs.isDirectory()) {
                        return new File(oneDriveBusinessDocs, "Workouts");
                    }
                }
            }
        }
        
        // Fallback to regular Documents folder
        return new File(userHome, "Documents/Workouts");
    }


    private void loadWorkout(WorkoutEditorController controller) {
        // Define the default folder - prioritize OneDrive Documents on Windows
        File workoutFolder = findBestDocumentsFolder();

        // Ensure the directory exists; if not, create it
        if (!workoutFolder.exists()) {
            workoutFolder.mkdirs();
        }

        // Use JavaFX FileChooser on JavaFX Application Thread
        Platform.runLater(() -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load Workout");
            fileChooser.setInitialDirectory(workoutFolder);
            
            // Set file extension filter for JSON files
            FileChooser.ExtensionFilter jsonFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
            fileChooser.getExtensionFilters().add(jsonFilter);
            
            // Show the file chooser dialog
            File selectedFile = fileChooser.showOpenDialog(null);
            
            if (selectedFile != null) {
                // Switch back to EDT for Swing operations
                SwingUtilities.invokeLater(() -> {
                    try {
                        controller.loadWorkout(selectedFile);
                        treePanel.updateTree(controller.getCurrentWorkout());
                        detailsPanel.updateDetails(controller.getCurrentWorkout());
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this, "Failed to load workout: " + ex.getMessage());
                    }
                });
            }
        });
    }


    private void saveWorkout(WorkoutEditorController controller) {
        Workout currentWorkout = controller.getCurrentWorkout();
        if (currentWorkout == null || currentWorkout.getId() == null || currentWorkout.getId().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Workout ID is missing or invalid. Please ensure the workout has a valid ID before saving.");
            return;
        }

        // Define the default folder - prioritize OneDrive Documents on Windows
        File workoutFolder = findBestDocumentsFolder();

        // Ensure the directory exists
        if (!workoutFolder.exists()) {
            workoutFolder.mkdirs();
        }

        // Use JavaFX FileChooser on JavaFX Application Thread
        Platform.runLater(() -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Workout");
            fileChooser.setInitialDirectory(workoutFolder);
            
            // Set file extension filter for JSON files
            FileChooser.ExtensionFilter jsonFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
            fileChooser.getExtensionFilters().add(jsonFilter);
            
            // Set default file name using the workout ID
            String fileName = currentWorkout.getId() + ".json";
            fileChooser.setInitialFileName(fileName);

            // Show the file chooser dialog
            File selectedFile = fileChooser.showSaveDialog(null);
            
            if (selectedFile != null) {
                // Ensure the file has a .json extension
                if (!selectedFile.getName().endsWith(".json")) {
                    selectedFile = new File(selectedFile.getParentFile(), selectedFile.getName() + ".json");
                }

                final File finalFile = selectedFile;
                // Switch back to EDT for Swing operations
                SwingUtilities.invokeLater(() -> {
                    try {
                        controller.saveWorkout(finalFile);
                        JOptionPane.showMessageDialog(this, "Workout saved successfully!");
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(this, "Failed to save workout: " + ex.getMessage());
                    }
                });
            }
        });
    }


}
