package console;

import network.AIClient;
import network.listener.AIClientListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main
{
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static int currentGameNumber = 1;
    private static int wonGames;

    public static void main(String[] args)
    {
        if(args.length != 3)
        {
            log.error("Impossible de démarrer un client sans préciser les arguments suivants : nom de la strégie, nombre de parties à jouer, afficher ou non les logs.");
        }
        else
        {
            String strategyName = args[0];
            int gamesToPlay = Integer.parseInt(args[1]);
            boolean displayLog = Boolean.parseBoolean(args[2]);

            try
            {
                System.out.println("-------------------------");
                System.out.println("ProjectInventors - Client");
                System.out.println("-------------------------");

                Class<?> strategyType = Class.forName(String.format("engine.ai.strategies.%s", strategyName));
                AIClient client = new AIClient(strategyType, displayLog);

                //Écouteur attendant la fin d'une la partie...
                AIClientListener aiClientListener = new AIClientListener() {
                    @Override
                    public void onGameEnded(boolean victory)
                    {
                        if(victory)
                            wonGames++;

                        log.info(String.format("%d sur %d partie(s) terminée(s).", currentGameNumber, gamesToPlay));

                        client.disconnect();
                    }

                    @Override
                    public void onGameClosed()
                    {
                        if(currentGameNumber < gamesToPlay)
                        {
                            currentGameNumber++;
                            client.connect();
                        }
                        else
                        {
                            log.info(String.format("Vous avez gagné %.0f%% des parties (%d/%d) !",
                                    (((float)wonGames / (float)gamesToPlay) * 100),
                                    wonGames, gamesToPlay));
                        }
                    }
                };

                //Ajout de l'écouteur
                client.addListener(aiClientListener);

                client.connect();

                Thread.sleep(Integer.MAX_VALUE);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                log.error(ex.getMessage());
            }
        }
    }
}
