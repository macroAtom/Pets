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
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
//import android.support.v4.app.NavUtils;
//import android.support.v7.app.AppCompatActivity;
import android.provider.UserDictionary;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDbHelper;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * ?????????????????????Loader,????????????????????????
     */
    private static final int PET_LOADER_EDITOR = 1;

    /**
     * boolean ???????????????pet???????????????????????????
     */

    private boolean mPetHasChanged = false;


    // ???????????????
    public static final String LOG_TAG = EditorActivity.class.getSimpleName();

    /**
     * ????????????Uri ????????????
     * Content URI for the existing pet (null if it's a new pet)
     */

    private Uri mCurrentPetUri;

    /**
     * Database helper that will provide us access to the database
     */
    private PetDbHelper mDbHelper;

    /**
     * EditText field to enter the pet's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the pet's breed
     */
    private EditText mBreedEditText;

    /**
     * EditText field to enter the pet's weight
     */
    private EditText mWeightEditText;

    /**
     * EditText field to enter the pet's gender
     */
    private Spinner mGenderSpinner;

    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Log.i(LOG_TAG, "onCreate: ??????2 ");
        // mDbHelper = new PetDbHelper(this);

        // Use getIntent() and getData() to get the associated URI
        // getIntent();

        // Examine the intent that was used to launch this activity
        // in order to figure out if we're creating a new pet or editing an existing one.
        // ??????intent
        Intent intent = getIntent();
        Log.i(LOG_TAG, "onCreate: ??????3 ");
        Log.i(LOG_TAG, "onCreate:getIntent() getData() " + intent);
        // ??????Uri
        mCurrentPetUri = intent.getData();
        Log.i(LOG_TAG, "onCreate: ??????4 ");
//        if(uri != null){
//            Log.i(LOG_TAG, "onCreate:getIntent() getData() " + uri);
//            this.setTitle("Edit Pet");
//        } else {
//            this.setTitle(R.string.editor_activity_title_new_pet);
//        }

        // if the intent DOES NOT contain a pet content URI, then we know that we are creating a new pet.
        if (mCurrentPetUri == null) {
            /**
             * ??????insert mode,??????mCurrentPetUri ??????
             */
            Log.i(LOG_TAG, "onCreate:getIntent() getData() " + mCurrentPetUri);
            // This is a new pet, so change the app bar to say "Add a Pet"
            this.setTitle(getString(R.string.editor_activity_title_new_pet));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a pet that hasn't been created yet.)
            invalidateOptionsMenu();

        } else {
            // otherwise this is an existing pet, so change app bar to say "Edit Pet"

            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = LoaderManager.getInstance(this);
            Log.i(LOG_TAG, "onCreate: ??????5 ");
            // ?????????Loader; kick off the loader
            loaderManager.initLoader(PET_LOADER_EDITOR, null, this).forceLoad();

            /**
             * ??????update mode,??????mCurrentPetUri ?????????
             */
            Log.i(LOG_TAG, "onCreate: ??????6 ");
            this.setTitle(getString(R.string.editor_activity_title_edit_pet));
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        setupSpinner();


        /**
         * ???????????????????????????????????????????????????????????????mPetHasChanged ?????????true;
         */
        mNameEditText.setOnTouchListener(mTouchListener);
        mBreedEditText.setOnTouchListener(mTouchListener);
        mWeightEditText.setOnTouchListener(mTouchListener);
        mGenderSpinner.setOnTouchListener(mTouchListener);

    }

    /**
     * ??????view??????????????????
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mPetHasChanged = true;
            return false;
        }
    };

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.unsaved_changes_dialog_msg);

        builder.setPositiveButton(R.string.discard, discardButtonClickListener);

        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    /**
     * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     */
    @Override
    public void onBackPressed() {
        Log.i(LOG_TAG, "onBackPressed: " + "??????1");
        // If the pet hasn't changed, continue with handling back button press
        if (!mPetHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };


        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);

    }


    //    private void showUnsavedChangesDialog(
