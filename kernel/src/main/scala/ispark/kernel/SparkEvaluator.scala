package ispark.kernel

import java.io.ByteArrayOutputStream
import java.io.PrintWriter

import scala.tools.nsc.interpreter.Results._
import scala.tools.nsc.Settings

import org.apache.spark.repl.SparkCommandLine
import org.apache.spark.repl.SparkIMain

/** Evaluates scala code in a Spark interpreter and returns its stdout responses. */
class SparkEvaluator {

  // How we capture output from Spark and relay it to our client.
  val bytesOut: ByteArrayOutputStream = new ByteArrayOutputStream
  val printWriter: PrintWriter = new PrintWriter(bytesOut)

  // Scala compiler settings to initialize the Spark interpreter with.
  // Takes an argv for the spark interpreter (empty list) and an error handling fn (we
  // print to our "stdout" printWriter defined above), and retrieve the "settings" from that.
  val initialSettings: Settings = new SparkCommandLine(List(),
      msg => printWriter.print(msg)).settings

  // Use the Java classpath as well.
  initialSettings.usejavacp.value = true

  // The actual spark executor
  val sparkMain: SparkIMain = new SparkIMain(initialSettings, printWriter)

  private class EvaluationException extends Exception { }

  def evaluate(code: String): String = {

    // Clear any residual stdout from the last execution
    printWriter.flush()
    bytesOut.reset()

    try {
      code.split("\n").foreach { line => 
        sparkMain.interpret(line) match {
          case Success => { /* keep going */ }
          case Incomplete => { /* keep going */ }
          case Error => { throw new EvaluationException /* stop interpreting */ }
        }
      }
    } catch {
      // If there was an error evaluating the lines, we broke out of the loop
      // and stopped evaluating code, but we will just display the error in bytesOut..
      case ee: EvaluationException => { } /* Don't do anything in particular. */ 
    }

    // Return whatever text was collected in the bytearray output stream.
    printWriter.flush()
    return bytesOut.toString()
  }
}
