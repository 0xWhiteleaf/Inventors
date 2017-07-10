package console;

import com.corundumstudio.socketio.SocketIOClient;
import engine.Game;
import network.Server;
import network.ServerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Main
{
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args)
    {
        try
        {
            System.out.println("-------------------------");
            System.out.println("ProjectInventors - Server");
            System.out.println("-------------------------");

            Server inventorsServer = new Server();
            inventorsServer.addListener(new ServerListener()
            {
                @Override
                public void onGameReady(List<SocketIOClient> players)
                {
                    Game game = new Game(players);

                    try
                    {
                        game.init();
                        game.play();
                    }
                    catch (Exception ex)
                    {
                        log.error(ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            });

            inventorsServer.start();
            Thread.sleep(Integer.MAX_VALUE);
            inventorsServer.stop();
        }
        catch (Exception ex)
        {
            log.error(ex.getMessage());
            ex.printStackTrace();
        }
    }
}
