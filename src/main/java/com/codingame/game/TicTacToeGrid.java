package com.codingame.game;

import java.util.ArrayList;
import java.util.List;

import com.codingame.gameengine.module.entities.Curve;
import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Line;
import com.codingame.gameengine.module.entities.Sprite;
import com.google.inject.Inject;

public class TicTacToeGrid {
    @Inject private GraphicEntityModule graphicEntityModule;

    private String[] images = { "cross.png", "circle.png" };

    private Group entity;
    
    final private int boardSize = 9;

    private int origX;
    private int origY;
    private int cellSize;
    private int[][] grid = new int[boardSize][boardSize];
    protected int winner = 0;

    public List<Action> getValidActions() {
        List<Action> validActions = new ArrayList<>();
        if (winner == 0) {
            for (int x = 0; x < boardSize; x++) {
                for (int y = 0; y < boardSize; y++) {
                    if (grid[x][y] == 0) {
                        validActions.add(new Action(null, x, y));
                    }
                }
            }
        }
        return validActions;
    }

    public int[] play(Action action) throws InvalidAction {
        if (action.row < 0 || action.row >= boardSize || action.col < 0 || action.col >= boardSize || grid[action.row][action.col] != 0) {
            throw new InvalidAction("Invalid move!");
        }

        // update grid
        grid[action.row][action.col] = action.player.getIndex() + 1;
        
        boolean flag = true;
        for(int i = 0; i < boardSize; ++i) {
        	for(int j = 0; j < boardSize; ++j) {
        		if(grid[i][j] == 0) {
        			flag = false;
        		}
        	}
        }
                
        drawPlay(action);
        return countScore();
    }

    private int[] countScore() {
    	int cnt[] = {0 , 0, 0};
        for(int i = 0; i < boardSize; ++i) {
        	for(int j = 0; j < boardSize; ++j) {
        		if(grid[i][j] == 0) {
        			cnt[0] += 1;
        		}
        		// row check
        		if(i + 2 < boardSize && grid[i][j] == grid[i + 1][j] && grid[i + 1][j] == grid[i + 2][j]) {
        			cnt[grid[i][j]] += 1;
        		}
        		// col check
        		if(j + 2 < boardSize && grid[i][j] == grid[i][j + 1] && grid[i][j + 1] == grid[i][j + 2]) {
        			cnt[grid[i][j]] += 1;
        		}
        		
        		// diag-right check
        		if(j + 2 < boardSize && i + 2 < boardSize && grid[i][j] == grid[i + 1][j + 1] && grid[i + 1][j + 1] == grid[i + 2][j + 2]) {
        			cnt[grid[i][j]] += 1;
        		}
        		
        		// diag-left check
        		if(j - 2 >= 0 && i + 2 < boardSize && grid[i][j] == grid[i + 1][j - 1] && grid[i + 1][j - 1] == grid[i + 2][j - 2]) {
        			cnt[grid[i][j]] += 1;
        		}
        	}
        }
        
        return cnt;
    }

    public void draw(int origX, int origY, int cellSize, int lineWidth, int lineColor) {
        this.origX = origX;
        this.origY = origY;
        this.cellSize = cellSize;
        this.entity = graphicEntityModule.createGroup();

        double xs[] = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8};
        double x2s[] = new double[] { 8, 8, 8, 8, 8, 8, 8, 8, 0, 1, 2, 3, 4, 5, 6, 7 };
        double ys[] = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 0, 0, 0, 0, 0, 0, 0, 0 };
        double y2s[] = new double[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 8, 8, 8, 8, 8, 8, 8 };

        for (int i = 0; i < xs.length; ++i) {
            Line line = graphicEntityModule.createLine()
                .setX(convert(origX, cellSize, xs[i] - 0.5))
                .setX2(convert(origX, cellSize, x2s[i] + 0.5))
                .setY(convert(origY, cellSize, ys[i] - 0.5))
                .setY2(convert(origY, cellSize, y2s[i] + 0.5))
                .setLineWidth(lineWidth)
                .setLineColor(lineColor);
            entity.add(line);
        }
    }

    public void drawPlay(Action action) {
        Sprite avatar = graphicEntityModule.createSprite()
            .setX(convert(origX, cellSize, action.col))
            .setY(convert(origY, cellSize, action.row))
            .setImage(images[action.player.getIndex()])
            .setBaseWidth((int) (0.8 * cellSize))
            .setBaseHeight((int) (0.8 * cellSize))
            .setTint(action.player.getColorToken())
            .setAnchor(0.5);

        // Animate arrival
        avatar.setScale(0);
        graphicEntityModule.commitEntityState(0.2, avatar);
        avatar.setScale(1, Curve.ELASTIC);
        graphicEntityModule.commitEntityState(1, avatar);

        this.entity.add(avatar);
    }

    private int convert(int orig, int cellSize, double unit) {
        return (int) (orig + unit * cellSize);
    }

    public void hide() {
        this.entity.setAlpha(0);
        this.entity.setVisible(false);
    }

    public void activate() {
        this.entity.setAlpha(1, Curve.NONE);
        graphicEntityModule.commitEntityState(1, entity);
    }

    public void deactivate() {
        if (winner == 0) {
            this.entity.setAlpha(0.5, Curve.NONE);
            graphicEntityModule.commitEntityState(1, entity);
        }
    }
}