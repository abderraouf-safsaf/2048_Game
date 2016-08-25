/* 
 * AbDoU-VB Game2048
 *
 */
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.BorderFactory;
import javax.swing.text.MaskFormatter;
import javax.swing.JSpinner.*;

public class Game2048	{

	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(() -> {
            GameWindow gameWindow1 = new GameWindow();
            gameWindow1.setVisible(true);
        });
	}
}
class GameWindow extends JFrame implements MouseListener, ActionListener, KeyListener	{

	private final String GAME_TITLE = "2048";
	private GameParameters gameParameters;

	JPopupMenu popupMenu;
	JMenuItem parametersItem, closeItem;
	private Box vBox;
	private PlanPanel planPanel;
	private HeadPanel headPanel;

	ParametersDialog parametersDialog;
	public GameWindow()	{

		setDefaultGameParameters();

		Container frameContainer = getContentPane();
		putComponentsInto(frameContainer);
		setDefautFrameParameters(frameContainer);

		refreshFrameSize();
		setFrameLocation();
	}
	private void setDefautFrameParameters(Container c)	{

		setTitle(gameParameters.GAME_TITLE);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		ImageIcon icon = new ImageIcon("Ressources/2048icon.png");
		setIconImage(icon.getImage());
		c.setBackground(gameParameters.COMPONENTS_BG_COLOR);
		addMouseListener(this);
		addKeyListener(this);
	}
	private void putComponentsInto(Container c)	{

		popupMenu = new JPopupMenu();

		parametersItem = new JMenuItem("Parameters");
		parametersItem.addActionListener(this);
		popupMenu.add(parametersItem);

		popupMenu.addSeparator();

		closeItem = new JMenuItem("Close");
		closeItem.addActionListener(this);
		popupMenu.add(closeItem);

		vBox = Box.createVerticalBox();
		vBox.setBackground(gameParameters.COMPONENTS_BG_COLOR);
		c.add(vBox);

		headPanel = new HeadPanel(gameParameters);
		headPanel.setBackground(gameParameters.COMPONENTS_BG_COLOR);
		vBox.add(headPanel);

		planPanel = new PlanPanel(gameParameters);
		planPanel.setBackground(gameParameters.COMPONENTS_BG_COLOR);
		vBox.add(planPanel);

	}
	public void refreshFrameSize()	{

		int headPanelHeight = headPanel.getPreferredSize().height;
		int planPanelHeight = planPanel.getPreferredSize().height;

		int width = planPanel.getPlanDimension().width;
		int height = headPanelHeight + planPanelHeight;

		setSize(width, height);
	}
	private void setFrameLocation()	{

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
        int x = ((int)rect.getMaxX()) / 2 - getWidth() / 2;
        int y = 0;
        setLocation(x, y);
	}
	public void newGame(GameParameters newGameParameters)	{

		this.gameParameters = newGameParameters;
		planPanel.newPlan(gameParameters);
		refreshParameters();
		refreshFrameSize();
	}
	private void setDefaultGameParameters()	{

		GameParameters defaultGameParameters = new GameParameters();
		defaultGameParameters.loadDefaultParameters();
		gameParameters = defaultGameParameters;
	}
	public void keyPressed(KeyEvent ev)	{

		int keyPressed = ev.getKeyCode();
		Plan plan = planPanel.getPlan();
		if (!gameParameters.isWin() && !gameParameters.isLose())	{

			switch (keyPressed) {
	            case KeyEvent.VK_LEFT:
	            	plan.left();
	            	break;
	            case KeyEvent.VK_RIGHT:
	            	plan.right();
	            	break;
	            case KeyEvent.VK_DOWN:
	            	plan.down();
	            	break;
	            case KeyEvent.VK_UP:
		            plan.up();
		            break;
	        }
		}
		switch (keyPressed) {
			case KeyEvent.VK_SPACE:
	            plan.resetGame();
	             break;
	        case KeyEvent.VK_ESCAPE:
	        	plan.loadOldPlan();
	        	break;
		}
		refreshParameters();
		repaint();
	}
	public void keyReleased(KeyEvent ev)	{	}
	public void keyTyped(KeyEvent ev)	{	}
	public void actionPerformed(ActionEvent ev)	{

		Object source = ev.getSource();
		if (source == parametersItem)	{
			parametersDialog = new ParametersDialog(this);
			GameParameters newGameParameters = parametersDialog.showParametersDialog(gameParameters);
			if (newGameParameters != null) newGame(newGameParameters);
		}
		else if (source == closeItem)	{
			this.setVisible(false);
			this.dispose();
		}
	}
	public void mousePressed(MouseEvent ev)	{

		if (ev.isPopupTrigger())	{
			int x = ev.getX();
			int y = ev.getY();
			popupMenu.show(this, x, y);
		}
	}
	public void mouseClicked(MouseEvent ev)	{	}
	public void mouseReleased(MouseEvent ev)	{
	if (ev.isPopupTrigger())	{
			int x = ev.getX();
			int y = ev.getY();
			popupMenu.show(this, x, y);
		}	
	}
	public void mouseEntered(MouseEvent ev)	{	}
	public void mouseExited(MouseEvent ev)	{	}
	private void refreshParameters()	{

		int score = planPanel.getScore();
		gameParameters.setScore(score);
		gameParameters.setWin(planPanel.isWin());
		gameParameters.setLose(planPanel.isLose());

		refreshHeadPanelParameters(gameParameters);
	}
	private void refreshHeadPanelParameters(GameParameters newParameters)	{

		headPanel.refreshHeadPanelParameters(newParameters);
		headPanel.repaint();
	}
}
class HeadPanel extends JPanel	{

