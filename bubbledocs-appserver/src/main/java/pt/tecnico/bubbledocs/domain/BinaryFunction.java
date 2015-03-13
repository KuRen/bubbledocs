package pt.tecnico.bubbledocs.domain;

public class BinaryFunction extends BinaryFunction_Base {
    
    public BinaryFunction() {
        super();
    }
    
    public BinaryFunction(Content content1, Content content2) {
        super();
        init(content1, content2);
    }
    
    protected void init(Content content1, Content content2) {
        setContent1(content1);
        setContent2(content2);
    }
}