//            DialogInterface.OnClickListener discardButtonClickListener) {
//        // Create an AlertDialog.Builder and set the message, and click listeners
//        // for the positive and negative buttons on the dialog.
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage(R.string.unsaved_changes_dialog_msg);
//        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
//        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                // User clicked the "Keep editing" button, so dismiss the dialog
//                // and continue editing the pet.
//                if (dialog != null) {
//                    dialog.dismiss();
//                }
//            }
//        });
//
//        // Create and show the AlertDialog
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
//    }


    /**
     * OnTouchListener than listens for any user touches on a view, implying that
     * the view, and we change the mPetHasChanged boolean to true
     */

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetEntry.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetEntry.GENDER_FEMALE; // Female
                    } else {
                        mGender = PetEntry.GENDER_UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = 0; // Unknown
            }
        });
    }


    /**
     * ??????edittext ??????????????????
     * Get user input from editor and save new pet into database.
     */

    private void savePet() {

        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        /**
         * ??????editor ?????????????????????
         */
        String nameString = mNameEditText.getText().toString().trim();
        String breedString = mBreedEditText.getText().toString().trim();
        String weightString = mWeightEditText.getText().toString().trim();
        int weight = 0;
        if (!TextUtils.isEmpty(weightString)) {
            weight = Integer.parseInt(weightString);
        }

        // ???????????????????????????????????????????????????????????????????????????????????????
        if (mCurrentPetUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(breedString) &&
                TextUtils.isEmpty(weightString) && mGender == PetEntry.GENDER_UNKNOWN) {
            return;
        }

        Log.i(LOG_TAG, "insert Pet: " + nameString);


        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME, nameString);
        values.put(PetEntry.COLUMN_PET_BREED, breedString);
        values.put(PetEntry.COLUMN_PET_GENDER, mGender);
        values.put(PetEntry.COLUMN_PET_WEIGHT, weight);

        // Insert a new pet into the provider, returning the content URI for the new pet.
        if (mCurrentPetUri == null) {

            Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI, values);
            Log.i(LOG_TAG, "newUri: " + newUri);
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_pet_failed), Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_pet_successful), Toast.LENGTH_SHORT).show();
            }

        } else {

            // ??????????????????
//            String selection = PetEntry._ID + "=?";
//            String[] selectionArgs = new String[]{String.valueOf(ContentUris.parseId(mCurrentPetUri))};
//
//            int updateId = getContentResolver().update(
//                    mCurrentPetUri,
//                    values,
//                    selection,
//                    selectionArgs
//
//            );
//
//            if(updateId !=0){
//                Toast.makeText(this, getString(R.string.editor_update_pet_successful), Toast.LENGTH_SHORT).show();
//            }else{
//                Toast.makeText(this, getString(R.string.editor_update_pet_failed), Toast.LENGTH_SHORT).show();
//            }

            // ??????????????????
            // Otherwise this is an EXISTING pet, so update the pet with content URI: mCurrentPetUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentPetUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentPetUri, values, null, null);
            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }

        }


        //Show a toast message depending on whether or not the insertion was successful
//        if (newUri == null) {
//
//            // If the new content URI is null, then there was an error with insertion.
//            Toast.makeText(this, getString(R.string.editor_insert_pet_failed), Toast.LENGTH_SHORT).show();
//
//        } else {
//            // Otherwise, the insertion was successful and we can display a toast.
//            Toast.makeText(this, getString(R.string.editor_insert_pet_successful), Toast.LENGTH_SHORT).show();
//
//        }


    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.i(LOG_TAG, "onPrepareOptionsMenu: ?????? 1");
        super.onPrepareOptionsMenu(menu);

        /**
         * ??????????????????????????????delete??????
         */
        if (mCurrentPetUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }


    /**
     * ??????edittext ??????????????????
     * Get user input from editor and save new pet into database.
     */

