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

import java.util.HashMap;

public class FoodAdd extends AppCompatActivity {

    EditText etxtNombre, etxtDescripcion;
    ImageView imgTipo;
    Spinner spnTipo;
    CheckBox cbxSinTACC;
    Button btnAgregarComida;

    HashMap<Integer, Integer> imageResourceFromTipo = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.food_add);

        etxtNombre = findViewById(R.id.etxt_nombre);
        etxtDescripcion = findViewById(R.id.etxt_descripcion);
        spnTipo = findViewById(R.id.spn_tipo);
        imgTipo = findViewById(R.id.img_tipo);
        cbxSinTACC = findViewById(R.id.cbx_sin_tacc);
        btnAgregarComida = findViewById(R.id.btn_agregar_comida);

        imageResourceFromTipo.put(1, R.drawable.carne);
        imageResourceFromTipo.put(2, R.drawable.vegetariano);
        imageResourceFromTipo.put(3, R.drawable.vegano);

        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(
                this, R.layout.support_simple_spinner_dropdown_item,
                new String[]{"Carne", "Vegetariano", "Vegano"}
        );
        spnTipo.setAdapter(adapterTipo);
        spnTipo.setSelection(1);
        changeTipoImage();

        spnTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changeTipoImage();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        btnAgregarComida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper dbHelper = new DBHelper(getApplicationContext());
                String nombre = etxtNombre.getText().toString();
                byte tipo = (byte)(spnTipo.getSelectedItemPosition() + 1);
                byte sinTACC = 0;
                if(cbxSinTACC.isChecked()) sinTACC = 1;
                String description = etxtDescripcion.getText().toString();
                dbHelper.insertFood(nombre, tipo, sinTACC, description);
                finish();
            }
        });

    }

    private void changeTipoImage() {
        imgTipo.setImageResource(imageResourceFromTipo.get(spnTipo.getSelectedItemPosition() + 1));
    }

}
