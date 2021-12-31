package com.example.android.pets.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.net.URI;

/**
 * Created by ... 12-27-2021
 * The reason whey use final
 * 1.只用来提供常量
 * 2.不需要扩展或为此外部类实现任何内容
 */


/**
 * API Contract for the Pets app.
 */
public final class PetContract {
    /**
     * 创建content authority
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.pets";

    /**
     * 组建基本Uri
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * 创建表名路径
     * This constants stores the path for each of the tables which will be appended to the base content URI.
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.pets/pets/ is a valid path for
     * looking at pet data. content://com.example.android.pets/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_PETS = "pets";


    /**
     * 防止别人偶然初始化该类，将构造器私有化的空构造器
     * To prevent someone from accidentally instantiating the contract class,
     * give it an empty constructor.
     */
    private PetContract() {
    }

    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single pet.
     * <p>
     * 内部类定义pets 数据库表的常量值
     * 表中的每个条目代表一个pet
     */
    public static class PetEntry implements BaseColumns {


        /**
         * 完成的content_uri
         * The content URI to access the pet data in the provider
         */

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);


        /**
         * 创建表名称常量和列名称常量
         * Name of database table for pets.
         */

        public static final String TABLE_NAME = "pets";

        /**
         * Unique ID number for the pet (only for use in the database table).
         * PET唯一ID（仅用于数据库表）
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;


        /**
         * Name of the pet
         * <p>
         * TYPE: TEXT
         */

        public static final String COLUMN_PET_NAME = "name";

        /**
         * Breed of the pet.
         * <p>
         * Type: TEXT
         */

        public static final String COLUMN_PET_BREED = "breed";

        /**
         * Gender of the pet
         * The only possible values are {@link #GENDER_UNKNOWN}, {@link #GENDER_MALE},
         * or {@link #GENDER_FEMALE}.
         * Type:INTEGER
         */

        public static final String COLUMN_PET_GENDER = "gender";

        /**
         * Weight of the pet
         * <p>
         * Type: INTEGER
         */
        public static final String COLUMN_PET_WEIGHT = "weight";

        /**
         * 创建性别常量
         * Possible values for the gender of the pet.
         */
        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;
    }

}
