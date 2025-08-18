// [FILEPATH] src/schemas/authSchema.ts
import { z } from 'zod';

export const signupSchema = z.object({
  nickname: z.string()
    .min(2, '2~12자의 한글, 영문, 숫자만 사용 가능합니다.')
    .max(12, '2~12자의 한글, 영문, 숫자만 사용 가능합니다.')
    .regex(/^[a-zA-Z0-9가-힣]+$/, '특수문자는 사용할 수 없습니다.'),
  email: z.string().email('올바른 이메일 형식이 아닙니다.'),
  password: z.string().min(8, '비밀번호는 8자 이상이어야 합니다.'),
  passwordConfirm: z.string(),
  gender: z.enum(['남', '여']),
  birthYear: z.string()
    .nonempty('출생 연도를 입력해주세요.')
    .regex(/^(19|20)\d{2}$/, '정확한 연도를 입력해주세요 (YYYY)'),
  birthMonth: z.string().nonempty('월을 선택해주세요.'),
  birthDay: z.string().nonempty('일을 입력해주세요.'),
  recommenderNickname: z.string().optional(),
}).refine(data => data.password === data.passwordConfirm, {
  message: '비밀번호가 일치하지 않습니다.',
  path: ['passwordConfirm'],
});

export type SignupFormState = z.infer<typeof signupSchema>;