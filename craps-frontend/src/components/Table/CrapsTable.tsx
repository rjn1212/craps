import type { BetType, BetDto, RoundPhase } from '../../types/game';
import './CrapsTable.css';

interface BettingZone {
  id: BetType;
  label: string;
  area: string;
}

const PLACE_ZONES: BettingZone[] = [
  { id: 'PLACE_4', label: '4', area: 'p4' },
  { id: 'PLACE_5', label: '5', area: 'p5' },
  { id: 'PLACE_6', label: 'SIX', area: 'p6' },
  { id: 'PLACE_8', label: '8', area: 'p8' },
  { id: 'PLACE_9', label: 'NINE', area: 'p9' },
  { id: 'PLACE_10', label: '10', area: 'p10' },
];

const PROP_ZONES: BettingZone[] = [
  { id: 'ANY_SEVEN', label: 'Any 7', area: 'any7' },
  { id: 'ANY_CRAPS', label: 'Any Craps', area: 'anyc' },
  { id: 'HARD_4', label: 'Hard 4', area: 'h4' },
  { id: 'HARD_6', label: 'Hard 6', area: 'h6' },
  { id: 'HARD_8', label: 'Hard 8', area: 'h8' },
  { id: 'HARD_10', label: 'Hard 10', area: 'h10' },
  { id: 'YO', label: 'Yo 11', area: 'yo' },
  { id: 'C_AND_E', label: 'C & E', area: 'ce' },
  { id: 'ACES', label: 'Aces', area: 'aces' },
  { id: 'BOXCARS', label: '12', area: 'box' },
  { id: 'HORN', label: 'Horn', area: 'horn' },
];

function getBetsForType(activeBets: BetDto[], betType: BetType): BetDto[] {
  return activeBets.filter(b => b.betType === betType);
}

function totalForType(activeBets: BetDto[], betType: BetType): number {
  return getBetsForType(activeBets, betType).reduce((s, b) => s + b.amount, 0);
}

export default function CrapsTable({ activeBets, point, roundPhase, onPlaceBet }: {
  activeBets: BetDto[];
  point: number | null;
  roundPhase: RoundPhase;
  onPlaceBet: (betType: BetType) => void;
}) {
  const pointToArea: Record<number, string> = { 4: 'p4', 5: 'p5', 6: 'p6', 8: 'p8', 9: 'p9', 10: 'p10' };

  return (
    <div className="craps-table">
      {/* Puck indicator */}
      {point && (
        <div className={`craps-table__puck craps-table__puck--${pointToArea[point]}`}>
          ON
        </div>
      )}

      {/* Place numbers row */}
      <div className="craps-table__places">
        {PLACE_ZONES.map(z => {
          const amt = totalForType(activeBets, z.id);
          return (
            <button key={z.id} className="craps-table__zone craps-table__zone--place" onClick={() => onPlaceBet(z.id)}>
              <span className="craps-table__zone-label">{z.label}</span>
              {amt > 0 && <span className="craps-table__chip-marker">${amt}</span>}
            </button>
          );
        })}
      </div>

      {/* Main betting areas */}
      <div className="craps-table__main">
        <button className="craps-table__zone craps-table__zone--come" onClick={() => onPlaceBet('COME')}>
          <span className="craps-table__zone-label">COME</span>
          {totalForType(activeBets, 'COME') > 0 && (
            <span className="craps-table__chip-marker">${totalForType(activeBets, 'COME')}</span>
          )}
        </button>

        <button className="craps-table__zone craps-table__zone--dc" onClick={() => onPlaceBet('DONT_COME')}>
          <span className="craps-table__zone-label">DON'T COME</span>
          {totalForType(activeBets, 'DONT_COME') > 0 && (
            <span className="craps-table__chip-marker">${totalForType(activeBets, 'DONT_COME')}</span>
          )}
        </button>

        <button className="craps-table__zone craps-table__zone--field" onClick={() => onPlaceBet('FIELD')}>
          <span className="craps-table__zone-label">FIELD</span>
          <span className="craps-table__zone-sub">2 pays 2:1 · 12 pays 3:1</span>
          {totalForType(activeBets, 'FIELD') > 0 && (
            <span className="craps-table__chip-marker">${totalForType(activeBets, 'FIELD')}</span>
          )}
        </button>
      </div>

      {/* Pass / Don't Pass */}
      <div className="craps-table__pass-row">
        <button className="craps-table__zone craps-table__zone--dp" onClick={() => onPlaceBet('DONT_PASS')}>
          <span className="craps-table__zone-label">DON'T PASS BAR</span>
          {totalForType(activeBets, 'DONT_PASS') > 0 && (
            <span className="craps-table__chip-marker">${totalForType(activeBets, 'DONT_PASS')}</span>
          )}
        </button>
        <button className="craps-table__zone craps-table__zone--pass" onClick={() => onPlaceBet('PASS_LINE')}>
          <span className="craps-table__zone-label">PASS LINE</span>
          {totalForType(activeBets, 'PASS_LINE') > 0 && (
            <span className="craps-table__chip-marker">${totalForType(activeBets, 'PASS_LINE')}</span>
          )}
        </button>
      </div>

      {/* Odds buttons */}
      {roundPhase === 'POINT' && (
        <div className="craps-table__odds-row">
          {activeBets.some(b => b.betType === 'PASS_LINE') && !activeBets.some(b => b.betType === 'PASS_ODDS') && (
            <button className="craps-table__zone craps-table__zone--odds" onClick={() => onPlaceBet('PASS_ODDS')}>
              <span className="craps-table__zone-label">PASS ODDS</span>
            </button>
          )}
          {activeBets.some(b => b.betType === 'DONT_PASS') && !activeBets.some(b => b.betType === 'DONT_PASS_ODDS') && (
            <button className="craps-table__zone craps-table__zone--odds" onClick={() => onPlaceBet('DONT_PASS_ODDS')}>
              <span className="craps-table__zone-label">DP ODDS</span>
            </button>
          )}
          {totalForType(activeBets, 'PASS_ODDS') > 0 && (
            <span className="craps-table__odds-marker">Pass Odds: ${totalForType(activeBets, 'PASS_ODDS')}</span>
          )}
          {totalForType(activeBets, 'DONT_PASS_ODDS') > 0 && (
            <span className="craps-table__odds-marker">DP Odds: ${totalForType(activeBets, 'DONT_PASS_ODDS')}</span>
          )}
        </div>
      )}

      {/* Propositions */}
      <div className="craps-table__props">
        <div className="craps-table__props-label">PROPOSITIONS</div>
        <div className="craps-table__props-grid">
          {PROP_ZONES.map(z => {
            const amt = totalForType(activeBets, z.id);
            return (
              <button key={z.id} className="craps-table__zone craps-table__zone--prop" onClick={() => onPlaceBet(z.id)}>
                <span className="craps-table__zone-label">{z.label}</span>
                {amt > 0 && <span className="craps-table__chip-marker">${amt}</span>}
              </button>
            );
          })}
        </div>
      </div>
    </div>
  );
}