//    private void insertPet() {
//
//        // Read from input fields
//        // Use trim to eliminate leading or trailing white space
//        /**
//         * ??????editor ?????????????????????
//         */
//        String nameString = mNameEditText.getText().toString().trim();
//        String breedString = mBreedEditText.getText().toString().trim();
//        String weightString = mWeightEditText.getText().toString().trim();
//        int weight;
//        if (!TextUtils.isEmpty(weightString)) {
//            weight = Integer.parseInt(weightString);
//        } else {
//            weight = 0;
//        }
//
//        Log.i(LOG_TAG, "insert Pet: " + weight);
//
//
//        // Create a ContentValues object where column names are the keys,
//        // and pet attributes from the editor are the values.
//        ContentValues values = new ContentValues();
//        values.put(PetEntry.COLUMN_PET_NAME, nameString);
//        values.put(PetEntry.COLUMN_PET_BREED, breedString);
//        values.put(PetEntry.COLUMN_PET_GENDER, mGender);
//        values.put(PetEntry.COLUMN_PET_WEIGHT, weight);
//
//        // Insert a new pet into the provider, returning the content URI for the new pet.
//        Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI, values);
//
//        //Show a toast message depending on whether or not the insertion was successful
//        if (newUri == null) {
//
//            // If the new content URI is null, then there was an error with insertion.
//            Toast.makeText(this, getString(R.string.editor_insert_pet_failed), Toast.LENGTH_SHORT).show();
//
//        } else {
//            // Otherwise, the insertion was successful and we can display a toast.
//            Toast.makeText(this, getString(R.string.editor_insert_pet_successful), Toast.LENGTH_SHORT).show();
//
//        }
//
//
////        // Insert a new row for pet in the database, returning the ID of that new row.
////        long newRowId = db.insert(PetEntry.TABLE_NAME, null, values);
////
////        // Show a toast message depending on whether or not the insertion was successful
////        if (newRowId == -1) {
////            // If the row ID is -1, then there was an error with insertion.
////            Toast.makeText(this, "Error with saving pet", Toast.LENGTH_SHORT).show();
////        } else {
////            // Otherwise, the insertion was successful and we can display a toast with the row ID.
////            Toast.makeText(this, "Pet saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
////        }
//
//    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(LOG_TAG, "onCreateOptionsMenu: ??????7 ");
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(LOG_TAG, "onOptionsItemSelected: ??????8 ");
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Do nothing for now
                savePet();

                // Navigate back to parent activity (CatalogActivity)
                /**
                 * ??????????????????????????????????????????????????????
                 */
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            /**
             * ????????????????????????????????????
             * Respond to a click on the "Delete" menu option
             */
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;

            /**
             * ???????????????????????????????????????????????????????????????????????????????????????Activity???
             * Respond to a click on the "Up" arrow button in the app bar
             */
            case android.R.id.home:
                Log.i(LOG_TAG, "onOptionsItemSelected: " + "?????? 2");
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mPetHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                // Navigate back to parent activity (CatalogActivity)
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED,
                PetEntry.COLUMN_PET_GENDER,
                PetEntry.COLUMN_PET_WEIGHT};

        switch (id) {
            case PET_LOADER_EDITOR:
                /**
                 * ??????????????????CursorLaoder
                 */
                return new CursorLoader(
                        this,                // Parent activity context
                        mCurrentPetUri,              // Table to query???Query the content URI for the current pet
                        projection,                 // Projection to return???Columns to include in the resulting Cursor
                        null,               // No selection clause
                        null,            // No selection arguments
                        null                // Default sort order
                );
            default:
                // An invalid id was passed in
                /**
                 * ??????id??????
                 */
                return null;
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        Log.i(LOG_TAG, "onLoadFinished: " + loader + ". \nCursor: " + cursor);


        if (cursor.moveToFirst()) {
            /// Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
            int breedColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);
            int genderColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
            int weightColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String breed = cursor.getString(breedColumnIndex);
            int gender = cursor.getInt(genderColumnIndex);
            Log.i(LOG_TAG, " gender " + gender);
            int weight = cursor.getInt(weightColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mBreedEditText.setText(breed);
            mWeightEditText.setText(Integer.toString(weight));
            // Gender is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Unknown, 1 is Male, 2 is Female).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (gender) {
                case PetEntry.GENDER_MALE:
                    mGenderSpinner.setSelection(1);
                    break;
                case PetEntry.GENDER_FEMALE:
                    mGenderSpinner.setSelection(2);
                    break;
                default:
                    mGenderSpinner.setSelection(0);
                    break;
            }

        }

        /**
         * find the column of pet attributes that we're interested in
         * ???????????????????????????
         */

        /**
         * ??????name ??????
         */
//        int nameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
//
//        /**
//         * ???????????? ??????
//         */
//        int breedColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);
//
//        /**
//         * ???????????? ??????
//         */
//        int genderColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
//
//        /**
//         * ???????????? ??????
//         */
//        int weightColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);
//
//        /**
//         * ??????name???breed??????cursor??????
//         */
//        cursor.moveToFirst();
//        String petName = cursor.getString(nameColumnIndex);
//        String petBreed = cursor.getString(breedColumnIndex);
//        String petGender = cursor.getString(genderColumnIndex);
//        String petWeight = cursor.getString(weightColumnIndex);
//
//        /**
//         * ????????????textView ??????
//         */
//
//        /**
//         * ??????name
//         */
//        mNameEditText.setText(petName);
//        /**
//         * ????????????
//         */
//        mBreedEditText.setText(petBreed);
//
//        /**
//         * ????????????
//         */
//        mGenderSpinner.setSelection(Integer.parseInt(petGender));
//
//        /**
//         * ????????????
//         */
//        mWeightEditText.setText(petWeight);


    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.i(LOG_TAG, "onLoaderReset: " + loader);

//        /**
//         * ??????????????????
//         */
//        mNameEditText = null;
//        mBreedEditText = null;
//        mGenderSpinner = null;
//        mWeightEditText = null;

        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mBreedEditText.setText("");
        mWeightEditText.setText("");
        mGenderSpinner.setSelection(0); // Select "Unknown" gender

    }


    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deletePet() {
        // TODO: Implement this method
        // ?????? ????????????

        // ??????????????????
//        String selection = PetEntry._ID + " = ?";
//        String[] selectionArgs = new String[]{String.valueOf(ContentUris.parseId(mCurrentPetUri))};
//
//        int rowsAffected = getContentResolver().delete(mCurrentPetUri, selection,selectionArgs);
//
//        /**
//         * ????????????????????????
//         */
//        if(rowsAffected>0){
//            Toast.makeText(this, R.string.editor_delete_pet_successful, Toast.LENGTH_SHORT).show();
//        }else {
//            Toast.makeText(this, R.string.editor_delete_pet_failed, Toast.LENGTH_SHORT).show();
//        }
//
//
//        finish();


        // Only perform the delete if this is an existing pet.


        // Only perform the delete if this is an existing pet.
        if (mCurrentPetUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentPetUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_pet_successful),
                        Toast.LENGTH_SHORT).show();
            }

            // Close the activity
            finish();
        }
    }
}