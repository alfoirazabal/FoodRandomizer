package com.alfoirazaballevy.foodrandomizer;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.alfoirazaballevy.foodrandomizer.db.DBHelper;
import com.alfoirazaballevy.foodrandomizer.domains.Food;

import java.util.HashMap;

public class FoodModify extends AppCompatActivity {

    DBHelper dbHelper;

    Food currentFood;

    EditText etxtNombre, etxtDescripcion;
    ImageView imgTipo;
    Spinner spnTipo;
    CheckBox cbxSinTACC;
    Button btnModificarComida;

    HashMap<Integer, Integer> imageResourceFromTipo = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.food_modify);

        dbHelper = new DBHelper(getApplicationContext());

        etxtNombre = findViewById(R.id.etxt_nombre);
        etxtDescripcion = findViewById(R.id.etxt_descripcion);
        imgTipo = findViewById(R.id.img_tipo);
        spnTipo = findViewById(R.id.spn_tipo);
        cbxSinTACC = findViewById(R.id.cbx_sin_tacc);
        btnModificarComida = findViewById(R.id.btn_modificar_comida);

        imageResourceFromTipo.put(1, R.drawable.carne);
        imageResourceFromTipo.put(2, R.drawable.vegetariano);
        imageResourceFromTipo.put(3, R.drawable.vegano);

        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(
                this, R.layout.support_simple_spinner_dropdown_item,
                new String[]{"Carne", "Vegetariano", "Vegano"}
        );
        spnTipo.setAdapter(adapterTipo);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        long id = bundle.getLong("FOODID");
        currentFood = dbHelper.getFood(id);

        etxtNombre.setText(currentFood.getName());
        etxtDescripcion.setText(currentFood.getDescription());
        cbxSinTACC.setChecked(currentFood.getSinTACC() == 1);

        spnTipo.setSelection(currentFood.getFoodType() - 1);
        changeTipoImage();

        spnTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changeTipoImage();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        btnModificarComida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyComida();
            }
        });

    }

    private void modifyComida() {
        currentFood.setName(etxtNombre.getText().toString());
        currentFood.setDescription(etxtDescripcion.getText().toString());
        currentFood.setFoodType((byte)(spnTipo.getSelectedItemPosition() + 1));
        byte sinTACC = 0;
        if(cbxSinTACC.isChecked()) sinTACC = 1;
        currentFood.setSinTACC(sinTACC);
        dbHelper.updateFood(currentFood);
        finish();
    }

    private void changeTipoImage() {
        imgTipo.setImageResource(imageResourceFromTipo.get(spnTipo.getSelectedItemPosition() + 1));
    }

}
