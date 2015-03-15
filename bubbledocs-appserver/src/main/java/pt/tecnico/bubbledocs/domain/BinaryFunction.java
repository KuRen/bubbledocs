package pt.tecnico.bubbledocs.domain;

import java.util.List;

import org.jdom2.Element;

public abstract class BinaryFunction extends BinaryFunction_Base {

    public BinaryFunction() {
        super();
    }

    public BinaryFunction(Content content1, Content content2) {
        super();
        init(content1, content2);
    }

    protected void init(Content content1, Content content2) {
        setArgument1(content1);
        setArgument2(content2);
    }
    
    public Integer getValue() {
    	return exec();
    }
    
    public abstract Integer exec();

    @Override
    public void delete() {
        setCell(null);
        setArgument1(null);
        setArgument2(null);
        deleteDomainObject();
    }
    
    @Override
	public void importFromXML(Element contentElement) {
    	List<Element> argsElement = contentElement.getChildren();
    	Element arg1Element = argsElement.get(0);
    	Element arg2Element = argsElement.get(1);
    	
    	String arg1Type = arg1Element.getName();
    	Content arg1content = Content.contentFactory(arg1Type);
    	arg1content.importFromXML(arg1Element);
    	setArgument1(arg1content);
    	
    	String arg2Type = arg2Element.getName();
    	Content arg2content = Content.contentFactory(arg2Type);
    	arg1content.importFromXML(arg2Element);
    	setArgument2(arg2content);
		
	}
}
