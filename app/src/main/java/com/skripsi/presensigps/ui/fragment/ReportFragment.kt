package com.skripsi.presensigps.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.skripsi.presensigps.R
import com.skripsi.presensigps.adapter.PresenceAdapter
import com.skripsi.presensigps.adapter.ReportAdapter
import com.skripsi.presensigps.model.DataResponse
import com.skripsi.presensigps.network.ApiClient
import kotlinx.android.synthetic.main.fragment_report.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportFragment(private var type: String) : Fragment() {
    private lateinit var lLayoutManager: LinearLayoutManager
    private lateinit var pAdapter: PresenceAdapter
    private lateinit var rAdapter: ReportAdapter
    private lateinit var snackbar: Snackbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lLayoutManager = LinearLayoutManager(activity)
        rv.layoutManager = lLayoutManager
        rv.setHasFixedSize(true)

        when (type) {
            "presence" -> presence()
            "report" -> report()
        }
    }

    private fun presence() {
        ApiClient.instance.getPresence().enqueue(object : Callback<DataResponse> {
            override fun onResponse(call: Call<DataResponse>, response: Response<DataResponse>) {
                val value = response.body()?.value
                val message = response.body()?.message

                if (value.equals("1")) {
                    pAdapter = PresenceAdapter(response.body()!!.result)
                    rv.adapter = pAdapter

                } else {
                    snackbar =
                        Snackbar.make(activity!!.frame, message.toString(), Snackbar.LENGTH_SHORT)
                    snackbar.show()
                }
            }

            override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                snackbar =
                    Snackbar.make(activity!!.frame, t.message.toString(), Snackbar.LENGTH_SHORT)
                snackbar.show()
            }
        })
    }

    private fun report() {
        ApiClient.instance.getReport().enqueue(object : Callback<DataResponse>{
            override fun onResponse(call: Call<DataResponse>, response: Response<DataResponse>) {
                val value = response.body()?.value
                val message = response.body()?.message

                if (value.equals("1")) {
                    rAdapter = ReportAdapter(response.body()!!.result)
                    rv.adapter = rAdapter

                } else {
                    snackbar =
                        Snackbar.make(activity!!.frame, message.toString(), Snackbar.LENGTH_SHORT)
                    snackbar.show()
                }
            }

            override fun onFailure(call: Call<DataResponse>, t: Throwable) {
                snackbar =
                    Snackbar.make(activity!!.frame, t.message.toString(), Snackbar.LENGTH_SHORT)
                snackbar.show()
            }
        })
    }

//    override fun onResume() {
//        super.onResume()
//        when (type) {
//            "presence" -> presence()
//            "report" -> report()
//        }
//    }
}