import axios from './axios'

const API_URL =
  window.location.hostname === 'localhost'
    ? 'http://localhost:8080'
    : 'https://zmnoory.vercel.app/v1'

export interface GiftCardCreateRequest {
  productId: number
  giftCardImage: string // base64 encoded image
}

export const registerGift = async (
  payload: GiftCardCreateRequest,
  isTest?: boolean
): Promise<void> => {
  const connect_url = isTest ? 'http://localhost:8080' : API_URL
  try {
    const url = `${connect_url}/api/admin/giftcards`;
    console.log("Requesting URL:", url);
    await axios.post(url, payload)
  } catch (err) {
    console.error('기프티콘 등록 실패:', err)
    throw err
  }
}