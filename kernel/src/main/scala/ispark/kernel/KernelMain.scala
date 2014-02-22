
package ispark.kernel

import com.ispark.ipc._

import java.net.InetSocketAddress
import com.twitter.finagle.thrift.ThriftServerFramedCodec
import org.apache.thrift.protocol.TBinaryProtocol
import com.twitter.finagle.builder.Server
import com.twitter.finagle.builder.ServerBuilder

/** Main class: Launch the thrift service. */
object KernelMain {

  def main(args: Array[String]) {
    System.out.println("Booting Scala kernel service...")
    val service = new ISparkKernel$FinagleService(new KernelServer, new TBinaryProtocol.Factory())
    val server: Server = ServerBuilder()
        .name("SparkKernelService")
        .bindTo(new InetSocketAddress(9401))
        .codec(ThriftServerFramedCodec())
        .build(service)
  }

}
