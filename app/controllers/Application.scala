package controllers

import play.api.mvc._
import db.Users
import org.dupontmanual.forms._
import org.dupontmanual.forms.fields._

object Application extends Controller {

  def index() = Action { implicit request =>
    val message = request.flash.get("message") getOrElse ""
    Ok(views.html.index(message))
  }
  
}