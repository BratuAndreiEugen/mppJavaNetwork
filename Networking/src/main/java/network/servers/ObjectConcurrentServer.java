package network.servers;

import network.objectprotocol.ClientObjectWorker;
import utils.IServices;

import java.net.Socket;

public class ObjectConcurrentServer extends AbsConcurrentServer{

    private IServices server;

    public ObjectConcurrentServer(int port, IServices s) {
        super(port);
        this.server = s;
        System.out.println("ObjectConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client) {
        ClientObjectWorker worker = new ClientObjectWorker(server, client);
        Thread t = new Thread(worker);
        return t;
    }
}
