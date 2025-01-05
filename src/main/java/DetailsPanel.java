import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DetailsPanel extends JPanel {
    private JTextField idField;
    private JTextField nameField;
    private JCheckBox completedCheckBox;
    private BlockDetailPanel blockDetailsArea;

    public DetailsPanel(WorkoutEditorController controller, WorkoutTreePanel treePanel) {
        setLayout(new GridBagLayout()); // Flexible layout manager
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Add padding around components

        // Row 1: ID Label and Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END; // Align label to the right
        add(new JLabel("ID:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Stretch field horizontally
        idField = new JTextField(15); // Set preferred width
        add(idField, gbc);

        // Add listener for ID field updates
        idField.getDocument().addDocumentListener(new WorkoutDocumentListener() {
            @Override
            public void update() {
                Workout currentWorkout = controller.getCurrentWorkout();
                if (currentWorkout != null) {
                    currentWorkout.setId(idField.getText().trim());
                    treePanel.updateTree(currentWorkout);
                }
            }
        });

        // Row 2: Name Label and Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE; // Reset fill
        gbc.anchor = GridBagConstraints.LINE_END;
        add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        nameField = new JTextField(15);
        add(nameField, gbc);

        // Add listener for Name field updates
        nameField.getDocument().addDocumentListener(new WorkoutDocumentListener() {
            @Override
            public void update() {
                Workout currentWorkout = controller.getCurrentWorkout();
                if (currentWorkout != null) {
                    currentWorkout.setName(nameField.getText().trim());
                    treePanel.updateTree(currentWorkout);
                }
            }
        });

        // Row 3: Completed Label and Checkbox
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_END;
        add(new JLabel("Completed:"), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START; // Align checkbox to the left
        completedCheckBox = new JCheckBox();
        add(completedCheckBox, gbc);

        // Add listener for Completed checkbox updates
        completedCheckBox.addActionListener(e -> {
            Workout currentWorkout = controller.getCurrentWorkout();
            if (currentWorkout != null) {
                currentWorkout.setCompleted(completedCheckBox.isSelected());
            }
        });

        // Row 4: Block Details Label and Text Area
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.FIRST_LINE_END; // Align to the top
        add(new JLabel("Block Details:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH; // Stretch area both horizontally and vertically
        gbc.weightx = 1.0; // Allow horizontal expansion
        gbc.weighty = 1.0; // Allow vertical expansion
        blockDetailsArea = new BlockDetailPanel(controller, treePanel); // Set preferred size
        add(new JScrollPane(blockDetailsArea), gbc);
    }

    /**
     * Updates the fields based on the current workout.
     *
     * @param workout The workout to display in the panel.
     */
    public void updateDetails(Workout workout) {
        if (workout != null) {
            idField.setText(workout.getId());
            nameField.setText(workout.getName());
            completedCheckBox.setSelected(workout.isCompleted());
            blockDetailsArea.loadBlocks(workout.getBlocks());
        } else {
            idField.setText("");
            nameField.setText("");
            completedCheckBox.setSelected(false);
            blockDetailsArea.loadBlocks(new ArrayList<>());
        }
    }

    public BlockDetailPanel getBlockDetailsPanel() {
        return blockDetailsArea;
    }

}

