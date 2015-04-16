package pt.ulisboa.tecnico.sdis.store.ws.impl;

public class Document {

    private byte[] contents;
    private int size;

    public byte[] getContent() {
        return contents;
    }

    public void setContent(byte[] contents) {
        this.contents = contents;
        if(contents!=null)
            this.size = contents.length;
        else
            size = 0;
    }
    
    public int getSize() {
        if(contents!=null)
            return size;
        else
            return 0;
    }
}
