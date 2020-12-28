package com.alfoirazaballevy.foodrandomizer.adapters.lstadapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alfoirazaballevy.foodrandomizer.FoodDisplay;
import com.alfoirazaballevy.foodrandomizer.FoodModify;
import com.alfoirazaballevy.foodrandomizer.R;
import com.alfoirazaballevy.foodrandomizer.db.DBHelper;
import com.alfoirazaballevy.foodrandomizer.domains.Food;

import java.util.ArrayList;

public class AdapterLstComidas extends RecyclerView.Adapter<AdapterLstComidas.MyHolder> {

    private static final int[] FOOD_TYPES_IMG_RESOURCES = new int[]{
            R.drawable.carne,
            R.drawable.vegetariano,
            R.drawable.vegano
    };

    Context context;
    ArrayList<Food> foods;

    public AdapterLstComidas(Context context, ArrayList<Food> foods) {
        this.context = context;
        this.foods = foods;
    }

    @NonNull
    @Override
    public AdapterLstComidas.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(
                R.layout.list_item_food, parent, false
        );

        MyHolder myHolder = new MyHolder(view);
        return myHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterLstComidas.MyHolder holder, int position) {

        final Food food = foods.get(position);

        holder.setIsRecyclable(false);  //So views don't get messed up (like icons not shown, etc.)

        holder.txtName.setText(food.getName());
        if(food.getDescription().equals("")) {
            holder.txtDescription.setVisibility(View.GONE);
        } else {
            holder.txtDescription.setText(food.getDescription());
        }
        if(food.getSinTACC() == 1) {
            holder.imgSinTACC.setImageResource(R.drawable.sin_tacc);
        } else {
            holder.imgSinTACC.setVisibility(View.GONE);
        }
        holder.imgTipo.setImageResource(
                FOOD_TYPES_IMG_RESOURCES[food.getFoodType() - 1]
        );
        holder.listContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, holder.listContainer);
                popupMenu.inflate(R.menu.list_item_food_popupmenu);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupClickedListener(context, food));
            }
        });

    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {

        LinearLayout listContainer;
        TextView txtName, txtDescription;
        ImageView imgSinTACC, imgTipo;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            listContainer = itemView.findViewById(R.id.list_container);
            txtName = itemView.findViewById(R.id.txt_name);
            txtDescription = itemView.findViewById(R.id.txt_description);
            imgSinTACC = itemView.findViewById(R.id.img_sin_tacc);
            imgTipo = itemView.findViewById(R.id.img_tipo);

        }
    }

    class PopupClickedListener implements PopupMenu.OnMenuItemClickListener {

        private Context context;
        private Food food;

        public PopupClickedListener(Context context, Food food) {
            this.context = context;
            this.food = food;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.food_item_view:
                    Intent intentView = new Intent(context, FoodDisplay.class);
                    intentView.putExtra("FOODNAME", food.getName());
                    intentView.putExtra("SINTACC", food.getSinTACC());
                    intentView.putExtra("FOODTYPE", food.getFoodType());
                    intentView.putExtra("FOODDESCRIPTION", food.getDescription());
                    context.startActivity(intentView);
                    return true;
                case R.id.food_item_modify:
                    Intent intentModify = new Intent(context, FoodModify.class);
                    intentModify.putExtra("FOODID", food.getId());
                    context.startActivity(intentModify);
                    return true;
                case R.id.food_item_delete:
                    handleFoodDeleteDialog();
                    return true;
                default:
                    return false;
            }
        }

        private void handleFoodDeleteDialog() {

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Borrado de Comida");
            builder.setMessage("¿Segura de que desea borrar la comida '" + this.food.getName() + "'?");
            builder.setCancelable(true);

            builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DBHelper dbHelper = new DBHelper(context);
                    dbHelper.deleteFood(food.getId());
                    foods.remove(food);
                    notifyDataSetChanged();
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Cancelar...
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

    }

}
