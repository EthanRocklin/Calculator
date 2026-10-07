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

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView expressionTV, resultTV;
    MaterialButton buttonAC,
            buttonC,
            buttonDivide,
            buttonEqual,
            buttonLP,
            buttonMinus,
            buttonMultiply,
            buttonPlus,
            buttonRP,
            button0,
            button1,
            button2,
            button3,
            button4,
            button5,
            button6,
            button7,
            button8,
            button9;

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
        // Binds ID and listeners to all buttons
        assignID(buttonAC, R.id.buttonAC);
        assignID(buttonC, R.id.buttonC);
        assignID(buttonDivide, R.id.buttonDivide);
        assignID(buttonEqual, R.id.buttonEqual);
        assignID(buttonLP, R.id.buttonLP);
        assignID(buttonMinus, R.id.buttonMinus);
        assignID(buttonMultiply, R.id.buttonMultiply);
        assignID(buttonPlus, R.id.buttonPlus);
        assignID(buttonRP, R.id.buttonRP);
        assignID(button0, R.id.button0);
        assignID(button1, R.id.button1);
        assignID(button2, R.id.button2);
        assignID(button3, R.id.button3);
        assignID(button4, R.id.button4);
        assignID(button5, R.id.button5);
        assignID(button6, R.id.button6);
        assignID(button7, R.id.button7);
        assignID(button8, R.id.button8);
        assignID(button9, R.id.button9);
    }

    /**
     *
     * @param btn
     * @param id
     */
    void assignID(MaterialButton btn, int id) {
        btn = findViewById(id);
        btn.setOnClickListener(this);
    }

//    void clearAll(View view) {
//        expressionTV.setText("");
//        resultTV.setText("0");
//    }

    /**
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        MaterialButton btn = (MaterialButton) view;
        String btnText = btn.getText().toString();
        String dataToCalculate = expressionTV.getText().toString();

        if (btnText.equals("AC")) {
            expressionTV.setText("");
            resultTV.setText("0");
           return;
        } else if (btnText.equals("=")) {
            expressionTV.setText(resultTV.getText());
            return;
        } else if (btnText.equals("C")) {
                dataToCalculate = dataToCalculate.substring(0, dataToCalculate.length() - 1);
        }

        // BUG: Rhino library is not computing multiplication and division)
        else {
            String text = "";
            if (btnText.equals("@string/multiply")) {
                text = "*";
            } else if (btnText.equals("@string/divide")) {
                text = "/";
            } else {
                text = btnText;
            }
            dataToCalculate = dataToCalculate + text;
        }
        expressionTV.setText(dataToCalculate);
        String result = getResults(dataToCalculate);
        //
        if (!result.equals("err")) {
           resultTV.setText(result);
        };
    }

    String getResults(String data) {
        try {
            Context context = Context.enter();
            context.setOptimizationLevel(-1);
            Scriptable scriptable = context.initStandardObjects();
            return context.evaluateString(scriptable, data, "Javascript", 1, null).toString();
        } catch(Exception e) {
            System.out.println("ERROR:" + e.getMessage());
            e.printStackTrace();
            return "err";
        }
    }
}