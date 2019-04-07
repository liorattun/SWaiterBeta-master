package com.example.liorkaramany.swaiterbeta;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class DishListOrders extends ArrayAdapter<Dish> {

    private Activity context;
    private List<Dish> dishList;

    public DishListOrders(Activity context, List<Dish> dishList) {

        super(context, R.layout.list_layout2, dishList);
        this.context = context;
        this.dishList = dishList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.list_layout2, null, true);
        TextView tvName = (TextView) listViewItem.findViewById(R.id.tvName);
        Dish dish = dishList.get(position);
        tvName.setText(dish.getName());
        return listViewItem;
    }
}
