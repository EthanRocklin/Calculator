package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.button.MaterialButton;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 * This class contains all necessary variables and methods for running
 * the calculator, including Rhino integration and updating the UI.
 * @author Ethan Rocklin
 */
public class MainActivity extends AppCompatActivity {

    private static final Logger logger = Logger.getLogger(MainActivity.class.getName());
    String dataToCalculate = "";
    TextView expressionTV, resultTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Assigns TextView variables to associated XML elements
        expressionTV = findViewById(R.id.expressionTV);
        resultTV = findViewById(R.id.resultTV);
    }

    /**
     * This method appends the multiplication symbol "÷" to the screen while adhering to
     * Rhino's requirement for using "/" to evaluate expressions.
     * @param view The UI element that triggered the method call.
     */
    public void appendDiv(View view) {
        dataToCalculate += "/";
        expressionTV.append("÷");
    }

    /**
     * This method appends the character of a button (0-9, ., +, -) to the arithmetic
     * expression to be calculated.
     * @param view The UI element that triggered the method call.
     */
    public void appendExpr(View view) {
        MaterialButton btn = (MaterialButton) view;
        dataToCalculate += btn.getText().toString();
        expressionTV.append(btn.getText().toString());
    }

    /**
     * This method appends the multiplication symbol "×" to the screen while adhering to
     * Rhino's requirement for using "*" to evaluate expressions.
     * @param view The UI element that triggered the method call.
     */
    public void appendMulti(View view) {
        dataToCalculate += "*";
        expressionTV.append("×");
    }

    /**
     * This method computes and displays the result of the entire arithmetic expression
     * @param view The UI element that triggered the method call.
     */
    public void calculateResult(View view) {
        try {
            // Gets the result using Rhino arithmetic evaluation
            Context context = Context.enter();
            context.setOptimizationLevel(-1);
            Scriptable scriptable = context.initStandardObjects();
            String resultRaw = context.evaluateString(scriptable, dataToCalculate, "Javascript", 1, null)
                    .toString();
            // Cleans the result of inaccuracies and excessive digits; inserts commas
            String resultClean = cleanResult(resultRaw);
            resultClean = insertCommas(resultClean);
            resultTV.setText(resultClean);
        } catch(Exception e) {
            logger.log(Level.WARNING, "CALCULATION ERROR: Expression dataToCalculate = "
                    + dataToCalculate
                    + " failed Rhino expression evaluation.");
        } finally {
            Context.exit();
        }
    }

    /**
     * This method deletes the last character in the current arithmetic expression.
     * @param view The UI element that triggered the method call.
     */
    public void clear(View view) {
        try {
            if (!expressionTV.toString().isEmpty()) {
                dataToCalculate = dataToCalculate.substring(0, dataToCalculate.length() - 1);
                String displayedText = expressionTV.getText().toString();
                expressionTV.setText(displayedText.substring(0, displayedText.length() - 1));
            }
        } catch (Exception e) {
            logger.log(Level.INFO, "EXPECTED EXCEPTION: Clear button pressed with nothing to clear.");
        }
    }

    /**
     * This method resets the arithmetic expression to evaluate.
     * @param view The UI element that triggered the method call.
     */
    public void clearAll(View view) {
        dataToCalculate = "";
        expressionTV.setText("");
        resultTV.setText("0");
    }

    /**
     * This method takes the raw result of a Rhino arithmetic expression evaluation
     * and returns an accurate result up to 15 decimal places.
     * @param raw A String representing the initial arithmetic result from Rhino
     * @return aAString representing the accurate result of the initial arithmetic expression
     *         evaluated by Rhino.
     */
    private String cleanResult(String raw) {
        double resultNum = Context.toNumber(raw);
        // Converts to a BigDecimal for accurate rounding and stripping trailing zeros
        BigDecimal resultBD = BigDecimal.valueOf(resultNum)
                .round(new MathContext(15))
                .stripTrailingZeros();
        return resultBD.toPlainString();
    }

    /**
     * This method takes the result of an arithmetic evaluation and inserts
     * commas to distinguish place values.
     * @param num A String representing the result of an arithmetic evaluation
     * @return A String representing the result with place-value commas
     */
    private String insertCommas(String num) {
        String wholePart = "";
        String decimalPart = "";
        // Splits the result into its whole and decimal parts
        int decimalIndex = num.length();
        if (num.contains(".")) {
            decimalIndex = num.indexOf(".");
            decimalPart = num.substring(decimalIndex);
        } else {
            wholePart = num;
        }
        // Reverses the order of digits for easier processing
        wholePart = new StringBuilder(num.substring(0, decimalIndex))
                .reverse()
                .toString();
        // Temporarily removes the negative from wholePart
        boolean isNegative = false;
        int lastIndex = wholePart.length() - 1;
        if (wholePart.charAt(lastIndex) == '-') {
            isNegative = true;
            wholePart = wholePart.substring(0, lastIndex);
        }
        // Scans wholePart and inserts a comma every three digits.
        StringBuilder result = new StringBuilder();
        lastIndex = wholePart.length() - 1;
        for (int i = 0; i < wholePart.length(); i++) {
            result.append(wholePart.charAt(i));
            // if the
            if ((i + 1) % 3 == 0 && i != lastIndex) {
                result.append(",");
            }
        }
        // Restores the original order of the result
        if (isNegative) {
            result.append("-");
        }
        result.reverse().append(decimalPart);
        return result.toString();
    }

}