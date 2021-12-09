package com.parwinder.dicerolling;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import java.util.ArrayList;
import java.util.Comparator;
/**
 * Student name: Parwinder Deep Kaur
 * Student ID: A00237487
 * This is Dice Rolling App app. This lets user to roll a die twice or once.
 * The user can create his own die as well. The app workd in both night and day mode.
 * The app has animation too for roll button. The app sorts the list of die in numeric order
 *
 * @author Parwinder Deep Kaur
 * @version 1.0
 * @since 9 December, 2021
 */
public class MainActivity extends AppCompatActivity {

    private Button rollBT;
    private Spinner diceSP;
    private TextView resultTV;
    private EditText newDieET;
    private SwitchCompat dieSwitch;
    ArrayAdapter<String> dieAdapter;
    ArrayList<String> dieList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        initializeDieSpinner(); // set initial data in die drop down
        addEditorAction(); // action on done of DONE button of keyboard
        setClickListener();
        SharedPref.init(getApplicationContext()); // singleton class for Shared Preferences
    }

    @Override
    protected void onResume() {
        super.onResume();
        // get die list from SharedPreference and set list to spinner
        if (SharedPref.read("die_list") != null) {
            dieList = SharedPref.read("die_list");
        }
        if (dieList != null) {
            setDieAdapter();
        }

        // get previously selected die value of spinner from SharedPreference and set value of spinner
        if (SharedPref.read("selected_die", 0) != null) {
            int selectedDie = SharedPref.read("selected_die", 4);
            for (int position = 0; position < dieList.size(); position++) {
                if (Integer.parseInt(dieList.get(position)) == selectedDie) {
                    diceSP.setSelection(position);
                    return;
                }
            }
        }
    }

    /*
     *This initializes the arra adapter for spinner
     * set layout for spinner drop down popup and spinner itself
     * set the adapter for spinner*/
    private void setDieAdapter() {
        dieAdapter = new ArrayAdapter<>(this, R.layout.spinner_text, dieList);
        dieAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        diceSP.setAdapter(dieAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // save die list when app goes in background
        if (dieList != null)
            SharedPref.write("die_list", dieList);

        // save selected die value of spinner list in shared preference
        SharedPref.write("selected_die", Integer.parseInt(diceSP.getSelectedItem().toString()));
    }

    /*
     * set click listener on button */
    private void setClickListener() {
        rollBT.setOnClickListener(view -> {
            rollDie(); // method to roll the die and get random value
            animateButton();
        });
    }

    /*
     * animate the roll button to show shake effect
     * @res shake_anim: this get animation file from anim folder under res
     * set the animation to view rollBT */
    private void animateButton() {
        final Animation anim1 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake_anim);
        rollBT.startAnimation(anim1);
    }

    /*
     * the actual roll method to get the random value
     * max value is the die value selected from drop down
     * if roll twice is selected it will append two random values
     * else it will show one random value which is the die roll result*/
    private void rollDie() {
        resultTV.setText("");

        int maxValue = Integer.parseInt(diceSP.getSelectedItem().toString());
        int randomVal = (int) (Math.random() * maxValue) + 1;
        resultTV.setText(String.valueOf(randomVal));

        if (dieSwitch.isChecked()) {
            resultTV.append("," + ((int) (Math.random() * maxValue) + 1)); // append second value to first die value
        }
    }

    /*
    * this sets the action editor on edit text
    * called when the user press done button from keyboard
    * */
    private void addEditorAction() {
        newDieET.setOnEditorActionListener((v, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) { // check if ime option is done
                addNewDie(newDieET.getText().toString().trim()); // add new die to the die list
            }
            return false;
        });
    }

    /*
    * add the die value to the list
    * if die already exists then show error message
    * otherwise add the die to the die list and show success message*/
    @SuppressLint("NewApi")
    private void addNewDie(String dieValue) {
        if (dieList != null) {
            if (!dieList.contains(dieValue)) {
                dieList.add(dieValue); // add value to list
                dieList.sort(Comparator.comparingInt(Integer::parseInt)); // sort the die list with inetger values added to list
                setDieAdapter();
                newDieET.getText().clear();
                Toast.makeText(this, getString(R.string.new_die_created), Toast.LENGTH_SHORT).show();
            } else {
                newDieET.getText().clear();
                Toast.makeText(this, getString(R.string.die_already_exists), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
    * sets initial value of die and set the adapter to spinner*/
    private void initializeDieSpinner() {
        dieList.add("4");
        dieList.add("6");
        dieList.add("8");
        dieList.add("10");
        dieList.add("12");
        dieList.add("20");
        setDieAdapter();
    }

    /*
    * find xml views by ids*/
    private void findViews() {
        diceSP = findViewById(R.id.diceSP);
        newDieET = findViewById(R.id.new_dieET);
        dieSwitch = findViewById(R.id.on_offSC);
        rollBT = findViewById(R.id.rollBT);
        resultTV = findViewById(R.id.resultTV);
    }
}
