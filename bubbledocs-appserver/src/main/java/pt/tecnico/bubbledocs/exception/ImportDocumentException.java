package pt.tecnico.bubbledocs.exception;

public class ImportDocumentException extends BubbleDocsException {
	private static final long serialVersionUID = 1L;

	public ImportDocumentException() {
		super("Error importing spreadsheet from XML");
	}
}
