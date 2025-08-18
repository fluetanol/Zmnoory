// [FILEPATH] src/modules/emojigame/types/types.ts
// 타입 정의 파일 내부에서 필요한 타입 import
import type { Group, Mesh, Vector3 } from 'three'



export type GameData = {
  mapRepeat: number
  knockBackDuration: number
  detectTick: number
}

//어떤 오브젝트의 상태
export type ObjectInfo = {
  object: Mesh
  velocity: Vector3
  size: Vector3
  scale: Vector3
  isActive: boolean
}

/**
 * 현재 벽의 emoji 상태
 * 
 * - emojiObj : 이모지 오브젝트
 * 
 * - emojiobjID : 이모지 오브젝트의 id
 * 
 * - emojistate : 이모지 state (EmojiManager의 list_emoji_state 참고)
 * 
 * - emojiStr : 이모지 감정을 string 으로 변환한 값 (EmojiManager.map_state_name.get(emojiState)와 동일)
 * */
export type EmojiState = {
  emojiObj: Mesh      //이모지
  emojiobjID: number  //이모지 오브젝트 id
  emojistate: number  //이모지 state
  emojiStr: string    //이모지 감정을 string으로
}


export type EmojiImageInfo = {
  image: string
  imageName: string
}

//스크린 정보
export type ScreenInfo = {
  width : number,
  height : number
}

//포맷팅 시간 정보
export type Type_FormatTime = {
  hour : number,
  minute : number,
  second : number
}

/**
 * 현재 이모지 게임 진행 상태
 
  - current_group : emoji 벽과 emoji 원판의 묶음 그룹
  - current_wall : emoji 벽
  - current_info : emoji 벽의 현 상태
  - current_state_emoji : emoji 원판의 상태 (emojiManager에도 레퍼런스로 연결되어있음)

*/
export type State_Current = {
  current_group :Group
  current_wall : Mesh
  current_info : ObjectInfo
  current_state_emoji : EmojiState
}