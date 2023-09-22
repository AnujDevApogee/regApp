package com.apogee.registration.instance


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.apogee.registration.dao.ModelDao
import com.apogee.registration.model.getmodel.Model


@Database(
    entities = [Model::class],
    version = 1,
    exportSchema = false
)
abstract class RoomDataBaseInstance : RoomDatabase() {

    abstract fun ModelDao(): ModelDao

    companion object {
        private const val DatabaseName = "RoomDatabaseModel"

        @Volatile
        private var INSTANCE: RoomDataBaseInstance? = null

        fun getInstance(context: Context): RoomDataBaseInstance {
            synchronized(this) {
                val instance = INSTANCE
                if (instance != null) {
                    return instance
                }

                synchronized(this) {
                    val oldInstance = Room.databaseBuilder(
                        context.applicationContext,
                        RoomDataBaseInstance::class.java,
                        DatabaseName
                    ).fallbackToDestructiveMigration().build()

                    INSTANCE = oldInstance
                    return oldInstance
                }
            }
        }
    }

}