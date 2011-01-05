/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.manchester.fishlink.csv_convertor;


import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;

/**
 *
 * @author Christian
 */
public class MainFrame extends JFrame {

    private CSV_Table table;

    private int selectedColumn;

    private void stop(){
       System.out.println("Good bye");
       System.exit(0);
    }

    public MainFrame(){
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                stop();
            }
         });
    
         //WEST
        JButton applyButton = new JButton();
        InfoPanel infoPanel = new InfoPanel(applyButton);
        JScrollPane columnPane = new JScrollPane(infoPanel);
        this.getContentPane().add(columnPane, BorderLayout.WEST);

        //CENTRE
        CSV_Model model = new CSV_Model();
        table = new CSV_Table(model, infoPanel);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        this.getContentPane().add(scrollPane, BorderLayout.CENTER);

        //NORTH
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.X_AXIS));

        JButton loadConversion = new JButton("Load Conversion");
        loadConversion.setActionCommand("Load Conversion");
        northPanel.add(loadConversion);

        JButton loadFromCSV = new JButton("Load from CSV");
        loadFromCSV.setActionCommand("Load from CSV");
        northPanel.add(loadFromCSV);

        StatusChecker statusChecker = new StatusChecker();
        northPanel.add(statusChecker);
        this.getContentPane().add(northPanel, BorderLayout.NORTH);

        //SOUTH
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.X_AXIS));
        this.getContentPane().add(southPanel, BorderLayout.SOUTH);

        JButton saveToCSV = new JButton("Save to CSV");
        saveToCSV.setActionCommand("Save to CSV");
        southPanel.add(saveToCSV);

        JButton saveConversion = new JButton("Save conversion");
        saveConversion.setActionCommand("Save conversion");
        southPanel.add(saveConversion);

        DataPropertyPane dataPropertyPane =  new DataPropertyPane();
        this.getContentPane().add(dataPropertyPane, BorderLayout.EAST);

        ActionManager actionManager = new ActionManager(this, model, dataPropertyPane, infoPanel, statusChecker, loadConversion);
        applyButton.addActionListener(actionManager);
        table.getColumnModel().addColumnModelListener(actionManager);
        loadConversion.addActionListener(actionManager);
        loadFromCSV.addActionListener(actionManager);
        statusChecker.addActionListener(actionManager);
        saveToCSV.addActionListener(actionManager);
        saveConversion.addActionListener(actionManager);
        //dataPropertyPane handled by replaceOntology and its Children.
        actionManager.loadDefaults();
        this.pack();

    }

    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            int column = table.getSelectedColumn();
            //ystem.out.println(column);
        }
    }

}
