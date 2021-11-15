package mx.tecm.iigs.ladm_u3_practica2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    var baseRemota = FirebaseFirestore.getInstance()
    var dataList = ArrayList<String>()
    var listaId = ArrayList<String>()
    val date = getCurrentDateTime()
    val dateInString = date.toString("yyyy/MM/dd HH:mm:ss")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            insertar()
        }
        baseRemota.collection("Notas")
            .addSnapshotListener { querySnapshot, error ->
                if (error!=null){
                    mensaje(error.message!!)
                    return@addSnapshotListener
                }
                dataList.clear()
                listaId.clear()

                for (document in querySnapshot!!){

                    var cadena="${document.getString("Titulo")} -- ${document.get("Nota")} -- ${document.getDate("Fecha")}"
                    dataList.add(cadena)

                    listaId.add(document.id.toString())

                }
                lista.adapter=ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,dataList)
                lista.setOnItemClickListener { adapterView, view, posicion, l ->
                    dialogoEliminacionActualiza(posicion)
                }
            }
    }

    private fun dialogoEliminacionActualiza(posicion: Int) {
        var idElegido=listaId.get(posicion)

        AlertDialog.Builder(this).setTitle("Atencion")
            .setMessage("¿Que deseas hacer con \n${dataList.get(posicion)}?")
            .setPositiveButton("Eliminar"){d,i->
                eliminar(idElegido)
            }
            .setNeutralButton("Actualizar"){d,i,->
                //actualizar(idElegido)

            }
            .setNegativeButton("CANCELAR"){d,i->}
            .show()
    }

    private fun eliminar(idElegido: String) {
        baseRemota.collection("Notas")
            .document(idElegido)
            .delete()
            .addOnSuccessListener {
                alert("Se elimino con exito")
            }
            .addOnFailureListener {
                alert("Error ${it.message}")
            }
    }
/*
    private fun actualizar(idElegido: String) {


        baseRemota.collection("Notas")
            .document(idElegido)
            .update("Titulo",nuevo)
            .addOnSuccessListener {
                alert("Se Actualizo con exito")
            }
            .addOnFailureListener {
                alert("Error ${it.message}")
            }
    }

 */


    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    fun getCurrentDateTime(): Date { return Calendar.getInstance().time }

    private fun insertar() {
        var datosInsertar = hashMapOf(
            "Titulo" to Titulo.text.toString(),
            "Nota" to Notas.text.toString(),
            "Fecha" to getCurrentDateTime()
        )
        baseRemota.collection("Notas")
            .add(datosInsertar as Any)
            .addOnSuccessListener {
                alert("se inserto correcto en la nube")
            }
            .addOnFailureListener {
                mensaje("Error:${it.message!!}")

            }
        Titulo.setText("")
        Notas.setText("")
    }

    private fun mensaje(s: String) {
        AlertDialog.Builder(this).setTitle("Atencion")
            .setMessage(s)
            .setPositiveButton("Ok"){d,i->}
    }

    private fun alert(s: String) {
        Toast.makeText(this,s, Toast.LENGTH_LONG).show()
    }
}


