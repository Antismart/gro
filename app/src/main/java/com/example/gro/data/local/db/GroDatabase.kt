package com.example.gro.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gro.data.local.db.dao.PlantDao
import com.example.gro.data.local.db.entity.PlantEntity

@Database(
    entities = [PlantEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class GroDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao
}
