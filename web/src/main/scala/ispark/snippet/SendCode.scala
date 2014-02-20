package ispark.snippet

import net.liftweb._
import http._
import js._
import JsCmds._
import JE._

import ispark.comet.NotebookSession

/** Send a new code fragment which is then put into the NotebookSession. */
object SendCode {

  def render = SHtml.onSubmit(newCode => {
    NotebookSession ! newCode  // Send the new code fragment to the session.
    SetValById("new_code", "") // clear the submitted code form.
  })
}

