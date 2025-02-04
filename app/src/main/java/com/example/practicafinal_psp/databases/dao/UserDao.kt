package com.example.practicafinal_psp.databases.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.practicafinal_psp.databases.entities.User
import kotlin.coroutines.Continuation

@Dao
interface UserDao {
    @Query("SELECT * FROM usuario_tabla")
    fun getAll(): List<User>

    @Query("SELECT * FROM usuario_tabla WHERE id IN (:ids)")
    fun loadAllByIds(ids: IntArray): List<User>

    @Query("SELECT * FROM usuario_tabla WHERE email LIKE '%' || :search || '%' OR contraseña LIKE '%' || :search || '%'")
    fun buscarByEmailOrPassword(search: String): List<User>

    @Insert
    suspend fun insert(user: User)

    @Delete
    fun delete(user: User)
}