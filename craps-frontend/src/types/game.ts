export type RoundPhase = 'COME_OUT' | 'POINT';
export type GamePhase = 'PLAYING' | 'GAME_OVER';
export type BetStatus = 'ACTIVE' | 'WON' | 'LOST' | 'RETURNED';

export type BetType =
  | 'PASS_LINE' | 'DONT_PASS' | 'COME' | 'DONT_COME'
  | 'PASS_ODDS' | 'DONT_PASS_ODDS' | 'COME_ODDS' | 'DONT_COME_ODDS'
  | 'PLACE_4' | 'PLACE_5' | 'PLACE_6' | 'PLACE_8' | 'PLACE_9' | 'PLACE_10'
  | 'FIELD'
  | 'ANY_SEVEN' | 'ANY_CRAPS' | 'YO' | 'ACES' | 'BOXCARS'
  | 'HARD_4' | 'HARD_6' | 'HARD_8' | 'HARD_10'
  | 'C_AND_E' | 'HORN';

export interface DiceRoll {
  die1: number;
  die2: number;
  total: number;
  hardway: boolean;
}

export interface BetDto {
  id: string;
  betType: BetType;
  displayName: string;
  amount: number;
  status: BetStatus;
  pointNumber: number | null;
  isOn: boolean;
}

export interface BetResolutionDto {
  betId: string;
  betType: BetType;
  displayName: string;
  wagerAmount: number;
  payout: number;
  outcome: BetStatus;
}

export interface RollRecordDto {
  rollNumber: number;
  dice: DiceRoll;
  phaseBefore: RoundPhase;
  pointBefore: number | null;
  phaseAfter: RoundPhase;
  pointAfter: number | null;
  resolutions: BetResolutionDto[];
  bankrollAfter: number;
}

export interface GameState {
  gameId: string;
  bankroll: number;
  startingBankroll: number;
  roundPhase: RoundPhase;
  point: number | null;
  gamePhase: GamePhase;
  activeBets: BetDto[];
  recentRolls: RollRecordDto[];
  rollCount: number;
  totalBetsOnTable: number;
  gameOverReason: string | null;
  minBet: number;
  maxBet: number;
  maxOddsMultiple: number;
}

export interface RollResponse {
  dice: DiceRoll;
  roundPhaseBefore: RoundPhase;
  pointBefore: number | null;
  roundPhaseAfter: RoundPhase;
  pointAfter: number | null;
  resolutions: BetResolutionDto[];
  bankroll: number;
  activeBets: BetDto[];
  gamePhase: GamePhase;
  gameOverReason: string | null;
  rollCount: number;
}

export interface PlaceBetResponse {
  placedBet: BetDto;
  bankroll: number;
  activeBets: BetDto[];
}

export interface RemoveBetResponse {
  bankroll: number;
  activeBets: BetDto[];
}

export interface CashOutResponse {
  finalBankroll: number;
  netResult: number;
  totalRolls: number;
  totalBetsPlaced: number;
}

export interface CreateGameRequest {
  startingBankroll: number;
  maxOddsMultiple: number;
}

export interface PlaceBetRequest {
  betType: BetType;
  amount: number;
  targetNumber?: number;
}
