package com.nitroapps.emoticons

import android.arch.persistence.room.*
import com.nitroapps.emoticons.helpers.*

@Entity
class EmoticonEntity(@ColumnInfo var category: Int, @ColumnInfo var value: String, @ColumnInfo var isFavorite: Boolean) {

    @PrimaryKey(autoGenerate = true) var uid: Int? = null

    @Dao
    interface EmoticonDAO {
        @Query("SELECT * FROM EmoticonEntity")
        fun getAll(): List<EmoticonEntity>

        @Query("SELECT * FROM EmoticonEntity WHERE isFavorite")
        fun getFavorites(): List<EmoticonEntity>

        @Query("SELECT * FROM EmoticonEntity WHERE category = :catId")
        fun getByCategory(catId: Int): List<EmoticonEntity>

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insertAll(dataEntities: List<EmoticonEntity>)

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        fun insertOne(emoticon: EmoticonEntity)

        @Query("UPDATE EmoticonEntity SET isFavorite = :isFavorite WHERE uid = :id")
        fun changeFavoriteStatus(id : Int, isFavorite: Boolean)
    }

    companion object {
        @Ignore
        fun populateData(): ArrayList<EmoticonEntity> {
            val result = arrayListOf<EmoticonEntity>()

            result.addAll(HelperClass1.generateData())
            result.addAll(HelperClass2.generateData())
            result.addAll(HelperClass3.generateData())
            result.addAll(HelperClass4.generateData())

            return result
        }
    }
}