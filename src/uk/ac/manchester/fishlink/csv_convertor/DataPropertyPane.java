/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.manchester.fishlink.csv_convertor;

import java.awt.Container;
import java.io.File;
import java.util.Enumeration;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 *
 * @author Christian
 */
public class DataPropertyPane extends JScrollPane {

    private JTree tree;

    private DefaultMutableTreeNode top;

    //OWLOntology ontology;

    public DataPropertyPane(){
        super();
    }
        
    public void replaceOntology(File file, ActionManager actionManager){

        if (tree != null){
            this.remove(tree);
            tree.removeTreeSelectionListener(actionManager);
        }

        top = readOntology();
        System.out.println(top.getChildCount());
        tree = new JTree(top);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        ToolTipManager.sharedInstance().registerComponent(tree);
        TreeCellRenderer renderer = new ToolTipTreeCellRenderer();
        tree.setCellRenderer(renderer);

        this.setViewportView(tree);
        //this.setMinimumSize(null);
        tree.addTreeSelectionListener(actionManager);
    }

    public String loadFile(Container parent, ActionManager actionManager, JButton button){
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Ontology Files", "owl");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(parent);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (file.exists()) {
                if (file.isFile()){
                    file = chooser.getSelectedFile();
                    replaceOntology(file, actionManager);
                    button.setText ("Replace "+file.getName());
                    //ystem.out.println("You chose to open this file: " + file.getName());
                    return file.getAbsolutePath();
                } else {
                   JOptionPane.showMessageDialog(parent, "Sorry file " + file.getName() + " is a directory","Can not read directory", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(parent, "Sorry file " + file.getName() + " Does not exist", "File not found", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }

    private static void addNode(OWLDataProperty dataProperty, DefaultMutableTreeNode parent, OWLOntology ontology){
        DataPropertyTreeNode node = new DataPropertyTreeNode(dataProperty, ontology);
        parent.add(node);
        //ystem.out.println(dataProperty);
        for (OWLDataPropertyExpression child : dataProperty.getSubProperties(ontology)){
            addNode((OWLDataProperty) child, node, ontology);
        }
    }

    private static DefaultMutableTreeNode readOntology() {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("Vocabulary                      ");

        //Read the Ontology
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        IRI documentIRI = IRI.create("file:d:/Protege/Ontologies/vocab.owl");
        try {
            OWLOntology ontology = manager.loadOntologyFromOntologyDocument(documentIRI);
 
            //Add the Properties
            for (OWLDataProperty dataProperty : ontology.getDataPropertiesInSignature()){
                Set<OWLDataPropertyExpression> parents = dataProperty.getSuperProperties(ontology);
                if (parents.isEmpty()){
                    addNode (dataProperty, top, ontology);
                }
                //ystem.out.print(dataProperty);
                //ystem.out.println(dataProperty.isOWLTopDataProperty());
            }
       } catch (OWLOntologyCreationException ex) {
            ex.printStackTrace();
            System.exit(1);
       }
       return top;
    }

    /*
    private static void createNodes(DefaultMutableTreeNode top) {
        DefaultMutableTreeNode category = null;
        DefaultMutableTreeNode vocab = null;

        category = new DefaultMutableTreeNode(new VocabInfo("Location", false));
        top.add(category);

        vocab = new DefaultMutableTreeNode(new VocabInfo("Easting", true));
        category.add(vocab);

        vocab = new DefaultMutableTreeNode(new VocabInfo("Northing", true));
        category.add(vocab);

        vocab = new DefaultMutableTreeNode(new VocabInfo("GridRef", true));
        category.add(vocab);

        category = new DefaultMutableTreeNode(new VocabInfo("Chemisty", false));
        top.add(category);

        vocab = new DefaultMutableTreeNode(new VocabInfo("Sodium", true));
        category.add(vocab);

        vocab = new DefaultMutableTreeNode(new VocabInfo("PH", true));
        category.add(vocab);
    }*/

    public boolean checkName (String name){
        Enumeration e = top.breadthFirstEnumeration();
        while (e.hasMoreElements()) {
           DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
           if (node.getLevel() > 0){
               if (name.equals(((DataPropertyTreeNode)node).getName())){
                   return true;
               }
           }
       }
       return false;
    }
    
    public String suggestName (String name){
        Enumeration e = top.breadthFirstEnumeration();
        while (e.hasMoreElements()) {
           DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
           if (name.equalsIgnoreCase(((DataPropertyTreeNode)node).getName())){
               return name;
           }
       }
       return null;
    }
}
