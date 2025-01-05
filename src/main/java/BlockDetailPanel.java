import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BlockDetailPanel extends JPanel {
    private JComboBox<String> blockSelector;
    //private JTextField blockNameField;
    private JComboBox<String> blockTypeSelector;
    private DefaultListModel<String> exerciseListModel;
    private JList<String> exerciseList;
    private JTextArea setDetailArea;
    private JButton addExerciseButton;
    private JButton removeExerciseButton;
    private JTable setTable;
    private DefaultTableModel setTableModel;
    private JButton addSetButton;
    private JButton editSetButton;
    private JButton removeSetButton;

    // Block management buttons
    private JButton addBlockButton;
    private JButton removeBlockButton;
    private JButton moveBlockUpButton;
    private JButton moveBlockDownButton;

    private JTextField durationField;

    private Block currentBlock;
    private List<Block> blocks; // List of blocks to manage

    private WorkoutEditorController controller;
    private WorkoutTreePanel treePanel;

    public BlockDetailPanel(WorkoutEditorController controller, WorkoutTreePanel treePanel) {
        this.controller = controller;
        this.treePanel = treePanel;
        blocks = new ArrayList<>();

        setLayout(new BorderLayout());

        // Block Selection Panel
        JPanel blockSelectionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5)); // FlowLayout for horizontal alignment

        blockSelector = new JComboBox<>();
        blockSelector.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) { // Detect double click
                    renameBlock();
                }
            }
        });

        //blockNameField = new JTextField(15);

        addBlockButton = new JButton("Add Block");
        removeBlockButton = new JButton("Remove Block");
        moveBlockUpButton = new JButton("Move Up");
        moveBlockDownButton = new JButton("Move Down");

        // Set preferred sizes to ensure uniformity
        Dimension uniformSize = new Dimension(150, 25); // Width and height of components
        blockSelector.setPreferredSize(uniformSize);
        addBlockButton.setPreferredSize(uniformSize);
        removeBlockButton.setPreferredSize(uniformSize);
        moveBlockUpButton.setPreferredSize(uniformSize);
        moveBlockDownButton.setPreferredSize(uniformSize);



        // Adding components in a single row
        blockSelectionPanel.add(new JLabel("Block:"));
        blockSelectionPanel.add(blockSelector); // Add the combo box
        blockSelectionPanel.add(addBlockButton); // Add the "Add Block" button
        blockSelectionPanel.add(removeBlockButton); // Add the "Remove Block" button
        blockSelectionPanel.add(moveBlockUpButton);
        blockSelectionPanel.add(moveBlockDownButton);
        moveBlockUpButton.addActionListener(e -> moveBlock(-1));
        moveBlockDownButton.addActionListener(e -> moveBlock(1));

        // Add the panel to the frame or container
        add(blockSelectionPanel, BorderLayout.NORTH);


        // Exercise Panel
        JPanel exercisePanel = new JPanel(new BorderLayout());
        exerciseListModel = new DefaultListModel<>();
        exerciseList = new JList<>(exerciseListModel); // Initialize exerciseList with the model
        exerciseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        exerciseList.setDragEnabled(true);
        exerciseList.setDropMode(DropMode.INSERT);
        exerciseList.setTransferHandler(new ExerciseTransferHandler(this));
        exerciseList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                JList.DropLocation dropLocation = list.getDropLocation();
                if (dropLocation != null && dropLocation.getIndex() == index && dropLocation.isInsert()) {
                    renderer.setBackground(Color.LIGHT_GRAY);
                }

                return renderer;
            }
        });
        exerciseList.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) { // Detect double click
                    int index = exerciseList.locationToIndex(e.getPoint());
                    if (index != -1) {
                        renameExercise(index);
                    }
                }
            }
        });

        exercisePanel.add(new JScrollPane(exerciseList), BorderLayout.CENTER);


        JPanel exerciseButtonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        addExerciseButton = new JButton("Add Exercise");
        removeExerciseButton = new JButton("Remove Exercise");
        exerciseButtonPanel.add(addExerciseButton);
        exerciseButtonPanel.add(removeExerciseButton);
        exercisePanel.add(exerciseButtonPanel, BorderLayout.SOUTH);



        // Add the duration panel to the exercise section


        add(exercisePanel, BorderLayout.WEST);

        String[] columnNames = {"Percent", "Source", "Reps", "Load %", "Failed", "Weight"};
        setTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true; // All cells are editable
            }
        };
        setTableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();

            // Ensure the current block and exercise are valid
            if (currentBlock == null || exerciseList.getSelectedIndex() == -1) {
                return;
            }

            // Get the selected exercise
            Exercise selectedExercise = currentBlock.getExercises().get(exerciseList.getSelectedIndex());

            // Ensure the row is valid
            if (row >= 0 && row < selectedExercise.getSets().size()) {
                Set setToEdit = selectedExercise.getSets().get(row);

                // Update the appropriate field in the Set object
                switch (column) {
                    case 0 -> setToEdit.setPercent((String) setTableModel.getValueAt(row, column));
                    case 1 -> setToEdit.setSource((String) setTableModel.getValueAt(row, column));
                    case 2 -> setToEdit.setReps((String) setTableModel.getValueAt(row, column));
                    case 3 -> setToEdit.setLoadPercent((String) setTableModel.getValueAt(row, column));
                    case 4 -> setToEdit.setFailed((Boolean) setTableModel.getValueAt(row, column));
                    case 5 -> setToEdit.setWeight((String) setTableModel.getValueAt(row, column));
                }

                // Optional: Update the tree if necessary
                updateTree();
            }
        });

        setTable = new JTable(setTableModel);
        JScrollPane setTableScrollPane = new JScrollPane(setTable);

        // Create buttons for managing sets
        addSetButton = new JButton("Add Set");
        editSetButton = new JButton("Edit Set");
        removeSetButton = new JButton("Remove Set");

        JPanel setButtonPanel = new JPanel(new GridLayout(1, 3, 5, 5));
        setButtonPanel.add(addSetButton);
        setButtonPanel.add(editSetButton);
        setButtonPanel.add(removeSetButton);

        JPanel setManagementPanel = new JPanel(new BorderLayout());
        setManagementPanel.add(setTableScrollPane, BorderLayout.CENTER);
        setManagementPanel.add(setButtonPanel, BorderLayout.SOUTH);

        add(setManagementPanel, BorderLayout.CENTER);

        // Create a panel for duration management
        JPanel durationPanel = new JPanel(new BorderLayout());
        durationField = new JTextField(20);
        durationField.setEnabled(false); // Initially disabled
        //durationField.setFocusable(true);


        JPanel durationFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        durationFieldPanel.add(new JLabel("Duration:"));
        durationFieldPanel.add(durationField);

        durationPanel.add(durationFieldPanel, BorderLayout.NORTH);

        setManagementPanel.add(durationPanel, BorderLayout.NORTH);

        addListeners();
    }

    private void addListeners() {
        blockSelector.addActionListener(e -> loadSelectedBlock());
        addBlockButton.addActionListener(e -> addNewBlock());
        removeBlockButton.addActionListener(e -> removeSelectedBlock());
        addExerciseButton.addActionListener(e -> addExercise());
        removeExerciseButton.addActionListener(e -> removeSelectedExercise());
        addSetButton.addActionListener(e -> addSet());
        editSetButton.addActionListener(e -> editSet());
        removeSetButton.addActionListener(e -> removeSet());
//        blockNameField.getDocument().addDocumentListener(new WorkoutDocumentListener() {
//            @Override
//            public void update() {
//                if (currentBlock != null) {
//                    currentBlock.setBlockName(blockNameField.getText().trim());
//                    updateTree();
//                }
//            }
//        });

        exerciseList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedExercise();
            }
        });

        durationField.getDocument().addDocumentListener(new WorkoutDocumentListener() {
            @Override
            public void update() {
                saveExerciseDuration();
            }
        });

        blockSelector.addActionListener(e -> {
            if (blockSelector.isPopupVisible()) {
                blockSelector.hidePopup();
            }
        });


    }

    private void removeSet() {
        int selectedRow = setTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a set to remove.");
            return;
        }

        // Confirm removal
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to remove this set?",
                "Confirm Removal",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // Get the selected exercise
            int selectedExerciseIndex = exerciseList.getSelectedIndex();
            if (selectedExerciseIndex != -1 && currentBlock != null) {
                Exercise selectedExercise = currentBlock.getExercises().get(selectedExerciseIndex);

                // Remove the set from the exercise
                selectedExercise.getSets().remove(selectedRow);

                // Remove the row from the table
                setTableModel.removeRow(selectedRow);

                JOptionPane.showMessageDialog(this, "Set removed successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "No exercise selected.");
            }
        }
    }


    private void loadSelectedExercise() {
        int selectedIndex = exerciseList.getSelectedIndex();
        if (selectedIndex != -1 && currentBlock != null) {
            Exercise selectedExercise = currentBlock.getExercises().get(selectedIndex);

            // Populate the duration field if the block is "simple"
            if ("simple".equalsIgnoreCase(currentBlock.getType())) {
                durationField.setText(selectedExercise.getDuration());
            } else {
                durationField.setText(""); // Clear duration field for set-based blocks
            }

            // Populate the table with sets
            setTableModel.setRowCount(0); // Clear existing rows
            for (Set set : selectedExercise.getSets()) {
                setTableModel.addRow(new Object[]{
                        set.getPercent(),
                        set.getSource(),
                        set.getReps(),
                        set.getLoadPercent(),
                        set.isFailed(),
                        set.getWeight()
                });
            }
        } else {
            setTableModel.setRowCount(0); // Clear table if no exercise selected
            durationField.setText(""); // Clear duration field
        }
    }


    private void saveExerciseDuration() {
        if (currentBlock != null && "simple".equalsIgnoreCase(currentBlock.getType())) {
            int selectedIndex = exerciseList.getSelectedIndex();
            if (selectedIndex != -1) {
                Exercise selectedExercise = currentBlock.getExercises().get(selectedIndex);
                selectedExercise.setDuration(durationField.getText().trim());
                updateTree(); // Update the tree to reflect the changes
            }
        }
    }



    private void addSet() {
        if (currentBlock == null || exerciseList.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Please select an exercise to add a set.");
            return;
        }

        if ("simple".equalsIgnoreCase(currentBlock.getType())) {
            JOptionPane.showMessageDialog(this, "Sets cannot be added to a simple block.");
            return;
        }

        // Handle set-based blocks
        JTextField percentField = new JTextField();
        JTextField sourceField = new JTextField();
        JTextField repsField = new JTextField();
        JTextField loadPercentField = new JTextField();
        JTextField weightField = new JTextField();

        JPanel setInputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        setInputPanel.add(new JLabel("Percent:"));
        setInputPanel.add(percentField);
        setInputPanel.add(new JLabel("Source:"));
        setInputPanel.add(sourceField);
        setInputPanel.add(new JLabel("Reps:"));
        setInputPanel.add(repsField);
        setInputPanel.add(new JLabel("Load %:"));
        setInputPanel.add(loadPercentField);
        setInputPanel.add(new JLabel("Weight:"));
        setInputPanel.add(weightField);

        int result = JOptionPane.showConfirmDialog(this, setInputPanel, "Add New Set", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Set newSet = new Set();
            newSet.setPercent(percentField.getText());
            newSet.setSource(sourceField.getText());
            newSet.setReps(repsField.getText());
            newSet.setLoadPercent(loadPercentField.getText());
            newSet.setWeight(weightField.getText());

            Exercise selectedExercise = currentBlock.getExercises().get(exerciseList.getSelectedIndex());
            selectedExercise.getSets().add(newSet);

            // Update the table
            setTableModel.addRow(new Object[]{
                    newSet.getPercent(),
                    newSet.getSource(),
                    newSet.getReps(),
                    newSet.getLoadPercent(),
                    false, // Default "Failed" value
                    newSet.getWeight()
            });

            // Request focus on the "Add Set" button for quick adding
            addSetButton.requestFocusInWindow();
        }
    }



    private void editSet() {
        int selectedRow = setTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a set to edit.");
            return;
        }

        Exercise selectedExercise = currentBlock.getExercises().get(exerciseList.getSelectedIndex());
        Set selectedSet = selectedExercise.getSets().get(selectedRow);

        // Pre-fill inputs with existing values
        JTextField percentField = new JTextField(selectedSet.getPercent());
        JTextField sourceField = new JTextField(selectedSet.getSource());
        JTextField repsField = new JTextField(selectedSet.getReps());
        JTextField loadPercentField = new JTextField(selectedSet.getLoadPercent());
        JTextField weightField = new JTextField(selectedSet.getWeight());

        JPanel setInputPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        setInputPanel.add(new JLabel("Percent:"));
        setInputPanel.add(percentField);
        setInputPanel.add(new JLabel("Source:"));
        setInputPanel.add(sourceField);
        setInputPanel.add(new JLabel("Reps:"));
        setInputPanel.add(repsField);
        setInputPanel.add(new JLabel("Load %:"));
        setInputPanel.add(loadPercentField);
        setInputPanel.add(new JLabel("Weight:"));
        setInputPanel.add(weightField);

        int result = JOptionPane.showConfirmDialog(this, setInputPanel, "Edit Set", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            selectedSet.setPercent(percentField.getText());
            selectedSet.setSource(sourceField.getText());
            selectedSet.setReps(repsField.getText());
            selectedSet.setLoadPercent(loadPercentField.getText());
            selectedSet.setWeight(weightField.getText());

            // Update table
            setTableModel.setValueAt(selectedSet.getPercent(), selectedRow, 0);
            setTableModel.setValueAt(selectedSet.getSource(), selectedRow, 1);
            setTableModel.setValueAt(selectedSet.getReps(), selectedRow, 2);
            setTableModel.setValueAt(selectedSet.getLoadPercent(), selectedRow, 3);
            setTableModel.setValueAt(selectedSet.getWeight(), selectedRow, 4);
        }
    }



    private void renameBlock() {
        int selectedIndex = blockSelector.getSelectedIndex();
        if (selectedIndex == -1 || currentBlock == null) {
            JOptionPane.showMessageDialog(this, "No block selected to rename.");
            return;
        }

        String currentName = currentBlock.getBlockName();
        String newName = JOptionPane.showInputDialog(this, "Enter new block name:", currentName);

        if (newName != null && !newName.trim().isEmpty()) {
            // Update the block name in the data model
            currentBlock.setBlockName(newName);

            // Update the JComboBox item
            blockSelector.removeItemAt(selectedIndex);
            blockSelector.insertItemAt(newName, selectedIndex);
            blockSelector.setSelectedIndex(selectedIndex);

            // Update the tree to reflect changes
            updateTree();
        }
    }



    public void loadBlocks(List<Block> blockList) {
        blocks.clear();
        blockSelector.removeAllItems();

        blocks.addAll(blockList);
        for (Block block : blocks) {
            blockSelector.addItem(block.getBlockName());
        }

        if (!blocks.isEmpty()) {
            blockSelector.setSelectedIndex(0);
        }
    }

    private void addNewBlock() {
        String blockName = JOptionPane.showInputDialog(this, "Enter block name:");
        if (blockName != null && !blockName.trim().isEmpty()) {
            String[] types = {"set-based", "simple"};
            int typeChoice = JOptionPane.showOptionDialog(
                    this,
                    "Select block type:",
                    "Block Type",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    types,
                    types[0]
            );

            Block newBlock = new Block();
            newBlock.setBlockName(blockName);
            newBlock.setType(types[typeChoice]);
            newBlock.setExercises(new ArrayList<>());

            blocks.add(newBlock);
            blockSelector.addItem(blockName);
            blockSelector.setSelectedItem(blockName);

            currentBlock = newBlock;
            updateTree();
        }
    }

    public Block getCurrentBlock() {
        return currentBlock;
    }


    private void removeSelectedBlock() {
        if (currentBlock != null) {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to remove this block?",
                    "Confirm Block Removal",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                blocks.remove(currentBlock);
                blockSelector.removeItem(currentBlock.getBlockName());

                currentBlock = null;
                //blockNameField.setText("");
                exerciseListModel.clear();

                if (!blocks.isEmpty()) {
                    blockSelector.setSelectedIndex(0);
                }

                updateTree();
            }
        }
    }

    private void saveCurrentBlock() {
        if (currentBlock != null) {
            // Update block name and type from fields
            //currentBlock.setBlockName(blockNameField.getText().trim());
            currentBlock.setType((String) blockTypeSelector.getSelectedItem());

            // Update block selector if name changed
            blockSelector.removeItem(currentBlock.getBlockName());
            blockSelector.addItem(currentBlock.getBlockName());
            blockSelector.setSelectedItem(currentBlock.getBlockName());

            JOptionPane.showMessageDialog(this, "Block saved successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "No block to save.");
        }
    }

    private void loadSelectedBlock() {
        String selectedBlockName = (String) blockSelector.getSelectedItem();
        if (selectedBlockName != null) {
            currentBlock = blocks.stream()
                    .filter(block -> block.getBlockName().equals(selectedBlockName))
                    .findFirst()
                    .orElse(null);

            if (currentBlock != null) {
                //blockNameField.setText(currentBlock.getBlockName());
                exerciseListModel.clear();
                for (Exercise exercise : currentBlock.getExercises()) {
                    exerciseListModel.addElement(exercise.getName());
                }

                // Update the UI based on block type
                updateSetEditorUI(currentBlock.getType());

                // Enable or disable the duration field
                durationField.setEnabled("simple".equalsIgnoreCase(currentBlock.getType()));
            }
        }
    }


    private void updateSetEditorUI(String blockType) {
        if ("simple".equalsIgnoreCase(blockType)) {
            // Enable duration field and disable set table
            durationField.setEnabled(true);
            addSetButton.setEnabled(false);
            editSetButton.setEnabled(false);
            removeSetButton.setEnabled(false);
            setTable.setEnabled(false);
            durationField.setFocusable(true);
            addSetButton.setFocusable(false);

        } else if ("set-based".equalsIgnoreCase(blockType)) {
            // Disable duration field and enable set table
            durationField.setEnabled(false);
            addSetButton.setEnabled(true);
            editSetButton.setEnabled(true);
            removeSetButton.setEnabled(true);
            setTable.setEnabled(true);
            durationField.setFocusable(false);
            addSetButton.setFocusable(true);
        }
    }





    private void updateBlockDetails() {
        if (currentBlock != null) {
            //blockNameField.setText(currentBlock.getBlockName());
            blockTypeSelector.setSelectedItem(currentBlock.getType());
            exerciseListModel.clear();
            for (Exercise exercise : currentBlock.getExercises()) {
                exerciseListModel.addElement(exercise.getName());
            }
        }
    }

    private void addExercise() {
        if (currentBlock != null) {
            String exerciseName = JOptionPane.showInputDialog(this, "Enter exercise name:");
            if (exerciseName != null && !exerciseName.trim().isEmpty()) {
                Exercise newExercise = new Exercise();
                newExercise.setName(exerciseName);
                newExercise.setSets(new ArrayList<>());
                currentBlock.getExercises().add(newExercise);
                exerciseListModel.addElement(exerciseName);

                // Update the tree
                updateTree();

                // If the block is simple, place the cursor in the duration field
                if ("simple".equalsIgnoreCase(currentBlock.getType())) {
                    durationField.requestFocusInWindow();
                }
            }
        }
    }



    private void removeSelectedExercise() {
        int selectedIndex = exerciseList.getSelectedIndex();
        if (selectedIndex != -1 && currentBlock != null) {
            currentBlock.getExercises().remove(selectedIndex);
            exerciseListModel.remove(selectedIndex);
            updateTree();
        }
    }

    public void updateTree() {
        Workout currentWorkout = controller.getCurrentWorkout();
        if (currentWorkout != null) {
            currentWorkout.setBlocks(blocks);
            treePanel.updateTree(currentWorkout);
        }
    }

    public void resetUI() {
        //blockNameField.setText("");
        blockSelector.removeAllItems();
        exerciseListModel.clear();
        setTableModel.setRowCount(0);
        durationField.setText("");
        currentBlock = null;
        blocks.clear();
    }



    private void addSetToSelectedExercise() {
        int selectedIndex = exerciseList.getSelectedIndex();
        if (selectedIndex != -1 && currentBlock != null) {
            Exercise selectedExercise = currentBlock.getExercises().get(selectedIndex);

            // Check block type and create set accordingly
            if (currentBlock.getType().equals("simple")) {
                // Simple block: prompt for name and duration
                String name = JOptionPane.showInputDialog(this, "Enter set name:");
                String duration = JOptionPane.showInputDialog(this, "Enter duration:");

                Set newSet = new Set();
                newSet.setSource(name);
                newSet.setLoadPercent(duration);
                selectedExercise.getSets().add(newSet);
            } else {
                // Set-based block: prompt for all set attributes
                Set newSet = new Set();
                newSet.setPercent(JOptionPane.showInputDialog(this, "Enter percent:"));
                newSet.setSource(JOptionPane.showInputDialog(this, "Enter source:"));
                newSet.setReps(JOptionPane.showInputDialog(this, "Enter reps:"));
                newSet.setLoadPercent(JOptionPane.showInputDialog(this, "Enter load percent:"));

                // Failed set option
                int failedChoice = JOptionPane.showConfirmDialog(
                        this,
                        "Was this set failed?",
                        "Set Failed",
                        JOptionPane.YES_NO_OPTION
                );
                newSet.setFailed(failedChoice == JOptionPane.YES_OPTION);

                newSet.setWeight(JOptionPane.showInputDialog(this, "Enter weight:"));

                selectedExercise.getSets().add(newSet);
            }

            updateSetDetails(selectedExercise);
        } else {
            JOptionPane.showMessageDialog(this, "No exercise selected.");
        }
    }


    private void removeSetFromSelectedExercise() {
        int selectedIndex = exerciseList.getSelectedIndex();
        if (selectedIndex != -1 && currentBlock != null) {
            Exercise selectedExercise = currentBlock.getExercises().get(selectedIndex);
            if (!selectedExercise.getSets().isEmpty()) {
                selectedExercise.getSets().remove(selectedExercise.getSets().size() - 1); // Example: Remove last set
                updateSetDetails(selectedExercise);
            } else {
                JOptionPane.showMessageDialog(this, "No sets to remove.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No exercise selected.");
        }
    }

    private void moveBlock(int direction) {
        int selectedIndex = blockSelector.getSelectedIndex();

        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this, "No block selected.");
            return;
        }

        int newIndex = selectedIndex + direction;

        // Ensure the new index is within valid bounds
        if (newIndex < 0 || newIndex >= blocks.size()) {
            JOptionPane.showMessageDialog(this, "Cannot move block further.");
            return;
        }

        // Swap blocks in the list
        Block blockToMove = blocks.get(selectedIndex);
        blocks.remove(selectedIndex);
        blocks.add(newIndex, blockToMove);

        // Refresh the block selector
        updateBlockSelector();

        // Re-select the moved block
        blockSelector.setSelectedIndex(newIndex);

        // Update the tree to reflect the change
        updateTree();
    }

    private void updateBlockSelector() {
        blockSelector.removeAllItems();
        for (Block block : blocks) {
            blockSelector.addItem(block.getBlockName());
        }
    }




    private void updateSetDetails(Exercise exercise) {
        for (Set set : exercise.getSets()) {
            if (currentBlock.getType().equals("simple")) {
                setDetailArea.append("Name: " + set.getSource() +
                        ", Duration: " + set.getLoadPercent() + "\n");
            } else {
                setDetailArea.append(
                        "Percent: " + set.getPercent() +
                                ", Source: " + set.getSource() +
                                ", Reps: " + set.getReps() +
                                ", Load %: " + set.getLoadPercent() +
                                ", Failed: " + set.isFailed() +
                                ", Weight: " + set.getWeight() + "\n"
                );
            }
        }
    }

    private void renameExercise(int index) {
        String currentName = exerciseListModel.getElementAt(index);
        String newName = JOptionPane.showInputDialog(this, "Enter new name:", currentName);

        if (newName != null && !newName.trim().isEmpty()) {
            // Update the exercise name in the list model
            exerciseListModel.setElementAt(newName, index);

            // Update the exercise name in the current block
            if (currentBlock != null) {
                currentBlock.getExercises().get(index).setName(newName);
            }

            // Update the tree to reflect changes
            updateTree();
        }
    }


}
