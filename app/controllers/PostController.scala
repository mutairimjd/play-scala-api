package controllers

import javax.inject._

import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class PostController @Inject()(
    repo: PostRepository,
    cc: MessagesControllerComponents
)(implicit ec: ExecutionContext)
    extends MessagesAbstractController(cc) {

  val postForm: Form[CreatePostForm] = Form {
    mapping(
      "nickname" -> nonEmptyText,
      "post" -> text
    )(CreatePostForm.apply)(CreatePostForm.unapply)
  }

  def index = Action { implicit request =>
    Ok(views.html.index(postForm))
  }

  def addPost = Action.async { implicit request =>
    postForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.index(errorForm)))
      },
      post => {
        repo.create(post.nickname, post.post).map { _ =>
          Redirect(routes.PostController.index)
            .flashing("success" -> "post.created")
        }
      }
    )
  }

  def getPost = Action.async { implicit request =>
    repo.list().map { posts =>
      Ok(Json.toJson(posts))
    }
  }
}

case class CreatePostForm(nickname: String, post: String)
