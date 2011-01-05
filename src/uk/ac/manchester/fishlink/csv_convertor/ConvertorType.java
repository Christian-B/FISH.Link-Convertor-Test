/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.manchester.fishlink.csv_convertor;

import java.util.ArrayList;
import java.util.Set;
import org.semanticweb.owlapi.model.OWLDataRange;

/**
 *
 * @author Christian
 */
public class ConvertorType {

    boolean defined = false;

    boolean includeText = true;

    boolean includeDecimal = true;

    boolean IncludeNumber = true;

    boolean includeBoolean = true;

    boolean includeNulls = false;

    private void addRange (OWLDataRange range){
        String name = range.toString();
        if (name.equals("xsd:double")){
           includeDecimal = true;
           IncludeNumber = true;
        } else if (name.equals("xsd:float")){
           includeDecimal = true;
           IncludeNumber = true;
        } else if (name.equals("xsd:int")){
           IncludeNumber = true;
        } else if (name.equals("xsd:integer")){
           IncludeNumber = true;
        //} else if (name.equals("rdf:XMLLiteral")){
        //} else if (name.equals("rdfs:Literal")){
        //} else if (name.equals("xsd:ENTITIES")){
        //} else if (name.equals("xsd:ENTITY")){
        //} else if (name.equals("xsd:ID")){
        //} else if (name.equals("xsd:IDREF")){
        //} else if (name.equals("xsd:IDREFS")){
        //} else if (name.equals("xsd:NMToken")){
        //} else if (name.equals("xsd:NOTATION")){
        //} else if (name.equals("xsd:Name")){
        //} else if (name.equals("xsd:QName")){
        //} else if (name.equals("xsd:anySimpleType")){
        //} else if (name.equals("xsd:anyType")){
        //} else if (name.equals("xsd:anyURI")){
        //} else if (name.equals("xsd:base64Binary")){
        //} else if (name.equals("xsd:boolean")){
        } else if (name.equals("xsd:byte")){
           IncludeNumber = true;
        //} else if (name.equals("xsd:date")){
        //} else if (name.equals("xsd:dateTime")){
        } else if (name.equals("xsd:decimal")){
           IncludeNumber = true;
           includeDecimal = true;
        //} else if (name.equals("xsd:duration")){
        //} else if (name.equals("xsd:gDay")){
        //} else if (name.equals("xsd:gMonth")){
        //} else if (name.equals("xsd:gMonthYear")){
        //} else if (name.equals("xsd:gYear")){
        //} else if (name.equals("xsd:gYearMonth")){
        //} else if (name.equals("xsd:hexBinary")){
        //} else if (name.equals("xsd:language")){
        } else if (name.equals("xsd:long")){
           IncludeNumber = true;
        } else if (name.equals("xsd:negativeInteger")){
           IncludeNumber = true;
        } else if (name.equals("xsd:nonNegativeInteger")){
           IncludeNumber = true;
        } else if (name.equals("xsd:nonPositiveInteger")){
           IncludeNumber = true;
        } else if (name.equals("xsd:normalizedString")){
           IncludeNumber = true;
           includeDecimal = true;
           includeText = true;
        } else if (name.equals("xsd:positiveInteger")){
           IncludeNumber = true;
        } else if (name.equals("xsd:short")){
           IncludeNumber = true;
        } else if (name.equals("xsd:string")){
           includeDecimal = true;
           IncludeNumber = true;
           includeText = true;
        //} else if (name.equals("xsd:time")){
        //} else if (name.equals("xsd:token")){
        } else if (name.equals("xsd:unsignedByte")){
           IncludeNumber = true;
        } else if (name.equals("xsd:unsignedInt")){
           IncludeNumber = true;
        } else if (name.equals("xsd:unsignedLong")){
           IncludeNumber = true;
        } else if (name.equals("xsd:unsignedShort")){
           IncludeNumber = true;
        } else {
            System.err.println("Unexpectecd Type: "+ name);
        }
    }

    public ConvertorType (Set<OWLDataRange> ranges){
        if (!ranges.isEmpty()){
            defined = true;
            includeText = false;
            includeDecimal = false;
            IncludeNumber = false;
            includeBoolean = false;
            for (OWLDataRange range : ranges){
                addRange (range);
            }
        }
    }

    public ConvertorType (ArrayList<String[]> data, int column){
        defined = true;
        includeText = false;
        includeDecimal = false;
        IncludeNumber = false;
        includeBoolean = false;
        for (String[] line : data){
            String val = line[column];
            if (val.isEmpty()){
                includeNulls = true;
            } else if (val.equalsIgnoreCase("true")){
                includeBoolean = true;
            } else if (val.equalsIgnoreCase("false")){
                includeBoolean = true;
            } else {
                //not boolean so numbers ok
                IncludeNumber = true;
                if (!includeDecimal){
                    try {
                        long test = Long.parseLong(val);
                    } catch (NumberFormatException ex){
                        includeDecimal = true;
                    }
                } 
                if (includeDecimal) {
                    try {
                        double test = Double.parseDouble(val);
                    } catch (NumberFormatException ex){
                        includeText = true;
                        return;
                    }
                }
            }
        }
    }

    public String toString(){
        if (defined){
            if (includeText){
                return "String";
            }
            if (includeDecimal){
                if (includeBoolean){
                    return "Decimal or Boolean";
                }
                return "Decimal";
            } else if (IncludeNumber) {
                if (includeBoolean){
                    return "Whole or Boolean";
                }
                return "Whole Number";
            } else if(includeBoolean) {
                return "Boolean";
            } else {
                return "Nothing allowed";
            }
        } else {
            return "Undefinded";
        }
    }
}
