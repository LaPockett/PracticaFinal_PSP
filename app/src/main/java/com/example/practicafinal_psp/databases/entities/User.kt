package com.example.practicafinal_psp.databases.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuario_tabla") // Declaramos en nombre de la tabla
data class User (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,//ID único
    @ColumnInfo(name = "email") val nombre: String?,
    @ColumnInfo(name = "contraseña") val apellido: String?,

)