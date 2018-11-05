package com.example.user.inventory.Activities;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.user.inventory.Adapters.Products_list_cursor_adapter;

import com.example.user.inventory.Data.ProductContract.ProductEntry;
import com.example.user.inventory.R;

public class Products extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    /** Identifies a particular loader being used in this component */
    private static final int PRODUCT_LOADER = 0;

    ListView list_view_products;
    LinearLayout empty_view;
    Button btn_add_product;
    Products_list_cursor_adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        // Reference to list view that'll hold all products
        list_view_products = (ListView)findViewById(R.id.list_view_products);

        // Reference to list_view_products' empty view
        empty_view = (LinearLayout) findViewById(R.id.empty_view);

        // Set empty view of list_view_products
        list_view_products.setEmptyView(empty_view);

        // Handle click event of the add product button inside empty view
        btn_add_product = (Button) findViewById(R.id.btn_add_product);
        btn_add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),AddProduct.class));
            }
        });

        // Initialize and link empty adapter to list view
        adapter = new Products_list_cursor_adapter(getApplicationContext(),null);
        list_view_products.setAdapter(adapter);

        list_view_products.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent details = new Intent(getApplicationContext(),ProductDetails.class);
                Uri currentProductURI = ContentUris.withAppendedId(ProductEntry.CONTENT_URI,l);
                details.setDataAndNormalize(currentProductURI);
                startActivity(details);
            }
        });

        getLoaderManager().initLoader(PRODUCT_LOADER,null, this);

    }

    /**
     * Inflate home_menu.xml to add the (insert new product) option to toolbar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    /**
     * Handle options menu items clicks
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Insert new product" menu option
            case R.id.action_insert_new_product:
                startActivity(new Intent(getApplicationContext(),AddProduct.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[]Projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_CODE,
                ProductEntry.COLUMN_NAME,
                ProductEntry.COLUMN_QUANTITY,
                ProductEntry.COLUMN_UNIT,
                ProductEntry.COLUMN_PRICE,
                ProductEntry.COLUMN_IMG_PATH};
        return new CursorLoader(this,ProductEntry.CONTENT_URI,Projection,null,null,ProductEntry.COLUMN_NAME);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
