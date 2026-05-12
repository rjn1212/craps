import { useState } from 'react';
import { platform } from '../../api/gameApi';

const BANKROLL_OPTIONS = [500, 1000, 5000];
const ODDS_OPTIONS = [
  { value: 1, label: '1x' },
  { value: 2, label: '2x' },
  { value: 345, label: '3-4-5x' },
  { value: 10, label: '10x' },
];

export default function NewGameDialog({ onNewGame, loading }: {
  onNewGame: (bankroll: number, odds: number) => void;
  loading: boolean;
}) {
  const [bankroll, setBankroll] = useState(1000);
  const [odds, setOdds] = useState(345);
  const [showRules, setShowRules] = useState(false);
  const returnUrl = platform.getReturnUrl();

  return (
    <div className="new-game">
      <div className="new-game__card">
        <h1 className="new-game__title">Craps</h1>
        <p className="new-game__subtitle">Casino dice game — bet smart, roll lucky</p>

        <div className="new-game__section">
          <label className="new-game__label">Starting Bankroll</label>
          <div className="new-game__options">
            {BANKROLL_OPTIONS.map(b => (
              <button
                key={b}
                className={`new-game__option ${bankroll === b ? 'new-game__option--selected' : ''}`}
                onClick={() => setBankroll(b)}
              >
                ${b.toLocaleString()}
              </button>
            ))}
          </div>
        </div>

        <div className="new-game__section">
          <label className="new-game__label">Max Odds</label>
          <div className="new-game__options">
            {ODDS_OPTIONS.map(o => (
              <button
                key={o.value}
                className={`new-game__option ${odds === o.value ? 'new-game__option--selected' : ''}`}
                onClick={() => setOdds(o.value)}
              >
                {o.label}
              </button>
            ))}
          </div>
          <span className="new-game__hint">
            {odds === 345 ? '3x on 4/10, 4x on 5/9, 5x on 6/8' : `Up to ${odds}x your line bet`}
          </span>
        </div>

        <button
          className="btn btn--primary new-game__start"
          onClick={() => onNewGame(bankroll, odds)}
          disabled={loading}
        >
          {loading ? 'Starting...' : 'Start Game'}
        </button>

        <button
          className="new-game__rules-toggle"
          onClick={() => setShowRules(!showRules)}
        >
          {showRules ? 'Hide Rules' : 'How to Play'}
        </button>

        {showRules && (
          <div className="new-game__rules">
            <h3>Basics</h3>
            <ul>
              <li><strong>Come-Out Roll:</strong> 7 or 11 wins Pass Line. 2, 3, or 12 loses it. Other numbers set the Point.</li>
              <li><strong>Point Phase:</strong> Roll the Point again before 7 to win. Roll 7 = seven-out.</li>
            </ul>
            <h3>Common Bets</h3>
            <ul>
              <li><strong>Pass Line:</strong> Bet with the shooter. Wins on natural (7/11), loses on craps (2/3/12). Even money.</li>
              <li><strong>Don't Pass:</strong> Bet against. Wins on 2/3, pushes on 12, loses on 7/11.</li>
              <li><strong>Come / Don't Come:</strong> Same as Pass/Don't Pass but placed during Point phase.</li>
              <li><strong>Odds:</strong> Behind any line bet — true odds, zero house edge!</li>
              <li><strong>Place:</strong> Bet a number (4/5/6/8/9/10) hits before 7.</li>
              <li><strong>Field:</strong> One-roll bet on 2/3/4/9/10/11/12. Pays 2:1 on 2, 3:1 on 12.</li>
            </ul>
            <h3>How to Play</h3>
            <ul>
              <li>Select a chip denomination, then click a betting area on the table.</li>
              <li>Press <strong>Roll Dice</strong> when ready.</li>
              <li>Cash out anytime to end the session.</li>
            </ul>
          </div>
        )}

        {returnUrl && returnUrl !== '/' && (
          <a href={returnUrl} className="new-game__return">
            Back to Platform
          </a>
        )}
      </div>
    </div>
  );
}
