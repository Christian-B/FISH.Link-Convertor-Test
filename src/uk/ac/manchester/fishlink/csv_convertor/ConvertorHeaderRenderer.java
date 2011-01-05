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
import uk.ac.manchester.fishlink.csv_convertor.DataPropertyType;

/**
 *
 * @author Christian
 */
public class ConvertorHeaderRenderer extends JLabel implements TableCellRenderer {
    // This method is called each time a column header
    // using this renderer needs to be rendered.
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {
        setText(value.toString());

        CSV_Model model = (CSV_Model)table.getModel();
        DataPropertyType type = model.getDataPropertyType(vColIndex);
        switch (type){
            case NULL:
                this.setForeground(Color.RED);
                setToolTipText("No matching data property");
                break;
            case LEAF:
                this.setForeground(Color.GREEN);
                setToolTipText("Column matched with a data property");
                break;
            case LOW_DETAIL:
                this.setForeground(Color.YELLOW);
                setToolTipText("Matching data property not at lowest possible level of detail");
                break;
            case PROPERTIES:
                this.setForeground(Color.RED);
                setToolTipText("Name matches a data properties group");
                break;
            default:
                System.err.println("unexpected type found : "+type);
                int error = 1/0;
        }
         // Set tool tip if desired
 
        // Since the renderer is a component, return itself
        return this;
    }



}
