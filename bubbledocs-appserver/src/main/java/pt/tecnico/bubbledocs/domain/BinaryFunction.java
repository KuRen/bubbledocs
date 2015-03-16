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

    @Override
    public Integer getValue() {
        return exec();
    }

    public abstract Integer exec();

    @Override
    public void delete() {
        getArgument1().delete();
        getArgument2().delete();

        setArgument1(null);
        setArgument2(null);

        super.delete();
    }

    @Override
    public void importFromXML(Element contentElement) {
        List<Element> argsElement = contentElement.getChildren();
        Element arg1Element = argsElement.get(0);
        Element arg2Element = argsElement.get(1);

        String arg1Type = arg1Element.getName();
        Content arg1content = Content.contentFactory(arg1Type);
        setArgument1(arg1content);
        arg1content.importFromXML(arg1Element);

        String arg2Type = arg2Element.getName();
        Content arg2content = Content.contentFactory(arg2Type);
        setArgument2(arg2content);
        arg2content.importFromXML(arg2Element);

    }
}