	JPanel titlePanel, scorePanel;

	GameParameters parameters;
	public HeadPanel(GameParameters gameParameters)	{

		this.parameters = gameParameters;
		setLayout(new BorderLayout());
		putComponents();

	}
	public void refreshHeadPanelParameters(GameParameters gameParameters)	{

		this.parameters = gameParameters;
	}
	private void putComponents()	{

		titlePanel = new JPanel()	{
			public void paintComponent(Graphics graphics)	{

				Graphics2D g = UtilMethods.setGraphics(graphics);
				drawTitle(g);
			}
		};
		titlePanel.setPreferredSize(new Dimension(180, 80));
		add(titlePanel, "West");

		scorePanel = new JPanel()	{
			public void paintComponent(Graphics graphics)	{

				Graphics2D g = UtilMethods.setGraphics(graphics);
				drawScore(g);
			}
		};
		scorePanel.setPreferredSize(new Dimension(105, 80));
		add(scorePanel, "East");
	}
	private void drawTitle(Graphics2D g)	{
		
		g.setColor(parameters.PLAN_BG_COLOR.darker());
		g.setFont(parameters.FONT.deriveFont((float)60));
		g.drawString(parameters.GAME_TITLE, 10, 65);
	}
	private void drawScore(Graphics2D g)	{

		int rectWidth = 100, rectHeight = 50;
    	g.setColor(parameters.PLAN_BG_COLOR);
		g.fillRoundRect(0, 8, rectWidth, rectHeight, 10, 10);

		g.setColor(Color.white);
		g.setFont(parameters.FONT.deriveFont((float)12));
		FontMetrics fm = g.getFontMetrics();
		int x = 35, y = 28;
		g.drawString("Score: ", x, y);
		x = 15;
		y += fm.getHeight() + 10;
		g.setFont(parameters.FONT.deriveFont((float)18));
		String score = String.valueOf(parameters.getScore());
		g.drawString(score, x, y);
	}
}
class ParametersDialog extends JDialog implements ActionListener	{

	Box vBox, buttonsBox;
	JPanel headerPanel, generalParametersPanel, styleParametersPanel, buttonsPanel ;
	JSpinner planLengthSpinner, tileNoAtBeginningSpinner, winValueSpinner, tileSizeSpinner, tileMarginSpinner;
	JButton newGameButton, cancelButton, resetButton;
	JFormattedTextField colorTextField;

	GameParameters parameters, newParameters;

