import type { DiceRoll } from '../../types/game';
import './DiceDisplay.css';

const DOT_POSITIONS: Record<number, [number, number][]> = {
  1: [[50, 50]],
  2: [[25, 25], [75, 75]],
  3: [[25, 25], [50, 50], [75, 75]],
  4: [[25, 25], [75, 25], [25, 75], [75, 75]],
  5: [[25, 25], [75, 25], [50, 50], [25, 75], [75, 75]],
  6: [[25, 20], [75, 20], [25, 50], [75, 50], [25, 80], [75, 80]],
};

function Die({ value, rolling }: { value: number; rolling: boolean }) {
  const dots = DOT_POSITIONS[value] ?? [];
  return (
    <div className={`die ${rolling ? 'die--rolling' : ''}`}>
      {dots.map(([x, y], i) => (
        <div
          key={i}
          className="die__dot"
          style={{ left: `${x}%`, top: `${y}%` }}
        />
      ))}
    </div>
  );
}

export default function DiceDisplay({ dice, rolling }: {
  dice: DiceRoll | null;
  rolling: boolean;
}) {
  return (
    <div className="dice-display">
      <Die value={dice?.die1 ?? 1} rolling={rolling} />
      <Die value={dice?.die2 ?? 1} rolling={rolling} />
      {dice && !rolling && (
        <div className="dice-display__total">{dice.total}</div>
      )}
    </div>
  );
}
