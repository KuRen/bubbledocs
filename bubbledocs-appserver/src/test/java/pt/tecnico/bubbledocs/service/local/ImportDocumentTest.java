package pt.tecnico.bubbledocs.service.local;

import org.junit.Assert;
import mockit.Mock;
import mockit.MockUp;

import org.junit.Test;

import pt.tecnico.bubbledocs.domain.Spreadsheet;
import pt.tecnico.bubbledocs.exception.CannotLoadDocumentException;
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.exception.UnavailableServiceException;
import pt.tecnico.bubbledocs.service.remote.StoreRemoteServices;

public class ImportDocumentTest extends BubbleDocsServiceTest {

    private String authorizedToken;

    private static final String USER = "userName";
    private static final String DOCID = "documento";

    @Test(expected = UnavailableServiceException.class)
    public void testRemoteInvocationException() {
        new MockUp<StoreRemoteServices>() {
            @Mock
            public byte[] loadDocument(String username, String docName) {
                throw new RemoteInvocationException();
            }
        };

        ImportDocument service = new ImportDocument(DOCID, authorizedToken);
        service.execute();

    }

    @Test(expected = CannotLoadDocumentException.class)
    public void testCannotStoreDocumentException() {
        new MockUp<StoreRemoteServices>() {
            @Mock
            public byte[] loadDocument(String username, String docName) {
                throw new CannotLoadDocumentException();
            }
        };

        ImportDocument service = new ImportDocument(DOCID, authorizedToken);
        service.execute();
    }

    @Test
    public void testStoreDocument() {
        new MockUp<StoreRemoteServices>() {
            @Mock
            public byte[] loadDocument(String username, String docName) {
                return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Spreadsheet rows=\"1\" columns=\"1\" id=\"99\" name=\"A SS Name\" owner=\"userName\" created=\"2015-04-24T20:25:06.747+01:00\"><Cells><Cell row=\"1\" column=\"1\"><Literal literal=\"2\" /></Cell></Cells></Spreadsheet>".getBytes();
            }
        };
        ImportDocument service = new ImportDocument(DOCID, authorizedToken);
        service.execute();
        Spreadsheet ss = getSpreadSheet("A SS Name");
        Assert.assertEquals(ss.getOwner().getUsername(), USER);
        Assert.assertEquals(ss.getColumns(), new Integer(1));
        Assert.assertEquals(ss.getRows(), new Integer(1));
    }

    @Override
    public void populate4Test() {
        createUser(USER, "password", "email", "big complete name");
        authorizedToken = addUserToSession(USER);
    }
}
