package com.example.clubdeportivog3.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.clubdeportivog3.model.Socio
import com.example.clubdeportivog3.model.NoSocio
import com.example.clubdeportivog3.model.Actividad

// Clase helper para manejar la base de datos SQLite del club deportivo
class ClubDeportivoBD(context: Context) :
    SQLiteOpenHelper(context, "club_deportivo.db", null, 4) {
    // Se crean todas las tablas necesarias al inicializar la base de datos por primera vez
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("""
            CREATE TABLE IF NOT EXISTS socios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                apellido TEXT NOT NULL,
                dni TEXT NOT NULL UNIQUE,
                correo TEXT,
                telefono TEXT,
                cuota REAL,
                aptoFisico INTEGER,
                carnetEntregado INTEGER,
                pagoAlDia INTEGER
            )
        """.trimIndent())

        db?.execSQL("""
            CREATE TABLE IF NOT EXISTS nosocios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                apellido TEXT NOT NULL,
                dni TEXT NOT NULL UNIQUE,
                correo TEXT,
                telefono TEXT NOT NULL,
                pagoDiario REAL,
                aptoFisico INTEGER
            )
        """.trimIndent())

        db?.execSQL("""
            CREATE TABLE IF NOT EXISTS actividades (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                descripcion TEXT NOT NULL,
                dia TEXT NOT NULL,
                horario TEXT NOT NULL,
                monto REAL NOT NULL,
                cupoMaximo INTEGER NOT NULL
            )
        """.trimIndent())

        db?.execSQL("""
            CREATE TABLE IF NOT EXISTS inscripciones_socios (
                socio_id INTEGER NOT NULL,
                actividad_id INTEGER NOT NULL,
                PRIMARY KEY(socio_id, actividad_id),
                FOREIGN KEY(socio_id) REFERENCES socios(id) ON DELETE CASCADE,
                FOREIGN KEY(actividad_id) REFERENCES actividades(id) ON DELETE CASCADE
    )
""".trimIndent())

        db?.execSQL("""
            CREATE TABLE IF NOT EXISTS inscripciones_nosocios (
                no_socio_id INTEGER NOT NULL,
                actividad_id INTEGER NOT NULL,
                PRIMARY KEY(no_socio_id, actividad_id),
                FOREIGN KEY(no_socio_id) REFERENCES nosocios(id) ON DELETE CASCADE,
                FOREIGN KEY(actividad_id) REFERENCES actividades(id) ON DELETE CASCADE
    )
""".trimIndent())


        db?.execSQL("""
            CREATE TABLE IF NOT EXISTS admin (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario TEXT NOT NULL UNIQUE,
                contrasena TEXT NOT NULL
            )
        """.trimIndent())
    }


    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS socios")
        db?.execSQL("DROP TABLE IF EXISTS nosocios")
        db?.execSQL("DROP TABLE IF EXISTS actividades")
        db?.execSQL("DROP TABLE IF EXISTS admin")
        db?.execSQL("DROP TABLE IF EXISTS inscripciones_socios")
        db?.execSQL("DROP TABLE IF EXISTS inscripciones_nosocios")
        onCreate(db)
    }

    // Métodos para socios

    // Inserta un nuevo socio en la base de datos
    // Devuelve true si la operación fue exitosa
    fun insertarSocio(socio: Socio): Boolean {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", socio.nombre)
            put("apellido", socio.apellido)
            put("dni", socio.dni)
            put("correo", socio.correo)
            put("telefono", socio.telefono)
            put("cuota", socio.cuota)
            put("aptoFisico", if (socio.aptoFisico) 1 else 0)
            put("carnetEntregado", if (socio.carnetEntregado) 1 else 0)
            put("pagoAlDia", if (socio.pagoAlDia) 1 else 0)
        }
        val resultado = db.insert("socios", null, valores)
        db.close()
        return resultado != -1L
    }

    // Busca un socio por su ID.
    // Devuelve un objeto Socio o null si no se encuentra
    fun obtenerSocio(id: Int): Socio? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, nombre, apellido, dni, correo, telefono, cuota, aptoFisico, carnetEntregado, pagoAlDia FROM socios WHERE id = ?",
            arrayOf(id.toString())
        )
        var socio: Socio? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
            val apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido"))
            val dni = cursor.getString(cursor.getColumnIndexOrThrow("dni"))
            val correo = cursor.getString(cursor.getColumnIndexOrThrow("correo")) // Puede ser null
            val telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono"))
            val cuota = cursor.getDouble(cursor.getColumnIndexOrThrow("cuota"))
            val aptoFisico = cursor.getInt(cursor.getColumnIndexOrThrow("aptoFisico")) == 1
            val carnetEntregado = cursor.getInt(cursor.getColumnIndexOrThrow("carnetEntregado")) == 1
            val pagoAlDia = cursor.getInt(cursor.getColumnIndexOrThrow("pagoAlDia")) == 1

            socio = Socio(id, nombre, apellido, dni, correo, telefono, cuota, aptoFisico, carnetEntregado, pagoAlDia)
        }
        cursor.close()
        return socio
    }

    //Verificar q el dni no este ya inscripto
    fun dniYaExiste(dni: String, socioId: Int? = null): Boolean {
        val db = this.readableDatabase

        // Verifica en tabla Socios
        val querySocios = if (socioId != null) {
            "SELECT COUNT(*) FROM socios WHERE dni = ? AND id != ?"
        } else {
            "SELECT COUNT(*) FROM socios WHERE dni = ?"
        }
        val cursorSocios = if (socioId != null) {
            db.rawQuery(querySocios, arrayOf(dni, socioId.toString()))
        } else {
            db.rawQuery(querySocios, arrayOf(dni))
        }

        var existeEnSocios = false
        if (cursorSocios.moveToFirst()) {
            existeEnSocios = cursorSocios.getInt(0) > 0
        }
        cursorSocios.close()
        // Verifica en tabla NoSocios
        val queryNoSocios = "SELECT COUNT(*) FROM nosocios WHERE dni = ?"
        val cursorNoSocios = db.rawQuery(queryNoSocios, arrayOf(dni))

        var existeEnNoSocios = false
        if (cursorNoSocios.moveToFirst()) {
            existeEnNoSocios = cursorNoSocios.getInt(0) > 0
        }
        cursorNoSocios.close()

        return existeEnSocios || existeEnNoSocios
    }



    // Eliminar socio por id
    fun eliminarSocio(id: Int): Boolean {
        val db = writableDatabase
        val resultado = db.delete("socios", "id = ?", arrayOf(id.toString()))
        db.close()
        return resultado > 0
    }

    // Actualiza los datos de un socio existente.
    // Devuelve true si se realizó la actualización
    fun actualizarSocio(socio: Socio): Boolean {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", socio.nombre)
            put("apellido", socio.apellido)
            put("dni", socio.dni)
            put("correo", socio.correo)
            put("telefono", socio.telefono)
            put("cuota", socio.cuota)
            put("aptoFisico", if (socio.aptoFisico) 1 else 0)
            put("carnetEntregado", if (socio.carnetEntregado) 1 else 0)
            put("pagoAlDia", if (socio.pagoAlDia) 1 else 0)
        }
        val resultado = db.update("socios", valores, "id = ?", arrayOf(socio.id.toString()))
        db.close()
        return resultado > 0
    }

    // Marca al socio como "pago al día".
    // Devuelve true si se actualizó correctamente
    fun registrarPago(socioId: Int): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("pagoAlDia", 1) // true
        }
        val filasActualizadas = db.update(
            "socios",              // nombre de la tabla
            values,                // nuevos valores
            "id = ?",              // cláusula WHERE
            arrayOf(socioId.toString())  // argumentos WHERE
        )
        db.close()
        return filasActualizadas > 0
    }

    // Inscribe a un socio en una actividad determinada.
    // Devuelve false si ya estaba inscripto o hubo error
    fun inscribirSocioEnActividad(socioId: Int, actividadId: Int): Boolean {
        val db = writableDatabase
        return try {
            val stmt = db.compileStatement(
                "INSERT INTO inscripciones_socios (socio_id, actividad_id) VALUES (?, ?)"
            )
            stmt.bindLong(1, socioId.toLong())
            stmt.bindLong(2, actividadId.toLong())
            stmt.executeInsert()
            true
        } catch (e: android.database.sqlite.SQLiteConstraintException) {
            false // Ya estaba inscripto
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Verifica si un socio ya está inscripto en una actividad
    fun estaInscripto(socioId: Int, actividadId: Int): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT 1 FROM inscripciones_socios WHERE socio_id = ? AND actividad_id = ?",
            arrayOf(socioId.toString(), actividadId.toString())
        )
        val existe = cursor.moveToFirst()
        cursor.close()
        db.close()
        return existe
    }

    // Eliminar inscripción socio
    fun eliminarInscripcionSocio(socio_id: Int, actividad_id: Int): Boolean {
        val db = writableDatabase
        val resultado = db.delete("inscripciones_socios", "socio_id = ? AND actividad_id = ?", arrayOf(socio_id.toString(), actividad_id.toString()))
        db.close()
        return resultado > 0
    }

    // Devuelve la lista completa de socios almacenados
    fun obtenerSocios(): List<Socio> {
        val lista = mutableListOf<Socio>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM socios", null)
        if (cursor.moveToFirst()) {
            do {
                val socio = Socio(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido")),
                    dni = cursor.getString(cursor.getColumnIndexOrThrow("dni")),
                    correo = cursor.getString(cursor.getColumnIndexOrThrow("correo")),
                    telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono")),
                    cuota = cursor.getDouble(cursor.getColumnIndexOrThrow("cuota")),
                    aptoFisico = cursor.getInt(cursor.getColumnIndexOrThrow("aptoFisico")) == 1,
                    carnetEntregado = cursor.getInt(cursor.getColumnIndexOrThrow("carnetEntregado")) == 1,
                    pagoAlDia = cursor.getInt(cursor.getColumnIndexOrThrow("pagoAlDia")) == 1
                )
                lista.add(socio)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    // Devuelve las actividades en las que se inscribio un socio
    fun obtenerActividadesSocio(socioId: Int): List<Actividad> {
        val lista = mutableListOf<Actividad>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            """
        SELECT a.id, a.nombre, a.descripcion, a.dia, a.horario, a.monto, a.cupoMaximo
        FROM actividades a
        INNER JOIN inscripciones_socios i ON a.id = i.actividad_id
        WHERE i.socio_id = ?
        """, arrayOf(socioId.toString())
        )
        if (cursor.moveToFirst()) {
            do {
                val actividad = Actividad(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                    dia = cursor.getString(cursor.getColumnIndexOrThrow("dia")),
                    horario = cursor.getString(cursor.getColumnIndexOrThrow("horario")),
                    monto = cursor.getDouble(cursor.getColumnIndexOrThrow("monto")),
                    cupoMaximo = cursor.getInt(cursor.getColumnIndexOrThrow("cupoMaximo"))
                )
                lista.add(actividad)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }
    

    // Métodos para NoSocio

    fun insertarNoSocio(noSocio: NoSocio): Boolean {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", noSocio.nombre)
            put("apellido", noSocio.apellido)
            put("dni", noSocio.dni)
            put("correo", noSocio.correo)
            put("telefono", noSocio.telefono)
            put("pagoDiario", noSocio.pagoDiario)
            put("aptoFisico", if (noSocio.aptoFisico) 1 else 0)
        }
        val resultado = db.insert("nosocios", null, valores)
        db.close()
        return resultado != -1L
    }

    
    fun eliminarNoSocio(id: Int): Boolean {
        val db = writableDatabase
        val resultado = db.delete("nosocios", "id = ?", arrayOf(id.toString()))
        db.close()
        return resultado > 0
    }

    //no socio unico
    fun obtenerNoSocio(numero: Int): NoSocio? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, nombre, apellido, dni, correo, telefono, pagoDiario, aptoFisico FROM nosocios WHERE id = ?",
            arrayOf(numero.toString())
        )
        var noSocio: NoSocio? = null
        if (cursor.moveToFirst()) {
            noSocio = NoSocio(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido")),
                dni = cursor.getString(cursor.getColumnIndexOrThrow("dni")),
                correo = cursor.getString(cursor.getColumnIndexOrThrow("correo")),
                telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono")),
                pagoDiario = cursor.getDouble(cursor.getColumnIndexOrThrow("pagoDiario")),
                aptoFisico = cursor.getInt(cursor.getColumnIndexOrThrow("aptoFisico")) > 0
            )
        }
        cursor.close()
        db.close()
        return noSocio
    }



    //lista de no socios
    fun obtenerNoSocios(): List<NoSocio> {
        val lista = mutableListOf<NoSocio>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM nosocios", null)
        if (cursor.moveToFirst()) {
            do {
                val noSocio = NoSocio(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido")),
                    dni = cursor.getString(cursor.getColumnIndexOrThrow("dni")),
                    correo = cursor.getString(cursor.getColumnIndexOrThrow("correo")),
                    telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono")),
                    pagoDiario = cursor.getDouble(cursor.getColumnIndexOrThrow("pagoDiario")),
                    aptoFisico = cursor.getInt(cursor.getColumnIndexOrThrow("aptoFisico")) == 1
                )
                lista.add(noSocio)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }

    // Actualizar no socio
    fun actualizarNoSocio(noSocio: NoSocio): Boolean {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", noSocio.nombre)
            put("apellido", noSocio.apellido)
            put("dni", noSocio.dni)
            put("correo", noSocio.correo)
            put("telefono", noSocio.telefono)
            put("pagoDiario", noSocio.pagoDiario)
            put("aptoFisico", if (noSocio.aptoFisico) 1 else 0)
        }
        val resultado = db.update("nosocios", valores, "id = ?", arrayOf(noSocio.id.toString()))
        db.close()
        return resultado > 0
    }

    fun obtenerActividadesNoSocio(no_socio_id: Int): List<Actividad> {
        val lista = mutableListOf<Actividad>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            """
        SELECT a.id, a.nombre, a.descripcion, a.dia, a.horario, a.monto, a.cupoMaximo
        FROM actividades a
        INNER JOIN inscripciones_nosocios i ON a.id = i.actividad_id
        WHERE i.no_socio_id = ?
        """, arrayOf(no_socio_id.toString())
        )
        if (cursor.moveToFirst()) {
            do {
                val actividad = Actividad(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                    dia = cursor.getString(cursor.getColumnIndexOrThrow("dia")),
                    horario = cursor.getString(cursor.getColumnIndexOrThrow("horario")),
                    monto = cursor.getDouble(cursor.getColumnIndexOrThrow("monto")),
                    cupoMaximo = cursor.getInt(cursor.getColumnIndexOrThrow("cupoMaximo"))
                )
                lista.add(actividad)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    fun inscribirNoSocioEnActividad(noSocioId: Int, actividadId: Int): Boolean {
        val db = writableDatabase
        var exito = false
        db.beginTransaction()
        try {
            val yaInscripto = db.rawQuery(
                "SELECT 1 FROM inscripciones_nosocios WHERE no_socio_id = ? AND actividad_id = ?",
                arrayOf(noSocioId.toString(), actividadId.toString())
            ).use { it.moveToFirst() }

            if (yaInscripto) {
                exito = false
            } else {
                val cupoMaximo = db.rawQuery(
                    "SELECT cupoMaximo FROM actividades WHERE id = ?",
                    arrayOf(actividadId.toString())
                ).use {
                    if (it.moveToFirst()) it.getInt(0) else null
                }

                if (cupoMaximo == null) {
                    exito = false
                } else {
                    val cupoDisponible = obtenerCupoDisponible(actividadId, cupoMaximo)
                    if (cupoDisponible <= 0) {
                        exito = false
                    } else {
                        val valores = ContentValues().apply {
                            put("no_socio_id", noSocioId)
                            put("actividad_id", actividadId)
                        }
                        val resultado = db.insert("inscripciones_nosocios", null, valores)
                        exito = resultado != -1L
                        if (exito) db.setTransactionSuccessful()
                    }
                }
            }
        } finally {
            db.endTransaction()
        }
        return exito
    }


    fun estaNoSocioInscriptoEnActividad(noSocioId: Int, actividadId: Int): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT 1 FROM inscripciones_nosocios WHERE no_socio_id = ? AND actividad_id = ?",
            arrayOf(noSocioId.toString(), actividadId.toString())
        )
        val existe = cursor.count > 0
        cursor.close()
        db.close()
        return existe
    }


    fun eliminarInscripcionNoSocio(no_socio_id: Int, actividad_id: Int): Boolean {
        val db = writableDatabase
        val resultado = db.delete("inscripciones_nosocios", "no_socio_id = ? AND actividad_id = ?", arrayOf(no_socio_id.toString(), actividad_id.toString()))
        db.close()
        return resultado > 0
    }


    // Métodos para Actividad

    fun insertarActividad(actividad: Actividad): Boolean {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", actividad.nombre)
            put("descripcion", actividad.descripcion)
            put("dia", actividad.dia)
            put("horario", actividad.horario)
            put("monto", actividad.monto)
            put("cupoMaximo", actividad.cupoMaximo)
        }
        val resultado = db.insert("actividades", null, valores)
        db.close()
        return resultado != -1L
    }

    fun eliminarActividad(id: Int): Boolean {
        val db = writableDatabase
        val resultado = db.delete("actividades", "id = ?", arrayOf(id.toString()))
        db.close()
        return resultado > 0
    }

    fun actualizarActividad(actividad: Actividad): Boolean {
        val db = writableDatabase
        val valores = ContentValues().apply {
            put("nombre", actividad.nombre)
            put("descripcion", actividad.descripcion)
            put("dia", actividad.dia)
            put("horario", actividad.horario)
            put("monto", actividad.monto)
            put("cupoMaximo", actividad.cupoMaximo)
        }
        val resultado = db.update("actividades", valores, "id = ?", arrayOf(actividad.id.toString()))
        db.close()
        return resultado > 0
    }

    fun obtenerActividades(): List<Actividad> {
        val lista = mutableListOf<Actividad>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM actividades", null)
        if (cursor.moveToFirst()) {
            do {
                val actividad = Actividad(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                    dia = cursor.getString(cursor.getColumnIndexOrThrow("dia")),
                    horario = cursor.getString(cursor.getColumnIndexOrThrow("horario")),
                    monto = cursor.getDouble(cursor.getColumnIndexOrThrow("monto")),
                    cupoMaximo = cursor.getInt(cursor.getColumnIndexOrThrow("cupoMaximo"))
                )
                lista.add(actividad)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return lista
    }
    fun obtenerActividadPorId(id: Int): Actividad? {
        val db = readableDatabase
        var actividad: Actividad? = null

        val cursor = db.rawQuery("SELECT * FROM actividades WHERE id = ?", arrayOf(id.toString()))
        if (cursor.moveToFirst()) {
            actividad = Actividad(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                descripcion = cursor.getString(cursor.getColumnIndexOrThrow("descripcion")),
                dia = cursor.getString(cursor.getColumnIndexOrThrow("dia")),
                horario = cursor.getString(cursor.getColumnIndexOrThrow("horario")),
                monto = cursor.getDouble(cursor.getColumnIndexOrThrow("monto")),
                cupoMaximo = cursor.getInt(cursor.getColumnIndexOrThrow("cupoMaximo"))
            )
        }

        cursor.close()
        db.close()
        return actividad
    }

    //verificar cupos disponibles en actividad
    fun obtenerCupoDisponible(actividadId: Int, cupoMaximo: Int): Int {
        val db = readableDatabase

        // Contar inscriptos socios
        val cursorSocios = db.rawQuery(
            "SELECT COUNT(*) FROM inscripciones_socios WHERE actividad_id = ?",
            arrayOf(actividadId.toString())
        )
        var countSocios = 0
        if (cursorSocios.moveToFirst()) {
            countSocios = cursorSocios.getInt(0)
        }
        cursorSocios.close()

        // Contar inscriptos no socios
        val cursorNoSocios = db.rawQuery(
            "SELECT COUNT(*) FROM inscripciones_nosocios WHERE actividad_id = ?",
            arrayOf(actividadId.toString())
        )
        var countNoSocios = 0
        if (cursorNoSocios.moveToFirst()) {
            countNoSocios = cursorNoSocios.getInt(0)
        }
        cursorNoSocios.close()

        val cupoDisponible = cupoMaximo - (countSocios + countNoSocios)
        return if (cupoDisponible >= 0) cupoDisponible else 0
    }




    // Métodos para admin

    fun registrarAdmin(usuario: String, contrasena: String): Boolean {
        val db = writableDatabase
        val cursor = db.rawQuery("SELECT * FROM admin WHERE usuario = ?", arrayOf(usuario))
        val existe = cursor.count > 0
        cursor.close()
        if (existe) {
            db.close()
            return false
        }
        val valores = ContentValues().apply {
            put("usuario", usuario)
            put("contrasena", contrasena)
        }
        val resultado = db.insert("admin", null, valores)
        db.close()
        return resultado != -1L
    }


    fun verificarLogin(usuario: String, contrasena: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM admin WHERE usuario = ? AND contrasena = ?",
            arrayOf(usuario, contrasena)
        )
        val existe = cursor.count > 0
        cursor.close()
        db.close()
        return existe
    }
}