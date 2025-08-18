// [FILEPATH] src/constants/games.ts
export const DIFFICULTIES = [
  { value: 'easy',     label: '쉬움' },
  { value: 'normal',   label: '중간' },
  { value: 'hard',     label: '어려움' },
  { value: 'veryHard', label: '매우 어려움' }
] as const;

export const REWARDS = [
  { value: '0-50',   label: '0 ~ 50' },
  { value: '51-100', label: '51 ~ 100' },
  { value: '101+',   label: '100 이상' }
] as const;

export type GameStatus = '참여완료' | '미참여' | '심사중';
