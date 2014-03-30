
package ispark.comet

import net.liftweb._
import http._
import util._
import Helpers._

/** Holds the lines of code and results to display within one view of a notebook session. */
class NotebookLines extends CometActor with CometListener {

  /** Each 'codeChunk' represents one box element to add to the page. */
  private var codeChunks: Vector[String] = Vector()

  /**
   * Each 'responseChunk' represents one stdout response that goes under a codeChunk.
   * There should be the same number of codeChunks and responseChunks.
   */
  private var responseChunks: Vector[String] = Vector()

  def registerWith = NotebookSession

  /**
   * When receiving a new list of code chunks and corresponding responses
   * from the NotebookSession, update our state and re-render.
   */
  override def lowPriority = {
    case (newCodes: Vector[String], newResponses: Vector[String]) => {
      codeChunks = newCodes
      responseChunks = newResponses
      reRender()
    }
  }

  /**
   * For each "notebookBox" div element, replace its internal "codeLabel" div with a number
   * representing the codeChunk's index in our master vector, and the "codeChunk" textarea
   * itself with the body of the code chunk.
   *
   * The "responseChunk" div below it should be accordingly set to the appropriate 'response'
   * object.
   *
   * The height of the codeChunk textarea should be resized to hold the text.
   */
  def render = {
    ".notebookBox" #> (codeChunks.zip(responseChunks).zipWithIndex.map({
        case ((code, response), id) => 
            (".codeLabel *" #> ("In [" + id.toString + "]: ")
          & ".codeChunk *" #> code
          & ".codeChunk [id]" #> ("codeChunk_" + id.toString)
          & ".codeChunk [cols]" #> "40"
          & ".codeChunk [rows]" #> code.split("\n").size
          & ".codeChunk [onkeyup]" #> ("resizeTextArea('codeChunk_" + id.toString + "')")
          & ".responseChunk *" #> <pre>{response}</pre>
    )}))
  }

}
