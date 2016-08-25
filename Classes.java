/* 
 * AbDoU-VB Game2048
 *
 */
import java.lang.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class Plan	{

	public static int planLength;
	private int numberOfTileAtBegin;
	private int score, oldScore, winValue; 
	private Tile[][] plan;
	private Tile[][] oldPlan;

	public Plan(GameParameters parameters)	{

		this.numberOfTileAtBegin = parameters.TILE_NO_AT_BEGINNING;
		this.planLength = parameters.PLAN_LENGTH;
		this.winValue = parameters.WIN_VALUE;
		resetGame();
	}
	public int getPlanLength()	{

		return this.planLength;
	}
	public void resetGame()	{

		score = 0;
		oldScore = 0;
		intitialisePlans();
		setPlanRandomly();
		savePlan();
	}
	public Tile getTileAt(int i, int j)	{

		return plan[i][j];
	}
	public void right()	{

		boolean saved = false;
		boolean addNewTile = false;
		for (int i = 0; i < planLength; i++)	{

			Line line = getLine(i);
			if (line.canMoveRight)	{
				if (!saved) savePlan();
				saved = true;
				line.toRight();
				score += line.getScoreInThisLine();
				addNewTile = true;
			}
		}
		if (addNewTile && !isFull())	{
			addNewTile();
		}
	}
	public void left()	{

		boolean saved = false;
		boolean addNewTile = false;
		for (int i = 0; i < planLength; i++)	{

			Line line = getLine(i);
			if (line.canMoveLeft)	{
				if (!saved) savePlan();
				saved = true;
				line.toLeft();
				score += line.getScoreInThisLine();
				addNewTile = true;
			}
		}
		if (addNewTile && !isFull())	{
			addNewTile();
		}
	}
	public void up()	{

		boolean saved = false;
		boolean addNewTile = false;
		for (int i = 0; i < planLength; i++)	{

			Line line = getLineFromColumn(i);
			if (line.canMoveLeft)	{
				if (!saved) savePlan();
				saved = true;
				savePlan();
				line.toLeft();
				score += line.getScoreInThisLine();
				addNewTile = true;
			}
		}
		if (addNewTile && !isFull())	{
			addNewTile();
		}
	}
	public void down()	{

		boolean saved = false;
		boolean addNewTile = false;
		for (int i = 0; i < planLength; i++)	{

			Line line = getLineFromColumn(i);
			if (line.canMoveRight)	{
				if (!saved) savePlan();
				saved = true;
				savePlan();
				line.toRight();
				score += line.getScoreInThisLine();
				addNewTile = true;
			}
		}
		if (addNewTile && !isFull())	{
			addNewTile();
		}
	}
	public void savePlan()	{

		for (int i = 0; i < planLength; i++)	{

			for (int j = 0; j < planLength; j++)	{

				Tile tile = getTileAt(i, j);
				oldPlan[i][j].setValue(tile.getValue());
			}
		}
		oldScore = score;
	}
	public void loadOldPlan()	{

		for (int i = 0; i < planLength; i++)	{

			for (int j = 0; j < planLength; j++)	{

				Tile currentTile = getTileAt(i, j);
				Tile oldTile = oldPlan[i][j];
				currentTile.setValue(oldTile.getValue());
			}
		}
		this.score = oldScore;
	}
	public void printPlan()	{

		System.out.println("\n");
		for (int i = 0; i < planLength; i++)	{

			for (int j = 0; j < planLength; j++)	{

				Tile tile = getTileAt(i, j);
				System.out.print(tile.getValue() + ",   ");
			}
			System.out.println("\n");
		}
		System.out.println("\n\n");
	}
	public boolean isLose()	{

		boolean lose = true;
		for (int i = 0; i < planLength && lose; i++)	{

			Line line = getLine(i);
			lose = (!line.canMoveOnThisLine);
		}
		if (lose)	{
			for (int i = 0; i < planLength && lose; i++)	{

			Line line = getLineFromColumn(i);
			lose = (!line.canMoveOnThisLine);
			}
		}
		return lose;
	}
	public boolean isWin()	{

		return (winValueInPlan() >= winValue);
	}
	public int getScore()	{

		return this.score;
	}
	private void intitialisePlans()	{

		plan = new Tile[planLength][planLength];
		oldPlan = new Tile[planLength][planLength];
		for (int i = 0; i < planLength; i++)	{

			for (int j = 0; j < planLength; j++)	{

				plan[i][j] = new Tile(0);
				oldPlan[i][j] = new Tile(0);
			}
		}
	}
	public void addNewTile()	{

		boolean done = false;
		while (!done)	{

			int i = (int)(Math.random() * planLength);
			int j = (int)(Math.random() * planLength);
			Tile tile = getTileAt(i, j);
			if (tile.isEmpty())	{
				tile.setValue(randomValue());
				done = true;
			}
		}
	}
	private void setPlanRandomly()	{

		for (int k = 0; k < numberOfTileAtBegin; k++)	{

			int i = (int)(Math.random() * (planLength));
			int j = (int)(Math.random() * (planLength));
			getTileAt(i, j).setValue(randomValue());
		}
	}
	private static int randomValue()	{

		int r = (int)(Math.random() * 10);
		if (r <= 8) return 2;
		else return 4;
	}
	private Line getLine(int i)	{

		Line line = new Line(plan[i]);
		return line;
	}
	private Line getLineFromColumn(int index)	{

		Tile[] columnArr = new Tile[planLength];
		for (int i = 0; i < planLength; i++)	{

			columnArr[i] = plan[i][index];
		}
		Line line = new Line(columnArr);
		return line;
	}
	private boolean isFull()	{

		boolean full = true;
		for (int i = 0; i < planLength && full; i++)	{

			for (int j = 0; j < planLength && full; j++)	{

				full = (!getTileAt(i, j).isEmpty());
			}
		}
		return full;
	}
	private int winValueInPlan()	{

		int winValueInPlan = 0;
		for (int i = 0; i < planLength; i++)	{

			for (int j = 0; j < planLength; j++)	{

				if (getTileAt(i, j).getValue() > winValueInPlan)	{
					winValueInPlan = getTileAt(i, j).getValue();
				}
			}
		}
		return winValueInPlan;
	}
}
class Line	{

