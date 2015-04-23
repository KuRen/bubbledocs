package pt.tecnico.bubbledocs.service.local;

import pt.tecnico.bubbledocs.exception.BubbleDocsException;

public class AssignBinaryFunctionToCell extends BubbleDocsService {

    private String result;

    private String token;
    private int docId;
    private String cellId;
    private String expression;

    public AssignBinaryFunctionToCell(String token, int docId, String cellId, String expression) {
        this.token = token;
        this.docId = docId;
        this.cellId = cellId;
        this.expression = expression;
    }

    @Override
    protected void dispatch() throws BubbleDocsException {
        // TODO Auto-generated method stub

    }

    public String getResult() {
        return result;
    }

}
