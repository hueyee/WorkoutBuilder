import javax.swing.*;
import java.awt.datatransfer.*;
import java.util.List;

class ExerciseTransferHandler extends TransferHandler {
    private final BlockDetailPanel blockDetailPanel;
    private int fromIndex = -1;

    public ExerciseTransferHandler(BlockDetailPanel blockDetailPanel) {
        this.blockDetailPanel = blockDetailPanel;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return MOVE; // Allow moving items
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        if (c instanceof JList) {
            @SuppressWarnings("unchecked")
            JList<String> list = (JList<String>) c;
            fromIndex = list.getSelectedIndex();
            if (fromIndex != -1) {
                return new StringSelection(list.getSelectedValue());
            }
        }
        return null;
    }

    @Override
    public boolean canImport(TransferSupport support) {
        return support.getComponent() instanceof JList && support.isDataFlavorSupported(DataFlavor.stringFlavor);
    }

    @Override
    public boolean importData(TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }

        JList.DropLocation dropLocation = (JList.DropLocation) support.getDropLocation();
        int toIndex = dropLocation.getIndex();

        if (toIndex == fromIndex || fromIndex == -1) {
            return false;
        }

        try {
            String data = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);

            @SuppressWarnings("unchecked")
            JList<String> list = (JList<String>) support.getComponent();
            DefaultListModel<String> model = (DefaultListModel<String>) list.getModel();

            // Move the item in the list model
            model.remove(fromIndex);
            model.add(toIndex, data);

            // Update the corresponding exercise order in the current block
            Exercise exercise = blockDetailPanel.getCurrentBlock().getExercises().remove(fromIndex);
            blockDetailPanel.getCurrentBlock().getExercises().add(toIndex, exercise);

            // Update the tree to reflect the changes
            blockDetailPanel.updateTree();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
