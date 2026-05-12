import type { GameState } from '../../types/game';
import './GameInfo.css';

export default function GameInfo({ game }: { game: GameState }) {
  return (
    <div className="game-info">
      <div className="game-info__bankroll">
        <span className="game-info__label">Bankroll</span>
        <span className="game-info__value">${game.bankroll.toLocaleString()}</span>
      </div>
      <div className="game-info__row">
        <div className="game-info__item">
          <span className="game-info__label">On Table</span>
          <span className="game-info__value--small">${game.totalBetsOnTable.toLocaleString()}</span>
        </div>
        <div className="game-info__item">
          <span className="game-info__label">Phase</span>
          <span className="game-info__value--small">
            {game.roundPhase === 'COME_OUT' ? 'Come Out' : `Point: ${game.point}`}
          </span>
        </div>
        <div className="game-info__item">
          <span className="game-info__label">Rolls</span>
          <span className="game-info__value--small">{game.rollCount}</span>
        </div>
      </div>
      <div className="game-info__puck">
        {game.point ? (
          <span className="game-info__puck-on">ON {game.point}</span>
        ) : (
          <span className="game-info__puck-off">OFF</span>
        )}
      </div>
    </div>
  );
}
