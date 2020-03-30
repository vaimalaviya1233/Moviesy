package com.kpstv.yts.fragments.sheets

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kpstv.yts.AppInterface
import com.kpstv.yts.R
import com.kpstv.yts.activities.TorrentPlayerActivity
import com.kpstv.yts.adapters.SelectSubAdapter
import com.kpstv.yts.interfaces.listener.SingleClickListener
import com.kpstv.yts.models.SelectSubtitle
import kotlinx.android.synthetic.main.bottom_sheet_download.view.addLayout
import kotlinx.android.synthetic.main.bottom_sheet_library_download.view.*
import kotlinx.android.synthetic.main.custom_small_tip.view.*

class BottomSheetLibraryDownload: BottomSheetDialogFragment() {
    private lateinit var singleAdapter: SelectSubAdapter
    private lateinit var title: String
    private lateinit var imdbCode: String
    private lateinit var videoPath: String
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       return inflater.inflate(R.layout.bottom_sheet_library_download, container, false)?.also { view ->
           title = arguments?.getString("title")!!
           imdbCode = arguments?.getString("imdbCode")!!
           videoPath = arguments?.getString("normalLink")!!

           view.playButton.setOnClickListener {
               val i = Intent(context, TorrentPlayerActivity::class.java)
               i.putExtra("normalLink", videoPath)

               if (::singleAdapter.isInitialized) {
                   singleAdapter.models.forEach {
                       if (it.isChecked) {
                           i.putExtra("sub",it.text)
                       }
                   }
               }

               startActivity(i)
               dismiss()
           }

           showSubtitle(view)
       }
    }

    private fun showSubtitle(view: View) {
        if (AppInterface.SUBTITLE_LOCATION.listFiles()?.isNotEmpty()!!) {

            /** Filter subtitle to check whether subtile for current movie exist */

            val titleSpan = title.split(" ")[0]

            val onlySuchFiles = AppInterface.SUBTITLE_LOCATION.list()?.filter { f -> f?.contains(titleSpan)!! }
            if (onlySuchFiles?.isNotEmpty()!!) {
                val cssView = LayoutInflater.from(context)
                    .inflate(R.layout.custom_select_subtitle, view.addLayout)
                val recyclerView = cssView.findViewById<RecyclerView>(R.id.recyclerView)

                val list = ArrayList<SelectSubtitle>()
                onlySuchFiles.mapTo(list) { SelectSubtitle(it) }

                singleAdapter = SelectSubAdapter(context!!, list)
                singleAdapter.setOnClickListener(object :
                    SingleClickListener {
                    override fun onClick(obj: Any, i: Int) {
                        onlySuchFiles.indices.forEach { c ->
                            if (list[c].isChecked && i != c) {
                                list[c].isChecked = false
                                singleAdapter.notifyItemChanged(c)
                            }
                        }

                        list[i].isChecked = !list[i].isChecked
                        singleAdapter.notifyItemChanged(i)
                    }
                })

                recyclerView.layoutManager = LinearLayoutManager(context)
                recyclerView.adapter = singleAdapter
            } else commonNoSubtitle(view)
        } else commonNoSubtitle(view)
    }

    private fun commonNoSubtitle(view: View) {
        val subView = LayoutInflater.from(context).inflate(R.layout.custom_small_tip,view.addLayout)
        subView.tip_text.text = getString(R.string.noSubs)
        subView.tip_button.text = getString(R.string.download)
        subView.tip_button.setOnClickListener {
            val bottomSheetSubtitles =
                BottomSheetSubtitles()
            bottomSheetSubtitles.show(activity?.supportFragmentManager!!,imdbCode)
            dismiss()
        }
    }
}