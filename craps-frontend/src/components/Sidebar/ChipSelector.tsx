import './ChipSelector.css';

const CHIPS = [
  { value: 1, color: '#eee', textColor: '#222', label: '$1' },
  { value: 5, color: '#cc2233', textColor: '#fff', label: '$5' },
  { value: 25, color: '#22aa44', textColor: '#fff', label: '$25' },
  { value: 100, color: '#222', textColor: '#fff', label: '$100' },
];

export default function ChipSelector({ selected, onSelect, bankroll }: {
  selected: number;
  onSelect: (value: number) => void;
  bankroll: number;
}) {
  return (
    <div className="chip-selector">
      <label className="chip-selector__label">Chip Value</label>
      <div className="chip-selector__chips">
        {CHIPS.map(chip => (
          <button
            key={chip.value}
            className={`chip-selector__chip ${selected === chip.value ? 'chip-selector__chip--selected' : ''}`}
            style={{
              backgroundColor: chip.color,
              color: chip.textColor,
              opacity: bankroll < chip.value ? 0.4 : 1,
            }}
            onClick={() => onSelect(chip.value)}
            disabled={bankroll < chip.value}
          >
            {chip.label}
          </button>
        ))}
      </div>
    </div>
  );
}
