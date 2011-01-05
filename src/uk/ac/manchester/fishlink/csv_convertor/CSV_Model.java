/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.manchester.fishlink.csv_convertor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author Christian
 */
public class CSV_Model extends AbstractTableModel {

    private String[] columnNames = new String[0];

    private ArrayList<String[]> data = new ArrayList<String[]>(0);

    private DataPropertyTreeNode[] dataProperties;

    private CSV_Table table;

    public CSV_Model(){
        super();
    }

    public int getRowCount() {
        return data.size();
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
       return data.get(rowIndex)[columnIndex];
    }

    @Override
    public String getColumnName(int column){
        return columnNames[column];
    }

    public String[] getColumnNames(){
        return columnNames;
    }

    public void setColumnName(int column, String name){
         if (columnNames[column].equals(name)){
            columnNames[column] = name;
            table.getColumnModel().getColumn(column).setHeaderValue(columnNames[column]);
            table.getTableHeader().repaint();
        }
    }

    public void setColumnNode(int column, DataPropertyTreeNode node){
        dataProperties[column] = node;
        setColumnName(column, node.getName());
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    protected final void setCSV_Table (CSV_Table table){
        this.table = table;
    }

    protected final void setData(ArrayList<String[]> data){
        this.data = data;
        fireTableStructureChanged();
        ConvertorHeaderRenderer renderer = new ConvertorHeaderRenderer();
        for (int i = 0 ; i < getColumnCount(); i++){
            TableColumn col = table.getColumnModel().getColumn(i);
            col.setHeaderRenderer(renderer);
        }
        //fireTableStructureChanged();
    }

    public ConvertorType getColumnType(int column){
        return new ConvertorType(data, column);
    }

    public DataPropertyType getDataPropertyType(int column){
        if (dataProperties[column] == null){
            return DataPropertyType.NULL;
        }
        return dataProperties[column].getDataPropertyType();
    }

    public void setColumnNames (String[] names){
        columnNames = names;
        for (int i = 0; i < names.length; i++){
            table.getColumnModel().getColumn(i).setHeaderValue(columnNames[i]);
        }
        table.getTableHeader().repaint();
    }

    private File chooseFile(){
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
        chooser.setFileFilter(filter);
        chooser.setAcceptAllFileFilterUsed(false);
        int returnVal = chooser.showSaveDialog(table);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File temp = chooser.getSelectedFile();
            if (temp.exists()) {
                int response = JOptionPane.showConfirmDialog(table, " File " + temp.getName() + " already exists. Ok to overwrite ", "Confirm Overwrite",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (response == JOptionPane.CANCEL_OPTION) {
                    return null;
                }
            }
            return temp;
        }
        return null;
    }

    private void writeString(BufferedWriter buffer, String text) throws IOException{
        if (text.contains(",") || text.contains("\"")){
            buffer.write("\"");
            buffer.write(text);
            buffer.write("\"");
        } else
        {
            buffer.write(text);
        }
    }

    private void writeToCSV(File file){
        try {
            FileWriter writer = new FileWriter(file);
            BufferedWriter buffer = new BufferedWriter (writer);
            writeString(buffer,columnNames[0]);
            for (int i = 1; i < columnNames.length; i++){
                buffer.write(",");
                writeString(buffer,columnNames[i]);
            }
            buffer.newLine();
            Iterator<String[]> lines = data.iterator();
            while (lines.hasNext()){
                String[] line = lines.next();
                writeString(buffer,line[0]);
                for (int i = 1; i < columnNames.length; i++){
                    buffer.write(",");
                    writeString(buffer,line[i]);
                }
                buffer.newLine();
            }
            buffer.close();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(table, ex, "Save Error", JOptionPane.ERROR_MESSAGE);
        }
   }

    public void writeCSV (){
        File file = chooseFile();
        writeToCSV(file);
        System.out.println("Writen to "+file);
    }

    private String cleanup(String text){
        String newText = text.trim();
        if(newText.startsWith("\"") && newText.endsWith("\"")){
            newText = newText.substring(1, newText.length()-1);
        }
        return newText;
    }

    private String[] splitter(String text, int columns) throws CSV_Exception{
        //We know there will be columns tokens
        String[] result = new String[columns];
        int pos = 0;
        boolean insideQuote = false;
        int start = 0;
        int end = -1;
        for (int i = 0; i< text.length(); i++){
            if (insideQuote){
                switch (text.charAt(i)){
                    case '\"':
                        insideQuote = false;
                        //ystem.out.println(i+ " quote off");
                        break;
                    case '\\':
                        //Ignore the next character
                        //ystem.out.println(i+ " ignore next");
                        i = i+1;
                        break;
                }
            } else {
                switch (text.charAt(i)){
                    case ',':
                        result[pos] = cleanup(text.substring(start, i));
                        pos++;
                        if (pos == columns){
                            throw new CSV_Exception ("Found row with two many columns: "+ text);
                        }
                        start = i + 1;
                        break;
                    case '\"':
                        insideQuote = true;
                        //ystem.out.println(i+ " quote on");
                        break;
                    //default: //ystem.out.println(i+": "+ text.charAt(i));
                }
            }
        }
       if (pos < columns - 1){
          throw new CSV_Exception ("Found row with two few columns");
       }
       result[pos] = cleanup(text.substring(start, text.length()));
       return result;
    }

     private String[] headerSplitter(String text) {
        //We know there will be columns tokens
        ArrayList<String> result = new ArrayList<String>();
        int pos = 0;
        boolean insideQuote = false;
        int start = 0;
        int end = -1;
        for (int i = 0; i< text.length(); i++){
            if (insideQuote){
                switch (text.charAt(i)){
                    case '\'':
                        insideQuote = false;
                        //ystem.out.println(i+ " quote off");
                        break;
                    case '\\':
                        //Ignore the next character
                        //ystem.out.println(i+ " ignore next");
                        i = i+1;
                        break;
                }
            } else {
                switch (text.charAt(i)){
                    case ',':
                        result.add(cleanup(text.substring(start, i)));
                        start = i + 1;
                        break;
                    case '\'':
                        insideQuote = true;
                        //ystem.out.println(i+ " quote on");
                        break;
                    //default: //ystem.out.println(i+": "+ text.charAt(i));
                }
            }
        }
       result.add(cleanup(text.substring(start, text.length())));
       return result.toArray(new String[0]);
    }

   public void setCSV_File(File file, ActionManager actionManager) throws FileNotFoundException, IOException{
       FileReader fileReader = new FileReader(file);
       BufferedReader bufferedReader = new BufferedReader(fileReader);
       String line = bufferedReader.readLine();
       columnNames = headerSplitter(line);
       ArrayList<String[]> data =  new ArrayList<String[]>(0);
       line = bufferedReader.readLine();
       while (line != null){
           //ystem.out.println (line);
           data.add(splitter(line, columnNames.length));
           line = bufferedReader.readLine();
       }
       bufferedReader.close();
       setData(data);
       dataProperties = new DataPropertyTreeNode[columnNames.length];
       actionManager.setOriginalNames(columnNames);
    }

    public File loadFile(JButton button, ActionManager actionManager){
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files", "csv");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(table);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (file.exists()) {
                if (file.isFile()){
                    file = chooser.getSelectedFile();
                    try {
                        setCSV_File(file, actionManager);
                        button.setText ("Replace "+file.getName());
                        //ystem.out.println("You chose to open this file: " + file.getName());
                        return file;
                    } catch (FileNotFoundException ex) {
                        JOptionPane.showMessageDialog(table, ex, "Input Error", JOptionPane.ERROR_MESSAGE);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(table, ex, "Input Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                   JOptionPane.showMessageDialog(table, "Sorry file " + file.getName() + " is a directory","Can not read directory", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(table, "Sorry file " + file.getName() + " Does not exist", "File not found", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }

}
