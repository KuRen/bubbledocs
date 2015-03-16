package pt.tecnico.bubbledocs.xml;

public interface XMLable {
    org.jdom2.Element accept(XMLWriter writer);
}
