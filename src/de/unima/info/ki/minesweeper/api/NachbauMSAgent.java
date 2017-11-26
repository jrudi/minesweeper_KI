package de.unima.info.ki.minesweeper.api;

import java.util.ArrayList;
import java.util.Random;

import de.unima.info.ki.minesweeper.solver.MSSATSolver;

public class NachbauMSAgent extends MSAgent {

  private boolean displayActivated = false;

  private boolean[][] discoveredFields;
  private boolean[][] discoveredMines;

  private boolean alreadyMoved = false;

  private ArrayList<int[]> knowledgeBase = new ArrayList<>();

  MSSATSolver solver = new MSSATSolver();

  private ArrayList<Long> timeList = new ArrayList<Long>();


  private boolean positionExists(int x, int y) {
    if (x > 0 && y > 0 && x <= field.getNumOfCols() && y <= field.getNumOfRows())
      return true;
    return false;
  }

  private boolean[][] initializeBooleanList(boolean[][] list, int row, int col) {

    list = new boolean[row][col];
    for (int i = 0; i < list.length; i++) {
      for (int j = 0; j < list[0].length; j++) {
        list[i][j] = false;
      }
    }
    return list;
  }

  private void initializeKnowledgeBase() {
    for (int x = 1; x <= field.getNumOfCols(); x++) {
      for (int y = 1; y <= field.getNumOfRows(); y++) {
        knowledgeBase.add(new int[] {getFieldNumber(x, y), -getFieldNumber(x, y)});
      }
    }
  }

  private int getFieldNumber(int x, int y) {
    String tmpString = x + "0" + y;
    return Integer.parseInt(tmpString);
  }

  private int[] getNeighbourCords(int x, int y) {

    ArrayList<Integer> list = new ArrayList<Integer>();
    // goes through all neighbour cords {(x - 1, y - 1),(x - 1, y),(x - 1, y + 1),(x, y - 1),(x, y +
    // 1),(x + 1, y + 1),(x + 1, y),(x + 1, y - 1)} if not discovered
    if (positionExists(x - 1, y - 1) && !discoveredFields[(x - 1) - 1][(y - 1) - 1]) {
      list.add(getFieldNumber(x - 1, y - 1));
    }
    if (positionExists(x - 1, y) && !discoveredFields[(x - 1) - 1][y - 1]) {
      list.add(getFieldNumber(x - 1, y));
    }
    if (positionExists(x - 1, y + 1) && !discoveredFields[(x - 1) - 1][(y + 1) - 1]) {
      list.add(getFieldNumber(x - 1, y + 1));
    }
    if (positionExists(x, y - 1) && !discoveredFields[x - 1][(y - 1) - 1]) {
      list.add(getFieldNumber(x, y - 1));
    }
    if (positionExists(x, y + 1) && !discoveredFields[x - 1][(y + 1) - 1]) {
      list.add(getFieldNumber(x, y + 1));
    }
    if (positionExists(x + 1, y + 1) && !discoveredFields[(x + 1) - 1][(y + 1) - 1]) {
      list.add(getFieldNumber(x + 1, y + 1));
    }
    if (positionExists(x + 1, y) && !discoveredFields[(x + 1) - 1][y - 1]) {
      list.add(getFieldNumber(x + 1, y));
    }
    if (positionExists(x + 1, y - 1) && !discoveredFields[(x + 1) - 1][(y - 1) - 1]) {
      list.add(getFieldNumber(x + 1, y - 1));
    }
    // convert Arraylist
    int[] finishedList = new int[list.size()];
    for (int i = 0; i < finishedList.length; i++) {
      finishedList[i] = list.get(i);
    }
    return finishedList;
  }

  public int uncoverField(int x, int y) {
    // set field discovered
    discoveredFields[x - 1][y - 1] = true;
    // get field information
    int tmpInt = this.field.uncover(x - 1, y - 1);
    // tmpInt >= 0 | no mine in there
    if (tmpInt >= 0) {
      this.accumulateKnowlegde(x, y, tmpInt);
    }
    return tmpInt;
  }

  private void accumulateKnowlegde(int x, int y, int value) {
    // add kB that f(x, y) has no mine
    knowledgeBase.add(new int[] {getFieldNumber(x, y)});
    int[] neighbours = this.getNeighbourCords(x, y);

    ClauseBuilder bcb = null;
    for (int i = 0; i <= 8; i++) {
      if (i != value) {
        bcb = new ClauseBuilder(neighbours, i);
        knowledgeBase.addAll(bcb.getBombLists());
      }
    }

  }

