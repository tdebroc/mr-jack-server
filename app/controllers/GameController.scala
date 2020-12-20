package controllers

import com.fasterxml.jackson.databind._
import com.grooptown.mrjack.actions.ActionService
import com.grooptown.mrjack.game.GameService
import com.grooptown.mrjack.game.GameService._
import controllers.model._
import play.api.libs.json.JsValue
import play.api.mvc._
import play.mvc
import play.mvc.Results.{notFound, ok}

import javax.inject._

@Singleton
class GameController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  val mapper: ObjectMapper = new ObjectMapper()

  createGame

  def stringify(obj: Object): String = mapper.writeValueAsString(obj)

  def createGame: mvc.Result = {
    val gameId = addGame()
    games.get(gameId).initGame()
    ok(stringify(gameId))
  }

  def getGames: mvc.Result = ok(stringify(games.keySet()))

  def cloneGame(gameId: String): mvc.Result = {
    if (!games.containsKey(gameId)) NotFound("Game is not found: " + gameId)
    ok(stringify(GameService.cloneGame(gameId)))
  }

  def getGame(gameId: String): mvc.Result = {
    if (!games.containsKey(gameId)) NotFound("Game is not found : " + gameId)
    ok(stringify(games.get(gameId)))
  }

  def playAction(secretId: String): Action[AnyContent] = Action { request: Request[AnyContent] =>
    val moveRequest: JsValue = request.body.asJson.get
    val move = MoveRequest(moveRequest("gameId").as[String], moveRequest("action").as[String])
    val game = games.get(move.gameId)
    val moveAction = move.action
    val actionDetails = ActionService.getActionDetails(game, moveAction)
    if (!game.isAuthorizedToPlay(secretId)) {
      Forbidden("You can't play, this is not your turn.")
    } else if (!actionDetails.isValid) {
      BadRequest(actionDetails.errorMessage)
    } else {
      game.playAction(actionDetails)
      sendOk()
    }
  }

  def registerToGame: Action[AnyContent] = Action {
    request: Request[AnyContent] =>
      val registerRequest: JsValue = request.body.asJson.get
      val remote = request.remoteAddress
      println("Player " + remote)
      val register = RegisterRequest(
        registerRequest("gameId").as[String],
        registerRequest("pseudo").as[String],
        registerRequest("isMrJack").as[Boolean]
      )
      if (!games.containsKey(register.gameId)) {
        NotFound("Game is not found : " + register.gameId)
      } else {
        val game = games.get(register.gameId)
        if (register.isMrJack && game.isMrJackRegistered || !register.isMrJack && game.isDetectiveRegistered) {
          BadRequest("Player role already registered in this game.")
        } else {
          val secretId = game.registerPlayer(register.pseudo, register.isMrJack)
          Ok(stringify(SecretResponse(secretId)))
        }
      }
  }

  def registerAiToGame: Action[AnyContent] = Action {
    request: Request[AnyContent] =>
      val registerRequest: JsValue = request.body.asJson.get
      val register = RegisterRequestAI(
        registerRequest("gameId").as[String],
        registerRequest("aiLevel").as[String],
        registerRequest("isMrJack").as[Boolean]
      )
      if (!games.containsKey(register.gameId)) {
        NotFound("Game is not found : " + register.gameId)
      } else {
        val game = games.get(register.gameId)
        if (register.isMrJack && game.isMrJackRegistered || !register.isMrJack && game.isDetectiveRegistered) {
          BadRequest("Player role already registered in this game.")
        } else {
          game.registerAIPlayer(register.aiLevel, register.isMrJack)
          Ok(stringify(SecretResponse("AI registered")))
        }
      }
  }

  def getSecret(gameId: String, secretId: String): mvc.Result = {
    if (!games.containsKey(gameId)) notFound("Game is not found : " + gameId)
    else if (!games.get(gameId).secrets.containsKey(secretId)) notFound("Player is not found : " + secretId)
    else ok(stringify(games.get(gameId).secrets.get(secretId)))
  }

  def sendOk(): Result = {
    Ok(stringify(MessageResponse("OK")))
  }
}
