/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tensorflow.lite.examples.posenet

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import android.speech.tts.TextToSpeech.OnInitListener
import android.speech.tts.UtteranceProgressListener


class CameraActivity : AppCompatActivity(){

  var tts: TextToSpeech? = null
  private var bundle=Bundle()
  lateinit var speakText:String
  private var forOnDone = 0
  private val params = Bundle()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.tfe_pn_activity_camera)
    savedInstanceState ?: supportFragmentManager.beginTransaction()
            .replace(R.id.container, PosenetActivity().apply { arguments=bundle })
            .commit()
    tts = TextToSpeech(this@CameraActivity, OnInitListener { status -> // TODO Auto-generated method stub
      if (status == TextToSpeech.SUCCESS) {
        val result = tts!!.setLanguage(Locale.KOREAN)
        tts!!.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
          override fun onStart(s: String) {

          }
          override fun onDone(s: String) {
            object : Thread() {
              override fun run() {
                runOnUiThread(object : runnable() {
                  override fun run() {

                  }
                })
              }
            }.start()
          }
          override fun onError(s: String) {
          }

        })
        if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED) {
          Log.e("error", "This Language is not supported")
        } else {
          startTts()
        }
      } else Log.e("error", "Initilization Failed!")
    })

    if (intent.hasExtra("postureInfo")) {
      speakText = intent.getStringExtra("postureInfo")
    }
    bundle.putString("postureInfo", speakText)

  }
  private abstract inner class runnable : Runnable
  private fun startTts() {

    startSpeak("자세 설명전 위치 조정을 시작하겠습니다")
    startSpeak("음성안내가 있을때까지 천천히 뒤로 가주세요")

  }

  private fun startSpeak(text: String) {
    tts!!.speak(text, TextToSpeech.QUEUE_ADD, params, text)

  }
  override fun onPause() {
    if (tts != null) {
      tts!!.stop()
      tts!!.shutdown()
    }
    super.onPause()
  }





}