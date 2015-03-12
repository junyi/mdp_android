package com.example.mdptool;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SendCoordinates extends Activity {
    public static ConnectedThread ct;
    String x, y, msg;
    int compX, compY;
    EditText e1, e2;
    TextView t1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sendcordinates);
        if (ct == null)
            ct = MainActivity.ct;
    }

    public void resetBtn(View view) {
        Button reset = (Button) findViewById(R.id.resetBtn);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                e1 = (EditText) findViewById(R.id.inputX);
                e2 = (EditText) findViewById(R.id.inputY);
                t1 = (TextView) findViewById(R.id.outputMsg);
                e1.setText(null);
                e2.setText(null);
                t1.setText("Output: ");
            }
        });
    }

    public void sendBtn(View view) {
        e1 = (EditText) findViewById(R.id.inputX);
        e2 = (EditText) findViewById(R.id.inputY);
        t1 = (TextView) findViewById(R.id.outputMsg);

        try {
            compX = Integer.parseInt(e1.getText().toString());
            compY = Integer.parseInt(e2.getText().toString());
        } catch (Exception e) {
            if (MainActivity.myToast != null) MainActivity.myToast.cancel();
            MainActivity.myToast = Toast.makeText(this, "Invalid Inputs", Toast.LENGTH_SHORT);
            MainActivity.myToast.show();
        }

        if ((compX < 1 || compX > 14) || (compY < 1 || compY > 18)) {
            if (MainActivity.myToast != null) MainActivity.myToast.cancel();
            MainActivity.myToast = Toast.makeText(this, "Invalid Coordinates", Toast.LENGTH_SHORT);
            MainActivity.myToast.show();
        } else {
            x = Integer.toString(compX);
            y = Integer.toString(compY);

            //x = e1.getText().toString();
            //y = e2.getText().toString();

            msg = Config.PC_coord + ":" + x + "," + y;
            ct.write(msg.getBytes());
            //ct.write(msg.getBytes());
            t1.setText("Output: " + msg);
            //ct.write(x.getBytes());
            //ct.write(y.getBytes());
        }
    }
}
