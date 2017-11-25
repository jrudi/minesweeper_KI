package de.unima.info.ki.minesweeper.solver;

import java.util.ArrayList;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

public class MSSATSolver {

  private final int MAXVAR = 1000000;
  private final int NBCLAUSES = 500000;
  private ISolver solver;
  private boolean solution = true;

  public MSSATSolver() {
    this.solver = SolverFactory.newDefault();
    this.solver.newVar(MAXVAR);
    this.solver.setExpectedNumberOfClauses(NBCLAUSES);
    this.solver.setTimeout(3600);
  }

  public boolean solve() {
    IProblem problem = solver;
    boolean satisfiable = false;

    if (!solution) {
      return solution;
    }

    try {
      satisfiable = problem.isSatisfiable();
    } catch (TimeoutException e) {
      System.out.println("That took too long! Make it short!");
      e.printStackTrace();
    }
    this.solver.reset();

    return satisfiable;
  }

  public void addClauses(ArrayList<int[]> clauses) {
    solution = true;
    for (int[] clause : clauses) {
      try {
        this.solver.addClause(new VecInt(clause));

      } catch (ContradictionException e) {
        solution = false;

        break;
      }
    }
  }
}
