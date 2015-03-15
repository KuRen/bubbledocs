package pt.tecnico.bubbledocs.domain;

import org.jdom2.Element;

import pt.tecnico.bubbledocs.xml.XMLWriter;

public class Subtraction extends Subtraction_Base {
    
    public Subtraction() {
        super();
    }
    
    public Subtraction(Content content1, Content content2) {
        super();
        init(content1, content2);
    }
    
    @Override
	public Element accept(XMLWriter writer) {
		return writer.visit(this);
	}

	@Override
	public Integer exec() {
		try {
			return getArgument1().getValue() - getArgument2().getValue();
		} catch (Exception e){ return null;
		}
	}
    
}
