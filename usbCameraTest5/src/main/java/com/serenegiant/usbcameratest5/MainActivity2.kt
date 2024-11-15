package com.serenegiant.usbcameratest5

import android.Manifest
import android.hardware.usb.UsbDevice
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.CompoundButton
import android.widget.ImageButton
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.serenegiant.common.BaseFragment
import com.serenegiant.dialog.MessageDialogFragment
import com.serenegiant.usb.CameraDialog
import com.serenegiant.usb.USBMonitor
import com.serenegiant.usb.USBMonitor.OnDeviceConnectListener
import com.serenegiant.usb.USBMonitor.UsbControlBlock
import com.serenegiant.usb.UVCCamera
import com.serenegiant.usbcameracommon.UVCCameraHandler
import com.serenegiant.utils.PermissionCheck
import com.serenegiant.utils.ThreadPool.queueEvent
import com.serenegiant.uvccamera.R
import com.serenegiant.widget.CameraViewInterface

class MainActivity2 : AppCompatActivity(), CameraDialog.CameraDialogParent {
    private val DEBUG: Boolean = true // TODO set false on release
    private val TAG: String = "MainActivity2"

    /**
     * preview resolution(width)
     * if your camera does not support specific resolution and mode,
     * [UVCCamera.setPreviewSize] throw exception
     */
    private val PREVIEW_WIDTH: Int = 640

    /**
     * preview resolution(height)
     * if your camera does not support specific resolution and mode,
     * [UVCCamera.setPreviewSize] throw exception
     */
    private val PREVIEW_HEIGHT: Int = 480

    /**
     * preview mode
     * if your camera does not support specific resolution and mode,
     * [UVCCamera.setPreviewSize] throw exception
     * 0:YUYV, other:MJPEG
     */
    private val PREVIEW_MODE: Int = UVCCamera.FRAME_FORMAT_MJPEG

    /**
     * for accessing USB
     */
    private var mUSBMonitor: USBMonitor? = null

    /**
     * Handler to execute camera releated methods sequentially on private thread
     */
    private var mCameraHandler: UVCCameraHandler? = null

    /**
     * for camera preview display
     */
    private lateinit var mUVCCameraView: CameraViewInterface

    /**
     * for open&start / stop&close camera preview
     */
    private lateinit var mCameraButton: ToggleButton

    /**
     * button for start/stop recording
     */
    private lateinit var mCaptureButton: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (DEBUG) Log.v(TAG, "onCreate:")
        setContentView(R.layout.activity_main)
        mCameraButton = findViewById(com.serenegiant.uvccamera.R.id.camera_button) as ToggleButton
        mCameraButton.setOnCheckedChangeListener(mOnCheckedChangeListener)
        mCaptureButton = findViewById(R.id.capture_button) as ImageButton
        mCaptureButton.setOnClickListener(mOnClickListener)
        mCaptureButton.visibility = View.INVISIBLE
        val view: View = findViewById(R.id.camera_view)
        view.setOnLongClickListener(mOnLongClickListener)
        mUVCCameraView = view as CameraViewInterface
        mUVCCameraView.aspectRatio =
            (PREVIEW_WIDTH / PREVIEW_HEIGHT.toFloat()).toDouble()

