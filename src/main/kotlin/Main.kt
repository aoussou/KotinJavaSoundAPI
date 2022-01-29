import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import javax.sound.sampled.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import javax.swing.JOptionPane

@Composable
@Preview
fun App() {

    val encoding = AudioFormat.Encoding.PCM_SIGNED
    val rate = 44100F
    val sampleSize = 16
    val channels = 2

    val bigEndian = false


    val format = AudioFormat(encoding, rate, sampleSize, channels, sampleSize / 8 * channels, rate, bigEndian)
    val info = DataLine.Info(TargetDataLine::class.java, format)

    if (!AudioSystem.isLineSupported(info)) {
        println("Not supported!")
    }

    val line = AudioSystem.getLine(info) as TargetDataLine
//    line.open(format, line.bufferSize)
    line.open()

//    JOptionPane.showMessageDialog(null, "Start")
    line.start()

    class SimpleThread : Thread() {
        public override fun run() {
            val recordingStream = AudioInputStream(line)
            val outputFile = File("record.wav")

            AudioSystem.write(recordingStream, AudioFileFormat.Type.WAVE, outputFile)
        }
    }


//    JOptionPane.showMessageDialog(null, "stop")



    var isRecording by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


            Button(
                onClick = {
                    if (!isRecording) {
                        isRecording = true
                        val thread = SimpleThread()
                        thread.start()
                    }else{
                        isRecording = false
                        line.stop()
                        line.close()
                    }
                }) {
                if (!isRecording) Text(text = "START") else Text(text = "STOP")
            }

    }
}


fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}