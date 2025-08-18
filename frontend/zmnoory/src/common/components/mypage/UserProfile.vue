<!-- [FILEPATH] src/common/components/mypage/UserProfile.vue -->
<template>
  <div v-if="user" class="profile-wrapper">
    <h1 class="welcome-message">어서오세요! {{ user.nickname }} 님</h1>
    <div class="divider"></div>

    <div class="info-section-header">
      <h2 class="section-title">회원 정보</h2>
      <button v-if="!isEditing" @click="isEditing = true" class="edit-link-button">
        <img src="@/assets/EditPencil.png" alt="수정" class="edit-icon" />
        회원 정보 수정
      </button>
    </div>

    <template v-if="!isEditing">
      <div class="info-body">
        <div class="details-container">
          <div class="info-grid">
            <span class="label">이름</span>
            <span class="value">{{ user.nickname }}</span>
            <span class="label">성별</span>
            <span class="value">{{ user.gender === 'MALE' ? '남성' : '여성' }}</span>
            <span class="label">이메일</span>
            <span class="value">{{ user.email }}</span>
            <span class="label">생년월일</span>
            <span class="value">{{ formattedBirthday }}</span>
          </div>
        </div>
        
        <div class="stats-container">
          <div class="stat-box">
            <span class="stat-label">보유 포인트</span>
            <span class="stat-value">
              {{ user.point }}
              <img src="@/assets/currency_symbol.png" alt="포인트" class="currency-icon" />
            </span>
          </div>
          <div class="stat-box">
            <span class="stat-label">쿠폰함</span>
            <span class="stat-value">0 개</span>
          </div>
          <div class="stat-box">
            <span class="stat-label">촬영 영상</span>
            <span class="stat-value">{{ videoCount }} 개</span>
          </div>
        </div>
      </div>
    </template>

    <div v-else class="edit-form">
        <div class="info-body">
            <div class="details-container">

                <div class="info-grid">
                <!-- 이름 -->
                <span class="label">이름</span>
                    <div class="input-wrap">
                        <input
                        type="text"
                        class="form-input"
                        v-model="formData.nickname"
                        maxlength="14"
                        :disabled="!isEditing || !fieldEdit.nickname"
                        :ref="el => (fieldRefs.nickname = el as HTMLInputElement)"
                        />
                        <small
                        v-show="isEditing && fieldEdit.nickname"
                        class="char-count"
                        >
                        {{ formData.nickname.length }}/14
                        </small>
                        <button
                        v-show="isEditing && !fieldEdit.nickname"
                        type="button"
                        class="pencil-btn"
                        @click="toggleField('nickname')"
                        aria-label="이름 수정"
                        >
                        <img src="@/assets/EditPencil.png" alt="" />
                        </button>
                    </div>

                <!-- 성별 -->
                <span class="label">성별</span>
                    <div class="input-wrap">
                        <select
                        class="form-select"
                        v-model="formData.gender"
                        :disabled="!isEditing || !fieldEdit.gender"
                        :ref="el => (fieldRefs.gender = el as HTMLSelectElement)"
                        >
                        <option value="MALE">남성</option>
                        <option value="FEMALE">여성</option>
                        </select>
                        <button
                        v-show="isEditing && !fieldEdit.gender"
                        type="button"
                        class="pencil-btn"
                        @click="toggleField('gender')"
                        aria-label="성별 수정"
                        >
                        <img src="@/assets/EditPencil.png" alt="" />
                        </button>
                    </div>

                    <!-- 이메일 (읽기 전용) -->
                    <span class="label">이메일</span>
                    <span class="value">{{ user.email }}</span>

                    <!-- 생년월일 -->
                <span class="label">생년월일</span>
                    <div class="input-wrap">
                        <input
                        type="date"
                        class="form-input"
                        v-model="formData.birthday"
                        :disabled="!isEditing || !fieldEdit.birthday"
                        :ref="el => (fieldRefs.birthday = el as HTMLInputElement)"
                        />
                        <button
                        v-show="isEditing && !fieldEdit.birthday"
                        type="button"
                        class="pencil-btn"
                        @click="toggleField('birthday')"
                        aria-label="생년월일 수정"
                        >
                        <img src="@/assets/EditPencil.png" alt="" />
                        </button>
                    </div>

                <span class="label">개인 정보 동의</span>
                  <div class="input-wrap">
                    <div class="field-box consent-field">
                      <!-- 토글 스위치 -->
                      <label class="switch">
                        <input
                          type="checkbox"
                          v-model="ref_isAgree"
                          :disabled="!isEditing"
                          aria-label="개인 정보 동의 여부"
                        />
                        <span class="slider"></span>
                      </label>

                      <!-- 상태 텍스트 -->
                      <span class="consent-state">{{ ref_isAgree ? '동의' : '미동의' }}</span>

                      <!-- 오른쪽 설명 + 보기버튼 -->
                      <div class="term-inline">
                        <span class="term-text">제3자 판매 및 AI 모델 학습 동의</span>
                        <button type="button" class="view-btn" @click="openTerms('thirdParty')">보기</button>
                      </div>
                    </div>
                  </div>
            </div>

        </div>
    </div>

        <!-- 액션 버튼은 수정 모드에서만 노출 -->
        <div class="form-actions" v-if="isEditing">
            <button @click="onCancel" class="button-cancel">취소</button>
            <button class="button-save" :disabled="saving || !hasChanges" @click="onSave">
                {{ saving ? '저장 중…' : '저장' }}
            </button>
        </div>
    </div>

    <TermsModal
      v-if="openedKey"
      :termType="openedKey"
      @close="closeTerms"
    />



  </div>
  
  <div v-else>
    <p>데이터를 불러오는 중입니다...</p>
  </div>
