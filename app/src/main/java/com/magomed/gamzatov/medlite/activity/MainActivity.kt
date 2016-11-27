package com.magomed.gamzatov.medlite.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuItem
import com.magomed.gamzatov.medlite.R
import com.magomed.gamzatov.medlite.fragment.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

//        startActivity(Intent(this@MainActivity, PuzzleActivity::class.java))

        val pref = PreferenceManager.getDefaultSharedPreferences(baseContext)
        if(pref.getString("cookie", "") == "")
        //if(!intent.getBooleanExtra("logged", false))
            startActivity(Intent(this, LoginActivity::class.java))

        if(pref.getString("isMedic", "") == "true") {
            bottom_navigation.menu.removeItem(R.id.action_map)
            bottom_navigation.menu.removeItem(R.id.action_list)
            toolbar.title = "Incoming"
            selectFragment(R.id.action_incoming)
        } else {
            bottom_navigation.menu.removeItem(R.id.action_incoming)
            toolbar.title = "Map"
            selectFragment(R.id.action_map)
        }

        bottom_navigation.setOnNavigationItemSelectedListener {
            selectFragment(it.itemId)
            true
        }
    }

    private fun selectFragment(itemId: Int) {
        var fragment: Fragment? = null
        when (itemId) {
            R.id.action_map -> {
                fragment = MapFragment.newInstance()
                toolbar.title = "Map"
            }
            R.id.action_list -> {
                fragment = ListFragment.newInstance()
                toolbar.title = "List"
            }
            R.id.action_history -> {
                fragment = HistoryFragment.newInstance()
                toolbar.title = "History"
            }
            R.id.action_incoming -> {
                fragment = IncomingFragment.newInstance()
                toolbar.title = "Incoming"
            }
            R.id.action_profile -> {
                fragment = ProfileFragment.newInstance()
                toolbar.title = "Profile"
            }
        }

        (0..bottom_navigation.menu.size() - 1)
                .map { bottom_navigation.menu.getItem(it) }
                .forEach { it.isChecked = it.itemId === itemId }

        if (fragment != null) {
            val ft = supportFragmentManager.beginTransaction()
            ft.replace(R.id.contentContainer, fragment, fragment.tag)
            ft.commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val id = item?.itemId

        if (id == R.id.action_exit) {
            val sPref = PreferenceManager.getDefaultSharedPreferences(baseContext)
            val ed = sPref.edit()
            ed.putString("cookie", "")
            ed.putString("isMedic", "")
            ed.apply()
            startActivity(Intent(this, LoginActivity::class.java))
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}
