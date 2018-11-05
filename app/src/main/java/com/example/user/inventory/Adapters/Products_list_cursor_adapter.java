package com.example.user.inventory.Adapters;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.user.inventory.Data.ProductContract;
import com.example.user.inventory.R;
import com.example.user.inventory.Data.ProductContract.ProductEntry;

import org.w3c.dom.Text;

public class Products_list_cursor_adapter extends CursorAdapter {
    public Products_list_cursor_adapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.product_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final Context mContext = context;

        TextView txt_product_name = (TextView)view.findViewById(R.id.txt_product_name);
        TextView txt_product_code = (TextView)view.findViewById(R.id.txt_product_code);
        TextView txt_product_price = (TextView)view.findViewById(R.id.txt_product_price);
        final TextView txt_product_quantity = (TextView)view.findViewById(R.id.txt_product_quantity);
        Button btn_sale = (Button)view.findViewById(R.id.btn_decrement_quantity);
        ImageView product_img = (ImageView)view.findViewById(R.id.product_img);


        int columnIdIndex = cursor.getColumnIndexOrThrow(ProductEntry._ID);
        int columnNameIndex = cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME);
        int columnCodeIndex = cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_CODE);
        int columnPriceIndex = cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRICE);
        int columnQuantityIndex = cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_QUANTITY);
        int columnUnitIndex = cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_UNIT);
        int imgPathColumnIndex = cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_IMG_PATH);

        final int product_id = cursor.getInt(columnIdIndex);
        String product_name = cursor.getString(columnNameIndex);
        String product_code = cursor.getString(columnCodeIndex);
        final int product_price = cursor.getInt(columnPriceIndex);
        final int product_quantity = cursor.getInt(columnQuantityIndex);
        String product_unit = cursor.getString(columnUnitIndex);

        txt_product_name.setText(product_name);
        txt_product_code.setText(product_code);
        txt_product_quantity.setText(product_quantity+"");
        txt_product_price.setText(setPrice(product_price,product_unit));

        RequestOptions options = new RequestOptions();
        options.centerInside();
        Glide.with(context)
                .load(cursor.getString(imgPathColumnIndex))
                .apply(options)
                .into(product_img);


        // handle btn_sale on click (minus 1 from product quantity and save to db
        btn_sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decrementQuantity(product_quantity,mContext,product_id,txt_product_quantity);
            }
        });

    }

    private void decrementQuantity(int quantity, Context context, int product_id, TextView product_quantity)
    {
        quantity = quantity - 1;
        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductEntry.COLUMN_QUANTITY,quantity);
        Uri currentProductURI = ContentUris.withAppendedId(ProductEntry.CONTENT_URI,product_id);
        context.getContentResolver().update(currentProductURI,contentValues,null,null);
        product_quantity.setText(quantity+"");
    }

    private String setPrice(int product_price,String product_unit)
    {
        return ("$"+ product_price+ "/"+ product_unit);

    }

}
