package aps.chat.service;

import aps.chat.bean.Chat;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ClienteService {
    
    private Socket socket;
    private ObjectOutputStream output;
    
    public Socket connect(){
        try {
            this.socket = new Socket("localhost", 5555);
            this.output = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ClienteService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return socket;
    }
    
    public void send(Chat mensagem){
        try {
            output.writeObject(mensagem);
        } catch (IOException ex) {
            Logger.getLogger(ClienteService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
