package pt.tecnico.bubbledocs.domain;

public class Cell extends Cell_Base {
    
    public Cell(Integer row, Integer column) {
        super();
        setRow(row);
        setColumn(column);
        setContent(null);
    }
    
}
