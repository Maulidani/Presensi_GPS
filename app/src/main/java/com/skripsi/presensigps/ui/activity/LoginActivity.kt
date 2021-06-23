@file:Suppress("DEPRECATION")

package com.skripsi.presensigps.ui.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.skripsi.presensigps.R
import com.skripsi.presensigps.model.DataResponse
import com.skripsi.presensigps.network.ApiClient
import com.skripsi.presensigps.utils.Constant
import com.skripsi.presensigps.utils.PreferencesHelper
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.inputEmail
import kotlinx.android.synthetic.main.activity_login.inputPassword
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var sharedPref: PreferencesHelper
    private lateinit var snackbar: Snackbar
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        supportActionBar?.title = "Masuk"

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Masuk")
        progressDialog.setMessage("Memuat Informasi...")
        progressDialog.setCancelable(false)

        sharedPref = PreferencesHelper(this)

        val position = sharedPref.getString(Constant.PREF_USER_POSITION)

        if (sharedPref.getBoolean(Constant.PREF_IS_LOGIN)) {
            if (position == "admin" || position == "manager") {
                startActivity(
                    Intent(
                        this@LoginActivity,
                        AdminActivity::class.java
                    )
                )
            } else if (position == "sales") {
                startActivity(
                    Intent(
                        this@LoginActivity,
                        MapsActivity::class.java
                    )
                )
            } else {
                sharedPref.logout()
            }
        }

        val emailFromRegister = intent.getStringExtra("email")
        val passwordFromRegister = intent.getStringExtra("password")

        inputEmail.setText(emailFromRegister)
        inputPassword.setText(passwordFromRegister)

        btnLogin.setOnClickListener {
            val getInputEmail = inputEmail.text.toString()
            val getInputPassword = inputPassword.text.toString()

            when {
                getInputEmail == "" -> inputEmail.error = "Tidak boleh kosong"
                getInputPassword == "" -> inputPassword.error = "Tidak boleh kosong"
                else -> loginUser(getInputEmail, getInputPassword)
            }
        }

        tvDaftarDisini.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser(inputEmail: String, inputPassword: String) {
        progressDialog.show()

        ApiClient.instance.loginUser(inputEmail, inputPassword)
            .enqueue(object : Callback<DataResponse> {
                override fun onResponse(
                    call: Call<DataResponse>,
                    response: Response<DataResponse>
                ) {
                    val value = response.body()?.value
                    val message = response.body()?.message

                    if (value.equals("1")) {
                        val idUser = response.body()?.id
                        val nameUser = response.body()?.name
                        val positionUser = response.body()?.position
                        val emailUser = response.body()?.email
                        val passwordUser = response.body()?.password

                        saveSession(idUser, nameUser, positionUser, emailUser, passwordUser)

                        when (positionUser) {
                            "admin" -> {
                                progressDialog.dismiss()

                                startActivity(
                                    Intent(
                                        this@LoginActivity,
                                        AdminActivity::class.java
                                    )
                                )

                                Toast.makeText(
                                    this@LoginActivity,
                                    message.toString(),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()

                            }
                            "manager" -> {
                                //                            startActivity(
                    //                                Intent(
                    //                                    this@LoginActivity,
                    //                                    ManagerActivity::class.java
                    //                                )
                    //                            )
                    //
                    //                            Toast.makeText(
                    //                                this@LoginActivity,
                    //                                message.toString(),
                    //                                Toast.LENGTH_SHORT
                    //                            )
                    //                                .show()

                            }
                            "sales" -> {
                                getOfficeLocation()

                            }
                            else -> {
                                progressDialog.dismiss()
                                sharedPref.logout()
                            }
                        }

                    } else {
                        progressDialog.dismiss()

                        snackbar = Snackbar.make(
                            parentLoginActivity, message.toString(),
                            Snackbar.LENGTH_SHORT
                        )
                        snackbar.show()
                    }
                }

                override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                    progressDialog.dismiss()

                    snackbar = Snackbar.make(
                        parentLoginActivity, t.message.toString(),
                        Snackbar.LENGTH_SHORT
                    )
                    snackbar.show()
                }


            })
    }

    private fun saveSession(
        idUser: String?,
        nameUser: String?,
        positionUser: String?,
        emailUser: String?,
        passwordUser: String?
    ) {
        sharedPref.put(Constant.PREF_USER_ID, idUser.toString())
        sharedPref.put(Constant.PREF_USER_NAME, nameUser.toString())
        sharedPref.put(Constant.PREF_USER_POSITION, positionUser.toString())
        sharedPref.put(Constant.PREF_USER_EMAIL, emailUser.toString())
        sharedPref.put(Constant.PREF_USER_PASSWORD, passwordUser.toString())
        sharedPref.put(Constant.PREF_IS_LOGIN, true)
    }

    private fun getOfficeLocation() {
        ApiClient.instance.getOfficeLocation().enqueue(object : Callback<DataResponse> {
            override fun onResponse(call: Call<DataResponse>, response: Response<DataResponse>) {
                val value = response.body()?.value
                val message = response.body()?.message

                if (value.equals("1")) {
                    progressDialog.dismiss()

                    sharedPref.put(
                        Constant.PREF_OFFICE_NAME,
                        response.body()?.name.toString()
                    )
                    sharedPref.put(
                        Constant.PREF_OFFICE_LATITUDE,
                        response.body()?.latitude.toString()
                    )
                    sharedPref.put(
                        Constant.PREF_OFFICE_LONGITUDE,
                        response.body()?.longitude.toString()
                    )
                    sharedPref.put(
                        Constant.PREF_OFFICE_RADIUS,
                        response.body()?.radius.toString()
                    )

                    Toast.makeText(this@LoginActivity, message.toString(), Toast.LENGTH_SHORT)
                        .show()

                    startActivity(Intent(this@LoginActivity, MapsActivity::class.java))
                } else {
                    progressDialog.dismiss()
                    sharedPref.logout()

                    Toast.makeText(this@LoginActivity, message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                progressDialog.dismiss()
                sharedPref.logout()

                Toast.makeText(this@LoginActivity, t.message.toString(), Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
}