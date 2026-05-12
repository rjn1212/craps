import { useEffect } from 'react';
import type { RollResponse } from '../../types/game';
import './RollResultBanner.css';

function describeRoll(r: RollResponse): string {
  const total = r.dice.total;
  const parts: string[] = [];

  if (r.roundPhaseBefore === 'COME_OUT') {
    if (total === 7 || total === 11) parts.push(`Natural! ${total}`);
    else if (total === 2 || total === 3 || total === 12) parts.push(`Craps! ${total}`);
    else parts.push(`Point is ${total}`);
  } else {
    if (r.pointBefore !== null && total === r.pointBefore) {
      parts.push(r.dice.hardway ? `Hard ${total}! Point made!` : `${total}! Point made!`);
    } else if (total === 7) {
      parts.push('Seven Out!');
    } else {
      parts.push(r.dice.hardway ? `Hard ${total}` : `Rolled ${total}`);
    }
  }

  const wins = r.resolutions.filter(res => res.outcome === 'WON');
  const totalWon = wins.reduce((s, w) => s + w.payout, 0);
  if (totalWon > 0) parts.push(`Won $${totalWon}`);

  return parts.join(' — ');
}

export default function RollResultBanner({ rollResponse, onDismiss }: {
  rollResponse: RollResponse;
  onDismiss: () => void;
}) {
  useEffect(() => {
    const timer = setTimeout(onDismiss, 4000);
    return () => clearTimeout(timer);
  }, [onDismiss]);

  const hasWins = rollResponse.resolutions.some(r => r.outcome === 'WON');
  const allLost = rollResponse.resolutions.length > 0 && rollResponse.resolutions.every(r => r.outcome === 'LOST');

  return (
    <div
      className={`roll-banner ${hasWins ? 'roll-banner--win' : ''} ${allLost ? 'roll-banner--loss' : ''}`}
      onClick={onDismiss}
    >
      {describeRoll(rollResponse)}
    </div>
  );
}
