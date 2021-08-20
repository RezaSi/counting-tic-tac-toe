import com.codingame.gameengine.runner.MultiplayerGameRunner;

public class Main {
    public static void main(String[] args) {
        
        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();
        gameRunner.addAgent(
        		Player1.class,             
        		"Reza",
                "https://www.jea.com/cdn/images/avatar/avatar-alt.svg");
        gameRunner.addAgent(Player2.class,
                "Hamid",
                "https://www.jea.com/cdn/images/avatar/avatar-alt.svg");
        
        // gameRunner.addAgent("python3 /home/user/player.py");
        
        // The first league is classic tic-tac-toe
        // gameRunner.setLeagueLevel(1);
        // The second league is ultimate tic-tac-toe
        // gameRunner.setLeagueLevel(2);
        
        gameRunner.start();
    }
}