	private Tile[] line;
	private final int lineLength;
	public boolean canMoveOnThisLine, canMoveLeft, canMoveRight;
	private int scoreInThisLine;
	public Line(Tile[] lineFromPlan)	{

		scoreInThisLine = 0;
		lineLength = lineFromPlan.length;
		line = new Tile[lineLength];
		for (int i = 0; i < lineLength; i++)	{

			this.line[i] = lineFromPlan[i]; 
		}
		canMoveLeft = canMoveLeft();
		canMoveRight = canMoveRight();
		canMoveOnThisLine = canMoveRight || canMoveLeft();
	}
	public Tile getTileAt(int i)	{

		return line[i];
	}
	public boolean canMoveLeft()	{

		boolean canMove = false;
		for (int i = 0; i < lineLength - 1 && !canMove; i++)	{
			Tile currentTile = getTileAt(i);
			Tile nextTile = getTileAt(i + 1);
			canMove = ((currentTile.getValue() == nextTile.getValue()) && (currentTile.getValue() != 0)) ||
						(currentTile.getValue() == 0 && nextTile.getValue() != 0);
		}
		return canMove;
	}
	public boolean canMoveRight()	{

		boolean canMove = false;
		for (int i = lineLength - 1; i > 0 && !canMove; i--)	{
			Tile currentTile = getTileAt(i);
			Tile nextTile = getTileAt(i - 1);
			canMove = ((currentTile.getValue() == nextTile.getValue()) && (currentTile.getValue() != 0)) ||
						(currentTile.getValue() == 0 && nextTile.getValue() != 0);
		}
		return canMove;
	}
	public int getScoreInThisLine()	{

		return this.scoreInThisLine;
	}
	public void toLeft()	{
		
		for (int i = 0; i < line.length; i++)	{

			Tile currentTile = getTileAt(i);
			boolean done = false;
			for (int j = i + 1; j < line.length && !done; j++)	{

				Tile nextTile = getTileAt(j);
				if (currentTile.getValue() == 0 && nextTile.getValue() != 0)	{
					currentTile.setValue(nextTile.getValue());
					nextTile.setValue(0);
					done = true;
					i--;
				}
				else if (currentTile.getValue() != 0)	{
					if (nextTile.getValue() != 0 && nextTile.getValue() != currentTile.getValue())	{
						done = true;
					}
					else if (currentTile.getValue() == nextTile.getValue())	{
						currentTile.setValue(nextTile.getValue() + currentTile.getValue());
						nextTile.setValue(0);
						done = true;
						scoreInThisLine += nextTile.getValue() + currentTile.getValue();
					}
				}
			}
		}
	}
	public void toRight()	{

		for (int i = line.length - 1; i >= 0; i--)	{

			Tile currentTile = getTileAt(i);
			boolean done = false;
			for (int j = i - 1; j >= 0 && !done; j--)	{

				Tile nextTile = getTileAt(j);
				if (currentTile.getValue() == 0 && nextTile.getValue() != 0)	{
					currentTile.setValue(nextTile.getValue());
					nextTile.setValue(0);
					done = true;
					i++;
				}
				else if (currentTile.getValue() != 0)	{
					if (nextTile.getValue() != 0 && nextTile.getValue() != currentTile.getValue())	{
						done = true;
					}
					else if (currentTile.getValue() == nextTile.getValue())	{
						currentTile.setValue(nextTile.getValue() + currentTile.getValue());
						nextTile.setValue(0);
						done = true;
						scoreInThisLine += nextTile.getValue() + currentTile.getValue();
					}
				}
			}
		}
	}
}
class Tile	{

