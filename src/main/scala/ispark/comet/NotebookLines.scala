
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
   * Replace the body of each 'codeChunkText'-classed element within a 'codeChunk'-classed elem
   * with the associated codeChunk and a prefix consisting of "[n]: ", its index in the vector.
   *
   * Set the id of each codeChunkText to codeChunkText_n, and its double-click behavior
   * is to turn it into a textarea for the user's modification.
   */
  def render = {
    ".notebookBox" #> (codeChunks.zipWithIndex.map({case (chunk, id) => 
        (".codeLabel *" #> ("In [" + id.toString + "]: ")
      & ".codeChunk *" #> chunk
      & ".codeChunk [ondblclick]" #> ("enableInnerEdit('codeChunkText_" + id.toString + "')")
      & ".codeChunk [id]" #> ("codeChunkText_" + id.toString)
    )}))
  }

}
