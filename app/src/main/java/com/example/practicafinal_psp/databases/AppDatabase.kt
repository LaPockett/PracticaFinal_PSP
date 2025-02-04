package com.example.practicafinal_psp.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.practicafinal_psp.databases.dao.UserDao
import com.example.practicafinal_psp.databases.entities.User

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun userDao() : UserDao
}