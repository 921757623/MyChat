package com.hust.lar

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.hust.database.MMKVUtil
import com.hust.lar.components.Main
import com.hust.lar.ui.theme.MyChatTheme
import com.hust.resbase.ArouterConfig
import com.hust.resbase.Constant

@Route(path = ArouterConfig.ACTIVITY_LAR)
class LARActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
        setContent {
                MyChatTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background,
                    ) {
                        Main() {
                            val mmkv = MMKVUtil.getMMKV(this)
                            mmkv.put(Constant.IS_LOGIN, true)
                            ARouter.getInstance().build(ArouterConfig.ACTIVITY_HOME).navigation()
                            finish()
                        }
                    }
                }
            }

        var lastPressedTime = 0L
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (lastPressedTime + 2000L > System.currentTimeMillis()) {
                    finish()
                } else {
                    Toast.makeText(this@LARActivity, "请再按一次退出", Toast.LENGTH_SHORT)
                        .show()
                }
                lastPressedTime = System.currentTimeMillis()
            }
        })
    }
}