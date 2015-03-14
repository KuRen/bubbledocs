package pt.tecnico.bubbledocs.service;

import java.io.UnsupportedEncodingException;

import org.jdom2.Document;
import org.jdom2.output.XMLOutputter;

import pt.tecnico.bubbledocs.domain.Spreadsheet;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.ExportDocumentException;
import pt.tecnico.bubbledocs.xml.BaseXMLWriter;
import pt.tecnico.bubbledocs.xml.XMLWriter;

public class ExportSpreadsheetService extends BubbleDocsService {

	private Spreadsheet spreadsheet;
	private XMLWriter writer;
	private byte[] document;
	
	public ExportSpreadsheetService(Spreadsheet ss) {
		this(ss, new BaseXMLWriter());
	}
	
	public ExportSpreadsheetService(Spreadsheet ss, XMLWriter xmlWriter) {
		spreadsheet = ss;
		writer = xmlWriter;
	}
	
	@Override
	protected void dispatch() throws BubbleDocsException {
		Document jdomDoc = new Document();

        jdomDoc.setRootElement(spreadsheet.accept(writer));

        XMLOutputter xml = new XMLOutputter();
        try {
            document = xml.outputString(jdomDoc).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new ExportDocumentException();
        }

	}
	
	public byte[] getResult() {
		return document;
	}

}
