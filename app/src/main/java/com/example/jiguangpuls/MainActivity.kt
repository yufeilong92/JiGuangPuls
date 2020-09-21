package com.example.jiguangpuls

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.jpush.android.api.JPushInterface
import com.example.jiguangpuls.jpushdemo.ExampleUtil
import com.example.jiguangpuls.jpushdemo.LocalBroadcastManager
import com.example.jiguangpuls.jpushdemo.PushSetActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    //for receive customer msg from jpush server
    private var mMessageReceiver: MessageReceiver? = null
    val KEY_TITLE = "title"
    companion object {
        @JvmField
        var MESSAGE_RECEIVED_ACTION: String ="com.example.jpushdemo.MESSAGE_RECEIVED_ACTION"
        @JvmField
        var KEY_MESSAGE: String = "message"

        @JvmField
        var KEY_EXTRAS: String = "extras"

        @JvmField
        var isForeground: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        registerMessageReceiver()
    }

    private fun initView() {
        val imei = ExampleUtil.getImei(applicationContext, "")
        if (null != imei) tv_imei.text = "imei=$imei"
        var appKey = ExampleUtil.getAppKey(applicationContext)
        if (null == appKey) appKey = "AppKey异常"
        tv_appkey.text = "AppKey: $appKey"
        tv_regId.text = "RegId:"
        val packageName = packageName
        tv_package.text = "PackageName: $packageName"
        val deviceId = ExampleUtil.getDeviceId(applicationContext)
        tv_device_id.text = "deviceId: $deviceId"
        val versionName = ExampleUtil.GetVersion(applicationContext)
        tv_version.text = "Version: $versionName"
        init.setOnClickListener {
            JPushInterface.init(applicationContext)
        }
        stopPush.setOnClickListener {
            JPushInterface.stopPush(applicationContext)
        }
        resumePush.setOnClickListener {
            JPushInterface.resumePush(applicationContext)
        }
        getRegistrationId.setOnClickListener {
            val rid = JPushInterface.getRegistrationID(applicationContext)
            if (!rid.isEmpty()) {
                tv_regId.setText("RegId:$rid")
            } else {
                Toast.makeText(
                    this,
                    "Get registration fail, JPush init failed!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        setting.setOnClickListener {
            val intent = Intent(this@MainActivity, PushSetActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        isForeground = true
        super.onResume()
    }


    override fun onPause() {
        isForeground = false
        super.onPause()
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
        super.onDestroy()
    }



    fun registerMessageReceiver() {
        mMessageReceiver = MessageReceiver()
        val filter = IntentFilter()
        filter.priority = IntentFilter.SYSTEM_HIGH_PRIORITY
        filter.addAction(MESSAGE_RECEIVED_ACTION)
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter)
    }

    inner class MessageReceiver : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            try {
                if (MESSAGE_RECEIVED_ACTION == intent.action) {
                    val messge = intent.getStringExtra(KEY_MESSAGE)
                    val extras = intent.getStringExtra(KEY_EXTRAS)
                    val showMsg = StringBuilder()
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n")
                    if (!ExampleUtil.isEmpty(extras)) {
                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n")
                    }
                    setCostomMsg(showMsg.toString())
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun setCostomMsg(msg: String) {
        if (null != msg_rec) {
            msg_rec.setText(msg)
            msg_rec.setVisibility(View.VISIBLE)
        }
    }
}