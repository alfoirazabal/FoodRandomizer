package com.alfoirazaballevy.foodrandomizer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.alfoirazaballevy.foodrandomizer.db.DBHelper;
import com.alfoirazaballevy.foodrandomizer.domains.Food;
import com.alfoirazaballevy.foodrandomizer.adapters.lstadapters.AdapterLstComidas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private DBHelper dbHelper;

    private RecyclerView lstComidas;
    private RecyclerView.LayoutManager layoutManager;
    AdapterLstComidas adapterLstComidas;

    private CheckBox cbxCarne, cbxVegetariano, cbxVegano, cbxSinTACC;
    private Button btnRandomizer, btnAgregarComida;
    private RadioGroup rbtngOrdenacion;
    private RadioButton rbtnUltimosAgregados, rbtnPrimerosAgregados, rbtnAlfabetica, rbtnAlAzar;

    private ArrayList<Food> foods;
    private HashMap<Byte, CheckBox> cbxBoxFoodTypeHM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cbxBoxFoodTypeHM = new HashMap<>();

        lstComidas = findViewById(R.id.lst_comidas);
        cbxCarne = findViewById(R.id.cbx_carne);
        cbxVegetariano = findViewById(R.id.cbx_vegetariano);
        cbxVegano = findViewById(R.id.cbx_vegano);
        cbxSinTACC = findViewById(R.id.cbx_sin_tacc);
        rbtngOrdenacion = findViewById(R.id.rbtng_ordenacion);
        rbtnUltimosAgregados = findViewById(R.id.rbtn_ordenacion_ultimos_agregados);
        rbtnPrimerosAgregados = findViewById(R.id.rbtn_ordenacion_primeros_agregados);
        rbtnAlfabetica = findViewById(R.id.rbtn_ordenacion_alfabetica);
        rbtnAlAzar = findViewById(R.id.rbtn_ordenacion_al_azar);
        btnRandomizer = findViewById(R.id.btn_randomizar);
        btnAgregarComida = findViewById(R.id.btn_agregar_comida);

        loadSavedPreferences();

        cbxBoxFoodTypeHM.put((byte)1, cbxCarne);    //Foods with Carne are of type 1
        cbxBoxFoodTypeHM.put((byte)2, cbxVegetariano);
        cbxBoxFoodTypeHM.put((byte)3, cbxVegano);

        dbHelper = new DBHelper(getApplicationContext());

        foods = filterFoodsForSelectedOptions();

        fillFoodsList();

        CheckBox[] chxBoxes = new CheckBox[]{cbxCarne, cbxVegetariano, cbxVegano, cbxSinTACC};
        for(CheckBox chxBox : chxBoxes) {
            chxBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    foods = filterFoodsForSelectedOptions();
                    fillFoodsList();
                }
            });
        }

        rbtngOrdenacion.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(!rbtnAlAzar.isChecked()) {
                    Log.d("MAINACTIVITY", "Check changed!");
                    foods = filterFoodsForSelectedOptions();
                    fillFoodsList();
                } else {
                    /*
                        Will handle rbtnAlAzar onClickListener, because we want it to run again
                        even if the option is selected or not, the foods list will be reshuffled
                        anyways every time this radio button is clicked or checked.
                        If we were to ignore this if else structure, then the statements
                        foods = filterFoodsForSelectedOptions();
                        fillFoodsList();
                        will run multiple times, by triggering both OnCheckedChangeListener and
                        onClickListener, slowing down the process uselessly
                     */
                }
            }
        });
        rbtnAlAzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MAINACTIVITY", "Will shuffle the foods list again!");
                foods = filterFoodsForSelectedOptions();
                fillFoodsList();
            }
        });

        btnRandomizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Food randomFood = null;
                try {
                    randomFood = foodRandomizer();
                    Intent intent = new Intent(MainActivity.this, FoodDisplay.class);
                    intent.putExtra("FOODNAME", randomFood.getName());
                    intent.putExtra("SINTACC", randomFood.getSinTACC());
                    intent.putExtra("FOODTYPE", randomFood.getFoodType());
                    intent.putExtra("FOODDESCRIPTION", randomFood.getDescription());
                    startActivity(intent);
                } catch (EmptyFoodsList emptyFoodsList) {
                    Toast.makeText(
                            getApplicationContext(),
                            "No hay comidas en la lista",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        btnAgregarComida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FoodAdd.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        foods = filterFoodsForSelectedOptions();
        fillFoodsList();
        super.onResume();
    }

    @Override
    protected void onPause() {
        //Save preferences...
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("CARNE", cbxCarne.isChecked());
        editor.putBoolean("VEGETARIANO", cbxVegetariano.isChecked());
        editor.putBoolean("VEGANO", cbxVegano.isChecked());
        editor.putBoolean("SINTACC", cbxSinTACC.isChecked());
        int radioButtonID = rbtngOrdenacion.getCheckedRadioButtonId();
        View radioButton = rbtngOrdenacion.findViewById(radioButtonID);
        int nOrdenacion = rbtngOrdenacion.indexOfChild(radioButton);
        editor.putInt("NORDENACION", nOrdenacion);
        editor.commit();
        super.onPause();
    }

    private void loadSavedPreferences() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        cbxCarne.setChecked(sharedPref.getBoolean("CARNE", true));
        cbxVegetariano.setChecked(sharedPref.getBoolean("VEGETARIANO", true));
        cbxVegano.setChecked(sharedPref.getBoolean("VEGANO", true));
        cbxSinTACC.setChecked(sharedPref.getBoolean("SINTACC", true));
        int nOrdenacion = sharedPref.getInt("NORDENACION", 0);
        ((RadioButton)rbtngOrdenacion.getChildAt(nOrdenacion)).setChecked(true);
    }

    private Food foodRandomizer() throws EmptyFoodsList {
        int foodsSize = foods.size();
        if(foodsSize > 0) {
            Random random = new Random();
            Food randomFood = foods.get(random.nextInt(foods.size()));
            return randomFood;
        } else {
            throw new EmptyFoodsList();
        }

    }

    private ArrayList<Food> filterFoodsForSelectedOptions() {
        ArrayList<Food> allFoods = dbHelper.getFoods();
        if(rbtnAlAzar.isChecked()) {
            Collections.shuffle(allFoods);
        } else if (rbtnPrimerosAgregados.isChecked()) {
            Collections.reverse(allFoods);
        } else if (rbtnAlfabetica.isChecked()) {
            Collections.sort(allFoods);
        }
        ArrayList<Food> newFoods = new ArrayList<>();
        for(Food food : allFoods) {
            if(cbxSinTACC.isChecked()) {
                if(food.getSinTACC() == 1) {
                    if(cbxBoxFoodTypeHM.get(food.getFoodType()).isChecked()) {
                        newFoods.add(food);
                    }
                }
            } else {
                if(cbxBoxFoodTypeHM.get(food.getFoodType()).isChecked()) {
                    newFoods.add(food);
                }
            }
        }
        return newFoods;
    }

    private void fillFoodsList() {
        lstComidas.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        lstComidas.setLayoutManager(layoutManager);

        adapterLstComidas = new AdapterLstComidas(this, foods);
        lstComidas.setAdapter(adapterLstComidas);
    }

    class EmptyFoodsList extends Exception {
        @Override
        public String getMessage() {
            return "Foods List is Empty, cannot randomize";
        }
    }

}
