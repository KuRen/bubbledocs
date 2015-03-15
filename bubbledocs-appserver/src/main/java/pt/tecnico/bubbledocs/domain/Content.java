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

	public static Content importFromXML(Element contentElement, String elementType) {
		// TODO Auto-generated method stub
		return null;
	}
}