</template>


<script setup lang="ts">
import { ref, reactive, computed, watch, nextTick, onMounted } from 'vue';
import { useAccountStore } from '@/store/Accounts';
import type { UpdatePayload, Member } from '@/services/info';
import type { PropType, Ref } from 'vue';
import { profileImageService } from '@/services/upload';
import TermsModal from '../signup/TermsModal.vue';

const props = defineProps({
  user: { type: Object as PropType<Member | null>, default: null },
  videoCount: { type: Number, default: 0 },
  selectedPhoto: { type: Object as PropType<File | null>, default: null }
});

onMounted(async ()=>{
  if(accountStore.member_me == null) {
    await accountStore.getMyDetail();
  }
  ref_isAgree.value = accountStore.member_me?.optionalConsent!;
  formData.value.isAgree = ref_isAgree.value;
})


const isEditing = ref(false);
const saving = ref(false);
const accountStore = useAccountStore();
const emit = defineEmits(['update:isEditing', 'photo-uploaded', 'photo-cancelled']);

watch(isEditing, v => emit('update:isEditing', v));

/** 폼 데이터 */
const formData = ref({ nickname: '', gender: '', birthday: '', isAgree: true});

/** 개별 필드 편집 상태 */
const fieldEdit = reactive({ nickname: false, gender: false, birthday: false });

/** DOM refs (포커스/달력 오픈용) */
const fieldRefs: Record<string, HTMLInputElement | HTMLSelectElement | null> = reactive({
  nickname: null, gender: null, birthday: null,
});

const ref_isAgree : Ref<boolean> = ref(true);
watch(ref_isAgree, ()=> {
  // 동의 상태 변경 감지
});

/** 초기화 */
const initializeFormData = () => {
  if (!props.user) return;
  formData.value.nickname = props.user.nickname;
  formData.value.gender = props.user.gender;
  formData.value.birthday = (props.user.birthday || '').slice(0, 10);
};
watch(() => props.user, initializeFormData, { immediate: true });

/** 변경 여부 → 저장 버튼 가드 */
const hasChanges = computed(() => {
  if (!props.user) return false;
  const origBirthday = (props.user.birthday || '').slice(0, 10);
  return (
    formData.value.nickname.trim() !== props.user.nickname ||
    formData.value.gender !== props.user.gender ||
    formData.value.birthday !== origBirthday ||
    props.selectedPhoto !== null ||
    ref_isAgree.value !== formData.value.isAgree
  );
});

