package org.andromda.cartridges.bpm4struts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.andromda.metafacades.uml.ManageableEntity;
import org.andromda.utils.StringUtilsHelper;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

/**
 * Contains utilities for bpm4struts.
 *
 * @author Wouter Zoons
 */
public final class Bpm4StrutsUtils
{
	
	private static Collection itens   = new ArrayList();
	
	public static  void add(Object item){
		itens.add(item);
	}
	
	
	public static Collection getItens() {
		return itens;
	}
	
	
	public static void clear(){
		itens.clear();
	}
	
	
	/**
	 * 
	 * @param fileName
	 * @param itensUseCase
	 * @return Collection das relações de casos de uso menos aqueles definidos no arquivo fileName para serem removidos
	 */
	public static Collection useCaseRemove(String fileName , Collection itensUseCase){
		
		Collection candidateItens = new ArrayList();
		
		try {
	        BufferedReader in = new BufferedReader(new FileReader(fileName));
	        String candidateItem;
	        while ((candidateItem = in.readLine()) != null) {
	             candidateItens.add(candidateItem);
	        }
	        in.close();
	    } catch (IOException e) {
	    	
	    	
	    		return itensUseCase;
	    	
	    }
		
		
	    Iterator it = candidateItens.iterator();
	    while( it.hasNext()){
	    	Object candidate = it.next();
	    	itensUseCase.remove(candidate);
	    }
		
		return  itensUseCase;
	}
	
	
	
    /**
     * Creates and returns a List from an <code>enumeration</code>.
     *
     * @param enumeration the enumeration from which to create the List.
     * @return the new List.
     */
    public static List listEnumeration(Enumeration enumeration)
    {
        List list;
        if (enumeration == null)
        {
            list = Collections.EMPTY_LIST;
        }
        else
        {
            list = Collections.list(enumeration);
        }
        return list;
    }

    private static final Pattern VALIDATOR_TAGGEDVALUE_PATTERN = Pattern.compile(
        "\\w+(\\(\\w+=[^,)]*(,\\w+=[^,)]*)*\\))?");

    /**
     * Reads the validator arguments from the the given tagged value.
     *
     * @return never null, returns a list of String instances
     * @throws IllegalArgumentException when the input string does not match the required pattern
     */
    public static List parseValidatorArgs(String validatorTaggedValue)
    {
        if (validatorTaggedValue == null)
        {
            throw new IllegalArgumentException("Validator tagged value cannot be null");
        }

        // check if the input tagged value matches the required pattern
        if (!VALIDATOR_TAGGEDVALUE_PATTERN.matcher(validatorTaggedValue).matches())
        {
            throw new IllegalArgumentException(
                "Illegal validator tagged value (this tag is used to specify custom validators " +
                    "and might look like myValidator(myVar=myArg,myVar2=myArg2), perhaps you wanted to use " +
                    "@andromda.presentation.view.field.format?): " + validatorTaggedValue);
        }

        final List validatorArgs = new ArrayList();

        // only keep what is between parentheses (if any)
        int left = validatorTaggedValue.indexOf('(');
        if (left > -1)
        {
            final int right = validatorTaggedValue.indexOf(')');
            validatorTaggedValue = validatorTaggedValue.substring(left + 1, right);

            final String[] pairs = validatorTaggedValue.split(",");
            for (int i = 0; i < pairs.length; i++)
            {
                final String pair = pairs[i];
                final int equalsIndex = pair.indexOf('=');
                // it's possible the argument is the empty string
                if (equalsIndex < pair.length() - 1)
                {
                    validatorArgs.add(pair.substring(equalsIndex + 1));
                }
                else
                {
                    validatorArgs.add("");
                }
            }
        }
        return validatorArgs;
    }

