package com.nitroapps.emoticons

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.nitroapps.emoticons.events.DBCreatedEvent
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.Executors


@Database(entities = [EmoticonEntity::class], version = 1)
abstract class EmoticonsDatabase : RoomDatabase() {
    abstract fun emoticonDAO(): EmoticonEntity.EmoticonDAO

    companion object {
        private var INSTANCE: EmoticonsDatabase? = null

        fun getInstance(context: Context): EmoticonsDatabase? {
            if (INSTANCE == null) {
                synchronized(EmoticonsDatabase::class)
                {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        EmoticonsDatabase::class.java,
                        "emoticons.db"
                    ).addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Executors.newSingleThreadScheduledExecutor()
                                .execute {
                                    getInstance(context)?.emoticonDAO()?.insertAll(EmoticonEntity.populateData())
                                    EventBus.getDefault().post(DBCreatedEvent(true))
                                }
                        }

//                        override fun onOpen(db: SupportSQLiteDatabase) {
//                            super.onOpen(db)
//                            EventBus.getDefault().post(DBCreatedEvent(false))
//                        }
                    }).build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}