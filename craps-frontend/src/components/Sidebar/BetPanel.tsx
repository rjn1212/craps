import type { BetDto } from '../../types/game';
import './BetPanel.css';

export default function BetPanel({ bets, onRemove }: {
  bets: BetDto[];
  onRemove: (betId: string) => void;
}) {
  if (bets.length === 0) {
    return <div className="bet-panel__empty">No active bets</div>;
  }

  return (
    <div className="bet-panel">
      <label className="bet-panel__label">Active Bets</label>
      <div className="bet-panel__list">
        {bets.map(bet => (
          <div key={bet.id} className={`bet-panel__item ${!bet.isOn ? 'bet-panel__item--off' : ''}`}>
            <div className="bet-panel__bet-info">
              <span className="bet-panel__bet-name">{bet.displayName}</span>
              {bet.pointNumber && (
                <span className="bet-panel__bet-point">on {bet.pointNumber}</span>
              )}
              {!bet.isOn && <span className="bet-panel__bet-off">OFF</span>}
            </div>
            <div className="bet-panel__bet-right">
              <span className="bet-panel__bet-amount">${bet.amount}</span>
              <button
                className="bet-panel__remove"
                onClick={() => onRemove(bet.id)}
                title="Remove bet"
              >
                x
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
