

class Board {
  cells: Cell[][] = []
  districts: any | undefined = undefined
}

class CurrentAction {
  actionName: string | undefined = undefined
}

class ActionToken {
  currentAction: CurrentAction = new CurrentAction()
  recto: boolean = false
  tokenName: string | undefined = undefined
  used: boolean = false
}

class Player {
  turnTokenCount: number = 0
  alibiCards: String[] = [];
  alibiCardsCount: number = 0;
}

export class Game {
  actionTokens: ActionToken[] = []
  board: Board = new Board()
  currentPlayer: Player = new Player()
  detectiveCurrentPlayer: boolean | undefined = undefined
  detectivePlayer: Player | undefined = undefined
  mrJack: Player = new Player()
  turnNumber: number | undefined = undefined
  winner: string | undefined = undefined;
  detectiveRegistered: boolean = false;
  mrJackRegistered: boolean = false;
  history: String[] = [];
}
