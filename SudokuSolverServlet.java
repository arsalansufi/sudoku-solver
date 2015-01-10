package sudokusolver;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class SudokuSolverServlet extends HttpServlet {
    
    // redirect doPost method to doGet method
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws IOException {
        doGet(req, resp);
    }
    
    // doGet method
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws IOException {
    
    // store sudoku as array obtaining input from html form
    int[][] sudoku = new int[9][9];
    for (int row = 0; row < 9; row++) {
        for (int column = 0; column < 9; column++) {
        String inputBox = "n" + ((column + 1) + row * 9);
        if (req.getParameter(inputBox).equals("") ||
            req.getParameter(inputBox).equals(null)) sudoku[row][column] = 0;
        else sudoku[row][column] = Integer.parseInt(req.getParameter(inputBox));
        }
    }
    
    // solve sudoku if valid
    String prompt = "Huzzah!<br>Success!<br>I solved it!<br>Try another?<br><br>";
    if (isValidSudoku(sudoku)) {
        
        // store sudoku's initial condition in separate array
        int[][] sudokuInitial = new int[9][9];
        for (int row = 0; row < 9; row++) {
        for (int column = 0; column < 9; column++) {
            sudokuInitial[row][column] = sudoku[row][column];
        }
        }
        
        // find first blank square
        int rowMemory = 0;
        int columnMemory = 0;
        outerLoop:
        for (int row = 0; row < 9; row++) {
        for (int column = 0; column < 9; column++) {
            if (sudoku[row][column] == 0) {
            rowMemory = row;
            columnMemory = column;
            break outerLoop;
            }
        }
        }
        
        solve(sudoku, sudokuInitial, rowMemory, columnMemory);
    
    // don't attempt to solve sudoku if invalid and adjust prompt accordingly
    } else {
        prompt = "Sorry... I<br>couldn&rsquo;t solve<br>this one. Try<br>another?<br><br>";
    }
    
    // delay procession of program to allow time for javascript animation
    long start = System.currentTimeMillis();
    long end = start + 3000;
    int dummyVariable = 0;
    while (System.currentTimeMillis() < end) {
        dummyVariable++;
    }
    
    printSolutionPage(resp, sudoku, prompt);
    }
    
    // method used by doGet to determine whether sudoku is valid
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
    
    // method for solving sudoku
    public static void solve(int[][] sudoku, int[][] sudokuInitial,
                             int row, int column) {
    
    // loop until sudoku is solved
    while (row != 9) {
        
        // place valid value other than current value in square if possible
        int initialValue = sudoku[row][column];
        for (int numToCheck = initialValue + 1; numToCheck <= 9; numToCheck++) {
        if (isPossible(sudoku, row, column, numToCheck)) {
            sudoku[row][column] = numToCheck;
            break;
        }
        }
        
        // backtrack if no other valid value exists
        if (sudoku[row][column] == initialValue) {
        sudoku[row][column] = 0;
        do {
            if (column != 0) column--;
            else {
            row--;
            column = 8;
            }
        } while (sudokuInitial[row][column] != 0);
        
        // move on to next empty square if valid value does exist
        } else {
        do {
            if (column != 8) column++;
            else {
            row++;
            column = 0;
            }
            if (row == 9) break;
        } while (sudokuInitial[row][column] != 0);
        }
    }
    }
    
    // method used by solve and isValidSudoku to determine whether certain number
    // can be placed in certain square
    public static boolean isPossible(int[][] sudoku,
                                     int row, int column,
                                     int numToCheck) {
    
    // default value
    boolean memory = true;
    
    // check if number already exists in row
    for (int i = 0; i < 9; i++) {
        if (sudoku[row][i] == numToCheck) {
        memory = false;
        }
    }
    
    // check if number already exists in column
    for (int i = 0; i < 9; i++) {
        if (sudoku[i][column] == numToCheck) {
        memory = false;
        }
    }
    
    // check if number already exists in box
    for (int i = (row / 3) * 3; i < (row / 3) * 3 + 3; i++) {
        for (int j = (column / 3) * 3; j < (column / 3) * 3 + 3; j++) {
        if (sudoku[i][j] == numToCheck) {
            memory = false;
        }
        }
    }
    
    return memory;
    }
    
    // method for printing solution page
    public static void printSolutionPage(HttpServletResponse resp,
                                         int[][] sudoku,
                                         String prompt) throws IOException {
    resp.setContentType("text/html");
    resp.getWriter().print("\n" +
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
    
    // method used by printSolutionPage to replace zeros in sudoku array with
    // blank squares
    public static String valueInTd(int[][] sudoku, int row, int column) {
    if (sudoku[row][column] == 0) return "";
    else return "" + sudoku[row][column];
    }
}