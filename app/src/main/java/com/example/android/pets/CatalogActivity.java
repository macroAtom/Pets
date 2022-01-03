/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
//import android.support.design.widget.FloatingActionButton;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDbHelper;
import com.example.android.pets.data.PetProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.net.URI;

/**
 * Displays list of pets that were entered and stored in the app.
 */

/**
 * LoaderCallbacks中的参数,是作为Loader 的返回结果. 例如:Cursor,即 作为Loader的返回结果
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    // Identifies a particular Loader being used in this component
    /**
     * 标识一个特定的Loader,在这个组件中使用
     */
    private static final int PET_LOADER = 0;

    // 获取类名称
    public static final String LOG_TAG = CatalogActivity.class.getSimpleName();

    // To access our database, we instantiate our subclass of SQLiteOpenHelper
    // and pass the context, which is the current activity.
    /**
     * Database helper that will provide us access to the database
     */
    private PetDbHelper mDbHelper;


    /**
     * 设置adapter全局变量,在onCreate 方法中初始化.
     */
    PetCursorAdapter mCursorAdapter;

    // Create and/or open a database to read from it
    // 这一步相当与在命令行执行.open shelter.db
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class).setData(null);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the pet data
        ListView petListView = (ListView) findViewById(R.id.list);

        // 设置监听器，点击listView 中的item 打开editor
        // set the item click listener
        petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                /** Form the content Uri that represents the specific pet that was clicked on.
                 * by adding the "id" (passed as input to this method) onto the
                 *{@link PetEntry#CONTENT_URI}.
                 * For example, the uri would be "content://com.example.android.pets/pets/2"
                 * if the pet with id was clicked on.
                 */

                Uri currentPetUri = Uri.withAppendedPath(PetEntry.CONTENT_URI,String.valueOf(id));

//                Uri currentPetUri = ContentUris.withAppendedId(PetEntry.CONTENT_URI,id);
                Log.i(LOG_TAG, "onItemClick: currentPetUri " + currentPetUri);
                /**
                 * Set the uri on the data field of the intent
                 */
                intent.setData(currentPetUri);

                /**
                 * launch the intent {@link EditorActivity} to display the data for the current pet.
                 */
                startActivity(intent);
            }
        });

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new PetDbHelper(this);
        mCursorAdapter = new PetCursorAdapter(this, null);
        /**
         * Attach the adapter to the VistView
         */
        petListView.setAdapter(mCursorAdapter);
        // Get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = LoaderManager.getInstance(this);

        // 初始化Loader; kick off the loader
        loaderManager.initLoader(PET_LOADER, null, this).forceLoad();
        /**
         * 创建Provider的实例
         */

//        PetProvider petProvider = new PetProvider();
//        Uri CONTENT_URI = Uri.parse("content://com.example.android.pets/pets");

//        String queryUri = PetEntry.CONTENT_URI.toString();
//        Cursor cursor = getContentResolver().query(Uri.parse(queryUri), null, null, null, null);
//        Log.i(LOG_TAG, "onCreate: " + cursor);


        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        petListView.setEmptyView(emptyView);


        /**
         * 设置一个适配器，为cursor中的每一行宠物数据创建一个list 条目
         * Setup an Adapter to create a list item for each row of pet data in the Cursor.
         */

        Log.i(LOG_TAG, "displayDatabaseInfo: " + mCursorAdapter);


    }

    /**
     * EditorActivity 完成编辑后，重新进入会执行的方法
     */
//    @Override
//    protected void onStart() {
//        super.onStart();
//    }


    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     * 临时方法，显示信息在屏幕上，关于pets Database
     */
