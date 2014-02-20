
namespace java com.ispark.ipc

/** A request for evaluation of source code. */
struct EvalRequest {
  1: string inputCode
}

/** Response from evaluating source code. */
struct EvalResult {
  1: string outputText // the output from the interpreter.
}

service ISparkKernel {
  EvalResult evaluateCode(1: EvalRequest req)
}

