// [FILEPATH] src/services/info.ts
import blankThumbnail from '@/assets/blank_game_thumbnail.png'

export interface Video {
  id: number;
  participationId: number;
  memberNickname: string,
  memberEmail: string,
  memberProfileImageUrl: string,
  gameTitle: string,
  title: string;
  description: string;
  isPublic: boolean;
  videoUrl: string;
  thumbnailUrl: string;
  isUploaded?: boolean;
  createdAt: string;
  updatedAt?: string;
}

export interface VideoList {
  id: number;
  participationId: number;
  memberNickname: string;
  memberEmail: string,
  memberProfileImageUrl: string;
  gameTitle: string;
  title: string;
  description: string;
  isPublic: boolean;
  videoUrl: string;
  thumbnailUrl: string;
  createdAt: string;
}

export interface VideoUpdatePayload {
  title: string;
  description: string;
  isPublic: boolean;
  videoUrl: string;
  thumbnailUrl: string;
}

export interface Game {
  id: number,
  title: string,
  description : string,
  explanation: string,
  difficulty: string,
  point: number,
  thumbnail: string,
  requireDataType: string,
  created_at: string,
  updated_at: string
}

export interface Product {
  id: number
  title: string
  category: string
  price: number
  thumbnail: string
}

export interface CreateProductPayload {
  title: string
  category: string
  price: number
  thumbnail: string
}

export interface GiftCard {
  id: number
  giftCardImage: string
  productTitle: string
  productPrice: number
  productThumbnail: string
  createdAt: string
  isPurchased: boolean
  purchasedBy: string | null
  purchasedAt: string | null
}

export type Gender = 'MALE' | 'FEMALE'
export type Role = 'USER' | 'ADMIN' | 'BUYER'

export interface Member {
  id: number
  email: string
  password: string
  nickname: string
  birthday: string
  gender: Gender
  role: Role
  created_at: string
  updated_at: string
  point: number
  recommender_id?: number
  profileImageUrl?: string
}

export interface ImagePayload {
    fileName: string;
    data: string;  // base64 encoded string
}

export interface ImageUploadRequest {
  videoId: number;
  images: ImagePayload[];
}

export interface RewardResponase{
  gameTitle : string,
  status  : string
  videoId : number
}


export interface MyMember{
  email: string;
  password: string;
  nickname: string;
  birthday: string;
  gender: string;
  recommenderEmail: string;
  optionalConsent : boolean;
  point: number;
  role: string;
  profileImageUrl: string;
}

// 회원가입용
export interface SignupPayload {
  email: string
  password: string
  nickname: string
  birthday: string
  gender: Gender
  role: Role
}

// 정보수정용
export interface UpdatePayload {
  email: string
  nickname: string
  birthday: string
  gender: Gender
  optionalConsent: boolean
}

// 게임 등록용
export interface GameRegisterPayload {
  title: string,
  description: string,
  explanation: string,
  difficulty: string,
  point: number,
  thumbnail: string,
  requiredDataType: string,
}

// 로그인용
export interface LoginPayload {
  email: string,
  password: string,
}

// 비밀번호 수정용
export interface PasswordUpdate  {
    originPassword: string
    updatedPassword: string
}

// 참여한 게임 목록 불러오기 용
export interface ParticipatedPayload {
    gameTitle: string
    status: string
}

// 게임시작용
export interface StartPayload {
  email: string
  gameTitle: string
}

export interface ParticipateReward {
  email: string
  gameTitle: string
  videoObjectKey: string
  thumbnailObjectKey: string
  title: string
  description: string
  isPublic : true
}

export interface UrlPayload {
  email: string
  gameTitle: string
  fileName: string
  contentType: string
}

export interface PublicUrlPayload{
  email : string,
  gameTitle : string
}

export interface UrlResponse {
  participationId: number
  uploadUrl: string
  objectKey: string
  message: string
}

export interface PublicUrlResponse {
  participationId: number
  videoUploadUrl: string
  videoObjectKey: string
  thumbnailUploadUrl: string
  thumbnailObjectKey: string
  message: string
}

export interface Comment {
  memberNickname: string
  memberProfileImageUrl: string
  content: string
  createdAt: string
}

// 데이터 요청 관련 인터페이스
export interface DataRequest {
  id: number
  companyName: string
  contactInfo: string
  dataRequirements: string
  status: DataRequestStatus
  requestDate: string
  processedDate?: string
  adminNotes?: string
  createdAt: string
  updatedAt: string
}

export interface DataRequestCreatePayload {
  companyName: string
  contactInfo: string
  dataRequirements: string
}

export interface DataRequestStatusUpdatePayload {
  status: DataRequestStatus
}

export interface DataRequestNotesUpdatePayload {
  adminNotes: string
}

export type DataRequestStatus = 'PENDING' | 'REVIEWING' | 'APPROVED' | 'IN_PROGRESS' | 'COMPLETED' | 'REJECTED'

// 추가 예정 게임
export const blankGame: Game = {
  id: 0,
  title: '추가 예정',
  description: '표정을 인식해 따라하는 게임',
  explanation: '화면에 나오는 이모지를 따라 표정을 지어보세요.',
  difficulty: '',
  point: 0,
  thumbnail: blankThumbnail,
  requireDataType: '미수집',
  created_at: '2025-07-01T12:00:00Z',
  updated_at: '2025-07-01T12:00:00Z',
}

// 추가 예정 영상
export const blankVideo: VideoList = {
  id: 0,
  participationId: 0,
  memberNickname: '추가 예정',
  memberEmail: 'test@test.com',
  memberProfileImageUrl: '',
  gameTitle: '게임',
  title: '추가 예정',
  description: '곧 재미있는 영상이 업로드될 예정입니다',
  isPublic: true,
  videoUrl: '',
  thumbnailUrl: blankThumbnail,
  createdAt: '2025-07-01T12:00:00Z',
}
