/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.manchester.fishlink.csv_convertor;

import java.awt.Color;
import javax.swing.JButton;

/**
 *
 * @author Christian
 */
public class StatusChecker extends JButton{

    public final String FIND_DATA_PROPERTY = "Find Data Property";
    public final String STATUS_GREEN = "Status Green";

    private DataPropertyPane dataPropertyPane;

    public StatusChecker(){
        super("Load a datset!");
        setEnabled(false);
        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
    }

    public void redStatus(String detail){
        setText(FIND_DATA_PROPERTY);
        setActionCommand(FIND_DATA_PROPERTY);
        this.setToolTipText(detail);
        this.setEnabled(true);
        this.setBackground(Color.RED);
        this.setForeground(Color.BLACK);
    }

    public void greenStatus(){
        setText(STATUS_GREEN);
        this.setEnabled(false);
        this.setToolTipText("");
        this.setBackground(Color.GREEN);
        this.setForeground(Color.BLACK);
    }

    /*
    public boolean checkNamesLegal(String[] names){
        return checkNamesLegal( names, null);
    }

    public boolean checkNamesLegal(String[] names, Container parent){
        for (int i = 0; i< names.length; i++){
            if (!names[i].matches("[a-zA-Z][\\w]*")) {
                blackStatus("Please fix Illegal name: "+names[i], parent);
                return false;
            }
        }
        checkNamesInDataProperty(names);
        return true;
    }

    public String[] makeNamesLegal(String[] names){
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
        checkNamesInDataProperty(names);
        return names;
    }

    public boolean checkNamesInDataProperty(String[] names){
        return checkNamesInDataProperty(names, null);
    }

    public boolean checkNamesInDataProperty(String[] names, Container parent){
        for (int i = 0; i< names.length; i++){
            if (!dataPropertyPane.checkName(names[i])){
                setText(FIND_DATA_PROPERTY);
                setActionCommand(FIND_DATA_PROPERTY);
                this.setToolTipText("Name: " + names[i] + " not found in vocabulary");
                this.setEnabled(true);
                this.setBackground(Color.RED);
                this.setForeground(Color.BLACK);
                if (parent != null) {
                    JOptionPane.showMessageDialog(parent, "Name: " + names[i] + " not found in vocabulary", "Illegal Name", JOptionPane.ERROR_MESSAGE);
                }
                return false;
            }
        }
        setText(STATUS_GREEN);
        this.setEnabled(false);
        this.setToolTipText("");
        this.setBackground(Color.GREEN);
        this.setForeground(Color.BLACK);
        return true;
    }
*/
}
