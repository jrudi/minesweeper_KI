package de.unima.info.ki.minesweeper.api;

public class FirstMSAgent extends MSAgent{
    private boolean displayActivated = false;
    private boolean firstDecision = true;
    private int[][] progress;
    @Override
    public boolean solve() {
        int numOfRows = this.field.getNumOfRows();
        int numOfCols = this.field.getNumOfCols();
        int x, y, feedback;
        this.progress = new int[numOfRows][numOfCols];
        for (int i = 0; i < numOfRows; i++) {
            for (int j = 0; j < numOfCols; j++) {
                progress[i][j] = -1;
            }
        }
        do {
            if (displayActivated) System.out.println(field);
            if (firstDecision) {
                x = 0; //TODO Startwert finden
                y = 0; //TODO Startwert finden
                firstDecision = false;
            }
            else {

                //TODO HIER ALGORITHMUS
                x = numOfCols;
                y = numOfRows;
                /*
                vielleicht kann man, wenn man eine 0 zurückbekommt, eine Queue erstellen,
                in der die uliegenden Felder eingetragen werden. Und solange Elemente in der Queue sind,
                werden diese zuerst abgearbeitet.
                Wird ja theoretisch bei Microsoft MineSweeper auch so gemacht, wenn man ein Feld mit ner 0 trifft,
                werden alle leeren Felder ausßenrum auch aufgedeckt.
                 */
            }

            if (displayActivated) System.out.println("Uncovering (" + x + "," + y + ")");
            feedback = field.uncover(x,y);

            progress[x][y]=feedback;

        } while(feedback >= 0 && !field.solved());

        if (field.solved()) {
            if (displayActivated) System.out.println("Solved the field");
            return true;
        }
        else {
            if (displayActivated) System.out.println("BOOM!");
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


    }
}

