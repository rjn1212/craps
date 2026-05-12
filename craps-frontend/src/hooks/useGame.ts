import { useReducer, useCallback } from 'react';
import type { GameState, RollResponse, BetType } from '../types/game';
import * as api from '../api/gameApi';

interface GameStore {
  game: GameState | null;
  selectedChipValue: number;
  rolling: boolean;
  lastRoll: RollResponse | null;
  loading: boolean;
  error: string | null;
}

type Action =
  | { type: 'SET_LOADING' }
  | { type: 'SET_ERROR'; error: string }
  | { type: 'CLEAR_ERROR' }
  | { type: 'SET_GAME'; game: GameState }
  | { type: 'SELECT_CHIP'; value: number }
  | { type: 'ROLLING' }
  | { type: 'ROLL_COMPLETE'; game: GameState; rollResponse: RollResponse }
  | { type: 'BET_PLACED'; game: GameState }
  | { type: 'BET_REMOVED'; game: GameState }
  | { type: 'CLEAR_ROLL' }
  | { type: 'RESET' };

const initialState: GameStore = {
  game: null,
  selectedChipValue: 25,
  rolling: false,
  lastRoll: null,
  loading: false,
  error: null,
};

function reducer(state: GameStore, action: Action): GameStore {
  switch (action.type) {
    case 'SET_LOADING':
      return { ...state, loading: true, error: null };
    case 'SET_ERROR':
      return { ...state, loading: false, rolling: false, error: action.error };
    case 'CLEAR_ERROR':
      return { ...state, error: null };
    case 'SET_GAME':
      return { ...state, game: action.game, loading: false };
    case 'SELECT_CHIP':
      return { ...state, selectedChipValue: action.value };
    case 'ROLLING':
      return { ...state, rolling: true, lastRoll: null };
    case 'ROLL_COMPLETE':
      return { ...state, rolling: false, game: action.game, lastRoll: action.rollResponse };
    case 'BET_PLACED':
      return { ...state, game: action.game, error: null };
    case 'BET_REMOVED':
      return { ...state, game: action.game };
    case 'CLEAR_ROLL':
      return { ...state, lastRoll: null };
    case 'RESET':
      return initialState;
    default:
      return state;
  }
}

function rollResponseToGameState(game: GameState, r: RollResponse): GameState {
  return {
    ...game,
    bankroll: r.bankroll,
    roundPhase: r.roundPhaseAfter,
    point: r.pointAfter,
    gamePhase: r.gamePhase,
    activeBets: r.activeBets,
    rollCount: r.rollCount,
    gameOverReason: r.gameOverReason,
    totalBetsOnTable: r.activeBets.reduce((sum, b) => sum + b.amount, 0),
    recentRolls: [
      ...game.recentRolls,
      {
        rollNumber: r.rollCount,
        dice: r.dice,
        phaseBefore: r.roundPhaseBefore,
        pointBefore: r.pointBefore,
        phaseAfter: r.roundPhaseAfter,
        pointAfter: r.pointAfter,
        resolutions: r.resolutions,
        bankrollAfter: r.bankroll,
      },
    ].slice(-10),
  };
}

export function useGame() {
  const [state, dispatch] = useReducer(reducer, initialState);

  const newGame = useCallback(async (startingBankroll: number, maxOddsMultiple: number) => {
    dispatch({ type: 'SET_LOADING' });
    try {
      const game = await api.createGame({ startingBankroll, maxOddsMultiple });
      dispatch({ type: 'SET_GAME', game });
    } catch (e) {
      dispatch({ type: 'SET_ERROR', error: e instanceof Error ? e.message : 'Failed to create game' });
    }
  }, []);

  const placeBet = useCallback(async (betType: BetType, amount: number, targetNumber?: number) => {
    if (!state.game) return;
    try {
      const resp = await api.placeBet(state.game.gameId, { betType, amount, targetNumber });
      dispatch({
        type: 'BET_PLACED',
        game: {
          ...state.game,
          bankroll: resp.bankroll,
          activeBets: resp.activeBets,
          totalBetsOnTable: resp.activeBets.reduce((sum, b) => sum + b.amount, 0),
        },
      });
    } catch (e) {
      dispatch({ type: 'SET_ERROR', error: e instanceof Error ? e.message : 'Failed to place bet' });
    }
  }, [state.game]);

  const removeBet = useCallback(async (betId: string) => {
    if (!state.game) return;
    try {
      const resp = await api.removeBet(state.game.gameId, betId);
      dispatch({
        type: 'BET_REMOVED',
        game: {
          ...state.game,
          bankroll: resp.bankroll,
          activeBets: resp.activeBets,
          totalBetsOnTable: resp.activeBets.reduce((sum, b) => sum + b.amount, 0),
        },
      });
    } catch (e) {
      dispatch({ type: 'SET_ERROR', error: e instanceof Error ? e.message : 'Failed to remove bet' });
    }
  }, [state.game]);

  const doRoll = useCallback(async () => {
    if (!state.game || state.rolling) return;
    dispatch({ type: 'ROLLING' });
    try {
      const rollResponse = await api.roll(state.game.gameId);
      const updatedGame = rollResponseToGameState(state.game, rollResponse);
      dispatch({ type: 'ROLL_COMPLETE', game: updatedGame, rollResponse });
    } catch (e) {
      dispatch({ type: 'SET_ERROR', error: e instanceof Error ? e.message : 'Failed to roll' });
    }
  }, [state.game, state.rolling]);

  const doCashOut = useCallback(async () => {
    if (!state.game) return;
    try {
      await api.cashOut(state.game.gameId);
      const game = await api.getGame(state.game.gameId);
      dispatch({ type: 'SET_GAME', game });
    } catch (e) {
      dispatch({ type: 'SET_ERROR', error: e instanceof Error ? e.message : 'Failed to cash out' });
    }
  }, [state.game]);

  const selectChip = useCallback((value: number) => {
    dispatch({ type: 'SELECT_CHIP', value });
  }, []);

  const clearRoll = useCallback(() => {
    dispatch({ type: 'CLEAR_ROLL' });
  }, []);

  const clearError = useCallback(() => {
    dispatch({ type: 'CLEAR_ERROR' });
  }, []);

  const resetGame = useCallback(() => {
    dispatch({ type: 'RESET' });
  }, []);

  return {
    ...state,
    newGame,
    placeBet,
    removeBet,
    roll: doRoll,
    cashOut: doCashOut,
    selectChip,
    clearRoll,
    clearError,
    resetGame,
  };
}