	boolean newGame = false;
	public ParametersDialog(GameWindow gameWindow)	{

		super(gameWindow, true);
		setLocationRelativeTo(gameWindow);
		setResizable(false);
	}
	private void putComponents()	{

		vBox = Box.createVerticalBox();
		this.add(vBox);

		headerPanel = new JPanel(new GridBagLayout());
		vBox.add(headerPanel);

		UtilMethods.addComponent(headerPanel, new JLabel("Parameters: "), 0, 0, 1, 1, GridBagConstraints.LAST_LINE_START);
		vBox.add(Box.createVerticalStrut(10));

		generalParametersPanel = new JPanel(new GridBagLayout());
		generalParametersPanel.setBorder(BorderFactory.createTitledBorder("General"));
		vBox.add(generalParametersPanel);

		UtilMethods.addComponent(generalParametersPanel, new JLabel("Plan length:"), 0, 0, 1, 1, GridBagConstraints.LINE_END);
		UtilMethods.addComponent(generalParametersPanel, new JLabel("Number of tiles at beginning:"), 0, 1, 1, 1, GridBagConstraints.LINE_END);
		UtilMethods.addComponent(generalParametersPanel, new JLabel("Win value:"), 0, 2, 1, 1, GridBagConstraints.LINE_END);

		planLengthSpinner = createNumberSpinner(4, 4, 10, 1);
		UtilMethods.addComponent(generalParametersPanel, planLengthSpinner, 1, 0, 1, 1, GridBagConstraints.LINE_START);

		tileNoAtBeginningSpinner = createNumberSpinner(1, 1, 16, 1);
		UtilMethods.addComponent(generalParametersPanel, tileNoAtBeginningSpinner, 1, 1, 1, 1, GridBagConstraints.LINE_START);

		winValueSpinner = createNumberSpinner(2048, 2048, 4096, 2048);
		
		UtilMethods.addComponent(generalParametersPanel, winValueSpinner, 1, 2, 1, 1, GridBagConstraints.LINE_START);

		styleParametersPanel = new JPanel(new GridBagLayout());
		styleParametersPanel.setBorder(BorderFactory.createTitledBorder("Style"));
		vBox.add(styleParametersPanel);

		UtilMethods.addComponent(styleParametersPanel, new JLabel("Tile size:"), 0, 0, 1, 1, GridBagConstraints.LINE_END);
		UtilMethods.addComponent(styleParametersPanel, new JLabel("Tile margin:"), 0, 1, 1, 1, GridBagConstraints.LINE_END);
		UtilMethods.addComponent(styleParametersPanel, new JLabel("Background color:"), 0, 2, 1, 1, GridBagConstraints.LINE_END);

		tileSizeSpinner = createNumberSpinner(64, 64, 100, 1);
		UtilMethods.addComponent(styleParametersPanel, tileSizeSpinner, 1, 0, 1, 1, GridBagConstraints.LINE_START);

		tileMarginSpinner = createNumberSpinner(1, 1, 50, 1);
		UtilMethods.addComponent(styleParametersPanel, tileMarginSpinner, 1, 1, 1, 1, GridBagConstraints.LINE_START);

		colorTextField = new JFormattedTextField(createFormatter("0xHHHHHH"));
		colorTextField.setColumns(8);
		UtilMethods.addComponent(styleParametersPanel, colorTextField, 1, 2, 1, 1, GridBagConstraints.LINE_START);

		vBox.add(Box.createVerticalStrut(10));

		buttonsPanel = new JPanel(new GridBagLayout());
		vBox.add(buttonsPanel);

		resetButton = new JButton("Reset");
		resetButton.addActionListener(this);
		UtilMethods.addComponent(buttonsPanel, resetButton, 0, 0, 1, 1, GridBagConstraints.CENTER);
		buttonsBox = Box.createHorizontalBox();
		UtilMethods.addComponent(buttonsPanel, buttonsBox, 1, 0, 1, 1, GridBagConstraints.LAST_LINE_END);

		newGameButton = new JButton("New game");
		newGameButton.addActionListener(this);
		buttonsBox.add(newGameButton);

		buttonsBox.add(Box.createHorizontalStrut(10));

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);
		buttonsBox.add(cancelButton);

