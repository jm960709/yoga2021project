package org.tensorflow.lite.examples.posenet

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.util.*

class Sensor : AppCompatActivity(){

    var tts: TextToSpeech? = null
    private var bundle=Bundle()
    lateinit var speakText:String
    private var forOnDone = 0
    private val params = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.second)

        tts = TextToSpeech(this@Sensor,
            TextToSpeech.OnInitListener { status -> // TODO Auto-generated method stub
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
                                            forOnDone++
                                            if(forOnDone>=20)
                                            {
                                                val intent = Intent(this@Sensor, MainActivity::class.java)
                                                startActivity(intent)
                                            }
                                        }
                                    })
                                }
                            }.start()
                        }

                        override fun onError(s: String) {
                        }

                    })
                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED
                    ) {
                        Log.e("error", "This Language is not supported")
                    } else {
                        startTts()
                    }
                } else Log.e("error", "Initilization Failed!")
            })



    }
    private abstract inner class runnable : Runnable
    private fun startTts() {

        startSpeak("요가자세 연습모드를 시작합니다")
        startSpeak("보조기구를 함께 사용하세요")

        startSpeak("첫번째 전사자세 연습 시작. ")
        startSpeak("오른발의 끝을 바깥으로 향하게 놓고 왼발끝은 정면을 향하게 선다.")
        startSpeak("양 다리를 최대한 벌린다.")
        startSpeak("오른쪽 무릎이 n에 오게해 오른쪽 다리가 직각이 되게한다.")
        startSpeak("이때 왼쪽 다리가 일자로 펴질수 있도록 주의한다.")
        startSpeak("왼쪽손을 e  오른쪽 손을 h에 오도록 양 옆을 향해 뻗으며 오른쪽을 바라본다.")



        startSpeak("두번째 나무자세 연습 시작. ")
        startSpeak("두발을 모아 엄지발가락을 붙이고 똑바로 선다")
        startSpeak("두 손을 펴서 가슴앞에서 모은다 ")
        startSpeak("이때 왼쪽 팔굼치는 g에 오른쪽 팔굼치는 f에 오게 한다")
        startSpeak("오른발을 구부려 오른 발바닥을 왼쪽 허벅지 안쪽에 붙여 오른쪽 무릎이 j에 오게한다.")

        startSpeak("세번째 삼각자세 연습 시작. ")
        startSpeak("오른발의 끝을 바깥으로 향하게 놓고 왼발끝은 정면을 향하게 선다.")
        startSpeak("양발를 최대한 벌린다.")
        startSpeak("양팔을 벌려 수평이 되게 어깨 높이로 올린다")
        startSpeak("숨을 천천히 들이마시면서 양팔과 몸을 오른쪽으로 기울인다.")
        startSpeak("이때 골반이 정면을 향하며 오른손이 오른쪽다리에 닿을수있도록 하며 왼손은 b와 c 중간에 오게한다.")

        startSpeak("연습모드 종료 메인화면으로 이동합니다")


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