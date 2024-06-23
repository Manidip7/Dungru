package com.aksar.dungru.ui.home.golive

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Camera
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.aksar.dungru.R
import com.aksar.dungru.databinding.ActivityGoLiveBinding
import com.aksar.dungru.utils.NetworkUtils
import com.aksar.dungru.utils.Utils
import com.aksar.dungru.utils.extensions.adjustSystemNavigationBar
import java.io.IOException

class GoLiveActivity : AppCompatActivity(), OnClickListener, SurfaceHolder.Callback,
    Camera.AutoFocusCallback {
    private lateinit var binding: ActivityGoLiveBinding
    private val SYSTEM_PERMISSION_LIST = arrayOf(android.Manifest.permission.CAMERA)
    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private lateinit var cameraSurfaceView: SurfaceView
    private var isOriginalIconMute = true
    private var isOriginalIconCamera = true
    private var camera: Camera? = null
    private var currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK
    private var currentCameraOrientation = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoLiveBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        adjustSystemNavigationBar(window, binding.bottomLayout)
        cameraSurfaceView = binding.cameraSurfaceView

        cameraSurfaceView.holder.addCallback(this)

        NetworkUtils.checkConnection(this)
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.btnBack ->{
                onBackPressed()
            }

            binding.btnSwitchCamara -> switchCamera()

            binding.btnMike -> {
                if (isOriginalIconMute) {
                    binding.btnMike.setImageResource(R.drawable.ic_mike_icon)
                    Toast.makeText(this, "mute", Toast.LENGTH_SHORT).show()
                } else {
                    binding.btnMike.setImageResource(R.drawable.ic_unmute_icon)
                }
                isOriginalIconMute = !isOriginalIconMute
            }

            binding.btnVideoCam -> {
                if (isOriginalIconCamera) {
                    binding.btnVideoCam.setImageResource(R.drawable.ic_video_off_icon)
                    Toast.makeText(this, "Camera off", Toast.LENGTH_SHORT).show()
                    releaseCamera()
                } else {
                    binding.btnVideoCam.setImageResource(R.drawable.ic_video_on_icon)
                    startCamera()
                }
                isOriginalIconCamera = !isOriginalIconCamera
            }

            binding.btnShare -> {

            }

            binding.btnGift -> {

            }

            binding.btnMore -> {

            }

            binding.btnFinish -> {

            }
        }
    }

    /** CAMERA Surface Methods*/
    override fun surfaceCreated(holder: SurfaceHolder) {
        if (Utils(this).isAllPermissionsGranted(SYSTEM_PERMISSION_LIST)) {
            startCamera()
        } else {
            Utils(this).requestPermission(SYSTEM_PERMISSION_LIST,CAMERA_PERMISSION_REQUEST_CODE)
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        camera?.apply {
            try {
                setPreviewDisplay(holder)
                startPreview()
                autoFocus(this@GoLiveActivity)
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(
                    this@GoLiveActivity,
                    "Failed to start camera preview",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        releaseCamera()
    }


    /**CAMERA Operation methods*/
    private fun startCamera() {
        try {
            cameraSurfaceView.background = null
            camera = Camera.open(currentCameraId)
            camera?.let {
                it.setPreviewDisplay(cameraSurfaceView.holder)
                updateCameraOrientation()
                it.startPreview()

            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to start camera preview", Toast.LENGTH_SHORT).show()
        }
    }
    private fun releaseCamera() {
        camera?.apply {
            stopPreview()
            release()
        }
        camera = null
        cameraSurfaceView.setBackgroundColor(Color.BLACK)
    }

    private fun switchCamera() {
        // Switch between front and back cameras
        currentCameraId =
            if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) Camera.CameraInfo.CAMERA_FACING_FRONT else Camera.CameraInfo.CAMERA_FACING_BACK
        releaseCamera()
        startCamera()
    }

    private fun updateCameraOrientation() {
        // Update camera orientation based on the current camera ID
        val info = Camera.CameraInfo()
        Camera.getCameraInfo(currentCameraId, info)
        currentCameraOrientation = info.orientation
        camera?.setDisplayOrientation(currentCameraOrientation)
        camera?.setDisplayOrientation(90)
    }



    /** on listener result*/
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onAutoFocus(success: Boolean, camera: Camera?) {
        if (success) {
            // Auto-focus successful
            Toast.makeText(this, "Auto-focus successful", Toast.LENGTH_SHORT).show()
        } else {
            // Auto-focus failed
            Toast.makeText(this, "Auto-focus failed", Toast.LENGTH_SHORT).show()
        }
    }
}
