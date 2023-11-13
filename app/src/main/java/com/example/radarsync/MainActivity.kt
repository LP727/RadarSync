package com.example.radarsync

import android.os.Bundle
import android.widget.Toast
import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.FOREGROUND_SERVICE
import android.Manifest.permission.INTERNET
import android.Manifest.permission.POST_NOTIFICATIONS
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.radarsync.ui.theme.RadarSyncTheme
import com.example.radarsync.utilities.PermissionHelper.Companion.checkPermissions
import com.google.android.gms.common.GoogleApiAvailability

class MainActivity : AppCompatActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val allPermissionsGranted = permissions.all { it.value }
            if (allPermissionsGranted) {
                setContentView(R.layout.activity_main)
            } else {
                Toast.makeText(
                    this,
                    "Some features require certain permissions to function.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    @RequiresApi(34)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkPermissions()
        checkGooglePlayServices()


        // TODO: Uncomment when I have figured out how to use Compose
//        setContent {
//            RadarSyncTheme {
//                // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    Greeting("Android")
//                }
//            }
//        }
    }

    private fun isGooglePlayServicesAvailable(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this)
        return resultCode == com.google.android.gms.common.ConnectionResult.SUCCESS
    }

    private fun checkGooglePlayServices() {
        if (!isGooglePlayServicesAvailable()) {
            GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this)
        }
    }

    @RequiresApi(34)
    private fun checkPermissions() {
        val permissionsToRequest = arrayOf(
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION,
            ACCESS_BACKGROUND_LOCATION,
            INTERNET,
            FOREGROUND_SERVICE,
            POST_NOTIFICATIONS
        )

        if (checkPermissions(permissionsToRequest, this)) {
            // All requested permissions are granted. Continue with your app's workflow.
            setContentView(R.layout.activity_main)
        } else {
            // Some permissions are not granted. Request the required permissions from the user.
            requestPermissions(permissionsToRequest)
        }
    }

    private fun requestPermissions(permissions: Array<String>) {
        // Launch the permission request
        requestPermissionLauncher.launch(permissions)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RadarSyncTheme {
        Greeting("Android")
    }
}
