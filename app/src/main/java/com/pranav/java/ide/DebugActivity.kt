package com.pranav.java.ide

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class DebugActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		val intent? = getIntent()
		val errorMessage = intent?.getStringExtra("error")!!

		AlertDialog.Builder(this)
				.setTitle("An error occurred")
				.setMessage(errorMessage)
				.setPositiveButton("Quit", {dialog, which -> finish()})
				.create()
				.show()
	}
}