		vBox.add(Box.createVerticalStrut(2));
	}
	public GameParameters showParametersDialog(GameParameters currentParameters)	{

		parameters = currentParameters;
		setTitle(parameters.GAME_TITLE + " - Parameters");
		putComponents();
		loadParametersIntoFields(parameters);
		pack();
		setVisible(true);
		if (newGame) return newParameters;
		else return null;
	}
	public void actionPerformed(ActionEvent e)	{

		Object source = e.getSource();
		if (source == newGameButton)	{
			newGame = true;
			newParameters = getNewGameParameters();
			setVisible(false);
		}
		else if (source == resetButton)	{
			GameParameters defaultGameParameters = new GameParameters();
			defaultGameParameters.loadDefaultParameters();
			loadParametersIntoFields(defaultGameParameters);
		}
		else if (source == cancelButton)	{
			setVisible(false);
		}
	}
	private GameParameters getNewGameParameters()	{

		GameParameters newParameters = (GameParameters)parameters.clone();

		newParameters.TILE_NO_AT_BEGINNING = (int)tileNoAtBeginningSpinner.getValue();
		newParameters.PLAN_LENGTH = (int)planLengthSpinner.getValue();
		newParameters.WIN_VALUE = (int)winValueSpinner.getValue();
		newParameters.TILE_SIZE = (int)tileSizeSpinner.getValue();
		newParameters.TILES_MARGIN = (int)tileMarginSpinner.getValue();
		String newColorHexStr = colorTextField.getText();
		if (newColorHexStr.length() == 8)	{
			
			int newColorInt = Integer.parseInt(newColorHexStr.substring(2), 16);
			newParameters.PLAN_BG_COLOR = new Color(newColorInt);
		}

		return newParameters;
	}
	private void loadParametersIntoFields(GameParameters param)	{

		planLengthSpinner.setValue(param.PLAN_LENGTH);
		tileNoAtBeginningSpinner.setValue(param.TILE_NO_AT_BEGINNING);
		winValueSpinner.setValue(param.WIN_VALUE);
		tileSizeSpinner.setValue(param.TILE_SIZE);
		tileMarginSpinner.setValue(param.TILES_MARGIN);

		String bgColorHex = String.format("0x%06X", param.PLAN_BG_COLOR.getRGB() & 0x00FFFFFF);
		colorTextField.setValue(bgColorHex);
	}
	private JSpinner createNumberSpinner(int currentValue, int minValue, int maxValue, int step)	{

		SpinnerModel model = new SpinnerNumberModel(currentValue, minValue, maxValue, step);
		JSpinner spinner = new JSpinner(model);
		JFormattedTextField spinnerTextField = ((NumberEditor)spinner.getEditor()).getTextField();
		spinnerTextField.setEditable(false);
		spinnerTextField.setBackground(Color.white);

		return spinner;
	}
	private static MaskFormatter createFormatter(String mask)	{

		MaskFormatter formatter = null;
		try	{
			formatter = new MaskFormatter(mask);
		}
		catch (Exception e)	{	}
		return formatter;
	}
}
class PlanPanel extends JPanel	{

	private GameParameters parameters;
	private Plan plan;

