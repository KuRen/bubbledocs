package pt.ulisboa.tecnico.sdis.store.ws.client.command;

import java.util.List;

import javax.xml.ws.BindingProvider;

import pt.ulisboa.tecnico.sdis.store.ws.SDStore;
import pt.ulisboa.tecnico.sdis.store.ws.client.FrontEnd;
import pt.ulisboa.tecnico.sdis.store.ws.client.command.dto.HandlerInfo;

public class AbstractCommand {

    protected class Counter {

        int total = 0;
        int success = 0;
        int fails = 0;
        int timeout = 0;

        public int getTimeout() {
            return timeout;
        }

        public void incTimeout() {
            this.timeout++;
        }

        public int getTotal() {
            return total;
        }

        public void incTotal() {
            this.total++;
        }

        public int getSuccess() {
            return success;
        }

        public void incSuccess() {
            this.success++;
        }

        public int getFails() {
            return fails;
        }

        public void incFails() {
            this.fails++;
        }
    }

    protected HandlerInfo handlerInfo;
    protected List<SDStore> replicas;
    protected FrontEnd frontEnd;

    public AbstractCommand(HandlerInfo handlerInfo, List<SDStore> replicas, FrontEnd frontEnd) {
        super();
        this.handlerInfo = handlerInfo;
        this.replicas = replicas;
        this.frontEnd = frontEnd;
    }

    protected BindingProvider putToHandler(SDStore replica) {
        return frontEnd.putToHandler(handlerInfo.getTicket(), replica, handlerInfo.getKey(), handlerInfo.getUserId());
    }

}