//    private void displayDatabaseInfo() {
//
//        // Create and/or open a database to read from it
////        SQLiteDatabase db = mDbHelper.getReadableDatabase();
//
//        // Perform this raw SQL query "SELECT * FROM pets"
//        // to get a Cursor that contains all rows from the pets table.
////        Cursor cursor = db.rawQuery("SELECT * FROM " + PetEntry.TABLE_NAME, null);
//
//        /**
//         * Define a projection that specifies which columns from the database
//         * you will actually use after this query.
//         */
//        String[] protection = {
//                PetEntry._ID,
//                PetEntry.COLUMN_PET_NAME,
//                PetEntry.COLUMN_PET_BREED,
//                PetEntry.COLUMN_PET_GENDER,
//                PetEntry.COLUMN_PET_WEIGHT
//        };
//
//
//        Uri CONTENT_URI = Uri.parse("content://com.example.android.pets/pets");
//
//        String queryUri = CONTENT_URI.toString();
//
//        /**
//         * Perform a query on the provider using the ContentResolver;
//         * use the {@link PetEntry.CONTENT_URI} to access the pet data.
//         */
//        Cursor cursor = getContentResolver().query(
//                PetEntry.CONTENT_URI,    //Uri.parse(queryUri); The content URI of the words table.
//                protection,              // The array of columns to return (pass null to get all);The columns to return for each row.
//                null,            // The columns for the WHERE clause; Selection criteria
//                null,         // The values for the WHERE clause; Selection criteria
//                null);          // The sort order for the returned rows
//
//
//    }


    /**
     * 插入数据
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private void insertPet() {

        // Gets the database in write mode
//        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.

        ContentValues values = new ContentValues();

        values.put(PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetEntry.COLUMN_PET_GENDER, PetEntry.GENDER_MALE);
        values.put(PetEntry.COLUMN_PET_WEIGHT, 7);
        // Insert the new row, returning the primary key value of the new row
        // 返回最新行的id，如果插入为空，返回-1
//        long newRowId = db.insert(PetEntry.TABLE_NAME, null, values);
//        Log.i(LOG_TAG, "insertPet: " + newRowId);

//        getContentResolver().insert(PetEntry.CONTENT_URI, values);

        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI, values);

    }

    /**
     * 清空表数据
     */

//    private void deletePetOld() {
//        // Gets the database in write mode
//        SQLiteDatabase db = mDbHelper.getWritableDatabase();
//
//        // Define 'where' part of query.
//        String selection = PetEntry._ID + " >0";
//        // Specify arguments in placeholder order.
////        String[] selectionArgs = {"0"};
//        // Issue SQL statement.
////        int deletedRows = db.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
//
//        db.delete(PetEntry.TABLE_NAME, null, null);
//
//        Log.i(LOG_TAG, "deletePet: " + db.toString());
//
//    }


    /**
     * Helper method to delete all pets in the database.
     */
    private void deleteAllPets() {
        // TODO: Implement this method

        // Only perform the delete if this is an existing pet.

        // 我的方式
//        if (PetEntry.CONTENT_URI != null) {
//            // Call the ContentResolver to delete the pet at the given content URI.
//            // Pass in null for the selection and selection args because the mCurrentPetUri
//            // content URI already identifies the pet that we want.
//            int rowsDeleted = getContentResolver().delete(PetEntry.CONTENT_URI, null, null);
//
//            // Show a toast message depending on whether or not the delete was successful.
//            if (rowsDeleted == 0) {
//                // If no rows were deleted, then there was an error with the delete.
//                Toast.makeText(this, getString(R.string.editor_delete_pet_failed),
//                        Toast.LENGTH_SHORT).show();
//            } else {
//                // Otherwise, the delete was successful and we can display a toast.
//                Toast.makeText(this, getString(R.string.editor_delete_pet_successful),
//                        Toast.LENGTH_SHORT).show();
//            }
//        }


        int rowsDeleted = getContentResolver().delete(PetEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
//                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // 删除所有宠物
                deleteAllPets();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        String[] protection = {
                PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED
        };


        switch (id) {
            case PET_LOADER:
                /**
                 * 返回一个新的CursorLaoder
                 */
                return new CursorLoader(
                        this,                // Parent activity context
                        PetEntry.CONTENT_URI,        // Table to query
                        protection,                  // Projection to return
                        null,               // No selection clause
                        null,            // No selection arguments
                        null                // Default sort order
                );
            default:
                // An invalid id was passed in
                /**
                 * 非法id传入
                 */
                return null;
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {


        Cursor cursor = data;
        Log.i(LOG_TAG, "onLoadFinished: " + cursor);
//        adapter = new PetCursorAdapter(this, cursor);


        /**
         * Moves the query results into the adapter, causing the
         * ListView fronting this adapter to re-display
         *
         * 将返回的结果移入adapter,从而将结果显示在ListView
         * update {@link PetCursorAdapter} with the new cursor containing updated pet data
         * 用包含已更新宠物数据的新Cursor进行更新
         */

//        mCursorAdapter.changeCursor(data);
        mCursorAdapter.swapCursor(data);

    }


    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        /**
         * Clears out the adapter's reference to the Cursor.
         * This prevents memory leaks.
         *
         * 清空 adapter 对Cursor 的引用,防止内存泄露
         * Callback called when the data needs to be deleted
         * 这个回调是在需要删除数据时调用
         */
//        mCursorAdapter.changeCursor(null);
        mCursorAdapter.swapCursor(null);
    }
}
