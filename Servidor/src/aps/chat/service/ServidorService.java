package aps.chat.service;

import aps.chat.bean.Chat;
import aps.chat.bean.Chat.Action;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServidorService {

    private ServerSocket serverSocket;
    private Socket socket;
    // É onde será adicionado o novo usuário ao Chat;
    private Map<String, ObjectOutputStream> online = new HashMap<String, ObjectOutputStream>();

    public ServidorService() {
        try {
            serverSocket = new ServerSocket(5555);

            System.out.println("Servidor Conectado na porta " + serverSocket);

            while (true) {
                socket = serverSocket.accept();

                new Thread(new ListenerSocket(socket)).start();
            }
        } catch (IOException ex) {
            Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private class ListenerSocket implements Runnable {

        private ObjectOutputStream output;  //executa a saída de mensagens do servidor
        private ObjectInputStream input;    //executa a saída de mensagens do cliente

        public ListenerSocket(Socket socket) {
            try {
                this.output = new ObjectOutputStream(socket.getOutputStream());
                this.input = new ObjectInputStream(socket.getInputStream());
            } catch (IOException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void run() {
            Chat mensagem = null;

            try {

                while ((mensagem = (Chat) input.readObject()) != null) {
                    Action action = mensagem.getAction();

                    switch (action) {
                        case CONNECT:
                            boolean isConnect = connect(mensagem, output);
                            if (isConnect) {
                                System.out.println("Usuário " + mensagem.getNome() + " entrou.");
                                online.put(mensagem.getNome(), output);
                                usr_online();
                            }
                            break;
                        case DISCONNECT:
                            disconnect(mensagem, output);
                            System.out.println("Usuário " + mensagem.getNome() + " saiu.");
                            usr_online();
                            return;
                        case SEND_ONE:
                            send_one(mensagem, output);
                            break;
                        case SEND_ALL:
                            send_all(mensagem);
                            break;                        
                    }
                }
            } catch (IOException ex) {
                Chat msg = new Chat();
                msg.setNome(mensagem.getNome());
                disconnect(msg, output);
                usr_online();
                System.out.println("Usuário " + mensagem.getNome() + " saiu.");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private boolean connect(Chat mensagem, ObjectOutputStream output) {

        if (online.isEmpty()) {
            mensagem.setMensagem("Tchau!!");
            send_one(mensagem, output);

            return true;
        }

        
            if(online.containsKey(mensagem.getMensagem())){
                mensagem.setMensagem("NO");
                send_one(mensagem, output);
                return false;
            } else {
                mensagem.setMensagem("Tchau!!");
                send_one(mensagem, output);
                return true;
            }
        

    }

    private void disconnect(Chat mensagem, ObjectOutputStream output) {

        online.remove(mensagem.getNome());
        mensagem.setAction(Action.SEND_ONE);

        send_all(mensagem);

    }

    private void send_one(Chat mensagem, ObjectOutputStream output) {
        try {
            output.writeObject(mensagem);
        } catch (IOException ex) {
            Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    private void send_all(Chat mensagem) {
        for (Map.Entry<String, ObjectOutputStream> kv : online.entrySet()) {
            if (!kv.getKey().equals(mensagem.getNome())) {
                mensagem.setAction(Action.SEND_ONE);
                try {
                    kv.getValue().writeObject(mensagem);
                } catch (IOException ex) {
                    Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void usr_online() {
        Set<String> usuarios = new HashSet<String>();
        for (Map.Entry<String, ObjectOutputStream> kv : online.entrySet()) {
            usuarios.add(kv.getKey());
        }
        Chat mensagem = new Chat();
        mensagem.setAction(Action.USR_ONLINE);
        mensagem.setUsrOnline(usuarios);
        for (Map.Entry<String, ObjectOutputStream> kv : online.entrySet()) {
            mensagem.setNome(kv.getKey());
            try {
                kv.getValue().writeObject(mensagem);
            } catch (IOException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

}
