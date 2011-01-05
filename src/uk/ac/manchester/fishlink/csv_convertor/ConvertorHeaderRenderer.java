/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.manchester.fishlink.csv_convertor;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Christian
 */
public class ConvertorHeaderRenderer extends JLabel implements TableCellRenderer {
    // This method is called each time a column header
    // using this renderer needs to be rendered.
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {
        // 'value' is column header value of column 'vColIndex'
        // rowIndex is always -1
        // isSelected is always false
        // hasFocus is always false

        CSV_Model model = (CSV_Model)table.getModel();

        // Configure the component with the specified value
        setText(value.toString());
        this.setForeground(Color.red);

        // Set tool tip if desired
        setToolTipText("HERE");

        // Since the renderer is a component, return itself
        return this;
    }



}
