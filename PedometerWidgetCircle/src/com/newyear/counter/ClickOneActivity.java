package com.newyear.counter;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ClickOneActivity extends Activity {
    Button startButton;

    private static int maxSteps = 100;

    View.OnClickListener startListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            EditText editText = ((EditText) findViewById(R.id.txtFieldId));
            Editable text = null;
            if(editText != null) {
                text = editText.getText();
            }
            if(text != null) {
                maxSteps = Integer.parseInt(text.toString());
                MainWidget.setProgress(maxSteps);
            }
            Toast.makeText(getApplicationContext(), "Progress was set.",
                    Toast.LENGTH_SHORT).show();
            moveTaskToBack(true);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configure);
        EditText editText = ((EditText) findViewById(R.id.txtFieldId));
        editText.setText(String.valueOf(maxSteps));
        Toast.makeText(getApplicationContext(), "You may change settings",
                Toast.LENGTH_SHORT).show();


        startButton = (Button) findViewById(R.id.button_set_progress);
        startButton.setOnClickListener(startListener);
    }




}