	private static final Color[] colorArr = {new Color(0xcdc1b5), new Color(0xeee4da), new Color(0xede0c8),
		new Color(0xf2b179), new Color(0xf59563), new Color(0xf67c5f), new Color(0xf65e3b),
		new Color(0xedcf72), new Color(0xedcc61), new Color(0xedc850), new Color(0xedc53f), 
		new Color(0xedc22e), new Color(0x808080)};

	public Color color;
	public Font font;
	public Color fontColor;
	private int value;

	public Tile(int value)	{

		this.color = colorArr[0];
		fontColor = new Color(0x776e65);
		this.font = new Font("Arial", Font.BOLD, 36);
		this.value = value;
		refreshTileFields();
	}
	public int getValue()	{

		return this.value;
	}
	public void setValue(int newValue)	{

		this.value = newValue;
		refreshTileFields();
	}
	public boolean isEmpty()	{

		return (this.value == 0);
	}
	private void refreshTileFields()	{

		//Refresh color
		if (value != 0)	{
			color = colorArr[(int)(Math.log(value) / Math.log(2))];
		}
		else color = colorArr[0];
		//Refresh font
		int fontSize = value < 100 ? 36 : value < 1000 ? 32 : 24;
		this.font = this.font.deriveFont((float)fontSize);
		//Refresh font color
		fontColor = value < 16 ? new Color(0x776e65) :  new Color(0xf9f6f2);
	}
}
class GameParameters implements Cloneable	{

	public int TILE_NO_AT_BEGINNING, PLAN_LENGTH, WIN_VALUE, TILE_SIZE, TILES_MARGIN;
	public Color COMPONENTS_BG_COLOR, PLAN_BG_COLOR, WIN_BG_COLOR, LOSE_BG_COLOR;
	public String GAME_TITLE;
	public Font FONT;

	private boolean WIN, LOSE;
	private int SCORE;
	public void loadDefaultParameters()	{

		GAME_TITLE = "2048";
		TILE_NO_AT_BEGINNING = 2;
		PLAN_LENGTH = 4;
		WIN_VALUE = 2048;
		TILE_SIZE = 64;
		TILES_MARGIN = 10;
		FONT = new Font("Arial", Font.BOLD, 12);
		COMPONENTS_BG_COLOR = Color.white;
		PLAN_BG_COLOR = new Color(0xB9AEA0);
		WIN_BG_COLOR = new Color(238, 188, 55, 50);
		LOSE_BG_COLOR = new Color(128, 128, 128, 127);
		SCORE = 0;
		WIN = false;
		LOSE = false;
	}
	public Object clone()	{

		Object o = null;
		try	{
			o = super.clone();
		}
		catch (CloneNotSupportedException e)	{
			e.printStackTrace(System.err);
		}
		return o;
	}
	public int getScore()	{

		return this.SCORE;
	}
	public void setScore(int newScore)	{

		this.SCORE = newScore;
	}
	public void setWin(boolean win)	{

		this.WIN = win;
	}
	public boolean isWin()	{

		return this.WIN;
	}
	public void setLose(boolean lose)	{

		this.LOSE = lose;
	}
	public boolean isLose()	{

		return this.LOSE;
	}
}