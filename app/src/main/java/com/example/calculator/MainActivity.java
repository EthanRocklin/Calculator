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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class MainActivity extends AppCompatActivity {

    private static final Logger logger = Logger.getLogger(MainActivity.class.getName());
    String dataToCalculate = "";
    TextView expressionTV, resultTV;

    /**
     *
     * @param savedInstanceState
     */
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

    public void appendCalc(View view) {
        if (view instanceof MaterialButton) {
            MaterialButton btn = (MaterialButton) view;
            dataToCalculate += btn.getText().toString();
            expressionTV.append(btn.getText().toString());
        }
    }

    public void appendDiv(View view) {
        dataToCalculate += "/";
        expressionTV.append("÷");
    }

    public void appendMulti(View view) {
        dataToCalculate += "*";
        expressionTV.append("×");
    }

    /**
     *
     * @param view
     */
    public void calculateResult(View view) {
        try {
            Context context = Context.enter();
            context.setOptimizationLevel(-1);
            Scriptable scriptable = context.initStandardObjects();
            String result = context.evaluateString(scriptable, dataToCalculate, "Javascript", 1, null)
                    .toString();
            // Removes decimal values if applicable
            if (Double.parseDouble(result) % 1 == 0) {
                int decimalIndex = result.indexOf(".");
                result = result.substring(0, decimalIndex);
            }
            resultTV.setText(result);
        } catch(Exception e) {
            logger.log(Level.WARNING, "CALCULATION ERROR: Expression dataToCalculate = "
                    + dataToCalculate
                    + "failed Rhino expression evaluation.");
        }
    }

    public void clear(View view) {
        try {
            if (!expressionTV.toString().isEmpty()) {
                dataToCalculate = dataToCalculate.substring(0, dataToCalculate.length() - 1);
                expressionTV.setText(dataToCalculate);
            }
        } catch (Exception e) {
            logger.log(Level.INFO, "EXPECTED EXCEPTION: Clear button pressed with nothing to clear.");
        }
    }

    public void clearAll(View view) {
        dataToCalculate = "";
        expressionTV.setText("");
        resultTV.setText("0");
    }

}