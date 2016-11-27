package com.magomed.gamzatov.medlite.activity

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import com.magomed.gamzatov.medlite.R
import com.magomed.gamzatov.medlite.adapter.RecyclerListAdapter
import com.magomed.gamzatov.medlite.helper.OnStartDragListener
import kotlinx.android.synthetic.main.activity_puzzle.*
import com.magomed.gamzatov.medlite.adapter.RecyclerViewAdapter
import com.magomed.gamzatov.medlite.helper.SimpleItemTouchHelperCallback

class PuzzleActivity : AppCompatActivity(), OnStartDragListener {

    private var mItemTouchHelper: ItemTouchHelper? = null
    private val rightAnswer = arrayOf("90 deg", "45 deg", "25 deg", "10-15 deg")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_puzzle)

        val subjects = arrayOf("Intramuscular", "Subcutaneous", "Intravenous", "Intradermal")

        puzzle_text_rv.layoutManager = LinearLayoutManager(this)
        val recyclerViewAdapter = RecyclerViewAdapter(this, subjects)
        puzzle_text_rv.adapter = recyclerViewAdapter


        val adapter = RecyclerListAdapter(this, this)
        puzzle_rv.setHasFixedSize(true)
        puzzle_rv.adapter = adapter
        puzzle_rv.layoutManager = LinearLayoutManager(this)

        val callback = SimpleItemTouchHelperCallback(adapter)
        mItemTouchHelper = ItemTouchHelper(callback)
        mItemTouchHelper?.attachToRecyclerView(puzzle_rv)


        puzzle_submit.setOnClickListener {
            for (i in 0..rightAnswer.size-1) {
                if(adapter.getmItems()[i] != rightAnswer[i]) {
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                    return@setOnClickListener
                }
            }
            setResult(Activity.RESULT_OK)
            finish()
        }

    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder?) {
        mItemTouchHelper?.startDrag(viewHolder)
    }
}
