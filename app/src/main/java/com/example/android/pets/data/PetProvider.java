package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.android.pets.data.PetContract.PetEntry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.IllegalFormatException;

/**
 * {@link ContentProvider} for pets app
 */
public class PetProvider extends ContentProvider {

    /**
     * 获取类的名字,用于日志调用显示
     */
    public static final String LOG_TAG = PetDbHelper.class.getSimpleName();


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
     *
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
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PETS_ID);

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
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        /**
         * 获取 可读的(readable) database 对象
         */

        SQLiteDatabase database = mDbHelpler.getReadableDatabase();

        /**
         * cursor 用于存储返回的结果
         */
        Cursor cursor = null;
        int match = sUriMatcher.match(PetEntry.CONTENT_URI);

        switch (match) {
            case PETS:
                cursor = database.query(PetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;
            case PETS_ID:
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
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
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
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