/** 날짜 표기(보기 모드용) */
const formattedBirthday = computed(() =>
  props.user?.birthday ? props.user.birthday.replace(/-/g, '. ') : ''
);

/** 편집 토글 (여러 필드를 동시에 열 수 있게 개별 토글) */
const toggleField = async (key: 'nickname'|'gender'|'birthday') => {
  fieldEdit[key] = !fieldEdit[key];
  await nextTick();
  if (fieldEdit[key]) {
    fieldRefs[key]?.focus();
    if (key === 'birthday') {
      const el = fieldRefs.birthday as HTMLInputElement | null;
      (el as any)?.showPicker?.();     // 크롬/엣지: 필드 위치에 달력 오픈
      setTimeout(() => el?.click?.(), 0); // 폴백
    }
  }
};

const resetFieldEdit = () => {
  Object.keys(fieldEdit).forEach(k => (fieldEdit[k as keyof typeof fieldEdit] = false));
};

const onCancel = () => {
  initializeFormData();
  resetFieldEdit();
  isEditing.value = false;
  emit('photo-cancelled');
};

const onSave = async () => {
  if (!props.user) return;
  formData.value.isAgree = ref_isAgree.value;

  const name = formData.value.nickname.trim();
  if (!name) return alert('이름을 입력하세요.');
  if (name.length > 14) return alert('이름은 14자 이하로 입력해주세요.');
  if (!['MALE','FEMALE'].includes(formData.value.gender)) return alert('성별을 선택하세요.');
  if (!/^\d{4}-\d{2}-\d{2}$/.test(formData.value.birthday)) return alert('생년월일 형식이 올바르지 않습니다.');

  try {
    saving.value = true;

    // 프로필 사진 업로드 처리
    if (props.selectedPhoto) {
      try {
        const uploadUrlResponse = await profileImageService.getUploadUrl(
          props.selectedPhoto.name,
          props.selectedPhoto.type,
          false
        );
        
        await profileImageService.uploadToS3(uploadUrlResponse.uploadUrl, props.selectedPhoto);
        
        await profileImageService.updateProfile(uploadUrlResponse.objectKey, false);
      } catch (photoError) {
        console.error('Profile image upload failed:', photoError);
        alert('프로필 사진 업로드에 실패했습니다.');
        return;
      }
    }

    // 회원정보 업데이트
    const payload: UpdatePayload = {
      email: props.user.email,
      nickname: name,
      gender: formData.value.gender as 'MALE'|'FEMALE',
      birthday: formData.value.birthday,
      optionalConsent : formData.value.isAgree
    };

    await accountStore.Update(props.user.email, payload);
    
    // 사용자 정보 새로고침
    await accountStore.checkAuthStatus(false, true);
    
    resetFieldEdit();
    isEditing.value = false;
    emit('photo-uploaded');
  } catch (e) {
    alert('수정에 실패했습니다. 다시 시도해주세요.');
  } finally {
    saving.value = false;
  }
};



/* --------- 약관 보기 모달 --------- */
const openedKey : Ref<string|null> = ref(null)

function openTerms(key : string) {
  openedKey.value = key!
}
function closeTerms() {
  openedKey.value = null
}
</script>




