package com.example.android.pets.data;

import android.provider.BaseColumns;

/**
 * Created by ... 12-27-2021
 * The reason whey use final
 * 1.只用来提供常量
 * 2.不需要扩展或为此外部类实现任何内容
 *
 */


/**
 * API Contract for the Pets app.
 */
public final class PetContract {
    /**
     * 防止别人偶然初始化该类，将构造器私有化的空构造器
     *  To prevent someone from accidentally instantiating the contract class,
     *  give it an empty constructor.
     */
    private PetContract() {};


    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single pet.
     *
     * 内部类定义pets 数据库表的常量值
     * 表中的每个条目代表一个pet
     *
     */
    public static class PetEntry implements BaseColumns{

        /**
         * 创建表名称常量和列名称常量
         * Name of database table for pets.
         */

        public static final String TABLE_NAME = "pets";
//        public static final String COLUMN_PET_ID = "_id";

        /**
         * Unique ID number for the pet (only for use in the database table).
         *PET唯一ID（仅用于数据库表）
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;


        /**
         * Name of the pet
         *
         * TYPE: TEXT
         */

        public static final String COLUMN_PET_NAME = "name";

        /**
         * Breed of the pet.
         *
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
         *
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
