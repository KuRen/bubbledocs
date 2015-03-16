package pt.tecnico.bubbledocs.xml;

import org.jdom2.Element;

import pt.tecnico.bubbledocs.domain.Addition;
import pt.tecnico.bubbledocs.domain.Cell;
import pt.tecnico.bubbledocs.domain.Division;
import pt.tecnico.bubbledocs.domain.Literal;
import pt.tecnico.bubbledocs.domain.Multiplication;
import pt.tecnico.bubbledocs.domain.Reference;
import pt.tecnico.bubbledocs.domain.Spreadsheet;
import pt.tecnico.bubbledocs.domain.Subtraction;

public interface XMLWriter {
    Element visit(Spreadsheet ss);

    Element visit(Cell cell);

    Element visit(Addition add);

    Element visit(Subtraction sub);

    Element visit(Multiplication mul);

    Element visit(Division div);

    Element visit(Literal literal);

    Element visit(Reference reference);
}
