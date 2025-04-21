package com.example.parcialproyectosurtidor.datos.conexion

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Conexion(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "surtidor_db"
        private const val DATABASE_VERSION = 1 //  versión

        private const val TABLA_SURTIDOR = "Surtidor"

        // Columnas
        private const val COLUMNA_ID = "id"
        private const val COLUMNA_NOMBRE = "nombre"
        private const val COLUMNA_LATITUD = "latitud"
        private const val COLUMNA_LONGITUD = "longitud"
        private const val COLUMNA_CANTIDAD_BOMBAS = "cantidad_bombas"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val crearTablaSurtidor = """
            CREATE TABLE $TABLA_SURTIDOR (
                $COLUMNA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMNA_NOMBRE TEXT NOT NULL,
                $COLUMNA_LATITUD REAL NOT NULL,
                $COLUMNA_LONGITUD REAL NOT NULL,
                $COLUMNA_CANTIDAD_BOMBAS INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(crearTablaSurtidor)



        // Insertar datos de ejemplo (surtidores)
        val insertarSurtidor1 = """
            INSERT INTO $TABLA_SURTIDOR ($COLUMNA_NOMBRE, $COLUMNA_LATITUD, $COLUMNA_LONGITUD,$COLUMNA_CANTIDAD_BOMBAS)
            VALUES ('Biopetrol-Refineria Oriental', -17.768984371398503, -63.171002517830594,4)
        """.trimIndent()

        val insertarSurtidor2 = """
            INSERT INTO $TABLA_SURTIDOR ($COLUMNA_NOMBRE, $COLUMNA_LATITUD, $COLUMNA_LONGITUD,$COLUMNA_CANTIDAD_BOMBAS)
            VALUES ('Biopetrol-Beni', -17.769411876342136, -63.178779689932306,5)
        """.trimIndent()

        val insertarSurtidor3 = """
            INSERT INTO $TABLA_SURTIDOR ($COLUMNA_NOMBRE, $COLUMNA_LATITUD, $COLUMNA_LONGITUD,$COLUMNA_CANTIDAD_BOMBAS)
            VALUES ('Biopetrol-cabezas', -18.78742511074283, -63.31419824696175,6)
        """.trimIndent()

        val insertarSurtidor4 = """
             INSERT INTO $TABLA_SURTIDOR($COLUMNA_NOMBRE, $COLUMNA_LATITUD, $COLUMNA_LONGITUD,$COLUMNA_CANTIDAD_BOMBAS)
             VALUES ('Biopetrol-Berrea',-17.837367519879542, -63.23818518412395,7)
        """.trimIndent()

        val insertarSurtidor5 = """
             INSERT INTO $TABLA_SURTIDOR($COLUMNA_NOMBRE, $COLUMNA_LATITUD, $COLUMNA_LONGITUD,$COLUMNA_CANTIDAD_BOMBAS)
             VALUES ('Virgen de Cotoca',-17.78229402285384, -63.16358839044966,8)
        """.trimIndent()

        // Continúa con el resto de surtidores...
        val insertarSurtidor6 = """
             INSERT INTO $TABLA_SURTIDOR($COLUMNA_NOMBRE, $COLUMNA_LATITUD, $COLUMNA_LONGITUD,$COLUMNA_CANTIDAD_BOMBAS)
             VALUES ('Biopetrol-Equipetrol',-17.754442257871663, -63.197113370180176,6)
        """.trimIndent()

        val insertarSurtidor7 = """
             INSERT INTO $TABLA_SURTIDOR($COLUMNA_NOMBRE, $COLUMNA_LATITUD, $COLUMNA_LONGITUD,$COLUMNA_CANTIDAD_BOMBAS)
             VALUES ('Biopetrol-Gasco',-17.759355084273157, -63.179401075164144,7)
        """.trimIndent()

        val insertarSurtidor8 = """
             INSERT INTO $TABLA_SURTIDOR($COLUMNA_NOMBRE, $COLUMNA_LATITUD, $COLUMNA_LONGITUD,$COLUMNA_CANTIDAD_BOMBAS)
             VALUES ('Biopetrol-Teca',-17.76408829671861,-63.071413028252856,8)
        """.trimIndent()

        val insertarSurtidor9 = """
             INSERT INTO $TABLA_SURTIDOR($COLUMNA_NOMBRE, $COLUMNA_LATITUD, $COLUMNA_LONGITUD,$COLUMNA_CANTIDAD_BOMBAS)
             VALUES ('Biopetrol-Lopez',-17.7256802052732, -63.16538626233688,6)
        """.trimIndent()

        val insertarSurtidor10 = """
             INSERT INTO $TABLA_SURTIDOR($COLUMNA_NOMBRE, $COLUMNA_LATITUD, $COLUMNA_LONGITUD,$COLUMNA_CANTIDAD_BOMBAS)
             VALUES ('Biopetrol-Paragua',-17.764901273617195, -63.14949769739089,6)
        """.trimIndent()

        val insertarSurtidor11 = """
             INSERT INTO $TABLA_SURTIDOR($COLUMNA_NOMBRE, $COLUMNA_LATITUD, $COLUMNA_LONGITUD,$COLUMNA_CANTIDAD_BOMBAS)
             VALUES ('Biopetrol-PiraiCamiri',-17.022670789240202, -63.53260202264754,6)
        """.trimIndent()

        val insertarSurtidor12 = """
             INSERT INTO $TABLA_SURTIDOR($COLUMNA_NOMBRE, $COLUMNA_LATITUD, $COLUMNA_LONGITUD,$COLUMNA_CANTIDAD_BOMBAS)
             VALUES ('Biopetrol-Pirai',-17.785829254773738, -63.20441190466365,8)
        """.trimIndent()

        val insertarSurtidor13 = """
             INSERT INTO $TABLA_SURTIDOR($COLUMNA_NOMBRE, $COLUMNA_LATITUD, $COLUMNA_LONGITUD,$COLUMNA_CANTIDAD_BOMBAS)
             VALUES ('Biopetrol-Royal',-17.805768319173673, -63.19794190651265,6)
        """.trimIndent()

        val insertarSurtidor14 = """
             INSERT INTO $TABLA_SURTIDOR($COLUMNA_NOMBRE, $COLUMNA_LATITUD, $COLUMNA_LONGITUD,$COLUMNA_CANTIDAD_BOMBAS)
             VALUES ('Biopetrol-ES Sur',-17.799900227145187, -63.18048330366337,6)
        """.trimIndent()

        val insertarSurtidor15 = """
             INSERT INTO $TABLA_SURTIDOR($COLUMNA_NOMBRE, $COLUMNA_LATITUD, $COLUMNA_LONGITUD,$COLUMNA_CANTIDAD_BOMBAS)
             VALUES ('Biopetrol-Viru Viru',-17.6759135053985, -63.15903123830914,6)
        """.trimIndent()

        db.execSQL(insertarSurtidor1)
        db.execSQL(insertarSurtidor2)
        db.execSQL(insertarSurtidor3)
        db.execSQL(insertarSurtidor4)
        db.execSQL(insertarSurtidor5)
        db.execSQL(insertarSurtidor6)
        db.execSQL(insertarSurtidor7)
        db.execSQL(insertarSurtidor8)
        db.execSQL(insertarSurtidor9)
        db.execSQL(insertarSurtidor10)
        db.execSQL(insertarSurtidor11)
        db.execSQL(insertarSurtidor12)
        db.execSQL(insertarSurtidor13)
        db.execSQL(insertarSurtidor14)
        db.execSQL(insertarSurtidor15)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLA_SURTIDOR")
        onCreate(db)
    }
}