
package ispark.exec

import java.io.File
import java.net.URL
import java.net.URLClassLoader

/** Pulls the classpath out of the current context classloader. */
class ClasspathGrabber {

  def getClasspath(): String = {
    val buffer: StringBuffer = new StringBuffer
    val sep = System.getProperty("path.separator")
    val classLoader: ClassLoader = Thread.currentThread.getContextClassLoader
    if (classLoader.isInstanceOf[URLClassLoader]) {
      // Treat it as a URLClassloader and grab URLs explicitly.
      classLoader.asInstanceOf[URLClassLoader].getURLs.foreach { url: URL =>
        buffer.append(new File(url.getPath()))
        buffer.append(sep)
      }
      return buffer.toString() + sep + System.getProperty("java.class.path")
    } else {
      return System.getProperty("java.class.path") // Just use standard Java classpath.
    }
  }
}
