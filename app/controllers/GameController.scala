package controllers

import com.fasterxml.jackson.databind._
import com.grooptown.mrjack.actions.ActionService
import com.grooptown.mrjack.ai.AIService
import com.grooptown.mrjack.game.GameService
import com.grooptown.mrjack.game.GameService._
import controllers.model.{MessageResponse, MoveRequest, RegisterRequest, RegisterRequestAI, SecretResponse}

import javax.inject._
import play.api.libs.json.JsValue
import play.api.mvc._
import play.mvc

@Singleton
class GameController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  val mapper: ObjectMapper = new ObjectMapper()

  def stringify(obj: Object): String = mapper.writeValueAsString(obj)

  def createGame: mvc.Result = {
    val gameId = addGame()
    games.get(gameId).initGame()
    play.mvc.Results.ok(stringify(gameId))
  }

  def getGames: mvc.Result = play.mvc.Results.ok(stringify(games))

  def cloneGame(gameId: String): mvc.Result = {
    if (!games.containsKey(gameId)) NotFound("Game is not found : " + gameId)
    play.mvc.Results.ok(stringify(GameService.cloneGame(gameId)))
  }

  def getGame(gameId: String): mvc.Result = {
    if (!games.containsKey(gameId)) NotFound("Game is not found : " + gameId)
    play.mvc.Results.ok(stringify(games.get(gameId)))
  }

  def playAction(secretId: String): Action[AnyContent] = Action { request: Request[AnyContent] =>
    val moveRequest: JsValue = request.body.asJson.get
    val move = MoveRequest(moveRequest("gameId").as[String], moveRequest("action").as[String])
    val game = games.get(move.gameId)
    var moveAction = move.action
    if (moveAction.startsWith("AI_")) {
      moveAction = AIService.getNextAiMoveFromUserInput(moveAction, game)
    }
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
    if (!games.containsKey(gameId)) NotFound("Game is not found : " + gameId)
    if (!games.get(gameId).secrets.containsKey(secretId)) NotFound("Player is not found : " + secretId)
    play.mvc.Results.ok(stringify(games.get(gameId).secrets.get(secretId)))
  }

  def sendOk(): Result = {
    Ok(stringify(MessageResponse("OK")))
  }
}
