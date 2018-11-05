package com.example.user.inventory.Activities;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.user.inventory.Data.ProductContract;
import com.example.user.inventory.R;
import com.example.user.inventory.Data.ProductContract.ProductEntry;
import com.nguyenhoanglam.imagepicker.model.Config;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;

import java.util.ArrayList;

public class AddProduct extends AppCompatActivity {

    Button btn_add_product;
    EditText editText_product_code, editText_product_name, editText_product_unit, editText_product_price, editText_product_quantity;
    RelativeLayout upload_img_block;
    LinearLayout img_uploaded_block;
    ImageView product_img;
    String productImgPath;
    TextView change_img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        btn_add_product = (Button)findViewById(R.id.btn_add_product);
        // Handle click on Add product Button
        // If all data valid -> insert product into DB
        btn_add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertProduct();
                finish();

            }
        });

        editText_product_code = (EditText) findViewById(R.id.product_code_edit_text);
        editText_product_name = (EditText) findViewById(R.id.product_name_edit_text);
        editText_product_price = (EditText) findViewById(R.id.product_price_edit_text);
        editText_product_quantity = (EditText) findViewById(R.id.product_quantity_edit_text);
        editText_product_unit = (EditText) findViewById(R.id.product_unit_edit_text);

        TextWatcher mTextWatcher = new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // check Fields For Empty Values
                validate_input();
            }
        };
        editText_product_code.addTextChangedListener(mTextWatcher);
        editText_product_name.addTextChangedListener(mTextWatcher);
        editText_product_price.addTextChangedListener(mTextWatcher);
        editText_product_quantity.addTextChangedListener(mTextWatcher);
        editText_product_unit.addTextChangedListener(mTextWatcher);

        // Setup image upload block
        upload_img_block = (RelativeLayout)findViewById(R.id.img_upload_block);
        upload_img_block.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setup_image_picker();
            }
        });

        // This view will be visible only after an image has been chosen
        img_uploaded_block = (LinearLayout)findViewById(R.id.img_uploaded_block);
        product_img = (ImageView)findViewById(R.id.product_img);
        change_img = (TextView)findViewById(R.id.change_photo_btn);

        change_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setup_image_picker();
            }
        });
    }

    /**
     * Check fields for empty values to ensure no invalid data enters the DB
     */

    void validate_input()
    {
        String product_code = editText_product_code.getText().toString();
        String product_name = editText_product_name.getText().toString();
        String product_price = editText_product_price.getText().toString();
        String product_quantity = editText_product_quantity.getText().toString();
        String product_unit = editText_product_unit.getText().toString();

        // Button will stay disabled until all fields have non-empty values
        if( upload_img_block.getVisibility()== View.VISIBLE || product_code.trim().length() < 10 || product_name.isEmpty() || product_price.isEmpty() || product_quantity.isEmpty() ||product_unit.isEmpty() )
        {
            btn_add_product.setEnabled(false);
            btn_add_product.setAlpha(0.6f);
        }
        else
        {
            btn_add_product.setEnabled(true);
            btn_add_product.setAlpha(1f);
        }
    }

    /**
     * Call content provider method through content resolver to insert a new product
     */

    void insertProduct()
    {
        String product_code = editText_product_code.getText().toString();
        String product_name = editText_product_name.getText().toString();
        String product_price = editText_product_price.getText().toString();
        String product_quantity = editText_product_quantity.getText().toString();
        String product_unit = editText_product_unit.getText().toString();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductEntry.COLUMN_CODE, product_code);
        contentValues.put(ProductEntry.COLUMN_NAME, product_name);
        contentValues.put(ProductEntry.COLUMN_PRICE, product_price);
        contentValues.put(ProductEntry.COLUMN_QUANTITY, product_quantity);
        contentValues.put(ProductEntry.COLUMN_UNIT, product_unit);
        contentValues.put(ProductEntry.COLUMN_IMG_PATH,productImgPath);

        Uri insertedProductURI = getContentResolver().insert(ProductEntry.CONTENT_URI,contentValues);
    }

    /**
     * Show image picker when upload img block is clicked
     */
    void setup_image_picker()
    {
        ImagePicker.with(this)
                .setToolbarColor("#212121")
                .setStatusBarColor("#000000")
                .setToolbarTextColor("#FFFFFF")
                .setToolbarIconColor("#FFFFFF")
                .setProgressBarColor("#4CAF50")
                .setBackgroundColor("#212121")
                .setCameraOnly(false)
                .setMultipleMode(false)
                .setFolderMode(false)
                .setShowCamera(true)
                .setFolderTitle("Albums")
                .setImageTitle("Galleries")
                .setDoneTitle("Done")
                .setLimitMessage("You have reached selection limit")
                .setMaxSize(1)
                .setSavePath("ImagePicker")
                .setAlwaysShowDoneButton(true)
                .setKeepScreenOn(true)
                .start();
    }

    /**
     * What should be done when an image is selected
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Config.RC_PICK_IMAGES && resultCode == RESULT_OK && data != null) {
            ArrayList<Image> images = data.getParcelableArrayListExtra(Config.EXTRA_IMAGES);
            RequestOptions options = new RequestOptions();
            options.centerInside();
            upload_img_block.setVisibility(View.GONE);
            img_uploaded_block.setVisibility(View.VISIBLE);

            Glide.with(getApplicationContext())
                    .load(images.get(0).getPath())
                    .apply(options)
                    .into(product_img);

            productImgPath = images.get(0).getPath();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
