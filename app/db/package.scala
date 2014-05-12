import play.api.{ DefaultApplication, Mode, Play }
import play.core.StaticApplication

package object db {
  def startDevApp() {
    Play.start(new DefaultApplication(new java.io.File("."), classOf[StaticApplication].getClassLoader, None, Mode.Dev))
  }
}