
package ispark.comet

import net.liftweb._
import http._
import util._
import Helpers._

/** Holds the lines of code and results to display within one view of a notebook session. */
class NotebookLines extends CometActor with CometListener {

  /** Each 'codeChunk' represents one box element to add to the page. */
  private var codeChunks: Vector[String] = Vector()

  def registerWith = NotebookSession

  /**
   * When receiving a new list of code chunks from the NotebookSession, update our state and
   * re-render.
   */
  override def lowPriority = {
    case v: Vector[String] => codeChunks = v; reRender()
  }

  /**
   * For each "notebookBox" div element, replace its internal "codeLabel" div with a number
   * representing the codeChunk's index in our master vector, and the "codeChunk" textarea
   * itself with the body of the code chunk.
   *
   * The codeChunk textarea should be resized to hold the text.
   */
  def render = {
    ".notebookBox" #> (codeChunks.zipWithIndex.map({case (chunk, id) => 
        (".codeLabel *" #> ("In [" + id.toString + "]: ")
      & ".codeChunk *" #> chunk
      & ".codeChunk [id]" #> ("codeChunk_ " + id.toString)
      & ".codeChunk [cols]" #> "40"
      & ".codeChunk [rows]" #> chunk.split("\n").size
    )}))
  }

}
