package controllers

import play.api.mvc._
import db.Users
import org.dupontmanual.forms._
import org.dupontmanual.forms.fields._

object Auth extends Controller {
  object LoginForm extends Form {
    val username = new TextField("Username")
    val password= new PasswordField("Password")
    
    val fields = List(username, password)
  }
  
  def login = Action { implicit request =>
    if(request.session.get("user").isDefined)
      Redirect(routes.Application.index).flashing(
         "message" -> "You are already logged in."
      )
    else
      Ok(views.html.login(Binding(LoginForm)))
  }
  
  def loginP = Action { implicit request =>
    import LoginForm._
    Binding(LoginForm, request) match {
      case ib: InvalidBinding => Ok(views.html.login(ib))
      case vb: ValidBinding => {
        val newUser = vb.valueOf(username)
        val isValid = Users.checkPassword(newUser, vb.valueOf(password))
        if(isValid) {
          Redirect(routes.Application.index()).withSession(
              session + (("user", newUser))
          ).flashing(
            "message" -> s"Welcome $newUser"   
          )
        } else {
          Redirect(routes.Application.index()).flashing(
            "message" -> "Incorrect username/password combination."   
          )
        }
      }
    }
  }
  
  def logout = Action { implicit request => 
    request.session.get("user") match {
      case Some(_) => 
        Redirect(routes.Application.index()).withNewSession.flashing(
          "message" -> "Come back soon!"    
        )
      case None =>
        Redirect(routes.Application.index()).flashing(
          "message" -> "You must be logged in to logout."   
        )
    }
  }
}