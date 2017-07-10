package network;

import com.corundumstudio.socketio.SocketIOClient;

import java.util.List;
import java.util.UUID;

/**
 * Interface d'un écouteur du serveur.
 * @author Noé Mourton-Comte
 */
public interface ServerListener
{
    void onGameReady(List<SocketIOClient> players);
}
