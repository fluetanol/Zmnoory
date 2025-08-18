<!-- [FILEPATH] src/common/components/signup/TermsModal.vue -->
<template>
  <div class="modal-backdrop" @click.self="$emit('close')">
    <div class="modal">
      <h3 class="modal-title">{{ titles[termType] }}</h3>
      <div class="modal-content">
        <pre>{{ contents[termType] }}</pre>
      </div>
      <button type="button" class="modal-close" @click="$emit('close')">닫기</button>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  termType: {
    type: String,
    required: true,
    validator: (value) => ['service', 'privacy', 'faceData', 'thirdParty'].includes(value)
  }
});

const emit = defineEmits(['close']);

// ESC 키로 모달 닫기
function handleKeydown(event) {
  if (event.key === 'Escape') {
    emit('close');
  }
}

// 뒤로가기로 모달 닫기
function handlePopstate() {
  emit('close');
}

onMounted(() => {
  document.addEventListener('keydown', handleKeydown);
  // 모달이 열릴 때 history state 추가
  history.pushState({ modal: true }, '');
  window.addEventListener('popstate', handlePopstate);
});

onUnmounted(() => {
  document.removeEventListener('keydown', handleKeydown);
  window.removeEventListener('popstate', handlePopstate);
});

const titles = {
  service: '이용약관 (즈믄누리)',
  privacy: '개인정보 처리방침',
  faceData: '얼굴 데이터 수집 및 이용 동의',
  thirdParty: '제3자 판매 및 AI 모델 학습 동의'
}

