package uiComponent.core

import java.io.FileNotFoundException
import javax.script.SimpleScriptContext

import jdk.nashorn.api.scripting.JSObject
import model.ApplicationContext
import play.api.Mode
import play.api.libs.json.{JsObject, JsValue}

import scala.io.{BufferedSource, Source}
import scala.util.{Failure, Success, Try}

class JavascriptRenderer(javascriptFile: String) {

  private implicit lazy val scriptContext = new SimpleScriptContext()
  private lazy val memoizedJs: Try[JSObject] = loadJavascript()

  def render(props: Option[JsValue] = None)(implicit ac: ApplicationContext): Try[String] = {
    for {
      propsObject <- encodeProps(props)
      js <- if(ac.environment.mode == Mode.Dev) loadJavascript() else memoizedJs
      rendering <- JavascriptEngine.call[String](js)("render", propsObject)
    } yield rendering
  }

  private def encodeProps(props: Option[JsValue] = None): Try[JSObject] = {
    val propsId = "props"
    val emptyJson = JsObject(Seq())
    for {
      _ <- JavascriptEngine.put(propsId, props.getOrElse(emptyJson))
      propsObject <- JavascriptEngine.eval(s"JSON.parse($propsId)")
    } yield propsObject
  }

  private def loadJavascript(): Try[JSObject] = for {
    file <- loadFile(javascriptFile)
    js <- JavascriptEngine.eval(file.reader)
  } yield js

  private def loadFile(fileName: String): Try[BufferedSource] = {
    Option(getClass.getClassLoader.getResourceAsStream(fileName)) match {
      case Some(stream) => Success(Source.fromInputStream(stream, "UTF-8"))
      case None => Failure(new FileNotFoundException(s"${this.getClass.getSimpleName}: Cannot find file '$fileName'"))
    }
  }

}