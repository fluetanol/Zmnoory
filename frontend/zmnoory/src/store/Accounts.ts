import { onMounted, onUnmounted, ref } from "vue";
import { defineStore } from "pinia";
import axios from "@/services/axios";
import type {
  Member,
  SignupPayload,
  UpdatePayload,
  LoginPayload,
  PasswordUpdate,
  MyMember,
} from "@/services/info";
import { useRouter } from "vue-router";

export const useAccountStore = defineStore("account", () => {
  const LOCAL_URL = "http://localhost:8080";
  const API_URL =
    window.location.hostname === "localhost"
      ? LOCAL_URL // 로컬 프론트 → 로컬 백엔드
      : "https://zmnoory.vercel.app/v1";

  const router = useRouter();
  const token = ref<string | null>(sessionStorage.getItem("accessToken"));
  // 토큰 키
  const TOKEN_KEY = "accessToken";

  // 이메일 중복 확인
  const checkEmail = async (
    email: string,
    isTest?: boolean
  ): Promise<boolean> => {
    const connect_url = isTest ? LOCAL_URL : API_URL;
    try {
      const res = await axios.get(`${connect_url}/api/members/check-email`, {
        params: { email },
        withCredentials: false,
      });

      // API 응답에서 boolean 값을 반환 (true: 사용 가능, false: 중복)
      return res.data.body.data;
    } catch (err) {
      console.error("Email check failed:", err);
      throw err; // 오류 발생 시 컴포넌트에서 처리하도록 다시 던짐
    }
  };

  // 닉네임 중복 확인
  const checkNickname = async (
    nickname: string,
    isTest?: boolean
  ): Promise<boolean> => {
    const connect_url = isTest ? LOCAL_URL : API_URL;
    try {
      const res = await axios.get(`${connect_url}/api/members/check-nickname`, {
        params: { nickname },
        withCredentials: false,
      });
      // API 응답에서 boolean 값을 반환 (true: 사용 가능, false: 중복)
      return res.data.body.data;
    } catch (err) {
      console.error("Nickname check failed:", err);
      throw err; // 오류 발생 시 컴포넌트에서 처리하도록 다시 던짐
    }
  };

  // 1. 로그인 상태와 사용자 정보를 관리할 반응형 변수 추가
  const isLoggedIn = ref(false);
  const userInfo = ref<Member | null>(null);
  const isLoadingMe = ref(false);
  // 2. 앱 로드 시 또는 새로고침 시 로그인 상태를 확인하는 함수 (쿠키 기반)

  const checkAuthStatus = async (isTest?: boolean, force = false) => {
    const token = sessionStorage.getItem(TOKEN_KEY);
    if (!token) {
      isLoggedIn.value = false;
      userInfo.value = null;
      return;
    }
    // ✅ userInfo 기준으로 스킵 (isLoggedIn 아님) + 로딩중엔 또 안 때림
    if (!force && (userInfo.value || isLoadingMe.value)) return;
    isLoadingMe.value = true;

    const connect_url = isTest ? LOCAL_URL : API_URL;

    try {
      // 서버에 내 정보를 요청하는 API (백엔드에 /api/members/me 와 같은 엔드포인트 필요)
      // 이 요청에 쿠키가 자동으로 포함되어 전송되고, 서버가 유효한 유저 정보를 주면 로그인 된 것임
      const res = await axios.get(`${connect_url}/api/members/me`);
      isLoggedIn.value = true;
      userInfo.value = res.data.body.data;
      console.log("로그인 상태 유지 성공");
    } catch (err) {
      isLoggedIn.value = false;
      userInfo.value = null;
      console.log("로그인 상태 없음");
    } finally {
      isLoadingMe.value = false;
    }
  };

  // 3. 로그인 함수 수정
  const login = async (payload: LoginPayload, isTest?: boolean) => {
    const connect_url = isTest ? LOCAL_URL : API_URL;
    try {
      // 1) 로그인 요청
      const res = await axios.post(`${connect_url}/api/members/login`, payload);

      // 2) accessToken 추출 ─ 응답 구조에 맞춰 순서대로 검사
      const accessToken = res.data?.body?.data;
      token.value = accessToken;
      console.log(res.data);

      if (!accessToken) {
        throw new Error("서버 응답에 accessToken 이 없습니다.");
      }

      // 3) 세션스토리지 저장
      sessionStorage.setItem(TOKEN_KEY, accessToken);
      await checkAuthStatus(isTest, true);

      console.log("로그인 성공");
      router.push("/main");
    } catch (err) {
      console.error("로그인 실패:", err);
      sessionStorage.removeItem(TOKEN_KEY); // 🔻 추가
      isLoggedIn.value = false;
      alert("아이디 또는 비밀번호가 일치하지 않습니다.");
    }
  };

  // 4. 로그아웃 함수 수정
  const logout = async () => {
    // JWT 토큰 방식에서는 서버 API 호출 없이 클라이언트에서만 토큰 제거
    sessionStorage.removeItem(TOKEN_KEY);
    token.value = null;

    // 프론트엔드 상태 로그아웃 처리
    isLoggedIn.value = false;
    userInfo.value = null;
    console.log("로그아웃 완료");
  };

  // 전체 사용자 목록 조회
  const member_list = ref<Member[] | null>(null);
  const getMemberList = async (isTest?: boolean): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : "";
    try {
      const res = await axios.get(`${connect_url}/api/members`);
      console.log("전체 사용자 목록 get");
      member_list.value = res.data.body.data;
    } catch (err) {
      console.log(err);
    }
  };

  // 단일 사용자 조회
  const member_detail = ref<Member | null>(null);
  const getMemberDetail = async (
    memberId: number,
    isTest?: boolean
  ): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : "";
    try {
      const res = await axios.get(`${connect_url}/api/members/${memberId}`);
      console.log("단일 사용자 get");
      member_detail.value = res.data.body.data;
    } catch (err) {
      console.log(err);
    }
  };

  const member_me = ref<MyMember | null>(null);
  const getMyDetail = async (isTest?: boolean): Promise<MyMember | null> => {
    const connect_url = isTest ? LOCAL_URL : API_URL;
    try {
      const res = await axios.get(`${connect_url}/api/members/me`);
      const result: MyMember = res.data.body.data;

      member_me.value = result; // 응답 데이터 저장
      userInfo.value = result as any; // userInfo도 함께 업데이트
      console.log("res detail:", result);

      return result;
    } catch (err) {
      console.error("Failed to fetch member info:", err);
      return null;
    }
  };

  // 사용자 비밀번호 수정
  const passwordChange = async (
    payload: PasswordUpdate,
    isTest?: boolean
  ): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : "";
    try {
      const res = await axios.patch(
        `${connect_url}/api/members/password`,
        payload
      );
      console.log("비밀번호 수정 patch", res);
    } catch (err) {
      console.log(err);
    }
  };

  // 사용자 권한 수정
  const roleChange = async (
    targetId: number,
    isTest?: boolean
  ): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : "";
    try {
      const res = await axios.patch(
        `${connect_url}/api/members/admin/role-change/${targetId}`
      );
      console.log("사용자 권한 수정 patch", res);
    } catch (err) {
      console.log(err);
    }
  };

  // 회원가입
  const signUp = async (
    member: SignupPayload,
    isTest?: boolean
  ): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : API_URL;

    // ── ① 내가 보내는 JSON 확인 ──
    console.log("[SIGNUP][REQUEST] ->", JSON.stringify(member));

    try {
      // validateStatus 넣어도 되고 안 넣어도 됨. (아래처럼 넣으면 400도 catch로 감)
      const res = await axios.post(
        `${connect_url}/api/members/sign-up`,
        member,
        {
          validateStatus: (s) => s >= 200 && s < 300, // 200~299만 성공으로 간주
        }
      );

      console.log("[SIGNUP][RESPONSE] <-", res.status, res.data);
    } catch (err: any) {
      // ── ② 서버가 뭐라고 욕하는지 그대로 보기 ──
      console.error("[SIGNUP][ERROR]", {
        status: err?.response?.status,
        headers: err?.response?.headers,
        data: err?.response?.data, // 여기에 자세한 원인 들어있음
        // raw: err?.response?.request?.responseText, // 필요하면 주석 해제
      });

      throw err; // 그대로 컴포넌트로 던짐
    }
  };

  // 회원정보 수정
  const Update = async (
    email: string,
    memberUpdateRequest: UpdatePayload,
    isTest?: boolean
  ): Promise<void> => {
    const connect_url = isTest ? LOCAL_URL : API_URL;
    try {
      const res = await axios.put(
        `${connect_url}/api/members/${email}`,
        memberUpdateRequest
      );
      console.log("회원정보 수정 put", res);
      await checkAuthStatus(isTest, true);
    } catch (err) {
      console.log(err);
    }
  };

  const useAuthToken = () => {
    const setToken = (v: string | null) => {
      token.value = v;
      if (v) sessionStorage.setItem("accessToken", v);
      else sessionStorage.removeItem("accessToken");
    };

    const onStorage = (e: StorageEvent) => {
      if (e.storageArea === sessionStorage && e.key === "accessToken") {
        token.value = e.newValue; // 다른 탭에서 변경 감지
      }
    };

    onMounted(() => window.addEventListener("storage", onStorage));
    onUnmounted(() => window.removeEventListener("storage", onStorage));

    return { token, setToken };
  };

  return {
    isLoggedIn,
    userInfo,
    login,
    logout,
    checkAuthStatus,
    member_list,
    getMemberList,
    member_detail,
    getMemberDetail,
    passwordChange,
    roleChange,
    signUp,
    Update,
    checkEmail,
    checkNickname,
    member_me,
    getMyDetail,
    useAuthToken,
  };
});
