package de.unima.info.ki.minesweeper.api;

public class FirstMSAgent extends MSAgent{
    private boolean displayActivated = false;
    private boolean firstDecision = true;

    @Override
    public boolean solve() {
        int numOfRows = this.field.getNumOfRows();
        int numOfCols = this.field.getNumOfCols();
        int x, y, feedback;

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
            }

            if (displayActivated) System.out.println("Uncovering (" + x + "," + y + ")");
            feedback = field.uncover(x,y);

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

