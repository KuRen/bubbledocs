package pt.tecnico.bubbledocs.domain;

import org.jdom2.Element;

import pt.tecnico.bubbledocs.xml.XMLWriter;
import pt.tecnico.bubbledocs.xml.XMLable;

public class Literal extends Literal_Base implements XMLable {
    
    public Literal() {
        super();
    }
    
    public Literal(Integer literal) {
        super();
        setLiteral(literal);
    }
    
    @Override
	public Element accept(XMLWriter writer) {
		return writer.visit(this);
	}
    
}
