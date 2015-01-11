// file description -----------------------------------------------------------

// filename: SudokuSolverServlet.java
//
// author: Arsalan Sufi
//
// description:
//
// The SudokuSolverServlet class processes the information entered into the web
// app's HTML form. It then checks if the entered Sudoku is solvable. If so, it
// implements a backtracking algorithm to solve it and sends the solution in an
// HTML response.

// import statements ----------------------------------------------------------

package sudokusolver;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// SudokuSolverServlet class --------------------------------------------------

@SuppressWarnings("serial")
public class SudokuSolverServlet extends HttpServlet {

    // doGet() and doPost() methods -------------------------------------------

    // public void doGet()
    //
    // parameters:
    // -- HttpServletRequest req --
    //    The servlet request object.
    // -- HttpServletResponse resp --
    //    The servlet response object.
    //
    // This method handles get requests and is the primary method in this
    // class.
    //
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {
    
        // Store Sudoku as 2D array obtaining input from HTML form.
        int[][] sudoku = new int[9][9];
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                String inputBox = "n" + ((column + 1) + row * 9);
                if (   req.getParameter(inputBox).equals("")
                    || req.getParameter(inputBox).equals(null)) {
                    sudoku[row][column] = 0;
                } else {
                    sudoku[row][column] = Integer.parseInt(
                        req.getParameter(inputBox));
                }
            }
        }

        // Set default prompt.
        String prompt = "Huzzah!<br>Success!<br>I solved it!<br>Try another?" +
                        "<br><br>";
        
        // Solve Sudoku if valid.
        if (isValidSudoku(sudoku)) {
        
            // Store Sudoku's initial state in separate array.
            int[][] sudokuInitial = new int[9][9];
            for (int row = 0; row < 9; row++) {
                for (int column = 0; column < 9; column++) {
                    sudokuInitial[row][column] = sudoku[row][column];
                }
            }

            solve(sudoku, sudokuInitial);
    
        // Don't solve Sudoku if invalid and adjust prompt accordingly.
        } else {
            prompt = "Sorry... I<br>couldn&rsquo;t solve<br>this one. Try" +
                     "<br>another?<br><br>";
        }
    
        // Delay procession of program to allow time for JavaScript animation.
        long start = System.currentTimeMillis();
        long end = start + 3000;
        while (System.currentTimeMillis() < end) ;

        sendResponse(resp, sudoku, prompt);
    }

    // public void doPost()
    //
    // parameters:
    // -- HttpServletRequest req --
    //    The servlet request object.
    // -- HttpServletResponse resp --
    //    The servlet response object.
    //
    // This method redirects post requests to the doGet() method.
    //
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws IOException {
        doGet(req, resp);
    }

    // high-level methods -----------------------------------------------------

    // public static boolean isValidSudoku()
    //
    // parameters:
    // -- int[][] sudoku --
    //    The Sudoku puzzle in consideration stored as a 2D array.
    //
    // This method checks if {sudoku} is solvable.
    //
    public static boolean isValidSudoku(int[][] sudoku) {
        int numToCheck = 0;
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                if (sudoku[row][column] != 0) {
                    numToCheck = sudoku[row][column];
                    sudoku[row][column] = 0;
                    if (!isPossible(sudoku, row, column, numToCheck)) {
                        sudoku[row][column] = numToCheck;
                        return false;
                    } else {
                        sudoku[row][column] = numToCheck;
                    }
                }
            }
        }
        return true;
    }
    
    // public static void solve()
    //
    // parameters:
    // -- int[][] sudoku --
    //    The Sudoku puzzle in consideration stored as a 2D array.
    // -- int[][] sudokuInitial --
    //    The initial state of the Sudoku puzzle stored as a 2D array.
    //
    // This method solves {sudoku}. It implements a tail-recursive backtracking
    // algorithm using a while loop.
    //
    public static void solve(int[][] sudoku, int[][] sudokuInitial) {

        // Find first blank square.
        int row = 0;
        int column = 0;
        outerLoop:
        for (row = 0; row < 9; row++) {
            for (column = 0; column < 9; column++) {
                if (sudoku[row][column] == 0) break outerLoop;
            }
        }

        // Loop until {sudoku} represents a solution.
        while (row != 9) {
            
            // Place value other than current value in square if possible.
            int initialValue = sudoku[row][column];
            for (int numToCheck = initialValue + 1;
                 numToCheck <= 9;
                 numToCheck++) {
                if (isPossible(sudoku, row, column, numToCheck)) {
                    sudoku[row][column] = numToCheck;
                    break;
                }
            }
            
            // Backtrack if no other value is possible.
            if (sudoku[row][column] == initialValue) {
                sudoku[row][column] = 0;
                do {
                    if (column != 0) {
                        column--;
                    } else {
                        row--;
                        column = 8;
                    }
                } while (sudokuInitial[row][column] != 0);
                
            // Otherwise move on to next empty square.
            } else {
                do {
                    if (column != 8) {
                        column++;
                    } else {
                        row++;
                        column = 0;
                    }
                    if (row == 9) break;
                } while (sudokuInitial[row][column] != 0);
            }
        }
    }

    // public static void sendResponse()
    //
    // parameters:
    // -- HttpServletResponse resp --
    //    The servet response object.
    // -- int[][] sudoku --
    //    The solved Sudoku stored in a 2D array. If the Sudoku wasn't
    //    solvable, this variable contains the initial Sudoku.
    // -- String prompt --
    //    The prompt for the user. Varies depending on whether or not the
    //    entered Sudoku was solvable.
    //
    // This method sends the solution (if found) and associated prompt in an
    // HTML response.
    //
    // TODO: Use the repetition in the response to clean this method up.
    //
    public static void sendResponse(HttpServletResponse resp,
                                    int[][] sudoku, String prompt)
        throws IOException {
        resp.setContentType("text/html");
        resp.getWriter().print(
            "\n" +
            "<table id=\"subContainer\">\n" +
            "<tr>\n" +
            "\n" +
            "  <td>\n" +
            "  <table id=\"sudoku\">\n" +
            "    <tr>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 0, 0) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 0, 1) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 0, 2) + "</td>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 0, 3) + "</td>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 0, 4) + "</td>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 0, 5) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 0, 6) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 0, 7) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 0, 8) + "</td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 1, 0) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 1, 1) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 1, 2) + "</td>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 1, 3) + "</td>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 1, 4) + "</td>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 1, 5) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 1, 6) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 1, 7) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 1, 8) + "</td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 2, 0) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 2, 1) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 2, 2) + "</td>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 2, 3) + "</td>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 2 ,4) + "</td>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 2, 5) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 2, 6) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 2, 7) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 2, 8) + "</td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 3, 0) + "</td>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 3, 1) + "</td>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 3, 2) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 3, 3) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 3, 4) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 3, 5) + "</td>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 3, 6) + "</td>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 3, 7) + "</td>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 3, 8) + "</td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 4, 0) + "</td>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 4, 1) + "</td>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 4, 2) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 4, 3) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 4, 4) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 4, 5) + "</td>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 4, 6) + "</td>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 4, 7) + "</td>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 4, 8) + "</td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 5, 0) + "</td>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 5, 1) + "</td>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 5, 2) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 5, 3) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 5, 4) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 5, 5) + "</td>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 5, 6) + "</td>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 5, 7) + "</td>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 5, 8) + "</td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 6, 0) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 6, 1) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 6, 2) + "</td>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 6, 3) + "</td>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 6, 4) + "</td>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 6, 5) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 6, 6) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 6, 7) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 6, 8) + "</td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 7, 0) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 7, 1) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 7, 2) + "</td>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 7, 3) + "</td>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 7, 4) + "</td>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 7, 5) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 7, 6) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 7, 7) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 7, 8) + "</td>\n" +
            "    </tr>\n" +
            "    <tr>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 8, 0) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 8, 1) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 8, 2) + "</td>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 8, 3) + "</td>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 8, 4) + "</td>\n" +
            "      <td class=\"number-middle\">" + valueInTd(sudoku, 8, 5) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 8, 6) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 8, 7) + "</td>\n" +
            "      <td class=\"number\">" + valueInTd(sudoku, 8, 8) + "</td>\n" +
            "    </tr>\n" +
            "  </table>\n" +
            "  </td>\n" +
            "\n" +
            "  <td id=\"verticalGap\"></td>\n" +
            "\n" +
            "  <td id=\"instructions\">\n" +
            "    " + prompt + "\n" +
            "    <button id=\"reset\">Reset</button>\n" +
            "  </td>\n" +
            "\n" +
            "</tr>\n" +
            "</table>\n" +
            "\n" +
            "<div class=\"horizontalGapLarge\"></div>\n" +
            "\n" +
            "<div class=\"homeLinkContainer\">\n" +
            "  <img class=\"homeLink\" src=\"Arrow.png\"/>\n" +
            "</div>\n" +
            "\n" +
            "<div class=\"horizontalGapLarge\"></div>\n" +
            "\n");
    }

    // low-level helper methods -----------------------------------------------

    // public static boolean isPossible()
    //
    // parameters:
    // -- int[][] sudoku --
    //    The sudoku puzzle in consideration stored as a 2D array.
    // -- int row --
    //    The row number of the square in consideration.
    // -- int column --
    //    The column number of the square in consideration.
    // -- int numToCheck --
    //    The number to check.
    //
    // This method determines whether {numToCheck} can be placed in the square
    // designated by {row} and {column}. The method is used by isValidSudoku()
    // and solve().
    //
    public static boolean isPossible(int[][] sudoku,
                                     int row, int column,
                                     int numToCheck) {
    
        // Set default value.
        boolean memory = true;

        // Check if {numToCheck} already exists in row.
        for (int i = 0; i < 9; i++) {
            if (sudoku[row][i] == numToCheck) memory = false;
        }

        // Check if {numToCheck} already exists in column.
        for (int i = 0; i < 9; i++) {
            if (sudoku[i][column] == numToCheck) memory = false;
        }

        // Check if number already exists in 3-by-3 box.
        for (int i = (row / 3) * 3; i < (row / 3) * 3 + 3; i++) {
            for (int j = (column / 3) * 3; j < (column / 3) * 3 + 3; j++) {
                if (sudoku[i][j] == numToCheck) memory = false;
            }
        }

        return memory;
    }

    // public static String valueInTd()
    //
    // parameters:
    // -- int[][] sudoku --
    //    The sudoku puzzle in consideration stored as a 2D array.
    // -- int row --
    //    The row number of the square in consideration.
    // -- int column --
    //    The column number of the square in consideration.
    //
    // This method replaces the zeros in {sudoku} with blank squares.
    //
    public static String valueInTd(int[][] sudoku, int row, int column) {
        if (sudoku[row][column] == 0) {
            return "";
        } else {
            return "" + sudoku[row][column];
        }
    }
}
