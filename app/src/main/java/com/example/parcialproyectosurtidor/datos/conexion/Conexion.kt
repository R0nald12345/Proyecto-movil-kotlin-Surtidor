package com.example.parcialproyectosurtidor.datos.conexion

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Conexion(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "surtidor_db"
        private const val DATABASE_VERSION = 1

        private const val TABLA_SURTIDOR = "Surtidor"
        private const val TABLA_COMBUSTIBLE = "TipoCombustible"
        private const val TABLA_STOCK = "StockCombustible"
    }

    // Método que se ejecuta cuando se crea la base de datos
    override fun onCreate(db: SQLiteDatabase) {
        // Crear tabla Surtidor
        db.execSQL(""" 
            CREATE TABLE $TABLA_SURTIDOR (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                latitud REAL NOT NULL,
                longitud REAL NOT NULL
            )
        """)

        // Crear tabla TipoCombustible
        db.execSQL("""
            CREATE TABLE $TABLA_COMBUSTIBLE (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL
            )
        """)

        // Crear tabla StockCombustible
        db.execSQL("""
            CREATE TABLE $TABLA_STOCK (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_surtidor INTEGER NOT NULL,
                id_tipo_combustible INTEGER NOT NULL,
                cantidad REAL NOT NULL,
                nroBombas INTEGER NOT NULL,
                FOREIGN KEY (id_surtidor) REFERENCES $TABLA_SURTIDOR(id),
                FOREIGN KEY (id_tipo_combustible) REFERENCES $TABLA_COMBUSTIBLE(id)
            )
        """)

        // Insertar tipos de combustible
        db.execSQL("INSERT INTO $TABLA_COMBUSTIBLE (nombre) VALUES ('Gasolina')")
        db.execSQL("INSERT INTO $TABLA_COMBUSTIBLE (nombre) VALUES ('Diésel')")

        // Insertar surtidores
        val surtidores = listOf(
            Triple("Biopetrol-Refineria Oriental", -17.768984, -63.171002),
            Triple("Biopetrol-Beni", -17.769411, -63.178779),
            Triple("Biopetrol-cabezas", -18.787425, -63.314198),
            Triple("Biopetrol-Berrea", -17.837367, -63.238185),
            Triple("Virgen de Cotoca", -17.782294, -63.163588),
            Triple("Biopetrol-Equipetrol", -17.754442, -63.197113),
            Triple("Biopetrol-Gasco", -17.759355, -63.179401),
            Triple("Biopetrol-Teca", -17.764088, -63.071413),
            Triple("Biopetrol-Lopez", -17.725680, -63.165386),
            Triple("Biopetrol-Paragua", -17.764901, -63.149497),
            Triple("Biopetrol-PiraiCamiri", -17.022670, -63.532602),
            Triple("Biopetrol-Pirai", -17.785829, -63.204411),
            Triple("Biopetrol-Royal", -17.805768, -63.197941),
            Triple("Biopetrol-ES Sur", -17.799900, -63.180483),
            Triple("Biopetrol-Viru Viru", -17.675913, -63.159031)
        )

        surtidores.forEach { (nombre, lat, lon) ->
            db.execSQL("""
                INSERT INTO $TABLA_SURTIDOR (nombre, latitud, longitud)
                VALUES ('$nombre', $lat, $lon)
            """)
        }

        // Insertar stock con bombas por tipo de combustible
        //1 Gasolina
        //2 Disel
        db.execSQL("INSERT INTO $TABLA_STOCK (id_surtidor, id_tipo_combustible, cantidad, nroBombas) VALUES (1, 2, 10000.0, 4)")
        db.execSQL("INSERT INTO $TABLA_STOCK (id_surtidor, id_tipo_combustible, cantidad, nroBombas) VALUES (2, 2, 10000.0, 3)")
        db.execSQL("INSERT INTO $TABLA_STOCK (id_surtidor, id_tipo_combustible, cantidad, nroBombas) VALUES (3, 1, 10000.0, 4)")
        db.execSQL("INSERT INTO $TABLA_STOCK (id_surtidor, id_tipo_combustible, cantidad, nroBombas) VALUES (4, 1, 10000.0, 2)")
        db.execSQL("INSERT INTO $TABLA_STOCK (id_surtidor, id_tipo_combustible, cantidad, nroBombas) VALUES (5, 1, 10000.0, 3)")

        db.execSQL("INSERT INTO $TABLA_STOCK (id_surtidor, id_tipo_combustible, cantidad, nroBombas) VALUES (6, 1, 10000.0, 4)")
        db.execSQL("INSERT INTO $TABLA_STOCK (id_surtidor, id_tipo_combustible, cantidad, nroBombas) VALUES (7, 1, 10000.0, 3)")
        db.execSQL("INSERT INTO $TABLA_STOCK (id_surtidor, id_tipo_combustible, cantidad, nroBombas) VALUES (8, 1, 10000.0, 4)")
        db.execSQL("INSERT INTO $TABLA_STOCK (id_surtidor, id_tipo_combustible, cantidad, nroBombas) VALUES (9, 1, 10000.0, 2)")
        db.execSQL("INSERT INTO $TABLA_STOCK (id_surtidor, id_tipo_combustible, cantidad, nroBombas) VALUES (10, 1, 10000.0, 3)")

        db.execSQL("INSERT INTO $TABLA_STOCK (id_surtidor, id_tipo_combustible, cantidad, nroBombas) VALUES (11, 1, 10000.0, 4)")
        db.execSQL("INSERT INTO $TABLA_STOCK (id_surtidor, id_tipo_combustible, cantidad, nroBombas) VALUES (12, 1, 10000.0, 3)")
        db.execSQL("INSERT INTO $TABLA_STOCK (id_surtidor, id_tipo_combustible, cantidad, nroBombas) VALUES (13, 1, 10000.0, 4)")
        db.execSQL("INSERT INTO $TABLA_STOCK (id_surtidor, id_tipo_combustible, cantidad, nroBombas) VALUES (14, 1, 10000.0, 2)")
        db.execSQL("INSERT INTO $TABLA_STOCK (id_surtidor, id_tipo_combustible, cantidad, nroBombas) VALUES (15, 1, 10000.0, 3)")
    }

    // Método que se ejecuta cuando la versión de la base de datos se actualiza
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLA_STOCK")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_COMBUSTIBLE")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_SURTIDOR")
        onCreate(db)
    }

    // Método para obtener la conexión a la base de datos
    fun getConnection(): SQLiteDatabase {
        return this.writableDatabase
    }


}
