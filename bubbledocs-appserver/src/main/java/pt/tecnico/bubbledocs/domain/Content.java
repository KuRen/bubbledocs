package pt.tecnico.bubbledocs.domain;

import org.jdom2.Element;

import pt.tecnico.bubbledocs.xml.XMLable;

public abstract class Content extends Content_Base implements XMLable {

    public Content() {
        super();
    }

    public void delete() {
        setCell(null);
        deleteDomainObject();
    }

	public static Content contentFactory(String elementType) {
		switch (elementType) {
		case "Literal":
			return new Literal();
		case "Reference":
			return new Reference();
		case "Addition":
			return new Addition();
		case "Subtraction":
			return new Subtraction();
		case "Division":
			return new Division();
		case "Multiplication":
			return new Multiplication();
			
		default:
			break;
		}
		return null;
	}

	public abstract void importFromXML(Element contentElement);
	
	public abstract Integer getValue();
	
	public String toString() {
		Integer value = getValue();
		if (value != null)
			return value.toString();
		return "#VALUE";
	}
}
