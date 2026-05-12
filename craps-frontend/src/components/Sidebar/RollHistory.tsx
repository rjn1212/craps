import type { RollRecordDto } from '../../types/game';
import './RollHistory.css';

function rollDescription(roll: RollRecordDto): string {
  const total = roll.dice.total;
  if (roll.phaseBefore === 'COME_OUT') {
    if (total === 7 || total === 11) return 'Natural!';
    if (total === 2 || total === 3 || total === 12) return 'Craps!';
    return `Point is ${total}`;
  }
  if (roll.pointBefore !== null && total === roll.pointBefore) {
    return roll.dice.hardway ? `Hard ${total}! Winner!` : `${total}! Winner!`;
  }
  if (total === 7) return 'Seven Out!';
  return roll.dice.hardway ? `Hard ${total}` : `${total}`;
}

export default function RollHistory({ rolls }: { rolls: RollRecordDto[] }) {
  if (rolls.length === 0) return null;

  return (
    <div className="roll-history">
      <label className="roll-history__label">Roll History</label>
      <div className="roll-history__list">
        {[...rolls].reverse().map(r => {
          const hasWins = r.resolutions.some(res => res.outcome === 'WON');
          const hasLosses = r.resolutions.some(res => res.outcome === 'LOST');
          return (
            <div key={r.rollNumber} className="roll-history__item">
              <span className="roll-history__dice">
                {r.dice.die1} + {r.dice.die2} = {r.dice.total}
              </span>
              <span className={`roll-history__desc ${hasWins ? 'roll-history__desc--win' : ''} ${hasLosses && !hasWins ? 'roll-history__desc--loss' : ''}`}>
                {rollDescription(r)}
              </span>
            </div>
          );
        })}
      </div>
    </div>
  );
}
