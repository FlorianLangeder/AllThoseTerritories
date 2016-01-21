import javafx.scene.shape.Line;

import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class GameBoardFrame extends JFrame {

    private final static Color bgcolor = new Color(0, 41, 58);
    private final static Color linecolor = new Color(170, 132, 57);

    private ArrayList<TerritoryPolygon> polygonList = new ArrayList<>();
    private ArrayList<Line> lineList = new ArrayList<>();
    private String CurrentAction = "";
    private String currentPhase = "Setup";
    private String unitsLeft = "";

    public JPanel mainPanel;
    public JButton nextRoundBtn;

    public Point arrowFrom = new Point(0, 0);
    public Point arrowTo = new Point(0, 0);
    public boolean drawArrow = false;

    public GameBoardFrame() {
        super("All Those Territories - © Langeder, Mauracher 2016");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public void showFrame() {
        setSize(1250, 650);
        setResizable(false);

        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g; //Needed for Antialiasing
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setStroke(new BasicStroke(5.5f,
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND));

                g2.setColor(linecolor);
                for (Line line : lineList) {
                    g2.drawLine((int) line.getStartX(), (int) line.getStartY(), (int) line.getEndX(), (int) line.getEndY());
                }

                //DRAW NOT HIGHLIGHTED SECTIONS
                for (TerritoryPolygon pol : polygonList) {
                    Territory current = GameBoard.territories.get(pol.getName());
                    if (current == null || current.getIsHovered()) continue;

                    g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                    g2.setColor(Color.DARK_GRAY);
                    g2.drawPolygon(pol);
                    g2.setColor(current.getColor());
                    g2.fillPolygon(pol);

                    g2.setColor(Color.GREEN);
                    g2.drawString(String.valueOf(current.getArmy()), current.getCapital().x, current.getCapital().y);
                }

                //DRAW HIGHLIGHTED SECTIONS
                for (TerritoryPolygon pol : polygonList) {
                    Territory current = GameBoard.territories.get(pol.getName());
                    if (current == null || !current.getIsHovered()) continue;

                    g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                    g2.setColor(Color.ORANGE);
                    g2.drawPolygon(pol);
                    g2.setColor(current.getColor());
                    g2.fillPolygon(pol);

                    g2.setColor(Color.GREEN);
                    g2.drawString(String.valueOf(current.getArmy()), current.getCapital().x, current.getCapital().y);
                }

                if(drawArrow) {
                    drawArrow(g2, arrowTo, arrowFrom, Color.black);
                }

                g2.setColor(Color.GREEN);
                g2.drawString(getCurrentAction(), 625, 610);
                g2.drawString("Current Phase: " + currentPhase, 5, 15);
                if(!unitsLeft.equals("") && !unitsLeft.equals("0"))
                    g2.drawString("Units left: " + unitsLeft, 5, 30);

            }
        };
        mainPanel.setBackground(bgcolor);
        mainPanel.setSize(1250, 650);
        mainPanel.setLayout(null);
        nextRoundBtn = new JButton("Next Round");
        nextRoundBtn.setBounds(1145, 615, 100, 30);
        nextRoundBtn.setFont(new Font("Arial", Font.PLAIN, 10));
        mainPanel.add(nextRoundBtn);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
        getContentPane().add(mainPanel);
        setVisible(true);
    }

    public void drawNew() {
        SwingUtilities.invokeLater(() -> mainPanel.repaint());
    }

    private void drawArrow(Graphics2D g2, Point to, Point from, Color color)
    {
        g2.setPaint(color);
        Line2D l = new Line2D.Double(from.getX(), from.getY(), to.getX(), to.getY());
        g2.draw(l);
        double phi = Math.toRadians(40);
        int barb = 20;

        double dy = to.y - from.y;
        double dx = to.x - from.x;
        double theta = Math.atan2(dy, dx);

        double x, y, rho = theta + phi;
        for(int j = 0; j < 2; j++)
        {
            x = to.x - barb * Math.cos(rho);
            y = to.y - barb * Math.sin(rho);
            g2.draw(new Line2D.Double(to.x, to.y, x, y));
            rho = theta - phi;
        }
    }
    public void addPolygons(ArrayList<ArrayList<Point>> points, String name) {
        for (ArrayList<Point> pointList : points) {
            addPolygon(pointList, name);
        }
    }

    private void addPolygon(ArrayList<Point> points, String name) {
        TerritoryPolygon poly = new TerritoryPolygon(name);
        for (Point p : points) {
            poly.addPoint((int) (p.getX()), (int) p.getY());
        }
        polygonList.add(poly);
    }

    public void addLine(Point from, Point to) {
        lineList.add(new Line(from.x, from.y, to.x, to.y));
    }

    public String getClickedTerritory(int x, int y) {
        for (TerritoryPolygon pol : polygonList)
            if (pol.contains(x, y)) {
                drawNew();
                return pol.getName();
            }

        return null;
    }

    public String getCurrentAction() {
        return CurrentAction;
    }

    public void setCurrentAction(String value) {
        CurrentAction = value;
    }

    public void setCurrentPhase(String value) {
        currentPhase = value;
    }

    public void setUnitsLeft(int value) {
        unitsLeft = String.valueOf(value);
    }
}

class TerritoryPolygon extends Polygon {

    private String name;

    public TerritoryPolygon(String name) {
        super();
        this.name = name;
    }

    public String getName() { return name; }
}