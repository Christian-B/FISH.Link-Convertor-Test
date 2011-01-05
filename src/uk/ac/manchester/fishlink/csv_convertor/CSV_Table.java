/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.manchester.fishlink.csv_convertor;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;

/**
 *
 * @author Christian
 */
public class CSV_Table extends JTable  {

    private InfoPanel columnPanel;

    public CSV_Table (CSV_Model model, InfoPanel columnPanel){
        super(model);
        System.out.println(model);
        this.columnPanel = columnPanel;
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setRowSelectionAllowed(false);
        this.setColumnSelectionAllowed(true);
        this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        getTableHeader().setReorderingAllowed(false);

        model.setCSV_Table(this);
    }

    //public void valueChanged(ListSelectionEvent e) {
    //}

    @Override
    public void columnSelectionChanged(ListSelectionEvent e){
        super.columnSelectionChanged(e);
    }

//    public void tableChanged(TableModelEvent e){
//        System.out.println(e);
//        ConvertorHeaderRenderer renderer = new ConvertorHeaderRenderer();
//        CSV_Model model = (CSV_Model)getModel();
//        super.tableChanged(e);
//        for (int i = 0 ; i < model.getColumnCount(); i++){
//            TableColumn col = getColumnModel().getColumn(i);
//            col.setHeaderRenderer(renderer);
//        }
//       super.tableChanged(e);
//    }
}
