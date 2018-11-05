package com.example.user.inventory.Activities;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.user.inventory.R;
import com.example.user.inventory.Data.ProductContract.ProductEntry;

public class ProductDetails extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifies a particular loader being used in this component */
    private static final int PRODUCT_LOADER = 0;

    TextView txt_product_name,txt_product_code,txt_product_price,txt_product_quantity;
    ImageView product_img;

    Uri currentProduct;

    Button btn_delete_product, btn_increment, btn_decrement, btn_contact_supplier;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Intent details = getIntent();
        currentProduct = details.getData();



        btn_delete_product = (Button) findViewById(R.id.btn_delete_product);
        btn_delete_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, close the current activity.
                                finish();
                            }
                        };

                // Show dialog that there are unsaved changes
                showDeleteDialog(discardButtonClickListener);
            }
        });

        btn_contact_supplier = (Button) findViewById(R.id.btn_contact_supplier_product);
        btn_contact_supplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","Inventory.supplier@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, txt_product_code.getText().toString()+" - "+txt_product_name.getText().toString());
                emailIntent.putExtra(Intent.EXTRA_TEXT, "We need supplies of the following product "+"\n"+"Code : "+txt_product_code.getText().toString() +"\n"+"Name : "+ txt_product_name.getText().toString());
                startActivity(Intent.createChooser(emailIntent, "Contact Supplier..."));
            }
        });

        btn_decrement = (Button) findViewById(R.id.btn_decrement_quantity);
        btn_decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decrementQuantity();

            }
        });

        btn_increment = (Button) findViewById(R.id.btn_increment_quantity);
        btn_increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                incrementQuantity();

            }
        });

        getLoaderManager().initLoader(PRODUCT_LOADER,null,this);
    }

    private void showDeleteDialog(DialogInterface.OnClickListener discardButtonClickListener)
    {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this product?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteProduct();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_NAME,
                ProductEntry.COLUMN_CODE,
                ProductEntry.COLUMN_PRICE,
                ProductEntry.COLUMN_UNIT,
                ProductEntry.COLUMN_QUANTITY,
                ProductEntry.COLUMN_IMG_PATH
                 };
        return new CursorLoader(this,currentProduct,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            txt_product_name = (TextView) findViewById(R.id.txt_product_name);
            txt_product_code = (TextView) findViewById(R.id.txt_product_code);
            txt_product_price = (TextView) findViewById(R.id.txt_product_price);
            txt_product_quantity = (TextView) findViewById(R.id.txt_product_quantity);
            product_img = (ImageView)findViewById(R.id.product_img);

            int nameColumnIndex = cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_NAME);
            int codeColumnIndex = cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_CODE);
            int priceColumnIndex = cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_QUANTITY);
            int unitColumnIndex = cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_UNIT);
            int imgPathColumnIndex = cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_IMG_PATH);

            txt_product_name.setText(cursor.getString(nameColumnIndex));
            txt_product_code.setText(cursor.getString(codeColumnIndex));
            txt_product_price.setText(setPrice(cursor.getInt(priceColumnIndex),cursor.getString(unitColumnIndex)));
            txt_product_quantity.setText(cursor.getInt(quantityColumnIndex) + "");

            RequestOptions options = new RequestOptions();
            options.centerInside();
            Glide.with(getApplicationContext())
                    .load(cursor.getString(imgPathColumnIndex))
                    .apply(options)
                    .into(product_img);
        }
    }

    private String setPrice(int product_price,String product_unit)
    {
        return ("$"+ product_price+ "/"+ product_unit);
    }

    private void deleteProduct()
    {
        getContentResolver().delete(currentProduct,null,null);
        finish();
    }

    private void decrementQuantity()
    {
        int currentQuantity = Integer.parseInt(txt_product_quantity.getText().toString());
        int quantity = currentQuantity - 1;
        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductEntry.COLUMN_QUANTITY,quantity);
        getContentResolver().update(currentProduct,contentValues,null,null);
        txt_product_quantity.setText(quantity+"");
    }

    private void incrementQuantity()
    {
        int currentQuantity = Integer.parseInt(txt_product_quantity.getText().toString());
        int quantity = currentQuantity + 1;
        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductEntry.COLUMN_QUANTITY,quantity);
        getContentResolver().update(currentProduct,contentValues,null,null);
        txt_product_quantity.setText(quantity+"");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
