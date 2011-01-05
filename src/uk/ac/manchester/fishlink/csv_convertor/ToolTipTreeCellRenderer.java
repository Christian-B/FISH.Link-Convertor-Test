/*
 * 
 */

package uk.ac.manchester.fishlink.csv_convertor;

import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

/**
 *
 * @author Christian
 */
public class ToolTipTreeCellRenderer implements TreeCellRenderer{

    DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
            boolean leaf, int row, boolean hasFocus) {
        renderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        if (value != null) {
            if (value instanceof DataPropertyTreeNode){
                DataPropertyTreeNode node = (DataPropertyTreeNode)value;
                renderer.setToolTipText(node.getDescription());
            } else {
                renderer.setToolTipText(value.getClass().toString());
            }
        } else {
            renderer.setToolTipText("null");
        }
        return renderer;
    }

}
