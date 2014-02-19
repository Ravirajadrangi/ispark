
package ispark.comet

import net.liftweb._
import http._
import actor._

/**
 * Holds all state for one session of an open notebook.
 */
object NotebookSession extends LiftActor with ListenerManager {
  /** Each codeChunk represents one input code chunk. */
  private var codeChunks: Vector[String] = Vector("val x=4\nval y=5", "print x")
  /** Each responseChunk represents one stdout chunk associated with a codeChunk. */
  private var responseChunks: Vector[String] = Vector("", "4")

  /** An "update" to send to listeners (NotebookLines instances) are the chunks of code. */
  def createUpdate = (codeChunks, responseChunks)

  /** New code chunks are appended to the list. */
  override def lowPriority = {
    case codeChunk: String => {
      codeChunks :+= codeChunk
      responseChunks :+= "" // For now, associate empty response w/ new code chunk.
      updateListeners()
    }
  }
}
