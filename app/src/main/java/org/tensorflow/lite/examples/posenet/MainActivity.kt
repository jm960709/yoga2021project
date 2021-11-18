
/*
데모용, 정확도 높인 버전, 피드백끝나고 메인화면으로 돌아와서 버튼 누르는 버전
 */
package org.tensorflow.lite.examples.posenet

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import java.lang.reflect.Method
import java.util.*

class MainActivity : AppCompatActivity() {
    private val params = Bundle()
    private var tts: TextToSpeech? = null
    var button: Button? = null
    var intent1:Intent? = null
    var sRecognizer: SpeechRecognizer? = null
    var yogaName:String="yoga"

    private var contentTextView: TextView? = null

    val PERMISSION = 1
    private var forOnDone = 0

    private val url = "http://3.34.56.214:8080/Test1/info"

    lateinit var queue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.first_page)
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO), PERMISSION)
        }
        initTts()

        button = findViewById<View>(R.id.btn_start) as Button
        button!!.setOnClickListener(buttonListener)
        contentTextView = findViewById(R.id.tv_content)

        queue= Volley.newRequestQueue(this)
    }
    private abstract inner class runnable : Runnable

    private fun initTts() {
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, null)
        tts = TextToSpeech(this) { state ->
            if (state == TextToSpeech.SUCCESS) {
                tts!!.language = Locale.KOREAN
            } else {
                showState("TTS 객체 초기화 중 문제가 발생했습니다.")
            }
        }


        tts!!.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(s: String) {
                Log.d("확인", "onStart")
            }
            override fun onDone(s: String) {
                object : Thread() {
                    override fun run() {
                        runOnUiThread(object : runnable() {
                            override fun run() {

                                forOnDone++
                                if (forOnDone >=3) {

                                    startStt()
                                }
                            }
                        })
                    }
                }.start()
            }
            override fun onError(s: String) {
            }

        })
        intent1 = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent1!!.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        intent1!!.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
    }

    private val listener: RecognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle) {
        }

        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray) {}
        override fun onEndOfSpeech() {}
        override fun onError(error: Int) {

        }

        override fun onResults(results: Bundle) {
            val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            for (i in matches!!.indices) {
                contentTextView!!.text = matches[i]
                val string1 = "전사"
                val string2="천사"
                val string3="전화"

                val string4="나무"
                val string5="삼각"
                val string6="연습"

                if (contentTextView!!.text.toString() in string1 ||contentTextView!!.text.toString() in string2||contentTextView!!.text.toString() in string3 ){
                    yogaName="전사"
                    sendRequest(url)


                }
                else if(contentTextView!!.text.toString() in string4){
                    yogaName="나무"
                    sendRequest(url)

                }

                else if(contentTextView!!.text.toString() in string5){
                    yogaName="삼각"
                    sendRequest(url)

                }
                else if(contentTextView!!.text.toString() in string6) {
                    val avc = Intent(applicationContext, Sensor::class.java)
                    startActivity(avc)
                }
                else{
                    startSpeak("다시 말해주세요")
                }
            }
        }

        override fun onPartialResults(partialResults: Bundle) {}
        override fun onEvent(eventType: Int, params: Bundle) {}
    }
    var buttonListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.btn_start -> {
                button!!.isEnabled=false
                startTts()
            }

        }
    }

    fun sendRequest(url:String) {
        val request = object: StringRequest(Method.POST, url,
                Response.Listener<String> { response -> parseResponse(response) },
                Response.ErrorListener { }) {
            override fun getParams():Map<String, String> {
                val params = HashMap<String, String>()

              /////////////////////////////
                params["name"] = yogaName
                return params
            }
        }
        queue.add(request)
        Toast.makeText(applicationContext, "Request sent", Toast.LENGTH_LONG).show()
    }

    fun parseResponse(response:String) {
        println(response)
        val gson = Gson()
        var dto = gson.fromJson(response, Dto::class.java)
        println(dto.name+", "+dto.info)

        val intent = Intent(applicationContext, CameraActivity::class.java)
        intent.putExtra("postureInfo", dto.info)
        startActivity(intent)
    }
    private fun startTts() {

        startSpeak("안녕하세요")
        startSpeak("요가앱입니다")
        startSpeak("전사자세, 나무자세, 삼각자세, 연습모드. 중 원하는 이름을 말해주세요")

    }

    private fun startSpeak(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_ADD, params, text)

    }

    fun startStt() {
        sRecognizer = SpeechRecognizer.createSpeechRecognizer(this@MainActivity)
        sRecognizer?.setRecognitionListener(listener)
        sRecognizer?.startListening(intent1)

    }

    private fun showState(msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
    }
    override fun onDestroy() {
        tts!!.stop()
        tts!!.shutdown()
        super.onDestroy()
    }


}