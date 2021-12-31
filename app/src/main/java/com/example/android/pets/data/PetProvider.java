package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.android.pets.R;
import com.example.android.pets.data.PetContract.PetEntry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.IllegalFormatException;

/**
 * {@link ContentProvider} for pets app
 */
public class PetProvider extends ContentProvider {


    public PetProvider() {
    }

    /**
     * 获取类的名字,用于日志调用显示
     */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();


    /**
     * URI matcher code for the content URI for the pets table
     */
    private static final int PETS = 100;

    /**
     * URI matcher code for the content URI for a single pet in the pets table;
     */
    private static final int PETS_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     * <p>
     * The argument to the constructor specifies the value to return if there is no match.
     * As a best practice, use UriMatcher.NO_MATCH.
     * https://google-developer-training.github.io/android-developer-fundamentals-course-practicals/en/Unit%204/111a_p_implement_a_minimalist_content_provider.html
     * 这里s是static的首字母
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    // Static initializer. This is run the first time anything is called from this class.

    /**
     * Static 初始化.这个类的任何东西被调用,这里都会第一时间运行
     */
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // TODO: Add 2 content URIs to URI matcher

        /**
         * 将content URI 与 code 绑定
         */
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);
        Log.i(LOG_TAG, "static initializer: 1" + sUriMatcher);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PETS_ID);
        Log.i(LOG_TAG, "static initializer: 2" + sUriMatcher);

    }

    /**
     * databaseHelper object
     * 数据库对象实例，用于访问数据库
     * 声明一个数据库DbHelper的全局变量
     * m是member的首字母
     */

    PetDbHelper mDbHelpler;

    /**
     * Initialize the provider and the database helper object.
     *
     * @return
     */
    @Override
    public boolean onCreate() {

        /**
         * 实例化一个数据库PetDbHelper的实例对象,用于初始化petDbHelper.
         */
        mDbHelpler = new PetDbHelper(getContext());
        Log.i(LOG_TAG, "mDbHelpler " + mDbHelpler);
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments,
     * and sort order.
     * projection 是字符串数组
     * selectionArgs 也是字符串数组
     * selection 和 sortOrder 是 字符串
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        /**
         * 获取 可读的(readable) database 对象
         * Get readable database
         */

        SQLiteDatabase database = mDbHelpler.getReadableDatabase();
        Log.i(LOG_TAG, "Cursor query: " + database);
        /**
         * cursor 用于存储返回的结果
         * This cursor will hold the result of the query
         */
        Cursor cursor;

        /**
         * 临时测试
         */
//        Uri CONTENT_URI = Uri.parse("content://com.example.android.pets/pets/2");
//        int match = sUriMatcher.match(CONTENT_URI);

//        int match = sUriMatcher.match(PetEntry.CONTENT_URI);

        /**
         * Figure out if the URI matcher can match the URI to a specific code.
         * 搞清楚URI matcher 是否可以匹配到一个特定的URI code
         */
        int match = sUriMatcher.match(uri);
        switch (match) {

            case PETS:
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                cursor = database.query(PetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;


            case PETS_ID:

                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.

                selection = PetEntry._ID + "=?";

                /**
                 * 测试第二种pattern
                 */
//                long a = ContentUris.parseId(CONTENT_URI);
//                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(CONTENT_URI))};
//                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(PetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown uri " + uri);

        }


        return cursor;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        /**
         * 变量值不再更改
         */
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPet(uri, contentValues);
            default:
                /**
                 * 如果插入失败，抛出异常
                 */
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */

    private Uri insertPet(Uri uri, ContentValues values) {

        // TODO: Insert a new pet into the pets database table with the given ContentValues

        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it


        /**
         * 创建一个数据库对象用于插入数据库
         * Get writeable database
         */
        SQLiteDatabase database = mDbHelpler.getReadableDatabase();

        /**
         * Insert the new pet with the given values
         */
        long id = database.insert(PetEntry.TABLE_NAME, null, values);

//        if (id > 0) {
//            Toast.makeText(getContext(), R.string.toast_succeed, Toast.LENGTH_SHORT).show();
//
//            return ContentUris.withAppendedId(uri, id);
//        } else {
//            Toast.makeText(getContext(), R.string.toast_failed, Toast.LENGTH_SHORT).show();
//            throw new IllegalArgumentException("Insertion is not supported for " + uri);
//        }


        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);


    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
