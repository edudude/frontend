package uiComponent

import model.ApplicationContext
import play.api.libs.json.JsValue
import play.api.mvc.Result
import play.api.mvc.Results._
import uiComponent.core.JavascriptRenderer

import scala.util.{Failure, Success}

trait UIComponentState {
  def asJson: JsValue
}

abstract class UIComponent[-S <: UIComponentState](javascriptFile: String) {

  lazy val renderer = new JavascriptRenderer(javascriptFile)

  def render(state: Option[S] = None)(implicit ac: ApplicationContext): Result =
    renderer.render(state.map(_.asJson)) match {
      case Success(s) => Ok(s).withHeaders("Content-Type" -> "text/html")
      case Failure(f) => InternalServerError(f.getLocalizedMessage)
    }
}
