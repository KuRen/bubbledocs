package pt.tecnico.bubbledocs.domain;

import org.jdom2.Element;

import pt.tecnico.bubbledocs.xml.XMLWriter;

public class Multiplication extends Multiplication_Base {
    
    public Multiplication() {
        super();
    }
    
    public Multiplication(Content content1, Content content2) {
        super();
        init(content1, content2);
    }
    
    @Override
	public Element accept(XMLWriter writer) {
		return writer.visit(this);
	}
    
}
