package uk.ac.manchester.fishlink.csv_convertor;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import org.semanticweb.owlapi.model.OWLDataProperty;

/**
 *
 * @author Christian
 */
public class ActionManager implements TreeSelectionListener, ActionListener, TableColumnModelListener{

    private Container container;

    private CSV_Model model;

    private DataPropertyPane dataPropertyPane;

    private InfoPanel infoPanel;

    private StatusChecker statusChecker;

    private DataPropertyTreeNode node;

    private String[] originalNames = new String[0];

    private int selectedColumn = -1;

    File csvFile;

    File ontologyFile;

    public ActionManager(Container container, CSV_Model model,  DataPropertyPane dataPropertyPane, InfoPanel infoPanel,
                         StatusChecker statusChecker)
    {
        this.container = container;
        this.model = model;
        this.dataPropertyPane = dataPropertyPane;
        this.infoPanel = infoPanel;
        this.statusChecker = statusChecker;
        this.statusChecker.addActionListener(this);
    }

    public void valueChanged(TreeSelectionEvent e) {
        //out.println(e);
        JTree tree = (JTree)e.getSource();
        DataPropertyTreeNode node = (DataPropertyTreeNode)tree.getLastSelectedPathComponent();

        infoPanel.updateDataProperty(node);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals(InfoPanel.APPLY_LEAF)){
           applyDataProperty ();
        } else if(command.equals(InfoPanel.APPLY_LOW_DETAIL)) {
           applyLowDetailProperty ();
        } else if (command.equals("Save to CSV")) {
            model.writeCSV();
        } else if (command.equals("Load from CSV")) {
            csvFile = model.loadFile((JButton)e.getSource(), this);
        } else if (command.equals("Save conversion")) {
            System.out.println("Save conversion");
        } else{
            System.err.println(e);
        }
    }

    public void columnAdded(TableColumnModelEvent e) {
        //No addition work needed yet!
    }

    public void columnRemoved(TableColumnModelEvent e) {
        //No addition work needed yet!
    }

    public void columnMoved(TableColumnModelEvent e) {
        //No addition work needed yet!
    }

    public void columnMarginChanged(ChangeEvent e) {
        //No addition work needed yet!
    }

    public void columnSelectionChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            int column = e.getFirstIndex();
            selectedColumn = column;
            infoPanel.updateColumn(originalNames[column], model.getColumnName(column),
                    model.getColumnType(column).toString());
        }
    }

    public final void setOriginalNames (String[] names){
        originalNames = names;
        String[] columnNames = names.clone();
        makeNamesLegal(false);
    }

    public void setSelectedColumn(int column){
        if (selectedColumn != column) {
            //ystem.out.println("OK");
            if (column >= 0){
                selectedColumn = column;
                ConvertorType type = model.getColumnType(column);
                //if (dataProperties[selectedColumn] != null){
                    infoPanel.updateColumn(originalNames[column], model.getColumnName(column), type.toString());
                //} else {
                //    infoPanel.updateColumn(originalNames[column], columnNames[column], type.toString());
                //}
            } else {
                infoPanel.clearColumn();
            }
        //} else {
            //ystem.out.println("Same");
        }
    }

    private void applyDataProperty(){
        model.setColumnNode(selectedColumn, node);
        infoPanel.updateColumn(originalNames[selectedColumn], node);
    }

    private void applyLowDetailProperty(){
        int answer = JOptionPane.showConfirmDialog(container, "Are you sure you aren't able to pick a lower detail property.",
                "Confirm Low Detail", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (answer == JOptionPane.YES_OPTION){
            applyDataProperty();
        }
    }

    public void makeNamesLegal( boolean interactive){
        for (int column = 0; column < model.getColumnCount(); column++){
            String temp = model.getColumnName(column);
           //System.out.println(temp);
            temp = temp.replace("+","Plus");
            temp = temp.replace("-","Minus");
            temp = temp.replaceAll("[^\\w]", "_");
            boolean duplicate = false;
            int num = 0;
            String check = temp;
            do{
                for (int j = 0; j < column-1; j++){
                    if (check.equalsIgnoreCase(model.getColumnName(j))){
                        duplicate = true;
                    }
                }
                if (duplicate){
                    num = num + 1;
                    check = temp + num;
                }
            } while (duplicate);
            model.setColumnName(column, check);
        }
        checkNamesInDataProperty(interactive);
    }

    public boolean checkNamesInDataProperty(boolean interactive){
        boolean noError = true;
        for (int column = 0; column < model.getColumnCount(); column++){
            DataPropertyTreeNode byName = dataPropertyPane.checkName(model.getColumnName(column));
            if (byName == null){
                if (noError){
                     statusChecker.redStatus("Name: " + model.getColumnName(column) + " not found in vocabulary");
                     if (interactive) {
                         JOptionPane.showMessageDialog(container, "Name: " + model.getColumnName(column) + " not found in vocabulary", "Illegal Name", JOptionPane.ERROR_MESSAGE);
                     }
                     noError = false;
                 }
             } else {
                 model.setColumnNode(column, byName);
            }
        }
        if (noError){
            statusChecker.greenStatus();
        }
        return noError;
    }

    public void loadDefaults(){
        ontologyFile = new File("file:d:/Protege/Ontologies/vocab.owl");
        dataPropertyPane.replaceOntology(ontologyFile, this);

        try {
            csvFile = new File("c:/dropbox/FISH.Link_code/tarns/TarnschemFinal.csv");
            model.setCSV_File(csvFile, this);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
