package com.tarea5_1190_22_515

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    var TxtResultado: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        TxtResultado = findViewById(R.id.TxtResultado)
    }

    fun calcular(view: View) {
        var boton = view as Button
        var textoBoton = boton.text.toString()
        var concatenar = TxtResultado?.text.toString() + textoBoton
        var mostrarSinCeros = quitarCerosIzquierdos(concatenar)
        if (textoBoton == "=") {
            var resultado = 0.0
            try {
                resultado = eval(TxtResultado?.text.toString())
                TxtResultado?.text = resultado.toString()
            } catch (e: Exception) {
                TxtResultado?.text = e.toString()
            }
        } else if (textoBoton == "BORRAR") {
            TxtResultado?.text = "0"
        } else {
            TxtResultado?.text = mostrarSinCeros
        }
    }

    fun quitarCerosIzquierdos(str: String): String {
        var i = 0
        while (i < str.length && str[i] == '0') {
            i++
        }
        val sb = StringBuffer(str)
        sb.replace(0, i, "")

        return sb.toString()
    }

    fun eval(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch = 0
            fun SiguienteCaracter() {
                ch = if (++pos < str.length) str[pos].toInt() else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.toInt()) SiguienteCaracter()
                if (ch == charToEat) {
                    SiguienteCaracter()
                    return true
                }
                return false
            }

            fun evaluar(): Double {
                SiguienteCaracter()
                val x = EvaluarExpresion()
                if (pos < str.length) throw RuntimeException("Inesperado: " + ch.toChar())
                return x
            }

            fun EvaluarExpresion(): Double {
                var x = evaluarTermino()
                while (true) {
                    if (eat('+'.toInt())) x += evaluarTermino() // suma
                    else if (eat('-'.toInt())) x -= evaluarTermino() // resta
                    else return x
                }
            }

            fun evaluarTermino(): Double {
                var x = evaluarFactor()
                while (true) {
                    if (eat('*'.toInt())) x *= evaluarFactor() // multiplicacion
                    else if (eat('/'.toInt())) x /= evaluarFactor() // division
                    else return x
                }
            }

            fun evaluarFactor(): Double {
                if (eat('+'.toInt())) return evaluarFactor() // signo +
                if (eat('-'.toInt())) return -evaluarFactor() // signo -
                var x: Double
                val startPos = pos
                if (eat('('.toInt())) { // parentesis
                    x = EvaluarExpresion()
                    eat(')'.toInt())
                } else if (ch >= '0'.toInt() && ch <= '9'.toInt() || ch == '.'.toInt()) { // numeros
                    while (ch >= '0'.toInt() && ch <= '9'.toInt() || ch == '.'.toInt()) SiguienteCaracter()
                    x = str.substring(startPos, pos).toDouble()
                } else if (ch >= 'a'.toInt() && ch <= 'z'.toInt()) { // funciones
                    while (ch >= 'a'.toInt() && ch <= 'z'.toInt()) SiguienteCaracter()
                    val func = str.substring(startPos, pos)
                    x = evaluarFactor()
                    x = if (func == "sqrt") Math.sqrt(x) else if (func == "sin") Math.sin(
                        Math.toRadians(x)
                    ) else if (func == "cos") Math.cos(Math.toRadians(x)) else if (func == "tan") Math.tan(
                        Math.toRadians(x)
                    ) else throw RuntimeException("Función desconocida: $func")
                } else {
                    throw RuntimeException("Inesperado: " + ch.toChar())
                }
                if (eat('^'.toInt())) x = Math.pow(x, evaluarFactor()) // exponenciacion
                return x
            }
        }.evaluar()
    }
}
