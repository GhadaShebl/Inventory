package com.example.user.inventory.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.user.inventory.Activities.Products;
import com.example.user.inventory.Data.ProductContract.ProductEntry;

public class ProductsProvider extends ContentProvider {

    /** Database helper object */
    private ProductsDBHelper productsDBHelper;

    /** URI matcher code for the content URI for the PRODUCTS table (When dealing with the whole table) */
    private static final int PRODUCTS = 100;

    /** URI matcher code for the content URI for a single PRODUCT in the PRODUCTS table (accessing it with it's code) */
    private static final int PRODUCT_CODE = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY,ProductContract.PATH_PRODUCTS,PRODUCTS);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY,ProductContract.PATH_PRODUCTS+"/#",PRODUCT_CODE);
    }


    @Override
    public boolean onCreate() {
        productsDBHelper = new ProductsDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {

        // Get readable instance from inventory.db
        SQLiteDatabase db = productsDBHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);

        switch (match){
          case PRODUCTS:
              cursor = db.query(ProductEntry.TABLE_NAME,strings,s,strings1,null,null,s1);
              break;

            case PRODUCT_CODE:
                // Set selection to be the product code column
                s = ProductEntry._ID+ "=?";
                // Set selection args to be the product code sent in the uri
                strings1 =  new String[]{ String.valueOf(ContentUris.parseId(uri)) };
                cursor = db.query(ProductEntry.TABLE_NAME,strings,s,strings1,null,null,s1);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
         }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues)
    {final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }

    }

    private Uri insertProduct(Uri uri, ContentValues contentValues)
    {
        SQLiteDatabase db = productsDBHelper.getWritableDatabase();
        long id = db.insert(ProductEntry.TABLE_NAME,null,contentValues);

        // Notify all listeners that the data has been changed for the product content URI
        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri,id);

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {

        SQLiteDatabase db = productsDBHelper.getWritableDatabase();

        int deletedRows = 0;

        int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCT_CODE:
                s = ProductEntry._ID+"=?";
                strings = new String[] {String.valueOf(ContentUris.parseId(uri))};
                deletedRows = db.delete(ProductEntry.TABLE_NAME,s,strings);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (deletedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deletedRows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase db = productsDBHelper.getWritableDatabase();
        int affectedRows = 0;
        int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCT_CODE:
                s = ProductEntry._ID+"=?";
                strings = new String[] {String.valueOf(ContentUris.parseId(uri))};
                affectedRows = db.update(ProductEntry.TABLE_NAME,contentValues,s,strings);
                break;
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
        if (affectedRows!=0) {
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return affectedRows;
    }
}
