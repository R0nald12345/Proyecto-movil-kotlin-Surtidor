package com.example.parcialproyectosurtidor.datos.conexion

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Conexion (context: Context): SQLiteOpenHelper(context,DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "surtidor_db"
        private const val DATABASE_VERSION = 1

        //tablas
        private const val TABLA_SURTIDOR = "Surtidor"
        private const val TABLA_USUARIO = "Usuario"
        private const val TABLA_REGISTRO_ESPERA = "RegistroEspera"

        // Columnas comunes
        private const val COLUMNA_ID = "id"

        // Columnas Surtidor
        private const val COLUMNA_NOMBRE = "nombre"
        private const val COLUMNA_LATITUD = "latitud"
        private const val COLUMNA_LONGITUD = "longitud"

        // Columnas Usuario
        private const val COLUMNA_TELEFONO = "telefono"

        // Columnas RegistroEspera
        private const val COLUMNA_HORA_INICIO = "hora_inicio"
        private const val COLUMNA_CANTIDAD_AUTO_ANTES = "cantidad_auto_antes"
        private const val COLUMNA_TIEMPO_ESPERA_ESTIMADO = "tiempo_espera_estimado"
        private const val COLUMNA_CANTIDAD_COMBUSTIBLE = "cantidad_combustible"
        private const val COLUMNA_ID_USUARIO = "id_usuario"
        private const val COLUMNA_ID_SURTIDOR = "id_surtidor"

    }

    override fun onCreate(db: SQLiteDatabase) {
        // Crear tabla Surtidor
        val crearTablaSurtidor = """
            CREATE TABLE $TABLA_SURTIDOR (
                $COLUMNA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMNA_NOMBRE TEXT NOT NULL,
                $COLUMNA_LATITUD REAL NOT NULL,
                $COLUMNA_LONGITUD REAL NOT NULL
            )
        """.trimIndent()

        // Crear tabla Usuario
        val crearTablaUsuario = """
            CREATE TABLE $TABLA_USUARIO (
                $COLUMNA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMNA_NOMBRE TEXT NOT NULL,
                $COLUMNA_TELEFONO TEXT NOT NULL,
                $COLUMNA_LATITUD REAL NOT NULL,
                $COLUMNA_LONGITUD REAL NOT NULL
            )
        """.trimIndent()

        // Crear tabla RegistroEspera
        val crearTablaRegistroEspera = """
            CREATE TABLE $TABLA_REGISTRO_ESPERA (
                $COLUMNA_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMNA_HORA_INICIO TEXT NOT NULL,
                $COLUMNA_CANTIDAD_AUTO_ANTES INTEGER NOT NULL,
                $COLUMNA_TIEMPO_ESPERA_ESTIMADO INTEGER NOT NULL,
                $COLUMNA_CANTIDAD_COMBUSTIBLE REAL NOT NULL,
                $COLUMNA_ID_USUARIO INTEGER NOT NULL,
                $COLUMNA_ID_SURTIDOR INTEGER NOT NULL,
                FOREIGN KEY($COLUMNA_ID_USUARIO) REFERENCES $TABLA_USUARIO($COLUMNA_ID),
                FOREIGN KEY($COLUMNA_ID_SURTIDOR) REFERENCES $TABLA_SURTIDOR($COLUMNA_ID)
            )
        """.trimIndent()

        db.execSQL(crearTablaSurtidor)
        db.execSQL(crearTablaUsuario)
        db.execSQL(crearTablaRegistroEspera)

        // Insertar datos de ejemplo (surtidores)
        val insertarSurtidor1 = """
            INSERT INTO $TABLA_SURTIDOR ($COLUMNA_NOMBRE, $COLUMNA_LATITUD, $COLUMNA_LONGITUD)
            VALUES ('Biopetrol-Refineria Oriental', -17.768984371398503, -63.171002517830594)
        """.trimIndent()

        val insertarSurtidor2 = """
            INSERT INTO $TABLA_SURTIDOR ($COLUMNA_NOMBRE, $COLUMNA_LATITUD, $COLUMNA_LONGITUD)
            VALUES ('Biopetrol-Beni', -17.769411876342136, -63.178779689932306)
        """.trimIndent()

        val insertarSurtidor3 = """
            INSERT INTO $TABLA_SURTIDOR ($COLUMNA_NOMBRE, $COLUMNA_LATITUD, $COLUMNA_LONGITUD)
            VALUES ('Biopetrol-cabezas', -18.78742511074283, -63.31419824696175)
        """.trimIndent()

        val insertarSurtidor4 = """
             INSERT INTO $TABLA_SURTIDOR($COLUMNA_NOMBRE, $COLUMNA_LATITUD, $COLUMNA_LONGITUD)
             VALUES ('Biopetrol-Berrea',-17.837367519879542, -63.23818518412395)
        """.trimIndent()

        val insertarSurtidor5 = """
             INSERT INTO $TABLA_SURTIDOR($COLUMNA_NOMBRE, $COLUMNA_LATITUD, $COLUMNA_LONGITUD)
             VALUES ('Virgen de Cotoca',-17.78229402285384, -63.16358839044966)
        """.trimIndent()

        val insertarSurtidor6 = """
             INSERT INTO $TABLA_SURTIDOR($COLUMNA_NOMBRE, $COLUMNA_LATITUD, $COLUMNA_LONGITUD)
             VALUES ('Biopetrol-Equipetrol',-17.754442257871663, -63.197113370180176)
        """.trimIndent()

        val insertarSurtidor7 = """
             INSERT INTO $TABLA_SURTIDOR($COLUMNA_NOMBRE, $COLUMNA_LATITUD, $COLUMNA_LONGITUD)
             VALUES ('Biopetrol-Gasco',-17.759355084273157, -63.179401075164144)
        """.trimIndent()

        val insertarSurtidor8 = """
             INSERT INTO $TABLA_SURTIDOR($COLUMNA_NOMBRE, $COLUMNA_LATITUD, $COLUMNA_LONGITUD)
             VALUES ('Biopetrol-Teca',-17.76408829671861,-63.071413028252856 )
        """.trimIndent()

        val insertarSurtidor9 = """
             INSERT INTO $TABLA_SURTIDOR($COLUMNA_NOMBRE, $COLUMNA_LATITUD, $COLUMNA_LONGITUD)
             VALUES ('Biopetrol-Lopez',-17.7256802052732, -63.16538626233688)
        """.trimIndent()

        val insertarSurtidor10 = """
             INSERT INTO $TABLA_SURTIDOR($COLUMNA_NOMBRE, $COLUMNA_LATITUD, $COLUMNA_LONGITUD)
             VALUES ('Biopetrol-Paragua',-17.764901273617195, -63.14949769739089)
        """.trimIndent()

        val insertarSurtidor11 = """
             INSERT INTO $TABLA_SURTIDOR($COLUMNA_NOMBRE, $COLUMNA_LATITUD, $COLUMNA_LONGITUD)
             VALUES ('Biopetrol-PiraiCamiri',-17.022670789240202, -63.53260202264754)
        """.trimIndent()

        //----

        val insertarSurtidor12 = """
             INSERT INTO $TABLA_SURTIDOR($COLUMNA_NOMBRE, $COLUMNA_LATITUD, $COLUMNA_LONGITUD)
             VALUES ('Biopetrol-Pirai',-17.785829254773738, -63.20441190466365)
        """.trimIndent()

        val insertarSurtidor13 = """
             INSERT INTO $TABLA_SURTIDOR($COLUMNA_NOMBRE, $COLUMNA_LATITUD, $COLUMNA_LONGITUD)
             VALUES ('Biopetrol-Royal',-17.805768319173673, -63.19794190651265)
        """.trimIndent()

        val insertarSurtidor14 = """
             INSERT INTO $TABLA_SURTIDOR($COLUMNA_NOMBRE, $COLUMNA_LATITUD, $COLUMNA_LONGITUD)
             VALUES ('Biopetrol-ES Sur',-17.799900227145187, -63.18048330366337)
        """.trimIndent()
        val insertarSurtidor15 = """
             INSERT INTO $TABLA_SURTIDOR($COLUMNA_NOMBRE, $COLUMNA_LATITUD, $COLUMNA_LONGITUD)
             VALUES ('Biopetrol-Viru Viru',-17.6759135053985, -63.15903123830914)
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
        // Eliminar tablas si existen
        db.execSQL("DROP TABLE IF EXISTS $TABLA_REGISTRO_ESPERA")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_USUARIO")
        db.execSQL("DROP TABLE IF EXISTS $TABLA_SURTIDOR")
        // Crear de nuevo
        onCreate(db)
    }
}