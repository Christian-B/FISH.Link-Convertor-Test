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

    private OWLDataProperty[] dataProperties;

    private DataPropertyTreeNode node;

    private String fileName = "c:\\dropbox\\FISH.Link_code\\Tarns\\TarnschemFinal.csv";

    private String[] originalNames = new String[0];

    private int selectedColumn = -1;

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
            fileName = model.loadFile((JButton)e.getSource(), this);
        } else if (command.equals("Save conversion")) {
            System.out.println("Save conversion");
        } else if (command.equals(statusChecker.MAKE_NAMES_LEGAL)) {
            model.setColumnNames(makeNamesLegal(model.getColumnNames(), false));
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
        String[] columnNames = makeNamesLegal(names.clone(), false);
        dataProperties = new OWLDataProperty[names.length];
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
        model.setColumnName(selectedColumn, node.getName());
        dataProperties[selectedColumn] = node.getUserObject();
        checkNamesLegal(model.getColumnNames(), false);
        infoPanel.updateColumn(originalNames[selectedColumn], node);
    }

    private void applyLowDetailProperty(){
        int answer = JOptionPane.showConfirmDialog(container, "Are you sure you aren't able to pick a lower detail property.",
                "Confirm Low Detail", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (answer == JOptionPane.YES_OPTION){
            applyDataProperty();
        }
    }

    public boolean checkNamesLegal(String[] names, boolean interactive){
        for (int i = 0; i< names.length; i++){
            if (!names[i].matches("[a-zA-Z][\\w]*")) {
                statusChecker.blackStatus("Please fix Illegal name: "+names[i]);
                return false;
            }
        }
        checkNamesInDataProperty(names, interactive);
        return true;
    }

    public String[] makeNamesLegal(String[] names,  boolean interactive){
        for (int i = 0; i< names.length; i++){
            String temp = names[i];
           //System.out.println(temp);
            temp = temp.replace("+","Plus");
            temp = temp.replace("-","Minus");
            temp = temp.replaceAll("[^\\w]", "_");
            boolean duplicate = false;
            int num = 0;
            String check = temp;
            do{
                for (int j = 0; j < i-1; j++){
                    if (check.equalsIgnoreCase(names[j])){
                        duplicate = true;
                    }
                }
                if (duplicate){
                    num = num + 1;
                    check = temp + num;
                }
            } while (duplicate);
            names[i] = check;
            //ystem.out.println(names[i]);
        }
        checkNamesInDataProperty(names, interactive);
        return names;
    }

    public boolean checkNamesInDataProperty(String[] names, boolean interactive){
        for (int i = 0; i< names.length; i++){
            if (!dataPropertyPane.checkName(names[i])){
                statusChecker.redStatus("Name: " + names[i] + " not found in vocabulary");
                if (interactive) {
                    JOptionPane.showMessageDialog(container, "Name: " + names[i] + " not found in vocabulary", "Illegal Name", JOptionPane.ERROR_MESSAGE);
                }
                return false;
            }
        }
        statusChecker.greenStatus();
        return true;
    }

    public void loadDefaults(){
        dataPropertyPane.replaceOntology(new File("file:d:/Protege/Ontologies/vocab.owl"), this);
        try {
            model.setCSV_File(new File("c:/dropbox/FISH.Link_code/tarns/TarnschemFinal.csv"), this);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
