package com.example.user.inventory.Data;

import android.net.Uri;
import android.provider.BaseColumns;

public class ProductContract
{
    /** to be used in the content provider Uri, it is the same as the authority defined in manifest.xml */
    public static final String CONTENT_AUTHORITY = "com.example.android.products";

    /** concat first part of the Uri to the authority */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /** last part of the Uri */
    public static final String PATH_PRODUCTS = "products";

    public static abstract class ProductEntry implements BaseColumns {

        /** The content URI to access the product data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        public static final String TABLE_NAME = "products";

        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_CODE = "code";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_UNIT = "unit";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_IMG_PATH = "image";

    }
}
