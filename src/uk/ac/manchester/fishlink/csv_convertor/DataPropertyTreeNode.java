/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.manchester.fishlink.csv_convertor;

import java.util.Set;
import javax.swing.tree.DefaultMutableTreeNode;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 *
 * @author Christian
 */
public class DataPropertyTreeNode extends DefaultMutableTreeNode{

    String name;
    String description = "";
    OWLOntology ontology;

    public DataPropertyTreeNode (OWLDataProperty dataProperty, OWLOntology ontology){
        super(dataProperty);
        name = dataProperty.toStringID().substring(1);
        this.ontology = ontology;

        //ConvertorType type = new ConvertorType(dataProperty.getRanges(ontology));
        for (OWLAnnotation annotation : dataProperty.getAnnotations(ontology)){
            //ystem.out.println(annotation);
            OWLAnnotationProperty property = annotation.getProperty();
            if (property.toString().equals("<http://purl.org/dc/elements/1.1/description>")){
                System.out.println(dataProperty);
                OWLAnnotationValue value = annotation.getValue();
                //ystem.out.println(property);
                //ystem.out.println(property.toStringID());
                //ystem.out.println(property.getClass());
                description = value.toString();
            }
        }
        //ystem.out.println(type);
        //for (OWLDataRange dataRange : dataProperty.getRanges(ontology)){
        //    System.out.println(dataRange + " " + dataRange.getDataRangeType());

        //    if (dataRange.isDatatype()){
        //        OWLDatatype dataType = (OWLDatatype)dataRange;
                  //ystem.out.println(dataType.isBoolean());
                  //ystem.out.println(dataType.isString());
                  //ystem.out.println(dataType.isDouble());
                  //ystem.out.println(dataType.isFloat());
                  //ystem.out.println(dataType.isInteger());
        //    }
        //}

    }

    public String getDescription(){
        return description;
    }

    public DataPropertyType getDataPropertyType(){
        if (this.isLeaf()){
            return DataPropertyType.LEAF;
        }
        if (name.endsWith("Properties")){
            return DataPropertyType.PROPERTIES;
        }
        return DataPropertyType.LOW_DETAIL;
    }

    @Override
    public String toString(){
        return name;
    }

    public String getName(){
        return name;
    }

    @Override
    public OWLDataProperty getUserObject(){
        Object test = super.getUserObject();
        //ystem.out.println(test.getClass());
        return (OWLDataProperty) super.getUserObject();
    }

    public void setUserObject(OWLDataProperty dataProperty){
        super.setUserObject(dataProperty);
        name = dataProperty.toStringID().substring(1);
    }

    public Set<OWLDataRange> getRanges(){
        OWLDataProperty dataProperty = this.getUserObject();
        return dataProperty.getRanges(ontology);
    }

}
