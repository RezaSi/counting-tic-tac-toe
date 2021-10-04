import com.codingame.gameengine.runner.MultiplayerGameRunner;

public class Main {
	static String DEFAULT_AI = "python3 config/Boss.py";
    static String BOSS_WOOD1 = "python3 config/level1/Boss.py";
	static String BOSS_WOOD2 = "python3 config/level1/Boss.py";
    public static void main(String[] args) {
        
        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();
        gameRunner.setLeagueLevel(2);
        gameRunner.addAgent(
        		BOSS_WOOD1,             
        		"Reza",
                "https://www.jea.com/cdn/images/avatar/avatar-alt.svg");
        gameRunner.addAgent(
        		Player1.class,
                "Hamid",
                "https://www.jea.com/cdn/images/avatar/avatar-alt.svg");
        
        gameRunner.start();
    }
}
