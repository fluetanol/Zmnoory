import axios from 'axios'

const http = axios.create({
  baseURL: '/api',
  timeout: 8000
})
void http;

export interface Game {
  id: number
  title: string
  category: string
  status: '참여완료' | '미참여' | '심사중'
  difficulty: string
  reward: number
  imageKey: string
  description?: string
  creator?: string
  playCount?: string | number
}

export async function fetchGames(params?: {
  keyword?: string
  difficulty?: string
  reward?: string
}): Promise<Game[]> {
  void params;

  // 백엔드 연동을 위한 코드. 현재는 주석 처리되어 Mock 데이터를 반환합니다.
  /*
  try {
    const response = http.get('/games', { params: params });
    return response.data;
  } catch (error) {
    console.error('Failed to fetch games:', error);
    throw error;
  }
  */

  // 현재 Mock 데이터 반환 (필요시 'params'를 활용하여 Mock 데이터 필터링 가능)
  return [
    { id: 1, title: '이모지 게임 1', category: '표정 데이터', imageKey: 'default-thumbnail', status: '참여완료', difficulty: 'easy', reward: 30 },
    { id: 2, title: '단어 맞추기', category: '음성 데이터', imageKey: 'default-thumbnail', status: '미참여', difficulty: 'normal', reward: 70 },
    { id: 3, title: '상황 재연하기', category: '음성 데이터', imageKey: 'default-thumbnail', status: '심사중', difficulty: 'hard', reward: 120 },
  ]
}

export async function fetchGameById(id: string | number): Promise<Game> {
  void id;

  // 백엔드 연동을 위한 코드. 현재는 주석 처리되어 Mock 데이터를 반환합니다.
  /*
  try {
    const response = http.get(`/games/${id}`);
    return response.data;
  } catch (error) {
    console.error(`Failed to fetch game with id ${id}:`, error);
    throw error;
  }
  */

  // 현재 Mock 데이터 반환 (필요시 'id'를 활용하여 Mock 데이터 선택 가능)
  return {
    id: Number(id),
    title: '이모지 게임',
    category: '표정 데이터',
    imageKey: 'default-thumbnail', // <-- 이 라인을 추가
    status: '미참여',
    difficulty: 'normal',
    reward: 80,
    description: '이모티콘을 보고 똑같은 표정을 만들어보세요! 당신의 표정 인식 능력을 테스트해볼 시간입니다. 친구들과 함께 즐기면 더욱 재미있습니다.',
    creator: '즈믄누리',
    playCount: '1.2k'
  }
}

export async function fetchSimilarGames(id: string | number): Promise<Game[]> { // 85번째 줄
  void id; // 'id' 매개변수를 명시적으로 "사용"하여 TS6133 오류를 방지합니다.

  // 백엔드 연동을 위한 코드. 현재는 주석 처리되어 Mock 데이터를 반환합니다.
  /*
  try {
    const response = http.get(`/games/${id}/similar`);
    return response.data;
  } catch (error) {
    console.error(`Failed to fetch similar games for id ${id}:`, error);
    throw error;
  }
  */

  // 현재 Mock 데이터 반환 (필요시 'id'를 활용하여 Mock 데이터 필터링 가능)
  return [
    { id: 101, title: '다른 표정 게임 1', category: '표정 데이터', imageKey: 'default-thumbnail', status: '미참여', difficulty: 'easy', reward: 30 },
  ]
}