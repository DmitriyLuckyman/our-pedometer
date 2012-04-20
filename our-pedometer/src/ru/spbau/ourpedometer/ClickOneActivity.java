package ru.spbau.ourpedometer;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ClickOneActivity extends Activity {
    Button startButton;

    View.OnClickListener startListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            EditText editText = ((EditText) findViewById(R.id.txtFieldId));
            Editable text = null;
            if(editText != null) {
                text = editText.getText();
            }
            if(text != null) {
                MainWidget.setAimSteps(Integer.parseInt(text.toString()));
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

        Toast.makeText(getApplicationContext(), "We are in ClickOneActivity",
                Toast.LENGTH_SHORT).show();


        startButton = (Button) findViewById(R.id.button_set_progress);
        startButton.setOnClickListener(startListener);
    }




}
