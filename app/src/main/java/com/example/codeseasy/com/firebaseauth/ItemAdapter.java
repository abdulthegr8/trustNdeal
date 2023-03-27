package com.example.codeseasy.com.firebaseauth;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import java.util.List;
import java.util.Locale;

public class ItemAdapter extends ArrayAdapter<Item> {
    private final Context context;
    private final List<Item> itemList;

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
        ImageView itemImage = listItemView.findViewById(R.id.item_image);

        Item item = itemList.get(position);

        itemTitle.setText(item.getTitle());
        itemDescription.setText(item.getDescription());
        itemPrice.setText(String.format(Locale.getDefault(), "$%.2f", item.getPrice()));

        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), BitmapFactory.decodeResource(context.getResources(), R.drawable.rounded_placeholder_image));
        roundedBitmapDrawable.setCornerRadius(16);

        if (!item.getImageUrls().isEmpty()) {
            Glide.with(context)
                    .load(item.getImageUrls().get(0))
                    .placeholder(roundedBitmapDrawable)
                    .transform(new RoundedCorners(10))
                    .centerCrop()
                    .into(itemImage);
        } else {
            itemImage.setImageDrawable(roundedBitmapDrawable);
        }

        return listItemView;
    }
}
