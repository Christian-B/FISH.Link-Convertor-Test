/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.manchester.fishlink.csv_convertor;

import java.awt.Color;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

/**
 *
 * @author Christian
 */
public class InfoPanel extends JPanel{

    // private CSV_Model model;
    public final static String NO_COLUMN_SELECTED = "No column selected";

    public final static String APPLY_LEAF = "Apply Data Property";

    public final static String APPLY_LOW_DETAIL = "Apply general Data Property";

    public final static String PICK_DATA = "Pick a Data Property";

    private JLabel originalNameLabel;

    private JLabel newNameLabel;

    private JLabel columnType;
    
    private JLabel rangeLabel;

    private boolean columnSelected = false;
    private boolean dataPropertySelected = false;

    private JButton apply;

    private JLabel dataPropertyNameLabel;
    //private int selectedColumn = -1;

    public InfoPanel (JButton applyButton){
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        //Column Panel
        JPanel outerPanel = new JPanel();
        outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.Y_AXIS));
        outerPanel.setBorder(new LineBorder(Color.BLACK,3));
        this.add(outerPanel);

        //Original Name
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new LineBorder(Color.BLACK));
        JLabel label = new JLabel("Original Name");
        panel.add(label);
        originalNameLabel = new JLabel(NO_COLUMN_SELECTED);
        panel.add(originalNameLabel);
        outerPanel.add(panel);

        //New Name
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new LineBorder(Color.BLACK));
        label = new JLabel("New Name");
        panel.add(label);
        newNameLabel = new JLabel(NO_COLUMN_SELECTED);
        panel.add(newNameLabel);
        outerPanel.add(panel);

        //Column Type
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new LineBorder(Color.BLACK));
        label = new JLabel("Column Type");
        panel.add(label);
        columnType = new JLabel(NO_COLUMN_SELECTED);
        panel.add(columnType);
        outerPanel.add(panel);

        apply = applyButton;
        apply.setText(PICK_DATA);
        apply.setEnabled(false);
        this.add(apply);

        outerPanel = new JPanel();
        outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.Y_AXIS));
        outerPanel.setBorder(new LineBorder(Color.BLACK,3));
        this.add(outerPanel);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new LineBorder(Color.BLACK));
        label = new JLabel("Property Name");
        panel.add(label);
        dataPropertyNameLabel = new JLabel("No property Selected");
        panel.add(dataPropertyNameLabel);
        outerPanel.add(panel);


        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new LineBorder(Color.BLACK));
        label = new JLabel("Data Range");
        panel.add(label);
        rangeLabel = new JLabel("No property Selected");
        panel.add(rangeLabel);
        outerPanel.add(panel);
    }
    
    //public void setSelectedColumn(int column){
    //    if (selectedColumn != column) {
    //        selectedColumn = column;
    //        originalNameLabel.setText(model.getColumnName(column));
    //        newNameLabel.setText(model.getColumnName(column));
    //    }
    //}

    public void updateColumn(String originalName, String newName, String type){
        originalNameLabel.setText(originalName);
        newNameLabel.setText(newName);
        columnType.setText(type);
        columnSelected = true;
        apply.setEnabled(dataPropertySelected);
    }

    public void updateColumn(String originalName, DataPropertyTreeNode node){
        //ystem.out.println("**** "+ dataProperty);
        originalNameLabel.setText(originalName);
        newNameLabel.setText(node.getName());
        columnSelected = true;
        apply.setEnabled(dataPropertySelected);
    }

    public void clearColumn(){
        originalNameLabel.setText(NO_COLUMN_SELECTED);
        newNameLabel.setText(NO_COLUMN_SELECTED);
        columnSelected = false;
        apply.setEnabled(false);
    }

    public void updateDataProperty(DataPropertyTreeNode node){
        dataPropertyNameLabel.setText(node.getName());
        ConvertorType type = new ConvertorType(node.getRanges());
        rangeLabel.setText(type.toString());
        DataPropertyType dataPropertyType = node.getDataPropertyType();
        switch (dataPropertyType){
            case LEAF:
                apply.setText(APPLY_LEAF);
                apply.setEnabled(columnSelected);
                apply.setActionCommand(APPLY_LEAF);
                dataPropertySelected = true;
                break;
            case LOW_DETAIL:
                apply.setText(APPLY_LOW_DETAIL);
                apply.setEnabled(columnSelected);
                apply.setActionCommand(APPLY_LOW_DETAIL);
                dataPropertySelected = true;
                break;
            case PROPERTIES:
                apply.setText(PICK_DATA);
                apply.setEnabled(columnSelected);
                dataPropertySelected = false;
                break;
            default:
                System.err.println("Unexpected enum in updateDataProperty");
        }
     }

    //public void setOntology (OWLOntology ontology){
    //    this.ontology =  ontology;
    //}
}
