package com.arye.meital.jifirebase;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class ModifyListActivity extends AppCompatActivity {
    Button btn;
    EditText et;
    int pos;
    String oldName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_list);

        btn = findViewById(R.id.btn);
        et = findViewById(R.id.name);

        Intent thisIntent = getIntent();
        pos = thisIntent.getIntExtra("position", -1);
        oldName = thisIntent.getStringExtra("name");

        if(pos == -1) { // To add
            btn.setText(getResources().getString(R.string.add));
        }
        else { // To edit
            btn.setText(getResources().getString(R.string.edit));
            et.setHint(oldName);
        }
    }

    public void addBtnPressed(View view) {

        String newName = ((TextView)findViewById(R.id.name)).getText().toString();

        if(pos == -1) { // To add
            btn.setText("Add");
            Intent intent = new Intent();
            intent.putExtra("name", newName);
            //intent.putExtra("pos", name);
            setResult(RESULT_OK, intent);
            finish();
        }
        else { // To edit
            btn.setText("Edit");
            et.setHint(oldName);
            Intent intent = new Intent();
            intent.putExtra("name", newName);
            intent.putExtra("position", pos);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
