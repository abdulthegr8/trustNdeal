package com.example.codeseasy.com.firebaseauth;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Locale;

public class ItemAdapter extends ArrayAdapter<Item> {
    private Context context;
    private List<Item> itemList;

    public ItemAdapter(@NonNull Context context, List<Item> itemList) {
        super(context, R.layout.list_item, itemList);
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listItemView = inflater.inflate(R.layout.list_item, parent, false);

        TextView itemTitle = listItemView.findViewById(R.id.item_title);
        TextView itemDescription = listItemView.findViewById(R.id.item_description);
        TextView itemPrice = listItemView.findViewById(R.id.item_price);

        Item item = itemList.get(position);

        itemTitle.setText(item.getTitle());
        itemDescription.setText(item.getDescription());
        itemPrice.setText(String.format(Locale.getDefault(), "$%.2f", item.getPrice()));

        return listItemView;
    }
}
