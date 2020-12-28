package com.alfoirazaballevy.foodrandomizer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class FoodDisplay extends AppCompatActivity {

    private TextView txtFoodName, txtFoodDescription;
    private ImageView imgSinTACC, imgTipo;

    private HashMap<Byte, Integer> imageForFoodType = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.food_display);

        txtFoodName = findViewById(R.id.txt_name);
        txtFoodDescription = findViewById(R.id.txt_description);
        imgSinTACC = findViewById(R.id.img_sin_tacc);
        imgTipo = findViewById(R.id.img_tipo);

        imageForFoodType.put((byte)1, R.drawable.carne);
        imageForFoodType.put((byte)2, R.drawable.vegetariano);
        imageForFoodType.put((byte)3, R.drawable.vegano);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        String foodName = bundle.getString("FOODNAME");
        byte foodSinTACC = bundle.getByte("SINTACC");
        byte foodType = bundle.getByte("FOODTYPE");
        String foodDescription = bundle.getString("FOODDESCRIPTION");

        txtFoodName.setText(foodName);
        if(foodSinTACC == 1) {
            imgSinTACC.setImageResource(R.drawable.sin_tacc);
        } else {
            imgSinTACC.setVisibility(View.GONE);
        }
        txtFoodDescription.setText(foodDescription);

        imgTipo.setImageResource(imageForFoodType.get(foodType));

    }

}
