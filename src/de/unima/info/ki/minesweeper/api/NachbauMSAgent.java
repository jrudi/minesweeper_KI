package de.unima.info.ki.minesweeper.api;

import java.util.ArrayList;

public class NachbauMSAgent extends MSAgent {

  private boolean displayActivated = false;

  private boolean[][] discoveredFields;
  private boolean[][] discoveredMines;

  private boolean alreadyMoved = false;

  private ArrayList<int[]> knowledgeBase;

  private boolean[][] initializeBooleanList(boolean[][] list, int row, int col) {
    list = new boolean[row][col];
    for (int i = 0; i < list.length; i++) {
      for (int j = 0; j < list[0].length; j++) {
        list[i][j] = false;
      }
    }
    return list;
  }

  @Override
  public boolean solve() {
    // get row & col from parent attribute
    int rows = this.field.getNumOfRows();
    int cols = this.field.getNumOfCols();
    int x, y;
    
    // get borders of field & initialize
    this.discoveredFields = this.initializeBooleanList(discoveredFields, rows, cols);
    this.discoveredMines = this.initializeBooleanList(discoveredMines, rows, cols);

    // already moved?
    if (!alreadyMoved) {
      x = 0;
      y = 0;
    } else {
      x = 1; // todo
      y = 1;// todo
    }
    
    



    return false;


  }

  @Override
  public void activateDisplay() {
    this.displayActivated = true;

  }

  @Override
  public void deactivateDisplay() {
    this.displayActivated = false;
  }



}

