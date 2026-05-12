import type { GameState } from '../../types/game';

export default function GameOverDialog({ game, onPlayAgain }: {
  game: GameState;
  onPlayAgain: () => void;
}) {
  const net = game.bankroll - game.startingBankroll;
  const won = net >= 0;

  return (
    <div className="new-game">
      <div className="new-game__card">
        <h1 className="new-game__title">{won ? 'Winner!' : 'Busted'}</h1>
        <p className="new-game__subtitle">
          {game.gameOverReason === 'broke'
            ? 'You ran out of chips!'
            : 'You cashed out.'}
        </p>

        <div style={{ textAlign: 'center', margin: '1.5rem 0' }}>
          <div style={{ fontSize: '2rem', fontWeight: 'bold', color: won ? 'var(--success)' : 'var(--danger)' }}>
            {won ? '+' : ''}{net.toLocaleString()}
          </div>
          <div style={{ color: 'var(--text-muted)', marginTop: '0.5rem' }}>
            Final: ${game.bankroll.toLocaleString()} | Rolls: {game.rollCount}
          </div>
        </div>

        <button className="btn btn--primary new-game__start" onClick={onPlayAgain}>
          Play Again
        </button>
      </div>
    </div>
  );
}
