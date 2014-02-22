
package ispark.kernel

import com.ispark.ipc._
import com.twitter.util.Future

/** Implementation of the Thrift service to interpret spark code on request. */
class KernelServer extends ISparkKernel.FutureIface {

  val evaluator = new SparkEvaluator

  override def evaluateCode(req: EvalRequest): Future[EvalResult] = {
    val outStr: String = evaluator.evaluate(req.inputCode)
    return Future.value(EvalResult(outputText = outStr))
  }
  
}
