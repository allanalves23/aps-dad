package aps.chat.bean;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Chat implements Serializable {

    private String nome;
    private String mensagem;
    private Set<String> usrOnline = new HashSet<String>();
    private Action action;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Set<String> getUsrOnline() {
        return usrOnline;
    }

    public void setUsrOnline(Set<String> usrOnline) {
        this.usrOnline = usrOnline;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public enum Action {
        CONNECT, DISCONNECT, SEND_ONE, SEND_ALL, USR_ONLINE
    }
}
