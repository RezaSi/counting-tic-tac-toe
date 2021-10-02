package com.codingame.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.codingame.gameengine.core.AbstractPlayer.TimeoutException;
import com.codingame.gameengine.core.AbstractReferee;
import com.codingame.gameengine.core.GameManager;
import com.codingame.gameengine.core.MultiplayerGameManager;
import com.codingame.gameengine.module.endscreen.EndScreenModule;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Sprite;
import com.codingame.gameengine.module.entities.Circle;
import com.codingame.gameengine.module.entities.Text;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class Referee extends AbstractReferee {
    @Inject private MultiplayerGameManager<Player> gameManager;
    @Inject private GraphicEntityModule graphicEntityModule;
    @Inject private Provider<TicTacToeGrid> ticTacToeGridProvider;
    @Inject private EndScreenModule endScreenModule;

    final private int boardSize = 10;
    private TicTacToeGrid masterGrid;
    private TicTacToeGrid[][] smallGrids;
    private Action lastAction = null;
    private List<Action> validActions;
    private Random random;
    private Text[] score;
    
    @Override
    public void init() {
        random = new Random(gameManager.getSeed());

        drawBackground();
        drawHud();
        drawGrids();

        gameManager.setFrameDuration(500);
        gameManager.setMaxTurns(boardSize * boardSize);
        gameManager.setTurnMaxTime(100);
        validActions = getValidActions();
    }

    private void drawBackground() {
        graphicEntityModule.createSprite()
                .setImage("Background.jpg")
                .setAnchor(0);
    }

    private void drawGrids() {
        int bigCellSize = 67;
        int bigOrigX = (int) Math.round(1920 / 2 - 300);
        int bigOrigY = (int) Math.round(1080 / 2 - 300);
        masterGrid = ticTacToeGridProvider.get();
        masterGrid.draw(bigOrigX, bigOrigY, bigCellSize, 5, 0x27ae60);

        graphicEntityModule
            .createSprite()
            .setImage("board_border.png")
            .setX(1920 / 2)
            .setY(1080 / 2)
            .setAnchor(0.5);
    }
    
    private void drawHud() {
    	score = new Text[2];
        for (Player player : gameManager.getPlayers()) {
            int x = player.getIndex() == 0 ? 280 : 1920 - 280;
            int y = 500;

            graphicEntityModule
                    .createRectangle()
                    .setWidth(140)
                    .setHeight(140)
                    .setX(x - 70)
                    .setY(y - 70)
                    .setLineWidth(0)
                    .setFillColor(player.getColorToken());

            graphicEntityModule
                    .createRectangle()
                    .setWidth(120)
                    .setHeight(120)
                    .setX(x - 60)
                    .setY(y - 60)
                    .setLineWidth(0)
                    .setFillColor(0xffffff);

            Text text = graphicEntityModule.createText(player.getNicknameToken())
                    .setX(x)
                    .setY(y + 120)
                    .setZIndex(20)
                    .setFontSize(40)
                    .setFillColor(0xffffff)
                    .setAnchor(0.5);
            
            Circle scoreCircle = graphicEntityModule.createCircle().setRadius(50).setX(x).setY(y + 220).setFillColor(0x34495e);
            
            score[player.getIndex()] = graphicEntityModule.createText(Integer.toString(player.getScore()))
                    .setX(x)
                    .setY(y + 220)
                    .setZIndex(20)
                    .setFontSize(50)
                    .setFillColor(0xbdc3c7)
                    .setAnchor(0.5);

            Sprite avatar = graphicEntityModule.createSprite()
                    .setX(x)
                    .setY(y)
                    .setZIndex(20)
                    .setImage(player.getAvatarToken())
                    .setAnchor(0.5)
                    .setBaseHeight(116)
                    .setBaseWidth(116);

            player.hud = graphicEntityModule.createGroup(text, score[player.getIndex()], avatar);
        }
    }

    private void sendInputs(Player player, List<Action> validActions) {
        // last action
        if (lastAction != null) {
            player.sendInputLine(lastAction.toString());
        } else {
            player.sendInputLine("-1 -1");
        }

        // valid actions
        player.sendInputLine(Integer.toString(validActions.size()));
        for (Action action : validActions) {
            player.sendInputLine(action.toString());    
        }
    }

    private void setWinner(Player player) {
        endGame();
    }

    private List<Action> getValidActions() {
        List<Action> validActions;
        validActions = masterGrid.getValidActions();
        Collections.shuffle(validActions, random);
        return validActions;
    }

    @Override
    public void gameTurn(int turn) {
        Player player = gameManager.getPlayer(turn % gameManager.getPlayerCount());
        
        sendInputs(player, validActions);
        player.execute();

        // Read inputs
        try {
            final Action action = player.getAction();
            gameManager.addToGameSummary(String.format("Player %s played (%d %d)", action.player.getNicknameToken(), action.row, action.col));

            if (!validActions.contains(action)) {
                throw new InvalidAction("Invalid action.");
            }

            lastAction = action;

            final TicTacToeGrid grid;
            int[] scores = masterGrid.play(action);
            gameManager.addToGameSummary(String.format("{ Score %s -> %d }", gameManager.getPlayer(0).getNicknameToken(), scores[1]));
            gameManager.addToGameSummary(String.format("{ Score %s -> %d }", gameManager.getPlayer(1).getNicknameToken(), scores[2]));
            
            gameManager.getPlayer(0).setScore(scores[1]);
            gameManager.getPlayer(1).setScore(scores[2]);
            
            gameManager.getPlayer(player.getIndex()).hud.remove(score[player.getIndex()]);
            score[player.getIndex()].setText(Integer.toString(player.getScore()));
            gameManager.getPlayer(player.getIndex()).hud.add(score[player.getIndex()]);
            
            if (scores[0] == 0) {
            	if(scores[1] > scores[2]) {
            		setWinner(gameManager.getPlayer(0));
            		
            	}else {
            		setWinner(gameManager.getPlayer(1));
            	}
            }

            validActions = getValidActions();
            if (validActions.isEmpty()) {
                endGame();
            }
        } catch (NumberFormatException e) {
            player.deactivate("Wrong output!");
            player.setScore(-1);
            endGame();
        } catch (TimeoutException e) {
            gameManager.addToGameSummary(GameManager.formatErrorMessage(player.getNicknameToken() + " timeout!"));
            player.deactivate(player.getNicknameToken() + " timeout!");
            player.setScore(-1);
            endGame();
        } catch (InvalidAction e) {
            player.deactivate(e.getMessage());
            player.setScore(-1);
            endGame();
        }
    }

    private void endGame() {
        gameManager.endGame();
        
        int[] scores = { gameManager.getPlayer(0).getScore(), gameManager.getPlayer(1).getScore() };
        String[] text = new String[2];
        if(scores[0] > scores[1]) {
            gameManager.addToGameSummary(gameManager.formatErrorMessage(gameManager.getPlayer(0).getNicknameToken() + " won"));
            gameManager.addTooltip(gameManager.getPlayer(0), gameManager.getPlayer(0).getNicknameToken() + " won");
            text[0] = "Won ( " + String.valueOf(scores[0]) + " )";
            text[1] = "Lost ( " + String.valueOf(scores[1]) + " )";
            gameManager.getPlayer(1).hud.setAlpha(0.3);
        } else if(scores[0] < scores[1]) {
            gameManager.addToGameSummary(gameManager.formatErrorMessage(gameManager.getPlayer(1).getNicknameToken() + " won"));
            gameManager.addTooltip(gameManager.getPlayer(1), gameManager.getPlayer(1).getNicknameToken() + " won");
            text[0] = "Lost ( " + String.valueOf(scores[0]) + " )";
            text[1] = "Won ( " + String.valueOf(scores[1]) + " )";
            gameManager.getPlayer(0).hud.setAlpha(0.3);
        } else {
        	gameManager.addToGameSummary(gameManager.formatErrorMessage("Game is drawn"));
        	gameManager.addTooltip(gameManager.getPlayer(1), "Draw");
            text[0] = "Draw ( " + String.valueOf(scores[0]) + " )";
            text[1] = "Draw ( " + String.valueOf(scores[1]) + " )";
        }

        endScreenModule.setScores(scores, text);
    }
}
