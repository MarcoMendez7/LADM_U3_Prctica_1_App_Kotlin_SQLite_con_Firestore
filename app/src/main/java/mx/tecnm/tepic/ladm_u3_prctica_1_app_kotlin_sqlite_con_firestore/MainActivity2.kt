package mx.tecnm.tepic.ladm_u3_prctica_1_app_kotlin_sqlite_con_firestore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main2.*


class MainActivity2 : AppCompatActivity() {
    var baseremota = FirebaseFirestore.getInstance()
    var id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        var extra = intent.extras

        id = extra!!.getString("idElegido")!!
        //realizar la busqueda del documento. obtener un documento en base a su ID
        baseremota.collection("APARTADO")
            .document(id)
            .get()
            .addOnSuccessListener {
                actnombre.setText(it.getString("NOMBRE"))
                actproducto.setText(it.getString("PRODUCTO"))
                actprecio.setText(it.getString("PRECIO"))
            }
            .addOnFailureListener {
                alerta("ERROR!NO EXISTE ID${id}")
            }
        actualizar.setOnClickListener {
            actualizar()
        }
        regresar.setOnClickListener {

            finish()
        }
    }
    private fun actualizar(){
        baseremota.collection("APARTADO")
            .document(id)
            .update("NOMBRE",actnombre.text.toString(),"PRODUCTO",actproducto.text.toString(),"PRECIO",actprecio.text.toString().toFloat())
            .addOnSuccessListener {
                alerta("EXITO, SE ACTUALIZO ")
            }
            .addOnFailureListener {
                mensaje("ERROR, NO SE PUDO ACTUALIZAR")
            }
    }
    private fun alerta(s: String) {
        Toast.makeText(this,s, Toast.LENGTH_LONG).show()
    }
    private fun mensaje(s: String){
        AlertDialog.Builder(this).setTitle("ATENCION")
            .setMessage(s)
            .setPositiveButton("OK"){d,i->}
            .show()
    }
}