	public PlanPanel(GameParameters parameters)	{

		this.parameters = parameters;
		this.plan = new Plan(parameters);
		refreshPlanPanelSize();
	}
	public void newPlan(GameParameters newPlanParameters)	{

		parameters = newPlanParameters;
		plan = new Plan(newPlanParameters);
		repaint();
		refreshPlanPanelSize();
	}
	public Dimension getPlanDimension()	{

		int edge = (plan.planLength * (parameters.TILE_SIZE + parameters.TILES_MARGIN) + parameters.TILES_MARGIN);
		Dimension dimension = new Dimension(edge, edge);
		return dimension;
	}
	public void refreshPlanPanelSize()	{

		int width = getPlanDimension().width;
		int height = getPlanDimension().height + 5;

		Dimension planPanelDimesion = new Dimension(width, height);
		setPreferredSize(planPanelDimesion);
	}
	public void paintComponent(Graphics graphics)	{

		super.paintComponent(graphics);
		Graphics2D g = UtilMethods.setGraphics(graphics);

		int planBgWidth = getPlanDimension().width;
		int planBgHeight = getPlanDimension().height;
		drawPlanBG(g, planBgWidth, planBgHeight);
		drawPlan(g);
		if (parameters.isWin())	{
			g.setColor(parameters.WIN_BG_COLOR);
			drawGameOverRect(g, planBgWidth, planBgHeight);
			drawMessage(g, "You win!");
		}
		else if (parameters.isLose())	{
			g.setColor(parameters.LOSE_BG_COLOR);
			drawGameOverRect(g, planBgWidth, planBgHeight);
			drawMessage(g, "Game over!");
		}
	}
	public Plan getPlan()	{

		return this.plan;
	}
	public int getScore()	{

		return plan.getScore();
	}
	public boolean isWin()	{

		return plan.isWin();
	}
	public boolean isLose()	{

		return plan.isLose();
	}
	public void drawMessage(Graphics2D g, String message)	{

		int planBgWidth = getPlanDimension().width;
		int planBgHeight = getPlanDimension().height;

		g.setColor(Color.white);
		Font winMessageFont = parameters.FONT.deriveFont((float)55);
		g.setFont(winMessageFont);

		final FontMetrics fm = getFontMetrics(winMessageFont);
   		int messageWidth = fm.stringWidth(message);
   		int messageHeight = fm.getHeight();

   		int x = (planBgWidth / 2) - (messageWidth / 2);
		int y = (planBgHeight / 2);
		
		g.drawString(message, x, y);
	}
	private void drawPlanBG(Graphics2D g, int width, int height)	{

		g.setColor(parameters.PLAN_BG_COLOR);
		g.fillRoundRect(0, 0, width, height, 10, 10);
	}
	private void drawPlan(Graphics2D g)	{

		for (int j = 0; j < parameters.PLAN_LENGTH; j++)	{

			for (int i = 0; i < parameters.PLAN_LENGTH; i++)	{

				Tile tile_j_i = plan.getTileAt(j, i);
				drawTile(g, tile_j_i, i, j);
			}
		}
	}
	private void drawTile(Graphics2D g, Tile tile, int x, int y)	{
		
    	int xOffset = offsetCoors(x);
    	int yOffset = offsetCoors(y);
		g.setColor(tile.color);
		g.fillRoundRect(xOffset, yOffset, parameters.TILE_SIZE, parameters.TILE_SIZE, 14, 14);

		int value = tile.getValue();
		g.setColor(tile.fontColor);
		g.setFont(tile.font);
		String s = String.valueOf(value);
    	final FontMetrics fm = getFontMetrics(tile.font);
   		final int w = fm.stringWidth(s);
    	final int h = -(int) fm.getLineMetrics(s, g).getBaselineOffsets()[2];

	    if (value != 0)	{
	     	g.drawString(s, xOffset + (parameters.TILE_SIZE - w) / 2, yOffset + parameters.TILE_SIZE - (parameters.TILE_SIZE - h) / 2 - 2);
	    }
	}
	private void drawGameOverRect(Graphics2D g, int width, int height)	{

		g.fillRoundRect(0, 0, width, height, 10, 10);
	}
	private int offsetCoors(int arg) {

    	return arg * (parameters.TILES_MARGIN + parameters.TILE_SIZE) + parameters.TILES_MARGIN;
  	}
}
class UtilMethods	{

	public static Graphics2D setGraphics(Graphics graphics)	{

		Graphics2D g = ((Graphics2D) graphics);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    	g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
    	return g;
	}
	public static void addComponent(JPanel panel, JComponent component, int x, int y, int width, int height, int align)	{

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = x;
		c.gridy = y;
		c.gridwidth = width;
		c.gridheight = height;
		c.weightx = 100.0;
		c.weightx = 100.0;
		c.insets = new Insets(5, 5, 5, 5);
		c.fill = GridBagConstraints.NONE;
		c.anchor = align;
		panel.add(component, c);
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