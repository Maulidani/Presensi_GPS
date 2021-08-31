@file:Suppress("DEPRECATION")

package com.skripsi.presensigps.ui.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.skripsi.presensigps.R
import com.skripsi.presensigps.adapter.PresenceAdapter
import com.skripsi.presensigps.adapter.ReportAdapter
import com.skripsi.presensigps.model.DataResponse
import com.skripsi.presensigps.model.Result
import com.skripsi.presensigps.network.ApiClient
import com.skripsi.presensigps.utils.Constant
import com.skripsi.presensigps.utils.PreferencesHelper
import kotlinx.android.synthetic.main.fragment_history2.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class History2Fragment(_item: String) : Fragment(), ReportAdapter.IUserRecycler {
    private val item = _item
    private lateinit var snackbar: Snackbar
    private lateinit var sharedPref: PreferencesHelper
    private lateinit var lLayoutManager: LinearLayoutManager
    private lateinit var pAdapter: PresenceAdapter
    private lateinit var rAdapter: ReportAdapter
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = PreferencesHelper(requireActivity())
        val idUser = sharedPref.getString(Constant.PREF_USER_ID).toString()

        lLayoutManager = LinearLayoutManager(requireActivity())
        rv.layoutManager = lLayoutManager
        rv.setHasFixedSize(true)

        progressDialog = ProgressDialog(requireActivity())
        progressDialog.setTitle("Loading")
        progressDialog.setMessage("Memuat Informasi...")
        progressDialog.setCancelable(false)

        when (item) {
            "presence" -> presensiAndReport(idUser, item)
            "report" -> presensiAndReport(idUser, item)
            else -> Toast.makeText(requireActivity(), "error viewpager", Toast.LENGTH_SHORT).show()
        }
    }

    private fun presensiAndReport(id: String, itemType: String) {
        progressDialog.show()

        ApiClient.instance.getPresenceReport(id, itemType).enqueue(object : Callback<DataResponse> {
            override fun onResponse(
                call: Call<DataResponse>,
                response: Response<DataResponse>
            ) {
                val result = response.body()?.result
                val value = response.body()?.value
                var message = "Sukses"

                if (value == "1") {

                    if (itemType == "presence") {
                        pAdapter = PresenceAdapter(result!!)
                        rv.adapter = pAdapter
                    } else if (itemType == "report") {
                        rAdapter = ReportAdapter(result!!, this@History2Fragment)
                        rv.adapter = rAdapter
                    }

                } else {
                    message = "Gagal"

                    snackbar =
                        Snackbar.make(parentHistoryActivity, message, Snackbar.LENGTH_SHORT)
                    snackbar.show()

                }
                progressDialog.dismiss()
            }

            override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                snackbar =
                    Snackbar.make(
                        parentHistoryActivity,
                        t.message.toString(),
                        Snackbar.LENGTH_SHORT
                    )
                snackbar.show()
                progressDialog.dismiss()
            }
        })
    }

    override fun refreshView(dataResult: Result, onUpdate: Boolean, type_: String) {
        //
    }

}