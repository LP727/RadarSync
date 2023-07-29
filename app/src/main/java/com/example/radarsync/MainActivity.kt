package com.example.radarsync

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.radarsync.ui.theme.RadarSyncTheme

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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissions()
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

    private fun checkPermissions() {
        val permissionsToRequest = arrayOf(
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION,
            ACCESS_BACKGROUND_LOCATION
        )

        // Check if all the permissions are already granted
        val allPermissionsGranted = permissionsToRequest.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

        if (allPermissionsGranted) {
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
