package com.rico.evfinal

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import androidx.appcompat.app.AppCompatActivity

class MenuActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    lateinit var historiaButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        historiaButton = findViewById(R.id.historiaButton)

        historiaButton.setOnClickListener{
            val intento1 = Intent(this, HistoriaActivity::class.java)
            startActivity(intento1)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val ferreteriaLocation = LatLng(-12.152438519912288, -77.02241498922628)
        map.addMarker(MarkerOptions().position(ferreteriaLocation).title("RESTAURANTE CENTRAL"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(ferreteriaLocation, 18f))
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.orders -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                true
            }

            R.id.exit -> {
                finishAffinity()
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}