const contents = {
  service: 
  `[서비스 이용 약관] (필수)

시행일: [2025-08-11]
회사: gradation
문의: [이메일/전화]

1. 목적
본 약관은 ‘즈믄누리’ 이용에 관한 회사와 회원의 권리·의무 및 이용조건을 정합니다.

2. 가입 및 계정
- 정확한 정보 제공, ID/비밀번호 관리 책임은 회원에게 있습니다.
- 만 14세 미만은 법정대리인 동의가 필요합니다.

3. 서비스 제공·변경·중단
- 본인확인, 계정·콘텐츠 제공 등 서비스를 운영합니다.
- 점검·보안·운영상 필요 시 내용/시간이 변경 또는 중단될 수 있으며 사전(불가피 시 사후) 고지합니다.

4. 회원의 의무
- 법령·약관·공지 준수, 타인 권리 침해 및 운영 방해 금지.
- 불법 복제/배포, 계정 공유/대여/판매, 자동화 스크립트 남용, 리버스 엔지니어링, 허위 신고·사기 금지.

5. 위반 시 조치
- 경고, 게시물/데이터 삭제, 이용제한, 계약 해지 등 필요한 조치를 할 수 있습니다. 중대한 위반은 즉시 해지 가능.

6. 이용자 콘텐츠
- 회원이 업로드한 콘텐츠의 권리는 회원에게 있습니다. 서비스 운영·노출을 위한 범위 내에서 무상·비독점적으로 이용될 수 있습니다. 권리 침해 소지가 있으면 제한/삭제될 수 있습니다.

7. 지식재산권
- 서비스와 자료의 권리는 회사 또는 정당한 권리자에게 귀속되며, 무단 복제·배포·변형을 금지합니다.

8. 계약 해지
- 회원은 언제든 탈퇴할 수 있습니다. 약관 위반·운영 방해·불법 행위 시 사전 통지 후(긴급 시 사후) 제한/해지할 수 있습니다.

9. 책임의 제한
- 천재지변·통신장애 등 회사 귀책 없는 사유로 발생한 손해에 책임지지 않습니다. 무료/시범 기능은 변경·중단될 수 있습니다.
- 개인정보 사항은 개인정보처리방침을 따릅니다.

10. 약관 변경
- 법령/서비스 변경에 따라 개정할 수 있으며 시행일·주요 변경 사항을 사전 공지합니다. 이후 계속 이용 시 동의한 것으로 봅니다.

11. 준거법 및 분쟁
- 대한민국 법률을 따르며, 분쟁은 회사 본점 소재지 관할 법원을 전속 관할로 합니다.`,
  
  privacy: 
  `[개인정보 수집 및 이용 동의] (필수)

시행일: [2025-08-11]
회사: gradation / 문의: [mattermost]
개인정보 보호책임자: [김광현]

1. 수집 목적
본인확인, 계정 생성·관리, 서비스 제공·운영, 부정이용 방지, 통계·품질 개선

2. 수집 항목
- 필수: 이메일, 비밀번호, 서비스 이용기록, 접속 로그, 기기 정보, IP 주소
- 선택: 휴대전화번호, 프로필 정보

3. 보유·이용 기간
- 계정 관련 정보: 탈퇴 후 최대 3~5년(분쟁 대응 범위 내)
- 법령상 보존 의무가 있는 경우 해당 기간 준수

4. 처리 위탁
- 결제, 데이터 보관, 알림 발송 등 위탁 가능. 수탁사 명칭/업무는 별도 공개.

5. 제3자 제공
- 원칙적으로 동의 없이 제공하지 않음. 법령 허용 또는 동의 시 제공 가능.

6. 권리 행사
- 열람·정정·삭제·처리정지·동의 철회 요청 가능(마이페이지/이메일). 법정 기한 내 처리.

7. 안전조치
- 전송·저장 암호화, 접근권한 관리, 접속기록 보관·점검, 악성코드 방지, 정기 보안 점검

동의 거부 시: 본인확인 및 서비스 제공이 제한될 수 있습니다.
`,
  
  faceData: 
  `[얼굴 데이터(민감정보) 수집 및 이용 동의] (필수)

1. 목적
본인확인, 계정보호, 부정사용 방지, 얼굴 기반 기능 제공

2. 항목
얼굴 이미지/영상, 랜드마크·좌표, 임베딩(특징값), 촬영 일시·기기 정보

3. 보유기간
원본: 60일 이내 파기 / 임베딩: 최대 3년(또는 목적 달성 시 즉시 파기)

4. 처리 위탁
클라우드/보안 등 위탁 가능(수탁사·보호조치 별도 공개)

5. 권리·철회
언제든 철회·삭제 요청 가능(철회 시 원본 즉시 파기, 임베딩 파기 요청 가능)

6. 안전조치
전송·저장 암호화, 분리보관, 접근통제, 정기 취약점 점검

동의 거부 시: 본인확인 등 핵심 기능 제공이 어려워 가입이 제한될 수 있습니다.
`,

  thirdParty: 
  `[제3자 제공·판매 및 AI 모델 학습 동의] (선택)

연구/평가 및 모델 성능 개선을 위해,
가명처리된 얼굴 임베딩(필요 시 최소한의 원본/랜드마크)을
연구기관·AI기업 등 제3자에 제공할 수 있습니다.
보유기간: 최대 3년(또는 목적 달성 시 즉시 파기)
언제든 동의 철회 가능하며, 거부해도 기본 서비스 이용에 제한 없습니다.`
}
</script>

<style scoped>
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.45);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999;
}
.modal {
  width: 680px;
  max-width: calc(100% - 40px);
  max-height: calc(100% - 80px);
  background: #fff;
  border-radius: 6px;
  box-shadow: 0 20px 40px rgba(0,0,0,0.25);
  padding: 32px 28px 24px;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}
.modal-title {
  font-size: 22px;
  font-weight: 700;
  color: #2E2E2E;
  margin-bottom: 16px;
}
.modal-content {
  flex: 1;
  overflow-y: auto;
  padding-right: 6px;
  margin-bottom: 20px;
}
.modal-content pre {
  white-space: pre-wrap;
  line-height: 1.5;
  font-size: 15px;
  color: #333;
}
.modal-close {
  align-self: flex-end;
  border: 1px solid #A0A0A0;
  background: #F5E7DA;
  border-radius: 4px;
  padding: 8px 16px;
  font-size: 15px;
  cursor: pointer;
}
</style>