package mx.tecnm.tepic.ladm_u3_prctica_1_app_kotlin_sqlite_con_firestore

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseError
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import mx.tecnm.tepic.ladm_u3_sqlitebasico.BaseDatos
import java.util.*


class MainActivity : AppCompatActivity() {
    var baseremota =FirebaseFirestore.getInstance()
    var datalista = ArrayList<String>()

    var baseSQLite = BaseDatos(this,"APARTADO",null,1)
    var ListaID = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        guardar.setOnClickListener {
            insertar()
        }
        sincronizar.setOnClickListener {
            cargarContacto()
        }


    }

    fun mensaje (n:String) {
        AlertDialog.Builder(this)
            .setTitle("ATENCION")
            .setMessage(n)
            .setPositiveButton("ok") { d, i -> }
            .show()


    }
    fun insertar (){
        try {
            var transaccion = baseSQLite.writableDatabase
            var data= ContentValues()

            data.put("ID",idapartao.text.toString().toInt())
            data.put("NOMBRE",nombre.text.toString())
            data.put("PRODUCTO",producto.text.toString())
            data.put("PRECIO",precio.text.toString().toFloat())

            var respuesta = transaccion.insert("APARTADO",null,data)
            if (respuesta==-1L){
                mensaje("ERROR! no se pudo insertar")
            }else{
                mensaje("EXITO! Se inserto CORRECTAMENTE")
                limpiarCampos()


            }
            transaccion.close()
        }catch (err: SQLiteException){
            mensaje(err.message.toString())
        }

    }
    
    fun eliminarsql(){
        try{
            var transaccion=baseSQLite.writableDatabase
            for(i in 1..ListaID.size) {
                var resultado = transaccion.delete("APARTADO", "ID=?", arrayOf(ListaID.get(i-1).toString()))
            }
            transaccion.close()
        }catch (err:SQLiteException){
            mensaje(err.message!!)
        }
    }


    private fun cargarContacto(){
        try {
            var transaccion=baseSQLite.writableDatabase
            var persona = ArrayList<String>()
            var cursor = transaccion.query("APARTADO",arrayOf("*"),null,null,null,null,null)

            if(cursor.moveToFirst()){

                ListaID.clear()
                do {
                    var datosInsertar = hashMapOf(
                            "ID" to cursor.getString(0),
                            "NOMBRE" to cursor.getString(1),
                            "PRODUCTO" to cursor.getString(2),
                            "PRECIO" to cursor.getString(3))
                    ListaID.add(cursor.getInt(0).toString())


                    baseremota.collection("APARTADO")
                            .add(datosInsertar )
                            .addOnSuccessListener {documentReference ->


                            }
                            .addOnFailureListener {
                                mensaje("QUE PASO")
                            }

                }while (cursor.moveToNext())
            }else{
                persona.add("NO HAY DATOS CAPTURADOS AUN")
            }
            listacontacto.adapter= ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,persona)
            eliminarsql()
            transaccion.close()

                    mostrar()

        }catch (err:SQLiteException){
            mensaje(err.message!!)
        }
    }
    private fun alerta(s: String) {
        Toast.makeText(this,s, Toast.LENGTH_LONG).show()
    }
    private fun mostrar(){
        baseremota.collection("APARTADO")
                .addSnapshotListener { querySnapshot, error ->
                    if(error != null)
                    {
                        mensaje(error.message!!)
                        return@addSnapshotListener
                    }

                    datalista.clear()
                    ListaID.clear()
                    for (document in querySnapshot!!){
                        var cadena = "${document.getString("ID")} -- ${document.getString("NOMBRE")} -- ${document.get("PRODUCTO")} -- ${document.get("PRECIO")}"
                        datalista.add(cadena)

                        ListaID.add(document.id.toString())
                    }

                    listacontacto.adapter = ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, datalista)
                    listacontacto.setOnItemClickListener { parent, view, posicion, i ->
                        dialogoEliminarActualizar(posicion)
                    }
                }
    }
    private fun dialogoEliminarActualizar(posicion: Int) {
        var idElegido = ListaID.get(posicion)

        AlertDialog.Builder(this).setTitle("ATENCION")
                .setMessage("¿QUE DESEAS HACER CON\n${datalista.get(posicion)}?")
                .setPositiveButton("ELIMINAR"){d, i->
                    eliminar(idElegido)
                }
                .setNeutralButton("ACTUALIZAR"){d,i->
                    var intent = Intent(this, MainActivity2::class.java)
                    intent.putExtra("idElegido",idElegido)
                    startActivity(intent)
                }
                .setNegativeButton("CANCELAR"){d,i->}
                .show()
    }
    private fun eliminar(idElegido: String) {
        baseremota.collection("APARTADO")
                .document(idElegido)
                .delete()
                .addOnSuccessListener {
                    alerta("SE ELIMINO EL DOCUMENTO")
                }
                .addOnFailureListener {
                    mensaje("ERROR: ${it.message!!}")
                }
    }
    fun limpiarCampos(){
        idapartao.setText("")
        nombre.setText("")
        producto.setText("")
        precio.setText("")

    }
}