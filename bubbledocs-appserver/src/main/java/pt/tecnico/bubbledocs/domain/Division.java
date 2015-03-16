package pt.tecnico.bubbledocs.domain;

import org.jdom2.Element;

import pt.tecnico.bubbledocs.xml.XMLWriter;

public class Division extends Division_Base {

    public Division() {
        super();
    }

    public Division(Content content1, Content content2) {
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
            return getArgument1().getValue() / getArgument2().getValue();
        } catch (ArithmeticException e) {
            throw new ArithmeticException();
        } catch (Exception e) {
            return null;
        }
    }

}
