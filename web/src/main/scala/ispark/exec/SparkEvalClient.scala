
package ispark.exec

import scala.sys.process._

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream

import com.twitter.conversions.time._
import com.twitter.finagle.Service
import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.thrift.ThriftClientFramedCodec
import com.twitter.finagle.thrift.ThriftClientRequest
import org.apache.thrift.protocol.TBinaryProtocol
import java.net.InetSocketAddress

import com.ispark.ipc._


/**
 * Client for the evaluator process, which we speak to via IPC. Spawns
 * a new child process to run the evaluator and connects to it.
 */
class SparkEvalClient {

  // argv to launch an execution kernel. We can use our own classpath
  // because we have a (false) dependency on the ispark-kernel lib in
  // the ispark-web sbt project definition.
  private val argv = Seq(System.getProperty("java.home") + "/bin/java",
      "-cp", new ClasspathGrabber().getClasspath(),
      "ispark.kernel.KernelMain")

  // method to print an InputStream to the console (used in a bg thread).
  private def printer(in: InputStream): Unit = {
    try {
      val br: BufferedReader = new BufferedReader(new InputStreamReader(in))
      Iterator.continually (br.readLine).takeWhile(null !=).foreach(System.out.print(_:String))
    } finally {
      in.close() // ProcessIO threads are responsible for closing their associated streams.
    }
  }

  private def closeFn(out: OutputStream): Unit = {
    out.close()
  }

  // Launch the IPC process and hold on to it in a ProcessIO object.
  // TODO: Actually use the printer method above or something like it.
  // Requires converting strange stream types to normal ones.
  private val evalProcessThread: Thread = new Thread(new Runnable {
    override def run(): Unit = {
      System.out.println("Starting with argv: " + argv.toString())
      argv ! ProcessLogger((s) => System.out.println(s), (s) => System.err.println(s))
    }
  })

  evalProcessThread.setDaemon(true)
  evalProcessThread.start

  // TODO: Deal intelligently with the race condition around starting the child process
  // and then connecting to the service it exposes.

  private val service: Service[ThriftClientRequest, Array[Byte]] = ClientBuilder()
    .hosts(new InetSocketAddress(9401))
    .codec(ThriftClientFramedCodec())
    .hostConnectionLimit(1)
    .build()

  // Wrap the raw Thrift service in a Client decorator. The client provides
  // a convenient procedural interface for accessing the Thrift server.
  val client = new ISparkKernel$FinagleClient(service, new TBinaryProtocol.Factory())

  def eval(code: String): String = {
    // TODO: Instead of blocking on the 'get' here, we should really just return the Future
    // and let that update the client at its own pace.
    client.evaluateCode(EvalRequest(code)).get match {
      case EvalResult(resultText: String) => { return resultText}
    }
  }
}
