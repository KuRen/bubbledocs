package pt.ulisboa.tecnico.sdis.store.ws.client.command;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

import pt.ulisboa.tecnico.sdis.store.ws.CreateDocResponse;
import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocUserPair;
import pt.ulisboa.tecnico.sdis.store.ws.SDStore;
import pt.ulisboa.tecnico.sdis.store.ws.client.FrontEnd;
import pt.ulisboa.tecnico.sdis.store.ws.client.command.dto.HandlerInfo;

public class CreateDocumentCommand extends AbstractCommand {

    private DocUserPair docUserPair;

    public CreateDocumentCommand(FrontEnd frontEnd, HandlerInfo handlerInfo, List<SDStore> replicas, DocUserPair docUserPair) {
        super(handlerInfo, replicas, frontEnd);
        this.docUserPair = docUserPair;
    }

    public void execute() throws DocAlreadyExists_Exception, InterruptedException, TimeoutException {
        int n = replicas.size();
        int half = Math.floorDiv(n, 2);
        final Counter counter = new Counter();

        for (SDStore replica : replicas) {
            putToHandler(replica);

            replica.createDocAsync(docUserPair, new AsyncHandler<CreateDocResponse>() {

                @Override
                public void handleResponse(Response<CreateDocResponse> res) {
                    try {
                        res.get(5, TimeUnit.SECONDS);
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
            return; // we did good
        }

        if (counter.getFails() >= counter.getTimeout()) {
            throw new DocAlreadyExists_Exception("Document already exists.", null);
        }

        throw new TimeoutException();
    }

}
