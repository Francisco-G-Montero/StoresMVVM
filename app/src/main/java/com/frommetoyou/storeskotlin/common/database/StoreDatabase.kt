package com.frommetoyou.storeskotlin.common.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.frommetoyou.storeskotlin.common.database.StoreDao
import com.frommetoyou.storeskotlin.common.entities.StoreEntity

@Database(entities = [StoreEntity::class], version = 3)
abstract class StoreDatabase : RoomDatabase() {
    abstract fun storeDao(): StoreDao
}