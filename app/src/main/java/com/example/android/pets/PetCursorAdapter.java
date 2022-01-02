package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;


import com.example.android.pets.data.PetContract.PetEntry;

public class PetCursorAdapter extends CursorAdapter {

    // 获取类名称
    public static final String LOG_TAG = PetCursorAdapter.class.getSimpleName();

    /**
     * 构建一个新的 {@link PetCursorAdapter}
     *
     * @param context the context.
     * @param c       the cursor from which to get the data.
     */

    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c, 0/*flags*/);
    }

    /**
     * The newView method is used to inflate a new view and return it,
     * you don't bind any data to the view at this point.
     * 膨胀一个空的新的视图，这里无需填入数据
     * Makes a new blank list view. No data is set(or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved
     *                to the correct position
     * @param parent  The parent to which the new view is attached to.
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.i(LOG_TAG, "newView: " + cursor);
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

    }

    /**
     * 这里将获取的数据绑定到在newView 中膨胀的新视图
     * The bindView method is used to bind all data to a given view
     * such as setting the text on a TextView.
     * <p>
     * This method binds the pet data(int the current row pointed to by cursor) to the given list
     * item layout. For example, the name for the current pet can be set on the TextView in the
     * list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. the cursor is already moved to the correct
     *                position now.
     */

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.i(LOG_TAG, "bindView: " + cursor);
        /**
         * 找到name TextView id 并存储到TextView name对象中
         */
        TextView nameTextView = view.findViewById(R.id.name);
        Log.i(LOG_TAG, "bindView: " + nameTextView);
        /**
         * 找到品种 TextView id 并存储到TextView breed对象中
         */
        TextView summaryTextView = view.findViewById(R.id.summary);


        /**
         * find the column of pet attributes that we're interested in
         * 找到我们感兴趣的列
         */

        int nameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
        int breedColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);

        /**
         * 提取name、breed值从cursor里面
         */
        String petName = cursor.getString(nameColumnIndex);
        String petBreed = cursor.getString(breedColumnIndex);

        /**
         * 填充值到textView 里面
         */
        nameTextView.setText(petName);
        summaryTextView.setText(petBreed);
    }
}