  @Override
  public boolean solve() {
    // get row & col from parent attribute
    int rows = this.field.getNumOfRows();
    int cols = this.field.getNumOfCols();
    int x, y;
    int fieldInformation;
    int failedTimes = 0;

    // get borders of field & initialize
    this.discoveredFields = this.initializeBooleanList(discoveredFields, cols,rows);
    this.discoveredMines = this.initializeBooleanList(discoveredMines, cols,rows);

    // already moved?
    if (!alreadyMoved) {
      x = 0;
      y = 0;
    } else {
      x = failedTimes; // todo
      y = failedTimes;// todo
    }
    // initialize KnowledgeBase
    this.initializeKnowledgeBase();
    // discover first field
    fieldInformation = this.uncoverField(x + 1, y + 1);

    // while till mine found or field solved + attributes
    boolean discovered, foundMine, darkness;
    ArrayList<int[]> clickList, mineList;

    while (fieldInformation >= 0 && !this.field.solved()) {
      // reset boolean every loop
      discovered = false;
      foundMine = false;
      darkness = false;
      // iterate discoveredFields
      for (x = 1; x <= discoveredFields.length; x++) {
        for (y = 1; y <= discoveredFields[0].length; y++) {
          // Field unknown
          if (discoveredFields[x - 1][y - 1] == false && discoveredMines[x - 1][y - 1] == false) {
            clickList = (ArrayList<int[]>) knowledgeBase.clone();
            clickList.add(new int[] {-getFieldNumber(x, y)});
            solver.addClauses(clickList);

            // get fieldinformation
            if (solver.solve() == false) {
              fieldInformation = this.uncoverField(x, y);
              discovered = true;
            }

            mineList = (ArrayList<int[]>) knowledgeBase.clone();
            mineList.add(new int[] {getFieldNumber(x, y)});
            solver.addClauses(mineList);
            // register found mine, set foundMine true
            if (solver.solve() == false) {
              knowledgeBase.add(new int[] {-getFieldNumber(x, y)});
              discoveredMines[x - 1][y - 1] = true;
              foundMine = true;
            }
          }
        }

        if (x == discoveredFields.length && !discovered && !foundMine) {
          darkness = true;
        }
      }
      // everything is dark.. do random move
      long time = 0;
      Random rng = new Random();
      if (darkness) {
        while (true) {
          time = System.currentTimeMillis();
          if (this.field.solved()) {
            break;
          }
          x = rng.nextInt(rows);
          y = rng.nextInt(cols);
          if (!discoveredFields[x][y] && !discoveredMines[x][y]) {
            fieldInformation = this.uncoverField(x + 1, y + 1);
            break;
          }
          timeList.add(System.currentTimeMillis() - time);
        }
      }

    }


    if (field.solved()) {
      failedTimes = 0;
      return true;
    } else {
      failedTimes++;
      alreadyMoved = true;
      return false;
    }

  }

  @Override
  public void activateDisplay() {
    this.displayActivated = true;

  }

  @Override
  public void deactivateDisplay() {
    this.displayActivated = false;
  }

  private class ClauseBuilder {

    private ArrayList<int[]> mineList = null;
    private int[] neighbourList = null;
    private final int MAX_Neighbour_Count = 8;

    public ClauseBuilder(int[] list, int bombsToAdd) {
      this.mineList = new ArrayList<int[]>();
      this.neighbourList = list;
      buildBombAsList(new int[list.length], bombsToAdd, (8 - list.length));
    }

    public ArrayList<int[]> getBombLists() {
      return this.mineList;
    }

    public void buildBombAsList(int[] list, int bombsToAdd, int index) {
      if (bombsToAdd <= (8 - index)) {
        if (bombsToAdd == 0) {

          mineList.add(finishStringWithEmptyFields(list));

        } else {
          buildBombAsList(stringWithBombAtEnd(list.clone(), index), bombsToAdd - 1, index + 1);

          if (emptyFieldIsPossible(bombsToAdd, index)) {
            buildBombAsList(stringWithEmptyFieldAtEnd(list.clone(), index), bombsToAdd, index + 1);
          }
        }
      }
    }

    private int[] finishStringWithEmptyFields(int[] list) {
      for (int i = 0; i < list.length; i++) {
        if (list[i] == 0) {
          list[i] = -neighbourList[i];
        }
      }
      return list;
    }

    private int[] stringWithEmptyFieldAtEnd(int[] list, int index) {
      list[index - (8 - list.length)] = -neighbourList[index - (8 - list.length)];
      return list;
    }

    private int[] stringWithBombAtEnd(int[] list, int index) {
      list[index - (8 - list.length)] = neighbourList[index - (8 - list.length)];
      return list;
    }

    private boolean emptyFieldIsPossible(int bombsToAdd, int index) {
      return index < (MAX_Neighbour_Count - bombsToAdd);
    }

  }

}

