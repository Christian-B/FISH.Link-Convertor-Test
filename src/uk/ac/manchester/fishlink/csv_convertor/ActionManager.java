package uk.ac.manchester.fishlink.csv_convertor;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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

    private JButton loadConversion;

    private String[] originalNames = new String[0];

    private int selectedColumn = -1;

    File csvFile;

    File ontologyFile;

    File convertorFile;

    public ActionManager(Container container, CSV_Model model,  DataPropertyPane dataPropertyPane, InfoPanel infoPanel,
                         StatusChecker statusChecker, JButton loadConversion)
    {
        this.container = container;
        this.model = model;
        this.dataPropertyPane = dataPropertyPane;
        this.infoPanel = infoPanel;
        this.statusChecker = statusChecker;
        this.statusChecker.addActionListener(this);
        this.loadConversion = loadConversion;
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
        } else if (command.equals("Load Conversion")) {
            loadConversion();
        } else if (command.equals("Load from CSV")) {
            csvFile = model.loadFile((JButton)e.getSource(), this);
        } else if (command.equals("Save conversion")) {
            saveConvertorSettings();
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

    public void makeNamesLegal (boolean interactive){
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

    private void addColumnElements (Document doc, Element rootElement){
        for (int column = 0; column < model.getColumnCount(); column++){
            Element columnElement = doc.createElement("Column");
            rootElement.appendChild(columnElement);
            Attr attr = doc.createAttribute("OriginalName");
	    attr.setValue(originalNames[column]);
            columnElement.setAttributeNode(attr);
            attr = doc.createAttribute("UpdatedName");
	    attr.setValue(model.getColumnName(column));
            columnElement.setAttributeNode(attr);
        }
    }

    private File chooseFile(){
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("XML Files", "xml");
        chooser.setFileFilter(filter);
        chooser.setAcceptAllFileFilterUsed(false);
        int returnVal = chooser.showSaveDialog(container);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File temp = chooser.getSelectedFile();
            if (temp.exists()) {
                int response = JOptionPane.showConfirmDialog(container, " File " + temp.getName() + " already exists. Ok to overwrite ", "Confirm Overwrite",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (response == JOptionPane.CANCEL_OPTION) {
                    return null;
                }
            }
            return temp;
        }
        return null;
    }

    private void saveConvertorSettings() {
        int overwrite = JOptionPane.NO_OPTION;
        System.out.println(convertorFile);
        if (convertorFile != null){
            overwrite = JOptionPane.showConfirmDialog(container, "Overwrite " + convertorFile.getName() + " with new settings.", "Confirm Overwrite",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        }
        File file;
        if (overwrite == JOptionPane.NO_OPTION){
            file = chooseFile();
        } else {
            file = convertorFile;
        }
        saveConvertorSettings(file);
    }

    private void loadConversion(){
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("XML Files", "xml");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(container);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (file.exists()) {
                if (file.isFile()){
                    file = chooser.getSelectedFile();
                    //try {
                        loadConversion(file);
                        return;
                        //ystem.out.println("You chose to open this file: " + file.getName());
                    //} catch (FileNotFoundException ex) {
                    //    JOptionPane.showMessageDialog(container, ex, "Input Error", JOptionPane.ERROR_MESSAGE);
                    //} catch (IOException ex) {
                    //    JOptionPane.showMessageDialog(container, ex, "Input Error", JOptionPane.ERROR_MESSAGE);
                    //}
                } else {
                   JOptionPane.showMessageDialog(container, "Sorry file " + file.getName() + " is a directory","Can not read directory", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(container, "Sorry file " + file.getName() + " Does not exist", "File not found", JOptionPane.ERROR_MESSAGE);
            }
        }
        convertorFile = null;
    }

    private void loadConversion (File file) {
        convertorFile =  file;
        loadConversion.setText("replace " + file.getName());
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.parse(file);

            NodeList nodeList = doc.getElementsByTagName("Ontology_File");
            Element element = (Element)nodeList.item(0);
            String path = element.getAttribute("Path");
            ontologyFile = new File(path);
            dataPropertyPane.replaceOntology(ontologyFile, this);

            nodeList = doc.getElementsByTagName("CSV_File");
            element = (Element)nodeList.item(0);
            path = element.getAttribute("Path");
            csvFile = new File(path);
            model.setCSV_File(csvFile, this);


            nodeList = doc.getElementsByTagName("Column");
            for (int i = 0; i < nodeList.getLength(); i++){
                element = (Element)nodeList.item(i);
                String originalName = element.getAttribute("OriginalName");
                for (int j = 0; j < originalNames.length; j++){
                    if (originalNames[j].equals(originalName)){
                        String updatedName = element.getAttribute("UpdatedName");
                        model.setColumnName(j, updatedName);
                    }
                }
            }
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        }
        checkNamesInDataProperty(false);
    }

    private void saveConvertorSettings(File file) {
        try{
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            //root element
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("Conversion");
            doc.appendChild(rootElement);

            //csv file element
            Element csv_file = doc.createElement("CSV_File");
            rootElement.appendChild(csv_file);
            Attr attr = doc.createAttribute("Path");
	    attr.setValue(csvFile.getAbsolutePath());
            csv_file.setAttributeNode(attr);

            //ontology file element
            Element ontology_file = doc.createElement("Ontology_File");
            rootElement.appendChild(ontology_file);
            attr = doc.createAttribute("Path");
	    attr.setValue(ontologyFile.getAbsolutePath());
            ontology_file.setAttributeNode(attr);

            addColumnElements (doc, rootElement);
            
            //write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result =  new StreamResult(file);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            transformer.transform(source, result);

            loadConversion.setText("replace " + file.getName());

        }catch(ParserConfigurationException pce){
            pce.printStackTrace();
        }catch(TransformerException tfe){
            tfe.printStackTrace();
        }
    }

}
