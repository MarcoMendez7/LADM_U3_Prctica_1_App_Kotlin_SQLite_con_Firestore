package mx.tecnm.tepic.ladm_u3_sqlitebasico

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDatos(
    context: Context?,
    name:String?,
    factory:SQLiteDatabase.CursorFactory?,
    version:Int
):SQLiteOpenHelper(context,name,factory,version){
    override fun onCreate(p0: SQLiteDatabase) {
        //se invoca cuando se ejecuta por primera vez la aplicacion
        // p0.execSQL()// crear table inser delete upadate, etc
        //p0.rawQuery()//se usa para el selec
        p0.execSQL("CREATE TABLE  APARTADO (ID  INTEGER PRIMARY KEY  , NOMBRE VARCHAR(200),PRODUCTO VARCHAR(200),PRECIO FLOAT)")
    }

    override fun onUpgrade(p0: SQLiteDatabase, p1: Int, p2: Int) {
//update y upgrade= update=actualizacion menor Upgrade= cambio mayor
        //actualizacion
        // p1=version anterior
        // p2= version nueva
        //se invoca solo,cuando tu cambias el numero de version
    }

}