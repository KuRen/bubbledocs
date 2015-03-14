package pt.tecnico.bubbledocs.domain;

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
    public void delete() {
        setCell(null);
        getArgument1().setCell(null);
        getArgument2().setCell(null);
        deleteDomainObject();
    }
}