        mUSBMonitor = USBMonitor(this, mOnDeviceConnectListener)
        mCameraHandler = UVCCameraHandler.createHandler(
            this, mUVCCameraView,
            2, PREVIEW_WIDTH, PREVIEW_HEIGHT, PREVIEW_MODE
        )
    }

    override fun onStart() {
        super.onStart()
        if (DEBUG) Log.v(TAG, "onStart:")
        mUSBMonitor!!.register()
        mUVCCameraView.onResume()
    }

    override fun onStop() {
        if (DEBUG) Log.v(TAG, "onStop:")
        queueEvent(Runnable { mCameraHandler!!.close() })
        mUVCCameraView.onPause()
        setCameraButton(false)
        mCaptureButton.visibility = View.INVISIBLE
        mUSBMonitor!!.unregister()
        super.onStop()
    }

    public override fun onDestroy() {
        if (DEBUG) Log.v(TAG, "onDestroy:")
        if (mCameraHandler != null) {
            mCameraHandler!!.release()
            mCameraHandler = null
        }
        if (mUSBMonitor != null) {
            mUSBMonitor!!.destroy()
            mUSBMonitor = null
        }
        super.onDestroy()
    }


    /**
     * event handler when click camera / capture button
     */
    private val mOnClickListener =
        View.OnClickListener { view ->
            when (view.id) {
                R.id.capture_button -> if (mCameraHandler!!.isOpened) {
                    if (checkPermissionWriteExternalStorage() && checkPermissionAudio()) {
                        if (!mCameraHandler!!.isRecording) {
                            mCaptureButton!!.setColorFilter(-0x10000) // turn red
                            mCameraHandler!!.startRecording()
                        } else {
                            mCaptureButton!!.setColorFilter(0) // return to default color
                            mCameraHandler!!.stopRecording()
                        }
                    }
                }

                else -> {}
            }
        }

    private val mOnCheckedChangeListener =
        CompoundButton.OnCheckedChangeListener { compoundButton, isChecked ->
            when (compoundButton.id) {
                R.id.camera_button -> if (isChecked && !mCameraHandler!!.isOpened) {
                    CameraDialog.showDialog(this@MainActivity2)
                } else {
                    mCameraHandler!!.close()
                    mCaptureButton!!.visibility = View.INVISIBLE
                    setCameraButton(false)
                }

                else -> {}
            }
        }

    /**
     * capture still image when you long click on preview image(not on buttons)
     */
    private val mOnLongClickListener = OnLongClickListener { view ->
        when (view.id) {
            R.id.camera_view -> if (mCameraHandler!!.isOpened) {
                if (checkPermissionWriteExternalStorage()) {
                    mCameraHandler!!.captureStill()
                }
                return@OnLongClickListener true
            }

            else -> {}
        }
        false
    }

    private fun setCameraButton(isOn: Boolean) {
        runOnUiThread(Runnable {
            if (mCameraButton != null) {
                try {
                    mCameraButton!!.setOnCheckedChangeListener(null)
                    mCameraButton!!.isChecked = isOn
                } finally {
                    mCameraButton!!.setOnCheckedChangeListener(mOnCheckedChangeListener)
                }
            }
            if (!isOn && (mCaptureButton != null)) {
                mCaptureButton!!.visibility = View.INVISIBLE
            }
        }, 0)
    }

    private var mSurface: Surface? = null

    private fun startPreview() {
        val st = mUVCCameraView!!.surfaceTexture
        if (mSurface != null) {
            mSurface!!.release()
        }
        mSurface = Surface(st)
        mCameraHandler!!.startPreview(mSurface)
        runOnUiThread { mCaptureButton!!.visibility = View.VISIBLE }
    }

    private val mOnDeviceConnectListener: OnDeviceConnectListener =
        object : OnDeviceConnectListener {
            override fun onAttach(device: UsbDevice) {
                Toast.makeText(this@MainActivity, "USB_DEVICE_ATTACHED", Toast.LENGTH_SHORT).show()
            }

            override fun onConnect(
                device: UsbDevice,
                ctrlBlock: UsbControlBlock,
                createNew: Boolean
            ) {
                if (DEBUG) Log.v(TAG, "onConnect:")
                mCameraHandler!!.open(ctrlBlock)
                startPreview()
            }

            override fun onDisconnect(device: UsbDevice, ctrlBlock: UsbControlBlock) {
                if (DEBUG) Log.v(TAG, "onDisconnect:")
                if (mCameraHandler != null) {
                    mCameraHandler!!.close()
                    setCameraButton(false)
                }
            }

            override fun onDettach(device: UsbDevice) {
                Toast.makeText(this@MainActivity2, "USB_DEVICE_DETACHED", Toast.LENGTH_SHORT).show()
            }

            override fun onCancel(device: UsbDevice) {
            }
        }
    override fun getUSBMonitor(): USBMonitor? {
        return mUSBMonitor
    }

    override fun onDialogResult(canceled: Boolean) {
        if (canceled) {
            setCameraButton(false)
        }
    }

    protected fun checkPermissionWriteExternalStorage(): Boolean {
        if (!PermissionCheck.hasWriteExternalStorage(getActivity())) {
            MessageDialogFragment.showDialog(
                this,
                BaseFragment.REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE,
                com.serenegiant.common.R.string.permission_title,
                com.serenegiant.common.R.string.permission_ext_storage_request,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            )
            return false
        }
        return true
    }
}