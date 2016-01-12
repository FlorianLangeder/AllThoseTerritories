import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Florian Langeder on 23.12.15.
 */
public class GameBoard {
    private GameBoardFrame boardFrame;
    public static HashMap<String, Territory> territories;
    public static HashMap<String, Continent> continents;

    public GameBoard() {
        MapLoader loader = new MapLoader("world.map");

        territories = loader.getTerritories();
        continents = loader.getContinents();

        boardFrame = new GameBoardFrame();
        addLinesToFrame();
        addPatchesToFrame();
        boardFrame.showFrame();

        addFrameListener();

        /*for(Map.Entry<String, Territory> entry : territories.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue().toString());
        }
        for(Map.Entry<String, Continent> entry : continents.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue().toString());
        }*/
    }

    private void addLinesToFrame()
    {
        for(Map.Entry<String, Territory> entry: territories.entrySet()) {
            for(String nei : entry.getValue().getNeighbors())
            {
                Territory neighbor = territories.get(nei);
                if(neighbor == null) continue;

                boardFrame.addLine(entry.getValue().getCapital(), neighbor.getCapital());
            }
        }
    }

    private void addPatchesToFrame() {
        boardFrame.cleanPolyList();
        for(Map.Entry<String, Territory> entry: territories.entrySet()) {
            boardFrame.addPolygons(entry.getValue().getPatches(), entry.getKey());
        }
    }

    private void addFrameListener() {
        boardFrame.mainPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                String name = boardFrame.getClickedTerritory(e.getX(), e.getY());
                if(name == null) return;

                Territory item = territories.get(name);
                item.setArmy(23);
                territories.put(name, item);

                addPatchesToFrame();

                boardFrame.drawNew();

                System.out.println(name);
            }
        });
    }
}
