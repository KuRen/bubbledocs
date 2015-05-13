package pt.ulisboa.tecnico.sdis.store.ws.client.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

import pt.ulisboa.tecnico.sdis.store.ws.ListDocsResponse;
import pt.ulisboa.tecnico.sdis.store.ws.SDStore;
import pt.ulisboa.tecnico.sdis.store.ws.UserDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.client.FrontEnd;
import pt.ulisboa.tecnico.sdis.store.ws.client.command.dto.HandlerInfo;

public class ListDocumentsCommand extends AbstractCommand {

    class ListOfListOfDocuments {

        List<List<String>> list = new ArrayList<List<String>>();

        public void add(List<String> documentsList) {
            list.add(documentsList);
        }

        public List<String> merge() {
            Set<String> set = new HashSet<>();
            for (List<String> docList : list) {
                set.addAll(docList);
            }
            return new ArrayList<String>(set);
        }
    }

    private String userId;

    public ListDocumentsCommand(FrontEnd frontEnd, HandlerInfo handlerInfo, List<SDStore> replicas, String userId) {
        super(handlerInfo, replicas, frontEnd);
        this.userId = userId;
    }

    public List<String> execute() throws UserDoesNotExist_Exception, InterruptedException, TimeoutException {
        int n = replicas.size();
        int half = Math.floorDiv(n, 2);
        final Counter counter = new Counter();
        final ListOfListOfDocuments list = new ListOfListOfDocuments();

        for (SDStore replica : replicas) {
            putToHandler(replica);

            replica.listDocsAsync(userId, new AsyncHandler<ListDocsResponse>() {

                @Override
                public void handleResponse(Response<ListDocsResponse> res) {
                    try {
                        List<String> documentsList = res.get(5, TimeUnit.SECONDS).getDocumentId();
                        list.add(documentsList);
                        counter.incSuccess();
                    } catch (ExecutionException e1) {
                        counter.incFails();
                    } catch (InterruptedException | TimeoutException | CancellationException e2) {
                        counter.incTimeout();
                    } finally {
                        counter.incTotal();
                    }
                }
            });
        }

        while (counter.getTotal() < n) {
            Thread.sleep(100);
            System.out.print(".");
            System.out.flush();
        }

        if (counter.getSuccess() > half) {
            return list.merge();
        }

        if (counter.getFails() >= counter.getTimeout()) {
            throw new UserDoesNotExist_Exception("User does not exists.", null);
        }

        throw new TimeoutException();
    }
}
