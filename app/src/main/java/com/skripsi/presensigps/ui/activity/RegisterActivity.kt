package com.skripsi.presensigps.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.skripsi.presensigps.R
import com.skripsi.presensigps.api.ApiClient
import com.skripsi.presensigps.model.DataResponse
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.inputEmail
import kotlinx.android.synthetic.main.activity_register.inputPassword
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var snackbar: Snackbar
    private val itemPosition = listOf("manager", "supervisor", "foreman", "leader", "operator")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.title = "Daftar"

        val adapterPosition = ArrayAdapter(this, R.layout.list_dropdown, itemPosition)
        inputJabatan.setAdapter(adapterPosition)

        btnDaftar.setOnClickListener {
            val getInputNamaLengkap = inputNamaLengkap.text.toString()
            val getInputPosition = inputJabatan.text.toString()
            val getInputEmail = inputEmail.text.toString()
            val getInputPassword = inputPassword.text.toString()

            when {
                getInputNamaLengkap == "" -> inputNamaLengkap.error = "Tidak boleh kosong"
                getInputPosition == "" -> inputJabatan.error = "Tidak boleh kosong"
                getInputEmail == "" -> inputEmail.error = "Tidak boleh kosong"
                getInputPassword == "" -> inputPassword.error = "Tidak boleh kosong"
                else -> registerUser(
                    getInputNamaLengkap,
                    getInputPosition,
                    getInputEmail,
                    getInputPassword
                )
            }
        }

        tvMasuk.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun registerUser(
        inputNamaLengkap: String,
        inputPosition: String,
        inputEmail: String,
        inputPassword: String
    ) {
        ApiClient.instance.registerUser(inputNamaLengkap, inputEmail, inputPassword, inputPosition)
            .enqueue(object : Callback<DataResponse> {
                override fun onResponse(
                    call: Call<DataResponse>,
                    response: Response<DataResponse>
                ) {
                    val value = response.body()?.value
                    val message = response.body()?.message

                    if (value.equals("1")) {

                        startActivity(
                            Intent(this@RegisterActivity, LoginActivity::class.java)
                                .putExtra("email", inputEmail)
                                .putExtra("password", inputPassword)
                                .putExtra("position", inputPosition)
                        )

                        snackbar = Snackbar.make(
                            parentRegisterActivity, message.toString(),
                            Snackbar.LENGTH_SHORT
                        )
                        snackbar.show()
                    } else {
                        snackbar = Snackbar.make(
                            parentRegisterActivity, message.toString(),
                            Snackbar.LENGTH_SHORT
                        )
                        snackbar.show()
                    }
                }

                override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                    snackbar = Snackbar.make(
                        parentRegisterActivity, t.message.toString(),
                        Snackbar.LENGTH_SHORT
                    )
                    snackbar.show()
                }

            })

    }
}