<style scoped>
/* ===== Header / Section ===== */
.welcome-message{font-family:'Inter',sans-serif;font-weight:700;font-size:40px;color:#2E2E2E;margin:0}
.divider{border-top:1px solid #A0A0A0;margin:25px 0}
.info-section-header{display:flex;align-items:center;gap:15px;margin-bottom:25px}
.section-title{font-size:20px;font-weight:600}
.edit-link{display:inline-flex;align-items:center}
.stat-value{display:inline-flex;align-items:center}
.details-container{flex-grow:1}
.edit-icon{width:18px;height:18px;margin-right:5px}
.currency-icon{width:15px;height:15px;margin-left:5px}
.edit-link-button{background:none;border:0;padding:0;cursor:pointer;display:inline-flex;align-items:center;font-family:'Inter',sans-serif;font-weight:400;font-size:15px;color:#A0A0A0 !important;text-decoration:none}

/* ===== Grid (값 컬럼 폭 고정) ===== */
.profile-wrapper {
  /* 원하는 크기로 바꿔 써 */
  --stat-w: 172px;
  --stat-h: 60px;

  /* 입력 필드/아이콘 위치 변수도 여기서 통일로 관리 */
  --label-w: 90px;
  --field-w: 280px;
  --gap-x: 28px;
  --gap-y: 22px;
  --ctrl-h: 40px;
  --radius: 999px;
  --pencil-right: 16px;   /* 연필 버튼 오른쪽 여백 */
  --affordance-right: 16px; /* ▾/달력 아이콘: 연필 왼쪽 44px */
  --icon-size: 20px;
}



.info-grid{
  display:grid;
  grid-template-columns: var(--label-w) var(--field-w) var(--label-w) var(--field-w);
  gap: var(--gap-y) var(--gap-x);
  align-items:center;
  margin-bottom:35px;
}
.label{font-weight:700;font-size:15px}
.value{font-size:15px}

/* ===== Stats ===== */
.stats-container {
  display: flex;
  border: 1px solid #2E2E2E;
  border-radius: 20px;
  width: fit-content;
  /* 필요하면 전체 너비 강제: max-width로 제한 가능 */
}

.stat-box {
  flex: 0 0 var(--stat-w);
  min-height: var(--stat-h);
  padding: 14px 16px;
  text-align: center;
  display: grid;
  align-content: center;
  gap: 8px;
}

.stat-box:not(:last-child) {
  border-right: 1px solid #2E2E2E;
}

.stat-label{
    display:block;
    font-size:15px;
    font-weight:700;
    margin-bottom:12px
}


/* ===== Controls (필드 폭 캡 + 아이콘 정렬) ===== */
.form-input,.form-select,.read-pill{
  height:var(--ctrl-h);
  box-sizing:border-box;
  border:1px solid #ccc;
  border-radius:var(--radius);
  padding:8px 12px;
  font-size:16px;
  background:#fff;
  color:#2E2E2E;
}
.read-pill{background:#f8f9fa}

/* 필드 래퍼는 그리드 값 컬럼 폭을 상한으로 사용 */
.input-wrap{ position:relative; width:100%; max-width:var(--field-w); min-width:0; }
.input-wrap .form-input,
.input-wrap .form-select,
.input-wrap .read-pill{ width:100%; }

/* 연필 자리 기본 패딩 */
.input-wrap .form-input,
.input-wrap .form-select{ padding-right:44px; }

/* select/date는 ▾/달력 + 연필까지 고려해 여유 패딩 */
.input-wrap select.form-select,
.input-wrap input[type="date"].form-input{
  padding-right: calc(var(--affordance-right) + var(--pencil-right) + var(--icon-size));
}

/* 연필 버튼: 가장 오른쪽 */
.pencil-btn{
  position:absolute;
  right:var(--pencil-right);
  top:50%;
  transform:translateY(-50%);
  width:22px;height:22px;border:0;background:transparent;cursor:pointer;opacity:.6;z-index:3
}
.pencil-btn:hover{opacity:1}
.pencil-btn img{width:16px;height:16px;display:block}

/* select ▾ 아이콘을 연필 왼쪽 44px에 고정 */
.form-select{
  -webkit-appearance:none; -moz-appearance:none; appearance:none;
  background-repeat:no-repeat;
  background-image:url("data:image/svg+xml,%3Csvg width='12' height='8' viewBox='0 0 12 8' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M1 1l5 5 5-5' stroke='%23999' stroke-width='2' fill='none' stroke-linecap='round'/%3E%3C/svg%3E");
  background-position: right var(--affordance-right) center;
  background-size:12px 8px;
}

/* 달력 아이콘 위치도 동일 라인(연필 왼쪽 44px) */
.input-wrap input[type="date"]::-webkit-calendar-picker-indicator{
  position:absolute;
  right:var(--affordance-right);
  top:50%;
  transform:translateY(-50%);
  width:var(--icon-size); height:var(--icon-size);
  cursor:pointer; opacity:.75; z-index:2;
}

/* 상태 */
.form-input:disabled,.form-select:disabled{background:#f8f9fa;color:#666;cursor:not-allowed}
.button-save:disabled{opacity:.5;cursor:not-allowed}
.char-count{position:absolute;right:6px;bottom:-18px;font-size:12px;color:#888}

/* 액션 버튼 */
.form-actions{display:flex;justify-content:flex-end;gap:10px;margin-top:30px}
.button-cancel,.button-save{padding:10px 20px;border:0;border-radius:8px;font-size:16px;font-weight:600;cursor:pointer}
.button-cancel{background:#e9ecef;color:#495057}
.button-save{background:#2E2E2E;color:#fff}

.profile-section {
  display: flex;
  gap: 40px;
  align-items: flex-start;
  margin-bottom: 35px;
}

.profile-image-section {
  flex: 0 0 auto;
}

.info-body .details-container,
.edit-form .details-container {
  flex: 0 0 auto;      /* flex-grow 효과 제거 */
  width: fit-content;  /* 콘텐츠만큼 */
}

.info-grid .value {
  width: var(--field-w);        /* 입력필드와 동일한 고정 폭 */
  min-width: 0;
  height: var(--ctrl-h);        /* 동일한 높이 */
  box-sizing: border-box;
  display: flex;                /* 수직 가운데 정렬 */
  align-items: center;
  padding: 8px 12px;            /* 동일 패딩 */
  border: 1px solid #ccc;       /* 동일 테두리 */
  border-radius: var(--radius); /* 동일 라운드 */
  background: #f8f9fa;          /* pill 톤 */
  color: #2E2E2E;
  /* 폰트/라인 높이도 입력과 맞춤(미세 튀는 경우 대비) */
  font-size: 15px;
  line-height: 1;
}


.term-text{
  font-size: 10px;
}

.view-btn {
  border: 1px solid #A0A0A0;
  background: #FFFFFF;
  padding: 4px 12px;
  font-size: 10px;
  cursor: pointer;
  border-radius: 4px;
  flex-shrink: 0;
}



.field-box {
  height: var(--ctrl-h);
  box-sizing: border-box;
  border: 1px solid #ccc;
  border-radius: var(--radius);
  padding: 0 12px;
  background: #fff;
  color: #2E2E2E;
  display: flex;
  align-items: center;
  gap: 10px;
}

/* 동의 필드 전용 정렬 */
.consent-field {
  width: 100%;
  max-width: var(--field-w);
  min-width: 0;
}

/* 토글 스위치 */
.switch {
  position: relative;
  width: 44px;
  height: 24px;
  flex: 0 0 auto;
}
.switch input {
  position: absolute;
  opacity: 0;
  width: 0;
  height: 0;
}
.slider {
  position: absolute;
  inset: 0;
  background: #e9ecef;
  border-radius: 999px;
  transition: background 0.2s ease;
}
.slider::before {
  content: "";
  position: absolute;
  left: 3px;
  top: 3px;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: #fff;
  box-shadow: 0 1px 2px rgba(0,0,0,.15);
  transition: transform 0.2s ease;
}

/* 체크 상태 */
.switch input:checked + .slider {
  background: #2E2E2E;
}
.switch input:checked + .slider::before {
  transform: translateX(20px);
}

/* 상태 텍스트 */
.consent-state {
  font-size: 14px;
  color: #2E2E2E;
}

/* 오른쪽 설명 + 버튼 묶음 (우측 정렬) */
.term-inline {
  margin-left: auto;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

/* 기존 작은 글자/버튼과 자연스럽게 이어지도록 */
.term-text { font-size: 12px; color:#555; line-height: 1; }
.view-btn {
  border: 1px solid #A0A0A0;
  background: #FFFFFF;
  padding: 4px 12px;
  font-size: 12px;
  cursor: pointer;
  border-radius: 6px;
}
.view-btn:hover { background: #f6f6f6; }
</style>