    /**
     * Reads the validator variable names from the the given tagged value.
     *
     * @return never null, returns a list of String instances
     * @throws IllegalArgumentException when the input string does not match the required pattern
     */
    public static List parseValidatorVars(String validatorTaggedValue)
    {
        if (validatorTaggedValue == null)
        {
            throw new IllegalArgumentException("Validator tagged value cannot be null");
        }

        // check if the input tagged value matches the required pattern
        if (!VALIDATOR_TAGGEDVALUE_PATTERN.matcher(validatorTaggedValue).matches())
        {
            throw new IllegalArgumentException("Illegal validator tagged value: " + validatorTaggedValue);
        }

        final List validatorVars = new ArrayList();

        // only keep what is between parentheses (if any)
        int left = validatorTaggedValue.indexOf('(');
        if (left > -1)
        {
            int right = validatorTaggedValue.indexOf(')');
            validatorTaggedValue = validatorTaggedValue.substring(left + 1, right);

            final String[] pairs = validatorTaggedValue.split(",");
            for (int i = 0; i < pairs.length; i++)
            {
                final String pair = pairs[i];
                final int equalsIndex = pair.indexOf('=');
                validatorVars.add(pair.substring(0, equalsIndex));
            }
        }
        return validatorVars;
    }

    /**
     * Parses the validator name for a tagged value.
     *
     * @throws IllegalArgumentException when the input string does not match the required pattern
     */
    public static String parseValidatorName(String validatorTaggedValue)
    {
        if (validatorTaggedValue == null)
        {
            throw new IllegalArgumentException("Validator tagged value cannot be null");
        }

        // check if the input tagged value matches the required pattern
        if (!VALIDATOR_TAGGEDVALUE_PATTERN.matcher(validatorTaggedValue).matches())
        {
            throw new IllegalArgumentException("Illegal validator tagged value: " + validatorTaggedValue);
        }

        final int leftParen = validatorTaggedValue.indexOf('(');
        return (leftParen == -1) ? validatorTaggedValue : validatorTaggedValue.substring(0, leftParen);
    }

    /**
     * Sorts a collection of Manageable entities according to their 'manageableName' property.
     * Returns a new collection.
     */
    public static Collection sortManageables(Collection collection)
    {
        final List sorted = new ArrayList(collection);
        Collections.sort(sorted, new ManageableEntityComparator());
        return sorted;
    }

    /**
     * Converts the argument into a web file name, this means: all lowercase
     * characters and words are separated with dashes.
     *
     * @param string any string
     * @return the string converted to a value that would be well-suited for a
     *         web file name
     */
    public static String toWebFileName(final String string)
    {
        return StringUtilsHelper.toPhrase(string).replace(' ', '-').toLowerCase();
    }

    /**
     * Returns <code>true</code> if the argument name will not cause any troubles with the Jakarta commons-beanutils
     * library, which basically means it does not start with an lowercase characters followed by an uppercase character.
     * This means there's a bug in that specific library that causes an incompatibility with the Java Beans
     * specification as implemented in the JDK.
     *
     * @param name the name to test, may be <code>null</code>
     * @return <code>true</code> if the name is safe to use with the Jakarta libraries, <code>false</code> otherwise
     */
    public static boolean isSafeName(final String name)
    {
        boolean safe = true;

        if (name != null && name.length() > 1)
        {
            safe = !(Character.isLowerCase(name.charAt(0)) && Character.isUpperCase(name.charAt(1)));
        }

        return safe;
    }

    /**
     * Returns a sequence of file formats representing the desired export types for the display tag tables
     * used for the argument element.
     *
     * @param taggedValues the collection of tagged values representing the export types, should only contain
     *  <code>java.lang.String</code> instances and must never be <code>null</code>
     * @param defaultValue the default value to use in case the tagged values are empty
     * @return a space separated list of formats, never <code>null</code>
     */
    public static String getDisplayTagExportTypes(final Collection taggedValues, final String defaultValue)
    {
        String exportTypes;

        if (taggedValues.isEmpty())
        {
            exportTypes = defaultValue;
        }
        else
        {
            if (taggedValues.contains("none"))
            {
                exportTypes = "none";
            }
            else
            {
                final StringBuffer buffer = new StringBuffer();
                for (final Iterator iterator = taggedValues.iterator(); iterator.hasNext();)
                {
                    final String exportType = StringUtils.trimToNull(String.valueOf(iterator.next()));
                    if ("csv".equalsIgnoreCase(exportType) ||
                        "pdf".equalsIgnoreCase(exportType) ||
                        "xml".equalsIgnoreCase(exportType) ||
                        "excel".equalsIgnoreCase(exportType))
                    {
                        buffer.append(exportType);
                        buffer.append(' ');
                    }
                }
                exportTypes = buffer.toString().trim();
            }
        }

        return exportTypes;
    }

