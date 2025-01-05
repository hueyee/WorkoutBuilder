import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

public class WorkoutTreePanel extends JPanel {
    private JTree workoutTree;
    private DefaultMutableTreeNode root;

    public WorkoutTreePanel(WorkoutEditorController controller) {
        setLayout(new BorderLayout());
        root = new DefaultMutableTreeNode("Workouts");
        workoutTree = new JTree(root);

        // Set preferred size to ensure the tree has enough space
        setPreferredSize(new Dimension(300, 800)); // Adjust width and height as needed

        workoutTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) workoutTree.getLastSelectedPathComponent();
            if (selectedNode == null || controller.getCurrentWorkout() == null) return;

            String selectedName = selectedNode.toString();
            Workout workout = controller.getCurrentWorkout();

            if (selectedNode.isRoot()) {
                // Root node selected: Show workout details
                controller.setCurrentWorkout(workout);
            } else if (selectedNode.getParent() == root) {
                // Block node selected
                Block selectedBlock = workout.getBlocks().stream()
                        .filter(block -> block.getBlockName().equals(selectedName))
                        .findFirst()
                        .orElse(null);
                // Handle block-specific details here
            } else if (selectedNode.getParent().getParent() == root) {
                // Exercise node selected
                for (Block block : workout.getBlocks()) {
                    Exercise selectedExercise = block.getExercises().stream()
                            .filter(exercise -> exercise.getName().equals(selectedName))
                            .findFirst()
                            .orElse(null);
                    if (selectedExercise != null) {
                        // Handle exercise-specific details here
                        break;
                    }
                }
            } else if (selectedNode.getParent().getParent().getParent() == root) {
                // Set node selected
                for (Block block : workout.getBlocks()) {
                    for (Exercise exercise : block.getExercises()) {
                        Set selectedSet = exercise.getSets().stream()
                                .filter(set -> formatSetForTree(set).equals(selectedName))
                                .findFirst()
                                .orElse(null);
                        if (selectedSet != null) {
                            // Handle set-specific details here
                            break;
                        }
                    }
                }
            }
        });

        add(new JScrollPane(workoutTree), BorderLayout.CENTER);
    }


    public void updateTree(Workout workout) {
        root.removeAllChildren(); // Clear the tree

        DefaultMutableTreeNode workoutNode = new DefaultMutableTreeNode(workout.getName());
        root.add(workoutNode);

        for (Block block : workout.getBlocks()) {
            DefaultMutableTreeNode blockNode = new DefaultMutableTreeNode(block.getBlockName());
            workoutNode.add(blockNode);

            for (Exercise exercise : block.getExercises()) {
                DefaultMutableTreeNode exerciseNode = new DefaultMutableTreeNode(exercise.getName());
                blockNode.add(exerciseNode);

                for (Set set : exercise.getSets()) {
                    DefaultMutableTreeNode setNode = new DefaultMutableTreeNode(formatSetForTree(set));
                    exerciseNode.add(setNode);
                }
            }
        }

        ((DefaultTreeModel) workoutTree.getModel()).reload();
        expandAllNodes(workoutTree, 0); // Expand all nodes after updating the tree
    }


    // Helper method to format a Set for display in the tree
    private String formatSetForTree(Set set) {
        return String.format("Set [Percent: %s, Source: %s, Reps: %s, Load%%: %s, Failed: %b, Weight: %s]",
                set.getPercent(), set.getSource(), set.getReps(), set.getLoadPercent(), set.isFailed(), set.getWeight());
    }

    private void expandAllNodes(JTree tree, int startingRow) {
        int rowCount = tree.getRowCount();
        for (int i = startingRow; i < rowCount; i++) {
            tree.expandRow(i);
        }

        // If new rows were added due to expansion, repeat
        if (tree.getRowCount() != rowCount) {
            expandAllNodes(tree, rowCount);
        }
    }


}
