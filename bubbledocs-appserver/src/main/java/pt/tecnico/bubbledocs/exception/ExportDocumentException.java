package pt.tecnico.bubbledocs.exception;

public class ExportDocumentException extends BubbleDocsException {
	private static final long serialVersionUID = 1L;

    public ExportDocumentException() {
        super("Error exporting spreadsheet to XML");
    }
}