    /**
     * Convenient method to detect whether or not a String instance represents a boolean <code>true</code> value.
     */
    public static boolean isTrue(String string)
    {
        return "yes".equalsIgnoreCase(string) ||
            "true".equalsIgnoreCase(string) ||
            "on".equalsIgnoreCase(string) ||
            "1".equalsIgnoreCase(string);
    }

    private final static class ManageableEntityComparator
        implements Comparator
    {
        public int compare(
            Object left,
            Object right)
        {
            final ManageableEntity leftEntity = (ManageableEntity)left;
            final ManageableEntity rightEntity = (ManageableEntity)right;
            return StringUtils.trimToEmpty(leftEntity.getName()).compareTo(
                StringUtils.trimToEmpty(rightEntity.getName()));
        }
    }
    
    public static String getNewMagicDrawID(String entity, String name)
    {
    	String id = "";

    	// 1312207235890
    	//id += "_" + generateIntegerStr(14);
    	String encodedEntity = Base64.encode(entity.getBytes()); 
    	if(encodedEntity.length() > 14){
    		id += encodedEntity.substring(0, 14);
    	}else{
    		id += encodedEntity;
    	}
    	
    	// 30139
		id += "_" + generateIntegerStr(5);
		String encodedName = Base64.encode(name.getBytes()); 
		if(encodedName.length() > 5){
			id += encodedName.substring(0, 5);
    	}else{
    		id += encodedName;
    	}
		
    	// 1252
		id += "_" + generateIntegerStr(32 - id.length());
		
    	return id;
    }

    private static String generateIntegerStr (int length)
    {
    	Random randomGenerator = new Random();

    	// String s = new Long(randomGenerator.nextLong((long)(Math.pow(10, length) - Math.pow(10, length-1))) + (long)Math.pow(10, length-1)).toString();
    	String s = new Long(randomGenerator.nextLong()).toString();

    	return s.substring(s.length() - length, s.length());
    }
    
    public static String getDataTypeId(String nome, String dir)
    {    	
    	String id = new String();
    	try {
    		ZipFile zipFile = new ZipFile(dir + "/mda/src/uml/xml.zips/andromda-profile-datatype-3.1.xml.zip");
    		Enumeration entries = zipFile.entries();
    		ZipEntry entry = null;
    		if(entries.hasMoreElements()){
    	        entry = (ZipEntry)entries.nextElement();
    		}
    		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(zipFile.getInputStream(entry));

			Element xmi = doc.getDocumentElement();
			Element xmiContent = (Element) xmi.getElementsByTagName("XMI.content").item(0);
			Element umlModel = (Element) xmiContent.getElementsByTagName("UML:Model").item(0);
			Element ownedElement = (Element) umlModel.getElementsByTagName("UML:Namespace.ownedElement").item(0);
			NodeList umlPackages = ownedElement.getElementsByTagName("UML:Package");
			int i = 0;
			while (i < umlPackages.getLength()){
				Element umlPackage = (Element) umlPackages.item(i);
				if(umlPackage.getAttribute("name").equals("datatype")){
					Element packageOwnedElement = (Element) umlPackage.getElementsByTagName("UML:Namespace.ownedElement").item(0);
					NodeList dataTypes = packageOwnedElement.getElementsByTagName("UML:DataType");
					int j = 0;
					Element dataType;
					while (j < dataTypes.getLength()){
						dataType = (Element) dataTypes.item(j);
						if(dataType.getAttribute("name").equals(nome)){
							j=dataTypes.getLength();
							id = dataType.getAttribute("xmi.id");
						}
						else{
							j++;
						}
					}
					i=umlPackages.getLength();
				}
				else{
					i++;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return id;
    }

	public final static String getSuffixFromActionStruts2() {
		return "Action2";
	}

	public final static String getExecuteMethodFromActionStruts2() {
		return "execute";
	}

	public final static String getDefaultNameConfig() {
		return "default";
	}

	public final static String getStrutsDefaultNameConfig() {
		return "mdarte-struts-default";
	}
}
