import { useCallback, useEffect } from 'react';
import { useGame } from '../../hooks/useGame';
import NewGameDialog from '../Common/NewGameDialog';
import GameOverDialog from '../Common/GameOverDialog';
import CrapsTable from '../Table/CrapsTable';
import DiceDisplay from '../Dice/DiceDisplay';
import GameInfo from '../Sidebar/GameInfo';
import ChipSelector from '../Sidebar/ChipSelector';
import BetPanel from '../Sidebar/BetPanel';
import RollHistory from '../Sidebar/RollHistory';
import RollResultBanner from '../Sidebar/RollResultBanner';
import type { BetType } from '../../types/game';
import './GameView.css';

export default function GameView() {
  const {
    game, selectedChipValue, rolling, lastRoll, loading, error,
    newGame, placeBet, removeBet, roll, cashOut, selectChip, clearRoll, clearError, resetGame,
  } = useGame();

  const handlePlaceBet = useCallback((betType: BetType) => {
    if (!game || rolling) return;

    // For Horn bets, amount must be divisible by 4
    let amount = selectedChipValue;
    if (betType === 'HORN') {
      amount = Math.max(4, Math.floor(selectedChipValue / 4) * 4);
    }

    placeBet(betType, amount);
  }, [game, rolling, selectedChipValue, placeBet]);

  // Keyboard shortcut: space to roll
  useEffect(() => {
    function handleKey(e: KeyboardEvent) {
      if (e.code === 'Space' && game?.gamePhase === 'PLAYING' && game.activeBets.length > 0 && !rolling) {
        e.preventDefault();
        roll();
      }
    }
    window.addEventListener('keydown', handleKey);
    return () => window.removeEventListener('keydown', handleKey);
  }, [game, rolling, roll]);

  if (!game) {
    return <NewGameDialog onNewGame={newGame} loading={loading} />;
  }

  if (game.gamePhase === 'GAME_OVER') {
    return <GameOverDialog game={game} onPlayAgain={resetGame} />;
  }

  const lastDice = game.recentRolls.length > 0
    ? game.recentRolls[game.recentRolls.length - 1]!.dice
    : null;

  return (
    <div className="app app--game">
      {lastRoll && (
        <RollResultBanner rollResponse={lastRoll} onDismiss={clearRoll} />
      )}

      <div className="app__board-area">
        <DiceDisplay dice={lastDice} rolling={rolling} />

        <CrapsTable
          activeBets={game.activeBets}
          point={game.point}
          roundPhase={game.roundPhase}
          onPlaceBet={handlePlaceBet}
        />

        <div className="app__board-actions">
          <button
            className="btn btn--primary game-view__roll-btn"
            onClick={roll}
            disabled={rolling || game.activeBets.length === 0}
          >
            {rolling ? 'Rolling...' : 'Roll Dice'}
          </button>
        </div>
      </div>

      <div className="app__side-panel">
        <GameInfo game={game} />

        <div className="game-view__divider" />

        <ChipSelector
          selected={selectedChipValue}
          onSelect={selectChip}
          bankroll={game.bankroll}
        />

        <div className="game-view__divider" />

        <BetPanel bets={game.activeBets} onRemove={removeBet} />

        <div className="game-view__divider" />

        <RollHistory rolls={game.recentRolls} />

        <div className="game-view__divider" />

        <button className="btn btn--secondary" onClick={cashOut}>
          Cash Out
        </button>

        <button className="btn btn--danger" onClick={resetGame} style={{ marginTop: '0.5rem' }}>
          New Game
        </button>
      </div>

      {error && (
        <div className="app__error" onClick={clearError}>
          {error}
        </div>
      )}
    </div>
  );
}
